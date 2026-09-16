package com.vityarthi.smartcampus.service;

import com.vityarthi.smartcampus.exception.AuthenticationException;
import com.vityarthi.smartcampus.model.SystemRole;
import com.vityarthi.smartcampus.model.User;
import com.vityarthi.smartcampus.repository.DataStore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Service managing user authentication, credential validation, and security hashing.
 * Demonstrates Security Non-Functional Requirement and Service-Oriented Architecture.
 */
public class AuthenticationService {
    private final DataStore dataStore;
    private User currentUserSession;

    public AuthenticationService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public User authenticate(String loginIdentifier, String rawPassword) {
        if (loginIdentifier == null || loginIdentifier.trim().isEmpty()) {
            throw new AuthenticationException("Login identifier (RegNo / EmpId / Email) cannot be empty.");
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty.");
        }

        String id = loginIdentifier.trim();
        Optional<User> userOpt = dataStore.findUserById(id);
        if (userOpt.isEmpty()) {
            userOpt = dataStore.findUserByEmail(id);
        }

        if (userOpt.isEmpty()) {
            throw new AuthenticationException("User account not found: " + id);
        }

        User user = userOpt.get();
        // Check password (supports raw match or SHA-256 hash match)
        String storedHash = user.getPasswordHash();
        if (!storedHash.equals(rawPassword) && !storedHash.equalsIgnoreCase(hashPassword(rawPassword))) {
            throw new AuthenticationException("Invalid password credentials for user: " + id);
        }

        this.currentUserSession = user;
        return user;
    }

    public void logout() {
        this.currentUserSession = null;
    }

    public Optional<User> getCurrentUserSession() {
        return Optional.ofNullable(currentUserSession);
    }

    public boolean hasRole(SystemRole requiredRole) {
        return currentUserSession != null && currentUserSession.getRole() == requiredRole;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback
        }
    }
}
