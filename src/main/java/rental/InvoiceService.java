package rental;

import pricing.PricingStrategy;
import vehicle.Vehicle;

public class InvoiceService {

    public Invoice generateInvoice(
            Vehicle vehicle,
            PricingStrategy strategy,
            int duration,
            String customerName,
            PaymentService.PaymentReceipt receipt
    ) {
        double basePrice = vehicle.getBasePrice();
        double totalPrice = strategy.calculatePrice(basePrice, duration);

        return new Invoice(
                generateInvoiceNumber(),
                vehicle,
                strategy.getUnitName(),
                duration,
                basePrice,
                totalPrice,
                customerName,
                receipt,
                System.currentTimeMillis()
        );
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }
}
