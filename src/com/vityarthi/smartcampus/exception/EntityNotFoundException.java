package com.vityarthi.smartcampus.exception;

/**
 * Thrown when a requested course, student, faculty, or record is not found in the system.
 */
public class EntityNotFoundException extends CampusException {
    public EntityNotFoundException(String entityType, String identifier) {
        super(String.format("%s with identifier '%s' was not found.", entityType, identifier));
    }
}
