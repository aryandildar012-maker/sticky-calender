package kalender.util;

import java.awt.Color;
import java.awt.Font;

/**
 * Konstanta warna, font, dan konfigurasi aplikasi.
 */
public class AppConstants {

    private AppConstants() {}

    // ===================== WARNA UTAMA =====================
    public static final Color BG_MAIN       = new Color(245, 247, 250);
    public static final Color BG_HEADER     = new Color(67, 97, 238);
    public static final Color BG_TODAY      = new Color(67, 97, 238);
    public static final Color BG_SELECTED   = new Color(114, 9, 183);
    public static final Color BG_HAS_NOTE   = new Color(230, 244, 255);
    public static final Color BG_WEEKEND    = new Color(255, 245, 245);
    public static final Color TEXT_HEADER   = Color.WHITE;
    public static final Color TEXT_DAY      = new Color(30, 30, 60);
    public static final Color TEXT_MUTED    = new Color(160, 160, 180);
    public static final Color ACCENT        = new Color(67, 97, 238);
    public static final Color BORDER_LIGHT  = new Color(220, 225, 235);

    // ===================== WARNA STICKY NOTE =====================
    public static final String[] NOTE_COLOR_NAMES = {
        "Kuning", "Hijau Muda", "Merah Muda", "Biru Muda"
    };
    public static final Color[] NOTE_COLORS = {
        new Color(255, 243, 176),
        new Color(198, 246, 213),
        new Color(255, 213, 220),
        new Color(190, 227, 248)
    };

    // ===================== FONT =====================
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_BOLD_16   = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BOLD_15   = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD_13   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BOLD_12   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_PLAIN_13  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_PLAIN_12  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_PLAIN_10  = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font FONT_ITALIC_12 = new Font("Segoe UI", Font.ITALIC, 12);
    public static final Font FONT_ITALIC_11 = new Font("Segoe UI", Font.ITALIC, 11);
    public static final Font FONT_DAY_HEADER = new Font("Segoe UI", Font.BOLD, 12);

    // ===================== KONFIGURASI =====================
    public static final String DB_FILE              = "kalender_app.db";
    public static final String APP_TITLE            = "Kalender Catatan";
    public static final int    WINDOW_MIN_W         = 900;
    public static final int    WINDOW_MIN_H         = 620;
    public static final int    SIDEBAR_WIDTH        = 260;
    public static final int    REMINDER_INTERVAL_MS = 60_000;
}
