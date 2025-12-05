package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import app.Main;
import dao.VehicleDAO;
import rental.RentalServiceFacade;
import vehicle.Vehicle;

public class CustomerDashboardGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String FONT_NAME = "SansSerif";
    private static final Logger LOGGER = Logger.getLogger(CustomerDashboardGui.class.getName());
    
    private final String customerName;
    
    // Field yang memang harus disimpan untuk state aplikasi
    private transient DefaultTableModel tableModel;
    private transient VehicleDAO vehicleDAO;
    private transient RentalServiceFacade facade;
    private transient List<Vehicle> currentVehicleList;
    
    // HAPUS rentButton dari sini (sudah jadi lokal)

    public CustomerDashboardGui(String customerName) {
        this.customerName = customerName;
        this.vehicleDAO = new VehicleDAO();
        this.facade = new RentalServiceFacade(this.vehicleDAO);

        setTitle("Selamat Datang, " + customerName + "!");
        setSize(1100, 600);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadAvailableVehicles();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 100, 150));
        JLabel titleLabel = new JLabel("Daftar Kendaraan Tersedia", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // --- BUTTON PANEL ---
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        navPanel.setBackground(new Color(230, 230, 230));
        
        // DEKLARASI LOKAL rentButton
        JButton rentButton = createStyledButton("Sewa Sekarang", new Color(50, 150, 50));
        rentButton.setEnabled(false); // Default disabled

        JButton myRentalsButton = createStyledButton("Rental Saya", new Color(200, 140, 0));
        JButton logoutButton = createStyledButton("Logout", new Color(100, 100, 100));
        
        navPanel.add(rentButton);
        navPanel.add(myRentalsButton);
        navPanel.add(logoutButton);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(navPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // --- TABLE ---
        String[] columnNames = {"Tipe", "Merk", "Model", "Harga / Hari"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable vehicleTable = new JTable(tableModel);
        vehicleTable.setFillsViewportHeight(true);
        vehicleTable.setRowHeight(25);
        vehicleTable.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader tableHeader = vehicleTable.getTableHeader();
        tableHeader.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // --- EVENT HANDLERS ---
        
        // Logika Enable/Disable Button
        vehicleTable.getSelectionModel().addListSelectionListener(e -> 
            // Karena rentButton adalah variabel lokal yang "effectively final",
            // kita bisa mengaksesnya di dalam lambda ini. Aman.
            rentButton.setEnabled(vehicleTable.getSelectedRow() != -1)
        );

        logoutButton.addActionListener(e -> {
            this.dispose();
            Main.showLoginWorkflow();
        });

        myRentalsButton.addActionListener(e -> 
            new MyRentalsGui(this, customerName, facade).setVisible(true)
        );

        rentButton.addActionListener(e -> {
            int selectedRow = vehicleTable.getSelectedRow();
            if (selectedRow != -1) {
                Vehicle selectedVehicle = currentVehicleList.get(selectedRow);
                new RentalProcessGui(this, selectedVehicle, customerName, facade).setVisible(true);
                loadAvailableVehicles();
            }
        });
    }

    private void loadAvailableVehicles() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tableModel.setRowCount(0);

        SwingWorker<List<Vehicle>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Vehicle> doInBackground() throws Exception {
                List<Vehicle> allVehicles = vehicleDAO.getAll();
                allVehicles.removeIf(v -> !v.isAvailable());
                return allVehicles;
            }

            @Override
            protected void done() {
                try {
                    currentVehicleList = get();
                    for (Vehicle v : currentVehicleList) {
                        Object[] rowData = {
                                v.getType(),
                                v.getBrand(),
                                v.getModel(),
                                String.format("Rp %,.0f", v.getBasePrice())
                        };
                        tableModel.addRow(rowData);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "Load vehicles interrupted", e);
                    JOptionPane.showMessageDialog(CustomerDashboardGui.this,
                            "Proses memuat data dibatalkan.",
                            "Dibatalkan", JOptionPane.WARNING_MESSAGE);
                } catch (ExecutionException e) {
                    LOGGER.log(Level.SEVERE, "Error loading vehicles", e);
                    JOptionPane.showMessageDialog(CustomerDashboardGui.this,
                            "Gagal memuat data kendaraan: " + e.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(FONT_NAME, Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }
}