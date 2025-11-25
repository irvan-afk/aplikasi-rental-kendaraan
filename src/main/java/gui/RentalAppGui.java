package main.java.gui;

import main.java.dao.VehicleDAO;
import main.java.service.AdminService;
import main.java.vehicle.Vehicle;
import main.java.vehicle.factory.CarFactory;
import main.java.vehicle.factory.MotorcycleFactory;
import main.java.vehicle.factory.TruckFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RentalAppGui extends JFrame {

    private AdminService adminService;
    private JTextArea displayArea;

    public RentalAppGui() {
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
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        JButton viewButton = new JButton("Lihat Kendaraan");
        JButton rentButton = new JButton("Tambah Kendaraan");
        JButton returnButton = new JButton("Hapus Kendaraan");

        buttonPanel.add(viewButton);
        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);

        // Display area
        displayArea = new JTextArea("Selamat datang di Sistem Rental Kendaraan!\n");
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
    }

    // LIST VEHICLES FROM DB
    private void viewVehicles() {
        try {
            List<Vehicle> list = adminService.listVehicles();
            displayArea.setText("--- Daftar Kendaraan ---\n");
            if (list.isEmpty()) {
                displayArea.append("Belum ada kendaraan.\n");
            } else {
                list.forEach(v -> displayArea.append(v.toString() + "\n"));
            }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RentalAppGui().setVisible(true));
    }
}
