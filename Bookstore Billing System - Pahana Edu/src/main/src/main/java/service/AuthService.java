package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtils;
import util.ValidationUtil;
import util.ValidationUtil.ValidationResult;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthService {

    private UserDAO userDAO;
    private EmailService emailService;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    private static final int RESET_TOKEN_VALIDITY_HOURS = 24;

    // Store failed login attempts (in production, use Redis or database)
    private Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    
    // Store password reset tokens (in production, use database)
    private Map<String, ResetToken> resetTokens = new ConcurrentHashMap<>();

    public AuthService() {
        this.userDAO = new UserDAO();
        this.emailService = new EmailService();
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
     * Inner class to track password reset tokens
     */
    private static class ResetToken {
        private String token;
        private int userId;
        private LocalDateTime createdAt;
        private boolean used;

        public ResetToken(String token, int userId) {
            this.token = token;
            this.userId = userId;
            this.createdAt = LocalDateTime.now();
            this.used = false;
        }

        public boolean isExpired() {
            long hoursSinceCreated = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
            return hoursSinceCreated >= RESET_TOKEN_VALIDITY_HOURS;
        }

        public boolean isValid() {
            return !used && !isExpired();
        }

        // Getters and setters
        public String getToken() { return token; }
        public int getUserId() { return userId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public boolean isUsed() { return used; }
        public void setUsed(boolean used) { this.used = used; }
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
     * User creation result class
     */
    public static class UserCreationResult {
        private boolean success;
        private User user;
        private String message;
        private String temporaryPassword;

        public UserCreationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public UserCreationResult(boolean success, User user, String message, String temporaryPassword) {
            this.success = success;
            this.user = user;
            this.message = message;
            this.temporaryPassword = temporaryPassword;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public User getUser() { return user; }
        public String getMessage() { return message; }
        public String getTemporaryPassword() { return temporaryPassword; }
    }

    /**
     * Password reset result class
     */
    public static class PasswordResetResult {
        private boolean success;
        private String message;
        private String token;

        public PasswordResetResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public PasswordResetResult(boolean success, String message, String token) {
            this.success = success;
            this.message = message;
            this.token = token;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getToken() { return token; }
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

            System.out.println("Login successful for user: " + username);
            return new AuthResult(true, user, "Authentication successful");

        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
            return new AuthResult(false, "Authentication system error. Please try again.");
        }
    }

    /**
     * Create new user by admin
     */
    public UserCreationResult createUser(User adminUser, String username, String email, 
                                       String role, String fullName, String contactNumber) {
        
        // Validate admin permissions
        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            return new UserCreationResult(false, "Access denied. Only admins can create users.");
        }

        // Validate input
        ValidationUtil.ValidationResult validation = validateUserCreation(username, email, role, fullName);
        if (!validation.isValid()) {
            return new UserCreationResult(false, validation.getFirstError());
        }

        try {
            // Check if username already exists
            if (userDAO.findByUsername(username.trim().toLowerCase()) != null) {
                return new UserCreationResult(false, "Username already exists");
            }

            // Check if email already exists (if provided)
            if (email != null && !email.trim().isEmpty()) {
                if (userDAO.findByEmail(email.trim()) != null) {
                    return new UserCreationResult(false, "Email already registered");
                }
            }

            // Generate temporary password
            String temporaryPassword = PasswordUtils.generateTemporaryPassword();
            String hashedPassword = PasswordUtils.hashPassword(temporaryPassword);

            // Create user object
            User newUser = new User();
            newUser.setUsername(username.trim().toLowerCase());
            newUser.setPassword(hashedPassword);
            newUser.setEmail(email != null ? email.trim() : null);
            newUser.setRole(role);
            newUser.setFullName(fullName != null ? fullName.trim() : null);
            newUser.setContactNumber(contactNumber != null ? contactNumber.trim() : null);
            newUser.setStatus("active");
            newUser.setFirstLogin(true);
            newUser.setCreatedBy(adminUser.getUserId());

            // Save user to database
            int userId = userDAO.createUser(newUser);
            if (userId > 0) {
                newUser.setUserId(userId);
                
                // Log activity
                userDAO.logActivity(adminUser.getUserId(), "USER_CREATED", 
                    "Created new user: " + username + " with role: " + role);
                userDAO.logActivity(userId, "ACCOUNT_CREATED", 
                    "Account created by admin: " + adminUser.getUsername());

               

                System.out.println("User created successfully: " + username);
                return new UserCreationResult(true, newUser, "User created successfully", temporaryPassword);

            } else {
                return new UserCreationResult(false, "Failed to create user account");
            }

        } catch (SQLException e) {
            System.err.println("Database error during user creation: " + e.getMessage());
            e.printStackTrace();
            return new UserCreationResult(false, "System error occurred. Please try again.");
        }
    }

  


    /**
     * Reset user password by admin
     */
    public UserCreationResult resetUserPassword(User adminUser, int userId) {
        // Validate admin permissions
        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            return new UserCreationResult(false, "Access denied. Only admins can reset passwords.");
        }

        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                return new UserCreationResult(false, "User not found");
            }

            // Generate new temporary password
            String temporaryPassword = PasswordUtils.generateTemporaryPassword();
            String hashedPassword = PasswordUtils.hashPassword(temporaryPassword);

            // Update password and set first login flag
            userDAO.resetPasswordByAdmin(userId, hashedPassword);

            // Log activity
            userDAO.logActivity(adminUser.getUserId(), "PASSWORD_RESET_ADMIN", 
                "Reset password for user: " + user.getUsername());
            userDAO.logActivity(userId, "PASSWORD_RESET", 
                "Password reset by admin: " + adminUser.getUsername());

            

            System.out.println("Password reset for user: " + user.getUsername());
            return new UserCreationResult(true, user, "Password reset successfully", temporaryPassword);

        } catch (SQLException e) {
            System.err.println("Database error during password reset: " + e.getMessage());
            e.printStackTrace();
            return new UserCreationResult(false, "System error occurred. Please try again.");
        }
    }

    /**
     * Initiate password reset for user (self-service)
     */
    public PasswordResetResult initiatePasswordReset(String usernameOrEmail, String baseUrl) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return new PasswordResetResult(false, "Username or email is required");
        }

        try {
            User user = null;
            String input = usernameOrEmail.trim();
            
            // Try to find by email first, then by username
            if (input.contains("@")) {
                user = userDAO.findByEmail(input);
            } else {
                user = userDAO.findByUsername(input.toLowerCase());
            }

            // Always return success message for security (don't reveal if user exists)
            if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return new PasswordResetResult(true, 
                    "If an account exists with that information, a password reset link has been sent to the registered email address.");
            }

            // Check if user account is active
            if (!"active".equals(user.getStatus())) {
                return new PasswordResetResult(true, 
                    "If an account exists with that information, a password reset link has been sent to the registered email address.");
            }

            // Generate reset token
            String token = UUID.randomUUID().toString();
            ResetToken resetToken = new ResetToken(token, user.getUserId());
            resetTokens.put(token, resetToken);

         
            return new PasswordResetResult(true, 
                "If an account exists with that information, a password reset link has been sent to the registered email address.", 
                token);

        } catch (SQLException e) {
            System.err.println("Database error during password reset initiation: " + e.getMessage());
            e.printStackTrace();
            return new PasswordResetResult(false, "System error occurred. Please try again.");
        }
    }

    /**
     * Validate reset token
     */
    public boolean isValidResetToken(String token) throws SQLException {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        ResetToken resetToken = resetTokens.get(token);
        return resetToken != null && resetToken.isValid();
    }

    /**
     * Complete password reset using token
     */
    public ValidationResult completePasswordReset(String token, String newPassword, String confirmPassword) {
        ValidationResult result = new ValidationResult();

        if (token == null || token.trim().isEmpty()) {
            result.addError("Reset token is required");
            return result;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.addError("New password is required");
            return result;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            result.addError("Password confirmation is required");
            return result;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            result.addError("Passwords do not match");
            return result;
        }

        // Validate password strength
        if (!ValidationUtil.isValidPassword(newPassword)) {
            result.addError("Password must be at least 8 characters long and contain letters, numbers, and special characters");
            return result;
        }

        try {
            ResetToken resetToken = resetTokens.get(token);
            
            if (resetToken == null || !resetToken.isValid()) {
                result.addError("Invalid or expired reset token");
                return result;
            }

            // Mark token as used
            resetToken.setUsed(true);

            // Update user password
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            userDAO.updatePassword(resetToken.getUserId(), hashedPassword);
            
            // Clear first login flag if set
            userDAO.clearFirstLoginFlag(resetToken.getUserId());

            // Log activity
            userDAO.logActivity(resetToken.getUserId(), "PASSWORD_RESET_COMPLETED", 
                "Password reset completed successfully");

            System.out.println("Password reset completed for user ID: " + resetToken.getUserId());

        } catch (SQLException e) {
            System.err.println("Database error during password reset completion: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }

        return result;
    }

    /**
     * Change user password (authenticated user)
     */
    public ValidationResult changePassword(User user, String currentPassword, String newPassword, String confirmPassword) {
        ValidationResult result = new ValidationResult();

        if (user == null) {
            result.addError("User session invalid");
            return result;
        }

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            result.addError("Current password is required");
            return result;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.addError("New password is required");
            return result;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            result.addError("Password confirmation is required");
            return result;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            result.addError("Passwords do not match");
            return result;
        }

        // Validate new password strength
        if (!ValidationUtil.isValidPassword(newPassword)) {
            result.addError("New password must be at least 8 characters long and contain letters, numbers, and special characters");
            return result;
        }

        // Check if new password is same as current
        if (currentPassword.equals(newPassword)) {
            result.addError("New password must be different from current password");
            return result;
        }

        try {
            // Verify current password
            User dbUser = userDAO.findById(user.getUserId());
            if (dbUser == null || !PasswordUtils.verifyPassword(currentPassword, dbUser.getPassword())) {
                result.addError("Current password is incorrect");
                return result;
            }

            // Update password
            String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
            userDAO.updatePassword(user.getUserId(), hashedNewPassword);
            
            // Clear first login flag
            userDAO.clearFirstLoginFlag(user.getUserId());

            // Log activity
            userDAO.logActivity(user.getUserId(), "PASSWORD_CHANGED", 
                "Password changed by user");

         

            System.out.println("Password changed successfully for user: " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("Database error during password change: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }

        return result;
    }

    /**
     * Complete first time setup - CORRECTED VERSION
     */
    public ValidationResult completeFirstTimeSetup(User user, String newPassword, String confirmPassword, 
                                                  String email, String companyName) {
        ValidationResult result = new ValidationResult();

        System.out.println("=== AUTHSERVICE SETUP DEBUG START ===");
        System.out.println("User: " + (user != null ? user.getUsername() : "null"));
        System.out.println("User ID: " + (user != null ? user.getUserId() : "null"));
        System.out.println("User first login: " + (user != null ? user.isFirstLogin() : "null"));

        if (user == null) {
            result.addError("User session invalid");
            return result;
        }

        if (!user.isFirstLogin()) {
            result.addError("First time setup already completed");
            return result;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.addError("New password is required");
            return result;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            result.addError("Password confirmation is required");
            return result;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            result.addError("Passwords do not match");
            return result;
        }

        // Validate password strength
        if (!ValidationUtil.isValidPassword(newPassword)) {
            result.addError("Password must be at least 8 characters long and contain letters, numbers, and special characters");
            return result;
        }

        // Validate email if provided
        if (email != null && !email.trim().isEmpty()) {
            if (!ValidationUtil.isValidEmail(email)) {
                result.addError("Invalid email format");
                return result;
            }
            
            // Check if email is already used by another user
            try {
                User emailUser = userDAO.findByEmail(email.trim());
                if (emailUser != null && emailUser.getUserId() != user.getUserId()) {
                    result.addError("Email already registered with another account");
                    return result;
                }
            } catch (SQLException e) {
                System.err.println("Database error checking email: " + e.getMessage());
                result.addError("System error occurred. Please try again.");
                return result;
            }
        }

        try {
            System.out.println("Starting database update process...");
            
            // Hash the password once here
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            System.out.println("Password hashed successfully, length: " + hashedPassword.length());
            
            // Use the atomic method to update everything in one transaction
            userDAO.completeFirstTimeSetupAtomic(
                user.getUserId(), 
                hashedPassword, 
                email != null ? email.trim() : null, 
                companyName != null ? companyName.trim() : null
            );
            System.out.println("Database update completed successfully");

            // Log activity
            userDAO.logActivity(user.getUserId(), "FIRST_TIME_SETUP_COMPLETED",
                "First time setup completed");
            System.out.println("Activity logged successfully");

            System.out.println("=== AUTHSERVICE SETUP DEBUG END - SUCCESS ===");

        } catch (SQLException e) {
            System.out.println("=== AUTHSERVICE SETUP DEBUG END - ERROR ===");
            System.err.println("Database error during first time setup: " + e.getMessage());
            e.printStackTrace();
            result.addError("System error occurred. Please try again.");
        }

        return result;
    }
    /**
     * Get all users for admin management
     */
    public List<User> getAllUsers(User adminUser) {
        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            return new ArrayList<>();
        }

        try {
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            System.err.println("Database error getting users: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Validate user creation input
     */
    private ValidationUtil.ValidationResult validateUserCreation(String username, String email, String role, String fullName) {
        ValidationUtil.ValidationResult result = new ValidationUtil.ValidationResult();

        // Username validation
        if (username == null || username.trim().isEmpty()) {
            result.addError("Username is required");
        } else if (!ValidationUtil.isValidUsername(username.trim())) {
            result.addError("Username must be 3-30 characters, letters, numbers, and underscores only");
        }

        // Email validation (optional but must be valid if provided)
        if (email != null && !email.trim().isEmpty()) {
            if (!ValidationUtil.isValidEmail(email)) {
                result.addError("Invalid email format");
            }
        }

        // Role validation
        if (role == null || role.trim().isEmpty()) {
            result.addError("Role is required");
        } else if (!isValidRole(role)) {
            result.addError("Invalid role. Allowed roles: admin, manager, staff");
        }

        // Full name validation
        if (fullName != null && !fullName.trim().isEmpty()) {
            if (!ValidationUtil.isValidName(fullName)) {
                result.addError("Invalid full name format");
            }
        }

        return result;
    }

    /**
     * Check if role is valid
     */
    private boolean isValidRole(String role) {
        return "admin".equals(role) || "manager".equals(role) || "staff".equals(role);
    }

    /**
     * Record failed login attempt
     */
    private void recordFailedLogin(String lockKey, String username, String clientIP, String reason) {
        LoginAttempt attempt = loginAttempts.get(lockKey);
        if (attempt == null) {
            loginAttempts.put(lockKey, new LoginAttempt());
            System.out.println("First failed login attempt for: " + username + " from IP: " + clientIP + " (" + reason + ")");
        } else {
            attempt.increment();
            System.out.println("Failed login attempt #" + attempt.getAttempts() + " for: " + username + 
                                 " from IP: " + clientIP + " (" + reason + ")");

            if (attempt.isLocked()) {
                System.out.println("Account locked for: " + username + " from IP: " + clientIP);
            }
        }
    }

    /**
     * Clean up expired reset tokens (should be called periodically)
     */
    public void cleanupExpiredTokens() {
        resetTokens.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}