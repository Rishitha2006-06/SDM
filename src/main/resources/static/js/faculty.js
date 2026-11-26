/**
 * Faculty Dashboard Management Scripts
 */

$(document).ready(function() {
    let facultyCourses = [];

    // Verify session for ROLE_FACULTY
    SMS.initSession('ROLE_FACULTY', function(user) {
        // Set default date to today
        const today = new Date().toISOString().split('T')[0];
        $('#attDatePicker').val(today);

        loadFacultyCourses();
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

    // Load Assigned Courses
    function loadFacultyCourses() {
        $.ajax({
            url: '/api/faculty/courses',
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    facultyCourses = res.data;
                    const attSelect = $('#attCourseSelect').empty();
                    const marksSelect = $('#marksCourseSelect').empty();
                    const perfSelect = $('#perfCourseSelect').empty();

                    if (facultyCourses.length === 0) {
                        attSelect.append('<option value="">No courses assigned yet</option>');
                        marksSelect.append('<option value="">No courses assigned yet</option>');
                        perfSelect.append('<option value="">No courses assigned yet</option>');
                        return;
                    }

                    facultyCourses.forEach(c => {
                        const opt = `<option value="${c.id}">${c.courseCode} - ${c.courseName} (Sem ${c.semester})</option>`;
                        attSelect.append(opt);
                        marksSelect.append(opt);
                        perfSelect.append(opt);
                    });

                    // Trigger default roster load
                    loadPerformanceRoster();
                }
            }
        });
    }

    // -------------------------------------------------------------
    // SECTION 1: ATTENDANCE TRACKER
    // -------------------------------------------------------------
    $('#loadAttendanceRosterBtn').on('click', function() {
        const courseId = $('#attCourseSelect').val();
        const date = $('#attDatePicker').val();

        if (!courseId) {
            SMS.showAlert('facultyAlertContainer', 'Please select a course', 'warning');
            return;
        }

        const tbody = $('#attendanceRosterTbody').empty();
        tbody.html('<tr><td colspan="4" class="text-center text-muted py-3"><i class="fas fa-spinner fa-spin me-2"></i> Loading student roster...</td></tr>');

        // 1. Fetch enrolled students
        $.ajax({
            url: `/api/faculty/courses/${courseId}/students`,
            method: 'GET',
            success: function(stuRes) {
                if (!stuRes.success || !stuRes.data || stuRes.data.length === 0) {
                    tbody.html('<tr><td colspan="4" class="text-center text-muted py-4">No students currently enrolled in this course</td></tr>');
                    $('#saveAttendanceFooter').addClass('d-none');
                    return;
                }

                const students = stuRes.data;

                // 2. Fetch any previously saved attendance for this date
                $.ajax({
                    url: `/api/faculty/attendance/history?courseId=${courseId}&date=${date}`,
                    method: 'GET',
                    success: function(histRes) {
                        const historyMap = {};
                        if (histRes.success && histRes.data) {
                            histRes.data.forEach(h => {
                                historyMap[h.studentId] = h;
                            });
                        }

                        tbody.empty();
                        students.forEach(s => {
                            const prev = historyMap[s.id];
                            const currentStatus = prev ? prev.status : 'PRESENT';
                            const remarks = prev && prev.remarks ? prev.remarks : '';

                            const row = `
                                <tr data-student-id="${s.id}">
                                    <td class="fw-bold text-primary">${s.rollNumber}</td>
                                    <td class="fw-semibold">${s.firstName} ${s.lastName}</td>
                                    <td>
                                        <div class="btn-group" role="group">
                                            <input type="radio" class="btn-check" name="attStatus_${s.id}" id="present_${s.id}" value="PRESENT" ${currentStatus === 'PRESENT' ? 'checked' : ''}>
                                            <label class="btn btn-outline-success btn-sm" for="present_${s.id}"><i class="fas fa-check me-1"></i> Present</label>

                                            <input type="radio" class="btn-check" name="attStatus_${s.id}" id="late_${s.id}" value="LATE" ${currentStatus === 'LATE' ? 'checked' : ''}>
                                            <label class="btn btn-outline-warning btn-sm" for="late_${s.id}"><i class="fas fa-clock me-1"></i> Late</label>

                                            <input type="radio" class="btn-check" name="attStatus_${s.id}" id="absent_${s.id}" value="ABSENT" ${currentStatus === 'ABSENT' ? 'checked' : ''}>
                                            <label class="btn btn-outline-danger btn-sm" for="absent_${s.id}"><i class="fas fa-times me-1"></i> Absent</label>
                                        </div>
                                    </td>
                                    <td>
                                        <input type="text" class="form-control form-control-sm att-remarks" value="${remarks}" placeholder="e.g. Attentive, Quiz submitted">
                                    </td>
                                </tr>
                            `;
                            tbody.append(row);
                        });

                        $('#saveAttendanceFooter').removeClass('d-none');
                    }
                });
            }
        });
    });

    // Mark All Present Shortcut
    $('#markAllPresentBtn').on('click', function() {
        $('input[type="radio"][value="PRESENT"]').prop('checked', true);
    });

    // Submit Attendance Roster
    $('#submitAttendanceBtn').on('click', function() {
        const courseId = $('#attCourseSelect').val();
        const date = $('#attDatePicker').val();

        const entries = [];
        $('#attendanceRosterTbody tr[data-student-id]').each(function() {
            const studentId = $(this).data('student-id');
            const status = $(`input[name="attStatus_${studentId}"]:checked`).val() || 'PRESENT';
            const remarks = $(this).find('.att-remarks').val().trim();

            entries.push({
                studentId: studentId,
                status: status,
                remarks: remarks
            });
        });

        if (entries.length === 0) return;

        const btn = $(this);
        btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-1"></i> Saving...');

        $.ajax({
            url: '/api/faculty/attendance/batch',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                courseId: courseId,
                attendanceDate: date,
                entries: entries
            }),
            success: function(res) {
                btn.prop('disabled', false).html('<i class="fas fa-save me-1"></i> Save Classroom Attendance');
                SMS.showAlert('facultyAlertContainer', 'Classroom attendance successfully recorded and saved to MySQL!', 'success');
                loadPerformanceRoster();
            },
            error: function(xhr) {
                btn.prop('disabled', false).html('<i class="fas fa-save me-1"></i> Save Classroom Attendance');
                SMS.showAlert('facultyAlertContainer', 'Failed to save attendance records', 'danger');
            }
        });
    });

    // -------------------------------------------------------------
    // SECTION 2: ENTER EXAMINATION MARKS
    // -------------------------------------------------------------
    // Auto-update max marks on exam type change
    $('#marksExamTypeSelect').on('change', function() {
        const type = $(this).val();
        if (type === 'MID_TERM_1' || type === 'MID_TERM_2') {
            $('#marksMaxInput').val(30);
        } else if (type === 'ASSIGNMENT') {
            $('#marksMaxInput').val(20);
        } else if (type === 'SEMESTER_FINAL') {
            $('#marksMaxInput').val(100);
        } else if (type === 'LAB_INTERNAL') {
            $('#marksMaxInput').val(50);
        }
    });

    $('#loadMarksRosterBtn').on('click', function() {
        const courseId = $('#marksCourseSelect').val();
        const examType = $('#marksExamTypeSelect').val();
        const maxMarks = parseFloat($('#marksMaxInput').val()) || 100;

        if (!courseId) {
            SMS.showAlert('facultyAlertContainer', 'Please select a course', 'warning');
            return;
        }

        const tbody = $('#marksRosterTbody').empty();
        tbody.html('<tr><td colspan="6" class="text-center text-muted py-3"><i class="fas fa-spinner fa-spin me-2"></i> Loading student marks sheet...</td></tr>');

        // Fetch students
        $.ajax({
            url: `/api/faculty/courses/${courseId}/students`,
            method: 'GET',
            success: function(stuRes) {
                if (!stuRes.success || !stuRes.data || stuRes.data.length === 0) {
                    tbody.html('<tr><td colspan="6" class="text-center text-muted py-4">No enrolled students in this course</td></tr>');
                    return;
                }

                const students = stuRes.data;

                // Fetch existing marks for this exam type
                $.ajax({
                    url: `/api/faculty/marks?courseId=${courseId}&examType=${examType}`,
                    method: 'GET',
                    success: function(mRes) {
                        const marksMap = {};
                        if (mRes.success && mRes.data) {
                            mRes.data.forEach(m => {
                                marksMap[m.studentId] = m;
                            });
                        }

                        tbody.empty();
                        students.forEach(s => {
                            const prev = marksMap[s.id];
                            const obtained = prev ? prev.marksObtained : '';
                            const grade = prev ? prev.grade : '--';
                            const remarks = prev && prev.remarks ? prev.remarks : '';

                            const row = `
                                <tr data-student-id="${s.id}">
                                    <td class="fw-bold text-primary">${s.rollNumber}</td>
                                    <td class="fw-semibold">${s.firstName} ${s.lastName}</td>
                                    <td>
                                        <div class="input-group input-group-sm">
                                            <input type="number" step="0.5" min="0" max="${maxMarks}" class="form-control marks-obtained-input" value="${obtained}" placeholder="0 - ${maxMarks}">
                                            <span class="input-group-text">/ ${maxMarks}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge bg-primary fs-6 grade-badge">${grade}</span>
                                    </td>
                                    <td>
                                        <input type="text" class="form-control form-control-sm marks-remarks-input" value="${remarks}" placeholder="Performance notes...">
                                    </td>
                                    <td class="text-center">
                                        <button class="btn btn-outline-success btn-sm single-save-marks-btn" title="Save Marks">
                                            <i class="fas fa-check"></i> Save
                                        </button>
                                    </td>
                                </tr>
                            `;
                            tbody.append(row);
                        });
                    }
                });
            }
        });
    });

    // Dynamic Grade calculation on marks keystroke
    $(document).on('keyup change', '.marks-obtained-input', function() {
        const row = $(this).closest('tr');
        const obtained = parseFloat($(this).val());
        const maxMarks = parseFloat($('#marksMaxInput').val()) || 100;
        const badge = row.find('.grade-badge');

        if (isNaN(obtained) || obtained < 0) {
            badge.text('--').removeClass('bg-success bg-danger bg-warning').addClass('bg-primary');
            return;
        }

        const pct = (obtained / maxMarks) * 100;
        let grade = 'F';
        let cls = 'bg-danger';

        if (pct >= 90) { grade = 'A+'; cls = 'bg-success'; }
        else if (pct >= 80) { grade = 'A'; cls = 'bg-success'; }
        else if (pct >= 70) { grade = 'B'; cls = 'bg-primary'; }
        else if (pct >= 60) { grade = 'C'; cls = 'bg-info text-dark'; }
        else if (pct >= 50) { grade = 'D'; cls = 'bg-warning text-dark'; }
        else if (pct >= 40) { grade = 'E'; cls = 'bg-warning text-dark'; }

        badge.text(grade).attr('class', `badge fs-6 grade-badge ${cls}`);
    });

    // Save Single Student Marks
    $(document).on('click', '.single-save-marks-btn', function() {
        const row = $(this).closest('tr');
        const studentId = row.data('student-id');
        const courseId = $('#marksCourseSelect').val();
        const examType = $('#marksExamTypeSelect').val();
        const maxMarks = parseFloat($('#marksMaxInput').val()) || 100;
        const obtained = parseFloat(row.find('.marks-obtained-input').val());
        const remarks = row.find('.marks-remarks-input').val().trim();

        if (isNaN(obtained)) {
            alert('Please enter a valid marks value');
            return;
        }

        const btn = $(this);
        btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i>');

        $.ajax({
            url: '/api/faculty/marks',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                studentId: studentId,
                courseId: courseId,
                examType: examType,
                marksObtained: obtained,
                maxMarks: maxMarks,
                remarks: remarks
            }),
            success: function(res) {
                btn.prop('disabled', false).html('<i class="fas fa-check-double text-success"></i> Saved');
                setTimeout(() => {
                    btn.html('<i class="fas fa-check"></i> Save');
                }, 2000);
            },
            error: function(xhr) {
                btn.prop('disabled', false).html('<i class="fas fa-times text-danger"></i> Error');
                alert(xhr.responseJSON ? xhr.responseJSON.message : 'Error saving marks');
            }
        });
    });

    // -------------------------------------------------------------
    // SECTION 3: ASSIGNED STUDENTS & REPORTS
    // -------------------------------------------------------------
    $('#perfCourseSelect').on('change', function() {
        loadPerformanceRoster();
    });

    function loadPerformanceRoster() {
        const courseId = $('#perfCourseSelect').val() || (facultyCourses[0] ? facultyCourses[0].id : null);
        if (!courseId) return;

        const tbody = $('#performanceTbody').empty();
        tbody.html('<tr><td colspan="6" class="text-center text-muted py-3">Loading students...</td></tr>');

        $.ajax({
            url: `/api/faculty/courses/${courseId}/students`,
            method: 'GET',
            success: function(res) {
                if (!res.success || !res.data || res.data.length === 0) {
                    tbody.html('<tr><td colspan="6" class="text-center text-muted py-4">No students enrolled in this course</td></tr>');
                    return;
                }

                tbody.empty();
                res.data.forEach(s => {
                    // Check student attendance in this course
                    $.ajax({
                        url: `/api/student/attendance?studentId=${s.id}`,
                        method: 'GET',
                        success: function(attRes) {
                            let attSummary = null;
                            if (attRes.success && attRes.data) {
                                attSummary = attRes.data.find(item => item.courseId == courseId);
                            }

                            const pct = attSummary ? attSummary.percentage : 100;
                            const isLow = pct < 75.0;

                            const statusBadge = isLow
                                ? `<span class="badge bg-danger"><i class="fas fa-exclamation-triangle me-1"></i> Low Attendance (<75%)</span>`
                                : `<span class="badge bg-success"><i class="fas fa-check-circle me-1"></i> Good Attendance</span>`;

                            const attProgress = `
                                <div class="attendance-progress-container">
                                    <div class="progress attendance-progress-bar">
                                        <div class="progress-bar ${isLow ? 'bg-danger' : 'bg-success'}" style="width: ${pct}%"></div>
                                    </div>
                                    <span class="fw-bold small">${pct}%</span>
                                </div>
                            `;

                            tbody.append(`
                                <tr>
                                    <td class="fw-bold text-primary">${s.rollNumber}</td>
                                    <td class="fw-semibold">${s.firstName} ${s.lastName}</td>
                                    <td>${s.email}</td>
                                    <td style="width: 200px;">${attProgress}</td>
                                    <td>${statusBadge}</td>
                                    <td>
                                        <span class="text-muted small">${attSummary ? `${attSummary.presentCount} of ${attSummary.totalClasses} classes attended` : 'No classes recorded'}</span>
                                    </td>
                                </tr>
                            `);
                        }
                    });
                });
            }
        });
    }

    // -------------------------------------------------------------
    // REGISTER STUDENT MODAL (Faculty Portal)
    // -------------------------------------------------------------
    function loadDepartmentsForStudentReg() {
        $.ajax({
            url: '/api/faculty/departments',
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const sel = $('#facStuDeptSelect').empty();
                    sel.append('<option value="">Select Department</option>');
                    res.data.forEach(d => {
                        sel.append(`<option value="${d.id}">${d.departmentCode} - ${d.departmentName}</option>`);
                    });
                }
            }
        });
    }

    // Load departments when opening student modal
    $('#openRegisterStudentModalBtn, button[data-bs-target="#registerStudentModal"]').on('click', function() {
        loadDepartmentsForStudentReg();
    });
    loadDepartmentsForStudentReg();

    // Submit Student Registration Form
    $('#facultyRegisterStudentForm').on('submit', function(e) {
        e.preventDefault();

        const data = {
            rollNumber: $('#facStuRollNo').val().trim(),
            email: $('#facStuEmail').val().trim(),
            firstName: $('#facStuFirstName').val().trim(),
            lastName: $('#facStuLastName').val().trim(),
            departmentId: $('#facStuDeptSelect').val(),
            currentSemester: $('#facStuSemSelect').val(),
            phone: $('#facStuPhone').val().trim(),
            dob: $('#facStuDob').val() || null,
            gender: $('#facStuGender').val(),
            bloodGroup: $('#facStuBloodGroup').val(),
            guardianName: $('#facStuGuardianName').val().trim(),
            guardianPhone: $('#facStuGuardianPhone').val().trim(),
            address: $('#facStuAddress').val().trim(),
            password: $('#facStuPassword').val() || 'student@123'
        };

        const btn = $('#submitRegisterStudentBtn');
        const origText = btn.html();
        btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-1"></i> Registering...');

        $.ajax({
            url: '/api/faculty/students',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(res) {
                btn.prop('disabled', false).html(origText);
                $('#registerStudentModal').modal('hide');
                $('#facultyRegisterStudentForm')[0].reset();
                SMS.showAlert('facultyAlertContainer', `Student ${data.firstName} ${data.lastName} (${data.rollNumber}) registered successfully!`, 'success');
                loadPerformanceRoster();
            },
            error: function(xhr) {
                btn.prop('disabled', false).html(origText);
                const msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error registering student';
                SMS.showAlert('facultyAlertContainer', msg, 'danger');
            }
        });
    });
});
