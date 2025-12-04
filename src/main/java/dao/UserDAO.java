package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import app.User;
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
            
            return affectedRows == 1;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, role FROM users ORDER BY id ASC";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
                users.add(user);
            }
        }
        return users;
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getId());

            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ConnectionManager.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        }
    }
}
