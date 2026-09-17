package com.vityarthi.smartcampus.model;

import java.util.*;

/**
 * Abstract course model representing any academic subject offered on campus.
 */
public abstract class Course implements Comparable<Course> {
    private final String courseCode;
    private final String courseTitle;
    private final int credits;
    private Slot slot;
    private String facultyEmpId;
    private final int maxSeats;
    private final Set<String> enrolledStudentIds;
    private final Queue<String> waitlistQueue;
    private final List<String> prerequisites;

    public Course(String courseCode, String courseTitle, int credits, Slot slot,
                  String facultyEmpId, int maxSeats, List<String> prerequisites) {
        this.courseCode = Objects.requireNonNull(courseCode, "courseCode cannot be null").trim().toUpperCase();
        this.courseTitle = Objects.requireNonNull(courseTitle, "courseTitle cannot be null");
        this.credits = credits;
        this.slot = Objects.requireNonNull(slot, "slot cannot be null");
        this.facultyEmpId = facultyEmpId;
        this.maxSeats = maxSeats;
        this.enrolledStudentIds = new LinkedHashSet<>();
        this.waitlistQueue = new LinkedList<>();
        this.prerequisites = prerequisites != null ? new ArrayList<>(prerequisites) : new ArrayList<>();
    }

    /**
     * Polymorphic method to calculate course fees based on credit weighting and specialized resources.
     */
    public abstract double calculateTuitionFee(double baseRatePerCredit);

    /**
     * Returns the course type (e.g., Theory or Laboratory).
     */
    public abstract String getCourseType();

    public boolean isFull() {
        return enrolledStudentIds.size() >= maxSeats;
    }

    public int getAvailableSeats() {
        return Math.max(0, maxSeats - enrolledStudentIds.size());
    }

    public synchronized boolean enrollStudent(String studentId) {
        if (enrolledStudentIds.contains(studentId)) {
            return false; // Already enrolled
        }
        if (!isFull()) {
            enrolledStudentIds.add(studentId);
            waitlistQueue.remove(studentId);
            return true;
        }
        // Course is full, add to waitlist if not already present
        if (!waitlistQueue.contains(studentId)) {
            waitlistQueue.offer(studentId);
        }
        return false;
    }

    public synchronized String removeStudent(String studentId) {
        boolean removed = enrolledStudentIds.remove(studentId);
        waitlistQueue.remove(studentId);

        // If seat freed up, promote the next waitlisted student
        if (removed && !waitlistQueue.isEmpty()) {
            String promotedStudentId = waitlistQueue.poll();
            enrolledStudentIds.add(promotedStudentId);
            return promotedStudentId;
        }
        return null;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getCredits() {
        return credits;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public String getFacultyEmpId() {
        return facultyEmpId;
    }

    public void setFacultyEmpId(String facultyEmpId) {
        this.facultyEmpId = facultyEmpId;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public Set<String> getEnrolledStudentIds() {
        return Collections.unmodifiableSet(enrolledStudentIds);
    }

    public Queue<String> getWaitlistQueue() {
        return new LinkedList<>(waitlistQueue);
    }

    public int getWaitlistSize() {
        return waitlistQueue.size();
    }

    public List<String> getPrerequisites() {
        return Collections.unmodifiableList(prerequisites);
    }

    @Override
    public int compareTo(Course o) {
        return this.courseCode.compareTo(o.courseCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return Objects.equals(courseCode, course.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d Credits | Slot: %s | Seats: %d/%d | Waitlist: %d)",
                courseCode, courseTitle, credits, slot.getSlotCode(), enrolledStudentIds.size(), maxSeats, waitlistQueue.size());
    }
}
