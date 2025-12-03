package pricing;

public interface PricingStrategy {
    // Menghitung harga total berdasarkan harga dasar kendaraan dan durasi
    double calculatePrice(double basePrice, int duration);
    String getUnitName(); // misal: "Jam", "Hari", "Bulan"
}