package com.example.api.exception;

/**
 * Exception thrown when a request requires an authenticated session but none is present.
 *
 * <p>What's happening here:
 * This exception replaces generic {@link RuntimeException} usage for authentication failures.
 * It is caught by {@link GlobalExceptionHandler} and mapped to an HTTP 401 Unauthorized response.
 *
 * <p>What is done:
 * <ul>
 *   <li>Extends {@link RuntimeException} for unchecked exception behavior.</li>
 *   <li>Provides a constructor accepting a descriptive error message.</li>
 * </ul>
 */
public class AuthenticationRequiredException extends RuntimeException {

    /**
     * Constructs an AuthenticationRequiredException with the specified detail message.
     *
     * @param message The detail message explaining why authentication is required.
     */
    public AuthenticationRequiredException(String message) {
        super(message);
    }
}
