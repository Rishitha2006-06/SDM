package com.sms.repository;

import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.model.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    List<StudentCourse> findByStudent(Student student);
    List<StudentCourse> findByCourse(Course course);
    Optional<StudentCourse> findByStudentAndCourse(Student student, Course course);
    boolean existsByStudentAndCourse(Student student, Course course);
    void deleteByStudentAndCourse(Student student, Course course);
}
