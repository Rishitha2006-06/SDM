package com.sms.service;

import com.sms.dto.CourseDto;
import com.sms.model.Course;
import com.sms.model.Department;
import com.sms.model.Faculty;
import com.sms.model.Student;
import com.sms.model.StudentCourse;
import com.sms.repository.CourseRepository;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.FacultyRepository;
import com.sms.repository.StudentCourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentCourseRepository studentCourseRepository;

    public CourseService(CourseRepository courseRepository,
                         DepartmentRepository departmentRepository,
                         FacultyRepository facultyRepository,
                         StudentCourseRepository studentCourseRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getCoursesByDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .map(courseRepository::findByDepartment)
                .orElse(List.of());
    }

    public List<Course> getCoursesByFaculty(Long facultyId) {
        return facultyRepository.findById(facultyId)
                .map(courseRepository::findByFaculty)
                .orElse(List.of());
    }

    public List<Student> getEnrolledStudents(Long courseId) {
        return courseRepository.findById(courseId)
                .map(c -> studentCourseRepository.findByCourse(c).stream()
                        .map(StudentCourse::getStudent)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Transactional
    public Course createCourse(CourseDto dto) {
        if (courseRepository.existsByCourseCode(dto.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + dto.getCourseCode());
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID: " + dto.getDepartmentId()));
        }

        Faculty faculty = null;
        if (dto.getFacultyId() != null) {
            faculty = facultyRepository.findById(dto.getFacultyId())
                    .orElse(null);
        }

        Course course = new Course(dto.getCourseCode(), dto.getCourseName(), dto.getCredits(),
                dto.getSemester(), department, faculty);

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + id));

        course.setCourseName(dto.getCourseName());
        course.setCredits(dto.getCredits());
        course.setSemester(dto.getSemester());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Department ID: " + dto.getDepartmentId()));
            course.setDepartment(department);
        }

        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(dto.getFacultyId()).orElse(null);
            course.setFaculty(faculty);
        } else {
            course.setFaculty(null);
        }

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
