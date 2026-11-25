package com.sms.controller;

import com.sms.dto.ApiResponse;
import com.sms.dto.LoginRequest;
import com.sms.dto.LoginResponse;
import com.sms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse response = userService.authenticate(loginRequest);
        if (response.isSuccess()) {
            HttpSession session = request.getSession(true);
            session.setAttribute("USER_ID", response.getUserId());
            session.setAttribute("USERNAME", response.getUsername());
            session.setAttribute("ROLE", response.getRole());
            session.setAttribute("FULL_NAME", response.getFullName());
            session.setAttribute("ENTITY_ID", response.getEntityId());
            session.setAttribute("ROLL_OR_EMP_ID", response.getRollOrEmployeeId());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    @GetMapping("/current-user")
    public ResponseEntity<LoginResponse> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER_ID") == null) {
            return ResponseEntity.ok(new LoginResponse(false, "No active session"));
        }

        LoginResponse res = new LoginResponse(
                true,
                "Active session",
                (Long) session.getAttribute("USER_ID"),
                (String) session.getAttribute("USERNAME"),
                (String) session.getAttribute("FULL_NAME"),
                null,
                (com.sms.model.Role) session.getAttribute("ROLE"),
                (Long) session.getAttribute("ENTITY_ID"),
                (String) session.getAttribute("ROLL_OR_EMP_ID")
        );
        return ResponseEntity.ok(res);
    }
}
