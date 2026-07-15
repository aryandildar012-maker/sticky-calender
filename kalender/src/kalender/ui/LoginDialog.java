package kalender.ui;

import kalender.auth.AuthManager;
import kalender.util.AppConstants;
import kalender.util.UiHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Dialog login/register yang muncul sebelum aplikasi utama.
 * Menggunakan JTabbedPane: tab Login dan tab Daftar Akun.
 */
public class LoginDialog extends JDialog {

    private boolean loginSucceeded = false;

    // Tab Login
    private JTextField     tfLoginEmail;
    private JPasswordField pfLoginPassword;
    private JLabel         lblLoginStatus;

    // Tab Register
    private JTextField     tfRegNama, tfRegEmail;
    private JPasswordField pfRegPassword, pfRegKonfirmasi;
    private JComboBox<String> cmbPertanyaan;
    private JTextField     tfJawaban;
    private JLabel         lblRegStatus;

    private static final String[] PERTANYAAN_KEAMANAN = {
        "Pilih pertanyaan...",
        "Apa nama hewan peliharaan pertama Anda?",
        "Di kota mana Anda lahir?",
        "Apa nama SD Anda?",
        "Apa makanan favorit Anda semasa kecil?",
        "Apa nama jalan tempat Anda tumbuh besar?"
    };

    public LoginDialog(Frame parent) {
        super(parent, AppConstants.APP_TITLE, true);
        buildUI();
        setSize(440, 480);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppConstants.BG_HEADER);
        header.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel ico = new JLabel("📅");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        header.add(ico, BorderLayout.WEST);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.setBorder(new EmptyBorder(0, 12, 0, 0));

        JLabel lblApp = new JLabel(AppConstants.APP_TITLE);
        lblApp.setFont(AppConstants.FONT_TITLE);
        lblApp.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Catat jadwalmu, simpan rencanamu");
        lblSub.setFont(AppConstants.FONT_PLAIN_12);
        lblSub.setForeground(new Color(200, 210, 255));

