package com.sms.service;

import com.sms.dto.StudentDto;
import com.sms.model.Course;
import com.sms.model.Department;
import com.sms.model.Role;
import com.sms.model.Student;
import com.sms.model.StudentCourse;
import com.sms.model.User;
import com.sms.repository.*;
import com.sms.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;

    public StudentService(StudentRepository studentRepository,
                          DepartmentRepository departmentRepository,
                          UserRepository userRepository,
                          StudentCourseRepository studentCourseRepository,
                          CourseRepository courseRepository,
                          AttendanceRepository attendanceRepository,
                          MarksRepository marksRepository) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.studentCourseRepository = studentCourseRepository;
        this.courseRepository = courseRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByRollNumber(String rollNumber) {
        return studentRepository.findByRollNumber(rollNumber);
    }

    public Optional<Student> getStudentByUser(User user) {
        return studentRepository.findByUser(user);
    }

    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return studentRepository.findAll();
        }
        return studentRepository.searchStudents(query.trim());
    }

    public List<Student> filterStudents(Long deptId, Integer semester, String query) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return studentRepository.filterStudents(deptId, semester, cleanQuery);
    }

    public List<Course> getEnrolledCourses(Long studentId) {
        return studentRepository.findById(studentId)
                .map(s -> studentCourseRepository.findByStudent(s).stream()
                        .map(StudentCourse::getCourse)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Transactional
    public Student createStudent(StudentDto dto) {
        if (studentRepository.existsByRollNumber(dto.getRollNumber())) {
            throw new IllegalArgumentException("Roll number already exists: " + dto.getRollNumber());
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID: " + dto.getDepartmentId()));
        }

        String password = (dto.getPassword() != null && !dto.getPassword().trim().isEmpty())
                ? dto.getPassword().trim()
                : "student@123";

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(dto.getRollNumber(), hashedPassword, Role.ROLE_STUDENT,
                dto.getFirstName() + " " + dto.getLastName(), dto.getEmail(), dto.getPhone());
        user = userRepository.save(user);

        Student student = new Student(dto.getRollNumber(), dto.getFirstName(), dto.getLastName(),
                dto.getEmail(), dto.getPhone(), dto.getDob(), dto.getGender(), dto.getBloodGroup(),
                dto.getAddress(), dto.getCurrentSemester() != null ? dto.getCurrentSemester() : 1,
                dto.getAdmissionDate() != null ? dto.getAdmissionDate() : LocalDate.now(),
                dto.getGuardianName(), dto.getGuardianPhone(), department, user);

        student = studentRepository.save(student);

        // Auto-enroll student into courses for their department and semester
        if (department != null && student.getCurrentSemester() != null) {
            List<Course> courses = courseRepository.findByDepartment(department);
            for (Course course : courses) {
                if (course.getSemester().equals(student.getCurrentSemester())) {
                    studentCourseRepository.save(new StudentCourse(student, course, LocalDate.now(), "ACTIVE"));
                }
            }
        }

        return student;
    }

    @Transactional
    public Student updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setPhone(dto.getPhone());
        student.setDob(dto.getDob());
        student.setGender(dto.getGender());
        student.setBloodGroup(dto.getBloodGroup());
        student.setAddress(dto.getAddress());
        student.setCurrentSemester(dto.getCurrentSemester());
        student.setGuardianName(dto.getGuardianName());
        student.setGuardianPhone(dto.getGuardianPhone());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID: " + dto.getDepartmentId()));
            student.setDepartment(department);
        }

        if (student.getUser() != null) {
            student.getUser().setFullName(dto.getFirstName() + " " + dto.getLastName());
            student.getUser().setPhoneNumber(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                student.getUser().setPassword(PasswordUtil.hashPassword(dto.getPassword().trim()));
            }
            userRepository.save(student.getUser());
        }

        return studentRepository.save(student);
    }

    @Transactional
    public Student updatePermittedProfile(Long studentId, StudentDto dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        if (dto.getPhone() != null) student.setPhone(dto.getPhone());
        if (dto.getAddress() != null) student.setAddress(dto.getAddress());
        if (dto.getBloodGroup() != null) student.setBloodGroup(dto.getBloodGroup());
        if (dto.getGuardianName() != null) student.setGuardianName(dto.getGuardianName());
        if (dto.getGuardianPhone() != null) student.setGuardianPhone(dto.getGuardianPhone());

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            if (student.getUser() != null) {
                student.getUser().setPassword(PasswordUtil.hashPassword(dto.getPassword().trim()));
                userRepository.save(student.getUser());
            }
        }

        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        // Delete attendance, marks, courses enrollments
        attendanceRepository.deleteAll(attendanceRepository.findByStudent(student));
        marksRepository.deleteAll(marksRepository.findByStudent(student));
        studentCourseRepository.deleteAll(studentCourseRepository.findByStudent(student));

        User user = student.getUser();
        studentRepository.delete(student);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Transactional
    public void enrollCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!studentCourseRepository.existsByStudentAndCourse(student, course)) {
            studentCourseRepository.save(new StudentCourse(student, course, LocalDate.now(), "ACTIVE"));
        }
    }
}
