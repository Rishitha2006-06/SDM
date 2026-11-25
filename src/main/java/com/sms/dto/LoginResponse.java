package com.sms.dto;

import com.sms.model.Role;

public class LoginResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private Long entityId; // studentId or facultyId or adminId
    private String rollOrEmployeeId;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, Long userId, String username,
                         String fullName, String email, Role role, Long entityId, String rollOrEmployeeId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.entityId = entityId;
        this.rollOrEmployeeId = rollOrEmployeeId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getRollOrEmployeeId() {
        return rollOrEmployeeId;
    }

    public void setRollOrEmployeeId(String rollOrEmployeeId) {
        this.rollOrEmployeeId = rollOrEmployeeId;
    }
}
