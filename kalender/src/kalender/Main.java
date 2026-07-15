package kalender;

import kalender.auth.AuthManager;
import kalender.ui.CalendarApp;
import kalender.ui.LoginDialog;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Entry point aplikasi Kalender Catatan.
 * Alur: inisialisasi DB → tampilkan login → buka kalender jika login berhasil.
 */
public class Main {

    public static void main(String[] args) {
        // Gunakan tampilan sistem
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            // 1. Inisialisasi database (buat tabel jika belum ada)
            try {
                AuthManager.initDatabase();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                    "Gagal menginisialisasi database!\n\n" + e.getMessage() +
                    "\n\nPastikan file sqlite-jdbc-*.jar sudah ditambahkan ke Build Path Eclipse.",
                    "Error Database", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            // 2. Tampilkan layar login
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);

            // 3. Buka aplikasi jika login berhasil
            if (login.isLoginSucceeded()) {
                new CalendarApp();
            } else {
                System.exit(0); // User tutup dialog login
            }
        });
    }
}
