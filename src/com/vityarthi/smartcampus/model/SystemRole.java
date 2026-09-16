package com.vityarthi.smartcampus.model;

/**
 * Enumeration representing distinct user roles with role-based access control (RBAC).
 */
public enum SystemRole {
    STUDENT("Student"),
    FACULTY("Faculty Member"),
    ADMIN("System Administrator");

    private final String displayName;

    SystemRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
