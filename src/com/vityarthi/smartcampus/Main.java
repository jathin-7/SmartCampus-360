package com.vityarthi.smartcampus;

import com.vityarthi.smartcampus.concurrency.NotificationWorker;
import com.vityarthi.smartcampus.exception.CampusException;
import com.vityarthi.smartcampus.model.*;
import com.vityarthi.smartcampus.repository.StorageManager;
import com.vityarthi.smartcampus.service.*;
import com.vityarthi.smartcampus.test.SystemTestSuite;
import com.vityarthi.smartcampus.util.ConsoleFormatter;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Main application entry point for SmartCampus Management System.
 * Developed for Course CSE2006 (Programming in Java).
 *
 * Student: YATHAM JATHINDRA REDDY
 * Reg No : 25BAI10611
 */
public class Main {
    private static StorageManager storageManager;
    private static NotificationWorker notificationWorker;
    private static AuthenticationService authService;
    private static CourseManagementService courseService;
    private static EnrollmentService enrollmentService;
    private static AnalyticsReportingService reportingService;

    public static void main(String[] args) {
        initServices();

        // Check for CLI argument flags
        if (args.length > 0) {
            if ("--test".equalsIgnoreCase(args[0])) {
                SystemTestSuite.main(new String[0]);
                shutdown();
                return;
            } else if ("--demo".equalsIgnoreCase(args[0])) {
                runAutomatedDemo();
                shutdown();
                return;
            }
        }

        runInteractiveCLI();
        shutdown();
    }

    private static void initServices() {
        storageManager = new StorageManager("data");
        notificationWorker = new NotificationWorker();
        notificationWorker.start();

        authService = new AuthenticationService(storageManager);
        courseService = new CourseManagementService(storageManager);
        enrollmentService = new EnrollmentService(storageManager, notificationWorker);
        reportingService = new AnalyticsReportingService(storageManager);
    }

    private static void shutdown() {
        if (storageManager != null) {
            storageManager.persistAll();
        }
        if (notificationWorker != null) {
            notificationWorker.stop();
        }
    }

