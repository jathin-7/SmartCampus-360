package com.vityarthi.smartcampus.repository;

import com.vityarthi.smartcampus.model.*;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface defining persistence operations for users, courses, and registrations.
 */
public interface DataStore {
    // User operations
    void saveUser(User user);
    Optional<User> findUserById(String userId);
    Optional<User> findUserByEmail(String email);
    Collection<User> getAllUsers();

    // Course operations
    void saveCourse(Course course);
    Optional<Course> findCourseByCode(String courseCode);
    Collection<Course> getAllCourses();
    void deleteCourse(String courseCode);

    // Enrollment operations
    void saveEnrollment(Enrollment enrollment);
    Optional<Enrollment> findEnrollment(String studentRegNo, String courseCode);
    Collection<Enrollment> getEnrollmentsByStudent(String studentRegNo);
    Collection<Enrollment> getEnrollmentsByCourse(String courseCode);
    Collection<Enrollment> getAllEnrollments();

    // Persistence lifecycle
    void persistAll();
    void loadAll();
}
