package gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.UserDAO;

public class LoginGui extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private UserDAO userDAO;

    public LoginGui() {
        userDAO = new UserDAO();
        setTitle("Login Rental Kendaraan");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 5, 5));

        add(new JLabel("Username:"));
        userField = new JTextField();
        add(userField);

        add(new JLabel("Password:"));
        passField = new JPasswordField();
        add(passField);

        JButton loginButton = new JButton("Login");
        add(new JLabel("")); // spacer
        add(loginButton);

        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        try {
            String role = userDAO.authenticate(user, pass);
            if (role != null) {
                this.dispose(); // Tutup window login
                if (role.equalsIgnoreCase("ADMIN")) {
                    // Buka GUI Admin yang sudah kamu punya
                    new AdminAppGui().setVisible(true);
                } else {
                    // Buka GUI Pelanggan (Kita buat di langkah 5)
                    new CustomerAppGui().setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    
    // Update Main.java kamu nanti untuk memanggil ini dulu
}