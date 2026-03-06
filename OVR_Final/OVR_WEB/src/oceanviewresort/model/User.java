package oceanviewresort.model;

public class User {

    private int id;
    private String username;
    private String password;
    private String fullName;

    public User() {}

    public User(int id, String username,
                String password, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }

    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }

    public String getFullName() { return fullName; }
    public void setFullName(String f) { this.fullName = f; }
}
