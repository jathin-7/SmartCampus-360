# SmartCampus 360 - Academic & Course Registration System

A modular Java project built for **CSE2006 (Programming in Java)** at **VIT Bhopal**.

- **Student Name**: YATHAM JATHINDRA REDDY
- **Registration Number**: 25BAI10611
- **Program**: B.Tech Computer Science (AI & ML)
- **Course**: CSE2006 — Programming in Java

---

## 📌 Project Overview
**SmartCampus 360** is a console-based academic management system inspired by the Fully Flexible Credit System (FFCS) at VIT. It solves common real-world challenges faced by students and faculty during course registration:

1. **Preventing Timetable Clashes**: Automatically flags if a student tries to register for two courses running in the same slot (e.g., Slot `A1` vs `A1`, or composite slots like `L1+L2`).
2. **Automated Waitlists**: When a course section is full, students are placed in a First-Come-First-Served (FIFO) waitlist. When any enrolled student drops the course, the next waitlisted student is automatically promoted and notified.
3. **Credit Management**: Enforces the 27-credit semester cap so students do not overload their timetable.
4. **Attendance Tracking (75% Rule)**: Tracks attendance per course and flags students who fall below the mandatory 75% cutoff to prevent FAT debarment.
5. **Live CGPA Computation**: Calculates weighted GPA dynamically whenever grades (`S`, `A`, `B`, `C`, `D`, `E`, `F`) are recorded.
6. **Data Persistence**: Automatically reads and saves users, courses, and enrollments to clean CSV files in `data/`.

---

## ✨ Key Features

### For Students
- View all available theory and lab courses with live seat availability.
- Register for courses with instant slot clash and credit checks.
- Drop courses and free up seats for waitlisted peers.
- Check personal academic transcript with real-time CGPA.
- Track course-wise attendance percentages.
- View total semester tuition fees.

### For Faculty
- View the list of enrolled students for assigned courses.
- Mark attendance (Present/Absent) session by session.
- Enter final grades for students.

### For Administrators
- Add new theory and lab courses to the catalog with slot and capacity configurations.
- View campus-wide seat occupancy and waitlist sizes.
- View a dedicated list of attendance debarment warnings (<75%).

---

## 💻 Java Concepts Applied
This project was built without any heavy external libraries or databases, using pure Core Java (JDK 21):
- **Object-Oriented Programming (OOP)**:
  - **Abstraction**: Abstract `User` and `Course` base classes; `DataStore` interface.
  - **Inheritance**: `Student`, `Faculty`, `Admin` extend `User`; `TheoryCourse`, `LabCourse` extend `Course`.
  - **Polymorphism**: Overridden `calculateTuitionFee()` (lab courses add an infrastructure fee), overridden `getRoleSpecificDetails()`.
  - **Encapsulation**: Private attributes, defensive copying of collections, and clean getter/setter methods.
- **Custom Exceptions**: Specific domain exceptions including `SlotClashException`, `CreditLimitExceededException`, `CourseFullException`, and `AuthenticationException`.
- **Java Collections Framework**: `ConcurrentHashMap` for thread-safe caching, `LinkedHashSet` for rosters, and `LinkedList` for FIFO waitlists.
- **Multithreading**: A background `NotificationWorker` daemon thread uses a `BlockingQueue` to simulate async message delivery.
- **File I/O (NIO.2)**: Reads and writes structured CSV files (`users.csv`, `courses.csv`, `enrollments.csv`) with automatic seed data initialization.

---

## 📁 Project Structure
```
java project/
├── build_and_run.bat                            # Compile and start the app
├── run_tests.bat                                # Run all automated tests
├── generate_report_pdf.py                       # Compile the PDF report
├── statement.md                                 # Problem statement & scope
├── README.md                                    # This guide
├── Project_Report_CSE2006_25BAI10611.pdf        # Official 15-section project report (PDF)
├── data/                                        # Persistent CSV storage
│   ├── users.csv
│   ├── courses.csv
│   └── enrollments.csv
├── docs/
│   ├── Project_Report.md                        # Full report documentation
│   └── report_template.html                     # Styled HTML template for PDF
└── src/
    └── com/vityarthi/smartcampus/
        ├── Main.java                            # CLI & Demo runner
        ├── model/                               # Domain entities
        ├── service/                             # Business logic & algorithms
        ├── repository/                          # CSV storage engine
        ├── concurrency/                         # Background worker thread
        ├── exception/                           # Custom domain exceptions
        ├── util/                                # Regex validator & console styles
        └── test/                                # Automated unit test suite
```

---

## 🚀 How to Run the Project

### Prerequisites
- Java JDK 21 or later (`javac -version` and `java -version`)

### Quick Start (Windows)
1. **Interactive Console Menu**:
   ```cmd
   .\build_and_run.bat
   ```
2. **Automated Live Demonstration (Runs everything without typing)**:
   ```cmd
   .\build_and_run.bat --demo
   ```
3. **Run All Unit Tests**:
   ```cmd
   .\run_tests.bat
   ```

### Manual Commands
If you prefer running manual commands from PowerShell / Command Prompt:
```cmd
# 1. Compile all Java files into bin/
mkdir bin
javac -d bin (Get-ChildItem -Path src -Recurse -Filter *.java).FullName

# 2. Run the main menu
java -cp bin com.vityarthi.smartcampus.Main

# 3. Or run the automated demo
java -cp bin com.vityarthi.smartcampus.Main --demo

# 4. Or run the test suite
java -cp bin com.vityarthi.smartcampus.test.SystemTestSuite
```

---

## 🧪 Testing & Validation
The project includes a built-in test runner (`SystemTestSuite`) with 11 automated test cases:

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

## 👤 Author
- **Name**: YATHAM JATHINDRA REDDY
- **Registration Number**: 25BAI10611
- **Course**: CSE2006 (Programming in Java)
- **Institution**: Vellore Institute of Technology (VIT Bhopal)
- **Year**: 2025–2026
