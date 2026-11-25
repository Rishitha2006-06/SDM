/**
 * Authentication and Login JavaScript
 */

$(document).ready(function() {
    // Check if user already logged in
    $.ajax({
        url: '/api/auth/current-user',
        method: 'GET',
        success: function(res) {
            if (res.success && res.userId) {
                // If on login page, redirect to respective dashboard
                if (window.location.pathname.endsWith('index.html') || window.location.pathname === '/' || window.location.pathname === '') {
                    SMS.redirectByRole(res.role);
                }
            }
        }
    });

    // Handle Quick Demo Account Chips
    $('.demo-account-chip').on('click', function() {
        const u = $(this).data('user');
        const p = $(this).data('pass');
        const r = $(this).data('role');

        $('#username').val(u);
        $('#password').val(p);
        if (r) $('#roleSelect').val(r);

        // Highlight inputs briefly
        $('#username, #password').addClass('border-primary');
        setTimeout(() => {
            $('#username, #password').removeClass('border-primary');
        }, 1000);
    });

    // Handle Login Form Submit
    $('#loginForm').on('submit', function(e) {
        e.preventDefault();

        const username = $('#username').val().trim();
        const password = $('#password').val();
        const role = $('#roleSelect').val();

        if (!username || !password) {
            SMS.showAlert('loginAlertContainer', 'Please enter both username and password.', 'danger');
            return;
        }

        const btn = $('#loginSubmitBtn');
        const originalText = btn.html();
        btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i> Signing In...');

        $.ajax({
            url: '/api/auth/login',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                username: username,
                password: password,
                role: role
            }),
            success: function(res) {
                if (res.success) {
                    SMS.showAlert('loginAlertContainer', 'Login successful! Redirecting...', 'success');
                    setTimeout(() => {
                        SMS.redirectByRole(res.role);
                    }, 800);
                } else {
                    btn.prop('disabled', false).html(originalText);
                    SMS.showAlert('loginAlertContainer', res.message || 'Invalid username or password', 'danger');
                }
            },
            error: function(xhr) {
                btn.prop('disabled', false).html(originalText);
                let msg = 'Authentication failed. Please check credentials.';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message;
                }
                SMS.showAlert('loginAlertContainer', msg, 'danger');
            }
        });
    });
});
