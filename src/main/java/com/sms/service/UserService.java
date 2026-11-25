package com.sms.service;

import com.sms.dto.LoginRequest;
import com.sms.dto.LoginResponse;
import com.sms.model.*;
import com.sms.repository.*;
import com.sms.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AdminRepository adminRepository;

    public UserService(UserRepository userRepository, StudentRepository studentRepository,
                       FacultyRepository facultyRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.adminRepository = adminRepository;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return new LoginResponse(false, "Username and password cannot be empty");
        }

        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername().trim());
        if (userOpt.isEmpty()) {
            return new LoginResponse(false, "Invalid username or password");
        }

        User user = userOpt.get();
        if (!user.isEnabled()) {
            return new LoginResponse(false, "Account is disabled. Please contact administrator.");
        }

        if (!PasswordUtil.checkPassword(loginRequest.getPassword(), user.getPassword())) {
            return new LoginResponse(false, "Invalid username or password");
        }

        if (loginRequest.getRole() != null && !loginRequest.getRole().trim().isEmpty()) {
            String expectedRole = loginRequest.getRole().trim().toUpperCase();
            if (!expectedRole.startsWith("ROLE_")) {
                expectedRole = "ROLE_" + expectedRole;
            }
            if (!user.getRole().name().equalsIgnoreCase(expectedRole)) {
                return new LoginResponse(false, "User does not have access for role: " + loginRequest.getRole());
            }
        }

        Long entityId = null;
        String rollOrEmpId = null;

        if (user.getRole() == Role.ROLE_STUDENT) {
            Optional<Student> studentOpt = studentRepository.findByUser(user);
            if (studentOpt.isPresent()) {
                entityId = studentOpt.get().getId();
                rollOrEmpId = studentOpt.get().getRollNumber();
            }
        } else if (user.getRole() == Role.ROLE_FACULTY) {
            Optional<Faculty> facultyOpt = facultyRepository.findByUser(user);
            if (facultyOpt.isPresent()) {
                entityId = facultyOpt.get().getId();
                rollOrEmpId = facultyOpt.get().getEmployeeId();
            }
        } else if (user.getRole() == Role.ROLE_ADMIN) {
            Optional<Admin> adminOpt = adminRepository.findByUser(user);
            if (adminOpt.isPresent()) {
                entityId = adminOpt.get().getId();
                rollOrEmpId = "ADMIN";
            }
        }

        return new LoginResponse(true, "Login successful", user.getId(), user.getUsername(),
                user.getFullName(), user.getEmail(), user.getRole(), entityId, rollOrEmpId);
    }

    public User createUser(String username, String plainPassword, Role role, String fullName, String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        User user = new User(username, hashedPassword, role, fullName, email, phone);
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
