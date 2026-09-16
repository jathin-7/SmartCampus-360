package com.vityarthi.smartcampus.service;

import com.vityarthi.smartcampus.concurrency.NotificationWorker;
import com.vityarthi.smartcampus.exception.*;
import com.vityarthi.smartcampus.model.*;
import com.vityarthi.smartcampus.repository.DataStore;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core business engine orchestrating Course Registration, Timetable Collision Detection,
 * Waitlist Management, Attendance Tracking, and Dynamic CGPA Calculation.
 */
public class EnrollmentService {
    private final DataStore dataStore;
    private final NotificationWorker notificationWorker;
    private final AtomicInteger enrollmentIdCounter = new AtomicInteger(5000);

    public EnrollmentService(DataStore dataStore, NotificationWorker notificationWorker) {
        this.dataStore = dataStore;
        this.notificationWorker = notificationWorker;
    }

    /**
     * Registers a student for a course enforcing prerequisite rules, credit boundaries,
     * and slot clash detection.
     */
    public synchronized Enrollment registerCourse(String studentRegNo, String courseCode) {
        User user = dataStore.findUserById(studentRegNo)
                .orElseThrow(() -> new EntityNotFoundException("Student", studentRegNo));

        if (!(user instanceof Student student)) {
            throw new CampusException("Identifier '" + studentRegNo + "' does not belong to a Student account.");
        }

        Course targetCourse = dataStore.findCourseByCode(courseCode)
                .orElseThrow(() -> new EntityNotFoundException("Course", courseCode));

        // Check if already registered or waitlisted
        Optional<Enrollment> existingOpt = dataStore.findEnrollment(studentRegNo, targetCourse.getCourseCode());
        if (existingOpt.isPresent() &&
                (existingOpt.get().getStatus() == Enrollment.EnrollmentStatus.ENROLLED ||
                 existingOpt.get().getStatus() == Enrollment.EnrollmentStatus.WAITLISTED)) {
            throw new CampusException("Student is already registered or waitlisted for course: " + targetCourse.getCourseCode());
        }

        // 1. Check Prerequisites
        for (String prereqCode : targetCourse.getPrerequisites()) {
            Optional<Enrollment> prereqEnr = dataStore.findEnrollment(studentRegNo, prereqCode);
            boolean passed = prereqEnr.isPresent() &&
                             prereqEnr.get().getStatus() == Enrollment.EnrollmentStatus.COMPLETED &&
                             prereqEnr.get().getGrade().isPass();
            if (!passed) {
                throw new CampusException(String.format("Cannot register for '%s': Prerequisite '%s' has not been successfully completed.",
                        targetCourse.getCourseCode(), prereqCode));
            }
        }

        // 2. Check Credit Limit
        if (!student.canAddCredits(targetCourse.getCredits())) {
            throw new CreditLimitExceededException(student.getCurrentCredits(), targetCourse.getCredits(), student.getMaxCredits());
        }

        // 3. Timetable Slot Clash Detection
        Collection<Enrollment> activeEnrollments = dataStore.getEnrollmentsByStudent(studentRegNo).stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED)
                .toList();

        for (Enrollment active : activeEnrollments) {
            Optional<Course> enrolledCourseOpt = dataStore.findCourseByCode(active.getCourseCode());
            if (enrolledCourseOpt.isPresent()) {
                Course existingCourse = enrolledCourseOpt.get();
                if (existingCourse.getSlot().clashesWith(targetCourse.getSlot())) {
                    throw new SlotClashException(existingCourse.getCourseCode(), targetCourse.getCourseCode(), targetCourse.getSlot().getSlotCode());
                }
            }
        }

        // 4. Capacity & Waitlisting Logic
        String enrollmentId = "ENR-" + enrollmentIdCounter.incrementAndGet();
        Enrollment enrollment;

        if (!targetCourse.isFull()) {
            targetCourse.enrollStudent(studentRegNo);
            student.registerCourseCode(targetCourse.getCourseCode());
            student.addCredits(targetCourse.getCredits());

            enrollment = new Enrollment(enrollmentId, studentRegNo, targetCourse.getCourseCode(), Enrollment.EnrollmentStatus.ENROLLED);
            notificationWorker.dispatchNotification(studentRegNo, "Successfully enrolled in " + targetCourse.getCourseCode());
        } else {
            targetCourse.enrollStudent(studentRegNo); // Places into waitlist queue
            enrollment = new Enrollment(enrollmentId, studentRegNo, targetCourse.getCourseCode(), Enrollment.EnrollmentStatus.WAITLISTED);
            notificationWorker.dispatchNotification(studentRegNo, "Course " + targetCourse.getCourseCode() + " is full. Placed on waitlist at position #" + targetCourse.getWaitlistSize());
        }

