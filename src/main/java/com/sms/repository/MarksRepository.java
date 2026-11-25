package com.sms.repository;

import com.sms.model.Course;
import com.sms.model.ExamType;
import com.sms.model.Marks;
import com.sms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {
    List<Marks> findByStudent(Student student);
    List<Marks> findByCourse(Course course);
    List<Marks> findByStudentAndCourse(Student student, Course course);
    Optional<Marks> findByStudentAndCourseAndExamType(Student student, Course course, ExamType examType);
    List<Marks> findByCourseAndExamType(Course course, ExamType examType);
}
