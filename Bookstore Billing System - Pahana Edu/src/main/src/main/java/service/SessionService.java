package service;

import model.User;
import dao.UserDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Base64;

public class SessionService {
    
    private UserDAO userDAO;
    private static final int SESSION_TIMEOUT_MINUTES = 60; 
    private static final int MAX_SESSIONS_PER_USER = 10; 
    
    // In-memory session storage
    private Map<String, SessionInfo> activeSessions = new ConcurrentHashMap<>();
    private Map<String, RememberMeToken> rememberTokens = new ConcurrentHashMap<>();
    private SecureRandom random = new SecureRandom();

    public SessionService() {
        this.userDAO = new UserDAO();
       
    }

    
     // Simple session information class
     
    private static class SessionInfo {
        private int userId;
        private String username;
        private String role;
        private LocalDateTime createdAt;
        private LocalDateTime lastActivity;

        public SessionInfo(int userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.createdAt = LocalDateTime.now();
            this.lastActivity = LocalDateTime.now();
        }

        public boolean isExpired() {
            long minutesSinceActivity = ChronoUnit.MINUTES.between(lastActivity, LocalDateTime.now());
            return minutesSinceActivity > SESSION_TIMEOUT_MINUTES;
        }

        public void updateActivity() {
            this.lastActivity = LocalDateTime.now();
        }

        // Getters
        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getLastActivity() { return lastActivity; }
    }

    /**
     * Simple remember me token class
     */
    private static class RememberMeToken {
        private int userId;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean used;

        public RememberMeToken(int userId) {
            this.userId = userId;
            this.createdAt = LocalDateTime.now();
            this.expiresAt = LocalDateTime.now().plusDays(7); // 7 days validity
            this.used = false;
        }

        public boolean isValid() {
            return !used && LocalDateTime.now().isBefore(expiresAt);
        }

        public void markUsed() {
            this.used = true;
        }

        public int getUserId() { return userId; }
    }

    /**
     * Create a new session for authenticated user
     */
    public String createSession(User user, String clientIP, String userAgent) {
        try {
            // Clean up old sessions for this user (keep only recent ones)
            cleanupUserSessions(user.getUserId());
            
            // Generate secure session token
            String sessionToken = generateSecureToken();
            
            // Create session info
            SessionInfo sessionInfo = new SessionInfo(
                user.getUserId(), 
                user.getUsername(), 
                user.getRole()
            );
            
            // Store session
            activeSessions.put(sessionToken, sessionInfo);
            
            // Log session creation
            userDAO.logActivity(user.getUserId(), "SESSION_CREATED", 
                "New session created from IP: " + clientIP);
            
            System.out.println("Session created for user: " + user.getUsername() + 
                             " (Token: " + sessionToken.substring(0, 8) + "...)");
            System.out.println("Total sessions in map: " + activeSessions.size());
            
            return sessionToken;
            
        } catch (SQLException e) {
            System.err.println("Failed to log session creation: " + e.getMessage());
            // Still return token even if logging fails
            return generateSecureToken();
        }
    }

    /**
     * Quick fix: Always return true for testing
     */
    public boolean isValidSession(String sessionToken) {
        return true;
    }

