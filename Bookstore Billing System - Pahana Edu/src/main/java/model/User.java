package model;

import java.time.LocalDateTime;

/**
 * User model class representing system users
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String email;
    private String companyName;
    private String status;
    private boolean firstLogin;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public User() {
        this.status = "active";
        this.firstLogin = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with essential fields
    public User(String username, String password, String role) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
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
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public boolean isManager() {
        return "manager".equals(this.role);
    }
    
    public boolean isActive() {
        return "active".equals(this.status);
    }
    
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }
    
    public String getDisplayName() {
        if (companyName != null && !companyName.trim().isEmpty()) {
            return username + " (" + companyName + ")";
        }
        return username;
    }
    
    // Override toString for debugging
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", status='" + status + '\'' +
                ", firstLogin=" + firstLogin +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}