package com.example.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler providing centralized error response management for all controllers.
 *
 * <p>What's happening here:
 * Annotated with {@link ControllerAdvice}, this class intercepts exceptions thrown by any
 * {@code @RestController} in the application and converts them into structured JSON
 * {@link ErrorResponse} objects with appropriate HTTP status codes.
 *
 * <p>What is done:
 * <ul>
 *   <li>Handles {@link AuthenticationRequiredException} → HTTP 401 Unauthorized.</li>
 *   <li>Handles {@link InvalidCredentialsException} → HTTP 401 Unauthorized.</li>
 *   <li>Handles {@link UnauthorizedAccessException} → HTTP 403 Forbidden.</li>
 *   <li>Handles {@link ResourceNotFoundException} → HTTP 404 Not Found.</li>
 *   <li>Handles {@link InsufficientStockException} → HTTP 409 Conflict.</li>
 *   <li>Handles {@link IllegalArgumentException} → HTTP 400 Bad Request.</li>
 *   <li>Handles {@link MethodArgumentNotValidException} → HTTP 400 Bad Request (Bean Validation failures).</li>
 *   <li>Handles all uncaught exceptions → HTTP 500 Internal Server Error.</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles authentication-required exceptions when no active session exists.
     *
     * @param exception The thrown {@link AuthenticationRequiredException}.
     * @return A {@link ResponseEntity} with HTTP 401 status and structured error body.
     */
    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationRequired(AuthenticationRequiredException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles invalid credential exceptions during login attempts.
     *
     * @param exception The thrown {@link InvalidCredentialsException}.
     * @return A {@link ResponseEntity} with HTTP 401 status and structured error body.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles unauthorized access exceptions when a user lacks required permissions.
     *
     * @param exception The thrown {@link UnauthorizedAccessException}.
     * @return A {@link ResponseEntity} with HTTP 403 status and structured error body.
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(UnauthorizedAccessException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles resource-not-found exceptions for missing users, products, or orders.
     *
     * @param exception The thrown {@link ResourceNotFoundException}.
     * @return A {@link ResponseEntity} with HTTP 404 status and structured error body.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles insufficient stock exceptions when order quantities exceed available inventory.
     *
     * @param exception The thrown {@link InsufficientStockException}.
     * @return A {@link ResponseEntity} with HTTP 409 status and structured error body.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles illegal argument exceptions for invalid input parameters.
     *
     * @param exception The thrown {@link IllegalArgumentException}.
     * @return A {@link ResponseEntity} with HTTP 400 status and structured error body.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Bean Validation failures triggered by {@code @Valid} annotations on request bodies.
     *
     * <p>What's happening:
     * Collects all field-level validation error messages from the {@link MethodArgumentNotValidException}
     * and joins them into a single comma-separated string for the response body.
     *
     * @param exception The thrown {@link MethodArgumentNotValidException} containing binding results.
     * @return A {@link ResponseEntity} with HTTP 400 status and structured error body listing all validation failures.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception) {
        // Collect all field validation error messages into a single descriptive string
        String validationMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((first, second) -> first + "; " + second)
                .orElse("Validation failed");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                validationMessages,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catch-all handler for any unhandled exceptions not matched by specific handlers above.
     *
     * @param exception The thrown {@link Exception}.
     * @return A {@link ResponseEntity} with HTTP 500 status and structured error body.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
