/**
 * Common JavaScript Utilities for Student Management System
 */

const SMS = {
    // Current user session object
    currentUser: null,

    // Initialize session and verify access
    initSession: function(allowedRole, callback) {
        $.ajax({
            url: '/api/auth/current-user',
            method: 'GET',
            success: function(res) {
                if (!res.success || !res.userId) {
                    window.location.href = '/index.html';
                    return;
                }
                SMS.currentUser = res;
                if (allowedRole && res.role !== allowedRole) {
                    alert('Unauthorized access. Redirecting...');
                    SMS.redirectByRole(res.role);
                    return;
                }
                // Update UI username / display
                $('#userFullNameDisplay').text(res.fullName || res.username);
                $('#userRoleBadge').text(res.role ? res.role.replace('ROLE_', '') : '');
                if (callback) callback(res);
            },
            error: function() {
                window.location.href = '/index.html';
            }
        });
    },

    // Redirect to respective dashboard
    redirectByRole: function(role) {
        if (role === 'ROLE_ADMIN') {
            window.location.href = '/admin-dashboard.html';
        } else if (role === 'ROLE_FACULTY') {
            window.location.href = '/faculty-dashboard.html';
        } else if (role === 'ROLE_STUDENT') {
            window.location.href = '/student-dashboard.html';
        } else {
            window.location.href = '/index.html';
        }
    },

    // Handle logout
    logout: function() {
        if (!confirm('Are you sure you want to log out?')) return;
        $.ajax({
            url: '/api/auth/logout',
            method: 'POST',
            success: function() {
                SMS.currentUser = null;
                window.location.href = '/index.html';
            },
            error: function() {
                window.location.href = '/index.html';
            }
        });
    },

    // Show toast or alert notification
    showAlert: function(containerId, message, type = 'success') {
        const icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-triangle';
        const html = `
            <div class="alert alert-${type} alert-dismissible fade show d-flex align-items-center" role="alert">
                <i class="fas ${icon} me-2"></i>
                <div>${message}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        $(`#${containerId}`).html(html);
        setTimeout(() => {
            $(`#${containerId} .alert`).fadeOut(500, function() { $(this).remove(); });
        }, 5000);
    },

    // Format percentage with badge class
    getAttendanceBadge: function(percentage) {
        if (percentage >= 75) {
            return `<span class="badge bg-success badge-pill-custom">${percentage}% (Eligible)</span>`;
        } else if (percentage >= 65) {
            return `<span class="badge bg-warning text-dark badge-pill-custom">${percentage}% (Warning)</span>`;
        } else {
            return `<span class="badge bg-danger badge-pill-custom">${percentage}% (Low)</span>`;
        }
    },

    // Format date string
    formatDate: function(dateStr) {
        if (!dateStr) return 'N/A';
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    }
};

$(document).ready(function() {
    $('#logoutBtn, .logout-action').on('click', function(e) {
        e.preventDefault();
        SMS.logout();
    });
});
