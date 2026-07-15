package kalender.ui;

import kalender.auth.AuthManager;
import kalender.data.NoteStorage;
import kalender.model.Note;
import kalender.util.AppConstants;
import kalender.util.ReminderChecker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Jendela utama aplikasi kalender.
 * Mengelola state (bulan, tanggal dipilih, data catatan) dan
 * mengkoordinasikan semua komponen UI.
 */
public class CalendarApp extends JFrame
        implements TopBar.TopBarListener,
                   CalendarPanel.CalendarListener,
                   SidebarPanel.SidebarListener {

    private YearMonth                        currentYearMonth;
    private LocalDate                        selectedDate;
    private Map<LocalDate, List<Note>>       notesMap;

    private TopBar          topBar;
    private CalendarPanel   calendarPanel;
    private SidebarPanel    sidebarPanel;
    private ReminderChecker reminderChecker;

    public CalendarApp() {
        currentYearMonth = YearMonth.now();
        selectedDate     = LocalDate.now();
        notesMap         = NoteStorage.loadAllNotes();

        buildUI();
        initReminder();

        setTitle(AppConstants.APP_TITLE + " — " + AuthManager.getCurrentUser().getNamaLengkap());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(AppConstants.WINDOW_MIN_W, AppConstants.WINDOW_MIN_H));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI() {
        topBar        = new TopBar(this);
        calendarPanel = new CalendarPanel(notesMap, this);
        sidebarPanel  = new SidebarPanel(this, notesMap, this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppConstants.BG_MAIN);
        root.add(topBar,        BorderLayout.NORTH);
        root.add(calendarPanel, BorderLayout.CENTER);
        root.add(sidebarPanel,  BorderLayout.EAST);
        setContentPane(root);

        topBar.updateLabel(currentYearMonth);
        calendarPanel.refresh(currentYearMonth, selectedDate);
        sidebarPanel.refresh(selectedDate);
    }

    private void initReminder() {
        reminderChecker = new ReminderChecker(this, notesMap);
        reminderChecker.start();
    }

    // ===================== TopBarListener =====================

    @Override public void onPrevMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1); refreshAll();
    }
    @Override public void onNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1); refreshAll();
    }
    @Override public void onHariIni() {
        currentYearMonth = YearMonth.now();
        selectedDate     = LocalDate.now();
        refreshAll();
    }

    @Override
    public void onLogout() {
        int ok = JOptionPane.showConfirmDialog(this,
            "Yakin ingin logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        if (reminderChecker != null) reminderChecker.stop();
        AuthManager.logout();
        dispose();

        // Tampilkan kembali layar login
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            if (login.isLoginSucceeded()) new CalendarApp();
        });
    }

    @Override
    public void onGantiPassword() {
        ChangePasswordDialog d = new ChangePasswordDialog(this);
        d.setVisible(true);
    }

    // ===================== CalendarListener =====================

    @Override
    public void onDateSelected(LocalDate date) {
        selectedDate     = date;
        currentYearMonth = YearMonth.from(date);
        refreshAll();
    }

    // ===================== SidebarListener =====================

    @Override
    public void onTambahNote() {
        NoteDialog dialog = new NoteDialog(this, null);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) return;

        Note note = dialog.getResultNote();
        if (NoteStorage.insertNote(selectedDate, note)) {          // simpan ke DB
            notesMap.computeIfAbsent(selectedDate, k -> new ArrayList<>()).add(note);
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal menyimpan catatan ke database!",
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onEditNote(Note note, int index) {
        NoteDialog dialog = new NoteDialog(this, note);
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) return;

        Note updated = dialog.getResultNote();
        updated.setId(note.getId());                         // pertahankan ID asli
        if (NoteStorage.updateNote(updated)) {                     // update ke DB
            List<Note> list = notesMap.get(selectedDate);
            if (list != null) list.set(index, updated);
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal memperbarui catatan di database!",
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onHapusNote(Note note, int index) {
        if (NoteStorage.deleteNote(note.getId())) {                // hapus dari DB
            List<Note> list = notesMap.get(selectedDate);
            if (list != null) {
                list.remove(index);
                if (list.isEmpty()) notesMap.remove(selectedDate);
            }
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal menghapus catatan dari database!",
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== Helper =====================

    private void refreshAll() {
        topBar.updateLabel(currentYearMonth);
        calendarPanel.refresh(currentYearMonth, selectedDate);
        sidebarPanel.refresh(selectedDate);
    }
}
