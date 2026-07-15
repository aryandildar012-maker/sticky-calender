package kalender.auth;

/**
 * Model data untuk akun pengguna.
 */
public class User {

    private int    id;
    private String namaLengkap;
    private String email;
    private String passwordHash; // disimpan dalam bentuk hash SHA-256

    public User(int id, String namaLengkap, String email, String passwordHash) {
        this.id           = id;
        this.namaLengkap  = namaLengkap;
        this.email        = email;
        this.passwordHash = passwordHash;
    }

    public int    getId()           { return id; }
    public String getNamaLengkap()  { return namaLengkap; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }

    public void setNamaLengkap(String namaLengkap)   { this.namaLengkap  = namaLengkap; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', nama='" + namaLengkap + "'}";
    }
}
