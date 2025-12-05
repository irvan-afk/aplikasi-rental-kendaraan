package pricing;

public interface PricingStrategy {
    double calculatePrice(double basePrice, int duration);
    String getUnitName();
}