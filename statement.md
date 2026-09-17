# Project Statement: SmartCampus 360

**Course**: CSE2006 - Programming in Java  
**Student**: YATHAM JATHINDRA REDDY  
**Registration No**: 25BAI10611  
**Institution**: Vellore Institute of Technology (VIT Bhopal)  

---

## 1. Problem Statement
Every semester at VIT, course registration takes place through the Fully Flexible Credit System (FFCS). During registration, thousands of students log in at the same time to choose their theory and lab courses, select faculty members, and fit everything into a weekly timetable of slots (such as A1, B1, C1, L1+L2, etc.).

However, during this fast-paced process, students and faculty face several practical issues:
1. **Timetable Slot Clashes:** It is easy for a student to accidentally pick a theory or lab course that conflicts with an already registered class on the same day and time (for example, registering for two subjects in Slot A1, or a lab session overlapping with a lecture).
2. **Course Section Limits & Lack of Automatic Waitlists:** High-demand courses reach maximum capacity in minutes. When a course is full, students have no transparent way to know their queue position. More importantly, if an enrolled student drops the course later, there is no automatic system to immediately promote the next waiting student.
3. **Credit Overload:** Students have a maximum limit of 27 credits per semester. Manually keeping track of theory and lab credits can lead to registration errors.
4. **Attendance Debarment Risk:** At VIT, maintaining at least 75% attendance in every course is mandatory to avoid being debarred from the Final Assessment Test (FAT). Students often realize their attendance is short only when it is too late.
5. **Scattered Academic Tracking:** Calculating semester CGPA weighted by course credits is usually done on separate calculators rather than directly inside the course portal.

**SmartCampus 360** is a console-based academic management system designed to solve these exact problems. It provides automated timetable clash detection, fair FIFO waitlist handling with instant promotion on course drops, credit limit checks, real-time attendance alerts, and live CGPA calculation.

---

## 2. Scope of the Project
The project covers the core academic lifecycle of university students and faculty during a semester:

### What the project does:
- **User Roles:** Distinct functionality for Students, Faculty members, and Administrators.
- **Course Modeling:** Handles both Theory courses (lectures + tutorials) and Laboratory courses (practical sessions + lab software environments), with different credit structures and fee calculations.
- **Slot Collision Detection:** Checks both standard lecture slots (A1, B1, C1, etc.) and composite lab slots (like L1+L2) to prevent any schedule overlap.
- **Credit Limit Checks:** Restricts enrollment to a maximum of 27 credits per semester.
- **Waitlist Queue with Auto-Promotion:** When a course is full, students are placed in a First-Come, First-Served (FIFO) waitlist. When an enrolled student drops the course, the next student on the waitlist is automatically promoted and notified.
- **Attendance Monitoring:** Faculty can mark attendance per session; the system calculates the percentage and automatically flags students below the 75% requirement.
- **Grading & Dynamic CGPA:** Faculty can assign 10-point scale grades (S, A, B, C, D, E, F), and the student's cumulative GPA is updated dynamically based on credit weights.
- **Persistent Storage:** All student data, course catalogs, and enrollment records are saved to CSV files so state is preserved across restarts.
- **Background Notification Worker:** A multithreaded background worker simulates asynchronous alerts for waitlist promotions and registration updates.

### What is outside the scope:
- Real payment gateway integration (fees are computed polymorphically, but actual card/UPI transactions are not processed).
- Hardware biometric attendance integration (attendance is updated digitally by faculty).

---

## 3. Target Users
1. **Students (Primary Users):**
   - Browse the course catalog with live seat availability.
   - Register for theory and lab courses with automatic clash and credit checking.
   - Join waitlists when courses are full and get promoted automatically.
   - View their official academic transcript, current CGPA, and attendance status.
   - Check tuition fee breakdowns for enrolled subjects.

2. **Faculty Members:**
   - View rosters of students enrolled in their assigned sections.
   - Mark daily lecture/lab attendance.
   - Enter final grades for students.

3. **Academic Administrators:**
   - Add new courses, configure time slots, and set seat quotas.
   - View campus-wide course occupancy and waitlist sizes.
   - Monitor attendance debarment warning lists to take timely academic interventions.

---

## 4. High-Level Features
- **Role-Based Login System:** Secure access with separate dashboards for Student, Faculty, and Admin.
- **Smart Slot Clash Detector:** Instant validation preventing any timetable overlaps.
- **Fair Waitlist Engine:** Uses a FIFO queue to give students seats in the exact order they joined when vacancies open.
- **Polymorphic Course System:** Object-oriented design separating theory and lab courses with specialized attributes and fee calculations.
- **Live CGPA Calculator:** Dynamically recalculates weighted GPA whenever a new grade is entered.
- **Attendance Compliance Alert:** Warns students and faculty as soon as attendance drops below 75%.
- **Background Concurrency:** Uses a Java worker thread with a `BlockingQueue` to handle notifications in the background.
- **Built-in Automated Demo:** Includes a one-command `--demo` flag that walks through all features step-by-step for easy evaluation.
