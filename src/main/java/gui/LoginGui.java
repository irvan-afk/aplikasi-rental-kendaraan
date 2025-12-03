package gui;

import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginGui extends JFrame {

    private final AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGui() {
        this.authService = new AuthService();

        setTitle("Admin Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (authService.authenticate(username, password)) {
            // If authenticated, open the main app and close the login window
            SwingUtilities.invokeLater(() -> {
                new RentalAppGui().setVisible(true);
                this.dispose();
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
