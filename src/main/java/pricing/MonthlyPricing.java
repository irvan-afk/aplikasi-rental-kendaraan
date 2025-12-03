package pricing;

public class MonthlyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice, int duration) {
        // Durasi dalam bulan. Ada diskon 20% jika sewa bulanan
        double monthlyBase = basePrice * 30; 
        return (monthlyBase * duration) * 0.8; 
    }

    @Override
    public String getUnitName() {
        return "Bulan";
    }
}