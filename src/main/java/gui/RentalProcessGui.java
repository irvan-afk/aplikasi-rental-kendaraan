package gui;

import dao.VehicleDAO;
import rental.Invoice;
import pricing.DailyPricing;
import pricing.HourlyPricing;
import pricing.MonthlyPricing;
import pricing.PricingStrategy;
import rental.PaymentService;
import rental.RentalServiceFacade;
import vehicle.Vehicle;

import javax.swing.*;
import java.awt.*;

public class RentalProcessGui extends JDialog {

    private final RentalServiceFacade facade;
    private final Vehicle selectedVehicle;
    private final String customerName;

    private JComboBox<String> durationTypeCombo;
    private JTextField durationField;
    private JLabel priceLabel;
    private JComboBox<PaymentService.PaymentMethod> paymentMethodCombo;
    private JTextArea summaryArea;
    private JButton calculateButton;
    private JButton processButton;

    private double calculatedPrice = 0;

    public RentalProcessGui(Frame owner, Vehicle vehicle, String customerName) {
        super(owner, "Proses Rental - " + vehicle.getPlateNumber(), true);

        this.selectedVehicle = vehicle;
        this.customerName = customerName;
        this.facade = new RentalServiceFacade(new VehicleDAO());

        initComponents();
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createVehicleInfoPanel());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createRentalDetailsPanel());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createPaymentPanel());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createSummaryPanel());

        JPanel buttonPanel = createButtonPanel();

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createVehicleInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Informasi Kendaraan"));

        panel.add(new JLabel("Tipe:"));
        panel.add(new JLabel(selectedVehicle.getType()));

        panel.add(new JLabel("Plat Nomor:"));
        panel.add(new JLabel(selectedVehicle.getPlateNumber()));

        panel.add(new JLabel("Merk/Model:"));
        panel.add(new JLabel(selectedVehicle.getBrand() + " " + selectedVehicle.getModel()));

        panel.add(new JLabel("Harga Dasar:"));
        panel.add(new JLabel(String.format("Rp %,.2f / hari", selectedVehicle.getBasePrice())));

        return panel;
    }

    private JPanel createRentalDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Detail Rental"));

        panel.add(new JLabel("Nama Customer:"));
        panel.add(new JLabel(customerName));

        panel.add(new JLabel("Tipe Sewa:"));
        String[] types = {"Per Jam", "Harian", "Bulanan"};
        durationTypeCombo = new JComboBox<>(types);
        durationTypeCombo.addActionListener(e -> resetCalculation());
        panel.add(durationTypeCombo);

        panel.add(new JLabel("Durasi:"));
        durationField = new JTextField();
        durationField.addActionListener(e -> calculatePrice());
        panel.add(durationField);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Pembayaran"));

        panel.add(new JLabel("Metode Pembayaran:"));
        paymentMethodCombo = new JComboBox<>(PaymentService.PaymentMethod.values());
        panel.add(paymentMethodCombo);

        panel.add(new JLabel("Total Harga:"));
        priceLabel = new JLabel("Rp 0");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 128, 0));
        panel.add(priceLabel);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ringkasan"));

        summaryArea = new JTextArea(8, 40);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        summaryArea.setText("Silakan isi detail rental dan klik 'Hitung Harga'");

        panel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        calculateButton = new JButton("Hitung Harga");
        calculateButton.addActionListener(e -> calculatePrice());

        processButton = new JButton("Proses Rental");
        processButton.setEnabled(false);
        processButton.addActionListener(e -> processRental());

        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dispose());

        panel.add(calculateButton);
        panel.add(processButton);
        panel.add(cancelButton);

        return panel;
    }

    private PricingStrategy getSelectedStrategy() {
        String type = (String) durationTypeCombo.getSelectedItem();
        switch (type) {
            case "Per Jam":
                return new HourlyPricing();
            case "Bulanan":
                return new MonthlyPricing();
            default:
                return new DailyPricing();
        }
    }

    private void calculatePrice() {
        try {
            String durationText = durationField.getText().trim();
            if (durationText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Durasi tidak boleh kosong!",
                        "Validasi Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int duration = Integer.parseInt(durationText);
            PricingStrategy strategy = getSelectedStrategy();

            calculatedPrice = facade.calculateEstPrice(selectedVehicle, strategy, duration);

            priceLabel.setText(String.format("Rp %,.2f", calculatedPrice));
            updateSummary(strategy, duration);
            processButton.setEnabled(true);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Durasi harus berupa angka!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSummary(PricingStrategy strategy, int duration) {

        String text =
                "RINGKASAN RENTAL\n\n" +
                        "Kendaraan   : " + selectedVehicle.getType() + "\n" +
                        "Plat Nomor  : " + selectedVehicle.getPlateNumber() + "\n" +
                        "Merk/Model  : " + selectedVehicle.getBrand() + " " + selectedVehicle.getModel() + "\n\n" +
                        "Customer    : " + customerName + "\n" +
                        "Durasi      : " + duration + " " + strategy.getUnitName() + "\n" +
                        "Harga/" + strategy.getUnitName() + " : Rp " + String.format("%,.2f", selectedVehicle.getBasePrice()) + "\n\n" +
                        "TOTAL       : Rp " + String.format("%,.2f", calculatedPrice);

        summaryArea.setText(text);
    }

    private void processRental() {
        if (calculatedPrice <= 0) {
            JOptionPane.showMessageDialog(this, "Silakan hitung harga terlebih dahulu!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                String.format("Konfirmasi rental dengan total Rp %,.2f?", calculatedPrice),
                "Konfirmasi Rental",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            PricingStrategy strategy = getSelectedStrategy();
            PaymentService.PaymentMethod method = (PaymentService.PaymentMethod) paymentMethodCombo.getSelectedItem();

            Invoice invoice = facade.processCompleteBooking(
                    selectedVehicle,
                    strategy,
                    duration,
                    customerName,
                    method
            );

            showInvoiceGUI(invoice);

            JOptionPane.showMessageDialog(this, "Rental berhasil diproses!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memproses rental:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showInvoiceGUI(Invoice invoice) {
        JDialog dialog = new JDialog(this, "Invoice", true);
        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(this);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("INVOICE RENTAL");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(title);
        main.add(Box.createVerticalStrut(10));
        main.add(makeDivider());

        main.add(makeRow("No. Invoice", invoice.getInvoiceNumber()));
        main.add(makeRow("Tanggal", new java.util.Date(invoice.getTimestamp()).toString()));
        main.add(Box.createVerticalStrut(10));

        main.add(makeSection("Kendaraan"));
        main.add(makeRow("Jenis", invoice.getVehicle().getType()));
        main.add(makeRow("Plat", invoice.getVehicle().getPlateNumber()));
        main.add(makeRow("Merk/Model", invoice.getVehicle().getBrand() + " " + invoice.getVehicle().getModel()));
        main.add(Box.createVerticalStrut(10));

        main.add(makeSection("Detail Rental"));
        main.add(makeRow("Durasi", invoice.getDuration() + " " + invoice.getDurationType()));
        main.add(makeRow("Harga/" + invoice.getDurationType(), "Rp " + invoice.getBasePrice()));
        main.add(makeRow("Total", "Rp " + invoice.getTotalPrice()));
        main.add(Box.createVerticalStrut(10));

        main.add(makeSection("Pembayaran"));
        main.add(makeRow("Metode", invoice.getReceipt().getMethod().getDisplayName()));
        main.add(makeRow("Transaction ID", invoice.getReceipt().getTransactionId()));
        main.add(Box.createVerticalStrut(15));

        JButton close = new JButton("Tutup");
        close.setAlignmentX(Component.CENTER_ALIGNMENT);
        close.addActionListener(e -> dialog.dispose());
        main.add(close);

        dialog.add(new JScrollPane(main));
        dialog.setVisible(true);
    }

    private JPanel makeRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel left = new JLabel(label + ": ");
        JLabel right = new JLabel(value);

        left.setFont(new Font("Arial", Font.PLAIN, 14));
        right.setFont(new Font("Arial", Font.BOLD, 14));

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);

        return p;
    }

    private JLabel makeSection(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private void resetCalculation() {
        calculatedPrice = 0;
        priceLabel.setText("Rp 0");
        processButton.setEnabled(false);
        summaryArea.setText("Silakan isi detail rental dan klik 'Hitung Harga'");
    }
}
