package com.vityarthi.smartcampus.model;

import java.util.List;

/**
 * A practical laboratory subject with specialized software environments and extra lab fees.
 */
public class LabCourse extends Course {
    private final int practicalHours;
    private final String labSoftwareEnvironment;
    private final double labInfrastructureSurcharge;

    public LabCourse(String courseCode, String courseTitle, int credits, Slot slot,
                     String facultyEmpId, int maxSeats, List<String> prerequisites,
                     int practicalHours, String labSoftwareEnvironment, double labInfrastructureSurcharge) {
        super(courseCode, courseTitle, credits, slot, facultyEmpId, maxSeats, prerequisites);
        this.practicalHours = practicalHours;
        this.labSoftwareEnvironment = labSoftwareEnvironment;
        this.labInfrastructureSurcharge = labInfrastructureSurcharge;
    }

    @Override
    public double calculateTuitionFee(double baseRatePerCredit) {
        // Lab courses incorporate an additional laboratory infrastructure surcharge
        return (getCredits() * baseRatePerCredit) + labInfrastructureSurcharge;
    }

    @Override
    public String getCourseType() {
        return String.format("Laboratory Course (P: %dh/wk | Lab Env: %s)", practicalHours, labSoftwareEnvironment);
    }

    public int getPracticalHours() {
        return practicalHours;
    }

    public String getLabSoftwareEnvironment() {
        return labSoftwareEnvironment;
    }

    public double getLabInfrastructureSurcharge() {
        return labInfrastructureSurcharge;
    }
}
