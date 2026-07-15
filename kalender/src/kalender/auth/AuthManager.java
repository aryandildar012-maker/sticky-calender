package kalender.auth;

import kalender.util.AppConstants;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Mengelola semua operasi autentikasi: register, login, ganti password, lupa password.
 * Menggunakan SQLite (via JDBC) sebagai penyimpanan lokal.
 */
public class AuthManager {

    // User yang sedang login (null jika belum login)
    private static User currentUser = null;
    
    // Menyimpan satu koneksi tunggal agar tidak membuat koneksi baru terus menerus
    private static Connection connection = null;

    // ===================== INISIALISASI DATABASE =====================

    /**
     * Buat tabel jika belum ada. Dipanggil sekali saat aplikasi mulai.
     */
    public static void initDatabase() throws SQLException {
        // Menggunakan getConnection() langsung tanpa try-with-resources untuk koneksi utamanya,
        // melainkan hanya menutup Statement-nya saja agar koneksi tetap hidup selama aplikasi berjalan.
        Connection conn = getConnection(); 
        try (Statement stmt = conn.createStatement()) {
            // Tabel akun pengguna
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  nama_lengkap  TEXT    NOT NULL," +
                "  email         TEXT    NOT NULL UNIQUE," +
                "  password_hash TEXT    NOT NULL," +
                "  security_question TEXT NOT NULL DEFAULT ''," +
                "  security_answer   TEXT NOT NULL DEFAULT ''" +
                ")"
            );
            // Tabel catatan (terhubung ke user via user_id)
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS notes (" +
                "  id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  user_id       INTEGER NOT NULL," +
                "  tanggal       TEXT    NOT NULL," +   // format: yyyy-MM-dd
                "  judul         TEXT    NOT NULL," +
                "  isi           TEXT    NOT NULL DEFAULT ''," +
                "  color_index   INTEGER NOT NULL DEFAULT 0," +
                "  has_reminder  INTEGER NOT NULL DEFAULT 0," +  // 0=false, 1=true
                "  reminder_time TEXT    NOT NULL DEFAULT ''," +
                "  FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")"
            );
        }
    }

    // ===================== REGISTER =====================

    public static String register(String namaLengkap, String email, String password,
                                   String securityQuestion, String securityAnswer) {
        if (namaLengkap == null || namaLengkap.trim().isEmpty()) return "Nama lengkap tidak boleh kosong.";
        if (email == null || !email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$"))
            return "Format email tidak valid.";
        if (password == null || password.length() < 6) return "Password minimal 6 karakter.";
        if (securityQuestion == null || securityQuestion.trim().isEmpty()) return "Pilih pertanyaan keamanan.";
        if (securityAnswer == null || securityAnswer.trim().isEmpty()) return "Jawaban keamanan tidak boleh kosong.";

        try {
            Connection conn = getConnection();
            // Cek email sudah terdaftar
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE email = ?")) {
                ps.setString(1, email.toLowerCase());
                if (ps.executeQuery().next()) return "Email sudah terdaftar.";
            }
            // Insert user baru
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (nama_lengkap, email, password_hash, security_question, security_answer)" +
                    " VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, namaLengkap.trim());
                ps.setString(2, email.toLowerCase().trim());
                ps.setString(3, hash(password));
                ps.setString(4, securityQuestion);
                ps.setString(5, hash(securityAnswer.toLowerCase().trim()));
                ps.executeUpdate();
            }
            return null; // sukses
        } catch (SQLException e) {
            return "Gagal mendaftar: " + e.getMessage();
        }
    }

    // ===================== LOGIN =====================

    public static String login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.isEmpty())
            return "Email dan password tidak boleh kosong.";
        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nama_lengkap, email, password_hash FROM users WHERE email = ?")) {
                ps.setString(1, email.toLowerCase().trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return "Email tidak ditemukan.";
                    if (!rs.getString("password_hash").equals(hash(password)))
                        return "Password salah.";
                    currentUser = new User(
                        rs.getInt("id"),
                        rs.getString("nama_lengkap"),
                        rs.getString("email"),
                        rs.getString("password_hash")
                    );
                    return null; // sukses
                }
            }
        } catch (SQLException e) {
            return "Gagal login: " + e.getMessage();
        }
    }

    // ===================== LOGOUT =====================

    public static void logout() {
        currentUser = null;
    }

    // ===================== GANTI PASSWORD =====================

    public static String gantiPassword(String passwordLama, String passwordBaru) {
        if (currentUser == null) return "Belum login.";
        if (passwordLama == null || passwordBaru == null) return "Password tidak boleh kosong.";
        if (!currentUser.getPasswordHash().equals(hash(passwordLama)))
            return "Password lama tidak sesuai.";
        if (passwordBaru.length() < 6) return "Password baru minimal 6 karakter.";
        if (passwordLama.equals(passwordBaru)) return "Password baru tidak boleh sama dengan yang lama.";

        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?")) {
                ps.setString(1, hash(passwordBaru));
                ps.setInt(2, currentUser.getId());
                ps.executeUpdate();
                currentUser.setPasswordHash(hash(passwordBaru));
                return null;
            }
        } catch (SQLException e) {
            return "Gagal mengubah password: " + e.getMessage();
        }
    }

    // ===================== LUPA PASSWORD =====================

    public static String getSecurityQuestion(String email) {
        if (email == null) return null;
        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement("SELECT security_question FROM users WHERE email = ?")) {
                ps.setString(1, email.toLowerCase().trim());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getString("security_question") : null;
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public static boolean verifySecurityAnswer(String email, String jawabanKeamanan) {
        if (email == null || jawabanKeamanan == null) return false;
        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE email = ? AND security_answer = ?")) {
                ps.setString(1, email.toLowerCase().trim());
                ps.setString(2, hash(jawabanKeamanan.toLowerCase().trim()));
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static String resetPassword(String email, String jawabanKeamanan, String passwordBaru) {
        if (email == null || jawabanKeamanan == null || passwordBaru == null)
            return "Data tidak lengkap.";
        if (passwordBaru.length() < 6) return "Password baru minimal 6 karakter.";

        try {
            Connection conn = getConnection();
            // Verifikasi jawaban keamanan
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id FROM users WHERE email = ? AND security_answer = ?")) {
                ps.setString(1, email.toLowerCase().trim());
                ps.setString(2, hash(jawabanKeamanan.toLowerCase().trim()));
                if (!ps.executeQuery().next())
                    return "Jawaban keamanan salah.";
            }
            // Update password
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET password_hash = ? WHERE email = ?")) {
                ps.setString(1, hash(passwordBaru));
                ps.setString(2, email.toLowerCase().trim());
                ps.executeUpdate();
            }
            return null;
        } catch (SQLException e) {
            return "Gagal reset password: " + e.getMessage();
        }
    }

    // ===================== GETTER =====================

    public static User getCurrentUser() { return currentUser; }
    public static boolean isLoggedIn()  { return currentUser != null; }

    // ===================== HELPER =====================

    /**
     * Dipanggil oleh class luar (seperti NoteStorage) untuk memakai koneksi database yang sama.
     * Menggunakan modifier PUBLIC agar terlihat di package lain.
     */
    public static synchronized Connection getConnection() throws SQLException {
        try {
            // Memastikan koneksi lama masih hidup, jika null atau tertutup, kita buat baru
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + AppConstants.DB_FILE);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver SQLite tidak ditemukan!\n" +
                "Unduh sqlite-jdbc-*.jar dan tambahkan ke Build Path Eclipse.\n" +
                "Download: https://github.com/xerial/sqlite-jdbc/releases"
            );
        }
        return connection;
    }

    /**
     * Hash string menggunakan SHA-256.
     */
    public static String hash(String input) {
        if (input == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 tidak tersedia", e);
        }
    }
}