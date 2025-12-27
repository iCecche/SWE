package model.exceptions;

public class UserAlreadyDeleted extends RuntimeException {
    public UserAlreadyDeleted() {
        super();
    }

    public UserAlreadyDeleted(String message) {
        super(message);
    }
}
