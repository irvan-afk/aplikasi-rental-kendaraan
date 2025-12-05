package gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import rental.Rental;
import service.AdminService;

public class RentalInfoGui extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final String FONT_SANS_SERIF = "SansSerif";
    private static final Logger LOGGER = Logger.getLogger(RentalInfoGui.class.getName());

    private final transient AdminService adminService;
    
    // HAPUS: private JTable rentalTable; (Tidak perlu jadi field)
    
    // TETAP: tableModel harus tetap field agar bisa diakses di loadActiveRentals
    private DefaultTableModel tableModel;

    public RentalInfoGui(Frame owner, AdminService adminService) {
        super(owner, "Informasi Kendaraan yang Dirental", true);
        this.adminService = adminService;

        initComponents();
        loadActiveRentals();

        setSize(900, 500);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("Kendaraan yang Sedang Dirental", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_SANS_SERIF, Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table Model Setup
        String[] columnNames = {"Plat Nomor", "Merk", "Model", "Dirental Oleh", "Tgl Mulai", "Tgl Selesai", "Total Biaya"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // PERBAIKAN DISINI: Jadikan JTable sebagai LOCAL VARIABLE
        // Cukup tambahkan 'JTable' di depan nama variabelnya
        JTable rentalTable = new JTable(tableModel);
        
        rentalTable.setFillsViewportHeight(true);
        rentalTable.setRowHeight(25);
        rentalTable.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, 14));

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
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "Load rentals interrupted", e);
                    JOptionPane.showMessageDialog(RentalInfoGui.this,
                            "Proses memuat data dibatalkan.",
                            "Dibatalkan", JOptionPane.WARNING_MESSAGE);
                } catch (ExecutionException e) {
                    LOGGER.log(Level.SEVERE, "Error loading active rentals", e);
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