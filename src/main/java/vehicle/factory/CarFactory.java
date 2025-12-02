package vehicle.factory;

import vehicle.Car;
import vehicle.Vehicle;

public class CarFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle(String plate, String brand, String model, double basePrice) {
        return new Car(plate, brand, model, basePrice);
    }
}
