package com.onion.backend.common.exception;

import lombok.Getter;

@Getter
public class CustomAuthenticationException extends RuntimeException {

    private String error;

    public CustomAuthenticationException(String message, String error, Throwable cause) {
        super(message, cause);
        this.error = error;
    }

    public CustomAuthenticationException(String msg) {
        super(msg);
    }

    public CustomAuthenticationException(String message, String error) {
        super(message);
        this.error = error;
    }
}
