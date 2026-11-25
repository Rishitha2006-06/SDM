package com.sms.dto;

public class AttendanceSummaryDto {
    private Long courseId;
    private String courseCode;
    private String courseName;
    private long totalClasses;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private double percentage;
    private boolean lowAttendance; // < 75%
    private String statusBadgeClass; // success, warning, danger

    public AttendanceSummaryDto() {
    }

    public AttendanceSummaryDto(Long courseId, String courseCode, String courseName,
                                long totalClasses, long presentCount, long absentCount,
                                long lateCount, double percentage) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.totalClasses = totalClasses;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.lateCount = lateCount;
        this.percentage = Math.round(percentage * 100.0) / 100.0;
        this.lowAttendance = this.percentage < 75.0 && totalClasses > 0;
        if (this.percentage >= 75.0) {
            this.statusBadgeClass = "bg-success";
        } else if (this.percentage >= 65.0) {
            this.statusBadgeClass = "bg-warning text-dark";
        } else {
            this.statusBadgeClass = "bg-danger";
        }
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public long getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(long presentCount) {
        this.presentCount = presentCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(long absentCount) {
        this.absentCount = absentCount;
    }

    public long getLateCount() {
        return lateCount;
    }

    public void setLateCount(long lateCount) {
        this.lateCount = lateCount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public boolean isLowAttendance() {
        return lowAttendance;
    }

    public void setLowAttendance(boolean lowAttendance) {
        this.lowAttendance = lowAttendance;
    }

    public String getStatusBadgeClass() {
        return statusBadgeClass;
    }

    public void setStatusBadgeClass(String statusBadgeClass) {
        this.statusBadgeClass = statusBadgeClass;
    }
}
