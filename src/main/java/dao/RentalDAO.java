package dao;

import db.ConnectionManager;
import rental.Rental;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    public void save(Rental rental) throws SQLException {
        String sql = "INSERT INTO rentals (vehicle_id, customer_id, start_date, end_date, total_cost, duration_days) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Calculate duration_days, assuming dates are not null
            long diff = rental.getEndDate().getTime() - rental.getStartDate().getTime();
            int durationDays = (int) (diff / (1000 * 60 * 60 * 24));

            pstmt.setInt(1, rental.getVehicleId());
            pstmt.setInt(2, rental.getCustomerId());
            pstmt.setDate(3, new java.sql.Date(rental.getStartDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(rental.getEndDate().getTime()));
            pstmt.setDouble(5, rental.getTotalCost());
            pstmt.setInt(6, durationDays);
            pstmt.executeUpdate();
        }
    }

    public List<Rental> findRentalsByUsername(String username) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.id, r.start_date, r.end_date, r.total_cost, " +
                     "v.id as vehicle_id, v.brand, v.model, v.plate, v.available " +
                     "FROM rentals r " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "JOIN customers c ON r.customer_id = c.id " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE u.username = ? " +
                     "ORDER BY r.start_date DESC";

        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Rental rental = new Rental();
                    rental.setId(rs.getInt("id"));
                    rental.setStartDate(rs.getDate("start_date"));
                    rental.setEndDate(rs.getDate("end_date"));
                    rental.setTotalCost(rs.getDouble("total_cost"));
                    rental.setVehicleId(rs.getInt("vehicle_id"));
                    rental.setVehicleBrand(rs.getString("brand"));
                    rental.setVehicleModel(rs.getString("model"));
                    rental.setVehiclePlate(rs.getString("plate"));
                    rental.setVehicleIsAvailable(rs.getBoolean("available"));
                    rentals.add(rental);
                }
            }
        }
        return rentals;
    }

    public int findVehicleIdByRentalId(int rentalId) throws SQLException {
        String sql = "SELECT vehicle_id FROM rentals WHERE id = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rentalId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("vehicle_id");
                } else {
                    throw new SQLException("Rental with ID " + rentalId + " not found.");
                }
            }
        }
    }

    public List<Rental> findAllActiveRentals() throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        // This query joins rentals with vehicles and customers
        // and selects the most recent rental for each vehicle that is currently NOT available.
        String sql = "SELECT r.*, v.brand, v.model, v.plate, v.available, c.name as customer_name " +
                     "FROM rentals r " +
                     "JOIN ( " +
                     "    SELECT vehicle_id, MAX(start_date) as max_start_date " +
                     "    FROM rentals " +
                     "    GROUP BY vehicle_id " +
                     ") latest_r ON r.vehicle_id = latest_r.vehicle_id AND r.start_date = latest_r.max_start_date " +
                     "JOIN vehicles v ON r.vehicle_id = v.id " +
                     "JOIN customers c ON r.customer_id = c.id " +
                     "WHERE v.available = false " +
                     "ORDER BY r.start_date DESC";

        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Rental rental = new Rental();
                    rental.setId(rs.getInt("id"));
                    rental.setStartDate(rs.getDate("start_date"));
                    rental.setEndDate(rs.getDate("end_date"));
                    rental.setTotalCost(rs.getDouble("total_cost"));
                    rental.setVehicleId(rs.getInt("vehicle_id"));
                    rental.setVehicleBrand(rs.getString("brand"));
                    rental.setVehicleModel(rs.getString("model"));
                    rental.setVehiclePlate(rs.getString("plate"));
                    rental.setVehicleIsAvailable(rs.getBoolean("available"));
                    rental.setCustomerName(rs.getString("customer_name"));
                    rentals.add(rental);
                }
            }
        }
        return rentals;
    }
}
