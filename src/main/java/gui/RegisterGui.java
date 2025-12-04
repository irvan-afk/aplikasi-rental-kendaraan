package gui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.UserDAO;

public class RegisterGui extends JDialog {
    private JTextField userField;
    private JPasswordField passField;
    private UserDAO userDAO;

    public RegisterGui(Frame owner) {
        super(owner, "Register", true); // true for modal
        userDAO = new UserDAO();
        
        setSize(300, 150);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(3, 2, 5, 5));

        add(new JLabel("Username:"));
        userField = new JTextField();
        add(userField);

        add(new JLabel("Password:"));
        passField = new JPasswordField();
        add(passField);

        JButton registerButton = new JButton("Register");
        add(new JLabel("")); // spacer
        add(registerButton);

        registerButton.addActionListener(e -> handleRegister());
    }

    private void handleRegister() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!");
            return;
        }

        try {
            boolean success = userDAO.registerUser(user, pass);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan login.");
                dispose(); // Close the dialog
            } else {
                JOptionPane.showMessageDialog(this, "Registrasi gagal. Coba lagi.");
            }
        } catch (SQLException ex) {
            // Check for duplicate key violation
            if (ex.getSQLState().equals("23505")) { // This SQLSTATE is for unique violation in PostgreSQL
                JOptionPane.showMessageDialog(this, "Username sudah ada. Silakan pilih yang lain.");
            } else {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        }
    }
}
