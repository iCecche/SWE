package class_logic;

public class User {

    private int id;
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String indirizzo;
    private String cap;


    public User() {};
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void print() {
        System.out.println("ID: " + id);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println(" ");
    }
}

