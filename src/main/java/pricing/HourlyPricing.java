package pricing;

public class HourlyPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice, int duration) {
        // Contoh: Harga per jam adalah harga harian dibagi 24, dikali durasi
        return (basePrice / 24.0) * duration;
    }

    @Override
    public String getUnitName() {
        return "Jam";
    }
}