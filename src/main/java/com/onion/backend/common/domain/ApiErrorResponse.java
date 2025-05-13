package com.onion.backend.common.domain;

import lombok.Data;

@Data
public class ApiErrorResponse {

    private String status;
    private String error;
    private String message;

    private ApiErrorResponse(String error, String message, String status) {
        this.error = error;
        this.message = message;
        this.status = status;
    }

    public static ApiErrorResponse of(String error, String message, String status) {
        return new ApiErrorResponse(error, message, status);
    }
}
