package gui;

import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.UserDAO;

public class LoginGui extends JDialog {
    private JTextField userField;
    private JPasswordField passField;
    private UserDAO userDAO;
    private String authenticatedRole = null;

    public LoginGui(Frame owner) {
        super(owner, "Login", true); // true for modal
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
                this.authenticatedRole = role;
                dispose(); // Tutup dialog
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    
    public String getAuthenticatedRole() {
        return authenticatedRole;
    }
}