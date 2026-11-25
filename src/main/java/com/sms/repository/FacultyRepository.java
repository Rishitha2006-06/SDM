package com.sms.repository;

import com.sms.model.Department;
import com.sms.model.Faculty;
import com.sms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByEmployeeId(String employeeId);
    Optional<Faculty> findByUser(User user);
    Optional<Faculty> findByEmail(String email);
    List<Faculty> findByDepartment(Department department);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
}
