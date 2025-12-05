package rental;

import rental.PaymentService.PaymentReceipt;
import vehicle.Vehicle;

public class Invoice {

    private final String invoiceNumber;
    private final Vehicle vehicle;
    private final String durationType;
    private final int duration;
    private final double basePrice;
    private final double totalPrice;
    private final String customerName;
    private final PaymentReceipt receipt;
    private final long timestamp;

    @SuppressWarnings("java:S107")
    public Invoice(
            String invoiceNumber,
            Vehicle vehicle,
            String durationType,
            int duration,
            double basePrice,
            double totalPrice,
            String customerName,
            PaymentReceipt receipt,
            long timestamp
    ) {
        this.invoiceNumber = invoiceNumber;
        this.vehicle = vehicle;
        this.durationType = durationType;
        this.duration = duration;
        this.basePrice = basePrice;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.receipt = receipt;
        this.timestamp = timestamp;
    }

    public String getInvoiceNumber() { return invoiceNumber; }
    public Vehicle getVehicle() { return vehicle; }
    public String getDurationType() { return durationType; }
    public int getDuration() { return duration; }
    public double getBasePrice() { return basePrice; }
    public double getTotalPrice() { return totalPrice; }
    public String getCustomerName() { return customerName; }
    public PaymentReceipt getReceipt() { return receipt; }
    public long getTimestamp() { return timestamp; }
}
