package kalender.data;

import kalender.auth.AuthManager;
import kalender.auth.User;
import kalender.model.Note;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Menyimpan dan memuat catatan dari database SQLite.
 * Setiap catatan terhubung ke user yang sedang login (via user_id).
 */
public class NoteStorage {

    private NoteStorage() {}

    // ===================== LOAD =====================

    /**
     * Muat semua catatan milik user yang sedang login dari database.
     */
    public static Map<LocalDate, List<Note>> loadAllNotes() {
        Map<LocalDate, List<Note>> result = new HashMap<>();
        User user = AuthManager.getCurrentUser();
        if (user == null) return result;

        String sql = "SELECT id, tanggal, judul, isi, color_index, has_reminder, reminder_time" +
                     " FROM notes WHERE user_id = ? ORDER BY id ASC";
        try (Connection conn = AuthManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    String tanggalStr = rs.getString("tanggal");
                    if (tanggalStr == null) continue;
                    LocalDate date = LocalDate.parse(tanggalStr);
                    Note note = new Note(
                        rs.getInt("id"),
                        rs.getString("judul"),
                        rs.getString("isi"),
                        rs.getInt("color_index"),
                        rs.getInt("has_reminder") == 1,
                        rs.getString("reminder_time")
                    );
                    result.computeIfAbsent(date, k -> new ArrayList<>()).add(note);
                } catch (java.time.format.DateTimeParseException e) {
                    System.err.println("Gagal memformat tanggal catatan: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat catatan: " + e.getMessage());
        }
        return result;
    }

    // ===================== INSERT =====================

    /**
     * Simpan catatan baru ke database. Mengisi field id pada objek Note setelah insert.
     */
    public static boolean insertNote(LocalDate date, Note note) {
        User user = AuthManager.getCurrentUser();
        if (user == null || date == null || note == null) return false;

        String sql = "INSERT INTO notes (user_id, tanggal, judul, isi, color_index, has_reminder, reminder_time)" +
                     " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = AuthManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, user.getId());
            ps.setString(2, date.toString());
            ps.setString(3, note.getJudul());
            ps.setString(4, note.getIsi());
            ps.setInt(5, note.getColorIndex());
            ps.setInt(6, note.isHasReminder() ? 1 : 0);
            ps.setString(7, note.getReminderTime());
            ps.executeUpdate();
            // Ambil ID yang digenerate database dan simpan ke objek Note
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) note.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan catatan: " + e.getMessage());
            return false;
        }
    }

    // ===================== UPDATE =====================

    /**
     * Perbarui catatan yang sudah ada di database.
     */
    public static boolean updateNote(Note note) {
        if (note == null) return false;
        String sql = "UPDATE notes SET judul=?, isi=?, color_index=?, has_reminder=?, reminder_time=?" +
                     " WHERE id=?";
        try (Connection conn = AuthManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, note.getJudul());
            ps.setString(2, note.getIsi());
            ps.setInt(3, note.getColorIndex());
            ps.setInt(4, note.isHasReminder() ? 1 : 0);
            ps.setString(5, note.getReminderTime());
            ps.setInt(6, note.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengupdate catatan: " + e.getMessage());
            return false;
        }
    }

    // ===================== DELETE =====================

    /**
     * Hapus catatan dari database berdasarkan ID-nya.
     */
    public static boolean deleteNote(int noteId) {
        try (Connection conn = AuthManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM notes WHERE id=?")) {
            ps.setInt(1, noteId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus catatan: " + e.getMessage());
            return false;
        }
    }
}
