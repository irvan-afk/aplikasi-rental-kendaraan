package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dao.CustomerDAO;
import dao.UserDAO;

public class RegisterGui extends JDialog {
    private JTextField userField;
    private JPasswordField passField;
    private UserDAO userDAO;
    private CustomerDAO customerDAO;

    public RegisterGui(Frame owner) {
        super(owner, "Pendaftaran Akun Baru", true);
        userDAO = new UserDAO();
        customerDAO = new CustomerDAO();
        
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(50, 100, 150));
        JLabel titleLabel = new JLabel("Form Registrasi");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 2. FORM
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Username Baru:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        userField = new JTextField(15);
        formPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(15);
        formPanel.add(passField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 3. FOOTER
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        
        JButton registerButton = new JButton("Daftar Sekarang");
        registerButton.setBackground(new Color(50, 150, 50)); // Hijau
        registerButton.setForeground(Color.WHITE);
        registerButton.setPreferredSize(new Dimension(150, 35));

        JButton cancelButton = new JButton("Batal");
        cancelButton.setPreferredSize(new Dimension(80, 35));

        btnPanel.add(registerButton);
        btnPanel.add(cancelButton);
        add(btnPanel, BorderLayout.SOUTH);

        // Events
        registerButton.addActionListener(e -> handleRegister());
        cancelButton.addActionListener(e -> dispose());
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
                // NOTE: This should ideally be in a single transaction.
                // For this project's scope, we do it sequentially.
                int newUserId = userDAO.findIdByUsername(user);
                customerDAO.createCustomerForUser(newUserId, user); // Use username as default name

                JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan login.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registrasi gagal. Coba lagi.");
            }
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("23505")) { // Unique violation
                JOptionPane.showMessageDialog(this, "Username sudah ada. Silakan pilih yang lain.");
            } else {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        }
    }
}