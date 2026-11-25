package com.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementSystemApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("  Student Management System (SMS) Backend Started Successfully!   ");
        System.out.println("  Access the application at: http://localhost:8080               ");
        System.out.println("==================================================================");
    }
}
