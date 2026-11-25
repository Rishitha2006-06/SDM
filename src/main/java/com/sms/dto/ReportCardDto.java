package com.sms.dto;

import java.util.List;

public class ReportCardDto {
    private String rollNumber;
    private String studentName;
    private String departmentName;
    private Integer semester;
    private String academicYear;
    private double overallAttendancePercentage;
    private boolean lowAttendanceAlert;
    private List<CourseMarksReport> courses;
    private double totalMarksObtained;
    private double totalMaxMarks;
    private double percentage;
    private String overallGrade;
    private double cgpa;

    public static class CourseMarksReport {
        private String courseCode;
        private String courseName;
        private Integer credits;
        private Double mid1;
        private Double mid2;
        private Double finalExam;
        private Double assignment;
        private Double totalObtained;
        private Double maxMarks;
        private String grade;
        private double attendancePercentage;

        public CourseMarksReport() {
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

        public Integer getCredits() {
            return credits;
        }

        public void setCredits(Integer credits) {
            this.credits = credits;
        }

        public Double getMid1() {
            return mid1;
        }

        public void setMid1(Double mid1) {
            this.mid1 = mid1;
        }

        public Double getMid2() {
            return mid2;
        }

        public void setMid2(Double mid2) {
            this.mid2 = mid2;
        }

        public Double getFinalExam() {
            return finalExam;
        }

        public void setFinalExam(Double finalExam) {
            this.finalExam = finalExam;
        }

        public Double getAssignment() {
            return assignment;
        }

        public void setAssignment(Double assignment) {
            this.assignment = assignment;
        }

        public Double getTotalObtained() {
            return totalObtained;
        }

        public void setTotalObtained(Double totalObtained) {
            this.totalObtained = totalObtained;
        }

        public Double getMaxMarks() {
            return maxMarks;
        }

        public void setMaxMarks(Double maxMarks) {
            this.maxMarks = maxMarks;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public double getAttendancePercentage() {
            return attendancePercentage;
        }

        public void setAttendancePercentage(double attendancePercentage) {
            this.attendancePercentage = attendancePercentage;
        }
    }

    public ReportCardDto() {
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public double getOverallAttendancePercentage() {
        return overallAttendancePercentage;
    }

    public void setOverallAttendancePercentage(double overallAttendancePercentage) {
        this.overallAttendancePercentage = overallAttendancePercentage;
    }

    public boolean isLowAttendanceAlert() {
        return lowAttendanceAlert;
    }

    public void setLowAttendanceAlert(boolean lowAttendanceAlert) {
        this.lowAttendanceAlert = lowAttendanceAlert;
    }

    public List<CourseMarksReport> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseMarksReport> courses) {
        this.courses = courses;
    }

    public double getTotalMarksObtained() {
        return totalMarksObtained;
    }

    public void setTotalMarksObtained(double totalMarksObtained) {
        this.totalMarksObtained = totalMarksObtained;
    }

    public double getTotalMaxMarks() {
        return totalMaxMarks;
    }

    public void setTotalMaxMarks(double totalMaxMarks) {
        this.totalMaxMarks = totalMaxMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getOverallGrade() {
        return overallGrade;
    }

    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }
}
