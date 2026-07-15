package kalender.ui;

import kalender.auth.AuthManager;
import kalender.util.AppConstants;
import kalender.util.UiHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Dialog untuk mengubah password ketika user sudah login.
 */
public class ChangePasswordDialog extends JDialog {

    private JPasswordField pfLama, pfBaru, pfKonfirmasi;
    private JLabel         lblStatus;

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Ganti Password", true);
        buildUI();
        setSize(400, 320);
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppConstants.BG_HEADER);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("🔒  Ganti Password");
        title.setFont(AppConstants.FONT_BOLD_15);
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);

        form.add(UiHelper.formLabel("Password Lama:"));
        pfLama = UiHelper.styledPasswordField();
        pfLama.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pfLama.setAlignmentX(LEFT_ALIGNMENT);
        form.add(pfLama);
        form.add(Box.createVerticalStrut(10));

        form.add(UiHelper.formLabel("Password Baru (min. 6 karakter):"));
        pfBaru = UiHelper.styledPasswordField();
        pfBaru.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pfBaru.setAlignmentX(LEFT_ALIGNMENT);
        form.add(pfBaru);
        form.add(Box.createVerticalStrut(10));

        form.add(UiHelper.formLabel("Konfirmasi Password Baru:"));
        pfKonfirmasi = UiHelper.styledPasswordField();
        pfKonfirmasi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pfKonfirmasi.setAlignmentX(LEFT_ALIGNMENT);
        form.add(pfKonfirmasi);
        form.add(Box.createVerticalStrut(16));

        JButton btnSimpan = UiHelper.accentBtn("Simpan Password Baru");
        btnSimpan.setAlignmentX(LEFT_ALIGNMENT);
        btnSimpan.addActionListener(e -> onSimpan());
        form.add(btnSimpan);

        add(form, BorderLayout.CENTER);

        // Status error
        lblStatus = new JLabel(" ");
        lblStatus.setFont(AppConstants.FONT_PLAIN_12);
        lblStatus.setForeground(new Color(200, 50, 50));
        lblStatus.setBorder(new EmptyBorder(0, 24, 12, 24));
        lblStatus.setBackground(Color.WHITE);
        lblStatus.setOpaque(true);
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void onSimpan() {
        String lama      = new String(pfLama.getPassword());
        String baru      = new String(pfBaru.getPassword());
        String konfirmasi = new String(pfKonfirmasi.getPassword());

        if (!baru.equals(konfirmasi)) {
            lblStatus.setText("Konfirmasi password tidak cocok.");
            return;
        }

        String err = AuthManager.gantiPassword(lama, baru);
        if (err != null) {
            lblStatus.setText(err);
            return;
        }

        JOptionPane.showMessageDialog(this,
            "Password berhasil diubah!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
