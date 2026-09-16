# Project Statement: SmartCampus 360

## 1. Problem Statement
In contemporary higher education institutions, academic operations such as course registration, timetable scheduling, seat allocation, prerequisite verification, and attendance tracking are frequently fragmented across disparate, monolithic web portals. During peak registration intervals (such as the Fully Flexible Credit System - FFCS registration cycles at universities like VIT), conventional systems suffer from concurrency bottlenecks, lack real-time slot conflict resolution, and fail to provide transparent, automated waitlist handling when courses reach capacity.

Furthermore, students often lack an integrated tool to monitor credit limits, detect timetable collisions in advance, simulate GPA outcomes based on course credit weightings, and track attendance thresholds to prevent debarment. Faculty and administrators also require streamlined, thread-safe mechanisms to manage section limits, publish attendance updates, and process student promotions from waitlists automatically without manual interventions.

**SmartCampus 360** resolves these systemic bottlenecks by delivering an integrated, high-performance Java-based Academic & Resource Allocation System. Built upon object-oriented design principles, robust data structures, and multithreaded queue processing, SmartCampus 360 provides deterministic slot collision checking, automated FIFO waitlist promotion, polymorphic tuition and credit tracking, and persistent state management.

---

## 2. Scope of the Project
The scope of **SmartCampus 360** encompasses the academic course lifecycle and resource scheduling operations for higher education campuses:
- **Scope Inclusions:**
  - **Academic Structure Modeling:** Modeling diverse course formats (Theory lectures, Tutorials, Laboratory sessions with specialized computing environments).
  - **Timetable Conflict Resolution:** Algorithmic verification of slot overlaps (e.g., standard VIT slots such as A1, B1, C1, L1+L2) ensuring students cannot register for overlapping time commitments.
  - **Credit Regulation:** Dynamic verification of maximum credit limits (e.g., 27 credits per semester) preventing student credit overload.
  - **Automated Waitlisting Engine:** Real-time FIFO queuing when course sections reach capacity, with automated seat promotion upon course drop events.
  - **Academic Progress & Grading:** Letter grade assignment (S, A, B, C, D, E, F) and dynamic credit-weighted CGPA computation.
  - **Attendance Auditing:** Real-time tracking of lecture attendance with automated flagging of students dropping below the mandatory 75% threshold.
  - **Persistence & Threaded Notifications:** Persistent file-backed storage (CSV) and background multithreaded worker dispatching notifications asynchronously.
- **Scope Boundaries (Out of Scope for CLI Edition):**
  - Third-party bank payment gateway integration (mocked via polymorphic tuition fee calculation).
  - Physical biometric hardware integration (attendance recorded via faculty console).

---

## 3. Target Users
1. **Students (Primary Users):**
   - Browse active academic course catalogs with real-time seat availability.
   - Register for theory and lab courses with automated timetable clash and credit validation.
   - Monitor waitlist status and receive automated seat allocations.
   - View detailed academic transcripts, credit progress, and live CGPA.
   - Monitor attendance percentages to avert 75% debarment.
2. **Faculty Members / Instructors:**
   - Inspect rosters of enrolled students in assigned course sections.
   - Record lecture attendance on a per-session basis.
   - Enter semester grades and evaluate student academic performance.
3. **Academic Administrators / Registrars:**
   - Administer the university course catalog (create courses, assign slots, define seat quotas).
   - Review campus-wide seat occupancy and waitlist metrics.
   - Audit attendance debarment warning lists across all departments.

---

## 4. High-Level Features
- **Role-Based Access Control (RBAC):** Secure authentication architecture supporting distinct permissions for Student, Faculty, and Admin roles.
- **FFCS Timetable Collision Detector:** Deterministic slot clash algorithm verifying compound and atomic slot schedules.
- **Polymorphic Course & Fee Architecture:** Extensible inheritance hierarchy differentiating Theory and Laboratory courses with dynamic surcharge calculations.
- **Fair FIFO Waitlist Queue & Auto-Promotion Engine:** Thread-safe seat deallocation trigger promoting the earliest waitlisted student immediately when a vacancy emerges.
- **Dynamic 10-Point CGPA Calculator:** Credit-weighted formula calculating grade points across completed courses in real time.
- **Attendance Compliance Engine:** Per-course percentage tracking enforcing the mandatory 75% attendance policy.
- **Background Multithreaded Event Dispatcher:** Non-blocking daemon worker simulating asynchronous message queuing and system alerts.
- **Dual Mode Interface:** Interactive CLI console menu alongside an automated, self-executing demonstration mode (`--demo`) for instant evaluation.
