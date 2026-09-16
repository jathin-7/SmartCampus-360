# SmartCampus 360 - Integrated Academic & Resource Allocation System

> **Course**: CSE2006 - Programming in Java  
> **Candidate**: YATHAM JATHINDRA REDDY  
> **Registration Number**: 25BAI10611  
> **Institution**: Vellore Institute of Technology (VIT)

---

## 1. Project Overview
**SmartCampus 360** is an enterprise-grade academic management and resource allocation engine engineered in Java 21. Designed specifically to model and solve university operational complexities—such as the Fully Flexible Credit System (FFCS) course registration, timetable slot collision avoidance, quota-limited section waitlists, attendance threshold monitoring, and dynamic CGPA computation—the project demonstrates mastery over Object-Oriented Programming (OOP), the Java Collections Framework, Custom Exception Hierarchies, File I/O Persistence, and Multithreading.

---

## 2. Key Features

### 🎓 Academic & Student Lifecycle Management
- **FFCS Timetable Slot Collision Detector**: Algorithmic validation preventing registration in clashing timetable slots (e.g., Slot `A1` vs `A1`, compound slots like `L1+L2`).
- **Credit Limit Boundary Enforcement**: Real-time enforcement preventing student enrollment beyond the semester maximum (27 credits).
- **Prerequisite Validation**: Automated verification ensuring prerequisite courses are passed prior to enrollment.
- **Fair FIFO Waitlist Queue & Auto-Promotion**: When course quotas are full, students are queued in order; dropping a course automatically promotes the next student without manual staff intervention.
- **Dynamic 10-Point CGPA Calculator**: Weighted average GPA computation (`Sum(Credits * GradePoints) / TotalCredits`) updated on grade submission.
- **75% Attendance Compliance Monitor**: Per-student attendance tracking alerting faculty and administrators to students at risk of exam debarment.

### 🏛️ Object-Oriented Architecture
- **Abstraction & Polymorphism**:
  - `User` abstract class specialized by `Student`, `Faculty`, and `Admin`.
  - `Course` abstract base class specialized by `TheoryCourse` and `LabCourse`, featuring polymorphic tuition fee computations.
- **Encapsulation & Validation**: Strict access modifiers, defensive copying of collections, and regex input validation (`InputValidator`).
- **Robust Custom Exception Hierarchy**: Domain-specific unchecked exceptions (`SlotClashException`, `CreditLimitExceededException`, `CourseFullException`, `AuthenticationException`).

### ⚙️ Concurrency & Persistence
- **Multithreaded Background Dispatcher**: `NotificationWorker` daemon thread executing the Producer-Consumer pattern with `BlockingQueue` and `AtomicBoolean`.
- **Thread-Safe Repository**: Memory-cached storage backed by `ConcurrentHashMap` with CSV serialization to disk in `data/`.

---

## 3. Technologies & Tools Used
- **Programming Language**: Java (SE 21 LTS)
- **Compiler**: `javac` 21
- **Runtime**: Java Virtual Machine (JVM)
- **Design Methodology**: Object-Oriented Analysis & Design (OOAD), Repository Pattern, Producer-Consumer Pattern
- **Persistence**: File I/O (CSV data format)
- **Testing**: Built-in Unit Test Harness (`SystemTestSuite`)
- **Version Control**: Git

---

## 4. Project Directory Structure
```
java project/
├── .gitignore
├── README.md                                    # Project documentation & execution guide
├── statement.md                                 # Problem statement, scope & target users
├── build_and_run.bat                            # Windows compile & launch script
├── run_tests.bat                                # Windows automated test script
├── generate_report_pdf.py                       # Automated report compilation script
├── Project_Report_CSE2006_25BAI10611.pdf        # Official 15-section project report (PDF)
├── data/                                        # Persistent CSV storage
│   ├── users.csv
│   ├── courses.csv
│   └── enrollments.csv
├── docs/                                        # Report documentation and templates
│   ├── Project_Report.md
│   └── report_template.html
└── src/
    └── com/vityarthi/smartcampus/
        ├── Main.java                            # Main entry point (CLI & Demo runner)
        ├── model/                               # OOP domain model hierarchy
        │   ├── User.java
        │   ├── Student.java
        │   ├── Faculty.java
        │   ├── Admin.java
        │   ├── Course.java
        │   ├── TheoryCourse.java
        │   ├── LabCourse.java
        │   ├── Slot.java
        │   ├── Enrollment.java
        │   ├── Grade.java
        │   └── SystemRole.java
        ├── service/                             # Business logic & algorithms
        │   ├── AuthenticationService.java
        │   ├── CourseManagementService.java
        │   ├── EnrollmentService.java
        │   └── AnalyticsReportingService.java
        ├── repository/                          # Persistence & data store
        │   ├── DataStore.java
        │   └── StorageManager.java
        ├── concurrency/                         # Multithreading background worker
        │   └── NotificationWorker.java
        ├── exception/                           # Custom domain exceptions
        │   ├── CampusException.java
        │   ├── SlotClashException.java
        │   ├── CreditLimitExceededException.java
        │   ├── CourseFullException.java
        │   ├── AuthenticationException.java
        │   └── EntityNotFoundException.java
        ├── util/                                # Input sanitization & formatting
        │   ├── InputValidator.java
        │   └── ConsoleFormatter.java
        └── test/                                # Automated test suite
            ├── TestCase.java
            └── SystemTestSuite.java
```

