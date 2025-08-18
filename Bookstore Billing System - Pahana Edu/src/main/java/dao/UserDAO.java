package dao;

import model.User;
import util.ConnectionManager;
import util.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * UserDAO - Fixed and Enhanced
 * Handles all database operations related to users with better error handling
 */
public class UserDAO {
    private Connection connection;
    
    public UserDAO() {
        try {
            this.connection = ConnectionManager.getInstance().getConnection();
            if (this.connection == null) {
                System.err.println("❌ Failed to get database connection in UserDAO");
            } else {
                System.out.println("✅ Database connection established in UserDAO");
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing UserDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Authenticate user with username and password - FIXED
     */
    public User authenticateUser(String username, String password) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND status = 'active'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            
            System.out.println("🔍 Authenticating user: " + username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    // Use PasswordUtils for secure password verification
                    if (PasswordUtils.verifyPassword(password, storedPassword)) {
                        User user = mapResultSetToUser(rs);
                        System.out.println("✅ Authentication successful for user: " + username);
                        return user;
                    } else {
                        System.out.println("❌ Password verification failed for user: " + username);
                    }
                } else {
                    System.out.println("❌ User not found or inactive: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error during authentication: " + e.getMessage());
            throw e;
        }
        return null;
    }
    
    /**
     * Find user by username - CASE INSENSITIVE
     */
    public User findByUsername(String username) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding user by username: " + e.getMessage());
            throw e;
        }
        return null;
    }
    
