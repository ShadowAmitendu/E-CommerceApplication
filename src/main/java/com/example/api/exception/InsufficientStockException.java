package com.example.api.exception;

/**
 * Exception thrown when an order cannot be fulfilled due to insufficient product inventory.
 *
 * <p>What's happening here:
 * This exception replaces generic {@link RuntimeException} usage for stock shortage scenarios.
 * It is caught by {@link GlobalExceptionHandler} and mapped to an HTTP 409 Conflict response.
 *
 * <p>What is done:
 * <ul>
 *   <li>Extends {@link RuntimeException} for unchecked exception behavior.</li>
 *   <li>Provides a constructor accepting a descriptive error message.</li>
 * </ul>
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * Constructs an InsufficientStockException with the specified detail message.
     *
     * @param message The detail message describing the stock shortage.
     */
    public InsufficientStockException(String message) {
        super(message);
    }
}
