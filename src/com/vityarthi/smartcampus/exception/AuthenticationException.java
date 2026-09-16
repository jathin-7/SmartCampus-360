package com.vityarthi.smartcampus.exception;

/**
 * Thrown when user authentication fails due to invalid credentials or unauthorized access.
 */
public class AuthenticationException extends CampusException {
    public AuthenticationException(String message) {
        super(message);
    }
}
