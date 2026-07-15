package kalender.util;

import kalender.model.Note;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer latar belakang yang memeriksa reminder setiap menit.
 */
public class ReminderChecker {

    private final JFrame                     parentFrame;
    private final Map<LocalDate, List<Note>> notesMap;
    private final Timer                      timer;
    private final Set<String>                firedReminders;
    private LocalDate                        lastCheckedDate;

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

    public ReminderChecker(JFrame parentFrame, Map<LocalDate, List<Note>> notesMap) {
        this.parentFrame = parentFrame;
        this.notesMap    = notesMap;
        this.timer       = new Timer(true);
        this.firedReminders = new HashSet<>();
        this.lastCheckedDate = LocalDate.now();
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                SwingUtilities.invokeLater(() -> checkReminders());
            }
        }, 0, AppConstants.REMINDER_INTERVAL_MS);
    }

    public void stop() { timer.cancel(); }

    private void checkReminders() {
        LocalDateTime now     = LocalDateTime.now();
        String        nowTime = String.format("%02d:%02d", now.getHour(), now.getMinute());
        LocalDate     today   = now.toLocalDate();

        // Reset set jika hari berganti
        if (!today.equals(lastCheckedDate)) {
            firedReminders.clear();
            lastCheckedDate = today;
        }

        for (Note note : notesMap.getOrDefault(today, Collections.emptyList())) {
            if (note.isHasReminder() && note.getReminderTime().equals(nowTime)) {
                String key = note.getId() + "@" + note.getReminderTime();
                if (firedReminders.contains(key)) {
                    continue;
                }
                firedReminders.add(key);

                String pesan = "<html><b>📌 " + note.getJudul() + "</b>"
                    + (note.getIsi().isEmpty() ? "" : "<br><br>" + note.getIsi())
                    + "</html>";
                JOptionPane.showMessageDialog(
                    parentFrame, pesan,
                    "⏰ Pengingat — " + today.format(DATE_FMT),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
