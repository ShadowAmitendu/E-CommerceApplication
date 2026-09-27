package com.example.api.exception;

/**
 * Exception thrown when a requested resource (user, product, or order) cannot be found in the database.
 *
 * <p>What's happening here:
 * This exception replaces generic {@link RuntimeException} and {@link java.util.NoSuchElementException} usage
 * for missing entity lookups. It is caught by {@link GlobalExceptionHandler} and mapped to an HTTP 404 Not Found response.
 *
 * <p>What is done:
 * <ul>
 *   <li>Extends {@link RuntimeException} for unchecked exception behavior.</li>
 *   <li>Provides a constructor accepting a descriptive error message.</li>
 * </ul>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message identifying the missing resource.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
