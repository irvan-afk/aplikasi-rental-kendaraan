package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import vehicle.Car;
import vehicle.Motorcycle;
import vehicle.Truck;
import vehicle.Vehicle;

class VehicleTest {

    @Test
    @DisplayName("Should create a Car with correct properties")
    void testCarCreation() {
        Vehicle car = new Car("B 1234 ABC", "Toyota", "Avanza", 500000);

        assertEquals("Car", car.getType());
        assertEquals("B 1234 ABC", car.getPlateNumber());
        assertEquals("Toyota", car.getBrand());
        assertEquals("Avanza", car.getModel());
        assertEquals(500000, car.getBasePrice());
        assertTrue(car.isAvailable(), "New car should be available by default");
    }

    @Test
    @DisplayName("Should create a Motorcycle with correct properties")
    void testMotorcycleCreation() {
        Vehicle motorcycle = new Motorcycle("L 5678 XYZ", "Honda", "Vario", 150000);

        assertEquals("Motorcycle", motorcycle.getType());
        assertEquals("L 5678 XYZ", motorcycle.getPlateNumber());
        assertEquals("Honda", motorcycle.getBrand());
        assertEquals("Vario", motorcycle.getModel());
        assertEquals(150000, motorcycle.getBasePrice());
        assertTrue(motorcycle.isAvailable());
    }

    @Test
    @DisplayName("Should create a Truck with all arguments constructor")
    void testTruckCreationWithAllArgs() {
        Vehicle truck = new Truck(10, "N 9999 NN", "Mitsubishi", "Fuso", 1200000, false);

        assertEquals(10, truck.getId());
        assertEquals("Truck", truck.getType());
        assertEquals("N 9999 NN", truck.getPlateNumber());
        assertEquals("Mitsubishi", truck.getBrand());
        assertEquals("Fuso", truck.getModel());
        assertEquals(1200000, truck.getBasePrice());
        assertFalse(truck.isAvailable(), "Truck should be unavailable as specified in constructor");
    }

    @Test
    @DisplayName("Should allow setting and getting properties")
    void testSettersAndGetters() {
        Vehicle car = new Car("Z 1 Z", "Brand", "Model", 100);

        // Test setters
        car.setId(99);
        car.setPlateNumber("A 1 B");
        car.setBrand("NewBrand");
        car.setModel("NewModel");
        car.setBasePrice(200);
        car.setAvailable(false);

        // Verify with getters
        assertEquals(99, car.getId());
        assertEquals("A 1 B", car.getPlateNumber());
        assertEquals("NewBrand", car.getBrand());
        assertEquals("NewModel", car.getModel());
        assertEquals(200, car.getBasePrice());
        assertFalse(car.isAvailable());
    }
}
