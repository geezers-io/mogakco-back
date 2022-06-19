package io.geezers.mogakco.security.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginArgumentNotValidException extends AuthenticationException {

    public LoginArgumentNotValidException(String msg) {
        super(msg);
    }
}
