package com.vityarthi.smartcampus.model;

import java.util.List;

/**
 * A standard theory subject comprising lecture and tutorial hours.
 */
public class TheoryCourse extends Course {
    private final int lectureHours;
    private final int tutorialHours;

    public TheoryCourse(String courseCode, String courseTitle, int credits, Slot slot,
                        String facultyEmpId, int maxSeats, List<String> prerequisites,
                        int lectureHours, int tutorialHours) {
        super(courseCode, courseTitle, credits, slot, facultyEmpId, maxSeats, prerequisites);
        this.lectureHours = lectureHours;
        this.tutorialHours = tutorialHours;
    }

    @Override
    public double calculateTuitionFee(double baseRatePerCredit) {
        // Standard tuition computation
        return getCredits() * baseRatePerCredit;
    }

    @Override
    public String getCourseType() {
        return "Theory Course (L: " + lectureHours + ", T: " + tutorialHours + ")";
    }

    public int getLectureHours() {
        return lectureHours;
    }

    public int getTutorialHours() {
        return tutorialHours;
    }
}
