package kalender.ui;

import kalender.model.Note;
import kalender.util.AppConstants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Grid kalender 6×7. Setiap sel menampilkan tanggal dan preview catatan.
 */
public class CalendarPanel extends JPanel {

    private final Map<LocalDate, List<Note>> notesMap;
    private final CalendarListener           listener;
    private JPanel                           panelGrid;

    public interface CalendarListener {
        void onDateSelected(LocalDate date);
    }

    public CalendarPanel(Map<LocalDate, List<Note>> notesMap, CalendarListener listener) {
        this.notesMap = notesMap;
        this.listener = listener;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppConstants.BG_MAIN);
        setBorder(new EmptyBorder(16, 16, 16, 8));
        add(buildDayHeader(), BorderLayout.NORTH);
        panelGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        panelGrid.setOpaque(false);
        add(panelGrid, BorderLayout.CENTER);
    }

    private JPanel buildDayHeader() {
        String[] days = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
        JPanel row = new JPanel(new GridLayout(1, 7, 4, 0));
        row.setOpaque(false);
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(AppConstants.FONT_DAY_HEADER);
            boolean wknd = d.equals("Min") || d.equals("Sab");
            lbl.setForeground(wknd ? new Color(220, 80, 80) : new Color(100, 110, 140));
            lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
            row.add(lbl);
        }
        return row;
    }

    public void refresh(YearMonth ym, LocalDate selectedDate) {
        panelGrid.removeAll();
        LocalDate firstDay    = ym.atDay(1);
        int       startOffset = firstDay.getDayOfWeek().getValue() % 7;
        LocalDate today       = LocalDate.now();

        for (int i = 0; i < startOffset; i++) panelGrid.add(emptyCell());

        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);
            panelGrid.add(buildDayCell(date, today, selectedDate));
        }
        int filled = startOffset + ym.lengthOfMonth();
        for (int i = filled; i < 42; i++) panelGrid.add(emptyCell());

        panelGrid.revalidate();
        panelGrid.repaint();
    }

    private JPanel emptyCell() {
        JPanel p = new JPanel(); p.setOpaque(false); return p;
    }

    private JPanel buildDayCell(LocalDate date, LocalDate today, LocalDate selectedDate) {
        boolean isSel    = date.equals(selectedDate);
        boolean isToday  = date.equals(today);
        boolean isWknd   = date.getDayOfWeek() == DayOfWeek.SUNDAY
                        || date.getDayOfWeek() == DayOfWeek.SATURDAY;
        List<Note> notes = notesMap.getOrDefault(date, Collections.emptyList());
        boolean hasNotes = !notes.isEmpty();

        Color bgColor = isSel    ? new Color(240, 235, 255)
                      : isToday  ? new Color(235, 240, 255)
                      : hasNotes ? AppConstants.BG_HAS_NOTE
                      : isWknd   ? AppConstants.BG_WEEKEND
                      : Color.WHITE;

        Color borderColor = isSel   ? AppConstants.BG_SELECTED
                          : isToday ? AppConstants.BG_TODAY
                          : AppConstants.BORDER_LIGHT;
        int   borderThick = (isSel || isToday) ? 2 : 1;

        JPanel cell = new JPanel(new BorderLayout(2, 2));
        cell.setBackground(bgColor);
        cell.setBorder(cellBorder(borderColor, borderThick));

        JLabel lblDay = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.RIGHT);
        lblDay.setFont(new Font("Segoe UI", isToday ? Font.BOLD : Font.PLAIN, 13));
        lblDay.setForeground(isToday ? AppConstants.BG_TODAY
                           : isWknd  ? new Color(200, 80, 80)
                           : AppConstants.TEXT_DAY);
        cell.add(lblDay, BorderLayout.NORTH);

        if (hasNotes) cell.add(buildPreview(notes), BorderLayout.CENTER);

        cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cell.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { listener.onDateSelected(date); }
            @Override public void mouseEntered(MouseEvent e)  {
                if (!date.equals(selectedDate)) cell.setBorder(cellBorder(AppConstants.ACCENT, 1));
            }
            @Override public void mouseExited(MouseEvent e)   { cell.setBorder(cellBorder(borderColor, borderThick)); }
        });
        return cell;
    }

    private JPanel buildPreview(List<Note> notes) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        int show = Math.min(2, notes.size());
        for (int i = 0; i < show; i++) {
            Note n = notes.get(i);
            JLabel chip = new JLabel("• " + truncate(n.getJudul(), 9));
            chip.setFont(AppConstants.FONT_PLAIN_10);
            chip.setForeground(new Color(60, 60, 100));
            chip.setOpaque(true);
            int colorIdx = n.getColorIndex();
            if (colorIdx < 0 || colorIdx >= AppConstants.NOTE_COLORS.length) {
                colorIdx = 0;
            }
            chip.setBackground(AppConstants.NOTE_COLORS[colorIdx]);
            chip.setBorder(new EmptyBorder(1, 3, 1, 3));
            p.add(chip);
            p.add(Box.createVerticalStrut(1));
        }
        if (notes.size() > 2) {
            JLabel more = new JLabel("+" + (notes.size()-2) + " lagi");
            more.setFont(new Font("Segoe UI", Font.ITALIC, 9));
            more.setForeground(AppConstants.TEXT_MUTED);
            p.add(more);
        }
        return p;
    }

    private Border cellBorder(Color c, int t) {
        return new CompoundBorder(new LineBorder(c, t, true), new EmptyBorder(4, 5, 4, 5));
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}