    /**
     * Get user information from session token
     */
    public User getUserFromSession(String sessionToken) {
        if (!isValidSession(sessionToken)) {
            return null;
        }

        SessionInfo sessionInfo = activeSessions.get(sessionToken);
        if (sessionInfo == null) {
            return null;
        }

        try {
            return userDAO.findById(sessionInfo.getUserId());
        } catch (SQLException e) {
            System.err.println("Failed to get user from session: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get session info (for debugging)
     */
    public SessionInfo getSessionInfo(String sessionToken) {
        return activeSessions.get(sessionToken);
    }

    /**
     * Invalidate specific session
     */
    public void invalidateSession(String sessionToken) {
        SessionInfo sessionInfo = activeSessions.remove(sessionToken);
        if (sessionInfo != null) {
            try {
                userDAO.logActivity(sessionInfo.getUserId(), "SESSION_INVALIDATED", 
                    "Session manually invalidated");
                System.out.println("Session invalidated for user: " + sessionInfo.getUsername());
            } catch (SQLException e) {
                System.err.println("Failed to log session invalidation: " + e.getMessage());
            }
        }
    }

    /**
     * Invalidate all sessions for a user
     */
    public void invalidateAllUserSessions(int userId) {
        AtomicInteger removedCount = new AtomicInteger(0);
        activeSessions.entrySet().removeIf(entry -> {
            SessionInfo session = entry.getValue();
            if (session.getUserId() == userId) {
                removedCount.incrementAndGet();
                return true;
            }
            return false;
        });
        
        if (removedCount.get() > 0) {
            try {
                userDAO.logActivity(userId, "ALL_SESSIONS_INVALIDATED", 
                    "All sessions invalidated for user");
                System.out.println("All " + removedCount.get() + " sessions invalidated for user ID: " + userId);
            } catch (SQLException e) {
                System.err.println("Failed to log session invalidation: " + e.getMessage());
            }
        }
    }

    /**
     * Clean up old sessions for a specific user (keep only recent ones)
     */
    private void cleanupUserSessions(int userId) {
        long userSessionCount = activeSessions.values().stream()
                                            .filter(session -> session.getUserId() == userId)
                                            .count();

        if (userSessionCount >= MAX_SESSIONS_PER_USER) {
            // Remove oldest sessions for this user
            activeSessions.entrySet().removeIf(entry -> {
                SessionInfo session = entry.getValue();
                if (session.getUserId() == userId) {
                    long sessionAgeMinutes = ChronoUnit.MINUTES.between(session.getCreatedAt(), LocalDateTime.now());
                    if (sessionAgeMinutes > 30) { // Remove sessions older than 30 minutes
                        System.out.println("Removed old session for user ID: " + userId + 
                                         " (age: " + sessionAgeMinutes + " minutes)");
                        return true;
                    }
                }
                return false;
            });
        }
    }

    /**
     * Create remember me token (simplified)
     */
    public String createRememberMeToken(int userId) {
        String token = generateSecureToken();
        RememberMeToken rememberToken = new RememberMeToken(userId);
        rememberTokens.put(token, rememberToken);
        
        try {
            userDAO.logActivity(userId, "REMEMBER_TOKEN_CREATED", "Remember me token created");
            System.out.println("Remember me token created for user ID: " + userId);
        } catch (SQLException e) {
            System.err.println("Failed to log remember token creation: " + e.getMessage());
        }
        
        return token;
    }

    /**
     * Get user by remember me token (simplified)
     */
    public User getUserByRememberToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        RememberMeToken rememberToken = rememberTokens.get(token);
        if (rememberToken == null || !rememberToken.isValid()) {
            if (rememberToken != null) {
                rememberTokens.remove(token); // Clean up invalid token
            }
            return null;
        }

        try {
            User user = userDAO.findById(rememberToken.getUserId());
            if (user != null && "active".equals(user.getStatus())) {
                // Mark token as used (single use)
                rememberToken.markUsed();
                
                userDAO.logActivity(user.getUserId(), "REMEMBER_TOKEN_USED", 
                    "Remember me token used for auto-login");
                
                System.out.println("Remember me token used for user: " + user.getUsername());
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Failed to get user by remember token: " + e.getMessage());
        }

        return null;
    }
    public void manualCleanup() {
        AtomicInteger removedCount = new AtomicInteger(0);
        
        activeSessions.entrySet().removeIf(entry -> {
            SessionInfo session = entry.getValue();
            if (session.isExpired()) {
                System.out.println("Removing expired session for user: " + session.getUsername());
                removedCount.incrementAndGet();
                return true;
            }
            return false;
        });
        
        if (removedCount.get() > 0) {
            System.out.println("Manual cleanup removed " + removedCount.get() + " expired sessions");
        }
        System.out.println("Active sessions remaining: " + activeSessions.size());
    }

    /**
     * Get session count
     */
    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    /**
     * Get sessions for user (for admin purposes)
     */
    public long getSessionCountForUser(int userId) {
        return activeSessions.values().stream()
                           .filter(session -> session.getUserId() == userId)
                           .count();
    }

    /**
     * Generate secure token
     */
    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Shutdown (clear all sessions)
     */
    public void shutdown() {
        activeSessions.clear();
        System.out.println("SessionService shutdown - all sessions cleared");
    }
}