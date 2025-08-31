package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enhanced User model class with additional fields for comprehensive user management
 */
public class User {
    
    // Core user fields
    private int userId;
    private String username;
    private String password;
    private String email;
    private String role;
    
    // Extended profile fields
    private String fullName;
    private String contactNumber;
    private String companyName;
    
    // Account status and security
    private String status; // active, inactive, deleted
    private boolean firstLogin;
    private Timestamp lastLoginAt;
    private Timestamp passwordChangedAt;
    
    // Audit fields
    private int createdBy;
    private String createdByUsername; // For display purposes
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Default constructor
    public User() {
        this.status = "active";
        this.firstLogin = true;
        this.role = "staff";
    }
    
    // Constructor with basic fields
    public User(String username, String password, String email, String role) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
    
    // Constructor with extended fields
    public User(String username, String password, String email, String role, 
                String fullName, String contactNumber) {
        this(username, password, email, role);
        this.fullName = fullName;
        this.contactNumber = contactNumber;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim().toLowerCase() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : null;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber != null ? contactNumber.trim() : null;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName != null ? companyName.trim() : null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public Timestamp getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Timestamp lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Timestamp getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(Timestamp passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    
    /**
     * Check if user has email configured
     */
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    /**
     * Check if user is active
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return "admin".equals(role);
    }

    /**
     * Check if user is manager
     */
    public boolean isManager() {
        return "manager".equals(role);
    }

    /**
     * Check if user is staff
     */
    public boolean isStaff() {
        return "staff".equals(role);
    }

    /**
     * Get display name (full name or username)
     */
    public String getDisplayName() {
        return fullName != null && !fullName.trim().isEmpty() ? fullName : username;
    }

    /**
     * Get role display name
     */
    public String getRoleDisplayName() {
        if (role == null) return "Unknown";
        
        switch (role.toLowerCase()) {
            case "admin":
                return "Administrator";
            case "manager":
                return "Manager";
            case "staff":
                return "Staff";
            default:
                return role.substring(0, 1).toUpperCase() + role.substring(1);
        }
    }

    /**
     * Get status display name
     */
    public String getStatusDisplayName() {
        if (status == null) return "Unknown";
        
        if (firstLogin && "active".equals(status)) {
            return "Pending Setup";
        }
        
        switch (status.toLowerCase()) {
            case "active":
                return "Active";
            case "inactive":
                return "Inactive";
            case "deleted":
                return "Deleted";
            default:
                return status.substring(0, 1).toUpperCase() + status.substring(1);
        }
    }

    /**
     * Check if password needs to be changed (older than specified days)
     */
    public boolean isPasswordExpired(int maxAgeDays) {
        if (passwordChangedAt == null) {
            return true; // Never changed, consider expired
        }
        
        LocalDateTime passwordDate = passwordChangedAt.toLocalDateTime();
        LocalDateTime expireDate = passwordDate.plusDays(maxAgeDays);
        return LocalDateTime.now().isAfter(expireDate);
    }

    /**
     * Get formatted last login date
     */
    public String getFormattedLastLogin() {
        if (lastLoginAt == null) {
            return "Never";
        }
        
        LocalDateTime loginTime = lastLoginAt.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return loginTime.format(formatter);
    }

    /**
     * Get formatted creation date
     */
    public String getFormattedCreatedAt() {
        if (createdAt == null) {
            return "Unknown";
        }
        
        LocalDateTime creationTime = createdAt.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return creationTime.format(formatter);
    }

    /**
     * Check if user account needs attention (first login, inactive, etc.)
     */
    public boolean needsAttention() {
        return firstLogin || "inactive".equals(status) || 
               (lastLoginAt == null && !firstLogin);
    }

    /**
     * Get user initials for avatar display
     */
    public String getInitials() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length >= 2) {
                return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
            } else {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            }
        }
        
        if (username != null && !username.isEmpty()) {
            return username.substring(0, Math.min(2, username.length())).toUpperCase();
        }
        
        return "??";
    }

    /**
     * Get user permissions based on role
     */
    public UserPermissions getPermissions() {
        switch (role != null ? role.toLowerCase() : "staff") {
            case "admin":
                return new UserPermissions(true, true, true, true, true, true, true);
            case "manager":
                return new UserPermissions(true, true, true, true, false, true, false);
            case "staff":
                return new UserPermissions(true, false, false, false, false, false, false);
            default:
                return new UserPermissions(false, false, false, false, false, false, false);
        }
    }

    /**
     * Validate user data
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        // Username validation
        if (username == null || username.trim().isEmpty()) {
            result.addError("Username is required");
        } else if (username.length() < 3 || username.length() > 30) {
            result.addError("Username must be between 3 and 30 characters");
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            result.addError("Username can only contain letters, numbers, and underscores");
        }
        
        // Email validation (if provided)
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                result.addError("Invalid email format");
            }
        }
        
        // Role validation
        if (role == null || role.trim().isEmpty()) {
            result.addError("Role is required");
        } else if (!role.matches("^(admin|manager|staff)$")) {
            result.addError("Invalid role");
        }
        
        // Status validation
        if (status != null && !status.matches("^(active|inactive|deleted)$")) {
            result.addError("Invalid status");
        }
        
        return result;
    }

    /**
     * Create a copy of this user (for editing purposes)
     */
    public User copy() {
        User copy = new User();
        copy.userId = this.userId;
        copy.username = this.username;
        copy.password = this.password;
        copy.email = this.email;
        copy.role = this.role;
        copy.fullName = this.fullName;
        copy.contactNumber = this.contactNumber;
        copy.companyName = this.companyName;
        copy.status = this.status;
        copy.firstLogin = this.firstLogin;
        copy.lastLoginAt = this.lastLoginAt;
        copy.passwordChangedAt = this.passwordChangedAt;
        copy.createdBy = this.createdBy;
        copy.createdByUsername = this.createdByUsername;
        copy.createdAt = this.createdAt;
        copy.updatedAt = this.updatedAt;
        return copy;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                ", status='" + status + '\'' +
                ", firstLogin=" + firstLogin +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }

    // Inner classes for structured data

    /**
     * User permissions based on role
     */
    public static class UserPermissions {
        private boolean canAccessPOS;
        private boolean canManageInventory;
        private boolean canViewReports;
        private boolean canManageClients;
        private boolean canManageUsers;
        private boolean canViewDashboard;
        private boolean canManageSystem;

        public UserPermissions(boolean canAccessPOS, boolean canManageInventory, 
                             boolean canViewReports, boolean canManageClients, 
                             boolean canManageUsers, boolean canViewDashboard, 
                             boolean canManageSystem) {
            this.canAccessPOS = canAccessPOS;
            this.canManageInventory = canManageInventory;
            this.canViewReports = canViewReports;
            this.canManageClients = canManageClients;
            this.canManageUsers = canManageUsers;
            this.canViewDashboard = canViewDashboard;
            this.canManageSystem = canManageSystem;
        }

        // Getters
        public boolean canAccessPOS() { return canAccessPOS; }
        public boolean canManageInventory() { return canManageInventory; }
        public boolean canViewReports() { return canViewReports; }
        public boolean canManageClients() { return canManageClients; }
        public boolean canManageUsers() { return canManageUsers; }
        public boolean canViewDashboard() { return canViewDashboard; }
        public boolean canManageSystem() { return canManageSystem; }
    }

    /**
     * Simple validation result class
     */
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();

        public void addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append(", ");
            }
            errors.append(error);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrors() {
            return errors.toString();
        }
    }
}