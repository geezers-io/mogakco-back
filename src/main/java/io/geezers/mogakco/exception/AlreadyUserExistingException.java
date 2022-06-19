package io.geezers.mogakco.exception;

public class AlreadyUserExistingException extends RuntimeException {
    public AlreadyUserExistingException(String message) {
        super(message);
    }
}
