package com.vityarthi.smartcampus.model;

/**
 * Concrete subclass representing a System Administrator.
 */
public class Admin extends User {
    private final String department;
    private final int clearanceLevel;

    public Admin(String userId, String fullName, String email, String passwordHash,
                 String department, int clearanceLevel) {
        super(userId, fullName, email, passwordHash, SystemRole.ADMIN);
        this.department = department;
        this.clearanceLevel = clearanceLevel;
    }

    @Override
    public String getRoleSpecificDetails() {
        return String.format("Admin Dept: %s | Clearance: Level %d", department, clearanceLevel);
    }

    public String getDepartment() {
        return department;
    }

    public int getClearanceLevel() {
        return clearanceLevel;
    }
}
