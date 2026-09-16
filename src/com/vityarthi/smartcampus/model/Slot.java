package com.vityarthi.smartcampus.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an academic timetable slot (e.g., A1, B1, C1, L1+L2) with day and time mapping.
 * Provides slot collision and clash detection logic.
 */
public class Slot {
    private final String slotCode;
    private final String daySchedule;
    private final String timeRange;
    private final Set<String> atomicTokens;

    public Slot(String slotCode, String daySchedule, String timeRange) {
        this.slotCode = Objects.requireNonNull(slotCode, "slotCode cannot be null").trim().toUpperCase();
        this.daySchedule = Objects.requireNonNull(daySchedule, "daySchedule cannot be null");
        this.timeRange = Objects.requireNonNull(timeRange, "timeRange cannot be null");
        this.atomicTokens = new HashSet<>();
        
        // Parse compound slots like "L1+L2" or "A1+TA1"
        String[] tokens = this.slotCode.split("\\+");
        for (String t : tokens) {
            this.atomicTokens.add(t.trim());
        }
    }

    /**
     * Factory method for well-known campus standard slots.
     */
    public static Slot standard(String code) {
        String upper = code.trim().toUpperCase();
        return switch (upper) {
            case "A1" -> new Slot("A1", "Mon 08:00-08:50, Wed 09:00-09:50, Fri 10:00-10:50", "Morning Slot A1");
            case "B1" -> new Slot("B1", "Tue 08:00-08:50, Thu 09:00-09:50", "Morning Slot B1");
            case "C1" -> new Slot("C1", "Mon 09:00-09:50, Wed 10:00-10:50, Fri 11:00-11:50", "Morning Slot C1");
            case "D1" -> new Slot("D1", "Tue 09:00-09:50, Thu 10:00-10:50", "Morning Slot D1");
            case "E1" -> new Slot("E1", "Mon 10:00-10:50, Wed 11:00-11:50", "Morning Slot E1");
            case "F1" -> new Slot("F1", "Tue 10:00-10:50, Thu 11:00-11:50", "Morning Slot F1");
            case "A2" -> new Slot("A2", "Mon 14:00-14:50, Wed 15:00-15:50", "Afternoon Slot A2");
            case "B2" -> new Slot("B2", "Tue 14:00-14:50, Thu 15:00-15:50", "Afternoon Slot B2");
            case "L1+L2" -> new Slot("L1+L2", "Mon 14:00-15:40", "Lab Batch Slot L1+L2");
            case "L3+L4" -> new Slot("L3+L4", "Tue 14:00-15:40", "Lab Batch Slot L3+L4");
            case "L31+L32" -> new Slot("L31+L32", "Wed 14:00-15:40", "Lab Batch Slot L31+L32");
            case "L33+L34" -> new Slot("L33+L34", "Thu 14:00-15:40", "Lab Batch Slot L33+L34");
            default -> new Slot(upper, "Flexible Schedule", "Standard Slot " + upper);
        };
    }

    /**
     * Checks if this slot collides with another slot.
     */
    public boolean clashesWith(Slot other) {
        if (other == null) return false;
        if (this.slotCode.equalsIgnoreCase(other.slotCode)) return true;
        // Check if any atomic token overlaps (e.g. L1+L2 clashes with L1)
        for (String t : this.atomicTokens) {
            if (other.atomicTokens.contains(t)) {
                return true;
            }
        }
        return false;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public String getDaySchedule() {
        return daySchedule;
    }

    public String getTimeRange() {
        return timeRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slot slot)) return false;
        return Objects.equals(slotCode, slot.slotCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotCode);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", slotCode, daySchedule, timeRange);
    }
}
