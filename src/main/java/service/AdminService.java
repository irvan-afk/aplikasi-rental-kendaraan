package main.java.service;

import main.java.dao.VehicleDAO;
import main.java.vehicle.Vehicle;
import main.java.vehicle.factory.VehicleFactory;

import java.sql.SQLException;
import java.util.List;

public class AdminService {

    private VehicleDAO vehicleDAO;

    public AdminService(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    // create vehicle using factory, then save to DB
    public Vehicle addVehicle(VehicleFactory factory, String plate, String brand, String model, double basePrice) throws SQLException {
        Vehicle v = factory.createVehicle(plate, brand, model, basePrice);
        vehicleDAO.insertVehicle(v);
        return v;
    }

    public List<Vehicle> listVehicles() throws SQLException {
        return vehicleDAO.getAll();
    }

    public Vehicle findByPlate(String plate) throws SQLException {
        return vehicleDAO.findByPlate(plate);
    }

    public void deleteVehicle(int id) throws SQLException {
        vehicleDAO.deleteById(id);
    }

    public void setAvailability(int id, boolean available) throws SQLException {
        vehicleDAO.updateAvailability(id, available);
    }
}
