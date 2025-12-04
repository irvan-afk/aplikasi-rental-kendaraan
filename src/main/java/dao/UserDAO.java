package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.ConnectionManager;

public class UserDAO {
    // Mereturn role jika login sukses, return null jika gagal
    public String authenticate(String username, String password) throws SQLException {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        }
        return null;
    }    public boolean registerUser(String username, String password) throws SQLException {
        // Default role for new users is CUSTOMER
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'CUSTOMER')";
        
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            int affectedRows = ps.executeUpdate();
            
            // Return true if one row was affected, meaning the user was created
            return affectedRows == 1;
        }
        // The try-with-resources will handle closing the connection and statement.
        // SQLException will be thrown for issues like duplicate username (if constraint exists)
    }
}
