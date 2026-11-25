package com.sms.service;

import com.sms.dto.FacultyDto;
import com.sms.model.Course;
import com.sms.model.Department;
import com.sms.model.Faculty;
import com.sms.model.Role;
import com.sms.model.User;
import com.sms.repository.CourseRepository;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.FacultyRepository;
import com.sms.repository.UserRepository;
import com.sms.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public FacultyService(FacultyRepository facultyRepository,
                          DepartmentRepository departmentRepository,
                          CourseRepository courseRepository,
                          UserRepository userRepository) {
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    public Optional<Faculty> getFacultyById(Long id) {
        return facultyRepository.findById(id);
    }

    public Optional<Faculty> getFacultyByEmployeeId(String employeeId) {
        return facultyRepository.findByEmployeeId(employeeId);
    }

    public List<Course> getAssignedCourses(Long facultyId) {
        Optional<Faculty> facultyOpt = facultyRepository.findById(facultyId);
        if (facultyOpt.isPresent()) {
            return courseRepository.findByFaculty(facultyOpt.get());
        }
        return List.of();
    }

    @Transactional
    public Faculty createFaculty(FacultyDto dto) {
        if (facultyRepository.existsByEmployeeId(dto.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists: " + dto.getEmployeeId());
        }
        if (facultyRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID: " + dto.getDepartmentId()));
        }

        String password = (dto.getPassword() != null && !dto.getPassword().trim().isEmpty())
                ? dto.getPassword().trim()
                : "faculty@123";

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(dto.getEmployeeId(), hashedPassword, Role.ROLE_FACULTY,
                dto.getFirstName() + " " + dto.getLastName(), dto.getEmail(), dto.getPhone());
        user = userRepository.save(user);

        Faculty faculty = new Faculty(dto.getEmployeeId(), dto.getFirstName(), dto.getLastName(),
                dto.getEmail(), dto.getPhone(), dto.getDesignation(), dto.getQualification(),
                dto.getJoiningDate(), department, user);

        return facultyRepository.save(faculty);
    }

    @Transactional
    public Faculty updateFaculty(Long id, FacultyDto dto) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + id));

        faculty.setFirstName(dto.getFirstName());
        faculty.setLastName(dto.getLastName());
        faculty.setPhone(dto.getPhone());
        faculty.setDesignation(dto.getDesignation());
        faculty.setQualification(dto.getQualification());
        if (dto.getJoiningDate() != null) {
            faculty.setJoiningDate(dto.getJoiningDate());
        }

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID: " + dto.getDepartmentId()));
            faculty.setDepartment(department);
        }

        if (faculty.getUser() != null) {
            faculty.getUser().setFullName(dto.getFirstName() + " " + dto.getLastName());
            faculty.getUser().setPhoneNumber(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                faculty.getUser().setPassword(PasswordUtil.hashPassword(dto.getPassword().trim()));
            }
            userRepository.save(faculty.getUser());
        }

        return facultyRepository.save(faculty);
    }

    @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found with ID: " + id));

        // Unlink courses
        List<Course> courses = courseRepository.findByFaculty(faculty);
        for (Course c : courses) {
            c.setFaculty(null);
            courseRepository.save(c);
        }

        User user = faculty.getUser();
        facultyRepository.delete(faculty);
        if (user != null) {
            userRepository.delete(user);
        }
    }
}
