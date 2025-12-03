package service;

import dao.UserDAO;
import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public boolean authenticate(String username, String password) {
        try {
            if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
                return false;
            }
            return userDAO.validate(username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            // In a real app, you'd want to log this error more robustly
            return false;
        }
    }
}
