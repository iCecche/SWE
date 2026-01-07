package model.exceptions;

public class ProductServiceException extends RuntimeException {
    public ProductServiceException() {
        super();
    }

    public ProductServiceException(String message) {
        super(message);
    }
}
