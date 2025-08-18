package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtils;
import util.ValidationUtil;
import util.EmailUtils;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AuthService {

    private UserDAO userDAO;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    // Store failed login attempts (in production, use Redis or database)
    private Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Inner class to track login attempts
     */
    private static class LoginAttempt {
        private int attempts;
        private LocalDateTime lastAttempt;

        public LoginAttempt() {
            this.attempts = 1;
            this.lastAttempt = LocalDateTime.now();
        }

        public void increment() {
            this.attempts++;
            this.lastAttempt = LocalDateTime.now();
        }

        public boolean isLocked() {
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                long minutesSinceLastAttempt = ChronoUnit.MINUTES.between(lastAttempt, LocalDateTime.now());
                return minutesSinceLastAttempt < LOCKOUT_DURATION_MINUTES;
            }
            return false;
        }

        public int getRemainingLockoutMinutes() {
            if (isLocked()) {
                long minutesSinceLastAttempt = ChronoUnit.MINUTES.between(lastAttempt, LocalDateTime.now());
                return (int) (LOCKOUT_DURATION_MINUTES - minutesSinceLastAttempt);
            }
            return 0;
        }

        public int getAttempts() { return attempts; }
        public LocalDateTime getLastAttempt() { return lastAttempt; }
    }

    /**
     * Authentication result class
     */
    public static class AuthResult {
        private boolean success;
        private User user;
        private String message;
        private boolean accountLocked;
        private int remainingLockoutMinutes;
        private boolean firstTimeLogin;

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public AuthResult(boolean success, User user, String message) {
            this.success = success;
            this.user = user;
            this.message = message;
            this.firstTimeLogin = user != null && user.isFirstLogin();
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public User getUser() { return user; }
        public String getMessage() { return message; }
        public boolean isAccountLocked() { return accountLocked; }
        public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }
        public int getRemainingLockoutMinutes() { return remainingLockoutMinutes; }
        public void setRemainingLockoutMinutes(int minutes) { this.remainingLockoutMinutes = minutes; }
        public boolean isFirstTimeLogin() { return firstTimeLogin; }
    }

    /**
     * Authenticate user with enhanced security
     */
    public AuthResult authenticateUser(String username, String password, String clientIP) {
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            return new AuthResult(false, "Username is required");
        }

        if (password == null || password.trim().isEmpty()) {
            return new AuthResult(false, "Password is required");
        }

        // Clean username
        username = username.trim().toLowerCase();

        // Check if account is locked
        String lockKey = username + ":" + clientIP;
        LoginAttempt attempt = loginAttempts.get(lockKey);
        if (attempt != null && attempt.isLocked()) {
            AuthResult result = new AuthResult(false, 
                "Account temporarily locked due to multiple failed login attempts. " +
                "Try again in " + attempt.getRemainingLockoutMinutes() + " minutes.");
            result.setAccountLocked(true);
            result.setRemainingLockoutMinutes(attempt.getRemainingLockoutMinutes());
            return result;
        }

        try {
            // Find user first
            User user = userDAO.findByUsername(username);

            if (user == null) {
                // User doesn't exist
                recordFailedLogin(lockKey, username, clientIP, "User not found");
                return new AuthResult(false, "Invalid username or password");
            }

            // Check if user is active
            if (!"active".equals(user.getStatus())) {
                userDAO.logActivity(user.getUserId(), "LOGIN_BLOCKED", 
                    "Login blocked - account status: " + user.getStatus() + " from IP: " + clientIP);
                return new AuthResult(false, "Your account has been deactivated. Please contact administrator.");
            }

            // Verify password
            if (!PasswordUtils.verifyPassword(password, user.getPassword())) {
                // Wrong password
                recordFailedLogin(lockKey, username, clientIP, "Incorrect password");
                userDAO.logActivity(user.getUserId(), "LOGIN_FAILED", 
                    "Failed login attempt - incorrect password from IP: " + clientIP);

                // Check if this failure caused account lockout
                attempt = loginAttempts.get(lockKey);
                if (attempt != null && attempt.isLocked()) {
                    AuthResult result = new AuthResult(false, 
                        "Account locked due to multiple failed attempts. Try again in " 
                        + attempt.getRemainingLockoutMinutes() + " minutes.");
                    result.setAccountLocked(true);
                    result.setRemainingLockoutMinutes(attempt.getRemainingLockoutMinutes());
                    return result;
                }

                return new AuthResult(false, "Invalid username or password");
            }

            // Authentication successful
            loginAttempts.remove(lockKey); // Clear failed attempts
            userDAO.updateLastLogin(user.getUserId());
            userDAO.logActivity(user.getUserId(), "LOGIN_SUCCESS", 
                                  "Successful login from IP: " + clientIP);

            System.out.println("✅ Login successful for user: " + username);
            return new AuthResult(true, user, "Authentication successful");

        } catch (SQLException e) {
            System.err.println("❌ Database error during authentication: " + e.getMessage());
            e.printStackTrace();
            return new AuthResult(false, "Authentication system error. Please try again.");
        }
    }

    /**
     * Record failed login attempt
     */
    private void recordFailedLogin(String lockKey, String username, String clientIP, String reason) {
        LoginAttempt attempt = loginAttempts.get(lockKey);
        if (attempt == null) {
            loginAttempts.put(lockKey, new LoginAttempt());
            System.out.println("⚠️ First failed login attempt for: " + username + " from IP: " + clientIP + " (" + reason + ")");
        } else {
            attempt.increment();
            System.out.println("⚠️ Failed login attempt #" + attempt.getAttempts() + " for: " + username + 
                                 " from IP: " + clientIP + " (" + reason + ")");

            if (attempt.isLocked()) {
                System.out.println("🔒 Account locked for: " + username + " from IP: " + clientIP);
            }
        }
    }

    /**
     * Initiate password reset by generating a token and sending a reset link via email.
     */
    public ValidationUtil.ValidationResult initiatePasswordReset(String username, String baseUrl) {
        ValidationUtil.ValidationResult result = new ValidationUtil.ValidationResult();
        
        if (username == null || username.trim().isEmpty()) {
            result.addError("Username is required");
            return result;
        }

        try {
            User user = userDAO.findByUsername(username.trim().toLowerCase());
            
            if (user != null && "manager".equals(user.getRole()) && user.hasEmail() && "active".equals(user.getStatus())) {
                // Generate and save reset token
                String resetToken = PasswordUtils.generateResetToken();
                userDAO.savePasswordResetToken(user.getUserId(), resetToken);
                
                // Send reset email
                String resetLink = baseUrl + "/AuthServlet?action=resetPassword&token=" + resetToken;
                boolean emailSent = EmailUtils.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
                
                if (emailSent) {
                    userDAO.logActivity(user.getUserId(), "PASSWORD_RESET_INITIATED", "Password reset email sent.");
                    System.out.println("✅ Password reset email sent to: " + user.getEmail());
                } else {
                    result.addError("Failed to send reset email. Please check email configuration.");
                }
            } else {
                result.addError("User not found or email invalid");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error during password reset initiation: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }
        
        return result;
    }

    /**
     * Complete the password reset process by updating the password with a valid token.
     */
    public ValidationUtil.ValidationResult completePasswordReset(String token, String newPassword, String confirmPassword) {
        ValidationUtil.ValidationResult result = new ValidationUtil.ValidationResult();

        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            result.addError("Invalid reset token");
            return result;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.addError("New password is required");
        } else {
            ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(newPassword);
            if (!passwordResult.isValid()) {
                result.addError("Password validation failed: " + passwordResult.getFirstError());
            }
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            result.addError("Passwords do not match");
        }

        if (!result.isValid()) {
            return result;
        }

        try {
            // Validate token
            if (!userDAO.isValidResetToken(token)) {
                result.addError("Invalid or expired reset token");
                return result;
            }

            // Reset password
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            int userId = userDAO.resetPassword(token, hashedPassword);

            if (userId > 0) {
                userDAO.logActivity(userId, "PASSWORD_RESET_COMPLETED", "Password successfully reset using token");
                System.out.println("✅ Password reset completed for user ID: " + userId);
            } else {
                result.addError("Failed to reset password. Invalid token.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error during password reset completion: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }

        return result;
    }

    /**
     * Complete the first-time setup for the manager user (set up password, email, and company details).
     */
    public ValidationUtil.ValidationResult completeFirstTimeSetup(User user, String newPassword, 
                                                                 String confirmPassword, String email, 
                                                                 String companyName) {
        ValidationUtil.ValidationResult result = new ValidationUtil.ValidationResult();

        // Validate user permission
        if (user == null || !"manager".equals(user.getRole())) {
            result.addError("Access denied. Only managers can complete setup.");
            return result;
        }

        if (!user.isFirstLogin()) {
            result.addError("First-time setup has already been completed.");
            return result;
        }

        // Validate password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.addError("New password is required");
        } else {
            ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(newPassword);
            if (!passwordResult.isValid()) {
                result.addError("Password validation failed: " + passwordResult.getFirstError());
            }
        }

        // Check password confirmation
        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            result.addError("Passwords do not match");
        }

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            result.addError("Email is required");
        } else if (!ValidationUtil.isValidEmail(email)) {
            result.addError("Please enter a valid email address");
        }

        if (!result.isValid()) {
            return result;
        }

        try {
            // Check if email is already used by another user
            User existingUser = userDAO.findByEmail(email.trim());
            if (existingUser != null && existingUser.getUserId() != user.getUserId()) {
                result.addError("This email is already registered with another account");
                return result;
            }

            // Update user account
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            userDAO.completeFirstTimeSetup(user.getUserId(), hashedPassword, email.trim(), companyName != null ? companyName.trim() : null);

            // Update user object for session
            user.setEmail(email.trim());
            user.setFirstLogin(false);
            if (companyName != null && !companyName.trim().isEmpty()) {
                user.setCompanyName(companyName.trim());
            }

            // Log activity
            userDAO.logActivity(user.getUserId(), "FIRST_TIME_SETUP", "Completed first-time setup - password and email configured");

            

            System.out.println("✅ First-time setup completed for user: " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("❌ Database error during first-time setup: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred during setup. Please try again.");
        }

        return result;
    }

    /**
     * Change the user's password.
     */
    public ValidationUtil.ValidationResult changePassword(User user, String currentPassword, 
                                                         String newPassword, String confirmPassword) {
        ValidationUtil.ValidationResult result = new ValidationUtil.ValidationResult();

        // Validate inputs
        if (user == null) {
            result.addError("User session invalid");
            return result;
        }

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            result.addError("Current password is required");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.addError("New password is required");
        } else {
            ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(newPassword);
            if (!passwordResult.isValid()) {
                result.addError("New password validation failed: " + passwordResult.getFirstError());
            }
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            result.addError("New passwords do not match");
        }

        if (currentPassword != null && currentPassword.equals(newPassword)) {
            result.addError("New password must be different from current password");
        }

        if (!result.isValid()) {
            return result;
        }

        try {
            // Verify current password
            if (!PasswordUtils.verifyPassword(currentPassword, user.getPassword())) {
                result.addError("Current password is incorrect");
                return result;
            }

            // Update password
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            userDAO.updatePassword(user.getUserId(), hashedPassword);

            // Log activity
            userDAO.logActivity(user.getUserId(), "PASSWORD_CHANGED", "Password changed successfully");
            System.out.println("✅ Password changed for user: " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("❌ Database error during password change: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }

        return result;
    }
}
