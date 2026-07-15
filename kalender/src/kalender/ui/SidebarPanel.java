package kalender.ui;

import kalender.model.Note;
import kalender.util.AppConstants;
import kalender.util.UiHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Panel sidebar kanan — menampilkan daftar catatan tanggal terpilih.
 */
public class SidebarPanel extends JPanel {

    private final JFrame                     parentFrame;
    private final Map<LocalDate, List<Note>> notesMap;
    private final SidebarListener            listener;

    private JLabel lblSelectedDate;
    private JPanel panelNoteList;

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID"));

    public interface SidebarListener {
        void onTambahNote();
        void onEditNote(Note note, int index);
        void onHapusNote(Note note, int index);
    }

    public SidebarPanel(JFrame parentFrame, Map<LocalDate, List<Note>> notesMap,
                        SidebarListener listener) {
        this.parentFrame = parentFrame;
        this.notesMap    = notesMap;
        this.listener    = listener;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 8));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
            new MatteBorder(0, 1, 0, 0, AppConstants.BORDER_LIGHT),
            new EmptyBorder(16, 14, 16, 14)));
        setPreferredSize(new Dimension(AppConstants.SIDEBAR_WIDTH, 0));

        lblSelectedDate = new JLabel();
        lblSelectedDate.setFont(AppConstants.FONT_BOLD_15);
        lblSelectedDate.setForeground(new Color(40, 40, 80));
        add(lblSelectedDate, BorderLayout.NORTH);

        panelNoteList = new JPanel();
        panelNoteList.setLayout(new BoxLayout(panelNoteList, BoxLayout.Y_AXIS));
        panelNoteList.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(panelNoteList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);

        JButton btnTambah = UiHelper.accentBtn("+ Tambah Catatan");
        btnTambah.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnTambah.addActionListener(e -> listener.onTambahNote());
        add(btnTambah, BorderLayout.SOUTH);
    }

    public void refresh(LocalDate date) {
        lblSelectedDate.setText(date.format(DATE_FMT));
        panelNoteList.removeAll();
        panelNoteList.add(Box.createVerticalStrut(10));

        List<Note> notes = notesMap.getOrDefault(date, Collections.emptyList());
        if (notes.isEmpty()) {
            JLabel empty = new JLabel(
                "<html><center>Belum ada catatan<br>untuk hari ini</center></html>");
            empty.setFont(AppConstants.FONT_ITALIC_12);
            empty.setForeground(AppConstants.TEXT_MUTED);
            empty.setAlignmentX(CENTER_ALIGNMENT);
            empty.setBorder(new EmptyBorder(20, 0, 0, 0));
            panelNoteList.add(empty);
        } else {
            for (int i = 0; i < notes.size(); i++) {
                panelNoteList.add(buildNoteCard(notes.get(i), i));
                panelNoteList.add(Box.createVerticalStrut(8));
            }
        }
        panelNoteList.revalidate();
        panelNoteList.repaint();
    }

    private JPanel buildNoteCard(Note note, int index) {
        int colorIdx = note.getColorIndex();
        if (colorIdx < 0 || colorIdx >= AppConstants.NOTE_COLORS.length) {
            colorIdx = 0;
        }
        Color bg = AppConstants.NOTE_COLORS[colorIdx];

        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(bg);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setBorder(new CompoundBorder(
            new LineBorder(bg.darker(), 1, true),
            new EmptyBorder(8, 10, 8, 10)));

        // Baris atas: judul + tombol
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblJudul = new JLabel(note.getJudul());
        lblJudul.setFont(AppConstants.FONT_BOLD_13);
        lblJudul.setForeground(new Color(30, 30, 60));
        top.add(lblJudul, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        btnPanel.setOpaque(false);

        JButton btnEdit = iconBtn("✏", new Color(80, 80, 180));
        JButton btnDel  = iconBtn("🗑", new Color(200, 60, 60));
        btnEdit.addActionListener(e -> listener.onEditNote(note, index));
        btnDel.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(parentFrame,
                "Hapus catatan \"" + note.getJudul() + "\"?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) listener.onHapusNote(note, index);
        });
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);
        top.add(btnPanel, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Isi catatan
        JTextArea txtIsi = new JTextArea(note.getIsi());
        txtIsi.setFont(AppConstants.FONT_PLAIN_12);
        txtIsi.setForeground(new Color(60, 60, 80));
        txtIsi.setBackground(bg);
        txtIsi.setEditable(false);
        txtIsi.setLineWrap(true);
        txtIsi.setWrapStyleWord(true);
        txtIsi.setBorder(null);
        card.add(txtIsi, BorderLayout.CENTER);

        if (note.isHasReminder()) {
            JLabel badge = new JLabel("⏰ " + note.getReminderTime());
            badge.setFont(AppConstants.FONT_PLAIN_10);
            badge.setForeground(new Color(80, 80, 120));
            card.add(badge, BorderLayout.SOUTH);
        }
        return card;
    }

    private JButton iconBtn(String icon, Color fg) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        btn.setForeground(fg);
        btn.setBorder(new EmptyBorder(2, 5, 2, 5));
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }
}
