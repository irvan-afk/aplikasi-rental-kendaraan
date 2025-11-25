package main.java.app;

import main.java.vehicle.factory.CarFactory;
import main.java.vehicle.factory.MotorcycleFactory;
import main.java.vehicle.Vehicle;
import main.java.dao.VehicleDAO;
import main.java.service.AdminService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        VehicleDAO dao = new VehicleDAO();
        AdminService admin = new AdminService(dao);

        try {
            // Tambah mobil via factory
            Vehicle car = admin.addVehicle(new CarFactory(), "D1234ABC", "Toyota", "Avanza", 300000);
            System.out.println("Added: " + car);

            // Tambah motor
            Vehicle motorcycle = admin.addVehicle(new MotorcycleFactory(), "D5678XYZ", "Honda", "CBR150", 150000);
            System.out.println("Added: " + motorcycle);

            // List semua kendaraan
            List<Vehicle> all = admin.listVehicles();
            System.out.println("\n--- Semua Kendaraan ---");
            all.forEach(System.out::println);

            // Cari per plat
            Vehicle found = admin.findByPlate("D1234ABC");
            System.out.println("\nFound: " + found);

            // Set availability false (misalnya sedang disewa)
            admin.setAvailability(found.getId(), false);
            System.out.println("Set availability false for id=" + found.getId());

            // List lagi
            all = admin.listVehicles();
            all.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
