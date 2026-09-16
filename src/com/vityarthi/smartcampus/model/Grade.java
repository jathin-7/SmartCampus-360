package com.vityarthi.smartcampus.model;

/**
 * Standard 10-point academic grading scale.
 */
public enum Grade {
    S(10.0, "Outstanding"),
    A(9.0, "Excellent"),
    B(8.0, "Very Good"),
    C(7.0, "Good"),
    D(6.0, "Fair"),
    E(4.0, "Pass"),
    F(0.0, "Fail"),
    NOT_ASSIGNED(-1.0, "In Progress");

    private final double gradePoints;
    private final String description;

    Grade(double gradePoints, String description) {
        this.gradePoints = gradePoints;
        this.description = description;
    }

    public double getGradePoints() {
        return gradePoints;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPass() {
        return this != F && this != NOT_ASSIGNED;
    }
}
