package com.vityarthi.smartcampus.test;

import com.vityarthi.smartcampus.concurrency.NotificationWorker;
import com.vityarthi.smartcampus.exception.*;
import com.vityarthi.smartcampus.model.*;
import com.vityarthi.smartcampus.repository.StorageManager;
import com.vityarthi.smartcampus.service.*;
import com.vityarthi.smartcampus.util.ConsoleFormatter;
import com.vityarthi.smartcampus.util.InputValidator;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Automated test suite validating technical expectations, domain logic, and OOP concepts.
 */
public class SystemTestSuite {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        ConsoleFormatter.printHeader("SMARTCAMPUS - AUTOMATED VALIDATION TEST SUITE");
        System.out.println("Candidate: YATHAM JATHINDRA REDDY | Reg No: 25BAI10611 | Course: CSE2006\n");

        runTest("OOP - User Hierarchy & Polymorphism", SystemTestSuite::testUserHierarchy);
        runTest("OOP - Course Hierarchy & Polymorphic Tuition Fee", SystemTestSuite::testCoursePolymorphism);
        runTest("Timetable Slot Clash Detection Logic", SystemTestSuite::testSlotClashLogic);
        runTest("Input Validator Regex Verification", SystemTestSuite::testInputValidation);
        runTest("Course Registration & Credit Bounds", SystemTestSuite::testCourseRegistration);
        runTest("Credit Limit Overload Exception Enforcement", SystemTestSuite::testCreditLimitEnforcement);
        runTest("Registration Timetable Slot Clash Exception", SystemTestSuite::testSlotClashRegistration);
        runTest("Waitlist Queue & Automatic FIFO Seat Promotion", SystemTestSuite::testWaitlistAndPromotion);
        runTest("Grading & Dynamic Weighted CGPA Calculation", SystemTestSuite::testGradingAndCgpa);
        runTest("Attendance Tracking & Debarment Alert (<75%)", SystemTestSuite::testAttendanceAndDebarment);
        runTest("File I/O Persistence & Reload Engine", SystemTestSuite::testPersistenceAndReload);

