package main.java.vehicle.factory;

import main.java.vehicle.Truck;
import main.java.vehicle.Vehicle;

public class TruckFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle(String plate, String brand, String model, double basePrice) {
        return new Truck(plate, brand, model, basePrice);
    }
}
