package main.java.vehicle;

public class Motorcycle extends Vehicle {

    // Di sini Anda bisa menambahkan atribut khusus untuk motor, contoh:
    // private boolean hasSidecar;

    public Motorcycle(String plateNumber, String brand, String model, double basePrice) {
        super(plateNumber, "Motorcycle", brand, model, basePrice);
    }

    public Motorcycle(int id, String plateNumber, String brand, String model, double basePrice, boolean available) {
        super(id, plateNumber, "Motorcycle", brand, model, basePrice, available);
    }
}