        ConsoleFormatter.printHeader(String.format("TEST RESULTS: %d/%d PASSED (%.1f%%)",
                testsPassed, testsRun, (testsPassed * 100.0) / testsRun));

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void runTest(String name, Runnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.printf("  [PASS] %-60s\n", name);
        } catch (Throwable t) {
            System.out.printf("  [FAIL] %-60s -> %s\n", name, t.getMessage());
        }
    }

    private static void testUserHierarchy() {
        User student = new Student("U1", "Student One", "s1@vit.ac.in", "pass", "25BAI10001", "B.Tech AI", 2);
        User faculty = new Faculty("U2", "Dr. Faculty", "f1@vit.ac.in", "pass", "FAC50", "CSE", "AB1-101");
        User admin = new Admin("U3", "System Admin", "a1@vit.ac.in", "pass", "IT Services", 2);

        TestCase.assertEquals("Student Role", SystemRole.STUDENT, student.getRole());
        TestCase.assertEquals("Faculty Role", SystemRole.FACULTY, faculty.getRole());
        TestCase.assertEquals("Admin Role", SystemRole.ADMIN, admin.getRole());

        TestCase.assertTrue("Student details polymorphic", student.getRoleSpecificDetails().contains("25BAI10001"));
        TestCase.assertTrue("Faculty details polymorphic", faculty.getRoleSpecificDetails().contains("FAC50"));
    }

    private static void testCoursePolymorphism() {
        Course theory = new TheoryCourse("CSE2006", "Programming in Java", 4, Slot.standard("A1"), "FAC101", 60, Collections.emptyList(), 3, 1);
        Course lab = new LabCourse("CSE2006L", "Java Lab", 1, Slot.standard("L1+L2"), "FAC101", 30, Collections.emptyList(), 2, "JDK 21", 1500.0);

        // Tuition computation with base rate 1000 per credit
        double theoryFee = theory.calculateTuitionFee(1000.0); // 4 * 1000 = 4000
        double labFee = lab.calculateTuitionFee(1000.0);       // (1 * 1000) + 1500 = 2500

        TestCase.assertDoubleEquals("Theory tuition calculation", 4000.0, theoryFee, 0.01);
        TestCase.assertDoubleEquals("Lab tuition calculation with surcharge", 2500.0, labFee, 0.01);
    }

    private static void testSlotClashLogic() {
        Slot a1 = Slot.standard("A1");
        Slot a1Duplicate = Slot.standard("A1");
        Slot b1 = Slot.standard("B1");
        Slot compoundLab = new Slot("L1+L2", "Mon 14:00-15:40", "Lab");
        Slot singleLab = new Slot("L1", "Mon 14:00-14:50", "Lab");

        TestCase.assertTrue("Same slot clashes", a1.clashesWith(a1Duplicate));
        TestCase.assertFalse("Different slot does not clash", a1.clashesWith(b1));
        TestCase.assertTrue("Compound slot overlap clashes", compoundLab.clashesWith(singleLab));
    }

    private static void testInputValidation() {
        TestCase.assertTrue("Valid RegNo 25BAI10611", InputValidator.isValidRegNo("25BAI10611"));
        TestCase.assertTrue("Valid CourseCode CSE2006", InputValidator.isValidCourseCode("CSE2006"));
        TestCase.assertTrue("Valid Email", InputValidator.isValidEmail("jathindra.reddy2025@vitbhopal.ac.in"));
        TestCase.assertFalse("Invalid RegNo", InputValidator.isValidRegNo("INVALID_REG"));
        TestCase.assertFalse("Invalid CourseCode", InputValidator.isValidCourseCode("CS12"));
    }

    private static void testCourseRegistration() {
        StorageManager storage = new StorageManager("test_data");
        NotificationWorker worker = new NotificationWorker();
        EnrollmentService service = new EnrollmentService(storage, worker);

        Student testStudent = new Student("25BAI99999", "Test Candidate", "test@vit.ac.in", "pass", "25BAI99999", "B.Tech", 1, 20);
        storage.saveUser(testStudent);

        Course c1 = new TheoryCourse("CS1001", "Intro to CS", 3, Slot.standard("A1"), "FAC1", 50, Collections.emptyList(), 3, 0);
        storage.saveCourse(c1);

        Enrollment enr = service.registerCourse("25BAI99999", "CS1001");
        TestCase.assertEquals("Enrollment status", Enrollment.EnrollmentStatus.ENROLLED, enr.getStatus());
        TestCase.assertEquals("Student credits updated", 3, testStudent.getCurrentCredits());
    }

    private static void testCreditLimitEnforcement() {
        StorageManager storage = new StorageManager("test_data");
        NotificationWorker worker = new NotificationWorker();
        EnrollmentService service = new EnrollmentService(storage, worker);

        // Student with strict max credit limit of 4
        Student strictStudent = new Student("25BAI99998", "Strict Student", "strict@vit.ac.in", "pass", "25BAI99998", "B.Tech", 1, 4);
        storage.saveUser(strictStudent);

        Course c1 = new TheoryCourse("CS1002", "Advanced Algorithms", 3, Slot.standard("B1"), "FAC1", 50, Collections.emptyList(), 3, 0);
        Course c2 = new TheoryCourse("CS1003", "Machine Learning", 3, Slot.standard("C1"), "FAC1", 50, Collections.emptyList(), 3, 0);
        storage.saveCourse(c1);
        storage.saveCourse(c2);

        service.registerCourse("25BAI99998", "CS1002"); // 3 credits -> OK

        boolean threwExpected = false;
        try {
            service.registerCourse("25BAI99998", "CS1003"); // 3 + 3 = 6 > 4 -> Should throw
        } catch (CreditLimitExceededException e) {
            threwExpected = true;
        }
        TestCase.assertTrue("CreditLimitExceededException caught", threwExpected);
    }

    private static void testSlotClashRegistration() {
        StorageManager storage = new StorageManager("test_data");
        NotificationWorker worker = new NotificationWorker();
        EnrollmentService service = new EnrollmentService(storage, worker);

        Student student = new Student("25BAI99997", "Slot Student", "slot@vit.ac.in", "pass", "25BAI99997", "B.Tech", 1, 27);
        storage.saveUser(student);

        Course c1 = new TheoryCourse("CS1004", "Course 1", 3, Slot.standard("D1"), "FAC1", 50, Collections.emptyList(), 3, 0);
        Course c2 = new TheoryCourse("CS1005", "Course 2", 3, Slot.standard("D1"), "FAC2", 50, Collections.emptyList(), 3, 0);
        storage.saveCourse(c1);
        storage.saveCourse(c2);

        service.registerCourse("25BAI99997", "CS1004");

        boolean clashCaught = false;
        try {
            service.registerCourse("25BAI99997", "CS1005");
        } catch (SlotClashException e) {
            clashCaught = true;
        }
        TestCase.assertTrue("SlotClashException caught", clashCaught);
    }

    private static void testWaitlistAndPromotion() {
        StorageManager storage = new StorageManager("test_data");
        NotificationWorker worker = new NotificationWorker();
        EnrollmentService service = new EnrollmentService(storage, worker);

        // Course with capacity = 1
        Course tinyCourse = new TheoryCourse("CS1006", "Tiny Course", 3, Slot.standard("E1"), "FAC1", 1, Collections.emptyList(), 3, 0);
        storage.saveCourse(tinyCourse);

        Student s1 = new Student("25BAI99991", "First Student", "s1@vit.ac.in", "pass", "25BAI99991", "B.Tech", 1, 27);
        Student s2 = new Student("25BAI99992", "Second Student", "s2@vit.ac.in", "pass", "25BAI99992", "B.Tech", 1, 27);
        storage.saveUser(s1);
        storage.saveUser(s2);

        Enrollment enr1 = service.registerCourse("25BAI99991", "CS1006");
        Enrollment enr2 = service.registerCourse("25BAI99992", "CS1006");

        TestCase.assertEquals("First student enrolled", Enrollment.EnrollmentStatus.ENROLLED, enr1.getStatus());
        TestCase.assertEquals("Second student waitlisted", Enrollment.EnrollmentStatus.WAITLISTED, enr2.getStatus());

        // Drop first student -> s2 should be promoted automatically
        service.dropCourse("25BAI99991", "CS1006");

        Enrollment updatedS2 = storage.findEnrollment("25BAI99992", "CS1006").orElseThrow();
        TestCase.assertEquals("Waitlisted student automatically promoted", Enrollment.EnrollmentStatus.ENROLLED, updatedS2.getStatus());
    }

    private static void testGradingAndCgpa() {
        StorageManager storage = new StorageManager("test_data");
        NotificationWorker worker = new NotificationWorker();
        EnrollmentService service = new EnrollmentService(storage, worker);

        Student student = new Student("25BAI99990", "Grade Student", "grade@vit.ac.in", "pass", "25BAI99990", "B.Tech", 1, 27);
        storage.saveUser(student);

        Course c1 = new TheoryCourse("CS1007", "Subject 1", 4, Slot.standard("A1"), "FAC1", 50, Collections.emptyList(), 3, 1);
        Course c2 = new TheoryCourse("CS1008", "Subject 2", 4, Slot.standard("B1"), "FAC1", 50, Collections.emptyList(), 3, 1);
        storage.saveCourse(c1);
        storage.saveCourse(c2);

        service.registerCourse("25BAI99990", "CS1007");
        service.registerCourse("25BAI99990", "CS1008");

        // Grade S (10.0) in CS1007 (4 credits) and Grade A (9.0) in CS1008 (4 credits)
        // Expected CGPA: (4*10 + 4*9) / 8 = 9.50
        service.assignGrade("25BAI99990", "CS1007", Grade.S);
        service.assignGrade("25BAI99990", "CS1008", Grade.A);

        TestCase.assertDoubleEquals("Dynamic CGPA calculation", 9.50, student.getCgpa(), 0.01);
    }

    private static void testAttendanceAndDebarment() {
        Enrollment enr = new Enrollment("ENR-TEST", "25BAI10611", "CSE2006", Enrollment.EnrollmentStatus.ENROLLED);
        // 10 total classes, attended 7 -> 70% (< 75% threshold)
        for (int i = 0; i < 7; i++) enr.recordAttendance(true);
        for (int i = 0; i < 3; i++) enr.recordAttendance(false);

        TestCase.assertDoubleEquals("Attendance percentage", 70.0, enr.getAttendancePercentage(), 0.01);
        TestCase.assertTrue("Debarred flag is active", enr.isAttendanceDebarred());
    }

    private static void testPersistenceAndReload() {
        StorageManager storage1 = new StorageManager("test_data");
        storage1.persistAll();

        StorageManager storage2 = new StorageManager("test_data");
        TestCase.assertTrue("Users loaded from disk", !storage2.getAllUsers().isEmpty());
        TestCase.assertTrue("Courses loaded from disk", !storage2.getAllCourses().isEmpty());

        // Clean up test data folder
        deleteDirectory(new File("test_data"));
    }

    private static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) deleteDirectory(f);
            }
        }
        file.delete();
    }
}
