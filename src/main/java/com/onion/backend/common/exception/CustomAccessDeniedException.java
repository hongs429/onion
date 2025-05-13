package com.onion.backend.common.exception;

import lombok.Getter;

@Getter
public class CustomAccessDeniedException extends RuntimeException {

    private String error;

    public CustomAccessDeniedException(String msg) {
        super(msg);
    }

    public CustomAccessDeniedException(String msg, String error) {
        super(msg);
        this.error = error;
    }
}
