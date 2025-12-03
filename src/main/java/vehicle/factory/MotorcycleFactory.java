package vehicle.factory;

import vehicle.Motorcycle;
import vehicle.Vehicle;

public class MotorcycleFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle(String plate, String brand, String model, double basePrice) {
        return new Motorcycle(plate, brand, model, basePrice);
    }
}
