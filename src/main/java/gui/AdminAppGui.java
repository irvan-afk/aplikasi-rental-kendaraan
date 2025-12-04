package gui;

import dao.VehicleDAO;
import service.AdminService;
import vehicle.Vehicle;
import vehicle.factory.CarFactory;
import vehicle.factory.MotorcycleFactory;
import vehicle.factory.TruckFactory;

// import java.awt.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AdminAppGui extends JFrame {

    private AdminService adminService;
    private JTextArea displayArea;

    public AdminAppGui() {
        // Init service & DAO
        VehicleDAO dao = new VehicleDAO();
        adminService = new AdminService(dao);

        setTitle("Sistem Rental Kendaraan");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0)); // Changed to 1x5
        JButton viewButton = new JButton("Lihat Kendaraan");
        JButton rentButton = new JButton("Tambah Kendaraan");
        JButton updateButton = new JButton("Update Kendaraan");
        JButton returnButton = new JButton("Hapus Kendaraan");
        JButton customerViewButton = new JButton("Halaman Customer"); // New button

        buttonPanel.add(viewButton);
        buttonPanel.add(rentButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(customerViewButton); // Add new button to panel

        // Display area
        displayArea = new JTextArea("Selamat datang di Sistem Rental Kendaraan!\n");
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // ===== EVENT HANDLERS =====

        // 1) LIHAT KENDARAAN
        viewButton.addActionListener(e -> viewVehicles());

        // 2) TAMBAH KENDARAAN
        rentButton.addActionListener(e -> addVehicleDialog());

        // 3) HAPUS KENDARAAN
        returnButton.addActionListener(e -> deleteVehicleDialog());
        
        // 4) UPDATE KENDARAAN
        updateButton.addActionListener(e -> updateVehicleDialog());

        // 5) GO TO CUSTOMER VIEW
        customerViewButton.addActionListener(e -> {
            new CustomerAppGui().setVisible(true);
            this.dispose();
        });
    }

    // LIST VEHICLES FROM DB
    private void viewVehicles() {
        try {
            List<Vehicle> list = adminService.listVehicles();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-5s | %-15s | %-12s | %-20s | %-10s | %s\n", "ID", "PLAT NOMOR", "TIPE", "MERK & MODEL", "HARGA", "TERSEDIA"));
            sb.append("----------------------------------------------------------------------------------------------------\n");

            if (list.isEmpty()) {
                sb.append("Belum ada kendaraan.\n");
            } else {
                for (Vehicle v : list) {
                    String brandModel = v.getBrand() + " " + v.getModel();
                    sb.append(String.format("%-5d | %-15s | %-12s | %-20s | Rp %-7.0f | %s\n",
                            v.getId(),
                            v.getPlateNumber(),
                            v.getType(),
                            brandModel,
                            v.getBasePrice(),
                            v.isAvailable() ? "Ya" : "Tidak"));
                }
            }
            displayArea.setText(sb.toString());
        } catch (Exception ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    // ADD VEHICLE (GUI DIALOG)
    private void addVehicleDialog() {
        String[] options = {"Car", "Motorcycle", "Truck"};
        String type = (String) JOptionPane.showInputDialog(
                this,
                "Pilih jenis kendaraan:",
                "Tambah Kendaraan",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (type == null) return;

        String plate = JOptionPane.showInputDialog(this, "Nomor Plat:");
        String brand = JOptionPane.showInputDialog(this, "Merk:");
        String model = JOptionPane.showInputDialog(this, "Model:");
        String priceStr = JOptionPane.showInputDialog(this, "Harga dasar per hari:");

        if (plate == null || brand == null || model == null || priceStr == null) return;

        try {
            double basePrice = Double.parseDouble(priceStr);

            // pilih factory
            switch (type) {
                case "Car":
                    adminService.addVehicle(new CarFactory(), plate, brand, model, basePrice);
                    break;
                case "Motorcycle":
                    adminService.addVehicle(new MotorcycleFactory(), plate, brand, model, basePrice);
                    break;
                case "Truck":
                    adminService.addVehicle(new TruckFactory(), plate, brand, model, basePrice);
                    break;
            }

            displayArea.append("Kendaraan berhasil ditambahkan!\n");
            viewVehicles();

        } catch (Exception ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    private void deleteVehicleDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Masukkan ID kendaraan yang akan dihapus:");

        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            adminService.deleteVehicle(id);
            displayArea.append("Kendaraan ID " + id + " berhasil dihapus!\n");
            viewVehicles();
        } catch (Exception ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }

    private void updateVehicleDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Masukkan ID kendaraan yang akan diupdate:");

        if (idStr == null) return;

        try {
            int id = Integer.parseInt(idStr);
            Vehicle vehicle = adminService.findById(id);

            if (vehicle == null) {
                JOptionPane.showMessageDialog(this, "Kendaraan dengan ID " + id + " tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String plate = (String) JOptionPane.showInputDialog(this, "Nomor Plat:", "Update Kendaraan", JOptionPane.PLAIN_MESSAGE, null, null, vehicle.getPlateNumber());
            String brand = (String) JOptionPane.showInputDialog(this, "Merk:", "Update Kendaraan", JOptionPane.PLAIN_MESSAGE, null, null, vehicle.getBrand());
            String model = (String) JOptionPane.showInputDialog(this, "Model:", "Update Kendaraan", JOptionPane.PLAIN_MESSAGE, null, null, vehicle.getModel());
            String priceStr = (String) JOptionPane.showInputDialog(this, "Harga dasar per hari:", "Update Kendaraan", JOptionPane.PLAIN_MESSAGE, null, null, vehicle.getBasePrice());

            if (plate == null || brand == null || model == null || priceStr == null) return;

            double basePrice = Double.parseDouble(priceStr);

            vehicle.setPlateNumber(plate);
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setBasePrice(basePrice);

            adminService.updateVehicle(vehicle);

            displayArea.append("Kendaraan ID " + id + " berhasil diupdate!\n");
            viewVehicles();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            displayArea.setText("Error: " + ex.getMessage());
        }
    }
}