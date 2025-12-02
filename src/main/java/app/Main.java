package app;

import javax.swing.SwingUtilities;

import gui.LoginGui;

public class Main {
    public static void main(String[] args) {
        // Menjalankan aplikasi dalam Event Dispatch Thread (standar Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                // Membuka jendela Login sebagai pintu gerbang aplikasi
                LoginGui loginGui = new LoginGui();
                loginGui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Gagal menjalankan aplikasi: " + e.getMessage());
            }
        });
    }
}