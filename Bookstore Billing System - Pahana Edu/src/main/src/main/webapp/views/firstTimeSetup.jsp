<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>First Time Setup - Pahana Edu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
       /* ... Keep all the existing CSS styles ... */
:root {
    --primary-color: #D86C36;
    --primary-dark: #C4552C;
    --primary-darker: #A63F22;
    --accent-color: #f2a23f;
    --background-light: #F2E7DC;
    --background-white: #fff;
}

.setup-container {
    min-height: 100vh;
    background: --background-white;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
}

.setup-card {
    background: var(--background-white);
    border-radius: 15px;
    box-shadow: 0 20px 40px rgba(0,0,0,0.1);
    padding: 0;
    width: 100%;
    max-width: 600px;
    overflow: hidden;
}

.setup-header {
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
    color: white;
    padding: 40px;
    text-align: center;
}

.setup-header h1 {
    margin: 0;
    font-size: 28px;
    font-weight: 600;
}

.setup-header p {
    margin: 10px 0 0 0;
    opacity: 0.9;
}

.setup-body {
    padding: 40px;
}

.progress-bar {
    background: #f0f0f0;
    height: 4px;
    border-radius: 2px;
    margin-bottom: 30px;
    overflow: hidden;
}

.progress-fill {
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
    height: 100%;
    width: 0%;
    transition: width 0.3s ease;
    border-radius: 2px;
}

.step-info {
    background: var(--background-light);
    padding: 20px;
    border-radius: 10px;
    margin-bottom: 30px;
    border-left: 4px solid var(--primary-color);
}

.step-info h3 {
    color: #333;
    margin: 0 0 10px 0;
}

.step-info p {
    color: #666;
    margin: 0;
    line-height: 1.6;
}

.form-group {
    margin-bottom: 25px;
}

.form-group label {
    display: block;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
}

.form-group label .required {
    color: #e74c3c;
}

.input-wrapper {
    position: relative;
}

.form-group input {
    width: 100%;
    padding: 12px 45px 12px 15px;
    border: 2px solid #e1e5e9;
    border-radius: 8px;
    font-size: 16px;
    transition: all 0.3s ease;
    box-sizing: border-box;
}

.form-group input:focus {
    outline: none;
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px rgba(216, 108, 54, 0.15);
}

.input-icon {
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
    color: #999;
}

.password-toggle {
    cursor: pointer;
    right: 40px;
}

.password-toggle:hover {
    color: var(--primary-color);
}

.password-strength {
    margin-top: 8px;
    padding: 10px;
    border-radius: 5px;
    font-size: 14px;
    display: none;
}

.password-strength.weak {
    background: #ffeaa7;
    color: #d35400;
    border: 1px solid #f39c12;
}

.password-strength.medium {
    background: #fdcb6e;
    color: #e17055;
    border: 1px solid #f39c12;
}

.password-strength.strong {
    background: #d5f4e6;
    color: #27ae60;
    border: 1px solid #27ae60;
}

.error-message {
    color: #e74c3c;
    font-size: 14px;
    margin-top: 5px;
    display: none;
}

.form-group.error input {
    border-color: #e74c3c;
    box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.1);
}

.form-group.error .error-message {
    display: block;
}

.alert {
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.alert-error {
    background: #ffeaa7;
    color: #d35400;
    border: 1px solid #f39c12;
}

.btn {
    padding: 15px 30px;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
}

.btn-primary {
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
    color: white;
    width: 100%;
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(216, 108, 54, 0.3);
}

.btn-primary:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
}

.btn-loader {
    display: none;
}

.security-tips {
    background: #e8f4fd;
    border: 1px solid #3498db;
    border-radius: 8px;
    padding: 20px;
    margin-top: 20px;
}

.security-tips h4 {
    color: #2980b9;
    margin: 0 0 15px 0;
    display: flex;
    align-items: center;
    gap: 10px;
}

.security-tips ul {
    margin: 0;
    padding-left: 20px;
    color: #34495e;
}

.security-tips li {
    margin-bottom: 8px;
}

.debug-info {
    background: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 20px;
    font-family: monospace;
    font-size: 12px;
    color: #6c757d;
}

@media (max-width: 768px) {
    .setup-container {
        padding: 10px;
    }
    
    .setup-card {
        max-width: 100%;
    }
    
    .setup-header, .setup-body {
        padding: 30px 20px;
    }
}
    </style>
