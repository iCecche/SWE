package model;

public enum OrderStatus {
    PENDING,
    SHIPPED,
    DELIVERED;

    public static OrderStatus fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING; // valore di default
        }
    }

    @Override
    public String toString() {
        return name().toUpperCase();
    }

}
