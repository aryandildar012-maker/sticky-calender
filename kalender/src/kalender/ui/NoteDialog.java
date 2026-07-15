package kalender.ui;

import kalender.model.Note;
import kalender.util.AppConstants;
import kalender.util.UiHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * Dialog modal untuk menambah atau mengedit catatan.
 */
public class NoteDialog extends JDialog {

    private boolean confirmed = false;
    private Note    resultNote;

    private JTextField     txtJudul;
    private JTextArea      txtIsi;
    private JRadioButton[] colorBtns;
    private JCheckBox      chkReminder;
    private JTextField     txtTime;

    public NoteDialog(JFrame parent, Note existingNote) {
        super(parent, existingNote != null ? "Edit Catatan" : "Tambah Catatan Baru", true);
        buildUI(existingNote);
        setMinimumSize(new Dimension(480, 420));
        pack();
        if (getWidth() < 480) setSize(480, getHeight());
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void buildUI(Note existing) {
        boolean isEdit = (existing != null);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(16, 20, 8, 20));
        content.setBackground(Color.WHITE);

        // Judul
        content.add(UiHelper.formLabel("Judul Catatan *"));
        txtJudul = UiHelper.styledTextField(isEdit ? existing.getJudul() : "");
        txtJudul.setAlignmentX(LEFT_ALIGNMENT);
        txtJudul.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtJudul.setPreferredSize(new Dimension(440, 36));
        content.add(txtJudul);
        content.add(Box.createVerticalStrut(12));

        // Isi
        content.add(UiHelper.formLabel("Isi Catatan"));
        txtIsi = new JTextArea(isEdit ? existing.getIsi() : "");
        txtIsi.setFont(AppConstants.FONT_PLAIN_13);
        txtIsi.setLineWrap(true);
        txtIsi.setWrapStyleWord(true);
        txtIsi.setBorder(new EmptyBorder(6, 8, 6, 8));
        txtIsi.setRows(4);
        JScrollPane scrollIsi = new JScrollPane(txtIsi);
        scrollIsi.setAlignmentX(LEFT_ALIGNMENT);
        scrollIsi.setPreferredSize(new Dimension(440, 90));
        scrollIsi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        scrollIsi.setBorder(new LineBorder(new Color(200, 205, 220), 1, true));
        content.add(scrollIsi);
        content.add(Box.createVerticalStrut(12));

        // Warna
        content.add(UiHelper.formLabel("Warna Catatan"));
        JPanel colorPanel = buildColorSelector(isEdit ? existing.getColorIndex() : 0);
        colorPanel.setAlignmentX(LEFT_ALIGNMENT);
        colorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        content.add(colorPanel);
        content.add(Box.createVerticalStrut(12));

        // Reminder
        content.add(UiHelper.formLabel("Reminder"));
        JPanel reminderPanel = buildReminderPanel(existing);
        reminderPanel.setAlignmentX(LEFT_ALIGNMENT);
        reminderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        content.add(reminderPanel);
        content.add(Box.createVerticalStrut(8));

        add(content, BorderLayout.CENTER);
        add(buildButtonRow(isEdit), BorderLayout.SOUTH);
    }

    private JPanel buildColorSelector(int defaultIndex) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        colorBtns = new JRadioButton[AppConstants.NOTE_COLORS.length];
        for (int i = 0; i < AppConstants.NOTE_COLORS.length; i++) {
            colorBtns[i] = new JRadioButton(AppConstants.NOTE_COLOR_NAMES[i]);
            colorBtns[i].setFont(AppConstants.FONT_PLAIN_12);
            colorBtns[i].setBackground(AppConstants.NOTE_COLORS[i]);
            colorBtns[i].setOpaque(true);
            colorBtns[i].setBorder(new EmptyBorder(3, 8, 3, 8));
            colorBtns[i].setFocusPainted(false);
            colorBtns[i].setSelected(i == defaultIndex);
            group.add(colorBtns[i]);
            panel.add(colorBtns[i]);
        }
        return panel;
    }

    private JPanel buildReminderPanel(Note existing) {
        boolean editReminder = (existing != null && existing.isHasReminder());
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setOpaque(false);

        chkReminder = new JCheckBox("Aktifkan Reminder");
        chkReminder.setFont(AppConstants.FONT_PLAIN_12);
        chkReminder.setOpaque(false);
        chkReminder.setSelected(editReminder);

        txtTime = UiHelper.styledTextField(editReminder ? existing.getReminderTime() : "08:00");
        txtTime.setPreferredSize(new Dimension(70, 28));
        txtTime.setEnabled(editReminder);

        JLabel lblFmt = new JLabel("(HH:mm)");
        lblFmt.setFont(AppConstants.FONT_ITALIC_11);
        lblFmt.setForeground(AppConstants.TEXT_MUTED);

        chkReminder.addItemListener(e -> txtTime.setEnabled(e.getStateChange() == ItemEvent.SELECTED));
        panel.add(chkReminder);
        panel.add(txtTime);
        panel.add(lblFmt);
        return panel;
    }

    private JPanel buildButtonRow(boolean isEdit) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        row.setBackground(new Color(245, 247, 250));
        row.setBorder(new MatteBorder(1, 0, 0, 0, AppConstants.BORDER_LIGHT));

        JButton btnCancel = new JButton("Batal");
        btnCancel.setFont(AppConstants.FONT_PLAIN_13);
        btnCancel.setForeground(new Color(60, 60, 80));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = UiHelper.accentBtn(isEdit ? "Simpan" : "Tambah");
        btnSave.addActionListener(e -> onSave());

        row.add(btnCancel);
        row.add(btnSave);
        return row;
    }

    private void onSave() {
        String judul = txtJudul.getText().trim();
        if (judul.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Judul tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String reminderTime = txtTime.getText().trim();
        if (chkReminder.isSelected() && !reminderTime.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            JOptionPane.showMessageDialog(this, "Format waktu tidak valid!\nGunakan HH:mm (contoh: 08:30)",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedColor = 0;
        for (int i = 0; i < colorBtns.length; i++) {
            if (colorBtns[i].isSelected()) { selectedColor = i; break; }
        }
        resultNote = new Note(
            judul, txtIsi.getText().trim(), selectedColor,
            chkReminder.isSelected(),
            chkReminder.isSelected() ? reminderTime : ""
        );
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Note    getResultNote() { return resultNote; }
}
