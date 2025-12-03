package vehicle;

public class Car extends Vehicle {

    // Di sini Anda bisa menambahkan atribut khusus untuk mobil, contoh:
    // private int numberOfDoors;

    public Car(String plateNumber, String brand, String model, double basePrice) {
        super(plateNumber, "Car", brand, model, basePrice);
    }

    public Car(int id, String plateNumber, String brand, String model, double basePrice, boolean available) {
        super(id, plateNumber, "Car", brand, model, basePrice, available);
    }
}