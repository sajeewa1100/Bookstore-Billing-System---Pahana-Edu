package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtils;
import util.ValidationUtil;
import util.EmailUtils;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Authentication Service
 * Handles all authentication-related business logic
 */
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
        if (!ValidationUtil.isValidUsername(username)) {
            return new AuthResult(false, "Invalid username format");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new AuthResult(false, "Password is required");
        }
        
        // Check if account is locked
        String lockKey = username.toLowerCase() + ":" + clientIP;
        LoginAttempt attempt = loginAttempts.get(lockKey);
        if (attempt != null && attempt.isLocked()) {
            AuthResult result = new AuthResult(false, "Account temporarily locked due to multiple failed login attempts");
            result.setAccountLocked(true);
            result.setRemainingLockoutMinutes(attempt.getRemainingLockoutMinutes());
            return result;
        }
        
        try {
            // Authenticate with database
            User user = userDAO.authenticateUser(username.trim(), password);
            
            if (user != null) {
                // Authentication successful
                loginAttempts.remove(lockKey); // Clear failed attempts
                userDAO.updateLastLogin(user.getUserId());
                userDAO.logActivity(user.getUserId(), "LOGIN_SUCCESS", 
                                  "Successful login from IP: " + clientIP);
                
                return new AuthResult(true, user, "Authentication successful");
                
            } else {
                // Authentication failed
                recordFailedLogin(lockKey, username, clientIP);
                
                // Check if user exists (for logging purposes)
                User existingUser = userDAO.findByUsername(username.trim());
                if (existingUser != null) {
                    userDAO.logActivity(existingUser.getUserId(), "LOGIN_FAILED", 
                                      "Failed login attempt from IP: " + clientIP + " (incorrect password)");
                } else {
                    // Log failed attempt for non-existent user (security logging)
                    System.out.println("Failed login attempt for non-existent user: " + username + " from IP: " + clientIP);
                }
                
                AuthResult result = new AuthResult(false, "Invalid username or password");
                
                // Check if this failure caused account lockout
                attempt = loginAttempts.get(lockKey);
                if (attempt != null && attempt.isLocked()) {
                    result.setAccountLocked(true);
                    result.setRemainingLockoutMinutes(attempt.getRemainingLockoutMinutes());
                    result = new AuthResult(false, "Account locked due to multiple failed attempts. Try again in " 
                                           + attempt.getRemainingLockoutMinutes() + " minutes.");
                }
                
                return result;
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
            return new AuthResult(false, "Authentication system error. Please try again.");
        }
    }
    
    /**
     * Complete first-time setup for manager
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
        ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(newPassword);
        if (!passwordResult.isValid()) {
            result.addError("Password validation failed: " + passwordResult.getFirstError());
        }
        
        // Check password confirmation
        if (!newPassword.equals(confirmPassword)) {
            result.addError("Passwords do not match");
        }
        
        // Validate email
        if (!ValidationUtil.isValidEmail(email)) {
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
            userDAO.completeFirstTimeSetup(user.getUserId(), hashedPassword, email.trim(), 
                                         companyName != null ? companyName.trim() : null);
            
            // Update user object
            user.setEmail(email.trim());
            user.setFirstLogin(false);
            if (companyName != null && !companyName.trim().isEmpty()) {
                user.setCompanyName(companyName.trim());
            }
            
            // Log activity
            userDAO.logActivity(user.getUserId(), "FIRST_TIME_SETUP", 
                              "Completed first-time setup - password and email configured");
            
            // Send welcome email
            try {
                EmailUtils.sendWelcomeEmail(email.trim(), user.getUsername(), companyName);
            } catch (Exception e) {
                System.err.println("Failed to send welcome email: " + e.getMessage());
                // Don't fail the setup if email fails
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during first-time setup: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred during setup. Please try again.");
        }
        
        return result;
    }
    
    /**
     * Initiate password reset process
     */
    public ValidationUtil.ValidationResult initiatePasswordReset(String username, String baseUrl) {
        ValidationUtil.ValidationResult result = new ValidationUtil.ValidationResult();
        
        if (!ValidationUtil.isValidUsername(username)) {
            result.addError("Invalid username format");
            return result;
        }
        
        try {
            User user = userDAO.findByUsername(username.trim());
            
            if (user != null && "manager".equals(user.getRole()) && user.hasEmail()) {
                // Generate and save reset token
                String resetToken = PasswordUtils.generateResetToken();
                userDAO.savePasswordResetToken(user.getUserId(), resetToken);
                
                // Send reset email
                String resetLink = baseUrl + "/AuthServlet?action=resetPassword&token=" + resetToken;
                boolean emailSent = EmailUtils.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
                
                if (emailSent) {
                    userDAO.logActivity(user.getUserId(), "PASSWORD_RESET_INITIATED", 
                                      "Password reset email sent to: " + user.getEmail());
                } else {
                    result.addError("Failed to send reset email. Please try again later.");
                }
            } else {
                // For security, don't reveal whether user exists
                System.out.println("Password reset attempted for: " + username + 
                                 (user == null ? " (user not found)" : 
                                  !user.hasEmail() ? " (no email)" : 
                                  !" manager".equals(user.getRole()) ? " (not manager)" : ""));
            }
            
            // Always return success message for security
            if (result.isValid()) {
                result = new ValidationUtil.ValidationResult(); // Create new clean result
                // Don't add success message to result, handle in servlet
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during password reset: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }
        
        return result;
    }
    
    /**
     * Complete password reset process
     */
    public ValidationUtil.ValidationResult completePasswordReset(String token, String newPassword, 
                                                                String confirmPassword) {
        ValidationUtil.ValidationResult result = new ValidationUtil.ValidationResult();
        
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            result.addError("Invalid reset token");
            return result;
        }
        
        ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(newPassword);
        if (!passwordResult.isValid()) {
            result.addError("Password validation failed: " + passwordResult.getFirstError());
        }
        
        if (!newPassword.equals(confirmPassword)) {
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
                userDAO.logActivity(userId, "PASSWORD_RESET_COMPLETED", 
                                  "Password successfully reset using token");
            } else {
                result.addError("Failed to reset password. Invalid token.");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during password reset completion: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }
        
        return result;
    }
    
    /**
     * Change user password
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
        
        ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(newPassword);
        if (!passwordResult.isValid()) {
            result.addError("New password validation failed: " + passwordResult.getFirstError());
        }
        
        if (!newPassword.equals(confirmPassword)) {
            result.addError("New passwords do not match");
        }
        
        if (currentPassword.equals(newPassword)) {
            result.addError("New password must be different from current password");
        }
        
        if (!result.isValid()) {
            return result;
        }
        
        try {
            // Verify current password
            User authenticatedUser = userDAO.authenticateUser(user.getUsername(), currentPassword);
            if (authenticatedUser == null) {
                result.addError("Current password is incorrect");
                return result;
            }
            
            // Update password
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            userDAO.updatePassword(user.getUserId(), hashedPassword);
            
            // Log activity
            userDAO.logActivity(user.getUserId(), "PASSWORD_CHANGED", "Password changed successfully");
            
        } catch (SQLException e) {
            System.err.println("Database error during password change: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }
        
        return result;
    }
    
    /**
     * Create new user account
     */
    public ValidationUtil.ValidationResult createUserAccount(User creator, String username, String password, 
                                                            String email, String role) {
        ValidationUtil.ValidationResult result = new ValidationUtil.ValidationResult();
        
        // Check permissions
        if (creator == null || !"manager".equals(creator.getRole())) {
            result.addError("Access denied. Only managers can create user accounts.");
            return result;
        }
        
        // Validate inputs
        ValidationUtil.ValidationResult userValidation = ValidationUtil.validateUser(username, password, password, email, role);
        if (!userValidation.isValid()) {
            return userValidation;
        }
        
        try {
            // Check if username already exists
            User existingUser = userDAO.findByUsername(username.trim());
            if (existingUser != null) {
                result.addError("Username already exists");
                return result;
            }
            
            // Check if email already exists (if provided)
            if (email != null && !email.trim().isEmpty()) {
                User existingEmailUser = userDAO.findByEmail(email.trim());
                if (existingEmailUser != null) {
                    result.addError("Email already registered with another account");
                    return result;
                }
            }
            
            // Create user
            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setPassword(PasswordUtils.hashPassword(password));
            newUser.setRole(role);
            newUser.setEmail(email != null ? email.trim() : null);
            newUser.setStatus("active");
            newUser.setFirstLogin("manager".equals(role)); // Managers need first-time setup
            
            int userId = userDAO.createUser(newUser);
            
            if (userId > 0) {
                // Log activity
                userDAO.logActivity(creator.getUserId(), "USER_CREATED", 
                                  "Created new user account: " + username + " (role: " + role + ")");
                
                // Send account creation email if email provided
                if (email != null && !email.trim().isEmpty()) {
                    try {
                        EmailUtils.sendAccountCreatedEmail(email.trim(), username, password);
                    } catch (Exception e) {
                        System.err.println("Failed to send account creation email: " + e.getMessage());
                        // Don't fail account creation if email fails
                    }
                }
            } else {
                result.addError("Failed to create user account");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during user creation: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }
        
        return result;
    }
    
    /**
     * Record failed login attempt
     */
    private void recordFailedLogin(String lockKey, String username, String clientIP) {
        LoginAttempt attempt = loginAttempts.get(lockKey);
        if (attempt == null) {
            loginAttempts.put(lockKey, new LoginAttempt());}
    }
}