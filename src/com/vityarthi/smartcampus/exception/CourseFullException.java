package com.vityarthi.smartcampus.exception;

/**
 * Thrown when attempting to register for a course section that has reached maximum capacity.
 */
public class CourseFullException extends CampusException {
    private final String courseCode;
    private final int capacity;

    public CourseFullException(String courseCode, int capacity) {
        super(String.format("Course '%s' is full (Capacity: %d). Added to waitlist queue.", courseCode, capacity));
        this.courseCode = courseCode;
        this.capacity = capacity;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public int getCapacity() {
        return capacity;
    }
}
