package model.exceptions;

public class AuthServiceException extends RuntimeException {
    public AuthServiceException() {
        super();
    }

    public AuthServiceException(String message) {
        super(message);
    }
}
