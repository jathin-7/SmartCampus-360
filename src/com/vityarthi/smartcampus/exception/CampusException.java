package com.vityarthi.smartcampus.exception;

/**
 * Base unchecked exception for all SmartCampus domain-specific errors.
 */
public class CampusException extends RuntimeException {
    public CampusException(String message) {
        super(message);
    }

    public CampusException(String message, Throwable cause) {
        super(message, cause);
    }
}
