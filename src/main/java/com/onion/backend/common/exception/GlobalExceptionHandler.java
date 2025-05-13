package com.onion.backend.common.exception;


import com.onion.backend.common.domain.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> illegalArgumentException(IllegalArgumentException e) {
        log.error("Global exception", e);
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of("", e.getMessage(), HttpStatus.BAD_REQUEST.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> customAccessDeniedException(CustomAccessDeniedException e) {
        log.error("Global exception", e);
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of("", e.getMessage(), HttpStatus.UNAUTHORIZED.toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorResponse);
    }

}
