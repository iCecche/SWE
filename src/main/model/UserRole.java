package main.model;

public enum UserRole {
    USER,
    ADMIN;

    public static UserRole fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER; // valore di default
        }
    }

    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
