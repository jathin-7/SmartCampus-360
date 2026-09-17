# Project Report: SmartCampus 360
## Integrated Academic Resource & Course Allocation System

**Course Code**: CSE2006 — Programming in Java  
**Academic Term**: Fall 2025–2026 | Flipped Learning Project Evaluation  

---

## 1. Cover Page
- **Project Title**: SmartCampus 360 - Integrated Academic & Resource Allocation System
- **Student Name**: YATHAM JATHINDRA REDDY
- **Registration Number**: 25BAI10611
- **Degree / Branch**: B.Tech Computer Science and Engineering (AI & ML)
- **Course Code**: CSE2006
- **Course Title**: Programming in Java
- **Institution**: Vellore Institute of Technology (VIT Bhopal)
- **Date of Submission**: September 16, 2026

---

## 2. Introduction
In university life, course registration is one of the most critical events every semester. At VIT, we use the Fully Flexible Credit System (FFCS), which gives students the freedom to choose their preferred subjects, pick faculty members, and select timetable slots that match their schedule.

While FFCS gives great flexibility, it also introduces several practical headaches:
- Students can accidentally pick classes with overlapping timetable slots.
- Popular courses and good faculty sections fill up in seconds. If a section is full, students have to manually check again and again in case someone drops out.
- Keeping track of the 27-credit semester ceiling across both theory and lab courses can be confusing.
- Staying above the mandatory 75% attendance threshold requires constant attention so students don't get debarred from final exams (FAT).
- Calculating how different course grades impact semester CGPA requires manual calculations.

To solve these exact challenges, I built **SmartCampus 360** using Java. It is a complete, modular console application designed using Object-Oriented Programming (OOP) concepts learned throughout the CSE2006 course. It handles timetable conflict checks, automatic waitlisting and seat promotion, credit checks, attendance tracking, dynamic CGPA calculation, and file-based data persistence.

---

## 3. Problem Statement
During peak course registration, students and academic staff deal with multiple friction points:

1. **Timetable Slot Overlaps:** Students often register for multiple courses without realizing that two slots (like A1 and another course on A1, or a 2-hour lab slot like L1+L2 overlapping with an afternoon lecture) happen at the exact same time. The system should automatically prevent this.
2. **Lack of Automated Waitlist Promotion:** When a course reaches maximum capacity, students are turned away. If an enrolled student later changes their mind and drops the course, there is no automatic system to notify or promote the next waiting student.
3. **Credit Overload Risk:** University guidelines set a strict cap of 27 credits per semester. If a system does not validate credit totals before finalizing registration, students risk having their registrations cancelled later.
4. **Attendance Monitoring:** Attendance is often viewed on a separate portal weeks after classes begin. Without real-time calculation and clear warning alerts, students unknowingly drop below 75% and risk debarment.
5. **Separation of Academic Metrics:** Fee calculations, attendance records, and grade point averages are frequently stored in disconnected places rather than in one unified system.

---

## 4. Functional Requirements

### Module 1: User Management & Authentication (RBAC)
- **FR-1.1**: Provide support for three distinct user roles: `Student`, `Faculty`, and `Administrator`.
- **FR-1.2**: Secure user authentication using registration number / employee ID and password (supporting SHA-256 password hashing).
- **FR-1.3**: Provide role-specific menus so each user only sees actions relevant to them.

### Module 2: Course Catalog Management
- **FR-2.1**: Support both Theory courses (with lecture and tutorial hours) and Laboratory courses (with lab hours and software requirements).
- **FR-2.2**: Maintain course metadata including course code (e.g. `CSE2006`), title, credit count, assigned faculty, max seats, and time slots.
- **FR-2.3**: Allow administrators to add new courses or view enrollment statistics.

### Module 3: Registration & Timetable Conflict Resolution Engine
- **FR-3.1**: Check prerequisites before allowing enrollment in an advanced course.
- **FR-3.2**: Enforce the 27-credit semester cap, rejecting any course that pushes a student over the limit.
- **FR-3.3**: Perform automated timetable slot clash detection against the student's existing schedule.
- **FR-3.4**: If a course section is full, automatically place the student in a FIFO waitlist queue.
- **FR-3.5**: When any student drops an enrolled course, automatically promote the first student in the waitlist queue to enrolled status.

