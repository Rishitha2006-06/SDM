/**
 * Admin Dashboard Management Scripts
 */

$(document).ready(function() {
    // Verify session for ROLE_ADMIN
    SMS.initSession('ROLE_ADMIN', function(user) {
        loadDashboardStats();
        loadDepartments();
        loadFacultyList();
        loadStudentsList();
        loadCoursesList();
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
    // Dashboard Stats & Low Attendance Warning List
    // -------------------------------------------------------------
    function loadDashboardStats() {
        $.ajax({
            url: '/api/admin/stats',
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const d = res.data;
                    $('#statTotalStudents').text(d.totalStudents);
                    $('#statTotalFaculty').text(d.totalFaculty);
                    $('#statTotalCourses').text(d.totalCourses);
                    $('#statLowAttendance').text(d.lowAttendanceStudentsCount);
                    $('#statAvgAttendance').text(d.averageAttendancePercentage + '%');
                }
            }
        });

        // Load low attendance flagged students
        $.ajax({
            url: '/api/admin/students',
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const tbody = $('#lowAttendanceTbody');
                    tbody.empty();
                    let flaggedCount = 0;

                    res.data.forEach(s => {
                        // Check overall attendance for this student
                        $.ajax({
                            url: `/api/student/attendance/overall?studentId=${s.id}`,
                            method: 'GET',
                            success: function(attRes) {
                                if (attRes.success) {
                                    const pct = attRes.data;
                                    if (pct < 75.0) {
                                        flaggedCount++;
                                        const row = `
                                            <tr>
                                                <td class="fw-bold text-primary">${s.rollNumber}</td>
                                                <td>${s.firstName} ${s.lastName}</td>
                                                <td><span class="badge bg-light text-dark border">${s.department ? s.department.departmentCode : 'N/A'}</span></td>
                                                <td>Sem ${s.currentSemester}</td>
                                                <td><span class="badge bg-danger fs-6">${pct}%</span></td>
                                                <td><span class="text-danger fw-semibold"><i class="fas fa-exclamation-circle me-1"></i> Critical Shortage</span></td>
                                                <td>
                                                    <a href="tel:${s.guardianPhone || s.phone}" class="btn btn-outline-danger btn-sm" title="Call Guardian">
                                                        <i class="fas fa-phone-alt me-1"></i> ${s.guardianPhone || s.phone || 'No phone'}
                                                    </a>
                                                </td>
                                            </tr>
                                        `;
                                        tbody.append(row);
                                    }
                                }
                            }
                        });
                    });

                    setTimeout(() => {
                        if (tbody.children().length === 0) {
                            tbody.html('<tr><td colspan="7" class="text-center text-success py-3"><i class="fas fa-check-circle me-1"></i> All students currently maintain satisfactory attendance (&ge; 75%).</td></tr>');
                        }
                    }, 1200);
                }
            }
        });
    }

    // -------------------------------------------------------------
    // Departments Management
    // -------------------------------------------------------------
    function loadDepartments() {
        $.ajax({
            url: '/api/admin/departments',
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const list = res.data;
                    const tbody = $('#departmentsTbody').empty();
                    const stuDept = $('#stuDeptSelect, #studentDeptFilter').empty();
                    const facDept = $('#facDeptSelect').empty();
                    const crsDept = $('#courseDeptSelect').empty();

                    stuDept.append('<option value="">All Departments</option>');
                    facDept.append('<option value="">Select Department</option>');
                    crsDept.append('<option value="">Select Department</option>');

                    if (list.length === 0) {
                        tbody.html('<tr><td colspan="4" class="text-center text-muted py-3">No departments found</td></tr>');
                        return;
                    }

                    list.forEach(d => {
                        tbody.append(`
                            <tr>
                                <td class="fw-bold text-primary">${d.departmentCode}</td>
                                <td class="fw-semibold">${d.departmentName}</td>
                                <td class="text-muted small">${d.description || 'N/A'}</td>
                                <td class="text-center">
                                    <button class="btn btn-outline-danger btn-sm delete-dept-btn" data-id="${d.id}">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        `);

                        stuDept.append(`<option value="${d.id}">${d.departmentCode} - ${d.departmentName}</option>`);
                        facDept.append(`<option value="${d.id}">${d.departmentCode} - ${d.departmentName}</option>`);
                        crsDept.append(`<option value="${d.id}">${d.departmentCode} - ${d.departmentName}</option>`);
                    });
                }
            }
        });
    }

    $('#departmentForm').on('submit', function(e) {
        e.preventDefault();
        const data = {
            departmentCode: $('#deptCode').val().trim().toUpperCase(),
            departmentName: $('#deptName').val().trim(),
            description: $('#deptDesc').val().trim()
        };

        $.ajax({
            url: '/api/admin/departments',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(res) {
                $('#departmentModal').modal('hide');
                $('#departmentForm')[0].reset();
                SMS.showAlert('adminAlertContainer', 'Department created successfully', 'success');
                loadDepartments();
                loadDashboardStats();
            },
            error: function(xhr) {
                SMS.showAlert('adminAlertContainer', xhr.responseJSON ? xhr.responseJSON.message : 'Error adding department', 'danger');
            }
        });
    });

    $(document).on('click', '.delete-dept-btn', function() {
        const id = $(this).data('id');
        if (!confirm('Are you sure you want to delete this department?')) return;
        $.ajax({
            url: `/api/admin/departments/${id}`,
            method: 'DELETE',
            success: function() {
                SMS.showAlert('adminAlertContainer', 'Department deleted', 'success');
                loadDepartments();
                loadDashboardStats();
            }
        });
    });

    // -------------------------------------------------------------
    // Students Management
    // -------------------------------------------------------------
    function loadStudentsList() {
        const search = $('#studentSearchInput').val().trim();
        const deptId = $('#studentDeptFilter').val();
        const sem = $('#studentSemFilter').val();

        let url = `/api/admin/students/filter?search=${encodeURIComponent(search)}`;
        if (deptId) url += `&departmentId=${deptId}`;
        if (sem) url += `&semester=${sem}`;

        $.ajax({
            url: url,
            method: 'GET',
            success: function(res) {
                const tbody = $('#studentsTbody').empty();
                if (!res.success || !res.data || res.data.length === 0) {
                    tbody.html('<tr><td colspan="8" class="text-center text-muted py-4">No matching students found</td></tr>');
                    return;
                }

                res.data.forEach(s => {
                    tbody.append(`
                        <tr>
                            <td class="fw-bold text-primary">${s.rollNumber}</td>
                            <td class="fw-semibold">${s.firstName} ${s.lastName}</td>
                            <td><span class="badge bg-light text-dark border">${s.department ? s.department.departmentCode : 'General'}</span></td>
                            <td>Semester ${s.currentSemester}</td>
                            <td>${s.email}</td>
                            <td>${s.phone || 'N/A'}</td>
                            <td>${s.guardianName || 'N/A'} <br><small class="text-muted">${s.guardianPhone || ''}</small></td>
                            <td class="text-center">
                                <button class="btn btn-outline-primary btn-sm edit-student-btn me-1" data-id="${s.id}" title="Edit Student">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-outline-danger btn-sm delete-student-btn" data-id="${s.id}" data-name="${s.firstName} ${s.lastName}" title="Delete Student">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                    `);
                });
            }
        });
    }

    // Filter triggers
    $('#studentSearchInput').on('keyup', function() { loadStudentsList(); });
    $('#studentDeptFilter, #studentSemFilter').on('change', function() { loadStudentsList(); });
    $('#resetStudentFilterBtn').on('click', function() {
        $('#studentSearchInput').val('');
        $('#studentDeptFilter').val('');
        $('#studentSemFilter').val('');
        loadStudentsList();
    });

    // Reset student modal for creation
    $('#openAddStudentModalBtn').on('click', function() {
        $('#studentForm')[0].reset();
        $('#studentEditId').val('');
        $('#studentModalTitle').html('<i class="fas fa-user-plus me-2"></i> Register New Student');
        $('#stuPasswordContainer').show();
        $('#stuRollNo').prop('readonly', false);
    });

    // Edit Student click
    $(document).on('click', '.edit-student-btn', function() {
        const id = $(this).data('id');
        $.ajax({
            url: `/api/admin/students/${id}`,
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const s = res.data;
                    $('#studentEditId').val(s.id);
                    $('#stuRollNo').val(s.rollNumber).prop('readonly', true);
                    $('#stuEmail').val(s.email);
                    $('#stuFirstName').val(s.firstName);
                    $('#stuLastName').val(s.lastName);
                    $('#stuDeptSelect').val(s.department ? s.department.id : '');
                    $('#stuSemSelect').val(s.currentSemester);
                    $('#stuPhone').val(s.phone);
                    $('#stuDob').val(s.dob);
                    $('#stuGender').val(s.gender);
                    $('#stuBloodGroup').val(s.bloodGroup);
                    $('#stuGuardianName').val(s.guardianName);
                    $('#stuGuardianPhone').val(s.guardianPhone);
                    $('#stuAddress').val(s.address);
                    $('#studentModalTitle').html('<i class="fas fa-user-edit me-2"></i> Edit Student Record');
                    $('#stuPasswordContainer').hide();
                    $('#studentModal').modal('show');
                }
            }
        });
    });

    // Save Student (Create or Update)
    $('#studentForm').on('submit', function(e) {
        e.preventDefault();
        const editId = $('#studentEditId').val();

        const data = {
            rollNumber: $('#stuRollNo').val().trim(),
            email: $('#stuEmail').val().trim(),
            firstName: $('#stuFirstName').val().trim(),
            lastName: $('#stuLastName').val().trim(),
            departmentId: $('#stuDeptSelect').val(),
            currentSemester: $('#stuSemSelect').val(),
            phone: $('#stuPhone').val().trim(),
            dob: $('#stuDob').val() || null,
            gender: $('#stuGender').val(),
            bloodGroup: $('#stuBloodGroup').val(),
            guardianName: $('#stuGuardianName').val().trim(),
            guardianPhone: $('#stuGuardianPhone').val().trim(),
            address: $('#stuAddress').val().trim(),
            password: $('#stuPassword').val()
        };

        const method = editId ? 'PUT' : 'POST';
        const url = editId ? `/api/admin/students/${editId}` : '/api/admin/students';

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(res) {
                $('#studentModal').modal('hide');
                $('#studentForm')[0].reset();
                SMS.showAlert('adminAlertContainer', res.message || 'Student saved successfully', 'success');
                loadStudentsList();
                loadDashboardStats();
            },
            error: function(xhr) {
                SMS.showAlert('adminAlertContainer', xhr.responseJSON ? xhr.responseJSON.message : 'Error saving student', 'danger');
            }
        });
    });

    // Delete Student
    $(document).on('click', '.delete-student-btn', function() {
        const id = $(this).data('id');
        const name = $(this).data('name');
        if (!confirm(`Are you sure you want to delete student "${name}"? All associated attendance and marks will also be deleted.`)) return;

        $.ajax({
            url: `/api/admin/students/${id}`,
            method: 'DELETE',
            success: function() {
                SMS.showAlert('adminAlertContainer', `Student ${name} deleted successfully`, 'success');
                loadStudentsList();
                loadDashboardStats();
            },
            error: function(xhr) {
                SMS.showAlert('adminAlertContainer', 'Error deleting student', 'danger');
            }
        });
    });

    // -------------------------------------------------------------
    // Faculty Management
    // -------------------------------------------------------------
    function loadFacultyList() {
        $.ajax({
            url: '/api/admin/faculty',
            method: 'GET',
            success: function(res) {
                const tbody = $('#facultyTbody').empty();
                const crsFac = $('#courseFacultySelect').empty();
                crsFac.append('<option value="">-- No Faculty Assigned --</option>');

                if (!res.success || !res.data || res.data.length === 0) {
                    tbody.html('<tr><td colspan="8" class="text-center text-muted py-4">No faculty members found</td></tr>');
                    return;
                }

                res.data.forEach(f => {
                    tbody.append(`
                        <tr>
                            <td class="fw-bold text-success">${f.employeeId}</td>
                            <td class="fw-semibold">${f.firstName} ${f.lastName}</td>
                            <td><span class="badge bg-light text-dark border">${f.department ? f.department.departmentCode : 'General'}</span></td>
                            <td>${f.designation || 'Faculty'}</td>
                            <td>${f.qualification || 'N/A'}</td>
                            <td>${f.email}</td>
                            <td>${f.phone || 'N/A'}</td>
                            <td class="text-center">
                                <button class="btn btn-outline-success btn-sm edit-faculty-btn me-1" data-id="${f.id}" title="Edit Faculty">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-outline-danger btn-sm delete-faculty-btn" data-id="${f.id}" data-name="${f.firstName} ${f.lastName}" title="Delete Faculty">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                    `);

                    crsFac.append(`<option value="${f.id}">${f.firstName} ${f.lastName} (${f.employeeId})</option>`);
                });
            }
        });
    }

    $('#openAddFacultyModalBtn').on('click', function() {
        $('#facultyForm')[0].reset();
        $('#facultyEditId').val('');
        $('#facultyModalTitle').html('<i class="fas fa-user-plus me-2"></i> Add Faculty Member');
        $('#facPasswordContainer').show();
        $('#facEmpId').prop('readonly', false);
    });

    $(document).on('click', '.edit-faculty-btn', function() {
        const id = $(this).data('id');
        $.ajax({
            url: `/api/admin/faculty/${id}`,
            method: 'GET',
            success: function(res) {
                if (res.success && res.data) {
                    const f = res.data;
                    $('#facultyEditId').val(f.id);
                    $('#facEmpId').val(f.employeeId).prop('readonly', true);
                    $('#facEmail').val(f.email);
                    $('#facFirstName').val(f.firstName);
                    $('#facLastName').val(f.lastName);
                    $('#facDeptSelect').val(f.department ? f.department.id : '');
                    $('#facDesignation').val(f.designation);
                    $('#facQualification').val(f.qualification);
                    $('#facPhone').val(f.phone);
                    $('#facJoiningDate').val(f.joiningDate);
                    $('#facultyModalTitle').html('<i class="fas fa-user-edit me-2"></i> Edit Faculty Details');
                    $('#facPasswordContainer').hide();
                    $('#facultyModal').modal('show');
                }
            }
        });
    });

    $('#facultyForm').on('submit', function(e) {
        e.preventDefault();
        const editId = $('#facultyEditId').val();

        const data = {
            employeeId: $('#facEmpId').val().trim(),
            email: $('#facEmail').val().trim(),
            firstName: $('#facFirstName').val().trim(),
            lastName: $('#facLastName').val().trim(),
            departmentId: $('#facDeptSelect').val(),
            designation: $('#facDesignation').val().trim(),
            qualification: $('#facQualification').val().trim(),
            phone: $('#facPhone').val().trim(),
            joiningDate: $('#facJoiningDate').val() || null,
            password: $('#facPassword').val()
        };

        const method = editId ? 'PUT' : 'POST';
        const url = editId ? `/api/admin/faculty/${editId}` : '/api/admin/faculty';

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(res) {
                $('#facultyModal').modal('hide');
                $('#facultyForm')[0].reset();
                SMS.showAlert('adminAlertContainer', res.message || 'Faculty member saved successfully', 'success');
                loadFacultyList();
                loadDashboardStats();
            },
            error: function(xhr) {
                SMS.showAlert('adminAlertContainer', xhr.responseJSON ? xhr.responseJSON.message : 'Error saving faculty', 'danger');
            }
        });
    });

    $(document).on('click', '.delete-faculty-btn', function() {
        const id = $(this).data('id');
        const name = $(this).data('name');
        if (!confirm(`Are you sure you want to remove faculty member "${name}"?`)) return;

        $.ajax({
            url: `/api/admin/faculty/${id}`,
            method: 'DELETE',
            success: function() {
                SMS.showAlert('adminAlertContainer', `Faculty member ${name} deleted successfully`, 'success');
                loadFacultyList();
                loadDashboardStats();
            }
        });
    });

    // -------------------------------------------------------------
    // Courses & Assigned Teachers Management
    // -------------------------------------------------------------
    function loadCoursesList() {
        $.ajax({
            url: '/api/admin/courses',
            method: 'GET',
            success: function(res) {
                const tbody = $('#coursesTbody').empty();
                if (!res.success || !res.data || res.data.length === 0) {
                    tbody.html('<tr><td colspan="7" class="text-center text-muted py-4">No courses registered yet</td></tr>');
                    return;
                }

                res.data.forEach(c => {
                    const facultyDisplay = c.faculty
                        ? `<span class="badge bg-success-subtle text-success border border-success"><i class="fas fa-chalkboard-teacher me-1"></i> ${c.faculty.firstName} ${c.faculty.lastName}</span>`
                        : `<span class="badge bg-secondary-subtle text-secondary border">Unassigned</span>`;

                    tbody.append(`
                        <tr>
                            <td class="fw-bold text-info">${c.courseCode}</td>
                            <td class="fw-semibold">${c.courseName}</td>
                            <td>${c.credits} Credits</td>
                            <td>Semester ${c.semester}</td>
                            <td><span class="badge bg-light text-dark border">${c.department ? c.department.departmentCode : 'N/A'}</span></td>
                            <td>${facultyDisplay}</td>
                            <td class="text-center">
                                <button class="btn btn-outline-info btn-sm edit-course-btn me-1" data-id="${c.id}" title="Edit Course">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-outline-danger btn-sm delete-course-btn" data-id="${c.id}" data-name="${c.courseName}" title="Delete Course">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                    `);
                });
            }
        });
    }

    $('#openAddCourseModalBtn').on('click', function() {
        $('#courseForm')[0].reset();
        $('#courseEditId').val('');
        $('#courseModalTitle').html('<i class="fas fa-book me-2"></i> Add New Course');
        $('#courseCode').prop('readonly', false);
    });

    $(document).on('click', '.edit-course-btn', function() {
        const id = $(this).data('id');
        $.ajax({
            url: `/api/admin/courses`,
            method: 'GET',
            success: function(res) {
                const c = res.data.find(item => item.id == id);
                if (c) {
                    $('#courseEditId').val(c.id);
                    $('#courseCode').val(c.courseCode).prop('readonly', true);
                    $('#courseName').val(c.courseName);
                    $('#courseCredits').val(c.credits);
                    $('#courseSemester').val(c.semester);
                    $('#courseDeptSelect').val(c.department ? c.department.id : '');
                    $('#courseFacultySelect').val(c.faculty ? c.faculty.id : '');
                    $('#courseModalTitle').html('<i class="fas fa-edit me-2"></i> Edit Course & Teacher Assignment');
                    $('#courseModal').modal('show');
                }
            }
        });
    });

    $('#courseForm').on('submit', function(e) {
        e.preventDefault();
        const editId = $('#courseEditId').val();

        const data = {
            courseCode: $('#courseCode').val().trim(),
            courseName: $('#courseName').val().trim(),
            credits: parseInt($('#courseCredits').val()),
            semester: parseInt($('#courseSemester').val()),
            departmentId: $('#courseDeptSelect').val(),
            facultyId: $('#courseFacultySelect').val() || null
        };

        const method = editId ? 'PUT' : 'POST';
        const url = editId ? `/api/admin/courses/${editId}` : '/api/admin/courses';

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(res) {
                $('#courseModal').modal('hide');
                $('#courseForm')[0].reset();
                SMS.showAlert('adminAlertContainer', res.message || 'Course saved successfully', 'success');
                loadCoursesList();
                loadDashboardStats();
            },
            error: function(xhr) {
                SMS.showAlert('adminAlertContainer', xhr.responseJSON ? xhr.responseJSON.message : 'Error saving course', 'danger');
            }
        });
    });

    $(document).on('click', '.delete-course-btn', function() {
        const id = $(this).data('id');
        const name = $(this).data('name');
        if (!confirm(`Are you sure you want to delete course "${name}"?`)) return;

        $.ajax({
            url: `/api/admin/courses/${id}`,
            method: 'DELETE',
            success: function() {
                SMS.showAlert('adminAlertContainer', `Course ${name} deleted successfully`, 'success');
                loadCoursesList();
                loadDashboardStats();
            }
        });
    });
});
