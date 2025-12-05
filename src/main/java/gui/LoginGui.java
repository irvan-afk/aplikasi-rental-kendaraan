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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import dao.UserDAO;

public class LoginGui extends JDialog {
    private JTextField userField;
    private JPasswordField passField;
    private UserDAO userDAO;
    private String authenticatedRole = null;
    private String authenticatedUsername = null;

    public LoginGui(Frame owner) {
        super(owner, "Login Sistem", true);
        userDAO = new UserDAO();

        setSize(350, 260); // Diperbesar sedikit agar muat tombol baru
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel title = new JLabel("Silakan Login", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Form (Center)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        userField = new JTextField(15);
        formPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(15);
        formPanel.add(passField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons (Bottom)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Daftar Baru"); // Tombol Register
        JButton cancelBtn = new JButton("Batal");
        
        // Style buttons
        loginBtn.setBackground(new Color(50, 150, 50)); // Hijau
        loginBtn.setForeground(Color.WHITE);
        
        loginBtn.setPreferredSize(new Dimension(80, 30));
        registerBtn.setPreferredSize(new Dimension(110, 30));
        cancelBtn.setPreferredSize(new Dimension(70, 30));

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn); // Masukkan tombol register
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Actions
        loginBtn.addActionListener(e -> handleLogin());
        
        // Logika Tombol Register: Buka RegisterGui
        registerBtn.addActionListener(e -> {
            RegisterGui registerGui = new RegisterGui(null);
            registerGui.setVisible(true);
        });

        cancelBtn.addActionListener(e -> dispose());
        
        // Biar tekan Enter langsung login
        getRootPane().setDefaultButton(loginBtn);
    }

    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        try {
            String[] authResult = userDAO.authenticate(user, pass);
            if (authResult != null) {
                this.authenticatedRole = authResult[0];
                this.authenticatedUsername = authResult[1];
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    public String getAuthenticatedRole() {
        return authenticatedRole;
    }

    public String getAuthenticatedUsername() {
        return authenticatedUsername;
    }
}