    /**
     * Find user by email address
     */
    public User findByEmail(String email) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding user by email: " + e.getMessage());
            throw e;
        }
        return null;
    }
    
    /**
     * Update user's last login timestamp
     */
    public void updateLastLogin(int userId) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("✅ Last login updated for user ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating last login: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Complete first-time setup for manager
     */
    public void completeFirstTimeSetup(int userId, String hashedPassword, String email, String companyName) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "UPDATE users SET password = ?, email = ?, company_name = ?, first_login = FALSE, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);
            stmt.setString(3, companyName);
            stmt.setInt(4, userId);
            
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("✅ First-time setup completed for user ID: " + userId);
            } else {
                throw new SQLException("Failed to update user during first-time setup");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error completing first-time setup: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Save password reset token
     */
    public void savePasswordResetToken(int userId, String token) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        try {
            // Start transaction
            connection.setAutoCommit(false);
            
            // First, delete any existing tokens for this user
            String deleteSql = "DELETE FROM password_reset_tokens WHERE user_id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.executeUpdate();
            }
            
            // Insert new token
            String insertSql = "INSERT INTO password_reset_tokens (user_id, token, expires_at) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 1 HOUR))";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, token);
                int inserted = insertStmt.executeUpdate();
                
                if (inserted > 0) {
                    System.out.println("✅ Password reset token saved for user ID: " + userId);
                } else {
                    throw new SQLException("Failed to insert password reset token");
                }
            }
            
            // Commit transaction
            connection.commit();
            
        } catch (SQLException e) {
            // Rollback on error
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Error saving password reset token: " + e.getMessage());
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("❌ Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
    
    /**
     * Validate password reset token
     */
    public boolean isValidResetToken(String token) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "SELECT COUNT(*) FROM password_reset_tokens WHERE token = ? AND expires_at > NOW()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean isValid = rs.getInt(1) > 0;
                    System.out.println(isValid ? "✅ Reset token is valid" : "❌ Reset token is invalid or expired");
                    return isValid;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error validating reset token: " + e.getMessage());
            throw e;
        }
        return false;
    }
    
    /**
     * Reset password using token
     */
    public int resetPassword(String token, String hashedPassword) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        int userId = 0;
        
        try {
            // Start transaction
            connection.setAutoCommit(false);
            
            // Get user ID from valid token
            String getUserSql = "SELECT user_id FROM password_reset_tokens WHERE token = ? AND expires_at > NOW()";
            try (PreparedStatement stmt = connection.prepareStatement(getUserSql)) {
                stmt.setString(1, token);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    } else {
                        throw new SQLException("Invalid or expired reset token");
                    }
                }
            }
            
            if (userId > 0) {
                // Update password
                String updateSql = "UPDATE users SET password = ?, updated_at = NOW() WHERE user_id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setString(1, hashedPassword);
                    updateStmt.setInt(2, userId);
                    int updated = updateStmt.executeUpdate();
                    
                    if (updated == 0) {
                        throw new SQLException("Failed to update password");
                    }
                }
                
                // Delete used token
                String deleteSql = "DELETE FROM password_reset_tokens WHERE token = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                    deleteStmt.setString(1, token);
                    deleteStmt.executeUpdate();
                }
                
                System.out.println("✅ Password reset completed for user ID: " + userId);
            }
            
            // Commit transaction
            connection.commit();
            
        } catch (SQLException e) {
            // Rollback on error
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("❌ Error resetting password: " + e.getMessage());
            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("❌ Error resetting auto-commit: " + e.getMessage());
            }
        }
        
        return userId;
    }
    
    /**
     * Update user password
     */
    public void updatePassword(int userId, String hashedPassword) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "UPDATE users SET password = ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("✅ Password updated for user ID: " + userId);
            } else {
                throw new SQLException("Failed to update password - user not found");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating password: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Log user activity for audit trail
     */
    public void logActivity(int userId, String action, String details) throws SQLException {
        if (connection == null) {
            System.err.println("⚠️ Cannot log activity - no database connection");
            return; // Don't throw exception for logging
        }
        
        // Try to create activity log table if it doesn't exist
        createActivityLogTableIfNotExists();
        
        String sql = "INSERT INTO user_activities (user_id, action, details, created_at) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, details);
            
            int inserted = stmt.executeUpdate();
            if (inserted > 0) {
                System.out.println("📝 Activity logged: " + action + " for user ID: " + userId);
            }
        } catch (SQLException e) {
            // Log error but don't throw - activity logging should not break main functionality
            System.err.println("⚠️ Warning: Failed to log activity: " + e.getMessage());
        }
    }
    
    /**
     * Create activity log table if it doesn't exist
     */
    private void createActivityLogTableIfNotExists() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS user_activities (
                activity_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                action VARCHAR(50) NOT NULL,
                details TEXT,
                ip_address VARCHAR(45),
                user_agent TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_user_id (user_id),
                INDEX idx_action (action),
                INDEX idx_created_at (created_at)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(createTableSql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠️ Warning: Could not create user_activities table: " + e.getMessage());
        }
    }
    
    /**
     * Check if default admin exists
     */
    public boolean defaultAdminExists() throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = 'admin' AND role = 'manager'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println(exists ? "✅ Default admin exists" : "⚠️ Default admin does not exist");
                return exists;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking for default admin: " + e.getMessage());
            throw e;
        }
        return false;
    }
    
    /**
     * Create default admin if not exists
     */
    public void createDefaultAdmin() throws SQLException {
        if (!defaultAdminExists()) {
            System.out.println("🔧 Creating default admin account...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(PasswordUtils.hashPassword("admin123"));
            admin.setRole("manager");
            admin.setStatus("active");
            admin.setFirstLogin(true);
            
            int userId = createUser(admin);
            if (userId > 0) {
                System.out.println("✅ Default admin created with ID: " + userId);
                System.out.println("📋 Default login credentials:");
                System.out.println("   Username: admin");
                System.out.println("   Password: admin123");
                System.out.println("   ⚠️ Please change the password after first login!");
            }
        }
    }
    
    /**
     * Create new user account
     */
    public int createUser(User user) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        // Create users table if it doesn't exist
        createUsersTableIfNotExists();
        
        String sql = "INSERT INTO users (username, password, role, email, company_name, status, first_login, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getCompanyName());
            stmt.setString(6, user.getStatus());
            stmt.setBoolean(7, user.isFirstLogin());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        System.out.println("✅ User created successfully with ID: " + userId);
                        return userId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error creating user: " + e.getMessage());
            throw e;
        }
        return 0;
    }
    
    /**
     * Create users table if it doesn't exist - ESSENTIAL FOR INITIAL SETUP
     */
    private void createUsersTableIfNotExists() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                role ENUM('manager', 'staff') NOT NULL DEFAULT 'staff',
                email VARCHAR(100),
                company_name VARCHAR(255),
                status ENUM('active', 'inactive', 'deleted') NOT NULL DEFAULT 'active',
                first_login BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                last_login TIMESTAMP NULL,
                INDEX idx_username (username),
                INDEX idx_email (email),
                INDEX idx_status (status),
                INDEX idx_role (role)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(createTableSql)) {
            stmt.executeUpdate();
            System.out.println("✅ Users table verified/created");
            
            // Also create password reset tokens table
            createPasswordResetTableIfNotExists();
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating users table: " + e.getMessage());
        }
    }
    
    /**
     * Create password reset tokens table if it doesn't exist
     */
    private void createPasswordResetTableIfNotExists() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS password_reset_tokens (
                token_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                token VARCHAR(255) UNIQUE NOT NULL,
                expires_at TIMESTAMP NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                INDEX idx_token (token),
                INDEX idx_user_id (user_id),
                INDEX idx_expires_at (expires_at)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(createTableSql)) {
            stmt.executeUpdate();
            System.out.println("✅ Password reset tokens table verified/created");
        } catch (SQLException e) {
            System.err.println("❌ Error creating password reset tokens table: " + e.getMessage());
        }
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "SELECT * FROM users WHERE status != 'deleted' ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting all users: " + e.getMessage());
            throw e;
        }
        return users;
    }
    
    /**
     * Update user status (active/inactive)
     */
    public void updateUserStatus(int userId, String status) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "UPDATE users SET status = ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("✅ User status updated to '" + status + "' for user ID: " + userId);
            } else {
                throw new SQLException("Failed to update user status - user not found");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating user status: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Clean up expired password reset tokens
     */
    public void cleanupExpiredTokens() throws SQLException {
        if (connection == null) {
            System.err.println("⚠️ Cannot cleanup tokens - no database connection");
            return;
        }
        
        String sql = "DELETE FROM password_reset_tokens WHERE expires_at < NOW()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                System.out.println("🧹 Cleaned up " + deleted + " expired reset tokens");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Warning: Failed to cleanup expired tokens: " + e.getMessage());
        }
    }
    
    /**
     * Get user count by role
     */
    public int getUserCountByRole(String role) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND status = 'active'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting user count by role: " + e.getMessage());
            throw e;
        }
        return 0;
    }
    
    /**
     * Get user activity history
     */
    public List<UserActivity> getUserActivities(int userId, int limit) throws SQLException {
        List<UserActivity> activities = new ArrayList<>();
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        
        String sql = "SELECT * FROM user_activities WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserActivity activity = new UserActivity();
                    activity.setActivityId(rs.getInt("activity_id"));
                    activity.setUserId(rs.getInt("user_id"));
                    activity.setAction(rs.getString("action"));
                    activity.setDetails(rs.getString("details"));
                    activity.setIpAddress(rs.getString("ip_address"));
                    activity.setUserAgent(rs.getString("user_agent"));
                    activity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting user activities: " + e.getMessage());
            throw e;
        }
        return activities;
    }
    
    /**
     * Map ResultSet to User object - ENHANCED
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setEmail(rs.getString("email"));
        user.setCompanyName(rs.getString("company_name"));
        user.setStatus(rs.getString("status"));
        user.setFirstLogin(rs.getBoolean("first_login"));
        
        // Handle timestamps safely
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        if (connection == null) {
            System.err.println("❌ Database connection is null");
            return false;
        }
        
        try {
            if (connection.isClosed()) {
                System.err.println("❌ Database connection is closed");
                return false;
            }
            
            // Test with a simple query
            String sql = "SELECT 1";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✅ Database connection test successful");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
        }
        return false;
    }
    
    // Inner class for user activities - unchanged
    public static class UserActivity {
        private int activityId;
        private int userId;
        private String action;
        private String details;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime createdAt;
        
        // Getters and setters
        public int getActivityId() { return activityId; }
        public void setActivityId(int activityId) { this.activityId = activityId; }
        
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}


