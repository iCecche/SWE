package model.exceptions;

public class OrderBusinessException extends RuntimeException {
    public OrderBusinessException() {
        super();
    }
    public OrderBusinessException(String message) {
        super(message);
    }
}
