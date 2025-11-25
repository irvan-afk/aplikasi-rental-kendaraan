package main.java.vehicle;

public abstract class Vehicle {
    private int id; // id dari DB
    private String plateNumber;
    private String type;
    private String brand;
    private String model;
    private double basePrice;
    private boolean available;

    // Constructor untuk object baru (belum ada id)
    protected Vehicle(String plateNumber, String type, String brand, String model, double basePrice) {
        this(0, plateNumber, type, brand, model, basePrice, true);
    }

    // Full constructor (untuk mapping dari DB)
    protected Vehicle(int id, String plateNumber, String type, String brand, String model, double basePrice, boolean available) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.basePrice = basePrice;
        this.available = available;
    }

    // getters & setters
    public int getId() { return id; }
    public String getPlateNumber() { return plateNumber; }
    public String getType() { return type; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getBasePrice() { return basePrice; }
    public boolean isAvailable() { return available; }

    public void setId(int id) { this.id = id; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    @Override
    public String toString() {
        return "[" + id + "] " + plateNumber + " - " + type + " - " + brand + " " + model + " (Rp " + basePrice + ") Available: " + available;
    }
}
