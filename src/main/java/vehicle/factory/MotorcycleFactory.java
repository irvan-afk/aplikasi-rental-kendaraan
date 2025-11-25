package main.java.vehicle.factory;

import main.java.vehicle.Motorcycle;
import main.java.vehicle.Vehicle;

public class MotorcycleFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle(String plate, String brand, String model, double basePrice) {
        return new Motorcycle(plate, brand, model, basePrice);
    }
}
