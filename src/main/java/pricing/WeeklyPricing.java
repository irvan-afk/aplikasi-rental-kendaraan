package pricing;

public class WeeklyPricing implements PricingStrategy {

    @Override
    public double calculatePrice(double basePrice, int duration) {
        return (basePrice * 5) * duration;
    }
    @Override
    public String getUnitName() {
        return "Minggu"; 
    }
}