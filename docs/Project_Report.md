# Project Report: SmartCampus 360
### Integrated Academic Resource & Course Allocation System
**Course Code**: CSE2006 — Programming in Java  
**Academic Year**: 2025–2026 | Flipped Learning Evaluation  

---

## 1. Cover Page Details
- **Project Title**: SmartCampus 360 - Integrated Academic & Resource Allocation System
- **Student Name**: YATHAM JATHINDRA REDDY
- **Registration Number**: 25BAI10611
- **Program of Study**: B.Tech in Computer Science and Engineering (Specialization in AI & ML)
- **Course Code & Title**: CSE2006 — Programming in Java
- **Institution**: Vellore Institute of Technology (VIT Bhopal)
- **Submission Date**: September 16, 2026
- **Faculty Reviewer**: Department of Computer Science and Engineering

---

## 2. Introduction
Higher educational institutions worldwide operate dynamic course registration frameworks allowing students substantial flexibility in choosing subjects, time slots, and professors (e.g., the Fully Flexible Credit System - FFCS). However, managing the multi-faceted constraints inherent in higher education operations requires robust, scalable, and deterministic software engineering.

**SmartCampus 360** is a comprehensive academic operations system developed natively in Java 21. It addresses complex domain challenges including:
- Real-time timetable slot clash detection
- Semester credit overload enforcement
- Capacity-constrained course section enrollment with automated FIFO waitlists
- Dynamic 10-point CGPA recalculation based on course credit weightings
- Continuous attendance monitoring enforcing the mandatory 75% exam eligibility threshold
- Multithreaded background event dispatching for instant vacancy notifications

The project demonstrates key Object-Oriented Programming (OOP) concepts, the Java Collections Framework, Custom Exception Architectures, Concurrency Controls, and File I/O Persistence.

---

## 3. Problem Statement
Traditional university registration portals frequently encounter critical performance and design bottlenecks during course registration drives:
1. **Timetable Slot Conflicts**: Students unknowingly register for lecture or laboratory hours that physically overlap, causing scheduling deadlocks.
2. **Lack of Automated Waitlist Promotion**: When popular sections fill up, students must continually refresh portals manually. There is no automated, thread-safe queuing mechanism that immediately reassigns seats upon drops.
3. **Credit Overload**: Without rigorous boundary checks, students may exceed academic credit limits, causing regulatory and administrative complications.
4. **Attendance Debarment Blindspots**: Students often fall below the mandatory 75% attendance criterion without timely warnings.
5. **Decoupled Academic Metrics**: Tuition fee calculations, grade point averages, and attendance records are frequently handled in separate systems rather than a cohesive domain model.

SmartCampus 360 solves these issues by providing a unified, thread-safe system with deterministic validation rules.

---

## 4. Functional Requirements

### Module 1: Authentication & Role-Based Access Control (RBAC)
- **FR-1.1**: Support distinct user roles: `Student`, `Faculty`, and `Admin`.
- **FR-1.2**: Secure authentication validating registration numbers, employee IDs, and password credentials.
- **FR-1.3**: Provide role-specific dashboards ensuring students, faculty, and administrators access only authorized operations.

### Module 2: Course Catalog & Slot Administration
- **FR-2.1**: Support diverse course typologies including Theory Courses and Laboratory Courses.
- **FR-2.2**: Maintain academic metadata including course code, title, credits, maximum seats, and instructor assignment.
- **FR-2.3**: Map courses to standard timetable slots (e.g., `A1`, `B1`, `C1`, compound lab slots `L1+L2`).

### Module 3: Course Registration & Conflict Resolution Engine
- **FR-3.1**: Validate prerequisite completion before permitting course enrollment.
- **FR-3.2**: Enforce maximum semester credit limit (27 credits) and reject over-enrollment.
- **FR-3.3**: Perform algorithmic slot clash detection against a student's active schedule before finalizing registration.
- **FR-3.4**: Enforce section capacity limits. Automatically place excess registrants into a FIFO waitlist queue.
- **FR-3.5**: Handle course drops by immediately promoting the head of the waitlist queue to enrolled status.

### Module 4: Academic Progress, Grading & Attendance Tracking
- **FR-4.1**: Enable faculty members to record session attendance (present/absent) for enrolled students.
- **FR-4.2**: Automatically compute cumulative attendance percentage and trigger debarment warnings if attendance drops below 75%.
- **FR-4.3**: Allow faculty to assign official letter grades (`S`, `A`, `B`, `C`, `D`, `E`, `F`).
- **FR-4.4**: Dynamically compute cumulative GPA (CGPA) weighted by course credits.