---

## 5. Steps to Install & Run

### Prerequisites
- JDK 21 or higher installed and accessible via command line (`java -version`, `javac -version`).

### Option A: Using Batch Scripts (Recommended on Windows)
1. **Compile and Run Interactive Menu**:
   ```cmd
   build_and_run.bat
   ```
2. **Run Live Automated Demonstration (Zero-Input Mode)**:
   ```cmd
   build_and_run.bat --demo
   ```
3. **Execute Automated Test Suite**:
   ```cmd
   run_tests.bat
   ```

### Option B: Manual Command Line Execution
1. Create build directory:
   ```cmd
   mkdir bin
   ```
2. Compile all source files:
   ```cmd
   javac -d bin src/com/vityarthi/smartcampus/exception/*.java src/com/vityarthi/smartcampus/model/*.java src/com/vityarthi/smartcampus/util/*.java src/com/vityarthi/smartcampus/concurrency/*.java src/com/vityarthi/smartcampus/repository/*.java src/com/vityarthi/smartcampus/service/*.java src/com/vityarthi/smartcampus/test/*.java src/com/vityarthi/smartcampus/Main.java
   ```
3. Run the interactive console application:
   ```cmd
   java -cp bin com.vityarthi.smartcampus.Main
   ```
4. Run the automated demo:
   ```cmd
   java -cp bin com.vityarthi.smartcampus.Main --demo
   ```

---

## 6. Instructions for Testing

The system includes a built-in automated test suite (`SystemTestSuite`) with 11 test cases covering core OOP concepts, algorithmic edge cases, and data persistence:

To run tests:
```cmd
java -cp bin com.vityarthi.smartcampus.test.SystemTestSuite
```

### Test Suite Coverage:
1. `OOP - User Hierarchy & Polymorphism`: Tests abstract methods, specialized attributes, and role resolution.
2. `OOP - Course Hierarchy & Polymorphic Tuition Fee`: Verifies theory and lab tuition surcharge computations.
3. `Timetable Slot Clash Detection Logic`: Verifies collision detection for duplicate and compound slot tokens.
4. `Input Validator Regex Verification`: Validates registration number patterns (`25BAI10611`) and course codes (`CSE2006`).
5. `Course Registration & Credit Bounds`: Verifies standard registration workflows and credit accumulation.
6. `Credit Limit Overload Exception Enforcement`: Confirms `CreditLimitExceededException` is thrown when adding beyond max credits.
7. `Registration Timetable Slot Clash Exception`: Confirms `SlotClashException` is triggered on timetable conflict.
8. `Waitlist Queue & Automatic FIFO Seat Promotion`: Verifies queue insertion when section is full and automatic promotion upon drop.
9. `Grading & Dynamic Weighted CGPA Calculation`: Validates weighted grade point math.
10. `Attendance Tracking & Debarment Alert (<75%)`: Validates threshold computation and warning flags.
11. `File I/O Persistence & Reload Engine`: Validates CSV serialization and deserialization across application reboots.

---

## 7. Sample System Output / Demonstration

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

---

## 8. Author & Academic Metadata
- **Student Name**: YATHAM JATHINDRA REDDY
- **Registration Number**: 25BAI10611
- **Program**: B.Tech Artificial Intelligence & Machine Learning
- **Course Code**: CSE2006 (Programming in Java)
- **Institution**: Vellore Institute of Technology (VIT Bhopal)
