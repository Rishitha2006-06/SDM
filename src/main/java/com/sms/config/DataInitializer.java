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

        System.out.println(">>> Initializing Student Management System Seed Data with Indian Names...");

        // -------------------------------------------------------------
        // 1. Departments
        // -------------------------------------------------------------
        Department cse = departmentRepository.save(new Department("CSE", "Computer Science & Engineering", "Computing, Software Engineering & AI"));
        Department ece = departmentRepository.save(new Department("ECE", "Electronics & Communication", "Circuits, Signal Processing & VLSI"));
        Department it = departmentRepository.save(new Department("IT", "Information Technology", "Cloud Computing, Cyber Security & DevOps"));
        Department mech = departmentRepository.save(new Department("MECH", "Mechanical Engineering", "Robotics, Thermal Engineering & CAD"));

        // -------------------------------------------------------------
        // 2. Admin User
        // -------------------------------------------------------------
        User adminUser = userRepository.save(new User(
                "admin",
                PasswordUtil.hashPassword("admin@123"),
                Role.ROLE_ADMIN,
                "Dr. Vikramaditya Joshi (Administrator)",
                "admin@sms.edu",
                "+91-9876543210"
        ));
        adminRepository.save(new Admin("Dr. Vikramaditya Joshi", "admin@sms.edu", "+91-9876543210", adminUser));

        // -------------------------------------------------------------
        // 3. Faculty Members (10 Teachers - Indian Names)
        // -------------------------------------------------------------
        Faculty fac1 = createFacultyMember("dr.rajesh", "FAC-101", "Rajesh", "Sharma", "rajesh.sharma@sms.edu",
                "+91-9811100001", "Professor & HOD", "Ph.D. in Computer Science (IIT Bombay)", LocalDate.of(2016, 7, 1), cse);

        Faculty fac2 = createFacultyMember("prof.sunita", "FAC-102", "Sunita", "Verma", "sunita.verma@sms.edu",
                "+91-9811100002", "Associate Professor", "M.Tech, Ph.D. in AI (IIT Delhi)", LocalDate.of(2018, 1, 15), cse);

        Faculty fac3 = createFacultyMember("dr.amit", "FAC-103", "Amit", "Patel", "amit.patel@sms.edu",
                "+91-9811100003", "Professor", "Ph.D. in Distributed Systems", LocalDate.of(2017, 8, 10), cse);

        Faculty fac4 = createFacultyMember("prof.priya", "FAC-104", "Priya", "Nair", "priya.nair@sms.edu",
                "+91-9811100004", "Assistant Professor", "M.Tech in Software Engineering", LocalDate.of(2021, 6, 20), cse);

        Faculty fac5 = createFacultyMember("dr.vikram", "FAC-105", "Vikram", "Rao", "vikram.rao@sms.edu",
                "+91-9811100005", "Associate Professor & HOD", "Ph.D. in Signal Processing (IISc)", LocalDate.of(2017, 3, 12), ece);

        Faculty fac6 = createFacultyMember("prof.ananya", "FAC-106", "Ananya", "Iyer", "ananya.iyer@sms.edu",
                "+91-9811100006", "Assistant Professor", "M.Tech in VLSI Design", LocalDate.of(2020, 9, 1), ece);

        Faculty fac7 = createFacultyMember("dr.suresh", "FAC-107", "Suresh", "Reddy", "suresh.reddy@sms.edu",
                "+91-9811100007", "Professor & HOD", "Ph.D. in Cyber Security", LocalDate.of(2015, 4, 18), it);

        Faculty fac8 = createFacultyMember("prof.kavita", "FAC-108", "Kavita", "Joshi", "kavita.joshi@sms.edu",
                "+91-9811100008", "Associate Professor", "M.Tech in Cloud & Distributed Systems", LocalDate.of(2019, 11, 5), it);

        Faculty fac9 = createFacultyMember("dr.manoj", "FAC-109", "Manoj", "Gupta", "manoj.gupta@sms.edu",
                "+91-9811100009", "Professor & HOD", "Ph.D. in Thermal & Fluid Systems", LocalDate.of(2014, 5, 25), mech);

        Faculty fac10 = createFacultyMember("prof.deepika", "FAC-110", "Deepika", "Kulkarni", "deepika.kulkarni@sms.edu",
                "+91-9811100010", "Assistant Professor", "M.Tech in Robotics & Automation", LocalDate.of(2022, 2, 14), mech);

        // -------------------------------------------------------------
        // 4. Courses (12 Courses across departments)
        // -------------------------------------------------------------
        Course c1 = courseRepository.save(new Course("CS501", "Database Management Systems", 4, 5, cse, fac1));
        Course c2 = courseRepository.save(new Course("CS502", "Full Stack Web Development", 4, 5, cse, fac2));
        Course c3 = courseRepository.save(new Course("CS503", "Operating Systems", 3, 5, cse, fac3));
        Course c4 = courseRepository.save(new Course("CS504", "Computer Networks", 3, 5, cse, fac4));
        Course c5 = courseRepository.save(new Course("CS505", "Artificial Intelligence & ML", 4, 5, cse, fac2));
        Course c6 = courseRepository.save(new Course("CS506", "Cloud Computing Architecture", 3, 5, cse, fac3));
        Course c7 = courseRepository.save(new Course("EC401", "Digital Signal Processing", 4, 4, ece, fac5));
        Course c8 = courseRepository.save(new Course("EC402", "VLSI Design & Microelectronics", 4, 4, ece, fac6));
        Course c9 = courseRepository.save(new Course("IT501", "Cyber Security & Cryptography", 4, 5, it, fac7));
        Course c10 = courseRepository.save(new Course("IT502", "Enterprise Cloud DevOps", 3, 5, it, fac8));
        Course c11 = courseRepository.save(new Course("ME301", "Applied Thermodynamics", 4, 3, mech, fac9));
        Course c12 = courseRepository.save(new Course("ME302", "Robotics & Automation Design", 3, 3, mech, fac10));

        // -------------------------------------------------------------
        // 5. Students (12 Students - Indian Names)
        // -------------------------------------------------------------
        Student s1 = createStudentMember("STU2024001", "Aarav", "Sharma", "aarav.sharma@sms.edu", "+91-9820011101",
                LocalDate.of(2003, 5, 14), "Male", "O+", "Flat 402, Shanti Heights, Pune", 5,
                LocalDate.of(2022, 8, 1), "Ramesh Sharma", "+91-9820011199", cse);

        Student s2 = createStudentMember("STU2024002", "Diya", "Patel", "diya.patel@sms.edu", "+91-9820011102",
                LocalDate.of(2004, 2, 20), "Female", "A+", "B-12, Green Glen Layout, Bengaluru", 5,
                LocalDate.of(2022, 8, 1), "Bhavesh Patel", "+91-9820011198", cse);

        Student s3 = createStudentMember("STU2024003", "Rohan", "Verma", "rohan.verma@sms.edu", "+91-9820011103",
                LocalDate.of(2003, 11, 5), "Male", "B+", "15 Lakeview Residency, Hyderabad", 5,
                LocalDate.of(2022, 8, 1), "Sunil Verma", "+91-9820011197", cse);

        Student s4 = createStudentMember("STU2024004", "Ananya", "Reddy", "ananya.reddy@sms.edu", "+91-9820011104",
                LocalDate.of(2004, 7, 18), "Female", "AB+", "77 Jubliee Hills, Hyderabad", 5,
                LocalDate.of(2022, 8, 1), "Venkat Reddy", "+91-9820011196", cse);

        Student s5 = createStudentMember("STU2024005", "Ishaan", "Gupta", "ishaan.gupta@sms.edu", "+91-9820011105",
                LocalDate.of(2003, 9, 29), "Male", "O+", "Plot 24, Civil Lines, Jaipur", 5,
                LocalDate.of(2022, 8, 1), "Alok Gupta", "+91-9820011195", cse);

        Student s6 = createStudentMember("STU2024006", "Sneha", "Nair", "sneha.nair@sms.edu", "+91-9820011106",
                LocalDate.of(2004, 3, 11), "Female", "B+", "18 Panampilly Nagar, Kochi", 5,
                LocalDate.of(2022, 8, 1), "Muraleedharan Nair", "+91-9820011194", cse);

        Student s7 = createStudentMember("STU2024007", "Aditya", "Joshi", "aditya.joshi@sms.edu", "+91-9820011107",
                LocalDate.of(2003, 12, 2), "Male", "A+", "55 Kothrud, Pune", 5,
                LocalDate.of(2022, 8, 1), "Prakash Joshi", "+91-9820011193", cse);

        Student s8 = createStudentMember("STU2024008", "Tanvi", "Kulkarni", "tanvi.kulkarni@sms.edu", "+91-9820011108",
                LocalDate.of(2004, 8, 25), "Female", "O+", "104 Model Colony, Pune", 5,
                LocalDate.of(2022, 8, 1), "Nitin Kulkarni", "+91-9820011192", cse);

        Student s9 = createStudentMember("STU2024009", "Siddharth", "Rao", "siddharth.rao@sms.edu", "+91-9820011109",
                LocalDate.of(2003, 4, 16), "Male", "B+", "89 Indiranagar, Bengaluru", 5,
                LocalDate.of(2022, 8, 1), "Nagesh Rao", "+91-9820011191", cse);

        Student s10 = createStudentMember("STU2024010", "Pooja", "Deshmukh", "pooja.deshmukh@sms.edu", "+91-9820011110",
                LocalDate.of(2004, 1, 30), "Female", "A+", "33 Shivaji Nagar, Nagpur", 5,
                LocalDate.of(2022, 8, 1), "Sanjay Deshmukh", "+91-9820011190", cse);

        Student s11 = createStudentMember("STU2024011", "Karan", "Mehta", "karan.mehta@sms.edu", "+91-9820011111",
                LocalDate.of(2004, 6, 8), "Male", "O+", "601 Navrangpura, Ahmedabad", 4,
                LocalDate.of(2022, 8, 1), "Hitesh Mehta", "+91-9820011189", ece);

        Student s12 = createStudentMember("STU2024012", "Riya", "Sen", "riya.sen@sms.edu", "+91-9820011112",
                LocalDate.of(2003, 10, 19), "Female", "AB+", "12 Salt Lake Sector 2, Kolkata", 5,
                LocalDate.of(2022, 8, 1), "Anirban Sen", "+91-9820011188", it);

        // -------------------------------------------------------------
        // 6. Enrollments
        // -------------------------------------------------------------
        List<Student> cseStudents = List.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10);
        List<Course> cseCourses = List.of(c1, c2, c3, c4, c5, c6);

        for (Student s : cseStudents) {
            for (Course c : cseCourses) {
                studentCourseRepository.save(new StudentCourse(s, c, LocalDate.of(2024, 7, 1), "ACTIVE"));
            }
        }

        // ECE enrollments
        studentCourseRepository.save(new StudentCourse(s11, c7, LocalDate.of(2024, 7, 1), "ACTIVE"));
        studentCourseRepository.save(new StudentCourse(s11, c8, LocalDate.of(2024, 7, 1), "ACTIVE"));

        // IT enrollments
        studentCourseRepository.save(new StudentCourse(s12, c9, LocalDate.of(2024, 7, 1), "ACTIVE"));
        studentCourseRepository.save(new StudentCourse(s12, c10, LocalDate.of(2024, 7, 1), "ACTIVE"));

        // -------------------------------------------------------------
        // 7. Seed Attendance Records (Last 15 class dates)
        // -------------------------------------------------------------
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= 15; i++) {
            LocalDate date = today.minusDays(i);

            for (Student s : cseStudents) {
                // Determine attendance rate
                AttendanceStatus c1Status = AttendanceStatus.PRESENT;
                AttendanceStatus c2Status = AttendanceStatus.PRESENT;

                if (s.getRollNumber().equals("STU2024003")) {
                    // Rohan Verma: LOW attendance (~53%) to trigger critical shortage alert
                    c1Status = (i % 2 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
                    c2Status = (i % 3 == 0) ? AttendanceStatus.LATE : (i % 2 == 0 ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT);
                } else if (s.getRollNumber().equals("STU2024007")) {
                    // Aditya Joshi: LOW attendance (~60%) to trigger warning alert
                    c1Status = (i % 2 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
                    c2Status = (i % 5 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
                } else if (s.getRollNumber().equals("STU2024001") || s.getRollNumber().equals("STU2024008")) {
                    // Aarav & Tanvi: High attendance (>93%)
                    c1Status = (i == 10) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
                    c2Status = AttendanceStatus.PRESENT;
                } else {
                    // Other students: Good attendance (80% - 90%)
                    c1Status = (i % 7 == 0) ? AttendanceStatus.ABSENT : ((i % 11 == 0) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT);
                    c2Status = (i % 6 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
                }

                attendanceRepository.save(new Attendance(s, c1, date, c1Status, "Regular lecture", fac1));
                attendanceRepository.save(new Attendance(s, c2, date, c2Status, "Hands-on lab", fac2));
            }

            // ECE & IT attendance
            attendanceRepository.save(new Attendance(s11, c7, date, (i % 8 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT, "DSP Lecture", fac5));
            attendanceRepository.save(new Attendance(s12, c9, date, (i % 10 == 0) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT, "Security Lab", fac7));
        }

        // -------------------------------------------------------------
        // 8. Seed Examination Marks & Grades
        // -------------------------------------------------------------
        // Aarav Sharma (s1) - Top Performer
        seedMarksForStudent(s1, c1, 28.5, 30.0, 29.0, 30.0, 19.5, 20.0, 93.0, 100.0, fac1, today);
        seedMarksForStudent(s1, c2, 29.0, 30.0, 28.5, 30.0, 20.0, 20.0, 95.0, 100.0, fac2, today);

        // Diya Patel (s2)
        seedMarksForStudent(s2, c1, 26.0, 30.0, 27.0, 30.0, 18.0, 20.0, 88.0, 100.0, fac1, today);
        seedMarksForStudent(s2, c2, 27.5, 30.0, 28.0, 30.0, 19.0, 20.0, 90.0, 100.0, fac2, today);

        // Rohan Verma (s3) - Low Attendance & Needs Improvement
        seedMarksForStudent(s3, c1, 14.0, 30.0, 16.0, 30.0, 12.0, 20.0, 52.0, 100.0, fac1, today);
        seedMarksForStudent(s3, c2, 15.5, 30.0, 17.0, 30.0, 13.0, 20.0, 55.0, 100.0, fac2, today);

        // Ananya Reddy (s4)
        seedMarksForStudent(s4, c1, 25.0, 30.0, 24.5, 30.0, 17.5, 20.0, 84.0, 100.0, fac1, today);
        seedMarksForStudent(s4, c2, 26.0, 30.0, 25.0, 30.0, 18.0, 20.0, 85.0, 100.0, fac2, today);

        // Ishaan Gupta (s5)
        seedMarksForStudent(s5, c1, 27.0, 30.0, 26.0, 30.0, 18.5, 20.0, 89.0, 100.0, fac1, today);
        seedMarksForStudent(s5, c2, 26.5, 30.0, 27.0, 30.0, 19.0, 20.0, 88.0, 100.0, fac2, today);

        // Tanvi Kulkarni (s8) - Top Performer
        seedMarksForStudent(s8, c1, 29.5, 30.0, 29.0, 30.0, 20.0, 20.0, 96.0, 100.0, fac1, today);
        seedMarksForStudent(s8, c2, 29.0, 30.0, 29.5, 30.0, 20.0, 20.0, 97.0, 100.0, fac2, today);

        System.out.println(">>> 10+ Teachers, 12 Courses, and 12 Students with Indian names initialized successfully!");
    }

    private Faculty createFacultyMember(String username, String empId, String firstName, String lastName,
                                        String email, String phone, String designation, String qualification,
                                        LocalDate joiningDate, Department dept) {
        User user = userRepository.save(new User(
                username,
                PasswordUtil.hashPassword("faculty@123"),
                Role.ROLE_FACULTY,
                (designation.contains("Dr.") ? "Dr. " : "Prof. ") + firstName + " " + lastName,
                email,
                phone
        ));

        return facultyRepository.save(new Faculty(
                empId, firstName, lastName, email, phone, designation, qualification, joiningDate, dept, user
        ));
    }

    private Student createStudentMember(String rollNo, String firstName, String lastName, String email,
                                        String phone, LocalDate dob, String gender, String bloodGroup,
                                        String address, int semester, LocalDate admissionDate,
                                        String guardianName, String guardianPhone, Department dept) {
        User user = userRepository.save(new User(
                rollNo,
                PasswordUtil.hashPassword("student@123"),
                Role.ROLE_STUDENT,
                firstName + " " + lastName,
                email,
                phone
        ));

        return studentRepository.save(new Student(
                rollNo, firstName, lastName, email, phone, dob, gender, bloodGroup, address,
                semester, admissionDate, guardianName, guardianPhone, dept, user
        ));
    }

    private void seedMarksForStudent(Student s, Course c, double mid1, double maxMid1,
                                     double mid2, double maxMid2, double assign, double maxAssign,
                                     double finalExam, double maxFinal, Faculty faculty, LocalDate today) {
        marksRepository.save(new Marks(s, c, ExamType.MID_TERM_1, mid1, maxMid1,
                Marks.calculateGrade(mid1, maxMid1), "Good performance", faculty, today.minusDays(40)));
        marksRepository.save(new Marks(s, c, ExamType.MID_TERM_2, mid2, maxMid2,
                Marks.calculateGrade(mid2, maxMid2), "Consistent effort", faculty, today.minusDays(20)));
        marksRepository.save(new Marks(s, c, ExamType.ASSIGNMENT, assign, maxAssign,
                Marks.calculateGrade(assign, maxAssign), "Assignment submitted on time", faculty, today.minusDays(10)));
        marksRepository.save(new Marks(s, c, ExamType.SEMESTER_FINAL, finalExam, maxFinal,
                Marks.calculateGrade(finalExam, maxFinal), "Semester final evaluation", faculty, today.minusDays(5)));
    }
}
