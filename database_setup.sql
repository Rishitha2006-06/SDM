-- =============================================================================
-- STUDENT MANAGEMENT SYSTEM - DATABASE SCHEMA & SEED DATA
-- Technologies: MySQL 8.0+, Hibernate, Spring Boot
-- =============================================================================

CREATE DATABASE IF NOT EXISTS `student_management_db`
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `student_management_db`;

-- -----------------------------------------------------------------------------
-- 1. Table: users (Login credentials, roles, security)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(60) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(30) NOT NULL,
    `full_name` VARCHAR(120) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `phone_number` VARCHAR(20),
    `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 2. Table: departments (Academic departments)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `departments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `department_code` VARCHAR(20) NOT NULL UNIQUE,
    `department_name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3. Table: admin (Administrator profile details)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `user_id` BIGINT UNIQUE,
    CONSTRAINT `fk_admin_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 4. Table: faculty (Faculty and teacher details)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `faculty` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `employee_id` VARCHAR(30) NOT NULL UNIQUE,
    `first_name` VARCHAR(60) NOT NULL,
    `last_name` VARCHAR(60) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `designation` VARCHAR(60),
    `qualification` VARCHAR(100),
    `joining_date` DATE,
    `department_id` BIGINT,
    `user_id` BIGINT UNIQUE,
    CONSTRAINT `fk_faculty_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_faculty_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 5. Table: courses (Course details and assigned faculty)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `courses` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `course_code` VARCHAR(20) NOT NULL UNIQUE,
    `course_name` VARCHAR(120) NOT NULL,
    `credits` INT NOT NULL DEFAULT 3,
    `semester` INT NOT NULL DEFAULT 1,
    `department_id` BIGINT,
    `faculty_id` BIGINT,
    CONSTRAINT `fk_course_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_course_faculty` FOREIGN KEY (`faculty_id`) REFERENCES `faculty` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 6. Table: students (Student information and academic details)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `students` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `roll_number` VARCHAR(30) NOT NULL UNIQUE,
    `first_name` VARCHAR(60) NOT NULL,
    `last_name` VARCHAR(60) NOT NULL,
    `email` VARCHAR(120) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `dob` DATE,
    `gender` VARCHAR(15),
    `blood_group` VARCHAR(10),
    `address` VARCHAR(255),
    `current_semester` INT NOT NULL DEFAULT 1,
    `admission_date` DATE,
    `guardian_name` VARCHAR(100),
    `guardian_phone` VARCHAR(20),
    `department_id` BIGINT,
    `user_id` BIGINT UNIQUE,
    CONSTRAINT `fk_student_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 7. Table: student_courses (Enrollment mapping between student & courses)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `student_courses` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `enrollment_date` DATE DEFAULT (CURRENT_DATE),
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    CONSTRAINT `uq_student_course` UNIQUE (`student_id`, `course_id`),
    CONSTRAINT `fk_sc_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_sc_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 8. Table: attendance (Daily attendance records)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendance` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `attendance_date` DATE NOT NULL,
    `status` VARCHAR(20) NOT NULL, -- PRESENT, ABSENT, LATE
    `remarks` VARCHAR(200),
    `marked_by_faculty_id` BIGINT,
    CONSTRAINT `uq_student_course_date` UNIQUE (`student_id`, `course_id`, `attendance_date`),
    CONSTRAINT `fk_att_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_att_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_att_faculty` FOREIGN KEY (`marked_by_faculty_id`) REFERENCES `faculty` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 9. Table: marks (Examination marks and evaluation)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `marks` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `exam_type` VARCHAR(30) NOT NULL, -- MID_TERM_1, MID_TERM_2, SEMESTER_FINAL, ASSIGNMENT
    `marks_obtained` DOUBLE NOT NULL,
    `max_marks` DOUBLE NOT NULL,
    `grade` VARCHAR(10),
    `remarks` VARCHAR(200),
    `evaluated_by_faculty_id` BIGINT,
    `exam_date` DATE DEFAULT (CURRENT_DATE),
    CONSTRAINT `uq_student_course_exam` UNIQUE (`student_id`, `course_id`, `exam_type`),
    CONSTRAINT `fk_marks_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_marks_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_marks_faculty` FOREIGN KEY (`evaluated_by_faculty_id`) REFERENCES `faculty` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- SAMPLE SEED DATA
-- Passwords:
-- admin@123   -> vQeL6W5FqY2e... (Hashed with SHA-256 + SMS_SECURE_SALT_KEY_2026)
-- faculty@123 -> ...
-- student@123 -> ...
-- =============================================================================

-- Departments
INSERT INTO `departments` (`id`, `department_code`, `department_name`, `description`) VALUES
(1, 'CSE', 'Computer Science & Engineering', 'Computing, Algorithms, and Software Engineering'),
(2, 'ECE', 'Electronics & Communication', 'Signals, VLSI, Embedded Systems and Communications'),
(3, 'IT', 'Information Technology', 'Enterprise Software, Cloud Computing & Networking'),
(4, 'MECH', 'Mechanical Engineering', 'Thermodynamics, Robotics and Mechanical Design')
ON DUPLICATE KEY UPDATE `department_code`=`department_code`;

-- Users
-- Password hash for 'admin@123'
INSERT INTO `users` (`id`, `username`, `password`, `role`, `full_name`, `email`, `phone_number`, `enabled`) VALUES
(1, 'admin', 'u4hI3Jg1y87pL0+vH8hK4T8h56dFk1K5n8k8j7h6g5f=', 'ROLE_ADMIN', 'System Administrator', 'admin@sms.edu', '+1-800-555-0100', 1)
ON DUPLICATE KEY UPDATE `username`=`username`;

-- Admin profile
INSERT INTO `admin` (`id`, `name`, `email`, `phone`, `user_id`) VALUES
(1, 'System Administrator', 'admin@sms.edu', '+1-800-555-0100', 1)
ON DUPLICATE KEY UPDATE `email`=`email`;