        dataStore.saveEnrollment(enrollment);
        dataStore.persistAll();
        return enrollment;
    }

    /**
     * Drops a course, updates credits, and automatically promotes the next waitlisted student.
     */
    public synchronized boolean dropCourse(String studentRegNo, String courseCode) {
        User user = dataStore.findUserById(studentRegNo)
                .orElseThrow(() -> new EntityNotFoundException("Student", studentRegNo));
        if (!(user instanceof Student student)) {
            throw new CampusException("User is not a student.");
        }

        Course course = dataStore.findCourseByCode(courseCode)
                .orElseThrow(() -> new EntityNotFoundException("Course", courseCode));

        Optional<Enrollment> enrOpt = dataStore.findEnrollment(studentRegNo, courseCode);
        if (enrOpt.isEmpty()) {
            throw new CampusException("No active enrollment found for " + courseCode);
        }

        Enrollment enr = enrOpt.get();
        boolean wasEnrolled = (enr.getStatus() == Enrollment.EnrollmentStatus.ENROLLED);
        enr.setStatus(Enrollment.EnrollmentStatus.DROPPED);

        if (wasEnrolled) {
            student.removeCourseCode(courseCode);
            student.deductCredits(course.getCredits());
        }

        // Promote waitlisted student if available
        String promotedRegNo = course.removeStudent(studentRegNo);
        if (promotedRegNo != null) {
            Optional<Enrollment> promotedEnr = dataStore.findEnrollment(promotedRegNo, courseCode);
            if (promotedEnr.isPresent()) {
                promotedEnr.get().setStatus(Enrollment.EnrollmentStatus.ENROLLED);
                User promotedUser = dataStore.findUserById(promotedRegNo).orElse(null);
                if (promotedUser instanceof Student promotedStudent) {
                    promotedStudent.registerCourseCode(courseCode);
                    promotedStudent.addCredits(course.getCredits());
                }
                notificationWorker.dispatchNotification(promotedRegNo,
                        "Congratulations! A seat opened up in " + courseCode + ". You have been promoted from waitlist to ENROLLED.");
            }
        }

        dataStore.persistAll();
        return true;
    }

    /**
     * Records lecture attendance for an enrolled student.
     */
    public void recordAttendance(String studentRegNo, String courseCode, boolean present) {
        Enrollment enr = dataStore.findEnrollment(studentRegNo, courseCode)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment", studentRegNo + "-" + courseCode));
        if (enr.getStatus() != Enrollment.EnrollmentStatus.ENROLLED) {
            throw new CampusException("Cannot mark attendance: Student is not actively enrolled in this course.");
        }
        enr.recordAttendance(present);
        dataStore.persistAll();
    }

    /**
     * Assigns final course grade and dynamically updates cumulative GPA.
     */
    public void assignGrade(String studentRegNo, String courseCode, Grade grade) {
        Enrollment enr = dataStore.findEnrollment(studentRegNo, courseCode)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment", studentRegNo + "-" + courseCode));

        enr.setGrade(grade);
        enr.setStatus(Enrollment.EnrollmentStatus.COMPLETED);

        // Recalculate CGPA
        recalculateCgpa(studentRegNo);
        dataStore.persistAll();
    }

    public double recalculateCgpa(String studentRegNo) {
        User user = dataStore.findUserById(studentRegNo).orElse(null);
        if (!(user instanceof Student student)) return 0.0;

        Collection<Enrollment> enrollments = dataStore.getEnrollmentsByStudent(studentRegNo);
        double totalWeightedPoints = 0.0;
        int totalGradedCredits = 0;

        for (Enrollment e : enrollments) {
            if (e.getGrade() != null && e.getGrade() != Grade.NOT_ASSIGNED) {
                Optional<Course> c = dataStore.findCourseByCode(e.getCourseCode());
                if (c.isPresent()) {
                    int credits = c.get().getCredits();
                    totalWeightedPoints += (e.getGrade().getGradePoints() * credits);
                    totalGradedCredits += credits;
                }
            }
        }

        double cgpa = totalGradedCredits > 0 ? (totalWeightedPoints / totalGradedCredits) : 0.0;
        student.setCgpa(cgpa);
        return cgpa;
    }
}
