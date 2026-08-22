package com.example.usermanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiException> handleNotFound(UserNotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ApiException> handleEmailConflict(DuplicatedEmailException exception,
                                                            HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, exception.getMessage(), request,
                Map.of("email", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleValidation(MethodArgumentNotValidException exception,
                                                         HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiException> handleConflict(DataIntegrityViolationException exception,
                                                       HttpServletRequest request) {
        log.warn("Database constraint violation", exception);
        String message = "Email is already registered";
        return response(HttpStatus.CONFLICT, message, request, Map.of("email", message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiException> handleUnexpected(RuntimeException exception, HttpServletRequest request) {
        log.error("Unexpected backend error", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, Map.of());
    }

    private ResponseEntity<ApiException> response(HttpStatus status, String message, HttpServletRequest request,
                                                  Map<String, String> fieldErrors) {
        ApiException body = new ApiException(Instant.now(), status.value(), message, request.getRequestURI(), fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
