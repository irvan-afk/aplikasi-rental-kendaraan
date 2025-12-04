package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import app.Main;
import dao.VehicleDAO;
import service.AdminService;
import vehicle.Vehicle;
import vehicle.factory.CarFactory;
import vehicle.factory.MotorcycleFactory;
import vehicle.factory.TruckFactory;

public class AdminAppGui extends JFrame {

    private static final String FONT_NAME = "SansSerif";
    private static final String ERROR_PREFIX = "Error: ";
    private transient AdminService adminService;
    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    public AdminAppGui() {
        // Init service & DAO
        VehicleDAO dao = new VehicleDAO();
        adminService = new AdminService(dao);

        setTitle("Sistem Rental - Administrator Mode");
        setSize(900, 600); // Lebar ditambah agar tabel muat
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout Utama
        setLayout(new BorderLayout());

        // 1. HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 100, 150)); // Biru tema
        
        JLabel titleLabel = new JLabel("Dashboard Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // 2. BUTTON PANEL (NAVIGASI)
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        navPanel.setBackground(new Color(230, 230, 230)); // Abu-abu muda
        
        JButton viewButton = createStyledButton("Refresh Data", new Color(100, 100, 100));
        JButton rentButton = createStyledButton("Tambah Baru", new Color(50, 150, 50)); // Hijau
        JButton updateButton = createStyledButton("Edit Data", new Color(200, 140, 0)); // Oranye
        JButton returnButton = createStyledButton("Hapus Data", new Color(200, 50, 50)); // Merah
        JButton logoutButton = createStyledButton("Logout", new Color(50, 100, 150)); // Biru   

        navPanel.add(viewButton);
        navPanel.add(rentButton);
        navPanel.add(updateButton);
        navPanel.add(returnButton);
        navPanel.add(logoutButton); 

        // Gabungkan Header Title dan Navigasi di bagian ATAS (NORTH)
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(navPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // 3. TABLE AREA (CENTER) - PENGGANTI TEXT AREA
        // Nama Kolom Tabel
        String[] columnNames = {"ID", "Plat Nomor", "Tipe", "Merk", "Model", "Harga Dasar", "Status"};
        
        // Model tabel (non-editable cell)
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Agar sel tidak bisa diedit langsung (harus via tombol edit)
            }
        };

        vehicleTable = new JTable(tableModel);
        vehicleTable.setFillsViewportHeight(true);
        vehicleTable.setRowHeight(25); // Tinggi baris agar tidak rapat
        vehicleTable.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Hanya bisa pilih 1 baris

        // Styling Header Tabel
        JTableHeader header = vehicleTable.getTableHeader();
        header.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        header.setBackground(new Color(220, 220, 220));
        header.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Inventaris Kendaraan"));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20)); // Margin kiri-kanan
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);

        // Load data awal
        viewVehicles();

        // ===== EVENT HANDLERS =====
        viewButton.addActionListener(e -> viewVehicles());
        rentButton.addActionListener(e -> addVehicleDialog());
        returnButton.addActionListener(e -> deleteVehicleDialog());
        updateButton.addActionListener(e -> updateVehicleDialog());
        logoutButton.addActionListener(e -> {
            this.dispose();
            Main.showLoginWorkflow();
        });
    }

    // Helper untuk styling tombol
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(FONT_NAME, Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }

    // --- LOGIC METHOD ---

    private void viewVehicles() {
        try {
            // Bersihkan data lama di tabel
            tableModel.setRowCount(0);

            List<Vehicle> list = adminService.listVehicles();
            for (Vehicle v : list) {
                String status = v.isAvailable() ? "Tersedia" : "Disewa";
                // Masukkan data baris per baris
                Object[] rowData = {
                    v.getId(),
                    v.getPlateNumber(),
                    v.getType(),
                    v.getBrand(),
                    v.getModel(),
                    String.format("Rp %,.0f", v.getBasePrice()), // Format Rupiah
                    status
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void addVehicleDialog() {
        // Panel input kustom untuk JDialog
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 kolom
        
        String[] options = {"Car", "Motorcycle", "Truck"};
        JComboBox<String> typeCombo = new JComboBox<>(options);
        
        JTextField plateField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField priceField = new JTextField();

        panel.add(new JLabel("Tipe Kendaraan:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Nomor Plat:"));
        panel.add(plateField);
        panel.add(new JLabel("Merk:"));
        panel.add(brandField);
        panel.add(new JLabel("Model:"));
        panel.add(modelField);
        panel.add(new JLabel("Harga Dasar (Per Hari):"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Kendaraan Baru",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String type = (String) typeCombo.getSelectedItem();
                String plate = plateField.getText();
                String brand = brandField.getText();
                String model = modelField.getText();
                double price = Double.parseDouble(priceField.getText());

                if(plate.isEmpty() || brand.isEmpty() || model.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Semua data wajib diisi!");
                    return;
                }

                switch (type) {
                    case "Car": adminService.addVehicle(new CarFactory(), plate, brand, model, price); break;
                    case "Motorcycle": adminService.addVehicle(new MotorcycleFactory(), plate, brand, model, price); break;
                    case "Truck": adminService.addVehicle(new TruckFactory(), plate, brand, model, price); break;
                    default:
                        throw new IllegalArgumentException("Unknown vehicle type: " + type);
                }
                viewVehicles();
                JOptionPane.showMessageDialog(this, "Berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ERROR_PREFIX + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteVehicleDialog() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris kendaraan yang ingin dihapus terlebih dahulu!");
            return;
        }

        // Ambil ID dari kolom ke-0 (ID)
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String plate = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus kendaraan plat " + plate + "?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminService.deleteVehicle(id);
                viewVehicles();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ERROR_PREFIX + ex.getMessage());
            }
        }
    }

    private void updateVehicleDialog() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris kendaraan yang ingin diedit terlebih dahulu!");
            return;
        }

        // Ambil ID dari tabel
        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            Vehicle v = adminService.findById(id);
            if (v == null) return;

            // Panel input dengan data lama terisi
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            JTextField plateField = new JTextField(v.getPlateNumber());
            JTextField brandField = new JTextField(v.getBrand());
            JTextField modelField = new JTextField(v.getModel());
            JTextField priceField = new JTextField(String.format("%.0f", v.getBasePrice())); // Format angka polos

            panel.add(new JLabel("Nomor Plat:")); panel.add(plateField);
            panel.add(new JLabel("Merk:")); panel.add(brandField);
            panel.add(new JLabel("Model:")); panel.add(modelField);
            panel.add(new JLabel("Harga Dasar:")); panel.add(priceField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Data Kendaraan",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                v.setPlateNumber(plateField.getText());
                v.setBrand(brandField.getText());
                v.setModel(modelField.getText());
                v.setBasePrice(Double.parseDouble(priceField.getText()));
                
                adminService.updateVehicle(v);
                viewVehicles();
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus angka!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ERROR_PREFIX + ex.getMessage());
        }
    }
}