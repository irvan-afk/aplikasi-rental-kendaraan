package app;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.AdminAppGui;
import gui.CustomerDashboardGui;
import gui.LoginGui;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showLoginWorkflow);
    }

    public static void showLoginWorkflow() {
        try {
            // 1. SET TEMA AGAR LEBIH MODERN (NIMBUS)
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            
        }

        try {
            // 2. BUKA LOGIN
            LoginGui loginGui = new LoginGui(null);
            loginGui.setVisible(true); 
            // Program akan berhenti di sini sampai LoginGui ditutup (dispose)

            // 3. CEK HASIL LOGIN (SETELAH DIALOG DITUTUP)
            String role = loginGui.getAuthenticatedRole();
            String username = loginGui.getAuthenticatedUsername();

            if (role != null) {
                // Jika login berhasil (role tidak null), buka GUI yang sesuai
                if (role.equalsIgnoreCase("ADMIN")) {
                    new AdminAppGui().setVisible(true);
                } else if (role.equalsIgnoreCase("CUSTOMER")) {
                    new CustomerDashboardGui(username).setVisible(true);
                }
            } else {
                // Jika user menutup login tanpa berhasil masuk (klik silang/cancel)
                // Aplikasi akan berhenti normal karena tidak ada frame lain yang terbuka
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan fatal: " + e.getMessage());
        }
    }
}