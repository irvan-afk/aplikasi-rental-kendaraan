package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dao.VehicleDAO;
import pricing.DailyPricing;
import pricing.HourlyPricing;
import pricing.MonthlyPricing;
import pricing.PricingStrategy;
import rental.RentalServiceFacade;
import gui.RentalProcessGui;
import vehicle.Vehicle;

public class CustomerAppGui extends JFrame {
    private RentalServiceFacade facade;
    private VehicleDAO dao;
    private JComboBox<Vehicle> vehicleCombo;
    private JComboBox<String> durationTypeCombo;
    private JTextField durationField;
    private JLabel priceLabel;
    private String currentUserRole = null;

    public CustomerAppGui() {
        dao = new VehicleDAO();
        facade = new RentalServiceFacade(dao);

        setTitle("Rental Kendaraan - Menu Pelanggan");
        setSize(600, 450); // Ukuran sedikit diperbesar agar lega
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Layout Utama: BorderLayout
        setLayout(new BorderLayout(10, 10));

        // 1. HEADER PANEL
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(50, 100, 150)); // Biru tua
        JLabel titleLabel = new JLabel("Form Sewa Kendaraan");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 2. FORM PANEL (CENTER) - Menggunakan GridBagLayout untuk kerapian
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40)); // Margin kiri-kanan
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Jarak antar elemen
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Baris 1: Pilih Kendaraan
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Pilih Kendaraan:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        vehicleCombo = new JComboBox<>();
        loadAvailableVehicles();
        formPanel.add(vehicleCombo, gbc);

        // Baris 2: Tipe Sewa
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Tipe Sewa:"), gbc);

        gbc.gridx = 1;
        String[] types = {"Per Jam", "Harian", "Bulanan"};
        durationTypeCombo = new JComboBox<>(types);
        formPanel.add(durationTypeCombo, gbc);

        // Baris 3: Durasi
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Durasi (Angka):"), gbc);

        gbc.gridx = 1;
        durationField = new JTextField();
        formPanel.add(durationField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 3. FOOTER PANEL (Harga & Tombol)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        footerPanel.setBackground(new Color(240, 240, 240));

        // Panel Harga (Kiri Bawah)
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.setOpaque(false);
        priceLabel = new JLabel("Total: Rp 0");
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        priceLabel.setForeground(new Color(0, 120, 0)); // Hijau uang
        pricePanel.add(priceLabel);

        // Panel Tombol (Kanan Bawah)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton checkPriceBtn = new JButton("Cek Harga");
        JButton bookBtn = new JButton("Sewa Sekarang");
        bookBtn.setBackground(new Color(50, 150, 50)); // Tombol hijau
        bookBtn.setForeground(Color.WHITE);
        
        // Tombol Login/Register di pojok atas (opsional) atau bawah
        JButton loginBtn = new JButton("Login / Ganti Akun");

        buttonPanel.add(loginBtn);
        buttonPanel.add(checkPriceBtn);
        buttonPanel.add(bookBtn);

        footerPanel.add(pricePanel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST); // Tombol di kanan

        add(footerPanel, BorderLayout.SOUTH);

        // Events
        checkPriceBtn.addActionListener(e -> calculatePrice());
        bookBtn.addActionListener(e -> processBooking());
        loginBtn.addActionListener(e -> handleLogin());
    }

    // ... (Sisa method logic handleRegister, handleLogin, loadAvailableVehicles, dll SAMA SEPERTI SEBELUMNYA) ...
    // Copy method logic-nya ke sini
    
    private void handleLogin() {
        LoginGui loginDialog = new LoginGui(this);
        loginDialog.setVisible(true);

        String role = loginDialog.getAuthenticatedRole();
        if (role != null) {
            if (role.equalsIgnoreCase("ADMIN")) {
                new AdminAppGui().setVisible(true);
                this.dispose();
            } else {
                this.currentUserRole = role;
                JOptionPane.showMessageDialog(this, "Selamat datang, " + role + "!");
            }
        }
    }

    private void loadAvailableVehicles() {
        try {
            vehicleCombo.removeAllItems();
            List<Vehicle> list = dao.getAll();
            for (Vehicle v : list) {
                if (v.isAvailable()) {
                    vehicleCombo.addItem(v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PricingStrategy getSelectedStrategy() {
        String type = (String) durationTypeCombo.getSelectedItem();
        switch (type) {
            case "Per Jam": return new HourlyPricing();
            case "Harian": return new DailyPricing();
            case "Bulanan": return new MonthlyPricing();
            default: return new DailyPricing();
        }
    }

    private void calculatePrice() {
        try {
            Vehicle v = (Vehicle) vehicleCombo.getSelectedItem();
            if (v == null) return;
            int duration = Integer.parseInt(durationField.getText());
            PricingStrategy strategy = getSelectedStrategy();
            double total = facade.calculateEstPrice(v, strategy, duration);
            priceLabel.setText("Total: Rp " + String.format("%,.0f", total)); // Format angka ribuan
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Durasi harus angka!");
        }
    }

    private void processBooking() {
    if (currentUserRole == null) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Anda belum login. Login sekarang?", "Perhatian", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            handleLogin();
        }
        return;
    }

    try {
        Vehicle v = (Vehicle) vehicleCombo.getSelectedItem();
        if (v == null) return;

        // BUKA HALAMAN PROSES RENTAL
        new RentalProcessGui(this, v, "Customer").setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal membuka proses rental:\n" + e.getMessage());
    }
}
}
