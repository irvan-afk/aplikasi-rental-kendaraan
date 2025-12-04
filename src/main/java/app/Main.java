package app;

import javax.swing.SwingUtilities;

import gui.CustomerAppGui;

public class Main {
    public static void main(String[] args) {
        // Menjalankan aplikasi dalam Event Dispatch Thread (standar Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                // Membuka jendela Login sebagai pintu gerbang aplikasi
                CustomerAppGui customerAppGui = new CustomerAppGui();
                customerAppGui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Gagal menjalankan aplikasi: " + e.getMessage());
            }
        });
    }
}