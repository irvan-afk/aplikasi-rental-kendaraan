package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dao.VehicleDAO;
import pricing.DailyPricing;
import pricing.PricingStrategy;
import rental.RentalServiceFacade;
import rental.exception.RentalException;
import vehicle.Car;
import vehicle.Vehicle;
import rental.Invoice;

// PENTING: Ini adalah INTEGRATION TEST, bukan UNIT TEST.
// Test ini membutuhkan koneksi ke database yang aktif dan data yang sesuai dengan script.sql.
// Kegagalan pada test ini bisa disebabkan oleh masalah koneksi/data di database, bukan hanya logika kode.
class RentalFacadeTest {

    private RentalServiceFacade facade;
    private VehicleDAO vehicleDAO;

    @BeforeEach
    void setUp() {
        // Karena Facade tidak menggunakan Dependency Injection, kita harus membuat instance nyata.
        // Ini membuat test menjadi integration test.
        vehicleDAO = new VehicleDAO();
        facade = new RentalServiceFacade(vehicleDAO);
    }

    @Test
    @DisplayName("Should calculate estimated price correctly")
    void testCalculateEstPrice() {
        PricingStrategy dailyPricing = new DailyPricing();
        Vehicle car = new Car(1, "B 1234 ABC", "Toyota", "Avanza", 500000, true);
        
        double price = facade.calculateEstPrice(car, dailyPricing, 3);
        
        assertEquals(1500000, price, "Price should be base price * duration for daily pricing");
    }

    @Test
    @DisplayName("Should successfully process a complete booking")
    void testProcessCompleteBooking_Success() throws Exception {
        // Test ini berasumsi ada kendaraan yang tersedia di database.
        // Kita ambil kendaraan pertama yang tersedia.
        Vehicle availableVehicle = vehicleDAO.getAll().stream()
            .filter(Vehicle::isAvailable)
            .findFirst()
            .orElse(null);

        assertNotNull(availableVehicle, "Database should have at least one available vehicle for this test to run");

        // Asumsi customer 'user' ada di database (dari script.sql)
        String customerName = "user";
        PricingStrategy strategy = new DailyPricing();
        int duration = 2;

        // Jalankan proses booking
        Invoice invoice = facade.processCompleteBooking(availableVehicle, strategy, duration, customerName);

        // Verifikasi dasar
        assertNotNull(invoice, "Invoice should not be null on successful booking");
        assertEquals(customerName, invoice.getCustomerName());
        assertEquals(availableVehicle.getId(), invoice.getVehicle().getId());
        
        // Verifikasi side-effect: kendaraan seharusnya sudah tidak tersedia
        Vehicle vehicleAfterBooking = vehicleDAO.findById(availableVehicle.getId());
        assertFalse(vehicleAfterBooking.isAvailable(), "Vehicle should be unavailable after booking");

        // Kembalikan state kendaraan agar test lain tidak terpengaruh
        vehicleDAO.updateAvailability(availableVehicle.getId(), true);
    }

    @Test
    @DisplayName("Should fail to book a vehicle that is not available")
    void testProcessCompleteBooking_VehicleNotAvailable() throws SQLException {
        // Ambil kendaraan pertama yang TIDAK tersedia. Jika tidak ada, buat satu.
        Vehicle unavailableVehicle = vehicleDAO.getAll().stream()
            .filter(v -> !v.isAvailable())
            .findFirst()
            .orElse(null);
        
        // Jika semua mobil tersedia, kita buat satu jadi tidak tersedia
        if (unavailableVehicle == null) {
            Vehicle vehicleToUpdate = vehicleDAO.getAll().get(0);
            assertNotNull(vehicleToUpdate, "There must be at least one vehicle in the DB");
            vehicleDAO.updateAvailability(vehicleToUpdate.getId(), false);
            unavailableVehicle = vehicleDAO.findById(vehicleToUpdate.getId());
        }

        final Vehicle finalUnavailableVehicle = unavailableVehicle;
        
        // Coba booking kendaraan yang tidak tersedia, harapkan exception
        RentalException exception = assertThrows(RentalException.class, () -> {
            facade.processCompleteBooking(finalUnavailableVehicle, new DailyPricing(), 1, "user");
        });

        assertEquals(RentalException.ErrorCode.VEHICLE_NOT_AVAILABLE, exception.getErrorCode());

        // Kembalikan state kendaraan
        vehicleDAO.updateAvailability(finalUnavailableVehicle.getId(), true);
    }
}
