package com.vityarthi.smartcampus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete subclass representing a Faculty instructor.
 * Demonstrates Polymorphism and Domain Encapsulation.
 */
public class Faculty extends User {
    private final String employeeId;
    private final String department;
    private final String cabin;
    private final List<String> assignedCourseCodes;

    public Faculty(String userId, String fullName, String email, String passwordHash,
                   String employeeId, String department, String cabin) {
        super(userId, fullName, email, passwordHash, SystemRole.FACULTY);
        this.employeeId = employeeId;
        this.department = department;
        this.cabin = cabin;
        this.assignedCourseCodes = new ArrayList<>();
    }

    @Override
    public String getRoleSpecificDetails() {
        return String.format("EmpId: %s | Dept: %s | Cabin: %s | Assigned Courses: %d",
                employeeId, department, cabin, assignedCourseCodes.size());
    }

    public void assignCourse(String courseCode) {
        if (!assignedCourseCodes.contains(courseCode)) {
            assignedCourseCodes.add(courseCode);
        }
    }

    public void unassignCourse(String courseCode) {
        assignedCourseCodes.remove(courseCode);
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public String getCabin() {
        return cabin;
    }

    public List<String> getAssignedCourseCodes() {
        return Collections.unmodifiableList(assignedCourseCodes);
    }
}
