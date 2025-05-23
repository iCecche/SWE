package model;

public class User {

    private int id;
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String indirizzo;
    private String cap;
    private String provincia;
    private String stato;


    public User() {};
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password, String nome, String cognome, String indirizzo, String cap, String provincia, String stato) {
        this(id, username, password);
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.cap = cap;
        this.provincia = provincia;
        this.stato = stato;
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

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getCap() {
        return cap;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getStato() {
        return stato;
    }

    public void print() {
        System.out.println("ID: " + id);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Nome: " + nome);
        System.out.println("Cognome: " + cognome);
        System.out.println("Indirizzo: " + indirizzo);
        System.out.println("Cap: " + cap);
        System.out.println("Provincia: " + provincia);
        System.out.println("Stato: " + stato);
        System.out.println(" ");
    }
}

