package com.vityarthi.smartcampus.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class representing a registered campus user.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class User implements Comparable<User> {
    private final String userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private final SystemRole role;
    private final LocalDateTime registeredAt;

    public User(String userId, String fullName, String email, String passwordHash, SystemRole role) {
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash cannot be null");
        this.role = Objects.requireNonNull(role, "role cannot be null");
        this.registeredAt = LocalDateTime.now();
    }

    // Abstract method to be specialized by subclasses (Polymorphism)
    public abstract String getRoleSpecificDetails();

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public SystemRole getRole() {
        return role;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    @Override
    public int compareTo(User other) {
        return this.fullName.compareToIgnoreCase(other.fullName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", role, fullName, userId, email);
    }
}