### Module 5: Reporting & Financial Analytics
- **FR-5.1**: Generate official academic transcripts summarizing enrolled subjects, attendance, grades, and CGPA.
- **FR-5.2**: Provide administrators with campus-wide course occupancy and waitlist summaries.
- **FR-5.3**: Calculate semester tuition fees using polymorphic fee computation models.

---

## 5. Non-Functional Requirements
1. **Performance**: Algorithmic slot collision checks and waitlist queue transactions operate in sub-millisecond execution time (\(O(1)\) to \(O(N)\) where \(N\) is active enrollments).
2. **Security**: Role-Based Access Control (RBAC) prevents privilege escalation. Password hashing (SHA-256) is integrated into user credential validation.
3. **Usability**: Interactive, clear terminal interface with clean tabular layouts, alongside an automated zero-input `--demo` mode.
4. **Reliability & Data Integrity**: File-backed persistence ensures state survival across application restarts. In-memory data structures are guarded against corruption.
5. **Concurrency & Thread Safety**: Multithreaded background worker uses `BlockingQueue` and atomic flags to safely manage asynchronous message dispatching.
6. **Maintainability**: Clean architecture adhering to Single Responsibility and Separation of Concerns across `model`, `service`, `repository`, `exception`, and `concurrency` packages.

---

## 6. System Architecture

SmartCampus 360 is architected according to a Multi-Layered Service-Oriented Pattern:

```
+-------------------------------------------------------------------------+
|                           PRESENTATION LAYER                            |
|             com.vityarthi.smartcampus.Main (CLI & Demo Runner)           |
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
  - Browse Course Catalog          - View Enrolled Students        - Add / Modify Courses
  - Register Course (FFCS)         - Mark Lecture Attendance       - View Seat Occupancy
  - Drop Course                    - Assign Course Grades          - View Attendance Debarments
  - View Transcript & CGPA
  - Check Tuition Fees
```

### 7.2 Process Flow / Workflow Diagram (Course Registration)
```
[Start Registration]
        |
        v
[Check Student Exists & Active]
        |
        v
[Validate Prerequisites Passed?] ---> [NO] ---> Throw CampusException
        | [YES]
        v
[Credits + Course <= Max Credits (27)?] ---> [NO] ---> Throw CreditLimitExceededException
        | [YES]
        v
[Timetable Slot Clash Detected?] ---> [YES] ---> Throw SlotClashException
        | [NO]
        v
[Is Course Section Full?]
     /        \
 [NO]          [YES]
  |              |
  v              v
[Set Status:  [Enqueue to Waitlist]
 ENROLLED]    [Set Status: WAITLISTED]
  |              |
  v              v
[Update Student Credits & Persist]
        |
        v
[Dispatch Background Notification]
```

