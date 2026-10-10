package com.krishi.portfolio.devhub.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(NotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("status", 404, "error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        Map<String, String> fields = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> fields.put(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(Map.of("status", 400, "error", "Validation failed", "fields", fields));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> conflict(ConflictException e) {
        return ResponseEntity.status(409).body(Map.of("status", 409, "error", e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> unreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("status", 400, "error", "Malformed or invalid JSON request body"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> fallback(Exception e) {
        if (e instanceof ErrorResponse er) { // Spring's own 404 / 405 / 415, keep their status
            int code = er.getStatusCode().value();
            HttpStatus s = HttpStatus.resolve(code);
            return ResponseEntity.status(code)
                    .body(Map.of("status", code, "error", s != null ? s.getReasonPhrase() : "Request failed"));
        }
        log.error("Unexpected error", e);
        return ResponseEntity.status(500)
                .body(Map.of("status", 500, "error", "Internal server error"));
    }
}
