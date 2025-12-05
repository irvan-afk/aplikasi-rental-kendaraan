package dao;

import db.ConnectionManager;
import java.sql.*;

public class CustomerDAO {

    public void createCustomerForUser(int userId, String name) throws SQLException {
        String sql = "INSERT INTO customers (user_id, name) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        }
    }

    public int findCustomerIdByUsername(String username) throws SQLException {
        String sql = "SELECT c.id FROM customers c JOIN users u ON c.user_id = u.id WHERE u.username = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    // This case should be handled gracefully.
                    // Maybe the user is an admin or a user without a customer profile.
                    throw new SQLException("Customer ID for username '" + username + "' not found.");
                }
            }
        }
    }
}