        headerText.add(lblApp);
        headerText.add(lblSub);
        header.add(headerText, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== TABS =====
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppConstants.FONT_BOLD_13);
        tabs.setBackground(Color.WHITE);
        tabs.addTab("  Masuk  ",  buildLoginTab());
        tabs.addTab("  Daftar  ", buildRegisterTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ===================== TAB LOGIN =====================

    private JPanel buildLoginTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 24, 16, 24));
        p.setBackground(Color.WHITE);

        p.add(UiHelper.formLabel("Email"));
        tfLoginEmail = UiHelper.styledTextField("");
        tfLoginEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfLoginEmail.setAlignmentX(LEFT_ALIGNMENT);
        p.add(tfLoginEmail);
        p.add(Box.createVerticalStrut(10));

        p.add(UiHelper.formLabel("Password"));
        pfLoginPassword = UiHelper.styledPasswordField();
        pfLoginPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pfLoginPassword.setAlignmentX(LEFT_ALIGNMENT);
        // Login saat Enter ditekan
        pfLoginPassword.addActionListener(e -> onLogin());
        p.add(pfLoginPassword);
        p.add(Box.createVerticalStrut(6));

        // Link lupa password
        JButton btnLupa = new JButton("Lupa password?");
        btnLupa.setFont(AppConstants.FONT_PLAIN_12);
        btnLupa.setForeground(AppConstants.ACCENT);
        btnLupa.setBorderPainted(false);
        btnLupa.setContentAreaFilled(false);
        btnLupa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLupa.setAlignmentX(LEFT_ALIGNMENT);
        btnLupa.addActionListener(e -> {
            ForgotPasswordDialog d = new ForgotPasswordDialog(null);
            d.setVisible(true);
        });
        p.add(btnLupa);
        p.add(Box.createVerticalStrut(16));

        JButton btnLogin = UiHelper.accentBtn("Masuk");
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.addActionListener(e -> onLogin());
        p.add(btnLogin);
        p.add(Box.createVerticalStrut(8));

        lblLoginStatus = new JLabel(" ");
        lblLoginStatus.setFont(AppConstants.FONT_PLAIN_12);
        lblLoginStatus.setForeground(new Color(200, 50, 50));
        lblLoginStatus.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lblLoginStatus);

        return p;
    }

    private void onLogin() {
        String email    = tfLoginEmail.getText().trim();
        String password = new String(pfLoginPassword.getPassword());

        String err = AuthManager.login(email, password);
        if (err != null) {
            lblLoginStatus.setText(err);
            return;
        }
        loginSucceeded = true;
        dispose();
    }

    // ===================== TAB REGISTER =====================

    private JPanel buildRegisterTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 24, 16, 24));
        p.setBackground(Color.WHITE);

        // Nama lengkap
        p.add(UiHelper.formLabel("Nama Lengkap"));
        tfRegNama = UiHelper.styledTextField("");
        tfRegNama.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfRegNama.setAlignmentX(LEFT_ALIGNMENT);
        p.add(tfRegNama);
        p.add(Box.createVerticalStrut(8));

        // Email
        p.add(UiHelper.formLabel("Email"));
        tfRegEmail = UiHelper.styledTextField("");
        tfRegEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfRegEmail.setAlignmentX(LEFT_ALIGNMENT);
        p.add(tfRegEmail);
        p.add(Box.createVerticalStrut(8));

        // Password
        p.add(UiHelper.formLabel("Password (min. 6 karakter)"));
        pfRegPassword = UiHelper.styledPasswordField();
        pfRegPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pfRegPassword.setAlignmentX(LEFT_ALIGNMENT);
        p.add(pfRegPassword);
        p.add(Box.createVerticalStrut(8));

        // Konfirmasi
        p.add(UiHelper.formLabel("Konfirmasi Password"));
        pfRegKonfirmasi = UiHelper.styledPasswordField();
        pfRegKonfirmasi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pfRegKonfirmasi.setAlignmentX(LEFT_ALIGNMENT);
        p.add(pfRegKonfirmasi);
        p.add(Box.createVerticalStrut(8));

        // Pertanyaan keamanan
        p.add(UiHelper.formLabel("Pertanyaan Keamanan"));
        cmbPertanyaan = new JComboBox<>(PERTANYAAN_KEAMANAN);
        cmbPertanyaan.setFont(AppConstants.FONT_PLAIN_12);
        cmbPertanyaan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbPertanyaan.setAlignmentX(LEFT_ALIGNMENT);
        p.add(cmbPertanyaan);
        p.add(Box.createVerticalStrut(8));

        // Jawaban keamanan
        p.add(UiHelper.formLabel("Jawaban Keamanan"));
        tfJawaban = UiHelper.styledTextField("");
        tfJawaban.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfJawaban.setAlignmentX(LEFT_ALIGNMENT);
        p.add(tfJawaban);
        p.add(Box.createVerticalStrut(14));

        JButton btnDaftar = UiHelper.accentBtn("Buat Akun");
        btnDaftar.setAlignmentX(LEFT_ALIGNMENT);
        btnDaftar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnDaftar.addActionListener(e -> onRegister());
        p.add(btnDaftar);
        p.add(Box.createVerticalStrut(8));

        lblRegStatus = new JLabel(" ");
        lblRegStatus.setFont(AppConstants.FONT_PLAIN_12);
        lblRegStatus.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lblRegStatus);

        // Bungkus dalam scroll pane agar tab register bisa di-scroll jika layar kecil
        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scroll);
        return wrapper;
    }

    private void onRegister() {
        String nama      = tfRegNama.getText().trim();
        String email     = tfRegEmail.getText().trim();
        String password  = new String(pfRegPassword.getPassword());
        String konfirmasi = new String(pfRegKonfirmasi.getPassword());
        String pertanyaan = (String) cmbPertanyaan.getSelectedItem();
        String jawaban   = tfJawaban.getText().trim();

        // Validasi konfirmasi password di sisi UI
        if (!password.equals(konfirmasi)) {
            setRegStatus("Konfirmasi password tidak cocok.", true);
            return;
        }
        if (cmbPertanyaan.getSelectedIndex() == 0) {
            setRegStatus("Pilih pertanyaan keamanan.", true);
            return;
        }

        String err = AuthManager.register(nama, email, password, pertanyaan, jawaban);
        if (err != null) {
            setRegStatus(err, true);
            return;
        }

        setRegStatus("✓ Akun berhasil dibuat! Silakan login.", false);
        // Kosongkan form
        tfRegNama.setText("");
        tfRegEmail.setText("");
        pfRegPassword.setText("");
        pfRegKonfirmasi.setText("");
        tfJawaban.setText("");
        cmbPertanyaan.setSelectedIndex(0);
    }

    private void setRegStatus(String msg, boolean isError) {
        lblRegStatus.setForeground(isError ? new Color(200, 50, 50) : new Color(30, 140, 60));
        lblRegStatus.setText(msg);
    }

    // ===================== GETTER =====================

    public boolean isLoginSucceeded() { return loginSucceeded; }
}
