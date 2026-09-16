package com.vityarthi.smartcampus.service;

import com.vityarthi.smartcampus.model.*;
import com.vityarthi.smartcampus.repository.DataStore;

import java.util.Collection;
import java.util.List;

/**
 * Service providing academic analytics, grade transcripts, attendance audits,
 * and polymorphic fee computations.
 */
public class AnalyticsReportingService {
    private final DataStore dataStore;

    public AnalyticsReportingService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Computes the total academic fee using runtime polymorphism.
     */
    public double calculateTotalTuitionFee(String studentRegNo, double baseRatePerCredit) {
        Collection<Enrollment> enrollments = dataStore.getEnrollmentsByStudent(studentRegNo);
        double total = 0.0;
        for (Enrollment e : enrollments) {
            if (e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED || e.getStatus() == Enrollment.EnrollmentStatus.COMPLETED) {
                Course course = dataStore.findCourseByCode(e.getCourseCode()).orElse(null);
                if (course != null) {
                    // Polymorphic invocation (TheoryCourse vs LabCourse)
                    total += course.calculateTuitionFee(baseRatePerCredit);
                }
            }
        }
        return total;
    }

    /**
     * Compiles a comprehensive student academic transcript.
     */
    public String generateStudentTranscript(String studentRegNo) {
        User user = dataStore.findUserById(studentRegNo).orElse(null);
        if (!(user instanceof Student student)) {
            return "Student record not found for: " + studentRegNo;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("                   VIT SMARTCAMPUS ACADEMIC TRANSCRIPT                  \n");
        sb.append("========================================================================\n");
        sb.append(String.format("Student Name   : %s\n", student.getFullName()));
        sb.append(String.format("Registration No: %s\n", student.getRegNo()));
        sb.append(String.format("Degree Program : %s\n", student.getProgram()));
        sb.append(String.format("Semester       : %d\n", student.getSemester()));
        sb.append(String.format("Enrolled Credits: %d / %d\n", student.getCurrentCredits(), student.getMaxCredits()));
        sb.append(String.format("Cumulative GPA : %.2f\n", student.getCgpa()));
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("%-10s %-32s %-6s %-8s %-10s %-8s\n",
                "CODE", "TITLE", "CRED", "SLOT", "ATTD %", "GRADE"));
        sb.append("------------------------------------------------------------------------\n");

        Collection<Enrollment> enrollments = dataStore.getEnrollmentsByStudent(studentRegNo);
        for (Enrollment e : enrollments) {
            Course c = dataStore.findCourseByCode(e.getCourseCode()).orElse(null);
            String title = c != null ? c.getCourseTitle() : "Unknown Course";
            if (title.length() > 30) title = title.substring(0, 27) + "...";
            int cred = c != null ? c.getCredits() : 0;
            String slot = c != null ? c.getSlot().getSlotCode() : "N/A";
            String attd = String.format("%.1f%%", e.getAttendancePercentage());
            String grade = e.getGrade() != Grade.NOT_ASSIGNED ? e.getGrade().name() : "IP";

            sb.append(String.format("%-10s %-32s %-6d %-8s %-10s %-8s\n",
                    e.getCourseCode(), title, cred, slot, attd, grade));
        }
        sb.append("========================================================================\n");
        return sb.toString();
    }

    /**
     * Compiles a campus-wide course allocation and slot occupancy summary.
     */
    public String generateCourseOccupancyReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                       SMARTCAMPUS COURSE CATALOG & ALLOCATION REPORT                   \n");
        sb.append("========================================================================================\n");
        sb.append(String.format("%-10s %-30s %-6s %-10s %-10s %-10s %-10s\n",
                "CODE", "TITLE", "CRED", "SLOT", "ENROLLED", "MAX SEATS", "WAITLIST"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (Course c : dataStore.getAllCourses()) {
            String title = c.getCourseTitle();
            if (title.length() > 28) title = title.substring(0, 25) + "...";
            sb.append(String.format("%-10s %-30s %-6d %-10s %-10d %-10d %-10d\n",
                    c.getCourseCode(), title, c.getCredits(), c.getSlot().getSlotCode(),
                    c.getEnrolledStudentIds().size(), c.getMaxSeats(), c.getWaitlistSize()));
        }
        sb.append("========================================================================================\n");
        return sb.toString();
    }

    /**
     * Lists students whose attendance is below the 75% threshold in any course.
     */
    public List<Enrollment> getAttendanceDebarmentAlerts() {
        return dataStore.getAllEnrollments().stream()
                .filter(Enrollment::isAttendanceDebarred)
                .toList();
    }
}
