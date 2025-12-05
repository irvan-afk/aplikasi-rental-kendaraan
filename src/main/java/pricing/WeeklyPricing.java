package pricing;

public class WeeklyPricing implements PricingStrategy {

    @Override
    public double calculatePrice(double basePrice, int duration) {
        // Diskon 10% (dikali 0.9) jika sewa mingguan.
        double weeklyBase = basePrice * 7; 
        return (weeklyBase * duration) * 0.90; 
    }
    @Override
    public String getUnitName() {
        return "Minggu"; 
    }
}