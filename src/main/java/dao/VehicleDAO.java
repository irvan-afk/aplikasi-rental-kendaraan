package dao;

import db.ConnectionManager;
import vehicle.Car;
import vehicle.Motorcycle;
import vehicle.Truck;
import vehicle.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public void insertVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (plate, type, brand, model, base_price, available) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vehicle.getPlateNumber());
            ps.setString(2, vehicle.getType());
            ps.setString(3, vehicle.getBrand());
            ps.setString(4, vehicle.getModel());
            ps.setDouble(5, vehicle.getBasePrice());
            ps.setBoolean(6, vehicle.isAvailable());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    vehicle.setId(keys.getInt(1)); // set id generated
                }
            }
        }
    }

    public Vehicle findByPlate(String plate) throws SQLException {
        String sql = "SELECT id, plate, type, brand, model, base_price, available FROM vehicles WHERE plate = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Vehicle findById(int id) throws SQLException {
        String sql = "SELECT id, plate, type, brand, model, base_price, available FROM vehicles WHERE id = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Vehicle> getAll() throws SQLException {
        String sql = "SELECT id, plate, type, brand, model, base_price, available FROM vehicles ORDER BY id";
        List<Vehicle> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void deleteById(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtRental = null;
        PreparedStatement stmtVehicle = null;

        try {
            conn = ConnectionManager.getDataSource().getConnection(); 
            conn.setAutoCommit(false); // PENTING: Pakai Transaksi agar aman

            // LANGKAH 1: Hapus data di tabel Anak (Rentals) dulu
            // Ini solusi untuk error "foreign key constraint"
            String sqlDeleteRentals = "DELETE FROM rentals WHERE vehicle_id = ?";
            stmtRental = conn.prepareStatement(sqlDeleteRentals);
            stmtRental.setInt(1, id);
            stmtRental.executeUpdate();

            // LANGKAH 2: Hapus data di tabel Induk (Vehicles)
            // Hapus bagian ", status = ..." jika Anda sempat menambahkannya
            String sqlDeleteVehicle = "DELETE FROM vehicles WHERE id = ?";
            stmtVehicle = conn.prepareStatement(sqlDeleteVehicle);
            stmtVehicle.setInt(1, id);
            stmtVehicle.executeUpdate();

            conn.commit(); // Simpan perubahan
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Batalkan jika ada error
            throw e;
        } finally {
            // Tutup resource
            if (stmtRental != null) stmtRental.close();
            if (stmtVehicle != null) stmtVehicle.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void updateAvailability(int id, boolean available) throws SQLException {
        String sql = "UPDATE vehicles SET available = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET plate = ?, brand = ?, model = ?, base_price = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicle.getPlateNumber());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getModel());
            ps.setDouble(4, vehicle.getBasePrice());
            ps.setInt(5, vehicle.getId());
            ps.executeUpdate();
        }
    }

    public int countByAvailability(boolean isAvailable) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE available = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isAvailable);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String plate = rs.getString("plate");
        String type = rs.getString("type");
        String brand = rs.getString("brand");
        String model = rs.getString("model");
        double basePrice = rs.getDouble("base_price");
        boolean available = rs.getBoolean("available");

        switch (type) {
            case "Car":
                return new Car(id, plate, brand, model, basePrice, available);
            case "Motorcycle":
                return new Motorcycle(id, plate, brand, model, basePrice, available);
            case "Truck":
                return new Truck(id, plate, brand, model, basePrice, available);
            default:
                // Fallback or throw an exception if type is unknown
                throw new SQLException("Unknown vehicle type: " + type);
        }
    }
}