### 7.3 Sequence Diagram (Registration & Waitlist Promotion)
```
Student              EnrollmentService            Course              NotificationWorker
   |                         |                       |                         |
   |--- registerCourse() --->|                       |                         |
   |                         |--- isFull()? -------->|                         |
   |                         |<-- true (Full) -------|                         |
   |                         |--- enrollStudent() -->| (added to waitlist)     |
   |                         |------------------------------------------------>| dispatch("Waitlisted")
   |<-- Return WAITLISTED ---|                       |                         |
   |                         |                       |                         |
   | (Another Student Drops) |                       |                         |
   |                         |--- dropCourse() ----->|                         |
   |                         |--- removeStudent() -->|                         |
   |                         |<-- promotedStudentId -| (promoted from queue)   |
   |                         |------------------------------------------------>| dispatch("Seat Promoted")
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
+-----------------+     +-----------------+     +-----------------+
|     Student     |     |     Faculty     |     |      Admin      |
+-----------------+     +-----------------+     +-----------------+
| - regNo: String |     | - empId: String |     | - dept: String  |
| - credits: int  |     | - dept: String  |     | - level: int    |
| - cgpa: double  |     | - cabin: String |     +-----------------+
+-----------------+     +-----------------+

                     +------------------------+
                     |      <<abstract>>      |
                     |         Course         |
                     +------------------------+
                     | - courseCode: String   |
                     | - courseTitle: String  |
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

### 7.5 Database / Storage ER Diagram
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
1. **Separation of Domain Entities and Services**: Isolating business logic into dedicated services (`EnrollmentService`, `CourseManagementService`) guarantees adherence to Single Responsibility (SRP) and enables modular unit testing.
2. **Polymorphic Course Pricing**: Defining `calculateTuitionFee()` as an abstract method on `Course` allows `TheoryCourse` and `LabCourse` to encapsulate their pricing policies cleanly without fragile `if-else` type-checking.
3. **Decoupled File Persistence**: Implementing the `DataStore` interface allows in-memory collections (`ConcurrentHashMap`) to serve sub-millisecond lookups while asynchronously synchronizing state to disk CSV files.
4. **Daemon Notification Thread**: Utilizing a dedicated background `NotificationWorker` thread with a `LinkedBlockingQueue` ensures high-latency notification operations never block core student registration workflows.
5. **Domain-Specific Exception Hierarchy**: Creating a custom exception hierarchy extending `CampusException` (`SlotClashException`, `CreditLimitExceededException`, `CourseFullException`) allows precise error reporting and graceful client recovery.

---

## 9. Implementation Details
The system was developed strictly with standard Java 21 features:
- **Collections Framework**: Employs `LinkedHashSet` for deterministic enrollment rosters, `ConcurrentHashMap` for thread-safe caching, `LinkedList` for FIFO waitlist queuing, and `List` for immutable prerequisite definitions.
- **Java Streams & Lambdas**: Used extensively in query pipelines to filter active enrollments, search catalog listings, and calculate grade point aggregates.
- **Atomic Concurrency Controls**: Utilizes `AtomicBoolean` and `AtomicInteger` for thread synchronization and collision-free ID generation.
- **Java File I/O & NIO.2**: Employs `java.nio.file.Path`, `Files.newBufferedReader()`, and `BufferedWriter` to handle CSV serialization.

---

## 10. Screenshots / Results

### 10.1 Automated Validation Test Execution
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

### 10.2 Academic Transcript Output
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
Testing was executed through two complementary layers:
1. **Automated Unit Testing (`SystemTestSuite`)**: Eleven distinct unit tests validating polymorphism, input regular expressions, slot collision boundary scenarios, credit overload exceptions, queue promotions, and file serialization.
2. **Integration & Flow Testing (`Main --demo`)**: An automated end-to-end demonstration running through authentication, catalog querying, slot clash triggering, waitlist handling, attendance logging, grade submission, fee computation, and transcript generation.

---

## 12. Challenges Faced & Solutions
- **Compound Slot Overlaps**: Laboratory sessions occupy multi-period blocks (e.g., `L1+L2`). Comparing single string codes was insufficient.
  - *Solution*: Decomposed compound slot codes into atomic token sets (`L1`, `L2`) and checked set intersections.
- **Race Conditions in Waitlist Promotion**: Multiple simultaneous drops and adds could cause inconsistent seat counts.
  - *Solution*: Synchronized critical registration and drop blocks on course and enrollment objects, leveraging `ConcurrentHashMap` and thread-safe queues.
- **Dynamic GPA Weighting with Incomplete Courses**: Courses in progress with unassigned grades must not skew CGPA calculations.
  - *Solution*: Filtered out courses with `Grade.NOT_ASSIGNED` during summation, only dividing by the sum of completed, graded credits.

---

## 13. Learnings & Key Takeaways
- Mastery over core Object-Oriented paradigms (encapsulation, abstraction, polymorphic dispatch).
- Practical implementation of Java Concurrency utilities (`BlockingQueue`, `AtomicBoolean`, daemon worker threads).
- Architecting maintainable, multi-tiered systems separating storage, business logic, and presentation.
- Rigorous exception handling with custom unchecked exceptions reflecting domain business rules.

---

## 14. Future Enhancements
- **Spring Boot & RESTful Web APIs**: Transition the service layer to Spring Boot microservices with JWT-based authentication.
- **Relational Database Migration**: Replace CSV persistence with PostgreSQL via Spring Data JPA and Hibernate.
- **Web Interface (React / Angular)**: Develop a modern single-page frontend with responsive timetable grid visualization.

---

## 15. References
1. Schildt, Herbert. *Java: The Complete Reference*, 12th Edition. McGraw-Hill Education, 2021.
2. Bloch, Joshua. *Effective Java*, 3rd Edition. Addison-Wesley Professional, 2018.
3. Oracle Corporation. *Java SE 21 Documentation and Specifications*, 2023.
4. Vellore Institute of Technology. *FFCS Course Allocation and Academic Regulations Handbook*, 2025.
