package com.sms.repository;

import com.sms.model.Department;
import com.sms.model.Student;
import com.sms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByUser(User user);
    Optional<Student> findByEmail(String email);
    List<Student> findByDepartment(Department department);
    List<Student> findByCurrentSemester(Integer semester);
    boolean existsByRollNumber(String rollNumber);
    boolean existsByEmail(String email);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Student> searchStudents(@Param("query") String query);

    @Query("SELECT s FROM Student s WHERE " +
           "(:deptId IS NULL OR s.department.id = :deptId) AND " +
           "(:semester IS NULL OR s.currentSemester = :semester) AND " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Student> filterStudents(@Param("deptId") Long deptId,
                                 @Param("semester") Integer semester,
                                 @Param("query") String query);
}
