package kalender.ui;

import kalender.auth.AuthManager;
import kalender.util.AppConstants;
import kalender.util.UiHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Dialog lupa password — 3 langkah:
 *   1. Masukkan email → tampilkan pertanyaan keamanan
 *   2. Jawab pertanyaan keamanan
 *   3. Masukkan password baru
 */
public class ForgotPasswordDialog extends JDialog {

    private JPanel      cardPanel;
    private CardLayout  cardLayout;
    private JLabel      lblStatus;

    // Step 1
    private JTextField  txtEmail;

    // Step 2
    private JLabel      lblPertanyaan;
    private JTextField  txtJawaban;
    private String      emailTerpilih;

    // Step 3
    private JPasswordField pfBaru, pfKonfirmasi;

    public ForgotPasswordDialog(JFrame parent) {
        super(parent, "Lupa Password", true);
        buildUI();
        setSize(420, 300);
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppConstants.BG_HEADER);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("🔑  Lupa Password");
        title.setFont(AppConstants.FONT_BOLD_15);
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Card panel (3 langkah)
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBorder(new EmptyBorder(20, 24, 10, 24));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.add(buildStep1(), "step1");
        cardPanel.add(buildStep2(), "step2");
        cardPanel.add(buildStep3(), "step3");
        add(cardPanel, BorderLayout.CENTER);

        // Status error
        lblStatus = new JLabel(" ");
        lblStatus.setFont(AppConstants.FONT_PLAIN_12);
        lblStatus.setForeground(new Color(200, 50, 50));
        lblStatus.setBorder(new EmptyBorder(0, 24, 10, 24));
        lblStatus.setBackground(Color.WHITE);
        lblStatus.setOpaque(true);
        add(lblStatus, BorderLayout.SOUTH);
    }

    // ===================== STEP 1: Email =====================

    private JPanel buildStep1() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        p.add(UiHelper.formLabel("Masukkan email yang terdaftar:"));
        txtEmail = UiHelper.styledTextField("");
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtEmail.setAlignmentX(LEFT_ALIGNMENT);
        p.add(txtEmail);
        p.add(Box.createVerticalStrut(16));

        JButton btnLanjut = UiHelper.accentBtn("Lanjut →");
        btnLanjut.setAlignmentX(LEFT_ALIGNMENT);
        btnLanjut.addActionListener(e -> onStep1Next());
        p.add(btnLanjut);
        return p;
    }

    private void onStep1Next() {
        String email = txtEmail.getText().trim();
        String pertanyaan = AuthManager.getSecurityQuestion(email);
        if (pertanyaan == null || pertanyaan.trim().isEmpty()) {
            setStatus("Email tidak ditemukan.");
            return;
        }
        emailTerpilih = email;
        lblPertanyaan.setText("<html><b>Pertanyaan:</b> " + pertanyaan + "</html>");
        txtJawaban.setText("");
        setStatus(" ");
        cardLayout.show(cardPanel, "step2");
        pack();
        setSize(Math.max(getWidth(), 420), getHeight());
        setLocationRelativeTo(getOwner());
    }

    // ===================== STEP 2: Jawaban Keamanan =====================

    private JPanel buildStep2() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        lblPertanyaan = new JLabel();
        lblPertanyaan.setFont(AppConstants.FONT_PLAIN_13);
        lblPertanyaan.setForeground(new Color(40, 40, 80));
        lblPertanyaan.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lblPertanyaan);
        p.add(Box.createVerticalStrut(10));

        p.add(UiHelper.formLabel("Jawaban Anda:"));
        txtJawaban = UiHelper.styledTextField("");
        txtJawaban.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtJawaban.setAlignmentX(LEFT_ALIGNMENT);
        p.add(txtJawaban);
        p.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnKembali = new JButton("← Kembali");
        btnKembali.setFont(AppConstants.FONT_PLAIN_12);
        btnKembali.setBorderPainted(false);
        btnKembali.setContentAreaFilled(false);
        btnKembali.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKembali.addActionListener(e -> { setStatus(" "); cardLayout.show(cardPanel, "step1"); });

        JButton btnLanjut = UiHelper.accentBtn("Lanjut →");
        btnLanjut.addActionListener(e -> onStep2Next());

        btnRow.add(btnKembali);
        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(btnLanjut);
        p.add(btnRow);
        return p;
    }

    private void onStep2Next() {
        String jawaban = txtJawaban.getText().trim();
        if (jawaban.trim().isEmpty()) { setStatus("Jawaban tidak boleh kosong."); return; }
        
        boolean isCorrect = AuthManager.verifySecurityAnswer(emailTerpilih, jawaban);
        if (!isCorrect) {
            setStatus("Jawaban keamanan salah.");
            return;
        }

        pfBaru.setText("");
        pfKonfirmasi.setText("");
        setStatus(" ");
        cardLayout.show(cardPanel, "step3");
    }

    // ===================== STEP 3: Password Baru =====================

    private JPanel buildStep3() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        p.add(UiHelper.formLabel("Password Baru (min. 6 karakter):"));
        pfBaru = UiHelper.styledPasswordField();
        pfBaru.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pfBaru.setAlignmentX(LEFT_ALIGNMENT);
        p.add(pfBaru);
        p.add(Box.createVerticalStrut(10));

        p.add(UiHelper.formLabel("Konfirmasi Password Baru:"));
        pfKonfirmasi = UiHelper.styledPasswordField();
        pfKonfirmasi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pfKonfirmasi.setAlignmentX(LEFT_ALIGNMENT);
        p.add(pfKonfirmasi);
        p.add(Box.createVerticalStrut(16));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnKembali = new JButton("← Kembali");
        btnKembali.setFont(AppConstants.FONT_PLAIN_12);
        btnKembali.setBorderPainted(false);
        btnKembali.setContentAreaFilled(false);
        btnKembali.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKembali.addActionListener(e -> { setStatus(" "); cardLayout.show(cardPanel, "step2"); });

        JButton btnSelesai = UiHelper.accentBtn("Reset Password");
        btnSelesai.addActionListener(e -> onStep3Finish());

        btnRow.add(btnKembali);
        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(btnSelesai);
        p.add(btnRow);
        return p;
    }

    private void onStep3Finish() {
        String baru      = new String(pfBaru.getPassword());
        String konfirmasi = new String(pfKonfirmasi.getPassword());
        if (!baru.equals(konfirmasi)) { setStatus("Konfirmasi password tidak cocok."); return; }

        String err = AuthManager.resetPassword(
            emailTerpilih, txtJawaban.getText().trim(), baru);
        if (err != null) { setStatus(err); return; }

        JOptionPane.showMessageDialog(this,
            "Password berhasil direset!\nSilakan login dengan password baru.",
            "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    // ===================== HELPER =====================

    private void setStatus(String msg) {
        lblStatus.setText(msg);
    }
}
