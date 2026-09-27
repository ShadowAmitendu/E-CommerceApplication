package com.example.api.exception;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a structured error response returned to clients.
 *
 * <p>What's happening here:
 * This class provides a uniform JSON error response format for all exception types handled by
 * {@link GlobalExceptionHandler}. Instead of returning raw exception messages as plain text,
 * the API consistently returns a structured JSON body containing HTTP status, error description,
 * detail message, and a timestamp.
 *
 * <p>What is done:
 * <ul>
 *   <li>Encapsulates the HTTP status code, error label, descriptive message, and occurrence timestamp.</li>
 *   <li>Provides getter and setter methods for JSON serialization by Jackson.</li>
 * </ul>
 */
public class ErrorResponse {

    /**
     * The HTTP status code (e.g., 401, 403, 404, 409).
     */
    private int status;

    /**
     * A short error label describing the status (e.g., "Not Found", "Unauthorized").
     */
    private String error;

    /**
     * A detailed human-readable message explaining the error cause.
     */
    private String message;

    /**
     * The timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * Constructs a fully populated ErrorResponse.
     *
     * @param status    The HTTP status code.
     * @param error     The short error label.
     * @param message   The detailed error message.
     * @param timestamp The time of occurrence.
     */
    public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Gets the HTTP status code.
     *
     * @return The HTTP status code.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the HTTP status code.
     *
     * @param status The HTTP status code.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the short error label.
     *
     * @return The error label string.
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the short error label.
     *
     * @param error The error label string.
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Gets the detailed error message.
     *
     * @return The error message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the detailed error message.
     *
     * @param message The error message string.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the error occurrence timestamp.
     *
     * @return The {@link LocalDateTime} when the error occurred.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the error occurrence timestamp.
     *
     * @param timestamp The {@link LocalDateTime} when the error occurred.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
