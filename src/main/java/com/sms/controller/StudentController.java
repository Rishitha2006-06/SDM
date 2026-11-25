package com.sms.controller;

import com.sms.dto.*;
import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.service.AttendanceService;
import com.sms.service.MarksService;
import com.sms.service.ReportService;
import com.sms.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;
    private final ReportService reportService;

    public StudentController(StudentService studentService,
                             AttendanceService attendanceService,
                             MarksService marksService,
                             ReportService reportService) {
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.marksService = marksService;
        this.reportService = reportService;
    }

    private Long getStudentId(HttpServletRequest request, Long queryId) {
        if (queryId != null) return queryId;
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("ENTITY_ID") != null) {
            return (Long) session.getAttribute("ENTITY_ID");
        }
        return 1L; // fallback default
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Student>> getProfile(
            @RequestParam(required = false) Long studentId,
            HttpServletRequest request) {
        Long id = getStudentId(request, studentId);
        return studentService.getStudentById(id)
                .map(s -> ResponseEntity.ok(ApiResponse.ok("Profile retrieved", s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Student>> updateProfile(
            @RequestParam(required = false) Long studentId,
            @RequestBody StudentDto dto,
            HttpServletRequest request) {
        Long id = getStudentId(request, studentId);
        try {
            Student updated = studentService.updatePermittedProfile(id, dto);
            return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<Course>>> getEnrolledCourses(
            @RequestParam(required = false) Long studentId,
            HttpServletRequest request) {
        Long id = getStudentId(request, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Enrolled courses", studentService.getEnrolledCourses(id)));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryDto>>> getAttendanceSummary(
            @RequestParam(required = false) Long studentId,
            HttpServletRequest request) {
        Long id = getStudentId(request, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Attendance summary", attendanceService.getStudentAttendanceSummary(id)));
    }

    @GetMapping("/attendance/overall")
    public ResponseEntity<ApiResponse<Double>> getOverallAttendance(
            @RequestParam(required = false) Long studentId,
            HttpServletRequest request) {
        Long id = getStudentId(request, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Overall attendance", attendanceService.getOverallAttendancePercentage(id)));
    }

    @GetMapping("/marks")
    public ResponseEntity<ApiResponse<List<MarksRecordDto>>> getMarks(
            @RequestParam(required = false) Long studentId,
            HttpServletRequest request) {
        Long id = getStudentId(request, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Examination marks", marksService.getStudentMarks(id)));
    }

    @GetMapping("/report-card")
    public ResponseEntity<ApiResponse<ReportCardDto>> getReportCard(
            @RequestParam(required = false) Long studentId,
            HttpServletRequest request) {
        Long id = getStudentId(request, studentId);
        return ResponseEntity.ok(ApiResponse.ok("Report card", reportService.generateStudentReportCard(id)));
    }
}
