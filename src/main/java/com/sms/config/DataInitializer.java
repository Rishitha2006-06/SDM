package com.sms.config;

import com.sms.model.*;
import com.sms.repository.*;
import com.sms.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;

    public DataInitializer(UserRepository userRepository,
                           AdminRepository adminRepository,
                           DepartmentRepository departmentRepository,
                           FacultyRepository facultyRepository,
                           CourseRepository courseRepository,
                           StudentRepository studentRepository,
                           StudentCourseRepository studentCourseRepository,
                           AttendanceRepository attendanceRepository,
                           MarksRepository marksRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.studentCourseRepository = studentCourseRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            return; // Data already initialized
        }

        System.out.println(">>> Initializing Student Management System Seed Data...");

        // 1. Departments
        Department cse = departmentRepository.save(new Department("CSE", "Computer Science & Engineering", "Computing and Software Systems"));
        Department ece = departmentRepository.save(new Department("ECE", "Electronics & Communication", "Circuits, Signals and Communications"));
        Department it = departmentRepository.save(new Department("IT", "Information Technology", "Enterprise IT, Cloud & Cybersecurity"));
        Department mech = departmentRepository.save(new Department("MECH", "Mechanical Engineering", "Mechanics, Design & Thermodynamics"));

        // 2. Admin User
        User adminUser = userRepository.save(new User(
                "admin",
                PasswordUtil.hashPassword("admin@123"),
                Role.ROLE_ADMIN,
                "System Administrator",
                "admin@sms.edu",
                "+1-800-555-0100"
        ));
        adminRepository.save(new Admin("System Administrator", "admin@sms.edu", "+1-800-555-0100", adminUser));

        // 3. Faculty Users & Profiles
        User facUser1 = userRepository.save(new User(
                "prof.smith",
                PasswordUtil.hashPassword("faculty@123"),
                Role.ROLE_FACULTY,
                "Prof. Alan Smith",
                "alan.smith@sms.edu",
                "+1-555-0141"
        ));
        Faculty fac1 = facultyRepository.save(new Faculty(
                "FAC-101", "Alan", "Smith", "alan.smith@sms.edu", "+1-555-0141",
                "Professor & HOD", "Ph.D. in Computer Science", LocalDate.of(2018, 6, 15), cse, facUser1
        ));

        User facUser2 = userRepository.save(new User(
                "dr.raj",
                PasswordUtil.hashPassword("faculty@123"),
                Role.ROLE_FACULTY,
                "Dr. Rajesh Kumar",
                "rajesh.kumar@sms.edu",
                "+1-555-0182"
        ));
        Faculty fac2 = facultyRepository.save(new Faculty(
                "FAC-102", "Rajesh", "Kumar", "rajesh.kumar@sms.edu", "+1-555-0182",
                "Associate Professor", "M.Tech, Ph.D. in AI", LocalDate.of(2020, 1, 10), cse, facUser2
        ));

        // 4. Courses
        Course c1 = courseRepository.save(new Course("CS501", "Database Management Systems", 4, 5, cse, fac1));
        Course c2 = courseRepository.save(new Course("CS502", "Full Stack Web Development", 4, 5, cse, fac2));
        Course c3 = courseRepository.save(new Course("CS503", "Operating Systems", 3, 5, cse, fac1));
        Course c4 = courseRepository.save(new Course("CS504", "Computer Networks", 3, 5, cse, fac2));

        // 5. Students
        User stuUser1 = userRepository.save(new User(
                "STU2024001",
                PasswordUtil.hashPassword("student@123"),
                Role.ROLE_STUDENT,
                "Alex Morgan",
                "alex.morgan@sms.edu",
                "+1-555-0199"
        ));
        Student s1 = studentRepository.save(new Student(
                "STU2024001", "Alex", "Morgan", "alex.morgan@sms.edu", "+1-555-0199",
                LocalDate.of(2003, 4, 12), "Male", "O+", "42 Campus Avenue, Silicon City",
                5, LocalDate.of(2022, 8, 1), "Robert Morgan", "+1-555-0111", cse, stuUser1
        ));

        User stuUser2 = userRepository.save(new User(
                "STU2024002",
                PasswordUtil.hashPassword("student@123"),
                Role.ROLE_STUDENT,
                "Emma Watson",
                "emma.watson@sms.edu",
                "+1-555-0198"
        ));
        Student s2 = studentRepository.save(new Student(
                "STU2024002", "Emma", "Watson", "emma.watson@sms.edu", "+1-555-0198",
                LocalDate.of(2004, 2, 20), "Female", "A+", "88 University Road, Silicon City",
                5, LocalDate.of(2022, 8, 1), "David Watson", "+1-555-0112", cse, stuUser2
        ));

        User stuUser3 = userRepository.save(new User(
                "STU2024003",
                PasswordUtil.hashPassword("student@123"),
                Role.ROLE_STUDENT,
                "Rahul Sharma",
                "rahul.sharma@sms.edu",
                "+1-555-0197"
        ));
        Student s3 = studentRepository.save(new Student(
                "STU2024003", "Rahul", "Sharma", "rahul.sharma@sms.edu", "+1-555-0197",
                LocalDate.of(2003, 11, 5), "Male", "B+", "12 Academic Lane, Silicon City",
                5, LocalDate.of(2022, 8, 1), "Sunil Sharma", "+1-555-0113", cse, stuUser3
        ));

        // 6. Enrollments
        for (Course c : List.of(c1, c2, c3, c4)) {
            studentCourseRepository.save(new StudentCourse(s1, c, LocalDate.of(2024, 7, 1), "ACTIVE"));
            studentCourseRepository.save(new StudentCourse(s2, c, LocalDate.of(2024, 7, 1), "ACTIVE"));
            studentCourseRepository.save(new StudentCourse(s3, c, LocalDate.of(2024, 7, 1), "ACTIVE"));
        }

        // 7. Seed Attendance Records
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= 15; i++) {
            LocalDate date = today.minusDays(i);

            // s1: high attendance (~90%)
            attendanceRepository.save(new Attendance(s1, c1, date, (i % 8 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT, "Regular lecture", fac1));
            attendanceRepository.save(new Attendance(s1, c2, date, (i % 10 == 0) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT, "Hands-on lab", fac2));

            // s2: good attendance (~85%)
            attendanceRepository.save(new Attendance(s2, c1, date, (i % 6 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT, "Regular lecture", fac1));
            attendanceRepository.save(new Attendance(s2, c2, date, (i % 7 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT, "Hands-on lab", fac2));

            // s3: LOW attendance (~60% to demonstrate low attendance warning!)
            AttendanceStatus s3Status = (i % 2 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
            attendanceRepository.save(new Attendance(s3, c1, date, s3Status, "Regular lecture", fac1));
            attendanceRepository.save(new Attendance(s3, c2, date, s3Status, "Hands-on lab", fac2));
        }

        // 8. Seed Marks
        // Alex Morgan (s1)
        marksRepository.save(new Marks(s1, c1, ExamType.MID_TERM_1, 28.0, 30.0, "A+", "Outstanding theoretical foundation", fac1, today.minusDays(40)));
        marksRepository.save(new Marks(s1, c1, ExamType.MID_TERM_2, 27.5, 30.0, "A+", "Excellent SQL queries", fac1, today.minusDays(20)));
        marksRepository.save(new Marks(s1, c1, ExamType.ASSIGNMENT, 19.0, 20.0, "A+", "ER diagram assignment completed", fac1, today.minusDays(10)));
        marksRepository.save(new Marks(s1, c1, ExamType.SEMESTER_FINAL, 92.0, 100.0, "A+", "Exceptional performance in end sem", fac1, today.minusDays(5)));

        marksRepository.save(new Marks(s1, c2, ExamType.MID_TERM_1, 29.0, 30.0, "A+", "Great Spring Boot architecture", fac2, today.minusDays(40)));
        marksRepository.save(new Marks(s1, c2, ExamType.MID_TERM_2, 28.0, 30.0, "A+", "REST API well implemented", fac2, today.minusDays(20)));
        marksRepository.save(new Marks(s1, c2, ExamType.ASSIGNMENT, 20.0, 20.0, "A+", "Full stack project submitted", fac2, today.minusDays(10)));
        marksRepository.save(new Marks(s1, c2, ExamType.SEMESTER_FINAL, 94.0, 100.0, "A+", "Top score in full stack project exam", fac2, today.minusDays(5)));

        // Emma Watson (s2)
        marksRepository.save(new Marks(s2, c1, ExamType.MID_TERM_1, 24.0, 30.0, "A", "Very good conceptual clarity", fac1, today.minusDays(40)));
        marksRepository.save(new Marks(s2, c1, ExamType.SEMESTER_FINAL, 82.0, 100.0, "A", "Strong database normalization answers", fac1, today.minusDays(5)));
        marksRepository.save(new Marks(s2, c2, ExamType.MID_TERM_1, 26.0, 30.0, "A+", "Clean code and well formatted UI", fac2, today.minusDays(40)));
        marksRepository.save(new Marks(s2, c2, ExamType.SEMESTER_FINAL, 85.0, 100.0, "A", "Great API endpoints", fac2, today.minusDays(5)));

        // Rahul Sharma (s3)
        marksRepository.save(new Marks(s3, c1, ExamType.MID_TERM_1, 15.0, 30.0, "D", "Needs improvement in relational algebra", fac1, today.minusDays(40)));
        marksRepository.save(new Marks(s3, c1, ExamType.SEMESTER_FINAL, 58.0, 100.0, "D", "Borderline pass, requires attendance", fac1, today.minusDays(5)));

        System.out.println(">>> Student Management System Seed Data initialized successfully!");
    }
}
