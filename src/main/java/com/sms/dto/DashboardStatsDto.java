package com.sms.dto;

public class DashboardStatsDto {
    private long totalStudents;
    private long totalFaculty;
    private long totalCourses;
    private long totalDepartments;
    private long lowAttendanceStudentsCount;
    private double averageAttendancePercentage;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalStudents, long totalFaculty, long totalCourses,
                             long totalDepartments, long lowAttendanceStudentsCount,
                             double averageAttendancePercentage) {
        this.totalStudents = totalStudents;
        this.totalFaculty = totalFaculty;
        this.totalCourses = totalCourses;
        this.totalDepartments = totalDepartments;
        this.lowAttendanceStudentsCount = lowAttendanceStudentsCount;
        this.averageAttendancePercentage = Math.round(averageAttendancePercentage * 10.0) / 10.0;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalFaculty() {
        return totalFaculty;
    }

    public void setTotalFaculty(long totalFaculty) {
        this.totalFaculty = totalFaculty;
    }

    public long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(long totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public long getLowAttendanceStudentsCount() {
        return lowAttendanceStudentsCount;
    }

    public void setLowAttendanceStudentsCount(long lowAttendanceStudentsCount) {
        this.lowAttendanceStudentsCount = lowAttendanceStudentsCount;
    }

    public double getAverageAttendancePercentage() {
        return averageAttendancePercentage;
    }

    public void setAverageAttendancePercentage(double averageAttendancePercentage) {
        this.averageAttendancePercentage = averageAttendancePercentage;
    }
}
