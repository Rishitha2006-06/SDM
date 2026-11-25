package com.sms.repository;

import com.sms.model.Course;
import com.sms.model.Department;
import com.sms.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByDepartment(Department department);
    List<Course> findByFaculty(Faculty faculty);
    List<Course> findBySemester(Integer semester);
    boolean existsByCourseCode(String courseCode);
}
