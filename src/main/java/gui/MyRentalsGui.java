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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import rental.Rental;
import rental.RentalServiceFacade;

public class MyRentalsGui extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(MyRentalsGui.class.getName());
    private static final String FONT_SANS_SERIF = "SansSerif";

    private final String customerName;
    
    // FIX: Mark non-serializable fields as transient
    private final transient RentalServiceFacade facade;
    private transient List<Rental> currentRentals;
    
    private JTable rentalTable;
    private DefaultTableModel tableModel;
    private JButton returnButton;

    public MyRentalsGui(Frame owner, String customerName, RentalServiceFacade facade) {
        super(owner, "Riwayat Rental - " + customerName, true);
        this.customerName = customerName;
        this.facade = facade;

        initComponents();
        loadRentals();

        setSize(900, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("Riwayat Rental Anda", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_SANS_SERIF, Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Kendaraan", "Plat Nomor", "Tgl Mulai", "Tgl Selesai", "Total Biaya", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        rentalTable = new JTable(tableModel);
        rentalTable.setFillsViewportHeight(true);
        rentalTable.setRowHeight(25);
        rentalTable.setFont(new Font(FONT_SANS_SERIF, Font.PLAIN, 14));
        rentalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rentalTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateReturnButtonState();
            }
        });

        JScrollPane scrollPane = new JScrollPane(rentalTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        returnButton = new JButton("Kembalikan Kendaraan");
        returnButton.setEnabled(false);
        returnButton.addActionListener(e -> processReturn());
        
        JButton closeButton = new JButton("Tutup");
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(returnButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRentals() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        returnButton.setEnabled(false);
        tableModel.setRowCount(0);

        SwingWorker<List<Rental>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Rental> doInBackground() throws Exception {
                return facade.getRentalsForCustomer(customerName);
            }

            @Override
            protected void done() {
                try {
                    currentRentals = get();
                    if (currentRentals.isEmpty()) {
                        tableModel.addRow(new Object[]{"Belum ada riwayat rental.", "", "", "", "", ""});
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        for (Rental rental : currentRentals) {
                            String status = rental.isVehicleIsAvailable() ? "Selesai" : "Disewa";
                            tableModel.addRow(new Object[]{
                                    rental.getVehicleBrand() + " " + rental.getVehicleModel(),
                                    rental.getVehiclePlate(),
                                    dateFormat.format(rental.getStartDate()),
                                    dateFormat.format(rental.getEndDate()),
                                    String.format("Rp %,.0f", rental.getTotalCost()),
                                    status
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "Load rentals interrupted", e);
                    JOptionPane.showMessageDialog(MyRentalsGui.this,
                            "Proses memuat data dibatalkan.",
                            "Dibatalkan", JOptionPane.WARNING_MESSAGE);
                } catch (ExecutionException e) {
                    LOGGER.log(Level.SEVERE, "Error loading rentals", e);
                    JOptionPane.showMessageDialog(MyRentalsGui.this,
                            "Gagal memuat riwayat rental: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
    
    private void updateReturnButtonState() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow != -1 && currentRentals != null && selectedRow < currentRentals.size()) {
            Rental selectedRental = currentRentals.get(selectedRow);
            returnButton.setEnabled(!selectedRental.isVehicleIsAvailable());
        } else {
            returnButton.setEnabled(false);
        }
    }

    private void processReturn() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) return;

        Rental selectedRental = currentRentals.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Anda yakin ingin mengembalikan kendaraan " + selectedRental.getVehiclePlate() + "?",
                "Konfirmasi Pengembalian",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        returnButton.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                facade.returnVehicle(selectedRental.getId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(MyRentalsGui.this,
                            "Kendaraan berhasil dikembalikan.",
                            "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadRentals();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "Return process interrupted", e);
                     JOptionPane.showMessageDialog(MyRentalsGui.this,
                            "Proses pengembalian dibatalkan.",
                            "Dibatalkan", JOptionPane.WARNING_MESSAGE);
                } catch (ExecutionException e) {
                    LOGGER.log(Level.SEVERE, "Error returning vehicle", e);
                    JOptionPane.showMessageDialog(MyRentalsGui.this,
                            "Gagal mengembalikan kendaraan: " + e.getCause().getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
}