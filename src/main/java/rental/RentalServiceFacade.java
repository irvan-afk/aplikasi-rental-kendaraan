package rental;

import dao.VehicleDAO;
import pricing.PricingStrategy;
import rental.exception.RentalException;
import vehicle.Vehicle;

public class RentalServiceFacade {

    private final VehicleDAO vehicleDAO;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;

    public RentalServiceFacade(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
        this.inventoryService = new InventoryService(vehicleDAO);
        this.paymentService = new PaymentService();
        this.invoiceService = new InvoiceService();
        this.notificationService = new NotificationService();
    }

    public double calculateEstPrice(Vehicle v, PricingStrategy strategy, int duration) {
        return strategy.calculatePrice(v.getBasePrice(), duration);
    }

    public void bookVehicle(Vehicle v, PricingStrategy strategy, int duration) throws Exception {
        double total = calculateEstPrice(v, strategy, duration);

        Vehicle current = vehicleDAO.findById(v.getId());
        if (current == null) {
            throw new Exception("Kendaraan tidak ditemukan!");
        }

        if (!current.isAvailable()) {
            throw new Exception("Maaf, kendaraan ini baru saja disewa orang lain!");
        }

        vehicleDAO.updateAvailability(v.getId(), false);

        System.out.println("Booking berhasil untuk kendaraan " + v.getPlateNumber()
                + " | Harga total: " + total);
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

            inventoryService.reserveVehicle(vehicle.getId());
            reserved = true;

            double total = strategy.calculatePrice(vehicle.getBasePrice(), duration);

            PaymentService.PaymentReceipt receipt =
                    paymentService.processPayment(total, method, customerName);

            Invoice invoice = invoiceService.generateInvoice(
                    vehicle, strategy, duration, customerName, receipt
            );

            notificationService.sendBookingConfirmation(customerName,
                    vehicle.getPlateNumber(), total);
            notificationService.sendPaymentConfirmation(customerName, receipt.getTransactionId());

            return invoice;

        } catch (Exception e) {

            if (reserved) {
                try { inventoryService.releaseVehicle(vehicle.getId()); }
                catch (Exception ignored) {}
            }

            throw e;
        }
    }
}
