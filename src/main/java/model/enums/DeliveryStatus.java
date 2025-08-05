package model.enums;

public enum DeliveryStatus {
    PENDING,
    SHIPPED,
    DELIVERED;

    public static DeliveryStatus fromString(String value) {
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
