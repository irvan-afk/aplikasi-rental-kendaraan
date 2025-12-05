package vehicle;

public class Truck extends Vehicle {

    public Truck(String plateNumber, String brand, String model, double basePrice) {
        super(plateNumber, "Truck", brand, model, basePrice);
    }

    public Truck(int id, String plateNumber, String brand, String model, double basePrice, boolean available) {
        super(id, plateNumber, "Truck", brand, model, basePrice, available);
    }
}