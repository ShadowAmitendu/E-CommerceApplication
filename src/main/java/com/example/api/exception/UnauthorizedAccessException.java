package com.example.api.exception;

/**
 * Exception thrown when an authenticated user lacks the required role or permissions.
 *
 * <p>What's happening here:
 * This exception replaces generic {@link RuntimeException} usage for authorization failures.
 * It is caught by {@link GlobalExceptionHandler} and mapped to an HTTP 403 Forbidden response.
 *
 * <p>What is done:
 * <ul>
 *   <li>Extends {@link RuntimeException} for unchecked exception behavior.</li>
 *   <li>Provides a constructor accepting a descriptive error message.</li>
 * </ul>
 */
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * Constructs an UnauthorizedAccessException with the specified detail message.
     *
     * @param message The detail message explaining why access was denied.
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
