package dao;

import model.User;
import util.ConnectionManager;
import util.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Data Access Object for User operations
 * Handles all database operations related to users
 */
public class UserDAO {
    private Connection connection;
    
    public UserDAO() {
        this.connection = ConnectionManager.getInstance().getConnection();
    }
    
    /**
     * Authenticate user with username and password
     * Uses PasswordUtils for secure password verification
     */
    public User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND status = 'active'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    // Use PasswordUtils for secure password verification
                    if (PasswordUtils.verifyPassword(password, storedPassword)) {
                        return mapResultSetToUser(rs);
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Find user by username
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find user by email address
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Update user's last login timestamp
     */
    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Complete first-time setup for manager
     */
    public void completeFirstTimeSetup(int userId, String hashedPassword, String email, String companyName) throws SQLException {
        String sql = "UPDATE users SET password = ?, email = ?, company_name = ?, first_login = FALSE, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);
            stmt.setString(3, companyName);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Save password reset token
     */
    public void savePasswordResetToken(int userId, String token) throws SQLException {
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
            insertStmt.executeUpdate();
        }
    }
    
    /**
     * Validate password reset token
     */
    public boolean isValidResetToken(String token) throws SQLException {
        String sql = "SELECT COUNT(*) FROM password_reset_tokens WHERE token = ? AND expires_at > NOW()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Reset password using token
     */
    public int resetPassword(String token, String hashedPassword) throws SQLException {
        // Get user ID from valid token
        String getUserSql = "SELECT user_id FROM password_reset_tokens WHERE token = ? AND expires_at > NOW()";
        int userId = 0;
        
        try (PreparedStatement stmt = connection.prepareStatement(getUserSql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                }
            }
        }
        
        if (userId > 0) {
            // Update password
            String updateSql = "UPDATE users SET password = ?, updated_at = NOW() WHERE user_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setString(1, hashedPassword);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
            }
            
            // Delete used token
            String deleteSql = "DELETE FROM password_reset_tokens WHERE token = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, token);
                deleteStmt.executeUpdate();
            }
        }
        
        return userId;
    }
    
    /**
     * Update user password
     */
    public void updatePassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password = ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Create new user account
     */
    public int createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, email, company_name, status, first_login) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return 0;
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    /**
     * Update user status (active/inactive)
     */
    public void updateUserStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE users SET status = ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Delete user (soft delete by setting status to 'deleted')
     */
    public void deleteUser(int userId) throws SQLException {
        String sql = "UPDATE users SET status = 'deleted', updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Log user activity for audit trail
     */
    public void logActivity(int userId, String action, String details) throws SQLException {
        String sql = "INSERT INTO user_activities (user_id, action, details, ip_address, user_agent, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, details);
            stmt.setString(4, getCurrentIP()); // You'll need to implement this
            stmt.setString(5, getCurrentUserAgent()); // You'll need to implement this
            stmt.executeUpdate();
        }
    }
    
    /**
     * Get user activity history
     */
    public List<UserActivity> getUserActivities(int userId, int limit) throws SQLException {
        List<UserActivity> activities = new ArrayList<>();
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
        }
        return activities;
    }
    
    /**
     * Clean up expired password reset tokens
     */
    public void cleanupExpiredTokens() throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE expires_at < NOW()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
    
    /**
     * Get user count by role
     */
    public int getUserCountByRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND status = 'active'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Check if default admin exists
     */
    public boolean defaultAdminExists() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = 'admin' AND role = 'manager'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    /**
     * Create default admin if not exists
     */
    public void createDefaultAdmin() throws SQLException {
        if (!defaultAdminExists()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(PasswordUtils.hashPassword("admin123"));
            admin.setRole("manager");
            admin.setStatus("active");
            admin.setFirstLogin(true);
            createUser(admin);
        }
    }
    
    /**
     * Map ResultSet to User object
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
        
        // Handle timestamps
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
    
    // Helper methods for request context (implement based on your servlet context)
    private String getCurrentIP() {
        // This should be set from servlet request
        return "127.0.0.1"; // Default for now
    }
    
    private String getCurrentUserAgent() {
        // This should be set from servlet request
        return "Unknown"; // Default for now
    }
    
    // Inner class for user activities
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