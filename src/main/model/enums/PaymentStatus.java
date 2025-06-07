package main.model.enums;

public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED;

    public static PaymentStatus fromString(String value) {
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
