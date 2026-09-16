package com.vityarthi.smartcampus.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity linking a Student to a Course with academic lifecycle state,
 * attendance tracking, and grading metrics.
 */
public class Enrollment {
    public enum EnrollmentStatus {
        ENROLLED,
        WAITLISTED,
        DROPPED,
        COMPLETED
    }

    private final String enrollmentId;
    private final String studentRegNo;
    private final String courseCode;
    private EnrollmentStatus status;
    private final LocalDateTime registeredAt;
    private int attendedClasses;
    private int totalClasses;
    private Grade grade;

    public Enrollment(String enrollmentId, String studentRegNo, String courseCode, EnrollmentStatus status) {
        this.enrollmentId = Objects.requireNonNull(enrollmentId, "enrollmentId cannot be null");
        this.studentRegNo = Objects.requireNonNull(studentRegNo, "studentRegNo cannot be null");
        this.courseCode = Objects.requireNonNull(courseCode, "courseCode cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.registeredAt = LocalDateTime.now();
        this.attendedClasses = 0;
        this.totalClasses = 0;
        this.grade = Grade.NOT_ASSIGNED;
    }

    public Enrollment(String enrollmentId, String studentRegNo, String courseCode,
                      EnrollmentStatus status, int attendedClasses, int totalClasses, Grade grade) {
        this.enrollmentId = enrollmentId;
        this.studentRegNo = studentRegNo;
        this.courseCode = courseCode;
        this.status = status;
        this.registeredAt = LocalDateTime.now();
        this.attendedClasses = attendedClasses;
        this.totalClasses = totalClasses;
        this.grade = grade != null ? grade : Grade.NOT_ASSIGNED;
    }

    public void recordAttendance(boolean present) {
        this.totalClasses++;
        if (present) {
            this.attendedClasses++;
        }
    }

    public double getAttendancePercentage() {
        if (totalClasses == 0) return 100.0;
        return (attendedClasses * 100.0) / totalClasses;
    }

    public boolean isAttendanceDebarred() {
        // VIT 75% attendance rule
        return totalClasses > 0 && getAttendancePercentage() < 75.0;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment that)) return false;
        return Objects.equals(enrollmentId, that.enrollmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
    }

    @Override
    public String toString() {
        return String.format("[%s] Student: %s | Course: %s | Status: %s | Attd: %.1f%% (%d/%d) | Grade: %s",
                enrollmentId, studentRegNo, courseCode, status, getAttendancePercentage(), attendedClasses, totalClasses, grade);
    }
}
