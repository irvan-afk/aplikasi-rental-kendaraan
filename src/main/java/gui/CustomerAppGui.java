package gui;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import dao.VehicleDAO;
import pricing.DailyPricing;
import pricing.HourlyPricing;
import pricing.MonthlyPricing;
import pricing.PricingStrategy;
import rental.RentalServiceFacade;
import vehicle.Vehicle;

public class CustomerAppGui extends JFrame {
    private RentalServiceFacade facade;
    private VehicleDAO dao;
    private JComboBox<Vehicle> vehicleCombo;
    private JComboBox<String> durationTypeCombo;
    private JTextField durationField;
    private JLabel priceLabel;
    private String currentUserRole = null; // Placeholder for logged-in user

    public CustomerAppGui() {
        dao = new VehicleDAO();
        facade = new RentalServiceFacade(dao);

        setTitle("Menu Pelanggan - Rental Kendaraan");
        setSize(500, 350); // Adjusted size for new row
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10)); // Changed layout to 6x2

        // Komponen
        add(new JLabel("Pilih Kendaraan (Tersedia):"));
        vehicleCombo = new JComboBox<>();
        loadAvailableVehicles();
        add(vehicleCombo);

        add(new JLabel("Tipe Sewa:"));
        String[] types = {"Per Jam", "Harian", "Bulanan"};
        durationTypeCombo = new JComboBox<>(types);
        add(durationTypeCombo);

        add(new JLabel("Durasi (Angka):"));
        durationField = new JTextField();
        add(durationField);

        JButton checkPriceBtn = new JButton("Cek Harga");
        priceLabel = new JLabel("Total: Rp 0");
        add(checkPriceBtn);
        add(priceLabel);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        add(loginBtn);
        add(registerBtn);

        JButton bookBtn = new JButton("Sewa Sekarang");
        add(bookBtn);
        add(new JLabel("")); // spacer

        // Events
        checkPriceBtn.addActionListener(e -> calculatePrice());
        bookBtn.addActionListener(e -> processBooking());
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());
    }

    private void handleRegister() {
        RegisterGui registerDialog = new RegisterGui(this);
        registerDialog.setVisible(true);
    }

    private void handleLogin() {
        LoginGui loginDialog = new LoginGui(this);
        loginDialog.setVisible(true); // This will block until the dialog is closed

        String role = loginDialog.getAuthenticatedRole();
        if (role != null) {
            if (role.equalsIgnoreCase("ADMIN")) {
                // If admin, open admin gui and close this one
                new AdminAppGui().setVisible(true);
                this.dispose();
            } else {
                // If customer, just update role and show message
                this.currentUserRole = role;
                JOptionPane.showMessageDialog(this, "Login berhasil! Role: " + role);
            }
        }
    }

    private void loadAvailableVehicles() {
        try {
            // HAPUS ITEM LAMA DULU AGAR TIDAK DUPLIKAT SAAT REFRESH
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
            case "Bulanan": return new MonthlyPricing(); // Pastikan kelas ini dibuat
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
            priceLabel.setText("Total: Rp " + String.format("%.0f", total));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Durasi harus angka!");
        }
    }

    private void processBooking() {
        if (currentUserRole == null) {
            JOptionPane.showMessageDialog(this, "Anda harus login terlebih dahulu untuk menyewa kendaraan!");
            return;
        }

        try {
            Vehicle v = (Vehicle) vehicleCombo.getSelectedItem();
            if (v == null) return;
            
            int duration = Integer.parseInt(durationField.getText());
            PricingStrategy strategy = getSelectedStrategy();
            
            // Proses booking
            facade.bookVehicle(v, strategy, duration);
            
            JOptionPane.showMessageDialog(this, "Berhasil Menyewa Kendaraan!");
            loadAvailableVehicles(); // refresh list
            vehicleCombo.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal Booking: " + e.getMessage());
        }
    }
}