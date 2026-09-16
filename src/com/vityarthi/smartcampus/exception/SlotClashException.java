package com.vityarthi.smartcampus.exception;

/**
 * Thrown when a student attempts to enroll in a course whose timetable slot conflicts
 * with an already enrolled course.
 */
public class SlotClashException extends CampusException {
    private final String existingCourseCode;
    private final String conflictingCourseCode;
    private final String slotCode;

    public SlotClashException(String existingCourseCode, String conflictingCourseCode, String slotCode) {
        super(String.format("Timetable clash on slot [%s]: Course '%s' clashes with already registered '%s'",
                slotCode, conflictingCourseCode, existingCourseCode));
        this.existingCourseCode = existingCourseCode;
        this.conflictingCourseCode = conflictingCourseCode;
        this.slotCode = slotCode;
    }

    public String getExistingCourseCode() {
        return existingCourseCode;
    }

    public String getConflictingCourseCode() {
        return conflictingCourseCode;
    }

    public String getSlotCode() {
        return slotCode;
    }
}
