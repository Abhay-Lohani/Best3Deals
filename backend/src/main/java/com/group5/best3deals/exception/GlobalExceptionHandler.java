package com.group5.best3deals.exception;

import com.group5.best3deals.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

import java.util.NoSuchElementException;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<String>> handleNoSuchElementException(NoSuchElementException e) {
        ApiResponse<String> response = new ApiResponse<>(false, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // Respond with 404
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiResponse<String> response = new ApiResponse<>(false, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Handles @Valid validation errors (DTO validation errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handles ConstraintViolationException (used in path variables, query params)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handles other generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
