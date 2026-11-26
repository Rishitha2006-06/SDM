# Student Management System (SMS) – Java Full Stack Project

A comprehensive web-based application designed for colleges and educational institutions to manage student records, faculty, departments, courses, daily attendance, examination marks, and academic transcripts in one unified portal.

---

## 🛠️ Technology Stack

Strictly developed using the required Java Full Stack internship technology stack:

| Layer | Technology |
|---|---|
| **Frontend** | HTML5, CSS3 |
| **UI Framework** | Bootstrap 5.3 (Responsive layout, Modals, Cards, Navbars) |
| **Dynamic Functionality** | JavaScript (ES6+), jQuery 3.7.1 (AJAX, DOM manipulation, dynamic filters) |
| **Backend** | Java (JDK 21 LTS) |
| **Backend Framework** | Spring Boot 3.4.5 (REST APIs, MVC, Session Security) |
| **ORM** | Hibernate (Spring Data JPA) |
| **Database** | MySQL 8.0+ (with fallback in-memory H2 profile for zero-configuration testing) |
| **Version Control** | Git & GitHub |

---

## 🏛️ Project Architecture & Modules

```
                           +--------------------------------------+
                           |          Browser Client              |
                           |  (HTML5, CSS3, Bootstrap, jQuery)    |
                           +--------------------------------------+
                                             |
                                        HTTP / REST
                                             |
                           +--------------------------------------+
                           |      Spring Boot REST Controllers    |
                           |   (Auth, Admin, Faculty, Student)    |
                           +--------------------------------------+
                                             |
                           +--------------------------------------+
                           |            Service Layer             |
                           |  (Student, Attendance, Marks, etc.)  |
                           +--------------------------------------+
                                             |
                           +--------------------------------------+
                           |     Spring Data JPA / Hibernate      |
                           +--------------------------------------+
                                             |
                           +--------------------------------------+
                           |       MySQL Database (InnoDB)        |
                           +--------------------------------------+
```

### 1. Admin Module
- **Admin Authentication**: Secure login & logout with SHA-256 encrypted passwords.
- **Student Management**: Register new students, update details, delete records, view registered student directory.
- **Search & Filter**: Real-time search across roll numbers, names, departments, and semesters using jQuery AJAX.
- **Faculty Management**: Add faculty members, manage designations, view assigned courses.
- **Course & Department Management**: Create departments, add courses with credit weighting, assign courses to faculty members.
- **Academic Monitoring**: View campus-wide low attendance alerts (&lt; 75%) with one-click guardian emergency phone access.

### 2. Faculty Module
- **Assigned Courses**: View courses and subjects currently assigned to the faculty member.
- **Classroom Attendance**:
  - Date-picker and course selection.
  - Interactive student roster with Present, Late, and Absent status radio pills.
  - One-click **"Mark All Present"** shortcut.
  - Lecture remarks for individual students.
- **Register New Student**: Direct button on the faculty dashboard to register new students and automatically map them to department and semester courses.
- **Examination Marks Entry**:
  - Evaluation type selector: Mid-Term 1 (30), Mid-Term 2 (30), Assignments (20), Semester Final (100), Lab Internal (50).
  - Dynamic grade badge preview (`A+`, `A`, `B`, `C`, `D`, `E`, `F`) calculated live on keystroke via jQuery.
  - Instant save with feedback.
- **Student Performance Reports**: Real-time course attendance percentage and debarment risk badges.

### 3. Student Module
- **Personal Profile**: View roll number, department, semester, guardian details; update permitted contact details and change password.
- **Attendance Percentage Tracker**:
  - Overall aggregate attendance badge.
  - Course-wise attendance bars (Attended, Late, Absent).
- **⚠️ Low Attendance Alerts**: Prominent visual banner automatically displayed when overall attendance drops below 75%.
- **Examination Results**: Tabular breakdown of scores across mid-terms, final exams, and assignments with assigned letter grades.
- **Official Academic Report Card**:
  - Formatted transcript modal with CGPA (out of 10.0), aggregate percentage, and overall grade.
  - Print / Download to PDF capability (`window.print()`).

---

## 🗄️ Database Design

The schema contains 8 normalized relational tables in MySQL (`database_setup.sql`):

1. **`users`**: Login credentials, password hashes, roles (`ROLE_ADMIN`, `ROLE_FACULTY`, `ROLE_STUDENT`), activation status.
2. **`departments`**: Department code (e.g., `CSE`, `ECE`, `IT`, `MECH`), department name, description.
3. **`admin`**: System administrator profiles linked to users.
4. **`faculty`**: Employee ID, designation, qualification, joining date, department foreign key, user foreign key.
5. **`courses`**: Course code, title, credits, semester, department foreign key, assigned faculty foreign key.
6. **`students`**: Roll number, name, DOB, blood group, current semester, guardian details, department foreign key, user foreign key.
7. **`student_courses`**: Course enrollment mapping between students and courses.
8. **`attendance`**: Daily attendance records with date, status (`PRESENT`, `ABSENT`, `LATE`), and remarks.
9. **`marks`**: Examination marks with evaluation type, marks obtained, max marks, letter grade, and faculty remarks.

---

## 🔑 Default Login Credentials

Initial data is automatically pre-seeded by `DataInitializer.java` upon first startup:

| Portal | Username / ID | Password | Role Description |
|---|---|---|---|
| **Administrator** | `admin` | `admin@123` | Dr. Vikramaditya Joshi (Administrator) |
| **Faculty Member** | `dr.rajesh` | `faculty@123` | Dr. Rajesh Sharma (Professor & HOD, CSE) |
| **Faculty Member** | `prof.sunita` | `faculty@123` | Prof. Sunita Verma (Associate Professor, CSE) |
| **Faculty Member** | `dr.amit` | `faculty@123` | Dr. Amit Patel (Professor, CSE) |
| **Faculty Member** | `prof.priya` | `faculty@123` | Prof. Priya Nair (Assistant Professor, CSE) |
| **Student (High Att.)** | `STU2024001` | `student@123` | Aarav Sharma (93% Attendance, 9.5 CGPA) |
| **Student (Good Att.)** | `STU2024002` | `student@123` | Diya Patel (88% Attendance, 9.1 CGPA) |
| **Student (Low Att.)** | `STU2024003` | `student@123` | Rohan Verma (53% Attendance - Demonstrates Alert) |

*(Quick demo buttons are also provided directly on the login screen for one-click auto-fill).*

---

## 🚀 How to Run the Project

### Prerequisites
- **Java**: JDK 21 installed.
- **MySQL Server**: (Optional for production, or run using default in-memory H2 mode).

### Step 1: Database Setup (For MySQL)
1. Open MySQL Workbench or your MySQL shell:
   ```bash
   mysql -u root -p
   ```
2. Execute the provided `database_setup.sql` script:
   ```sql
   source /path/to/database_setup.sql;
   ```
3. Check `src/main/resources/application.properties` and verify your MySQL password:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=root
   ```

### Step 2: Build & Run

#### Running with MySQL (Default):
```bash
.\mvnw.cmd spring-boot:run
```

#### Running with In-Memory Demo Database (Zero MySQL setup needed):
```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```
or run the packaged JAR directly:
```bash
java -jar target/student-management-system-1.0.0.jar --spring.profiles.active=h2
```

### Step 3: Access the Application
Open your web browser and navigate to:
```
http://localhost:8080
```
