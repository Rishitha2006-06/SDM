/**
 * Student Dashboard & Academic Portal Scripts
 */

$(document).ready(function() {
    let studentId = null;

    // Verify session for ROLE_STUDENT
    SMS.initSession('ROLE_STUDENT', function(user) {
        studentId = user.entityId || 1;
        $('#stuHeaderRollDisplay').text('Roll No: ' + (user.rollOrEmployeeId || 'STU2024001'));

        loadStudentProfile();
        loadAttendanceData();
        loadMarksData();
        loadEnrolledCourses();
        loadReportCard();
    });

    // Sidebar navigation tabs
    $('.sidebar .nav-link[data-target]').on('click', function(e) {
        e.preventDefault();
        $('.sidebar .nav-link').removeClass('active');
        $(this).addClass('active');

        const target = $(this).data('target');
        $('.tab-pane-content').addClass('d-none');
        $(`#${target}Section`).removeClass('d-none');
    });

    // -------------------------------------------------------------
    // PROFILE MANAGEMENT
    // -------------------------------------------------------------
    function loadStudentProfile() {
        $.ajax({
            url: `/api/student/profile?studentId=${studentId}`,
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const s = res.data;
                    $('#profileNameDisplay').text(s.firstName + ' ' + s.lastName);
                    $('#profileRollDisplay').text(s.rollNumber);
                    $('#profileDeptDisplay').text(s.department ? s.department.departmentName : 'General');
                    $('#profileSemDisplay').text(s.currentSemester);

                    // Form inputs
                    $('#profRollNo').val(s.rollNumber);
                    $('#profEmail').val(s.email);
                    $('#profFirstName').val(s.firstName);
                    $('#profLastName').val(s.lastName);
                    $('#profDepartment').val(s.department ? s.department.departmentName : 'General');
                    $('#profSemester').val('Semester ' + s.currentSemester);
                    $('#profDob').val(s.dob || 'N/A');

                    // Editable fields
                    $('#profPhone').val(s.phone);
                    $('#profBloodGroup').val(s.bloodGroup);
                    $('#profGuardianName').val(s.guardianName);
                    $('#profGuardianPhone').val(s.guardianPhone);
                    $('#profAddress').val(s.address);
                }
            }
        });
    }

    $('#studentProfileForm').on('submit', function(e) {
        e.preventDefault();
        const data = {
            phone: $('#profPhone').val().trim(),
            bloodGroup: $('#profBloodGroup').val(),
            guardianName: $('#profGuardianName').val().trim(),
            guardianPhone: $('#profGuardianPhone').val().trim(),
            address: $('#profAddress').val().trim(),
            password: $('#profNewPassword').val()
        };

        $.ajax({
            url: `/api/student/profile?studentId=${studentId}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(res) {
                SMS.showAlert('studentNotificationContainer', 'Profile updated successfully!', 'success');
                $('#profNewPassword').val('');
                loadStudentProfile();
            },
            error: function(xhr) {
                SMS.showAlert('studentNotificationContainer', 'Failed to update profile', 'danger');
            }
        });
    });

    // -------------------------------------------------------------
    // ATTENDANCE TRACKER & LOW ATTENDANCE ALERT
    // -------------------------------------------------------------
    function loadAttendanceData() {
        // Overall Attendance
        $.ajax({
            url: `/api/student/attendance/overall?studentId=${studentId}`,
            method: 'GET',
            success: function(res) {
                if (res.success) {
                    const overall = res.data;
                    $('#statStuAttendance').text(overall + '%');

                    // Low-attendance alert check (<75%)
                    if (overall < 75.0) {
                        $('#lowAttendanceAlertContainer').removeClass('d-none');
                        $('#lowAttendanceAlertText').html(
                            `<strong>Critical Alert:</strong> Your aggregate attendance is <strong>${overall}%</strong> (below the institutional requirement of 75%). Low attendance may prevent you from appearing in semester final exams. Contact your department advisor immediately.`
                        );
                        $('#statStuAttendance').addClass('text-danger');
                    } else {
                        $('#lowAttendanceAlertContainer').addClass('d-none');
                        $('#statStuAttendance').removeClass('text-danger');
                    }
                }
            }
        });

        // Course-wise Attendance
        $.ajax({
            url: `/api/student/attendance?studentId=${studentId}`,
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const summaries = res.data;
                    const cardsGrid = $('#attendanceCardsGrid').empty();
                    const detailedTbody = $('#detailedAttendanceTbody').empty();

                    if (summaries.length === 0) {
                        cardsGrid.html('<div class="col-12 text-center py-3 text-muted">No attendance records found yet</div>');
                        detailedTbody.html('<tr><td colspan="8" class="text-center text-muted py-4">No records found</td></tr>');
                        return;
                    }

                    summaries.forEach(s => {
                        const isLow = s.percentage < 75.0;
                        const badgeColor = isLow ? 'bg-danger' : (s.percentage >= 85 ? 'bg-success' : 'bg-warning text-dark');
                        const progressColor = isLow ? 'bg-danger' : 'bg-primary';

                        // Dashboard Grid Card
                        cardsGrid.append(`
                            <div class="col-md-6">
                                <div class="p-3 border rounded-3 bg-white shadow-sm h-100">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <strong class="d-block text-dark">${s.courseName}</strong>
                                            <small class="text-muted">${s.courseCode}</small>
                                        </div>
                                        <span class="badge ${badgeColor} fs-6">${s.percentage}%</span>
                                    </div>
                                    <div class="progress mb-2" style="height: 10px;">
                                        <div class="progress-bar ${progressColor}" style="width: ${s.percentage}%"></div>
                                    </div>
                                    <div class="d-flex justify-content-between text-muted small">
                                        <span>Present: <strong>${s.presentCount}</strong> / Total: <strong>${s.totalClasses}</strong></span>
                                        <span>${isLow ? '<span class="text-danger fw-bold"><i class="fas fa-exclamation-triangle"></i> Shortage</span>' : '<span class="text-success"><i class="fas fa-check"></i> Eligible</span>'}</span>
                                    </div>
                                </div>
                            </div>
                        `);

                        // Detailed Table Row
                        detailedTbody.append(`
                            <tr>
                                <td class="fw-bold text-primary">${s.courseCode}</td>
                                <td class="fw-semibold">${s.courseName}</td>
                                <td>${s.totalClasses} Classes</td>
                                <td><span class="badge bg-success-subtle text-success border border-success">${s.presentCount}</span></td>
                                <td><span class="badge bg-warning-subtle text-warning border border-warning">${s.lateCount}</span></td>
                                <td><span class="badge bg-danger-subtle text-danger border border-danger">${s.absentCount}</span></td>
                                <td>
                                    <div class="d-flex align-items-center gap-2">
                                        <div class="progress flex-grow-1" style="height: 8px;">
                                            <div class="progress-bar ${progressColor}" style="width: ${s.percentage}%"></div>
                                        </div>
                                        <span class="fw-bold small">${s.percentage}%</span>
                                    </div>
                                </td>
                                <td>
                                    ${isLow
                                        ? '<span class="badge bg-danger"><i class="fas fa-times-circle me-1"></i> Debarment Risk</span>'
                                        : '<span class="badge bg-success"><i class="fas fa-check-circle me-1"></i> Eligible for Exam</span>'
                                    }
                                </td>
                            </tr>
                        `);
                    });
                }
            }
        });
    }

    // -------------------------------------------------------------
    // MARKS & SEMESTER RESULTS
    // -------------------------------------------------------------
    function loadMarksData() {
        $.ajax({
            url: `/api/student/marks?studentId=${studentId}`,
            method: 'GET',
            success: function(res) {
                const tbody = $('#marksTbody').empty();
                if (!res.success || !res.data || res.data.length === 0) {
                    tbody.html('<tr><td colspan="7" class="text-center text-muted py-4">No examination marks uploaded yet</td></tr>');
                    return;
                }

                res.data.forEach(m => {
                    const pct = Math.round((m.marksObtained / m.maxMarks) * 1000.0) / 10.0;
                    const gradeCls = (m.grade === 'A+' || m.grade === 'A') ? 'bg-success' : (m.grade === 'B' ? 'bg-primary' : (m.grade === 'F' ? 'bg-danger' : 'bg-warning text-dark'));

                    tbody.append(`
                        <tr>
                            <td class="fw-bold text-primary">${m.courseCode} - ${m.courseName}</td>
                            <td><span class="badge bg-light text-dark border">${formatExamType(m.examType)}</span></td>
                            <td class="fw-bold fs-6">${m.marksObtained}</td>
                            <td class="text-muted">${m.maxMarks}</td>
                            <td>${pct}%</td>
                            <td><span class="badge ${gradeCls} fs-6">${m.grade}</span></td>
                            <td class="text-muted small">${m.remarks || 'Evaluated'}</td>
                        </tr>
                    `);
                });
            }
        });
    }

    function formatExamType(type) {
        if (!type) return '';
        return type.replace(/_/g, ' ');
    }

    // -------------------------------------------------------------
    // ENROLLED COURSES
    // -------------------------------------------------------------
    function loadEnrolledCourses() {
        $.ajax({
            url: `/api/student/courses?studentId=${studentId}`,
            method: 'GET',
            success: function(res) {
                const tbody = $('#enrolledCoursesTbody').empty();
                if (!res.success || !res.data || res.data.length === 0) {
                    tbody.html('<tr><td colspan="6" class="text-center text-muted py-4">No courses enrolled</td></tr>');
                    return;
                }

                $('#statStuCoursesCount').text(res.data.length);

                res.data.forEach(c => {
                    const instructor = c.faculty
                        ? `<span class="badge bg-success-subtle text-success border border-success"><i class="fas fa-chalkboard-teacher me-1"></i> ${c.faculty.firstName} ${c.faculty.lastName}</span>`
                        : `<span class="badge bg-secondary">Unassigned</span>`;

                    tbody.append(`
                        <tr>
                            <td class="fw-bold text-info">${c.courseCode}</td>
                            <td class="fw-semibold">${c.courseName}</td>
                            <td>${c.credits} Credits</td>
                            <td>Semester ${c.semester}</td>
                            <td><span class="badge bg-light text-dark border">${c.department ? c.department.departmentCode : 'General'}</span></td>
                            <td>${instructor}</td>
                        </tr>
                    `);
                });
            }
        });
    }

    // -------------------------------------------------------------
    // OFFICIAL REPORT CARD & CGPA
    // -------------------------------------------------------------
    function loadReportCard() {
        $.ajax({
            url: `/api/student/report-card?studentId=${studentId}`,
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const rc = res.data;
                    $('#statStuCGPA').text(rc.cgpa.toFixed(2));
                    $('#statStuGrade').text(rc.overallGrade);

                    // Populate Report Card Modal
                    $('#rcStudentName').text(rc.studentName);
                    $('#rcRollNo').text(rc.rollNumber);
                    $('#rcDepartment').text(rc.departmentName);
                    $('#rcSemester').text(rc.semester);
                    $('#rcAcademicYear').text(rc.academicYear);
                    $('#rcOverallAtt').text(rc.overallAttendancePercentage + '%');
                    $('#rcTotalMarks').text(rc.totalMarksObtained + ' / ' + rc.totalMaxMarks);
                    $('#rcOverallPct').text(rc.percentage + '%');
                    $('#rcCGPA').text(rc.cgpa.toFixed(2) + ' / 10.0');

                    if (rc.lowAttendanceAlert) {
                        $('#rcAttendanceAlertNotice').removeClass('d-none');
                        $('#rcAttendanceAlertPct').text(rc.overallAttendancePercentage + '%');
                    } else {
                        $('#rcAttendanceAlertNotice').addClass('d-none');
                    }

                    const rcTbody = $('#rcCoursesTbody').empty();
                    if (rc.courses && rc.courses.length > 0) {
                        rc.courses.forEach(c => {
                            rcTbody.append(`
                                <tr>
                                    <td class="text-start"><strong>${c.courseCode}</strong>: ${c.courseName}</td>
                                    <td>${c.credits || 4}</td>
                                    <td>${c.mid1 != null ? c.mid1 : '-'}</td>
                                    <td>${c.mid2 != null ? c.mid2 : '-'}</td>
                                    <td>${c.assignment != null ? c.assignment : '-'}</td>
                                    <td>${c.finalExam != null ? c.finalExam : '-'}</td>
                                    <td class="fw-bold">${c.totalObtained} / ${c.maxMarks}</td>
                                    <td><span class="badge bg-dark">${c.grade}</span></td>
                                    <td><span class="badge ${c.attendancePercentage < 75 ? 'bg-danger' : 'bg-success'}">${c.attendancePercentage}%</span></td>
                                </tr>
                            `);
                        });
                    }
                }
            }
        });
    }

    // Open Report Card Modal
    $('#openReportCardModalBtn, #printReportCardBtn2').on('click', function() {
        loadReportCard();
        $('#reportCardModal').modal('show');
    });
});
