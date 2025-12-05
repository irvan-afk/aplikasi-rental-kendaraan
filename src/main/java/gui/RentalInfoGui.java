package gui;

import rental.Rental;
import service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RentalInfoGui extends JDialog {

    private final AdminService adminService;
    private JTable rentalTable;
    private DefaultTableModel tableModel;

    public RentalInfoGui(Frame owner, AdminService adminService) {
        super(owner, "Informasi Kendaraan yang Dirental", true);
        this.adminService = adminService;

        initComponents();
        loadActiveRentals();

        setSize(800, 500);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("Kendaraan yang Sedang Dirental", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Plat Nomor", "Merk", "Model", "Dirental Oleh", "Tgl Mulai", "Tgl Selesai", "Total Biaya"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rentalTable = new JTable(tableModel);
        rentalTable.setFillsViewportHeight(true);
        rentalTable.setRowHeight(25);
        rentalTable.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(rentalTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadActiveRentals());

        JButton closeButton = new JButton("Tutup");
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadActiveRentals() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tableModel.setRowCount(0);

        SwingWorker<List<Rental>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Rental> doInBackground() throws Exception {
                return adminService.listActiveRentals();
            }

            @Override
            protected void done() {
                try {
                    List<Rental> rentals = get();
                    if (rentals.isEmpty()) {
                        tableModel.addRow(new Object[]{"Tidak ada kendaraan yang sedang dirental.", "", "", "", "", "", ""});
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        for (Rental rental : rentals) {
                            tableModel.addRow(new Object[]{
                                    rental.getVehiclePlate(),
                                    rental.getVehicleBrand(),
                                    rental.getVehicleModel(),
                                    rental.getCustomerName(),
                                    dateFormat.format(rental.getStartDate()),
                                    dateFormat.format(rental.getEndDate()),
                                    String.format("Rp %,.0f", rental.getTotalCost())
                            });
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(RentalInfoGui.this,
                            "Gagal memuat data rental: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
}
