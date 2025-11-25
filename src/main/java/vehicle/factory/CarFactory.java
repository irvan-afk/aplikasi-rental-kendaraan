package main.java.vehicle.factory;

import main.java.vehicle.Vehicle;

public class CarFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle(String plate, String brand, String model, double basePrice) {
        return new Vehicle(plate, "Car", brand, model, basePrice);
    }
}
