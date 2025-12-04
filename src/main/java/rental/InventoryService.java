package rental;

import dao.VehicleDAO;
import rental.exception.RentalException;
import vehicle.Vehicle;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryService {

    private final VehicleDAO vehicleDAO;

    public InventoryService(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    public List<Vehicle> getAvailableVehicles() throws RentalException {
        try {
            return vehicleDAO.getAll().stream()
                    .filter(Vehicle::isAvailable)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RentalException(RentalException.ErrorCode.DATABASE_ERROR,
                    "Gagal mengambil data kendaraan");
        }
    }

    public boolean isVehicleAvailable(int vehicleId) throws RentalException {
        try {
            Vehicle v = vehicleDAO.findById(vehicleId);
            if (v == null)
                throw new RentalException(RentalException.ErrorCode.VEHICLE_NOT_FOUND);
            return v.isAvailable();
        } catch (SQLException e) {
            throw new RentalException(RentalException.ErrorCode.DATABASE_ERROR, e.getMessage());
        }
    }

    public void reserveVehicle(int vehicleId) throws RentalException {
        try {
            if (!isVehicleAvailable(vehicleId))
                throw new RentalException(RentalException.ErrorCode.VEHICLE_NOT_AVAILABLE);

            vehicleDAO.updateAvailability(vehicleId, false);
        } catch (SQLException e) {
            throw new RentalException(RentalException.ErrorCode.DATABASE_ERROR,
                    "Gagal mereserve kendaraan");
        }
    }

    public void releaseVehicle(int vehicleId) throws RentalException {
        try {
            vehicleDAO.updateAvailability(vehicleId, true);
        } catch (SQLException e) {
            throw new RentalException(RentalException.ErrorCode.DATABASE_ERROR,
                    "Gagal melepas kendaraan");
        }
    }

    public Vehicle getVehicleById(int id) throws RentalException {
        try {
            Vehicle v = vehicleDAO.findById(id);
            if (v == null)
                throw new RentalException(RentalException.ErrorCode.VEHICLE_NOT_FOUND);

            return v;
        } catch (SQLException e) {
            throw new RentalException(RentalException.ErrorCode.DATABASE_ERROR, e.getMessage());
        }
    }
}
