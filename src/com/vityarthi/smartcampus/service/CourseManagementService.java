package com.vityarthi.smartcampus.service;

import com.vityarthi.smartcampus.exception.CampusException;
import com.vityarthi.smartcampus.exception.EntityNotFoundException;
import com.vityarthi.smartcampus.model.Course;
import com.vityarthi.smartcampus.repository.DataStore;
import com.vityarthi.smartcampus.util.InputValidator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service orchestrating course catalog administration, curriculum queries, and seat availability.
 */
public class CourseManagementService {
    private final DataStore dataStore;

    public CourseManagementService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void registerNewCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null.");
        }
        if (!InputValidator.isValidCourseCode(course.getCourseCode())) {
            throw new CampusException("Invalid course code format: " + course.getCourseCode() + ". Must match format e.g. CSE2006");
        }
        if (dataStore.findCourseByCode(course.getCourseCode()).isPresent()) {
            throw new CampusException("Course with code '" + course.getCourseCode() + "' already exists in the catalog.");
        }
        dataStore.saveCourse(course);
        dataStore.persistAll();
    }

    public Course getCourseByCode(String courseCode) {
        return dataStore.findCourseByCode(courseCode)
                .orElseThrow(() -> new EntityNotFoundException("Course", courseCode));
    }

    public Optional<Course> findCourse(String courseCode) {
        return dataStore.findCourseByCode(courseCode);
    }

    public Collection<Course> getAllCourses() {
        return dataStore.getAllCourses();
    }

    public List<Course> searchCourses(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.copyOf(dataStore.getAllCourses());
        }
        String q = query.trim().toLowerCase();
        return dataStore.getAllCourses().stream()
                .filter(c -> c.getCourseCode().toLowerCase().contains(q) ||
                             c.getCourseTitle().toLowerCase().contains(q) ||
                             c.getSlot().getSlotCode().toLowerCase().contains(q))
                .toList();
    }

    public void removeCourse(String courseCode) {
        Course course = getCourseByCode(courseCode);
        if (!course.getEnrolledStudentIds().isEmpty()) {
            throw new CampusException("Cannot delete course '" + courseCode + "' because students are actively enrolled in it.");
        }
        dataStore.deleteCourse(courseCode);
        dataStore.persistAll();
    }
}
