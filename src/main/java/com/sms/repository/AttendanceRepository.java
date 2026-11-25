package com.sms.repository;

import com.sms.model.Attendance;
import com.sms.model.AttendanceStatus;
import com.sms.model.Course;
import com.sms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByCourse(Course course);
    List<Attendance> findByStudentAndCourse(Student student, Course course);
    List<Attendance> findByCourseAndAttendanceDate(Course course, LocalDate date);
    Optional<Attendance> findByStudentAndCourseAndAttendanceDate(Student student, Course course, LocalDate date);

    long countByStudent(Student student);
    long countByStudentAndStatus(Student student, AttendanceStatus status);

    long countByStudentAndCourse(Student student, Course course);
    long countByStudentAndCourseAndStatus(Student student, Course course, AttendanceStatus status);

    @Query("SELECT a FROM Attendance a WHERE a.course = :course ORDER BY a.attendanceDate DESC")
    List<Attendance> findRecentByCourse(@Param("course") Course course);
}
