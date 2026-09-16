package com.vityarthi.smartcampus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete subclass representing a University Student.
 * Demonstrates Inheritance and Domain Encapsulation.
 */
public class Student extends User {
    private final String regNo;
    private final String program;
    private int semester;
    private final int maxCredits;
    private int currentCredits;
    private double cgpa;
    private final List<String> enrolledCourseCodes;

    public Student(String userId, String fullName, String email, String passwordHash,
                   String regNo, String program, int semester) {
        super(userId, fullName, email, passwordHash, SystemRole.STUDENT);
        this.regNo = regNo;
        this.program = program;
        this.semester = semester;
        this.maxCredits = 27; // Standard VIT FFCS limit
        this.currentCredits = 0;
        this.cgpa = 0.0;
        this.enrolledCourseCodes = new ArrayList<>();
    }

    public Student(String userId, String fullName, String email, String passwordHash,
                   String regNo, String program, int semester, int maxCredits) {
        super(userId, fullName, email, passwordHash, SystemRole.STUDENT);
        this.regNo = regNo;
        this.program = program;
        this.semester = semester;
        this.maxCredits = maxCredits;
        this.currentCredits = 0;
        this.cgpa = 0.0;
        this.enrolledCourseCodes = new ArrayList<>();
    }

    @Override
    public String getRoleSpecificDetails() {
        return String.format("RegNo: %s | Program: %s | Sem: %d | Credits: %d/%d | CGPA: %.2f",
                regNo, program, semester, currentCredits, maxCredits, cgpa);
    }

    public boolean canAddCredits(int credits) {
        return (this.currentCredits + credits) <= this.maxCredits;
    }

    public void addCredits(int credits) {
        this.currentCredits += credits;
    }

    public void deductCredits(int credits) {
        this.currentCredits = Math.max(0, this.currentCredits - credits);
    }

    public void registerCourseCode(String courseCode) {
        if (!enrolledCourseCodes.contains(courseCode)) {
            enrolledCourseCodes.add(courseCode);
        }
    }

    public void removeCourseCode(String courseCode) {
        enrolledCourseCodes.remove(courseCode);
    }

    public String getRegNo() {
        return regNo;
    }

    public String getProgram() {
        return program;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getMaxCredits() {
        return maxCredits;
    }

    public int getCurrentCredits() {
        return currentCredits;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public List<String> getEnrolledCourseCodes() {
        return Collections.unmodifiableList(enrolledCourseCodes);
    }
}
