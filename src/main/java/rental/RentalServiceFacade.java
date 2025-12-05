package rental;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dao.CustomerDAO;
import dao.RentalDAO;
import dao.VehicleDAO;
import pricing.PricingStrategy;
import rental.exception.RentalException;
import vehicle.Vehicle;

public class RentalServiceFacade {

    private final RentalDAO rentalDAO;
    private final CustomerDAO customerDAO;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;

    public RentalServiceFacade(VehicleDAO vehicleDAO) {
        this.rentalDAO = new RentalDAO();
        this.customerDAO = new CustomerDAO();
        this.inventoryService = new InventoryService(vehicleDAO);
        this.paymentService = new PaymentService();
        this.invoiceService = new InvoiceService();
        this.notificationService = new NotificationService();
    }

    public double calculateEstPrice(Vehicle v, PricingStrategy strategy, int duration) {
        return strategy.calculatePrice(v.getBasePrice(), duration);
    }

    public List<Rental> getRentalsForCustomer(String customerName) throws SQLException {
        return rentalDAO.findRentalsByUsername(customerName);
    }

    public void returnVehicle(int rentalId) throws Exception {
        int vehicleId = rentalDAO.findVehicleIdByRentalId(rentalId);
        inventoryService.releaseVehicle(vehicleId);
    }

    public Invoice processCompleteBooking(
            Vehicle vehicle,
            PricingStrategy strategy,
            int duration,
            String customerName
    ) throws Exception {

        boolean reserved = false;

        try {
            if (vehicle == null) {
                throw new RentalException(RentalException.ErrorCode.VEHICLE_NOT_FOUND);
            }
            if (duration <= 0) {
                throw new RentalException(RentalException.ErrorCode.INVALID_DURATION);
            }

            if (!inventoryService.isVehicleAvailable(vehicle.getId())) {
                throw new RentalException(RentalException.ErrorCode.VEHICLE_NOT_AVAILABLE);
            }

            inventoryService.reserveVehicle(vehicle.getId());
            reserved = true;

            double total = strategy.calculatePrice(vehicle.getBasePrice(), duration);

            PaymentService.PaymentReceipt receipt =
                    paymentService.processPayment(total, PaymentService.PaymentMethod.CASH, customerName);

            int customerId = customerDAO.findCustomerIdByUsername(customerName);
            Date startDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);

            String unit = strategy.getUnitName();
            switch (unit) {
                case "Jam":
                    cal.add(Calendar.HOUR_OF_DAY, duration);
                    break;
                case "Minggu":
                    cal.add(Calendar.DATE, duration * 7);
                    break;
                case "Bulan":
                    cal.add(Calendar.MONTH, duration);
                    break;
                case "Hari":
                default:
                    cal.add(Calendar.DATE, duration);
                    break;
            }
            
            Date endDate = cal.getTime();

            Rental rental = new Rental(vehicle.getId(), customerId, startDate, endDate, total);
            rentalDAO.save(rental);

            Invoice invoice = invoiceService.generateInvoice(
                    vehicle, strategy, duration, customerName, receipt
            );

            notificationService.sendBookingConfirmation(customerName,
                    vehicle.getPlateNumber(), total);
            notificationService.sendPaymentConfirmation(customerName, receipt.getTransactionId());

            return invoice;

        } catch (Exception e) {
            if (reserved) {
                try {
                    inventoryService.releaseVehicle(vehicle.getId());
                } catch (Exception ignored) {}
            }
            throw e;
        }
    }
}