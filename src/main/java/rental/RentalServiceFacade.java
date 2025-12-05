package rental;

import dao.CustomerDAO;
import dao.RentalDAO;
import dao.VehicleDAO;
import pricing.PricingStrategy;
import rental.exception.RentalException;
import vehicle.Vehicle;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RentalServiceFacade {

    private final VehicleDAO vehicleDAO;
    private final RentalDAO rentalDAO;
    private final CustomerDAO customerDAO;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;

    public RentalServiceFacade(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
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
        // Here you could also add logic to mark the rental record as 'completed' if you add a status column to the rentals table.
    }

    public Invoice processCompleteBooking(
            Vehicle vehicle,
            PricingStrategy strategy,
            int duration,
            String customerName,
            PaymentService.PaymentMethod method
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

            // 1. Reserve vehicle first
            inventoryService.reserveVehicle(vehicle.getId());
            reserved = true;

            // 2. Calculate price
            double total = strategy.calculatePrice(vehicle.getBasePrice(), duration);

            // 3. Process payment
            PaymentService.PaymentReceipt receipt =
                    paymentService.processPayment(total, method, customerName);

            // 4. Persist the rental record to the database
            int customerId = customerDAO.findCustomerIdByUsername(customerName);
            Date startDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            // This is a simplification. The duration unit (days, hours) should be handled properly.
            // For now, we assume duration is in days as per the old schema.
            cal.add(Calendar.DATE, duration);
            Date endDate = cal.getTime();

            Rental rental = new Rental(vehicle.getId(), customerId, startDate, endDate, total);
            rentalDAO.save(rental);

            // 5. Generate invoice and send notifications
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
                }
                catch (Exception ignored) {}
            }

            throw e;
        }
    }
}