### Module 4: Attendance Tracking & Grading
- **FR-4.1**: Allow faculty members to record attendance (Present/Absent) per session for their courses.
- **FR-4.2**: Calculate the running attendance percentage and generate a debarment warning list for anyone below 75%.
- **FR-4.3**: Allow faculty to enter letter grades (`S`, `A`, `B`, `C`, `D`, `E`, `F`).
- **FR-4.4**: Dynamically recalculate the student's CGPA based on course credit weightings.

### Module 5: Transcripts & Fee Reports
- **FR-5.1**: Generate a clean academic transcript showing enrolled courses, slots, attendance percentages, grades, and CGPA.
- **FR-5.2**: Provide course occupancy reports showing enrolled counts, max seats, and waitlist sizes.
- **FR-5.3**: Compute semester tuition fees polymorphically (with lab courses including extra lab infrastructure fees).

---

## 5. Non-Functional Requirements
1. **Performance**: All timetable checks and student searches run instantly in memory using Java Collections (`ConcurrentHashMap`), giving sub-millisecond responses.
2. **Security**: Role-Based Access Control ensures students cannot modify grades or course catalogs. Passwords are encrypted with SHA-256 hashing.
3. **Usability**: The application provides an interactive menu with clean formatted tables and clear status messages. It also includes an automated `--demo` mode that runs through all features without manual typing.
4. **Reliability & Data Persistence**: Data is automatically saved to CSV files in the `data/` folder, ensuring records are not lost when the application is closed.
5. **Concurrency & Thread Safety**: Critical seat booking operations use synchronization to prevent race conditions. A background daemon thread manages asynchronous notifications using a `BlockingQueue`.
6. **Maintainability**: The codebase is cleanly structured into standard Java packages (`model`, `service`, `repository`, `exception`, `concurrency`, `util`, `test`).

---

## 6. System Architecture

The project is designed using a clean, layered architecture:

```
+-------------------------------------------------------------------------+
|                           PRESENTATION LAYER                            |
|             com.vityarthi.smartcampus.Main (CLI & Demo Mode)            |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                             SERVICE LAYER                               |
|  - AuthenticationService         - CourseManagementService              |
|  - EnrollmentService             - AnalyticsReportingService            |
+-------------------------------------------------------------------------+
                    |                               |
                    v                               v
+-----------------------------+     +-------------------------------------+
|      CONCURRENCY LAYER      |     |           DOMAIN MODEL              |
|  - NotificationWorker       |     |  - User (Student, Faculty, Admin)   |
|  - BlockingQueue Dispatcher |     |  - Course (TheoryCourse, LabCourse) |
+-----------------------------+     |  - Slot, Enrollment, Grade          |
                                    +-------------------------------------+
                                                    |
                                                    v
+-------------------------------------------------------------------------+
|                           PERSISTENCE LAYER                             |
|          DataStore Interface <--- StorageManager (CSV File I/O)         |
|             (users.csv, courses.csv, enrollments.csv)                   |
+-------------------------------------------------------------------------+
```

---

## 7. Design Diagrams

### 7.1 Use Case Diagram
```
                          +-------------------------------+
                          |        SmartCampus 360        |
                          +-------------------------------+
                                          |
          +-------------------------------+-------------------------------+
          |                               |                               |
          v                               v                               v
     [STUDENT]                        [FACULTY]                        [ADMIN]
  - View Course Catalog            - View Enrolled Students        - Add New Courses
  - Register Courses (FFCS)        - Mark Lecture Attendance       - View Seat Occupancy
  - Drop Courses                   - Enter Student Grades          - Audit Attendance Warnings
  - View Transcript & CGPA
  - View Tuition Fee Breakdown
```

