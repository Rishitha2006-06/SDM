package com.sms.controller;

import com.sms.dto.*;
import com.sms.model.Course;
import com.sms.model.Department;
import com.sms.model.Faculty;
import com.sms.model.Student;
import com.sms.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final CourseService courseService;
    private final DepartmentService departmentService;
    private final DashboardService dashboardService;
    private final AttendanceService attendanceService;

    public AdminController(StudentService studentService,
                           FacultyService facultyService,
                           CourseService courseService,
                           DepartmentService departmentService,
                           DashboardService dashboardService,
                           AttendanceService attendanceService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.courseService = courseService;
        this.departmentService = departmentService;
        this.dashboardService = dashboardService;
        this.attendanceService = attendanceService;
    }

    // Dashboard Statistics
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok("Stats loaded", dashboardService.getDashboardStats()));
    }

    // Students Management
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.ok("Students retrieved", studentService.getAllStudents()));
    }

    @GetMapping("/students/filter")
    public ResponseEntity<ApiResponse<List<Student>>> filterStudents(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok("Filtered students",
                studentService.filterStudents(departmentId, semester, search)));
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Student>> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(s -> ResponseEntity.ok(ApiResponse.ok("Found", s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/students")
    public ResponseEntity<ApiResponse<Student>> createStudent(@RequestBody StudentDto dto) {
        try {
            Student created = studentService.createStudent(dto);
            return ResponseEntity.ok(ApiResponse.ok("Student created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Student>> updateStudent(@PathVariable Long id, @RequestBody StudentDto dto) {
        try {
            Student updated = studentService.updateStudent(id, dto);
            return ResponseEntity.ok(ApiResponse.ok("Student updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok(ApiResponse.ok("Student deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Faculty Management
    @GetMapping("/faculty")
    public ResponseEntity<ApiResponse<List<Faculty>>> getAllFaculty() {
        return ResponseEntity.ok(ApiResponse.ok("Faculty retrieved", facultyService.getAllFaculty()));
    }

    @GetMapping("/faculty/{id}")
    public ResponseEntity<ApiResponse<Faculty>> getFacultyById(@PathVariable Long id) {
        return facultyService.getFacultyById(id)
                .map(f -> ResponseEntity.ok(ApiResponse.ok("Found", f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/faculty")
    public ResponseEntity<ApiResponse<Faculty>> createFaculty(@RequestBody FacultyDto dto) {
        try {
            Faculty created = facultyService.createFaculty(dto);
            return ResponseEntity.ok(ApiResponse.ok("Faculty created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/faculty/{id}")
    public ResponseEntity<ApiResponse<Faculty>> updateFaculty(@PathVariable Long id, @RequestBody FacultyDto dto) {
        try {
            Faculty updated = facultyService.updateFaculty(id, dto);
            return ResponseEntity.ok(ApiResponse.ok("Faculty updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/faculty/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFaculty(@PathVariable Long id) {
        try {
            facultyService.deleteFaculty(id);
            return ResponseEntity.ok(ApiResponse.ok("Faculty deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Courses Management
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.ok("Courses retrieved", courseService.getAllCourses()));
    }

    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody CourseDto dto) {
        try {
            Course created = courseService.createCourse(dto);
            return ResponseEntity.ok(ApiResponse.ok("Course created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable Long id, @RequestBody CourseDto dto) {
        try {
            Course updated = courseService.updateCourse(id, dto);
            return ResponseEntity.ok(ApiResponse.ok("Course updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok(ApiResponse.ok("Course deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Departments Management
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        return ResponseEntity.ok(ApiResponse.ok("Departments retrieved", departmentService.getAllDepartments()));
    }

    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<Department>> createDepartment(@RequestBody Department dept) {
        try {
            Department created = departmentService.saveDepartment(dept);
            return ResponseEntity.ok(ApiResponse.ok("Department created successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(ApiResponse.ok("Department deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