</head>
<body>
    <div class="setup-container">
        <div class="setup-card">
            <div class="setup-header">
                <h1><i class="fas fa-cog"></i> First Time Setup</h1>
                <p>Complete your account setup to get started</p>
            </div>

            <div class="setup-body">
               

                <div class="progress-bar">
                    <div class="progress-fill" id="progressFill"></div>
                </div>

                <div class="step-info">
                    <h3><i class="fas fa-user-cog"></i> Account Setup</h3>
                    <p>Please update your password and provide your contact information. This ensures your account is secure and we can reach you for important system notifications.</p>
                </div>

                <!-- Error Message Display -->
                <c:if test="${not empty error}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-triangle"></i>
                        <span>${error}</span>
                    </div>
                </c:if>

                <!-- FIXED FORM ACTION -->
                <form id="setupForm" action="${pageContext.request.contextPath}/AuthServlet" method="post" novalidate>
                    <input type="hidden" name="action" value="firstTimeSetup">

                    <!-- New Password -->
                    <div class="form-group">
                        <label for="newPassword">New Password <span class="required">*</span></label>
                        <div class="input-wrapper">
                            <input type="password" 
                                   id="newPassword" 
                                   name="newPassword" 
                                   placeholder="Enter a strong password"
                                   required>
                            <i class="fas fa-lock input-icon"></i>
                            <i class="fas fa-eye password-toggle" id="toggleNewPassword"></i>
                        </div>
                        <div class="password-strength" id="passwordStrength"></div>
                        <div class="error-message" id="newPasswordError"></div>
                    </div>

                    <!-- Confirm Password -->
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password <span class="required">*</span></label>
                        <div class="input-wrapper">
                            <input type="password" 
                                   id="confirmPassword" 
                                   name="confirmPassword" 
                                   placeholder="Confirm your password"
                                   required>
                            <i class="fas fa-lock input-icon"></i>
                            <i class="fas fa-eye password-toggle" id="toggleConfirmPassword"></i>
                        </div>
                        <div class="error-message" id="confirmPasswordError"></div>
                    </div>

                    <!-- Email Address -->
                    <div class="form-group">
                        <label for="email">Email Address <span class="required">*</span></label>
                        <div class="input-wrapper">
                            <input type="email" 
                                   id="email" 
                                   name="email" 
                                   placeholder="Enter your email address"
                                   required>
                            <i class="fas fa-envelope input-icon"></i>
                        </div>
                        <div class="error-message" id="emailError"></div>
                    </div>

                    <!-- Company Name (Optional) -->
                    <div class="form-group">
                        <label for="companyName">Company/School Name</label>
                        <div class="input-wrapper">
                            <input type="text" 
                                   id="companyName" 
                                   name="companyName" 
                                   placeholder="Enter your company or school name (optional)">
                            <i class="fas fa-building input-icon"></i>
                        </div>
                    </div>

                    <!-- Submit Button -->
                    <button type="submit" class="btn btn-primary" id="setupButton">
                        <span class="btn-text">Complete Setup</span>
                        <span class="btn-loader" id="btnLoader">
                            <i class="fas fa-spinner fa-spin"></i>
                        </span>
                    </button>
                </form>

                <!-- Security Tips -->
                <div class="security-tips">
                    <h4><i class="fas fa-shield-alt"></i> Security Tips</h4>
                    <ul>
                        <li>Use at least 8 characters with a mix of uppercase, lowercase, numbers, and symbols</li>
                        <li>Your email will be used for password resets and system notifications</li>
                        <li>Never share your login credentials with others</li>
                        <li>You can update these details later in your profile settings</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            console.log('=== FIRST-TIME SETUP JS DEBUG ===');
            console.log('Page loaded, initializing form');
            
            const setupForm = document.getElementById('setupForm');
            const newPasswordInput = document.getElementById('newPassword');
            const confirmPasswordInput = document.getElementById('confirmPassword');
            const emailInput = document.getElementById('email');
            const companyNameInput = document.getElementById('companyName');
            const progressFill = document.getElementById('progressFill');

            console.log('Form action:', setupForm.action);

            // Password toggle functionality
            document.getElementById('toggleNewPassword').addEventListener('click', function() {
                togglePasswordVisibility('newPassword', this);
            });

            document.getElementById('toggleConfirmPassword').addEventListener('click', function() {
                togglePasswordVisibility('confirmPassword', this);
            });

            // Password strength checker
            newPasswordInput.addEventListener('input', function() {
                checkPasswordStrength(this.value);
                updateProgress();
            });

            // Real-time validation
            confirmPasswordInput.addEventListener('input', updateProgress);
            emailInput.addEventListener('input', updateProgress);
            companyNameInput.addEventListener('input', updateProgress);

            // Form submission
            setupForm.addEventListener('submit', function(e) {
                console.log('Form submitted, validating...');
                if (!validateForm()) {
                    console.log('Form validation failed');
                    e.preventDefault();
                    return false;
                }
                console.log('Form validation passed, submitting...');
                showLoading(true);
            });

            // Update progress on page load
            updateProgress();
        });

        function togglePasswordVisibility(inputId, toggleIcon) {
            const input = document.getElementById(inputId);
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);
            toggleIcon.classList.toggle('fa-eye');
            toggleIcon.classList.toggle('fa-eye-slash');
        }

        function checkPasswordStrength(password) {
            const strengthDiv = document.getElementById('passwordStrength');
            
            if (!password) {
                strengthDiv.style.display = 'none';
                return;
            }

            let score = 0;
            let feedback = [];

            // Length check
            if (password.length >= 8) {
                score += 2;
            } else if (password.length >= 6) {
                score += 1;
                feedback.push('Use at least 8 characters');
            } else {
                feedback.push('Too short (minimum 6 characters)');
            }

            // Character variety checks
            if (/[A-Z]/.test(password)) score += 1;
            else feedback.push('Add uppercase letters');

            if (/[a-z]/.test(password)) score += 1;
            else feedback.push('Add lowercase letters');

            if (/[0-9]/.test(password)) score += 1;
            else feedback.push('Add numbers');

            if (/[^A-Za-z0-9]/.test(password)) score += 1;
            else feedback.push('Add special characters');

            // Display strength
            strengthDiv.style.display = 'block';
            
            if (score >= 5) {
                strengthDiv.className = 'password-strength strong';
                strengthDiv.innerHTML = '<i class="fas fa-check-circle"></i> Strong password';
            } else if (score >= 3) {
                strengthDiv.className = 'password-strength medium';
                strengthDiv.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Medium strength: ' + feedback.slice(0, 2).join(', ');
            } else {
                strengthDiv.className = 'password-strength weak';
                strengthDiv.innerHTML = '<i class="fas fa-times-circle"></i> Weak password: ' + feedback.slice(0, 2).join(', ');
            }
        }

        function validateForm() {
            let isValid = true;

            // Clear previous errors
            document.querySelectorAll('.error-message').forEach(el => el.textContent = '');
            document.querySelectorAll('.form-group').forEach(el => el.classList.remove('error'));

            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const email = document.getElementById('email').value;

            console.log('Validating form data:');
            console.log('- New password length:', newPassword.length);
            console.log('- Confirm password length:', confirmPassword.length);
            console.log('- Email:', email);

            // Password validation
            if (!newPassword) {
                showError('newPasswordError', 'New password is required');
                isValid = false;
            } else if (newPassword.length < 6) {
                showError('newPasswordError', 'Password must be at least 6 characters');
                isValid = false;
            }

            // Confirm password validation
            if (!confirmPassword) {
                showError('confirmPasswordError', 'Please confirm your password');
                isValid = false;
            } else if (newPassword !== confirmPassword) {
                showError('confirmPasswordError', 'Passwords do not match');
                isValid = false;
            }

            // Email validation
            if (!email) {
                showError('emailError', 'Email address is required');
                isValid = false;
            } else if (!isValidEmail(email)) {
                showError('emailError', 'Please enter a valid email address');
                isValid = false;
            }

            console.log('Form validation result:', isValid);
            return isValid;
        }

        function showError(elementId, message) {
            const errorElement = document.getElementById(elementId);
            errorElement.textContent = message;
            errorElement.parentElement.classList.add('error');
        }

        function isValidEmail(email) {
            const emailPattern = /^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\.[A-Za-z]{2,})$/;
            return emailPattern.test(email);
        }

        function updateProgress() {
            const fields = [
                document.getElementById('newPassword').value,
                document.getElementById('confirmPassword').value,
                document.getElementById('email').value,
                document.getElementById('companyName').value
            ];

            let filledFields = 0;
            fields.forEach(field => {
                if (field && field.trim()) filledFields++;
            });

            // Company name is optional, so adjust calculation
            const totalRequiredFields = 3;
            const progress = (filledFields >= totalRequiredFields ? 
                            (filledFields / 4) * 100 : 
                            (filledFields / totalRequiredFields) * 100);

            document.getElementById('progressFill').style.width = progress + '%';
        }

        function showLoading(show) {
            const setupButton = document.getElementById('setupButton');
            const btnText = document.querySelector('.btn-text');
            const btnLoader = document.getElementById('btnLoader');

            if (show) {
                setupButton.disabled = true;
                btnText.style.display = 'none';
                btnLoader.style.display = 'inline-block';
            } else {
                setupButton.disabled = false;
                btnText.style.display = 'inline-block';
                btnLoader.style.display = 'none';
            }
        }
    </script>
</body>
</html>