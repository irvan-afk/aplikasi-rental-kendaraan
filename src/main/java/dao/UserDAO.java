package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.ConnectionManager;

public class UserDAO {
    // Returns {role, username} on success, null on failure
    public String[] authenticate(String username, String password) throws SQLException {
        String sql = "SELECT role, username FROM users WHERE username = ? AND password = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("role"), rs.getString("username")};
                }
            }
        }
        return null;
    }

    public boolean registerUser(String username, String password) throws SQLException {
        // Default role for new users is CUSTOMER
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'CUSTOMER')";
        
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            int affectedRows = ps.executeUpdate();
            
            return affectedRows == 1;
        }
    }

    public int findIdByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("User with username '" + username + "' not found.");
                }
            }
        }
    }
}