    public static void runAutomatedDemo() {
        ConsoleFormatter.printHeader("SMARTCAMPUS 360 - LIVE DEMONSTRATION MODE");
        System.out.println("Student Name : YATHAM JATHINDRA REDDY");
        System.out.println("Reg No       : 25BAI10611");
        System.out.println("Course Code  : CSE2006 (Programming in Java)");
        System.out.println("University   : Vellore Institute of Technology (VIT)");
        System.out.println("========================================================================");

        try {
            // Step 1: Authentication
            ConsoleFormatter.printSubHeader("Step 1: Role-Based Authentication & Session Initialisation");
            User user = authService.authenticate("25BAI10611", "student@123");
            ConsoleFormatter.printSuccess("Authenticated as: " + user.getFullName() + " [" + user.getRole() + "]");
            System.out.println("Profile: " + user.getRoleSpecificDetails());

            // Step 2: Course Catalog Display
            ConsoleFormatter.printSubHeader("Step 2: Course Catalog & Available Seat Allocation");
            System.out.print(reportingService.generateCourseOccupancyReport());

            // Step 3: Registration with Clash Detection
            ConsoleFormatter.printSubHeader("Step 3: FFCS Registration & Slot Collision Detection");
            System.out.println("Attempting to enroll '25BAI10611' in 'CSE2001' (Slot B1, 4 Credits)...");
            Enrollment enr1 = enrollmentService.registerCourse("25BAI10611", "CSE2001");
            ConsoleFormatter.printSuccess("Enrollment Successful: " + enr1);

            System.out.println("\nAttempting to enroll another course on conflicting Slot A1 (MAT2002 on C1 vs duplicate)...");
            try {
                // Course CSE2006 is already enrolled in Slot A1
                Course clashCourse = new TheoryCourse("CSE2008", "Software Engineering", 3, Slot.standard("A1"), "FAC101", 60, List.of(), 3, 0);
                storageManager.saveCourse(clashCourse);
                System.out.println("Attempting to register CSE2008 (Slot A1) when CSE2006 (Slot A1) is already active...");
                enrollmentService.registerCourse("25BAI10611", "CSE2008");
            } catch (CampusException e) {
                ConsoleFormatter.printWarning("Caught Expected Domain Exception: " + e.getMessage());
            }

            // Step 4: Waitlist Queue & Promotion Demonstration
            ConsoleFormatter.printSubHeader("Step 4: Real-Time Seat Capacity & Waitlist Queue Processing");
            System.out.println("Course 'CSE3999' has max capacity of 2 seats.");
            System.out.println("Enrolling Student 1 (25BAI10611)...");
            enrollmentService.registerCourse("25BAI10611", "CSE3999");
            System.out.println("Enrolling Student 2 (25BAI10042)...");
            enrollmentService.registerCourse("25BAI10042", "CSE3999");

            Student student3 = new Student("25BAI10099", "Rahul Verma", "rahul@vit.ac.in", "pass", "25BAI10099", "B.Tech AI", 3);
            storageManager.saveUser(student3);
            System.out.println("Enrolling Student 3 (25BAI10099) when course is FULL (Waitlist trigger)...");
            Enrollment waitlistedEnr = enrollmentService.registerCourse("25BAI10099", "CSE3999");
            System.out.println("Result: " + waitlistedEnr);

            System.out.println("\nStudent 1 drops CSE3999 -> Auto-promoting Student 3 from Waitlist...");
            enrollmentService.dropCourse("25BAI10611", "CSE3999");
            Enrollment promoted = storageManager.findEnrollment("25BAI10099", "CSE3999").orElseThrow();
            ConsoleFormatter.printSuccess("Updated Status for 25BAI10099: " + promoted.getStatus() + " (Auto-promoted from Waitlist!)");

            // Step 5: Attendance Recording & Debarment Alert
            ConsoleFormatter.printSubHeader("Step 5: Attendance Monitoring & 75% Threshold Alerts");
            enrollmentService.recordAttendance("25BAI10611", "CSE2001", true);
            enrollmentService.recordAttendance("25BAI10611", "CSE2001", true);
            enrollmentService.recordAttendance("25BAI10611", "CSE2001", false);
            ConsoleFormatter.printSuccess("Attendance recorded for CSE2001.");

            // Step 6: Grading & Dynamic CGPA Recalculation
            ConsoleFormatter.printSubHeader("Step 6: Academic Grading & Dynamic Weighted CGPA Computation");
            enrollmentService.assignGrade("25BAI10611", "CSE2006", Grade.S);  // 4 credits * 10
            enrollmentService.assignGrade("25BAI10611", "CSE2006L", Grade.A); // 1 credit * 9
            enrollmentService.assignGrade("25BAI10611", "CSE2001", Grade.S);  // 4 credits * 10
            ConsoleFormatter.printSuccess("Grades assigned: CSE2006 (S), CSE2006L (A), CSE2001 (S)");

            // Step 7: Polymorphic Tuition Calculation
            ConsoleFormatter.printSubHeader("Step 7: Polymorphic Fee Computation (Theory vs Laboratory)");
            double totalFees = reportingService.calculateTotalTuitionFee("25BAI10611", 1200.0);
            System.out.printf("Total Semester Academic Fee Calculated: Rs. %,.2f\n", totalFees);

            // Step 8: Academic Transcript Output
            ConsoleFormatter.printSubHeader("Step 8: Official Academic Transcript Generation");
            System.out.println(reportingService.generateStudentTranscript("25BAI10611"));

            ConsoleFormatter.printSuccess("DEMONSTRATION COMPLETED SUCCESSFULLY WITH ZERO ERRORS!");
        } catch (Exception e) {
            ConsoleFormatter.printError("Demo execution error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runInteractiveCLI() {
        Scanner scanner = new Scanner(System.in);
        ConsoleFormatter.printHeader("WELCOME TO SMARTCAMPUS 360 (VIT BHOPAL)");
        System.out.println("Developed by: YATHAM JATHINDRA REDDY | Reg No: 25BAI10611 | CSE2006");

        boolean running = true;
        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Student Portal (Default: YATHAM JATHINDRA REDDY - 25BAI10611)");
            System.out.println("2. Faculty Portal (Dr. Rajesh Raman - FAC101)");
            System.out.println("3. Administrator Console (ADMIN01)");
            System.out.println("4. Run Complete Automated System Demo");
            System.out.println("5. Run Automated Unit Test Suite");
            System.out.println("0. Exit System");
            System.out.print("Select an option [0-5]: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> handleStudentMenu(scanner, "25BAI10611");
                case "2" -> handleFacultyMenu(scanner, "FAC101");
                case "3" -> handleAdminMenu(scanner);
                case "4" -> runAutomatedDemo();
                case "5" -> SystemTestSuite.main(new String[0]);
                case "0" -> {
                    running = false;
                    System.out.println("Exiting SmartCampus. All state persisted to disk. Have a great day!");
                }
                default -> ConsoleFormatter.printWarning("Invalid selection. Please choose an option between 0 and 5.");
            }
        }
    }

    private static void handleStudentMenu(Scanner scanner, String studentRegNo) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- STUDENT PORTAL [" + studentRegNo + "] ---");
            System.out.println("1. View Available Course Catalog");
            System.out.println("2. Register for a Course");
            System.out.println("3. Drop a Course");
            System.out.println("4. View My Academic Transcript & CGPA");
            System.out.println("5. View Tuition Fee Summary");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select an option [0-5]: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> System.out.print(reportingService.generateCourseOccupancyReport());
                    case "2" -> {
                        System.out.print("Enter Course Code to Register (e.g., MAT2002, CSE2004): ");
                        String code = scanner.nextLine().trim();
                        Enrollment enr = enrollmentService.registerCourse(studentRegNo, code);
                        ConsoleFormatter.printSuccess("Registration status: " + enr.getStatus() + " (ID: " + enr.getEnrollmentId() + ")");
                    }
                    case "3" -> {
                        System.out.print("Enter Course Code to Drop: ");
                        String code = scanner.nextLine().trim();
                        enrollmentService.dropCourse(studentRegNo, code);
                        ConsoleFormatter.printSuccess("Successfully dropped course: " + code);
                    }
                    case "4" -> System.out.println(reportingService.generateStudentTranscript(studentRegNo));
                    case "5" -> {
                        double fee = reportingService.calculateTotalTuitionFee(studentRegNo, 1200.0);
                        System.out.printf("Total Semester Academic Fee (Base Rs. 1,200/Credit): Rs. %,.2f\n", fee);
                    }
                    case "0" -> inMenu = false;
                    default -> ConsoleFormatter.printWarning("Invalid option.");
                }
            } catch (Exception e) {
                ConsoleFormatter.printError(e.getMessage());
            }
        }
    }

    private static void handleFacultyMenu(Scanner scanner, String empId) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- FACULTY PORTAL [" + empId + "] ---");
            System.out.println("1. View Enrolled Students for a Course");
            System.out.println("2. Record Lecture Attendance");
            System.out.println("3. Assign Course Grade");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select an option [0-3]: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Enter Course Code (e.g. CSE2006): ");
                        String code = scanner.nextLine().trim();
                        Collection<Enrollment> enrollments = storageManager.getEnrollmentsByCourse(code);
                        System.out.println("Enrolled Students in " + code + ": " + enrollments.size());
                        for (Enrollment e : enrollments) {
                            System.out.printf(" - %s | Status: %s | Attd: %.1f%% | Grade: %s\n",
                                    e.getStudentRegNo(), e.getStatus(), e.getAttendancePercentage(), e.getGrade());
                        }
                    }
                    case "2" -> {
                        System.out.print("Enter Student Reg No (e.g. 25BAI10611): ");
                        String regNo = scanner.nextLine().trim();
                        System.out.print("Enter Course Code (e.g. CSE2006): ");
                        String code = scanner.nextLine().trim();
                        System.out.print("Is student present? (Y/N): ");
                        boolean present = scanner.nextLine().trim().equalsIgnoreCase("Y");
                        enrollmentService.recordAttendance(regNo, code, present);
                        ConsoleFormatter.printSuccess("Attendance recorded.");
                    }
                    case "3" -> {
                        System.out.print("Enter Student Reg No: ");
                        String regNo = scanner.nextLine().trim();
                        System.out.print("Enter Course Code: ");
                        String code = scanner.nextLine().trim();
                        System.out.print("Enter Grade (S, A, B, C, D, E, F): ");
                        Grade grade = Grade.valueOf(scanner.nextLine().trim().toUpperCase());
                        enrollmentService.assignGrade(regNo, code, grade);
                        ConsoleFormatter.printSuccess("Grade " + grade + " successfully assigned!");
                    }
                    case "0" -> inMenu = false;
                    default -> ConsoleFormatter.printWarning("Invalid option.");
                }
            } catch (Exception e) {
                ConsoleFormatter.printError(e.getMessage());
            }
        }
    }

    private static void handleAdminMenu(Scanner scanner) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- ADMINISTRATOR CONSOLE ---");
            System.out.println("1. View Complete Course Catalog");
            System.out.println("2. Add New Course");
            System.out.println("3. View Attendance Debarment Alerts (<75%)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select an option [0-3]: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> System.out.print(reportingService.generateCourseOccupancyReport());
                    case "2" -> {
                        System.out.print("Course Code (e.g. CSE3001): ");
                        String code = scanner.nextLine().trim();
                        System.out.print("Course Title: ");
                        String title = scanner.nextLine().trim();
                        System.out.print("Credits: ");
                        int credits = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Slot (e.g. A1, B1, C1, L1+L2): ");
                        String slotCode = scanner.nextLine().trim();
                        System.out.print("Max Capacity: ");
                        int seats = Integer.parseInt(scanner.nextLine().trim());

                        Course newCourse = new TheoryCourse(code, title, credits, Slot.standard(slotCode),
                                "FAC101", seats, List.of(), 3, 0);
                        courseService.registerNewCourse(newCourse);
                        ConsoleFormatter.printSuccess("Course " + code + " registered successfully!");
                    }
                    case "3" -> {
                        List<Enrollment> debarred = reportingService.getAttendanceDebarmentAlerts();
                        System.out.println("\nAttendance Debarment Alerts (< 75% Attendance):");
                        if (debarred.isEmpty()) {
                            System.out.println("  No students currently debarred.");
                        } else {
                            for (Enrollment e : debarred) {
                                System.out.printf("  [ALERT] Student: %s | Course: %s | Attendance: %.1f%%\n",
                                        e.getStudentRegNo(), e.getCourseCode(), e.getAttendancePercentage());
                            }
                        }
                    }
                    case "0" -> inMenu = false;
                    default -> ConsoleFormatter.printWarning("Invalid option.");
                }
            } catch (Exception e) {
                ConsoleFormatter.printError(e.getMessage());
            }
        }
    }
}
