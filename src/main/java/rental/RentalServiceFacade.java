package rental;

import dao.VehicleDAO;
import pricing.PricingStrategy;
import vehicle.Vehicle;

public class RentalServiceFacade {
    private VehicleDAO vehicleDAO;

    public RentalServiceFacade(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    /**
     * Method 1: Menghitung estimasi harga (Dipanggil oleh CustomerAppGui)
     */
    public double calculateEstPrice(Vehicle v, PricingStrategy strategy, int duration) {
        // Delegasikan perhitungan ke strategy pattern
        return strategy.calculatePrice(v.getBasePrice(), duration);
    }

    /**
     * Method 2: Melakukan booking (Dipanggil oleh CustomerAppGui)
     */
    public void bookVehicle(Vehicle v, PricingStrategy strategy, int duration) throws Exception {
        // 1. Hitung harga final (bisa disimpan ke DB nanti)
        double totalCost = calculateEstPrice(v, strategy, duration);

        // 2. Cek apakah kendaraan masih available (untuk mencegah race condition)
        Vehicle currentVehicleState = vehicleDAO.findById(v.getId());
        if (!currentVehicleState.isAvailable()) {
            throw new Exception("Maaf, kendaraan ini baru saja disewa orang lain!");
        }

        // 3. Update status kendaraan di database menjadi tidak tersedia (false)
        vehicleDAO.updateAvailability(v.getId(), false);

        // 4. (Opsional) Di masa depan, temanmu bisa menambahkan logika 
        //    untuk insert ke tabel 'rentals' di sini.
        System.out.println("Booking Berhasil: " + v.getPlateNumber() + " | Total: " + totalCost);
    }
}