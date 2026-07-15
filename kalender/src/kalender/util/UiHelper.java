package kalender.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Kumpulan helper untuk membangun komponen UI yang konsisten di seluruh aplikasi.
 */
public class UiHelper {

    private UiHelper() {}

    /**
     * Tombol berwarna solid yang teks-nya selalu terlihat.
     * Menggunakan paintComponent kustom untuk menghindari masalah
     * Windows Look & Feel yang mengabaikan setBackground().
     */
    public static JButton solidBtn(String text, Color bgColor, Color fgColor, Font font) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(font);
        btn.setForeground(fgColor);
        btn.setBackground(bgColor);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        // Efek hover: sedikit lebih terang
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color base   = bgColor;
            final Color bright = bgColor.brighter();
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bright); btn.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(base); btn.repaint();
            }
        });
        return btn;
    }

    /** Shortcut: tombol aksen biru dengan teks putih */
    public static JButton accentBtn(String text) {
        return solidBtn(text, AppConstants.ACCENT, Color.WHITE, AppConstants.FONT_BOLD_13);
    }

    /** Tombol navigasi header (biru lebih gelap) */
    public static JButton navBtn(String text) {
        return solidBtn(text, new Color(50, 75, 200), Color.WHITE, AppConstants.FONT_BOLD_13);
    }

    /** Label form (bold, warna gelap) */
    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppConstants.FONT_BOLD_12);
        lbl.setForeground(new Color(70, 70, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    /** TextField standar dengan border rounded */
    public static JTextField styledTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(AppConstants.FONT_PLAIN_13);
        tf.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(new Color(200, 205, 220), 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    /** PasswordField standar */
    public static JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(AppConstants.FONT_PLAIN_13);
        pf.setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.LineBorder(new Color(200, 205, 220), 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }
}
