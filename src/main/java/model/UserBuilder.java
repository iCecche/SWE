package model;

import model.enums.UserRole;

public class UserBuilder {
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

    private UserBuilder() {};

    public static UserBuilder create() {
        return new UserBuilder();
    }

    public void withId(int id) {
        this.id = id;
    }

    public void withUsername(String username) {
        this.username = username;
    }

    public void withPassword(String password) {
        this.password = password;
    }

    public void withRole(UserRole role) {
        this.role = role;
    }

    public void withNome(String nome) {
        this.nome = nome;
    }

    public void withCognome(String cognome) {
        this.cognome = cognome;
    }

    public void withIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void withCap(String cap) {
        this.cap = cap;
    }

    public void withProvincia(String provincia) {
        this.provincia = provincia;
    }

    public void withStato(String stato) {
        this.stato = stato;
    }

    public User build() {
        return new User(id, username, password, role, nome, cognome, indirizzo, cap, provincia, stato);
    }
}
