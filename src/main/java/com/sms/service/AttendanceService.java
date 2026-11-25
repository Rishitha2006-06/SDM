package com.sms.service;

import com.sms.dto.AttendanceRecordDto;
import com.sms.dto.AttendanceSummaryDto;
import com.sms.dto.BatchAttendanceDto;
import com.sms.model.*;
import com.sms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final StudentCourseRepository studentCourseRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository,
                             FacultyRepository facultyRepository,
                             StudentCourseRepository studentCourseRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    @Transactional
    public Attendance markAttendance(Long studentId, Long courseId, LocalDate date,
                                     AttendanceStatus status, String remarks, Long facultyId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        Faculty faculty = facultyId != null ? facultyRepository.findById(facultyId).orElse(null) : null;

        Optional<Attendance> existing = attendanceRepository.findByStudentAndCourseAndAttendanceDate(student, course, date);
        Attendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            attendance.setStatus(status);
            attendance.setRemarks(remarks);
            if (faculty != null) attendance.setMarkedBy(faculty);
        } else {
            attendance = new Attendance(student, course, date, status, remarks, faculty);
        }

        return attendanceRepository.save(attendance);
    }

    @Transactional
    public List<Attendance> markBatchAttendance(BatchAttendanceDto batchDto, Long facultyId) {
        Course course = courseRepository.findById(batchDto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + batchDto.getCourseId()));
        Faculty faculty = facultyId != null ? facultyRepository.findById(facultyId).orElse(null) : null;

        List<Attendance> saved = new ArrayList<>();
        LocalDate date = batchDto.getAttendanceDate() != null ? batchDto.getAttendanceDate() : LocalDate.now();

        for (BatchAttendanceDto.StudentAttendanceEntry entry : batchDto.getEntries()) {
            Student student = studentRepository.findById(entry.getStudentId()).orElse(null);
            if (student == null) continue;

            Optional<Attendance> existing = attendanceRepository.findByStudentAndCourseAndAttendanceDate(student, course, date);
            Attendance attendance;
            if (existing.isPresent()) {
                attendance = existing.get();
                attendance.setStatus(entry.getStatus());
                attendance.setRemarks(entry.getRemarks());
                if (faculty != null) attendance.setMarkedBy(faculty);
            } else {
                attendance = new Attendance(student, course, date, entry.getStatus(), entry.getRemarks(), faculty);
            }
            saved.add(attendanceRepository.save(attendance));
        }

        return saved;
    }

    public List<AttendanceRecordDto> getStudentAttendanceRecords(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<Attendance> list;
        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            list = attendanceRepository.findByStudentAndCourse(student, course);
        } else {
            list = attendanceRepository.findByStudent(student);
        }

        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<AttendanceRecordDto> getCourseAttendanceByDate(Long courseId, LocalDate date) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return attendanceRepository.findByCourseAndAttendanceDate(course, date).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AttendanceSummaryDto> getStudentAttendanceSummary(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<StudentCourse> enrollments = studentCourseRepository.findByStudent(student);
        List<AttendanceSummaryDto> summaries = new ArrayList<>();

        for (StudentCourse sc : enrollments) {
            Course course = sc.getCourse();
            long total = attendanceRepository.countByStudentAndCourse(student, course);
            long present = attendanceRepository.countByStudentAndCourseAndStatus(student, course, AttendanceStatus.PRESENT);
            long late = attendanceRepository.countByStudentAndCourseAndStatus(student, course, AttendanceStatus.LATE);
            long absent = attendanceRepository.countByStudentAndCourseAndStatus(student, course, AttendanceStatus.ABSENT);

            // Present + (Late * 0.5) or standard attendance percentage: present / total * 100
            double effectivePresent = present + (late * 0.5);
            double percentage = total > 0 ? (effectivePresent / total) * 100.0 : 100.0;

            summaries.add(new AttendanceSummaryDto(course.getId(), course.getCourseCode(),
                    course.getCourseName(), total, present, absent, late, percentage));
        }

        return summaries;
    }

    public double getOverallAttendancePercentage(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        long total = attendanceRepository.countByStudent(student);
        if (total == 0) return 100.0;

        long present = attendanceRepository.countByStudentAndStatus(student, AttendanceStatus.PRESENT);
        long late = attendanceRepository.countByStudentAndStatus(student, AttendanceStatus.LATE);
        double effectivePresent = present + (late * 0.5);

        double pct = (effectivePresent / total) * 100.0;
        return Math.round(pct * 100.0) / 100.0;
    }

    private AttendanceRecordDto mapToDto(Attendance a) {
        AttendanceRecordDto dto = new AttendanceRecordDto();
        dto.setId(a.getId());
        dto.setStudentId(a.getStudent().getId());
        dto.setStudentRollNo(a.getStudent().getRollNumber());
        dto.setStudentName(a.getStudent().getFullName());
        dto.setCourseId(a.getCourse().getId());
        dto.setCourseName(a.getCourse().getCourseName());
        dto.setAttendanceDate(a.getAttendanceDate());
        dto.setStatus(a.getStatus());
        dto.setRemarks(a.getRemarks());
        return dto;
    }
}
