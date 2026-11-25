package com.sms.service;

import com.sms.dto.DashboardStatsDto;
import com.sms.model.Student;
import com.sms.repository.CourseRepository;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.FacultyRepository;
import com.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceService attendanceService;

    public DashboardService(StudentRepository studentRepository,
                            FacultyRepository facultyRepository,
                            CourseRepository courseRepository,
                            DepartmentRepository departmentRepository,
                            AttendanceService attendanceService) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.attendanceService = attendanceService;
    }

    public DashboardStatsDto getDashboardStats() {
        long totalStudents = studentRepository.count();
        long totalFaculty = facultyRepository.count();
        long totalCourses = courseRepository.count();
        long totalDepartments = departmentRepository.count();

        List<Student> students = studentRepository.findAll();
        long lowAttendanceCount = 0;
        double totalAttendanceSum = 0.0;

        for (Student s : students) {
            double pct = attendanceService.getOverallAttendancePercentage(s.getId());
            totalAttendanceSum += pct;
            if (pct < 75.0) {
                lowAttendanceCount++;
            }
        }

        double avgAttendance = (!students.isEmpty()) ? (totalAttendanceSum / students.size()) : 100.0;

        return new DashboardStatsDto(totalStudents, totalFaculty, totalCourses,
                totalDepartments, lowAttendanceCount, avgAttendance);
    }
}
