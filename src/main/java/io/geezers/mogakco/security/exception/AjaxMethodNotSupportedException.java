package io.geezers.mogakco.security.exception;

import org.springframework.security.core.AuthenticationException;

public class AjaxMethodNotSupportedException extends AuthenticationException {
    public AjaxMethodNotSupportedException(String message) {
        super(message);
    }
}
