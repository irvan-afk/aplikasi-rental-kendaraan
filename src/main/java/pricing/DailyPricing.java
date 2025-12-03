package pricing;

public class DailyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice, int duration) {
        // Durasi dalam hari
        return basePrice * duration;
    }

    @Override
    public String getUnitName() {
        return "Hari";
    }
}