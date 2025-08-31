<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Pahana Edu Bookstore</title>
    <link rel="stylesheet" href="assets/style.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
    
    
     :root {
    --primary-color: #D86C36;
    --primary-dark: #C4552C;
    --primary-darker: #A63F22;
    --accent-color: #f2a23f;
    --background-light: #fff;
}

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background:   --background-light;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
}

.login-container {
    background: white;
    border-radius: 20px;
    box-shadow: 0 20px 40px rgba(0,0,0,0.1);
    overflow: hidden;
    width: 100%;
    max-width: 900px;
    min-height: 500px;
    display: flex;
}

.login-left {
    flex: 1;
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
    padding: 60px 40px;
    color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
}

.login-left h1 {
    font-size: 3rem;
    margin-bottom: 20px;
    font-weight: 300;
}

.login-left p {
    font-size: 1.2rem;
    opacity: 0.9;
    line-height: 1.6;
}

.login-logo {
    width: 100px;
    height: 100px;
    margin-bottom: 30px;
    background: rgba(255,255,255,0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 3rem;
}

.login-right {
    flex: 1;
    padding: 60px 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.login-form h2 {
    color: #333;
    margin-bottom: 10px;
    font-size: 2rem;
    font-weight: 600;
}

.login-subtitle {
    color: #666;
    margin-bottom: 40px;
    font-size: 1rem;
}

.form-group {
    margin-bottom: 25px;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    color: #333;
    font-weight: 500;
}

.input-wrapper {
    position: relative;
}

.form-group input {
    width: 100%;
    padding: 15px 20px 15px 50px;
    border: 2px solid #e1e5e9;
    border-radius: 10px;
    font-size: 16px;
    transition: all 0.3s ease;
    background: var(--background-light);
}

.form-group input:focus {
    outline: none;
    border-color: var(--primary-color);
    background: white;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-icon {
    position: absolute;
    left: 18px;
    top: 50%;
    transform: translateY(-50%);
    color: #999;
    font-size: 16px;
}

.password-toggle {
    position: absolute;
    right: 18px;
    top: 50%;
    transform: translateY(-50%);
    color: #999;
    cursor: pointer;
    font-size: 16px;
}

.password-toggle:hover {
    color: var(--primary-color);
}

.form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
}

.remember-me {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #666;
    cursor: pointer;
}

.remember-me input[type="checkbox"] {
    width: auto;
    margin: 0;
}

.forgot-password {
    color: var(--primary-color);
    text-decoration: none;
    font-weight: 500;
    transition: color 0.3s ease;
}

.forgot-password:hover {
    color: var(--accent-color);
}

.login-btn {
    width: 100%;
    padding: 15px;
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.login-btn:active {
    transform: translateY(0);
}

.login-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none;
}

.alert {
    padding: 15px 20px;
    border-radius: 10px;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.alert-error {
    background: #fee;
    border: 1px solid #fcc;
    color: #c33;
}

.alert-success {
    background: #efe;
    border: 1px solid #cfc;
    color: #363;
}

.alert-info {
    background: #e8f4fd;
    border: 1px solid #b8e6ff;
    color: #2980b9;
}

.debug-info {
    background: #fff3cd;
    border: 1px solid #ffeaa7;
    color: #856404;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
    font-family: monospace;
    font-size: 12px;
}

@media (max-width: 768px) {
    .login-container {
        flex-direction: column;
        max-width: 100%;
        margin: 10px;
    }

    .login-left, .login-right {
        flex: none;
        padding: 40px 30px;
    }

    .login-left {
        min-height: 200px;
    }

    .login-left h1 {
        font-size: 2rem;
    }
    }


        
    </style>
</head>
<body>
    <div class="login-container">
        <!-- Left Panel -->
        <div class="login-left">
            <div class="login-logo">
                <i class="fas fa-graduation-cap"></i>
            </div>
            <h1>Pahana Edu</h1>
            <p>Bookstore Management System<br>Welcome back! Please sign in to continue.</p>
        </div>

        <!-- Right Panel - Login Form -->
        <div class="login-right">
            <div class="login-form">
                <h2>Sign In</h2>
                <p class="login-subtitle">Enter your credentials to access your account</p>

             

                <!-- Display Messages -->
                <c:if test="${not empty param.error}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>${param.error}</span>
                    </div>
                </c:if>

                <c:if test="${not empty error}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>${error}</span>
                    </div>
                </c:if>

                <c:if test="${not empty param.success}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        <span>${param.success}</span>
                    </div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        <span>${success}</span>
                    </div>
                </c:if>

                <c:if test="${not empty param.message}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i>
                        <span>${param.message}</span>
                    </div>
                </c:if>

                <!-- Account Lockout Warning -->
                <c:if test="${not empty lockoutTime}">
                    <div class="alert alert-error">
                        <i class="fas fa-lock"></i>
                        <span>Account locked. Try again in ${lockoutTime} minutes.</span>
                    </div>
                </c:if>

                <!-- Login Form - FIXED ACTION -->
                <form action="${pageContext.request.contextPath}/AuthServlet" method="post" id="loginForm">
                    <input type="hidden" name="action" value="login">

                    <div class="form-group">
                        <label for="username">Username</label>
                        <div class="input-wrapper">
                            <i class="fas fa-user input-icon"></i>
                            <input type="text" id="username" name="username" 
                                   placeholder="Enter your username" required
                                   value="${param.username != null ? param.username : (cookie.rememberedUser != null ? cookie.rememberedUser.value : '')}">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <div class="input-wrapper">
                            <i class="fas fa-lock input-icon"></i>
                            <input type="password" id="password" name="password" 
                                   placeholder="Enter your password" required>
                            <i class="fas fa-eye password-toggle" id="togglePassword"></i>
                        </div>
                    </div>

                    <div class="form-options">
                        <label class="remember-me">
                            <input type="checkbox" name="rememberMe" value="true"
                                   ${cookie.rememberedUser != null ? 'checked' : ''}>
                            <span>Remember me</span>
                        </label>
                        <a href="#" class="forgot-password" onclick="showForgotPassword()">
                            Forgot Password?
                        </a>
                    </div>

                    <button type="submit" class="login-btn" id="loginBtn">
                        <i class="fas fa-sign-in-alt"></i>
                        <span>Sign In</span>
                    </button>
                </form>

              

                <!-- Default Login Info -->
                <div style="background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 10px; padding: 20px; margin-top: 30px;">
                    <h4 style="color: #495057; margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
                        <i class="fas fa-info-circle"></i> Default Login Credentials
                    </h4>
                    
                    <div style="background: white; padding: 15px; border-radius: 8px; border-left: 4px solid #667eea; margin-bottom: 10px;">
                        <strong>Admin:</strong> 
                        <code style="background: #e9ecef; padding: 2px 6px; border-radius: 4px;">admin</code> / 
                        <code style="background: #e9ecef; padding: 2px 6px; border-radius: 4px;">admin123</code>
                        <div style="font-size: 0.9rem; color: #6c757d; margin-top: 5px;">Full system access</div>
                    </div>
                    
                    <small><i class="fas fa-shield-alt"></i> Change password on first login</small>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Enhanced debugging and form handling
        document.addEventListener('DOMContentLoaded', function() {
            console.log('Login page loaded');
            console.log('Context path:', '${pageContext.request.contextPath}');
            
            // Password toggle functionality
            const togglePassword = document.getElementById('togglePassword');
            const passwordInput = document.getElementById('password');

            if (togglePassword && passwordInput) {
                togglePassword.addEventListener('click', function() {
                    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                    passwordInput.setAttribute('type', type);
                    this.classList.toggle('fa-eye');
                    this.classList.toggle('fa-eye-slash');
                });
            }

            // Form validation and loading state
            const loginForm = document.getElementById('loginForm');
            const loginBtn = document.getElementById('loginBtn');

            if (loginForm && loginBtn) {
                loginForm.addEventListener('submit', function(e) {
                    console.log('Form submit triggered');
                    
                    const username = document.getElementById('username').value.trim();
                    const password = document.getElementById('password').value.trim();

                    console.log('Username:', username);
                    console.log('Password length:', password.length);

                    if (!username || !password) {
                        e.preventDefault();
                        alert('Please enter both username and password');
                        console.log('Form submission prevented - missing fields');
                        return false;
                    }

                    // Show loading state
                    loginBtn.disabled = true;
                    loginBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Signing In...';
                    
                    console.log('Form will be submitted to:', loginForm.action);
                    console.log('Form method:', loginForm.method);
                    
                    // Allow form to submit normally
                    return true;
                });
            } else {
                console.error('Login form or button not found!');
            }

            // Focus first input
            const usernameInput = document.getElementById('username');
            if (usernameInput) {
                usernameInput.focus();
            }
        });

        // Test function for debugging
        function testFormSubmission() {
            const form = document.getElementById('loginForm');
            console.log('=== FORM DEBUG INFO ===');
            console.log('Form element:', form);
            console.log('Form action:', form.action);
            console.log('Form method:', form.method);
            console.log('Context path: ${pageContext.request.contextPath}');
            
            // Get all form data
            const formData = new FormData(form);
            console.log('Form data:');
            for (let [key, value] of formData.entries()) {
                console.log(`${key}: ${value}`);
            }
            
            // Try manual submission
            if (confirm('Submit form manually for testing?')) {
                // Fill in test data
                document.getElementById('username').value = 'admin';
                document.getElementById('password').value = 'admin123';
                form.submit();
            }
        }

        // Global error handler
        window.addEventListener('error', function(e) {
            console.error('JavaScript error:', e.error);
            console.error('Error message:', e.message);
            console.error('Error location:', e.filename + ':' + e.lineno);
        });
    </script>
</body>
</html>