package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dao.VehicleDAO;
import pricing.DailyPricing;
import pricing.PricingStrategy;
import rental.Invoice;
import rental.RentalServiceFacade;
import rental.exception.RentalException;
import vehicle.Car;
import vehicle.Vehicle;

class RentalFacadeTest {

    private RentalServiceFacade facade;
    private VehicleDAO vehicleDAO;
    private Vehicle testVehicle; 

    @BeforeEach
    void setUp() throws SQLException {
        vehicleDAO = new VehicleDAO();
        facade = new RentalServiceFacade(vehicleDAO);
        testVehicle = null; 
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (testVehicle != null) {
            // Check apakah kendaraan sudah tersimpan di DB (punya ID)
            Vehicle vehicleInDb = vehicleDAO.findById(testVehicle.getId());
            
            if (vehicleInDb != null) {
                try {
                    vehicleDAO.deleteById(testVehicle.getId());
                } catch (Exception e) {
                    System.out.println("Warning: Gagal membersihkan data rental: " + e.getMessage());
                }

            }
        }
    }

    @Test
    @DisplayName("Should calculate estimated price correctly")
    void testCalculateEstPrice() {
        PricingStrategy dailyPricing = new DailyPricing();
        Vehicle car = new Car("B 1234 ABC", "Toyota", "Avanza", 500000);
        
        double price = facade.calculateEstPrice(car, dailyPricing, 3);
        
        assertEquals(1500000, price, "Price should be base price * duration for daily pricing");
    }

    @Test
    @DisplayName("Should successfully process a complete booking")
    void testProcessCompleteBooking_Success() throws Exception {
        String uniquePlate = "SUCCESS-" + UUID.randomUUID().toString().substring(0, 8);
        testVehicle = new Car(uniquePlate, "Toyota", "Avanza", 250000);
        vehicleDAO.insertVehicle(testVehicle);

        String customerName = "user";
        PricingStrategy strategy = new DailyPricing();
        int duration = 2;

        Invoice invoice = facade.processCompleteBooking(testVehicle, strategy, duration, customerName);

        assertNotNull(invoice, "Invoice should not be null on successful booking");
        assertEquals(customerName, invoice.getCustomerName());
        
        Vehicle vehicleAfterBooking = vehicleDAO.findById(testVehicle.getId());
        assertNotNull(vehicleAfterBooking);
        assertFalse(vehicleAfterBooking.isAvailable(), "Vehicle should be unavailable after booking");
        
        vehicleDAO.updateAvailability(testVehicle.getId(), true);
    }

    @Test
    @DisplayName("Should fail to book a vehicle that is not available")
    void testProcessCompleteBooking_VehicleNotAvailable() throws Exception {
        String uniquePlate = "UNAVAILABLE-" + UUID.randomUUID().toString().substring(0, 8);
        testVehicle = new Car(uniquePlate, "Honda", "Jazz", 300000);
        vehicleDAO.insertVehicle(testVehicle);
        vehicleDAO.updateAvailability(testVehicle.getId(), false);

        RentalException exception = assertThrows(RentalException.class, () -> {
            facade.processCompleteBooking(testVehicle, new DailyPricing(), 1, "user");
        });

        assertEquals(RentalException.ErrorCode.VEHICLE_NOT_AVAILABLE, exception.getErrorCode());
    }
}