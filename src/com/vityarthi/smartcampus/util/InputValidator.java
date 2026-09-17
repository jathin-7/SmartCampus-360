package com.vityarthi.smartcampus.util;

import java.util.regex.Pattern;

/**
 * Validates registration numbers, course codes, and emails using regular expressions.
 */
public final class InputValidator {
    // Matches VIT Registration Format e.g., 25BAI10611
    private static final Pattern REG_NO_PATTERN = Pattern.compile("^[0-9]{2}[A-Z]{3}[0-9]{4,5}$");
    
    // Matches standard university course code e.g., CSE2006
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z]{3}[0-9]{4}$");
    
    // Standard Email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private InputValidator() {
        // Prevent instantiation
    }

    public static boolean isValidRegNo(String regNo) {
        if (regNo == null) return false;
        return REG_NO_PATTERN.matcher(regNo.trim().toUpperCase()).matches();
    }

    public static boolean isValidCourseCode(String courseCode) {
        if (courseCode == null) return false;
        return COURSE_CODE_PATTERN.matcher(courseCode.trim().toUpperCase()).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositive(int number) {
        return number > 0;
    }
}