### 7.2 Registration Process Flow / Workflow Diagram
```
[Student Chooses Course]
           |
           v
[Check Prerequisites Passed?] ------(NO)----> [Display Prerequisite Error]
           | (YES)
           v
[Current Credits + Course <= 27?] --(NO)----> [Throw CreditLimitExceededException]
           | (YES)
           v
[Timetable Slot Clash Detected?] ---(YES)---> [Throw SlotClashException]
           | (NO)
           v
[Is Course Section Full?]
      /         \
  (NO)           (YES)
   |               |
   v               v
[Set Status:     [Place in Waitlist Queue]
  ENROLLED]      [Set Status: WAITLISTED]
   |               |
   v               v
[Update Credits & Save to CSV]
           |
           v
[Send Background Notification]
```

### 7.3 Sequence Diagram (Registration & Auto-Promotion on Drop)
```
Student A                EnrollmentService             Course Section          Student B (Waitlist)
    |                            |                            |                         |
    |---- registerCourse() ----->|                            |                         |
    |                            |---- isFull()? ------------>|                         |
    |                            |<--- false (Seat Available)-|                         |
    |                            |---- enrollStudent() ------>| (Seat booked)           |
    |<--- Status: ENROLLED ------|                            |                         |
    |                            |                            |                         |
    |                            |                            | (Attempts to register)  |
    |                            |<--- registerCourse() --------------------------------|
    |                            |---- isFull()? ------------>|                         |
    |                            |<--- true (Course Full) ----|                         |
    |                            |---- enqueueWaitlist() ---->|                         |
    |                            |---------------- Status: WAITLISTED ----------------->|
    |                            |                            |                         |
    | (Student A drops course)   |                            |                         |
    |---- dropCourse() --------->|                            |                         |
    |                            |---- removeStudent() ------>|                         |
    |                            |<--- promotedStudentId -----| (Student B popped)      |
    |                            |---------------- Promoted to ENROLLED --------------->|
```

### 7.4 Class / Component Diagram
```
                   +------------------------+
                   |      <<abstract>>      |
                   |          User          |
                   +------------------------+
                   | - userId: String       |
                   | - fullName: String     |
                   | - email: String        |
                   | - role: SystemRole     |
                   +------------------------+
                               ^
       +-----------------------+-----------------------+
       |                       |                       |
+---------------+       +---------------+       +---------------+
|    Student    |       |    Faculty    |       |     Admin     |
+---------------+       +---------------+       +---------------+
| - regNo       |       | - empId       |       | - department  |
| - credits     |       | - department  |       | - level       |
| - cgpa        |       | - cabin       |       +---------------+
+---------------+       +---------------+

                   +------------------------+
                   |      <<abstract>>      |
                   |         Course         |
                   +------------------------+
                   | - courseCode: String   |
                   | - credits: int         |
                   | - slot: Slot           |
                   | - maxSeats: int        |
                   +------------------------+
                   | + calculateTuitionFee()|
                   +------------------------+
                               ^
       +-----------------------+-----------------------+
       |                                               |
+--------------------------+               +--------------------------+
|       TheoryCourse       |               |        LabCourse         |
+--------------------------+               +--------------------------+
| - lectureHours: int      |               | - practicalHours: int    |
| - tutorialHours: int     |               | - labEnvironment: String |
+--------------------------+               | - surcharge: double      |
| + calculateTuitionFee()  |               +--------------------------+
+--------------------------+               | + calculateTuitionFee()  |
                                           +--------------------------+
```

### 7.5 Storage / ER Diagram
```
+-------------------+           +-----------------------+           +-------------------+
|      USERS        | 1       * |      ENROLLMENTS      | *       1 |      COURSES      |
+-------------------+-----------+-----------------------+-----------+-------------------+
| PK userId         |           | PK enrollmentId       |           | PK courseCode     |
|    fullName       |           | FK studentRegNo       |           |    courseTitle    |
|    email          |           | FK courseCode         |           |    credits        |
|    role           |           |    status             |           |    slot           |
|    regNo / empId  |           |    attendedClasses    |           |    maxSeats       |
|    department     |           |    totalClasses       |           |    facultyEmpId   |
+-------------------+           |    grade              |           +-------------------+
                                +-----------------------+
```

---

## 8. Design Decisions & Rationale
When planning this project, I made several deliberate architectural decisions:

