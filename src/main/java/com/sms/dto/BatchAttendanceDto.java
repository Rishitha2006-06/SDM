package com.sms.dto;

import com.sms.model.AttendanceStatus;
import java.time.LocalDate;
import java.util.List;

public class BatchAttendanceDto {
    private Long courseId;
    private LocalDate attendanceDate;
    private List<StudentAttendanceEntry> entries;

    public static class StudentAttendanceEntry {
        private Long studentId;
        private AttendanceStatus status;
        private String remarks;

        public StudentAttendanceEntry() {
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public AttendanceStatus getStatus() {
            return status;
        }

        public void setStatus(AttendanceStatus status) {
            this.status = status;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }

    public BatchAttendanceDto() {
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public List<StudentAttendanceEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<StudentAttendanceEntry> entries) {
        this.entries = entries;
    }
}
