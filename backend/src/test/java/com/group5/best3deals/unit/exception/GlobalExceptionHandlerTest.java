package com.group5.best3deals.unit.exception;
import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.exception.GlobalExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolation<?> constraintViolation;

    @Mock
    private Path path;

    @Test
    void handleNoSuchElementException() {
        // Arrange
        String errorMessage = "Element not found";
        NoSuchElementException exception = new NoSuchElementException(errorMessage);

        // Act
        ResponseEntity<ApiResponse<String>> response =
                globalExceptionHandler.handleNoSuchElementException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorMessage, response.getBody().getMessage()); // Changed from getData() to getMessage()
    }

    @Test
    void handleIllegalArgumentException() {
        // Arrange
        String errorMessage = "Invalid argument";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<ApiResponse<String>> response =
                globalExceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorMessage, response.getBody().getMessage()); // Changed from getData() to getMessage()
    }

    @Test
    void handleMethodArgumentNotValidException() {
        // Arrange
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        FieldError fieldError = new FieldError(
                "objectName",
                "fieldName",
                "defaultMessage"
        );
        when(bindingResult.getFieldErrors())
                .thenReturn(Collections.singletonList(fieldError));

        // Act
        ResponseEntity<Map<String, String>> response =
                globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("fieldName", "defaultMessage");
        assertEquals(expectedErrors, response.getBody());
    }

    @Test
    void handleConstraintViolationException() {
        // Arrange
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        when(constraintViolation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("propertyPath");
        when(constraintViolation.getMessage()).thenReturn("message");
        violations.add(constraintViolation);

        ConstraintViolationException exception =
                new ConstraintViolationException("message", violations);

        // Act
        ResponseEntity<Map<String, String>> response =
                globalExceptionHandler.handleConstraintViolationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("propertyPath", "message");
        assertEquals(expectedErrors, response.getBody());
    }

    @Test
    void handleGlobalExceptions() {
        // Arrange
        String errorMessage = "Internal server error";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<String> response =
                globalExceptionHandler.handleGlobalExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleEmptyConstraintViolationException() {
        // Arrange
        ConstraintViolationException exception =
                new ConstraintViolationException("message", Collections.emptySet());

        // Act
        ResponseEntity<Map<String, String>> response =
                globalExceptionHandler.handleConstraintViolationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void handleEmptyMethodArgumentNotValidException() {
        // Arrange
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<Map<String, String>> response =
                globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}