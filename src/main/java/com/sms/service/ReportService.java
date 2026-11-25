package com.sms.service;

import com.sms.dto.ReportCardDto;
import com.sms.model.Course;
import com.sms.model.ExamType;
import com.sms.model.Marks;
import com.sms.model.Student;
import com.sms.model.StudentCourse;
import com.sms.repository.MarksRepository;
import com.sms.repository.StudentCourseRepository;
import com.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final MarksRepository marksRepository;
    private final AttendanceService attendanceService;

    public ReportService(StudentRepository studentRepository,
                         StudentCourseRepository studentCourseRepository,
                         MarksRepository marksRepository,
                         AttendanceService attendanceService) {
        this.studentRepository = studentRepository;
        this.studentCourseRepository = studentCourseRepository;
        this.marksRepository = marksRepository;
        this.attendanceService = attendanceService;
    }

    public ReportCardDto generateStudentReportCard(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        ReportCardDto report = new ReportCardDto();
        report.setRollNumber(student.getRollNumber());
        report.setStudentName(student.getFullName());
        report.setDepartmentName(student.getDepartment() != null ? student.getDepartment().getDepartmentName() : "General");
        report.setSemester(student.getCurrentSemester());
        report.setAcademicYear("2025 - 2026");

        double overallAttendance = attendanceService.getOverallAttendancePercentage(studentId);
        report.setOverallAttendancePercentage(overallAttendance);
        report.setLowAttendanceAlert(overallAttendance < 75.0);

        List<StudentCourse> enrolled = studentCourseRepository.findByStudent(student);
        List<ReportCardDto.CourseMarksReport> courseReports = new ArrayList<>();

        double grandTotalObtained = 0.0;
        double grandTotalMax = 0.0;
        double totalCredits = 0.0;
        double totalGradePoints = 0.0;

        for (StudentCourse sc : enrolled) {
            Course course = sc.getCourse();
            ReportCardDto.CourseMarksReport cr = new ReportCardDto.CourseMarksReport();
            cr.setCourseCode(course.getCourseCode());
            cr.setCourseName(course.getCourseName());
            cr.setCredits(course.getCredits());

            List<Marks> courseMarks = marksRepository.findByStudentAndCourse(student, course);

            double courseObtained = 0.0;
            double courseMax = 0.0;

            for (Marks m : courseMarks) {
                if (m.getExamType() == ExamType.MID_TERM_1) {
                    cr.setMid1(m.getMarksObtained());
                } else if (m.getExamType() == ExamType.MID_TERM_2) {
                    cr.setMid2(m.getMarksObtained());
                } else if (m.getExamType() == ExamType.SEMESTER_FINAL) {
                    cr.setFinalExam(m.getMarksObtained());
                } else if (m.getExamType() == ExamType.ASSIGNMENT) {
                    cr.setAssignment(m.getMarksObtained());
                }
                courseObtained += (m.getMarksObtained() != null ? m.getMarksObtained() : 0.0);
                courseMax += (m.getMaxMarks() != null ? m.getMaxMarks() : 0.0);
            }

            cr.setTotalObtained(courseObtained);
            cr.setMaxMarks(courseMax > 0 ? courseMax : 100.0);
            String courseGrade = Marks.calculateGrade(courseObtained, cr.getMaxMarks());
            cr.setGrade(courseGrade);

            // Calculate attendance percentage for this course
            attendanceService.getStudentAttendanceSummary(studentId).stream()
                    .filter(s -> s.getCourseId().equals(course.getId()))
                    .findFirst()
                    .ifPresent(s -> cr.setAttendancePercentage(s.getPercentage()));

            courseReports.add(cr);

            grandTotalObtained += courseObtained;
            grandTotalMax += cr.getMaxMarks();

            // CGPA Calculation: Grade points * credits
            double gp = getGradePoint(courseGrade);
            totalGradePoints += (gp * (course.getCredits() != null ? course.getCredits() : 3));
            totalCredits += (course.getCredits() != null ? course.getCredits() : 3);
        }

        report.setCourses(courseReports);
        report.setTotalMarksObtained(grandTotalObtained);
        report.setTotalMaxMarks(grandTotalMax);

        double pct = grandTotalMax > 0 ? (grandTotalObtained / grandTotalMax) * 100.0 : 0.0;
        report.setPercentage(Math.round(pct * 100.0) / 100.0);
        report.setOverallGrade(Marks.calculateGrade(grandTotalObtained, grandTotalMax));

        double cgpa = totalCredits > 0 ? (totalGradePoints / totalCredits) : 0.0;
        report.setCgpa(Math.round(cgpa * 100.0) / 100.0);

        return report;
    }

    private double getGradePoint(String grade) {
        if ("A+".equals(grade)) return 10.0;
        if ("A".equals(grade)) return 9.0;
        if ("B".equals(grade)) return 8.0;
        if ("C".equals(grade)) return 7.0;
        if ("D".equals(grade)) return 6.0;
        if ("E".equals(grade)) return 5.0;
        return 0.0;
    }
}
