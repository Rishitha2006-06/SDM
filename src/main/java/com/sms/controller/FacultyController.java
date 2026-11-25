package com.sms.controller;

import com.sms.dto.*;
import com.sms.model.Course;
import com.sms.model.ExamType;
import com.sms.model.Marks;
import com.sms.model.Student;
import com.sms.service.AttendanceService;
import com.sms.service.CourseService;
import com.sms.service.FacultyService;
import com.sms.service.MarksService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyService facultyService;
    private final CourseService courseService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;

    public FacultyController(FacultyService facultyService,
                             CourseService courseService,
                             AttendanceService attendanceService,
                             MarksService marksService) {
        this.facultyService = facultyService;
        this.courseService = courseService;
        this.attendanceService = attendanceService;
        this.marksService = marksService;
    }

    private Long getFacultyId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("ENTITY_ID") != null) {
            return (Long) session.getAttribute("ENTITY_ID");
        }
        return null;
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<Course>>> getAssignedCourses(
            @RequestParam(required = false) Long facultyId,
            HttpServletRequest request) {
        Long fId = (facultyId != null) ? facultyId : getFacultyId(request);
        if (fId == null) {
            return ResponseEntity.ok(ApiResponse.ok("All courses fallback", courseService.getAllCourses()));
        }
        return ResponseEntity.ok(ApiResponse.ok("Assigned courses", facultyService.getAssignedCourses(fId)));
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<ApiResponse<List<Student>>> getEnrolledStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.ok("Enrolled students", courseService.getEnrolledStudents(courseId)));
    }

    @PostMapping("/attendance/batch")
    public ResponseEntity<ApiResponse<Void>> markBatchAttendance(
            @RequestBody BatchAttendanceDto dto,
            @RequestParam(required = false) Long facultyId,
            HttpServletRequest request) {
        try {
            Long fId = (facultyId != null) ? facultyId : getFacultyId(request);
            attendanceService.markBatchAttendance(dto, fId);
            return ResponseEntity.ok(ApiResponse.ok("Attendance marked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/attendance/history")
    public ResponseEntity<ApiResponse<List<AttendanceRecordDto>>> getAttendanceHistory(
            @RequestParam Long courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok("Attendance history",
                attendanceService.getCourseAttendanceByDate(courseId, date)));
    }

    @PostMapping("/marks")
    public ResponseEntity<ApiResponse<Marks>> enterMarks(
            @RequestBody MarksRecordDto dto,
            @RequestParam(required = false) Long facultyId,
            HttpServletRequest request) {
        try {
            Long fId = (facultyId != null) ? facultyId : getFacultyId(request);
            Marks saved = marksService.saveMarks(dto, fId);
            return ResponseEntity.ok(ApiResponse.ok("Marks saved successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/marks")
    public ResponseEntity<ApiResponse<List<MarksRecordDto>>> getCourseMarks(
            @RequestParam Long courseId,
            @RequestParam(required = false) ExamType examType) {
        return ResponseEntity.ok(ApiResponse.ok("Marks list", marksService.getCourseMarks(courseId, examType)));
    }
}