1. **Inheritance for Courses (Theory vs Lab):** At VIT, theory and lab courses are handled differently. A lab course has practical sessions, software tools, and extra lab consumables. Rather than using messy boolean flags like `isLab`, I created an abstract `Course` class and extended it into `TheoryCourse` and `LabCourse`. This made polymorphic fee calculation clean and natural.
2. **Custom Domain Exceptions:** When something goes wrong (e.g. a timetable collision or credit overload), throwing generic `RuntimeException` or printing an error message in the middle of business logic makes code messy. Creating custom exceptions (`SlotClashException`, `CreditLimitExceededException`, `CourseFullException`) allows the service layer to signal errors cleanly, which the CLI catches and presents nicely to the user.
3. **FIFO Queue for Waitlists:** Fairness is essential in course registration. I used Java's `LinkedList` implementing the `Queue` interface so waitlisted students are queued strictly in the order they applied.
4. **File-Based CSV Storage:** Instead of requiring a heavy external database (like MySQL or Oracle) that requires complex credentials and configuration, I built a CSV storage manager using Java NIO.2. This ensures the project is completely portable and can be downloaded and run immediately on any machine with JDK 21.
5. **Background Concurrency:** Sending notifications or writing audit logs shouldn't freeze the user's registration screen. I created a `NotificationWorker` daemon thread that processes tasks asynchronously using a thread-safe `BlockingQueue`.

---

## 9. Implementation Details
The application is built using standard Java 21 features:
- **OOP Principles**:
  - **Encapsulation**: All fields in models are private, accessed through getters, with collections protected by `Collections.unmodifiableList()`.
  - **Abstraction**: Base classes define abstract methods (`calculateTuitionFee()`, `getRoleSpecificDetails()`) implemented by child classes.
  - **Polymorphism**: The analytics service calculates tuition fees dynamically by calling `course.calculateTuitionFee()` without checking concrete types.
- **Collections Framework**:
  - `ConcurrentHashMap` for thread-safe in-memory caching of users, courses, and enrollments.
  - `LinkedHashSet` to maintain student enrollment order.
  - `LinkedList` as a FIFO queue for waitlisting.
- **Regex Validation**: `InputValidator` uses regular expressions to validate VIT registration numbers (e.g., `25BAI10611`) and course codes (e.g., `CSE2006`).
- **File I/O**: `StorageManager` uses `BufferedReader` and `BufferedWriter` to read and write CSV files, handling commas, headers, and type conversions cleanly.

---

## 10. Execution Results & Screenshots

### 10.1 Automated Validation Test Suite Output
```
========================================================================
             SMARTCAMPUS - AUTOMATED VALIDATION TEST SUITE            
========================================================================
Candidate: YATHAM JATHINDRA REDDY | Reg No: 25BAI10611 | Course: CSE2006

  [PASS] OOP - User Hierarchy & Polymorphism                         
  [PASS] OOP - Course Hierarchy & Polymorphic Tuition Fee            
  [PASS] Timetable Slot Clash Detection Logic                        
  [PASS] Input Validator Regex Verification                          
  [PASS] Course Registration & Credit Bounds                         
  [PASS] Credit Limit Overload Exception Enforcement                 
  [PASS] Registration Timetable Slot Clash Exception                 
  [PASS] Waitlist Queue & Automatic FIFO Seat Promotion              
  [PASS] Grading & Dynamic Weighted CGPA Calculation                 
  [PASS] Attendance Tracking & Debarment Alert (<75%)                
  [PASS] File I/O Persistence & Reload Engine                        

========================================================================
                  TEST RESULTS: 11/11 PASSED (100.0%)                 
========================================================================
```

