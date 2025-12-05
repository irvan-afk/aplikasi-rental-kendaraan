package service;

import dao.RentalDAO;
import dao.VehicleDAO;
import rental.Rental;
import vehicle.Vehicle;
import vehicle.factory.VehicleFactory;

import java.sql.SQLException;
import java.util.List;

public class AdminService {

    private final VehicleDAO vehicleDAO;
    private final RentalDAO rentalDAO;

    public AdminService(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
        this.rentalDAO = new RentalDAO();
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
    
    public List<Rental> listActiveRentals() throws SQLException {
        return rentalDAO.findAllActiveRentals();
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

    public Vehicle findById(int id) throws SQLException {
        return vehicleDAO.findById(id);
    }

    public void updateVehicle(Vehicle vehicle) throws SQLException {
        vehicleDAO.updateVehicle(vehicle);
    }

    public int getNumberOfVehiclesAvailable() throws SQLException {
        return vehicleDAO.countByAvailability(true);
    }

    public int getNumberOfVehiclesRented() throws SQLException {
        return vehicleDAO.countByAvailability(false);
    }
}
