package model;

public abstract class Vehicle {

    private int id;
    private String brand;
    private String model;
    private double dailyPrice;
    private boolean available = true;

    public Vehicle(int id, String brand, String model, double dailyPrice) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.dailyPrice = dailyPrice;
    }

    public abstract double calculateRentalCost(int days);

    // getter sama setter nya
    public int getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getDailyPrice() { return dailyPrice; }
    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }
}
