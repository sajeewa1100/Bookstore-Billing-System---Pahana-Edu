package dao;

import model.User;
import util.ConnectionManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Create default admin user if not exists - Updated for SHA-256
     */
    public void createDefaultAdmin() throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        String insertSql = """
            INSERT INTO users (username, password, email, role, full_name, status, first_login, created_at, created_by) 
            VALUES (?, ?, NULL, 'admin', 'System Administrator', 'active', true, NOW(), 0)
        """;

        try (Connection conn = ConnectionManager.getConnection()) {
            // Check if admin exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    System.out.println("Admin user already exists - checking password format...");
                    
                    // Check if existing admin has BCrypt format and update if needed
                    updateExistingAdminToSHA256();
                    return;
                }
            }

            // Create default admin with SHA-256 hash
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, "admin");
                
                // Generate SHA-256 hash for admin123
                String hashedPassword = util.PasswordUtils.hashPassword("admin123");
                insertStmt.setString(2, hashedPassword);
                
                System.out.println("=== ADMIN CREATION DEBUG ===");
                System.out.println("Creating admin with password: admin123");
                System.out.println("Generated hash: " + hashedPassword);
                
                int result = insertStmt.executeUpdate();
                if (result > 0) {
                    System.out.println("✅ Default admin user created successfully");
                    System.out.println("   Username: admin");
                    System.out.println("   Password: admin123");
                    System.out.println("   Hash format: SHA-256 with salt");
                    
                    // Test the hash immediately
                    boolean verification = util.PasswordUtils.verifyPassword("admin123", hashedPassword);
                    System.out.println("   Hash verification test: " + verification);
                } else {
                    System.out.println("❌ Failed to create default admin user");
                }
            }
        }
    }

    /**
     * Update existing admin user from BCrypt to SHA-256 format
     */
    public void updateExistingAdminToSHA256() throws SQLException {
        String selectSql = "SELECT password FROM users WHERE username = 'admin'";
        String updateSql = "UPDATE users SET password = ?, updated_at = NOW() WHERE username = 'admin'";
        
        try (Connection conn = ConnectionManager.getConnection()) {
            // First, check the current password format
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    String currentHash = rs.getString("password");
                    
                    System.out.println("=== ADMIN PASSWORD FORMAT CHECK ===");
                    System.out.println("Current hash: " + currentHash);
                    
                    // Check if it's BCrypt format (starts with $2a$, $2b$, or $2y$)
                    if (currentHash != null && (currentHash.startsWith("$2a$") || 
                                              currentHash.startsWith("$2b$") || 
                                              currentHash.startsWith("$2y$"))) {
                        
                        System.out.println("Detected BCrypt format - updating to SHA-256...");
                        
                        // Generate new SHA-256 hash for admin123
                        String newHashedPassword = util.PasswordUtils.hashPassword("admin123");
                        
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, newHashedPassword);
                            
                            int result = updateStmt.executeUpdate();
                            if (result > 0) {
                                System.out.println("✅ Admin password updated to SHA-256 successfully");
                                System.out.println("   Old format: BCrypt");
                                System.out.println("   New format: SHA-256 with salt");
                                System.out.println("   New hash: " + newHashedPassword);
                                
                                // Test the new hash immediately
                                boolean verification = util.PasswordUtils.verifyPassword("admin123", newHashedPassword);
                                System.out.println("   Hash verification test: " + verification);
                            } else {
                                System.out.println("❌ Failed to update admin password");
                            }
                        }
                    } else if (currentHash != null && currentHash.contains(":")) {
                        System.out.println("✅ Admin already uses SHA-256 format - no update needed");
                        
                        // Still test the current hash
                        boolean verification = util.PasswordUtils.verifyPassword("admin123", currentHash);
                        System.out.println("   Current hash verification test: " + verification);
                    } else {
                        System.out.println("⚠️  Unknown password format detected");
                        System.out.println("   Hash: " + currentHash);
                        System.out.println("   You may need to manually reset the admin password");
                    }
                }
            }
        }
    }

    /**
     * Migrate all users with BCrypt passwords to SHA-256 format
     * Note: This will only work for users with known default passwords
     */
    public void migrateAllBCryptToSHA256() throws SQLException {
        String selectSql = "SELECT user_id, username, password FROM users WHERE password LIKE '$2%' AND status != 'deleted'";
        String updateSql = "UPDATE users SET password = ?, updated_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.println("=== BCRYPT TO SHA-256 MIGRATION ===");
            
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                ResultSet rs = selectStmt.executeQuery();
                
                int migrated = 0;
                int skipped = 0;
                
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String oldHash = rs.getString("password");
                    
                    System.out.println("Found BCrypt hash for user: " + username);
                    
                    // For known default passwords, we can migrate directly
                    String knownPassword = getKnownDefaultPassword(username);
                    if (knownPassword != null) {
                        // Generate new SHA-256 hash
                        String newHash = util.PasswordUtils.hashPassword(knownPassword);
                        
                        // Update the database
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, newHash);
                            updateStmt.setInt(2, userId);
                            
                            int result = updateStmt.executeUpdate();
                            if (result > 0) {
                                System.out.println("✅ Migrated password for user: " + username);
                                migrated++;
                            }
                        }
                    } else {
                        System.out.println("⚠️  Unknown password for user: " + username + " - skipping (will need password reset)");
                        skipped++;
                    }
                }
                
                System.out.println("=== MIGRATION SUMMARY ===");
                System.out.println("Users migrated: " + migrated);
                System.out.println("Users skipped: " + skipped);
                if (skipped > 0) {
                    System.out.println("Note: Skipped users will need to reset their passwords");
                }
            }
        }
    }

    /**
     * Return known default passwords for system users
     * In production, you might want to force password resets instead
     */
    private String getKnownDefaultPassword(String username) {
        switch (username.toLowerCase()) {
            case "admin":
                return "admin123";
            case "manager":
                return "manager123";
            case "staff":
                return "staff123";
            // Add other known default passwords here if any
            default:
                return null;
        }
    }

    /**
     * Force password reset for users with BCrypt passwords that can't be migrated
     */
    public void forcePasswordResetForBCryptUsers() throws SQLException {
        String selectSql = "SELECT user_id, username FROM users WHERE password LIKE '$2%' AND status != 'deleted'";
        String updateSql = "UPDATE users SET password = NULL, first_login = true, updated_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection()) {
            System.out.println("=== FORCING PASSWORD RESET FOR BCRYPT USERS ===");
            
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                ResultSet rs = selectStmt.executeQuery();
                
                int resetCount = 0;
                
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    
                    // Skip users with known passwords (they should be migrated instead)
                    if (getKnownDefaultPassword(username) != null) {
                        continue;
                    }
                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, userId);
                        int result = updateStmt.executeUpdate();
                        
                        if (result > 0) {
                            System.out.println("🔄 Set password reset required for user: " + username);
                            resetCount++;
                        }
                    }
                }
                
                System.out.println("Password reset forced for " + resetCount + " users");
            }
        }
    }

    /**
     * Create new user (by admin)
     */
    public int createUser(User user) throws SQLException {
        String sql = """
            INSERT INTO users (username, password, email, role, full_name, contact_number, 
                             status, first_login, created_at, created_by) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)
        """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            // Ensure password is hashed with SHA-256
            String hashedPassword = (user.getPassword() != null && !user.getPassword().isEmpty()) 
                ? util.PasswordUtils.hashPassword(user.getPassword()) 
                : null;
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getContactNumber());
            stmt.setString(7, user.getStatus());
            stmt.setBoolean(8, user.isFirstLogin());
            stmt.setInt(9, user.getCreatedBy());

            int result = stmt.executeUpdate();
            if (result > 0) {
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
     * Update user by admin
     */
    public void updateUserByAdmin(int userId, String email, String role, String fullName, 
                                String contactNumber, String status) throws SQLException {
        String sql = """
            UPDATE users 
            SET email = ?, role = ?, full_name = ?, contact_number = ?, status = ?, 
                updated_at = NOW() 
            WHERE user_id = ?
        """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, role);
            stmt.setString(3, fullName);
            stmt.setString(4, contactNumber);
            stmt.setString(5, status);
            stmt.setInt(6, userId);

            stmt.executeUpdate();
        }
    }

    /**
     * Reset password by admin (now uses SHA-256)
     */
    public void resetPasswordByAdmin(int userId, String plainTextPassword) throws SQLException {
        String sql = """
            UPDATE users 
            SET password = ?, first_login = true, password_changed_at = NOW(), updated_at = NOW() 
            WHERE user_id = ?
        """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Hash the password using SHA-256
            String hashedPassword = util.PasswordUtils.hashPassword(plainTextPassword);
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
            System.out.println("Password reset for user ID: " + userId + " using SHA-256 hash");
        }
    }

    /**
     * Get all users for admin management
     */
    

    /**
     * Find user by username
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND status != 'deleted'";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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
     * Find user by email
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND status != 'deleted'";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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
     * Find user by ID
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ? AND status != 'deleted'";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Update last login time
     */
    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_login_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Update password (now uses SHA-256)
     */
    public void updatePassword(int userId, String plainTextPassword) throws SQLException {
        String sql = "UPDATE users SET password = ?, password_changed_at = NOW(), updated_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Hash the password using SHA-256
            String hashedPassword = util.PasswordUtils.hashPassword(plainTextPassword);
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Clear first login flag
     */
    public void clearFirstLoginFlag(int userId) throws SQLException {
        String sql = "UPDATE users SET first_login = false, updated_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Update user email
     */
    public void updateUserEmail(int userId, String email) throws SQLException {
        String sql = "UPDATE users SET email = ?, updated_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Update user company
     */
    public void updateUserCompany(int userId, String companyName) throws SQLException {
        String sql = "UPDATE users SET company_name = ?, updated_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, companyName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Log user activity
     */
    public void logActivity(int userId, String action, String description) throws SQLException {
        String sql = "INSERT INTO user_activity_log (user_id, action, description, created_at) VALUES (?, ?, ?, NOW())";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, description);
            stmt.executeUpdate();
        }
    }

    /**
     * Get user activity log for admin
     */
    public List<UserActivity> getUserActivity(int userId, int limit) throws SQLException {
        String sql = """
            SELECT ual.*, u.username 
            FROM user_activity_log ual
            JOIN users u ON ual.user_id = u.user_id
            WHERE ual.user_id = ? 
            ORDER BY ual.created_at DESC 
            LIMIT ?
        """;
        
        List<UserActivity> activities = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserActivity activity = new UserActivity();
                    activity.setId(rs.getInt("id"));
                    activity.setUserId(rs.getInt("user_id"));
                    activity.setUsername(rs.getString("username"));
                    activity.setAction(rs.getString("action"));
                    activity.setDescription(rs.getString("description"));
                    activity.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    activities.add(activity);
                }
            }
        }
        
        return activities;
    }

    /**
     * Get all user activities for admin dashboard
     */
    public List<UserActivity> getAllUserActivities(int limit) throws SQLException {
        String sql = """
            SELECT ual.*, u.username 
            FROM user_activity_log ual
            JOIN users u ON ual.user_id = u.user_id
            ORDER BY ual.created_at DESC 
            LIMIT ?
        """;
        
        List<UserActivity> activities = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserActivity activity = new UserActivity();
                    activity.setId(rs.getInt("id"));
                    activity.setUserId(rs.getInt("user_id"));
                    activity.setUsername(rs.getString("username"));
                    activity.setAction(rs.getString("action"));
                    activity.setDescription(rs.getString("description"));
                    activity.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    activities.add(activity);
                }
            }
        }
        
        return activities;
    }

    /**
     * Get user statistics for admin dashboard
     */
    public UserStatistics getUserStatistics() throws SQLException {
        String sql = """
            SELECT 
                COUNT(*) as total_users,
                SUM(CASE WHEN status = 'active' THEN 1 ELSE 0 END) as active_users,
                SUM(CASE WHEN status = 'inactive' THEN 1 ELSE 0 END) as inactive_users,
                SUM(CASE WHEN role = 'admin' THEN 1 ELSE 0 END) as admin_count,
                SUM(CASE WHEN role = 'manager' THEN 1 ELSE 0 END) as manager_count,
                SUM(CASE WHEN role = 'staff' THEN 1 ELSE 0 END) as staff_count,
                SUM(CASE WHEN first_login = true THEN 1 ELSE 0 END) as pending_setup,
                SUM(CASE WHEN last_login_at > DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 ELSE 0 END) as active_last_week
            FROM users 
            WHERE status != 'deleted'
        """;
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserStatistics stats = new UserStatistics();
                    stats.setTotalUsers(rs.getInt("total_users"));
                    stats.setActiveUsers(rs.getInt("active_users"));
                    stats.setInactiveUsers(rs.getInt("inactive_users"));
                    stats.setAdminCount(rs.getInt("admin_count"));
                    stats.setManagerCount(rs.getInt("manager_count"));
                    stats.setStaffCount(rs.getInt("staff_count"));
                    stats.setPendingSetup(rs.getInt("pending_setup"));
                    stats.setActiveLastWeek(rs.getInt("active_last_week"));
                    return stats;
                }
            }
        }
        
        return new UserStatistics();
    }

    /**
     * Soft delete user
     * @return 
     */
   
    /**
     * Complete first-time setup in a single atomic transaction
     */
    public void completeFirstTimeSetupAtomic(int userId, String hashedPassword, String email, String companyName) throws SQLException {
        String sql = """
            UPDATE users 
            SET password = ?, email = ?, company_name = ?, first_login = false, 
                password_changed_at = NOW(), updated_at = NOW() 
            WHERE user_id = ?
        """;

        System.out.println("=== USERDAO ATOMIC SETUP DEBUG ===");
        System.out.println("Updating user ID: " + userId);
        System.out.println("Email: " + (email != null ? email : "null"));
        System.out.println("Company: " + (companyName != null ? companyName : "null"));
        System.out.println("Password hash length: " + (hashedPassword != null ? hashedPassword.length() : 0));

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hashedPassword); // Already hashed - don't hash again
            stmt.setString(2, email);
            stmt.setString(3, companyName);
            stmt.setInt(4, userId);
            
            System.out.println("Executing SQL update...");
            int rows = stmt.executeUpdate();
            System.out.println("First-time setup update affected " + rows + " rows");
            
            if (rows == 0) {
                throw new SQLException("No user updated - user may not exist or already completed setup");
            }
            
            System.out.println("Atomic setup update completed successfully");
            
            // Verify the update worked
            String verifySQL = "SELECT first_login, email, company_name FROM users WHERE user_id = ?";
            try (PreparedStatement verifyStmt = conn.prepareStatement(verifySQL)) {
                verifyStmt.setInt(1, userId);
                try (ResultSet rs = verifyStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Verification - first_login: " + rs.getBoolean("first_login"));
                        System.out.println("Verification - email: " + rs.getString("email"));
                        System.out.println("Verification - company: " + rs.getString("company_name"));
                    }
                }
            }
        }
    }
    
    /**
     * Update user status
     */
    public boolean updateUserStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE users SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Updated user status for ID " + userId + " to " + status);
                return true;
            }
            
            return false;
        }
    }

    /**
     * Delete user account
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Deleted user with ID: " + userId);
                return true;
            }
            
            return false;
        }
    }

    /**
     * Get all users for admin management
     */
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY role, username";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
        }
        
        return users;
    }
    

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setFullName(rs.getString("full_name"));
        user.setContactNumber(rs.getString("contact_number"));
        user.setCompanyName(rs.getString("company_name"));
        user.setStatus(rs.getString("status"));
        user.setFirstLogin(rs.getBoolean("first_login"));
        user.setLastLoginAt(rs.getTimestamp("last_login_at"));
        user.setPasswordChangedAt(rs.getTimestamp("password_changed_at"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        user.setCreatedBy(rs.getInt("created_by"));
        return user;
    }

    // Inner classes for activity logging and statistics
    public static class UserActivity {
        private int id;
        private int userId;
        private String username;
        private String action;
        private String description;
        private Timestamp createdAt;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }

    public static class UserStatistics {
        private int totalUsers;
        private int activeUsers;
        private int inactiveUsers;
        private int adminCount;
        private int managerCount;
        private int staffCount;
        private int pendingSetup;
        private int activeLastWeek;

        // Getters and setters
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        public int getActiveUsers() { return activeUsers; }
        public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
        public int getInactiveUsers() { return inactiveUsers; }
        public void setInactiveUsers(int inactiveUsers) { this.inactiveUsers = inactiveUsers; }
        public int getAdminCount() { return adminCount; }
        public void setAdminCount(int adminCount) { this.adminCount = adminCount; }
        public int getManagerCount() { return managerCount; }
        public void setManagerCount(int managerCount) { this.managerCount = managerCount; }
        public int getStaffCount() { return staffCount; }
        public void setStaffCount(int staffCount) { this.staffCount = staffCount; }
        public int getPendingSetup() { return pendingSetup; }
        public void setPendingSetup(int pendingSetup) { this.pendingSetup = pendingSetup; }
        public int getActiveLastWeek() { return activeLastWeek; }
        public void setActiveLastWeek(int activeLastWeek) { this.activeLastWeek = activeLastWeek; }
    }
}