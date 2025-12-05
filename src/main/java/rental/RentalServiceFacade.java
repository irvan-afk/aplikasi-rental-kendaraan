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

    public void returnVehicle(int rentalId) throws RentalException {
        try {
            int vehicleId = rentalDAO.findVehicleIdByRentalId(rentalId);
            inventoryService.releaseVehicle(vehicleId);
        } catch (SQLException e) {
            throw new RentalException(RentalException.ErrorCode.DATABASE_ERROR, e);
        }
    }

    public Invoice processCompleteBooking(
            Vehicle vehicle,
            PricingStrategy strategy,
            int duration,
            String customerName
    ) throws RentalException {

        boolean reserved = false;

        try {
            validateBookingRequest(vehicle, duration);

            if (!inventoryService.isVehicleAvailable(vehicle.getId())) {
                throw new RentalException(RentalException.ErrorCode.VEHICLE_NOT_AVAILABLE);
            }

            // 1. Reserve vehicle
            inventoryService.reserveVehicle(vehicle.getId());
            reserved = true;

            // 2. Calculate price
            double total = strategy.calculatePrice(vehicle.getBasePrice(), duration);

            // 3. Process payment
            PaymentService.PaymentReceipt receipt =
                    paymentService.processPayment(total, PaymentService.PaymentMethod.CASH, customerName);

            // 4. Persist rental
            saveRentalData(vehicle, duration, customerName, total, strategy);

            // 5. Generate invoice & Notify
            Invoice invoice = invoiceService.generateInvoice(
                    vehicle, strategy, duration, customerName, receipt
            );

            notificationService.sendBookingConfirmation(customerName, vehicle.getPlateNumber(), total);
            notificationService.sendPaymentConfirmation(customerName, receipt.getTransactionId());

            return invoice;

        } catch (RentalException re) {
            handleRollback(reserved, vehicle);
            throw re;
        } catch (Exception e) {
            handleRollback(reserved, vehicle);
            throw new RentalException(RentalException.ErrorCode.DATABASE_ERROR, e);
        }
    }

    private void validateBookingRequest(Vehicle vehicle, int duration) throws RentalException {
        if (vehicle == null) {
            throw new RentalException(RentalException.ErrorCode.VEHICLE_NOT_FOUND);
        }
        if (duration <= 0) {
            throw new RentalException(RentalException.ErrorCode.INVALID_DURATION);
        }
    }

    private void saveRentalData(Vehicle vehicle, int duration, String customerName, double total, PricingStrategy strategy) throws SQLException {
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
    }

    private void handleRollback(boolean reserved, Vehicle vehicle) {
        if (reserved && vehicle != null) {
            try {
                inventoryService.releaseVehicle(vehicle.getId());
            } catch (RentalException e) {
                // Log the rollback failure. We cannot throw another exception here
                // as it would mask the original exception.
                System.err.println("Rollback failed for vehicle ID " + vehicle.getId() + ": " + e.getMessage());
            }
        }
    }
}