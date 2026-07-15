package kalender.ui;

import kalender.auth.AuthManager;
import kalender.util.AppConstants;
import kalender.util.UiHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Panel header: judul app, info user, navigasi bulan, tombol akun.
 */
public class TopBar extends JPanel {

    private JLabel lblBulanTahun;
    private final TopBarListener listener;

    private static final DateTimeFormatter MONTH_FMT =
        DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"));

    public interface TopBarListener {
        void onPrevMonth();
        void onNextMonth();
        void onHariIni();
        void onLogout();
        void onGantiPassword();
    }

    public TopBar(TopBarListener listener) {
        this.listener = listener;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppConstants.BG_HEADER);
        setBorder(new EmptyBorder(12, 20, 12, 20));

        // ===== KIRI: judul + info user =====
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblTitle = new JLabel("📅  " + AppConstants.APP_TITLE);
        lblTitle.setFont(AppConstants.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        left.add(lblTitle);

        // Nama & email user yang sedang login
        if (AuthManager.isLoggedIn()) {
            JLabel lblUser = new JLabel(
                "👤  " + AuthManager.getCurrentUser().getNamaLengkap()
                + "  ·  " + AuthManager.getCurrentUser().getEmail()
            );
            lblUser.setFont(AppConstants.FONT_PLAIN_12);
            lblUser.setForeground(new Color(200, 210, 255));
            left.add(lblUser);
        }
        add(left, BorderLayout.WEST);

        // ===== KANAN: navigasi bulan + menu akun =====
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        // Navigasi bulan
        JButton btnPrev  = UiHelper.navBtn("◀");
        lblBulanTahun    = new JLabel("", SwingConstants.CENTER);
        lblBulanTahun.setFont(AppConstants.FONT_BOLD_16);
        lblBulanTahun.setForeground(Color.WHITE);
        lblBulanTahun.setPreferredSize(new Dimension(180, 28));
        JButton btnNext  = UiHelper.navBtn("▶");
        JButton btnToday = UiHelper.navBtn("Hari Ini");

        btnPrev.addActionListener(e  -> listener.onPrevMonth());
        btnNext.addActionListener(e  -> listener.onNextMonth());
        btnToday.addActionListener(e -> listener.onHariIni());

        right.add(btnPrev);
        right.add(lblBulanTahun);
        right.add(btnNext);
        right.add(btnToday);
        right.add(Box.createHorizontalStrut(8));

        // Menu akun (dropdown)
        JButton btnAkun = UiHelper.navBtn("⚙ Akun ▾");
        btnAkun.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem gantiPw = new JMenuItem("🔒  Ganti Password");
            gantiPw.setFont(AppConstants.FONT_PLAIN_13);
            gantiPw.addActionListener(ev -> listener.onGantiPassword());

            JMenuItem logout = new JMenuItem("🚪  Logout");
            logout.setFont(AppConstants.FONT_PLAIN_13);
            logout.setForeground(new Color(180, 40, 40));
            logout.addActionListener(ev -> listener.onLogout());

            menu.add(gantiPw);
            menu.addSeparator();
            menu.add(logout);
            menu.show(btnAkun, 0, btnAkun.getHeight());
        });
        right.add(btnAkun);

        add(right, BorderLayout.EAST);
    }

    public void updateLabel(YearMonth ym) {
        lblBulanTahun.setText(ym.format(MONTH_FMT));
    }
}
