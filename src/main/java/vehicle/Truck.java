package main.java.vehicle;

public class Truck extends Vehicle {

    // Di sini Anda bisa menambahkan atribut khusus untuk truk, contoh:
    // private double cargoCapacity;

    public Truck(String plateNumber, String brand, String model, double basePrice) {
        super(plateNumber, "Truck", brand, model, basePrice);
    }

    public Truck(int id, String plateNumber, String brand, String model, double basePrice, boolean available) {
        super(id, plateNumber, "Truck", brand, model, basePrice, available);
    }
}