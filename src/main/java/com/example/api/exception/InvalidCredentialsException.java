package com.example.api.exception;

/**
 * Exception thrown when a user provides incorrect login credentials (e.g., wrong password).
 *
 * <p>What's happening here:
 * This exception replaces generic {@link RuntimeException} usage for credential validation failures.
 * It is caught by {@link GlobalExceptionHandler} and mapped to an HTTP 401 Unauthorized response.
 *
 * <p>What is done:
 * <ul>
 *   <li>Extends {@link RuntimeException} for unchecked exception behavior.</li>
 *   <li>Provides a constructor accepting a descriptive error message.</li>
 * </ul>
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructs an InvalidCredentialsException with the specified detail message.
     *
     * @param message The detail message explaining the credential failure.
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
