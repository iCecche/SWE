package model;

import model.enums.UserRole;

public class User {

    private int id;
    private String username;
    private String password;
    private UserRole role;
    private String nome;
    private String cognome;
    private String indirizzo;
    private String cap;
    private String provincia;
    private String stato;
    private boolean is_deleted;

    private User(int id, String username, String password, UserRole role, boolean is_deleted) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.is_deleted = is_deleted;
    }

    public User(int id, String username, String password, UserRole role, String nome, String cognome, String indirizzo, String cap, String provincia, String stato, boolean is_deleted) {
        this(id, username, password, role, is_deleted);
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.cap = cap;
        this.provincia = provincia;
        this.stato = stato;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User other)) return false;
        return this.id == other.id;
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

    public UserRole getRole() {
        return role;
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

    public boolean isDeleted() { return is_deleted; }

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

