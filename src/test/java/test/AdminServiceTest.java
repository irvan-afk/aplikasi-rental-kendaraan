package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dao.VehicleDAO;
import rental.Rental;
import service.AdminService;
import vehicle.Vehicle;
import vehicle.factory.CarFactory;

// PENTING: Ini adalah INTEGRATION TEST, bukan UNIT TEST.
// Test ini membutuhkan koneksi ke database yang aktif.
// Test ini juga memodifikasi database (menambah dan menghapus data).
class AdminServiceTest {

    private AdminService adminService;
    private VehicleDAO vehicleDAO;
    private Vehicle tempVehicle;

    @BeforeEach
    void setUp() {
        vehicleDAO = new VehicleDAO();
        adminService = new AdminService(vehicleDAO);
        tempVehicle = null;
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Membersihkan data yang mungkin tersisa jika test gagal di tengah jalan
        if (tempVehicle != null && vehicleDAO.findById(tempVehicle.getId()) != null) {
            adminService.deleteVehicle(tempVehicle.getId());
        }
    }

    @Test
    @DisplayName("Should add a new vehicle and then delete it")
    void testAddAndThenDeleteVehicle() throws SQLException {
        // Gunakan plat nomor unik untuk menghindari konflik jika test gagal sebelumnya
        String uniquePlate = "TEST-" + UUID.randomUUID().toString().substring(0, 8);
        
        // 1. Add
        tempVehicle = adminService.addVehicle(new CarFactory(), uniquePlate, "TestBrand", "TestModel", 1000);
        assertNotNull(tempVehicle, "addVehicle should return the created vehicle");
        assertTrue(tempVehicle.getId() > 0, "Vehicle ID should be populated after insertion");

        // 2. Verify addition
        Vehicle foundVehicle = vehicleDAO.findByPlate(uniquePlate);
        assertNotNull(foundVehicle, "Vehicle should be findable in DB after adding");
        assertEquals("TestBrand", foundVehicle.getBrand());

        // 3. Delete
        adminService.deleteVehicle(foundVehicle.getId());

        // 4. Verify deletion
        Vehicle deletedVehicle = vehicleDAO.findById(foundVehicle.getId());
        assertNull(deletedVehicle, "Vehicle should not be findable in DB after deleting");
        tempVehicle = null; // Set null agar tidak dihapus lagi di tearDown
    }

    @Test
    @DisplayName("Should update an existing vehicle's details")
    void testUpdateVehicle() throws SQLException {
        // 1. Setup: Add a vehicle to be updated
        String uniquePlate = "UPDATE-" + UUID.randomUUID().toString().substring(0, 8);
        tempVehicle = adminService.addVehicle(new CarFactory(), uniquePlate, "OldBrand", "OldModel", 2000);

        // 2. Update properties
        tempVehicle.setBrand("NewBrand");
        tempVehicle.setModel("NewModel");
        adminService.updateVehicle(tempVehicle);

        // 3. Verify update
        Vehicle updatedVehicle = vehicleDAO.findById(tempVehicle.getId());
        assertNotNull(updatedVehicle);
        assertEquals("NewBrand", updatedVehicle.getBrand());
        assertEquals("NewModel", updatedVehicle.getModel());
    }

    @Test
    @DisplayName("Should correctly report dashboard statistics")
    void testDashboardStats() throws SQLException {
        // Ambil data awal
        int initialRented = adminService.getNumberOfVehiclesRented();
        int initialAvailable = adminService.getNumberOfVehiclesAvailable();
        
        // 1. Setup: add a new, available vehicle
        String uniquePlate = "STAT-" + UUID.randomUUID().toString().substring(0, 8);
        tempVehicle = adminService.addVehicle(new CarFactory(), uniquePlate, "StatBrand", "StatModel", 3000);
        
        // Verifikasi jumlah available bertambah 1
        assertEquals(initialAvailable + 1, adminService.getNumberOfVehiclesAvailable());

        // 2. Set vehicle to unavailable (rented)
        adminService.setAvailability(tempVehicle.getId(), false);

        // 3. Verify stats
        assertEquals(initialRented + 1, adminService.getNumberOfVehiclesRented());
        assertEquals(initialAvailable, adminService.getNumberOfVehiclesAvailable());

        // 4. Clean up by setting it back to available
        adminService.setAvailability(tempVehicle.getId(), true);
        assertEquals(initialAvailable + 1, adminService.getNumberOfVehiclesAvailable());
    }

    @Test
    @DisplayName("Should list all active rentals")
    void testListActiveRentals() throws Exception {
        // Test ini sulit dilakukan tanpa setup data yang kompleks (user, customer, vehicle, rental).
        // Untuk saat ini, kita hanya panggil methodnya dan pastikan tidak error.
        // Test yang lebih baik akan memerlukan setup data di database terlebih dahulu.
        List<Rental> activeRentals = adminService.listActiveRentals();
        assertNotNull(activeRentals, "The list of active rentals should not be null, even if empty");
    }
}
