package com.vityarthi.smartcampus.repository;

import com.vityarthi.smartcampus.model.*;
import com.vityarthi.smartcampus.util.ConsoleFormatter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Robust file-based and in-memory persistence implementation of DataStore.
 * Demonstrates Java File I/O, Streams, Collections Framework, and Thread Safety.
 */
public class StorageManager implements DataStore {
    private final Path dataDirectory;
    private final Map<String, User> userMap = new ConcurrentHashMap<>();
    private final Map<String, Course> courseMap = new ConcurrentHashMap<>();
    private final Map<String, Enrollment> enrollmentMap = new ConcurrentHashMap<>();

    public StorageManager() {
        this("data");
    }

    public StorageManager(String directoryPath) {
        this.dataDirectory = Paths.get(directoryPath);
        initDirectories();
        loadAll();
    }

    private void initDirectories() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
        } catch (IOException e) {
            ConsoleFormatter.printError("Failed to initialize storage directory: " + e.getMessage());
        }
    }

    @Override
    public void saveUser(User user) {
        userMap.put(user.getUserId(), user);
    }

    @Override
    public Optional<User> findUserById(String userId) {
        if (userId == null) return Optional.empty();
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        if (email == null) return Optional.empty();
        return userMap.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    @Override
    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(userMap.values());
    }

    @Override
    public void saveCourse(Course course) {
        courseMap.put(course.getCourseCode(), course);
    }

    @Override
    public Optional<Course> findCourseByCode(String courseCode) {
        if (courseCode == null) return Optional.empty();
        return Optional.ofNullable(courseMap.get(courseCode.trim().toUpperCase()));
    }

    @Override
    public Collection<Course> getAllCourses() {
        return Collections.unmodifiableCollection(courseMap.values());
    }

    @Override
    public void deleteCourse(String courseCode) {
        courseMap.remove(courseCode.trim().toUpperCase());
    }

    @Override
    public void saveEnrollment(Enrollment enrollment) {
        enrollmentMap.put(enrollment.getEnrollmentId(), enrollment);
    }

    @Override
    public Optional<Enrollment> findEnrollment(String studentRegNo, String courseCode) {
        return enrollmentMap.values().stream()
                .filter(e -> e.getStudentRegNo().equalsIgnoreCase(studentRegNo) &&
                             e.getCourseCode().equalsIgnoreCase(courseCode))
                .findFirst();
    }

    @Override
    public Collection<Enrollment> getEnrollmentsByStudent(String studentRegNo) {
        return enrollmentMap.values().stream()
                .filter(e -> e.getStudentRegNo().equalsIgnoreCase(studentRegNo))
                .toList();
    }

    @Override
    public Collection<Enrollment> getEnrollmentsByCourse(String courseCode) {
        return enrollmentMap.values().stream()
                .filter(e -> e.getCourseCode().equalsIgnoreCase(courseCode))
                .toList();
    }

    @Override
    public Collection<Enrollment> getAllEnrollments() {
        return Collections.unmodifiableCollection(enrollmentMap.values());
    }

    @Override
    public synchronized void persistAll() {
        persistUsers();
        persistCourses();
        persistEnrollments();
    }

    @Override
    public synchronized void loadAll() {
        loadUsers();
        loadCourses();
        loadEnrollments();

        // If no data exists, seed default dataset with user's details
        if (userMap.isEmpty() || courseMap.isEmpty()) {
            seedDefaultData();
            persistAll();
        }
    }

    private void persistUsers() {
        Path path = dataDirectory.resolve("users.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("role,userId,fullName,email,passwordHash,param1,param2,param3\n");
            for (User u : userMap.values()) {
                if (u instanceof Student s) {
                    writer.write(String.format("STUDENT,%s,%s,%s,%s,%s,%s,%d\n",
                            s.getUserId(), s.getFullName(), s.getEmail(), s.getPasswordHash(),
                            s.getRegNo(), s.getProgram(), s.getSemester()));
                } else if (u instanceof Faculty f) {
                    writer.write(String.format("FACULTY,%s,%s,%s,%s,%s,%s,%s\n",
                            f.getUserId(), f.getFullName(), f.getEmail(), f.getPasswordHash(),
                            f.getEmployeeId(), f.getDepartment(), f.getCabin()));
                } else if (u instanceof Admin a) {
                    writer.write(String.format("ADMIN,%s,%s,%s,%s,%s,%d,\n",
                            a.getUserId(), a.getFullName(), a.getEmail(), a.getPasswordHash(),
                            a.getDepartment(), a.getClearanceLevel()));
                }
            }
        } catch (IOException e) {
            ConsoleFormatter.printError("Could not persist users: " + e.getMessage());
        }
    }

    private void loadUsers() {
        Path path = dataDirectory.resolve("users.csv");
        if (!Files.exists(path)) return;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    String role = parts[0];
                    String userId = parts[1];
                    String name = parts[2];
                    String email = parts[3];
                    String pass = parts[4];
                    if ("STUDENT".equalsIgnoreCase(role)) {
                        String regNo = parts[5];
                        String prog = parts[6];
                        int sem = parts.length > 7 && !parts[7].isEmpty() ? Integer.parseInt(parts[7]) : 1;
                        userMap.put(userId, new Student(userId, name, email, pass, regNo, prog, sem));
                    } else if ("FACULTY".equalsIgnoreCase(role)) {
                        String empId = parts[5];
                        String dept = parts[6];
                        String cabin = parts.length > 7 ? parts[7] : "Tech Tower";
                        userMap.put(userId, new Faculty(userId, name, email, pass, empId, dept, cabin));
                    } else if ("ADMIN".equalsIgnoreCase(role)) {
                        String dept = parts[5];
                        int level = parts.length > 6 && !parts[6].isEmpty() ? Integer.parseInt(parts[6]) : 1;
                        userMap.put(userId, new Admin(userId, name, email, pass, dept, level));
                    }
                }
            }
        } catch (Exception e) {
            ConsoleFormatter.printWarning("User data file empty or malformed: " + e.getMessage());
        }
    }

    private void persistCourses() {
        Path path = dataDirectory.resolve("courses.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("type,code,title,credits,slot,facultyId,maxSeats,p1,p2,p3\n");
            for (Course c : courseMap.values()) {
                if (c instanceof TheoryCourse tc) {
                    writer.write(String.format("THEORY,%s,%s,%d,%s,%s,%d,%d,%d,\n",
                            tc.getCourseCode(), tc.getCourseTitle(), tc.getCredits(),
                            tc.getSlot().getSlotCode(), tc.getFacultyEmpId(), tc.getMaxSeats(),
                            tc.getLectureHours(), tc.getTutorialHours()));
                } else if (c instanceof LabCourse lc) {
                    writer.write(String.format("LAB,%s,%s,%d,%s,%s,%d,%d,%s,%.2f\n",
                            lc.getCourseCode(), lc.getCourseTitle(), lc.getCredits(),
                            lc.getSlot().getSlotCode(), lc.getFacultyEmpId(), lc.getMaxSeats(),
                            lc.getPracticalHours(), lc.getLabSoftwareEnvironment(), lc.getLabInfrastructureSurcharge()));
                }
            }
        } catch (IOException e) {
            ConsoleFormatter.printError("Could not persist courses: " + e.getMessage());
        }
    }

    private void loadCourses() {
        Path path = dataDirectory.resolve("courses.csv");
        if (!Files.exists(path)) return;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    String type = parts[0];
                    String code = parts[1];
                    String title = parts[2];
                    int credits = Integer.parseInt(parts[3]);
                    Slot slot = Slot.standard(parts[4]);
                    String facultyId = parts[5];
                    int maxSeats = Integer.parseInt(parts[6]);

                    if ("THEORY".equalsIgnoreCase(type)) {
                        int lHours = parts.length > 7 && !parts[7].isEmpty() ? Integer.parseInt(parts[7]) : 3;
                        int tHours = parts.length > 8 && !parts[8].isEmpty() ? Integer.parseInt(parts[8]) : 0;
                        courseMap.put(code, new TheoryCourse(code, title, credits, slot, facultyId, maxSeats, Collections.emptyList(), lHours, tHours));
                    } else if ("LAB".equalsIgnoreCase(type)) {
                        int pHours = parts.length > 7 && !parts[7].isEmpty() ? Integer.parseInt(parts[7]) : 2;
                        String env = parts.length > 8 && !parts[8].isEmpty() ? parts[8] : "OpenJDK 21";
                        double surcharge = parts.length > 9 && !parts[9].isEmpty() ? Double.parseDouble(parts[9]) : 1500.0;
                        courseMap.put(code, new LabCourse(code, title, credits, slot, facultyId, maxSeats, Collections.emptyList(), pHours, env, surcharge));
                    }
                }
            }
        } catch (Exception e) {
            ConsoleFormatter.printWarning("Courses data file empty or malformed: " + e.getMessage());
        }
    }

    private void persistEnrollments() {
        Path path = dataDirectory.resolve("enrollments.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("enrollmentId,studentRegNo,courseCode,status,attended,total,grade\n");
            for (Enrollment e : enrollmentMap.values()) {
                writer.write(String.format("%s,%s,%s,%s,%d,%d,%s\n",
                        e.getEnrollmentId(), e.getStudentRegNo(), e.getCourseCode(),
                        e.getStatus().name(), e.getAttendedClasses(), e.getTotalClasses(), e.getGrade().name()));
            }
        } catch (IOException e) {
            ConsoleFormatter.printError("Could not persist enrollments: " + e.getMessage());
        }
    }

    private void loadEnrollments() {
        Path path = dataDirectory.resolve("enrollments.csv");
        if (!Files.exists(path)) return;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    String id = parts[0];
                    String regNo = parts[1];
                    String courseCode = parts[2];
                    Enrollment.EnrollmentStatus status = Enrollment.EnrollmentStatus.valueOf(parts[3]);
                    int attended = Integer.parseInt(parts[4]);
                    int total = Integer.parseInt(parts[5]);
                    Grade grade = Grade.valueOf(parts[6]);

                    Enrollment enrollment = new Enrollment(id, regNo, courseCode, status, attended, total, grade);
                    enrollmentMap.put(id, enrollment);

                    // Synchronize course seat allocation
                    Course course = courseMap.get(courseCode);
                    if (course != null && status == Enrollment.EnrollmentStatus.ENROLLED) {
                        course.enrollStudent(regNo);
                    }
                    // Synchronize student credits
                    User user = userMap.get(regNo);
                    if (user instanceof Student s && status == Enrollment.EnrollmentStatus.ENROLLED && course != null) {
                        s.registerCourseCode(courseCode);
                        s.addCredits(course.getCredits());
                    }
                }
            }
        } catch (Exception e) {
            ConsoleFormatter.printWarning("Enrollment data file empty or malformed: " + e.getMessage());
        }
    }

    private void seedDefaultData() {
        // Primary user requested details
        Student primaryStudent = new Student("25BAI10611", "YATHAM JATHINDRA REDDY",
                "jathindra.reddy2025@vitbhopal.ac.in", "student@123",
                "25BAI10611", "B.Tech Computer Science (AI & ML)", 3, 27);
        userMap.put(primaryStudent.getUserId(), primaryStudent);

        // Additional sample students
        Student student2 = new Student("25BAI10042", "Aarav Sharma",
                "aarav.sharma2025@vitbhopal.ac.in", "student@123",
                "25BAI10042", "B.Tech Computer Science (AI & ML)", 3, 27);
        userMap.put(student2.getUserId(), student2);

        // Faculty
        Faculty faculty1 = new Faculty("FAC101", "Dr. Rajesh Raman",
                "rajesh.raman@vitbhopal.ac.in", "faculty@123",
                "FAC101", "Computer Science & Engineering", "Academic Block 2 - Cabin 402");
        faculty1.assignCourse("CSE2006");
        userMap.put(faculty1.getUserId(), faculty1);

        Faculty faculty2 = new Faculty("FAC102", "Dr. Priya Sundaram",
                "priya.sundaram@vitbhopal.ac.in", "faculty@123",
                "FAC102", "Computer Science & Engineering", "Academic Block 1 - Cabin 215");
        faculty2.assignCourse("CSE2001");
        userMap.put(faculty2.getUserId(), faculty2);

        // Admin
        Admin admin = new Admin("ADMIN01", "Academic Registrar Admin",
                "admin@vitbhopal.ac.in", "admin@123", "Academic Staff College", 3);
        userMap.put(admin.getUserId(), admin);

        // Standard VIT Courses
        Course cse2006 = new TheoryCourse("CSE2006", "Programming in Java", 4,
                Slot.standard("A1"), "FAC101", 60, Collections.emptyList(), 3, 1);
        Course cse2006Lab = new LabCourse("CSE2006L", "Java Programming Lab", 1,
                Slot.standard("L1+L2"), "FAC101", 30, List.of("CSE2006"), 2, "Eclipse IDE / JDK 21", 1200.0);
        Course cse2001 = new TheoryCourse("CSE2001", "Data Structures and Algorithms", 4,
                Slot.standard("B1"), "FAC102", 60, Collections.emptyList(), 3, 1);
        Course mat2002 = new TheoryCourse("MAT2002", "Discrete Mathematics and Graph Theory", 4,
                Slot.standard("C1"), "FAC103", 60, Collections.emptyList(), 3, 1);
        Course cse2004 = new TheoryCourse("CSE2004", "Database Management Systems", 3,
                Slot.standard("D1"), "FAC104", 60, Collections.emptyList(), 3, 0);
        Course cse2004Lab = new LabCourse("CSE2004L", "DBMS Laboratory", 1,
                Slot.standard("L3+L4"), "FAC104", 30, List.of("CSE2004"), 2, "PostgreSQL & MySQL Workbench", 1500.0);

        // Compact capacity course for waitlist demonstration
        Course aiSeminar = new TheoryCourse("CSE3999", "Special Topics in Deep Learning & Generative AI", 2,
                Slot.standard("E1"), "FAC101", 2, Collections.emptyList(), 2, 0);

        courseMap.put(cse2006.getCourseCode(), cse2006);
        courseMap.put(cse2006Lab.getCourseCode(), cse2006Lab);
        courseMap.put(cse2001.getCourseCode(), cse2001);
        courseMap.put(mat2002.getCourseCode(), mat2002);
        courseMap.put(cse2004.getCourseCode(), cse2004);
        courseMap.put(cse2004Lab.getCourseCode(), cse2004Lab);
        courseMap.put(aiSeminar.getCourseCode(), aiSeminar);

        // Default initial enrollment for primary student
        cse2006.enrollStudent(primaryStudent.getRegNo());
        primaryStudent.registerCourseCode("CSE2006");
        primaryStudent.addCredits(cse2006.getCredits());
        Enrollment enr1 = new Enrollment("ENR-1001", primaryStudent.getRegNo(), "CSE2006",
                Enrollment.EnrollmentStatus.ENROLLED, 18, 20, Grade.NOT_ASSIGNED);
        enrollmentMap.put(enr1.getEnrollmentId(), enr1);

        cse2006Lab.enrollStudent(primaryStudent.getRegNo());
        primaryStudent.registerCourseCode("CSE2006L");
        primaryStudent.addCredits(cse2006Lab.getCredits());
        Enrollment enr2 = new Enrollment("ENR-1002", primaryStudent.getRegNo(), "CSE2006L",
                Enrollment.EnrollmentStatus.ENROLLED, 9, 10, Grade.NOT_ASSIGNED);
        enrollmentMap.put(enr2.getEnrollmentId(), enr2);
    }
}
