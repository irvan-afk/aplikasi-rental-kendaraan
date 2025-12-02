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
    }
}