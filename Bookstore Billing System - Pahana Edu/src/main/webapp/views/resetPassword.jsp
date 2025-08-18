<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>First Time Setup - Pahana Edu</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .setup-container {
            max-width: 600px;
            margin: 0 auto;
            padding: 2rem;
        }
        
        .setup-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        
        .brand-logo {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #ffc107, #fd7e14);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 1rem;
            color: white;
            font-size: 2rem;
        }
        
        .form-floating {
            margin-bottom: 1rem;
        }
        
        .form-control {
            border: 2px solid #e1e5e9;
            border-radius: 12px;
            padding: 12px 16px;
            transition: all 0.3s ease;
        }
        
        .form-control:focus {
            border-color: #ffc107;
            box-shadow: 0 0 0 0.2rem rgba(255, 193, 7, 0.25);
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #ffc107, #fd7e14);
            border: none;
            border-radius: 12px;
            padding: 12px;
            font-weight: 600;
            transition: all 0.3s ease;
            color: #212529;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(255, 193, 7, 0.3);
            color: #212529;
        }
        
        .alert {
            border-radius: 12px;
            border: none;
        }
        
        .welcome-message {
            background: linear-gradient(135deg, #e3f2fd, #f3e5f5);
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            border: none;
        }
        
        .step-indicator {
            display: flex;
            justify-content: center;
            margin-bottom: 2rem;
        }
        
        .step {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: linear-gradient(135deg, #ffc107, #fd7e14);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            margin: 0 10px;
        }
        
        .password-requirements {
            font-size: 0.875rem;
        }
        
        .password-requirements .requirement {
            padding: 2px 0;
        }
        
        .password-requirements .valid {
            color: #28a745;
        }
        
        .password-requirements .invalid {
            color: #dc3545;
        }
    </style>
</head>
<body>
    <%
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("views/login.jsp");
            return;
        }
    %>
    
    <div class="container-fluid d-flex align-items-center justify-content-center min-vh-100">
        <div class="setup-container">
            <div class="card setup-card border-0">
                <div class="card-body p-4">
                    <!-- Brand -->
                    <div class="text-center mb-4">
                        <div class="brand-logo">
                            <i class="fas fa-cog"></i>
                        </div>
                        <h3 class="fw-bold text-dark mb-1">Welcome to Pahana Edu!</h3>
                        <p class="text-muted">Let's set up your account</p>
                    </div>

                    <!-- Step Indicator -->
                    <div class="step-indicator">
                        <div class="step">1</div>
                    </div>

                    <!-- Welcome Message -->
                    <div class="welcome-message">
                        <div class="d-flex align-items-center">
                            <i class="fas fa-hand-wave text-warning me-3" style="font-size: 2rem;"></i>
                            <div>
                                <h5 class="mb-1">Hello, <%= currentUser.getUsername() %>!</h5>
                                <p class="mb-0 text-muted">
                                    This is your first time logging in. Please complete your profile setup 
                                    to secure your account and unlock all features.
                                </p>
                            </div>
                        </div>
                    </div>

                    <!-- Error Message -->
                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>

                    <!-- Setup Form -->
                    <form action="${pageContext.request.contextPath}/AuthServlet" method="post" id="setupForm">
                        <input type="hidden" name="action" value="firstTimeSetup">
                        
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-floating">
                                    <input type="password" class="form-control" id="newPassword" name="newPassword" 
                                           placeholder="New Password" required>
                                    <label for="newPassword"><i class="fas fa-lock me-2"></i>New Password</label>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-floating">
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                                           placeholder="Confirm Password" required>
                                    <label for="confirmPassword"><i class="fas fa-lock me-2"></i>Confirm Password</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-floating">
                            <input type="email" class="form-control" id="email" name="email" 
                                   placeholder="Email Address" required>
                            <label for="email"><i class="fas fa-envelope me-2"></i>Email Address *</label>
                        </div>

                        <div class="form-floating">
                            <input type="text" class="form-control" id="companyName" name="companyName" 
                                   placeholder="Company Name (Optional)">
                            <label for="companyName"><i class="fas fa-building me-2"></i>Company Name (Optional)</label>
                        </div>

                        <!-- Password Requirements -->
                        <div class="card bg-light border-0 mb-4">
                            <div class="card-body py-3">
                                <h6 class="card-title mb-2">
                                    <i class="fas fa-shield-alt me-2"></i>Password Requirements
                                </h6>
                                <div class="password-requirements">
                                    <div class="requirement" id="length-req">
                                        <i class="fas fa-times-circle invalid me-1"></i>
                                        At least 8 characters long
                                    </div>
                                    <div class="requirement" id="upper-req">
                                        <i class="fas fa-times-circle invalid me-1"></i>
                                        At least one uppercase letter
                                    </div>
                                    <div class="requirement" id="lower-req">
                                        <i class="fas fa-times-circle invalid me-1"></i>
                                        At least one lowercase letter
                                    </div>
                                    <div class="requirement" id="number-req">
                                        <i class="fas fa-times-circle invalid me-1"></i>
                                        At least one number
                                    </div>
                                    <div class="requirement" id="match-req">
                                        <i class="fas fa-times-circle invalid me-1"></i>
                                        Passwords match
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Important Notes -->
                        <div class="alert alert-info">
                            <h6><i class="fas fa-info-circle me-2"></i>Important Notes:</h6>
                            <ul class="mb-0 small">
                                <li>Your email will be used for password recovery</li>
                                <li>You can update company information later in your profile</li>
                                <li>This setup is required for manager accounts only</li>
                            </ul>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 mb-3" id="submitBtn" disabled>
                            <i class="fas fa-check me-2"></i>Complete Setup
                        </button>
                    </form>
                </div>
            </div>

            <!-- Footer -->
            <div class="text-center mt-4">
                <p class="text-white-50 small">
                    © 2024 Pahana Edu. All rights reserved.
                </p>
            </div>
        </div>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script>
        const newPasswordInput = document.getElementById('newPassword');
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const emailInput = document.getElementById('email');
        const submitBtn = document.getElementById('submitBtn');

        // Password validation
        function validateForm() {
            const password = newPasswordInput.value;
            const confirmPassword = confirmPasswordInput.value;
            const email = emailInput.value;
            
            // Check length
            updateRequirement('length-req', password.length >= 8);
            
            // Check uppercase
            updateRequirement('upper-req', /[A-Z]/.test(password));
            
            // Check lowercase
            updateRequirement('lower-req', /[a-z]/.test(password));
            
            // Check numbers
            updateRequirement('number-req', /\d/.test(password));
            
            // Check passwords match
            updateRequirement('match-req', password === confirmPassword && password.length > 0);
            
            // Enable/disable submit button
            const allPasswordValid = document.querySelectorAll('.requirement .valid').length === 5;
            const emailValid = email.trim().length > 0 && validateEmail(email);
            
            submitBtn.disabled = !(allPasswordValid && emailValid);
        }

        function updateRequirement(id, isValid) {
            const element = document.getElementById(id);
            const icon = element.querySelector('i');
            
            if (isValid) {
                element.classList.remove('invalid');
                element.classList.add('valid');
                icon.classList.remove('fa-times-circle', 'invalid');
                icon.classList.add('fa-check-circle', 'valid');
            } else {
                element.classList.remove('valid');
                element.classList.add('invalid');
                icon.classList.remove('fa-check-circle', 'valid');
                icon.classList.add('fa-times-circle', 'invalid');
            }
        }

        function validateEmail(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        }

        // Add event listeners
        newPasswordInput.addEventListener('input', validateForm);
        confirmPasswordInput.addEventListener('input', validateForm);
        emailInput.addEventListener('input', validateForm);

        // Form submission validation
        document.getElementById('setupForm').addEventListener('submit', function(e) {
            const password = newPasswordInput.value;
            const confirmPassword = confirmPasswordInput.value;
            const email = emailInput.value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match!');
                return;
            }
            
            if (password.length < 8) {
                e.preventDefault();
                alert('Password must be at least 8 characters long!');
                return;
            }
            
            if (!validateEmail(email)) {
                e.preventDefault();
                alert('Please enter a valid email address!');
                return;
            }
        });

        // Email validation styling
        emailInput.addEventListener('blur', function() {
            if (this.value && !validateEmail(this.value)) {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
            }
        });
    </script>
</body>
</html>