### 10.2 Student Academic Transcript Output
```
========================================================================
                   VIT SMARTCAMPUS ACADEMIC TRANSCRIPT                  
========================================================================
Student Name   : YATHAM JATHINDRA REDDY
Registration No: 25BAI10611
Degree Program : B.Tech Computer Science (AI & ML)
Semester       : 3
Enrolled Credits: 9 / 27
Cumulative GPA : 9.89
------------------------------------------------------------------------
CODE       TITLE                            CRED   SLOT     ATTD %     GRADE   
------------------------------------------------------------------------
CSE2006L   Java Programming Lab             1      L1+L2    90.0%      A       
CSE2006    Programming in Java              4      A1       90.0%      S       
CSE3999    Special Topics in Deep Lear...   2      E1       100.0%     IP      
CSE2001    Data Structures and Algorithms   4      B1       66.7%      S       
========================================================================
```

---

## 11. Testing Approach
To verify that every part of the system works correctly, I used two levels of testing:
1. **Automated Unit Tests (`SystemTestSuite.java`)**:
   - Written with a custom `TestCase` harness that checks conditions and throws clear assertion errors if anything fails.
   - Tested: User inheritance, polymorphic fee calculation, slot clash logic, regex validation, credit limit overflow, waitlist FIFO promotion, CGPA math, attendance warning triggers, and file saving/loading.
2. **Automated End-to-End Walkthrough (`Main --demo`)**:
   - A demonstration flag that executes a complete real-world scenario from start to finish: logging in as student `25BAI10611`, checking courses, enrolling, attempting a clash, filling a section to trigger the waitlist, dropping to promote the next student, entering grades, and printing the transcript.

---

## 12. Challenges Faced & How I Solved Them
1. **Handling Compound Timetable Slots:**
   - *Problem:* In VIT's timetable, labs occupy compound slots like `L1+L2` or `L31+L32`. A simple string comparison (`slotA.equals(slotB)`) failed when comparing `L1+L2` with a single slot `L1`.
   - *Solution:* In the `Slot` class, I tokenized slot codes on `+` and stored them in a `Set<String>`. Clash detection now checks whether any atomic token from one slot overlaps with another.
2. **Preventing Race Conditions During Waitlist Promotion:**
   - *Problem:* If two students dropped courses at the exact same moment, two waitlisted students could be promoted simultaneously, potentially exceeding maximum seats.
   - *Solution:* I made the enrollment and drop methods `synchronized` and used `ConcurrentHashMap` for all internal storage maps.
3. **Dynamic CGPA Calculation with In-Progress Courses:**
   - *Problem:* In an ongoing semester, some subjects have received grades while others are still in progress (`NOT_ASSIGNED`). Dividing total points by all registered credits unfairly lowered the CGPA.
   - *Solution:* I updated the CGPA formula to sum only the credits of courses where final grades have been submitted, preventing division by zero or skewed averages.

---

## 13. Learnings & Key Takeaways
- **Real Value of OOP:** Before this project, OOP concepts like abstraction and polymorphism felt theoretical. Building `Course`, `TheoryCourse`, and `LabCourse` with polymorphic fee calculation made me realize how clean and extensible object-oriented code is compared to nested `if-else` statements.
- **Java Collections in Practice:** Choosing the right data structure matters—using `Set` for fast duplicate checks, `Queue` for fair waitlisting, and `Map` for instant lookups made the system fast and readable.
- **Thread Safety:** Learning how to use daemon threads, `BlockingQueue`, and `synchronized` blocks showed me how real backend systems handle background tasks safely.
- **Defensive Programming:** Writing custom exceptions and input validators helped me catch bugs early before they corrupted stored data.

---

## 14. Future Enhancements
- **Graphical User Interface (GUI):** Build a modern UI using JavaFX or React to display the timetable as a visual weekly grid with color-coded slots.
- **Database Integration:** Connect the `DataStore` interface to a relational database like PostgreSQL or MySQL using JDBC.
- **Real-Time Push Notifications:** Send actual email or SMS notifications when a student is promoted from the waitlist.

---

## 15. References
1. Schildt, Herbert. *Java: The Complete Reference*, 12th Edition. McGraw-Hill Education, 2021.
2. Bloch, Joshua. *Effective Java*, 3rd Edition. Addison-Wesley Professional, 2018.
3. Oracle Corporation. *Java SE 21 Language Documentation & Specifications*, 2023.
4. Vellore Institute of Technology. *FFCS Course Allocation Guidelines and Academic Regulations Handbook*, 2025.
