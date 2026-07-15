package kalender.model;

/**
 * Model data untuk satu catatan (sticky note).
 * Tidak lagi Serializable karena data kini disimpan di SQLite.
 */
public class Note {

    private int     id;          // primary key di DB (0 jika belum disimpan)
    private String  judul;
    private String  isi;
    private int     colorIndex;
    private boolean hasReminder;
    private String  reminderTime; // format "HH:mm"

    public Note(int id, String judul, String isi, int colorIndex,
                boolean hasReminder, String reminderTime) {
        this.id           = id;
        this.judul        = judul;
        this.isi          = isi;
        this.colorIndex   = colorIndex;
        this.hasReminder  = hasReminder;
        this.reminderTime = reminderTime;
    }

    // Konstruktor tanpa id (untuk catatan baru yang belum disimpan)
    public Note(String judul, String isi, int colorIndex,
                boolean hasReminder, String reminderTime) {
        this(0, judul, isi, colorIndex, hasReminder, reminderTime);
    }

    public int     getId()           { return id; }
    public void    setId(int id)     { this.id = id; }

    public String  getJudul()                    { return judul; }
    public void    setJudul(String judul)        { this.judul = judul; }

    public String  getIsi()                      { return isi; }
    public void    setIsi(String isi)            { this.isi = isi; }

    public int     getColorIndex()               { return colorIndex; }
    public void    setColorIndex(int colorIndex) { this.colorIndex = colorIndex; }

    public boolean isHasReminder()                   { return hasReminder; }
    public void    setHasReminder(boolean v)         { this.hasReminder = v; }

    public String  getReminderTime()                   { return reminderTime; }
    public void    setReminderTime(String reminderTime){ this.reminderTime = reminderTime; }
}
