package vehicle.factory;

import vehicle.Vehicle;

public abstract class VehicleFactory {
    public abstract Vehicle createVehicle(String plate, String brand, String model, double basePrice);
}
