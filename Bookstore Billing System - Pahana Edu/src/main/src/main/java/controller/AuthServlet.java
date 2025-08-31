package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import model.User;
import dao.UserDAO;
import service.AuthService;
import service.AuthService.PasswordResetResult;
import service.SessionService;
import util.ValidationUtil;
import util.AuthorizationUtil;

@WebServlet("/AuthServlet")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private AuthService authService;
    private SessionService sessionService;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        authService = new AuthService();
        sessionService = new SessionService();
        
        // Create default admin if not exists
        try {
            userDAO.createDefaultAdmin();
        } catch (SQLException e) {
            System.err.println("Failed to create default admin: " + e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("views/login.jsp");
            return;
        }

        // Admin access check for user management
        if ("manageUsers".equals(action) && !AuthorizationUtil.hasAdminAccess(request)) {
            response.sendRedirect("views/login.jsp?error=Admin access required");
            return;
        }

        switch (action) {
            case "logout":
                handleLogout(request, response);
                break;
            case "resetPassword":
                handlePasswordResetPage(request, response);
                break;
            case "firstTimeSetup":
                handleFirstTimeSetupPage(request, response);
                break;
            case "manageUsers":
                handleManageUsersPage(request, response);
                break;
            case "profile":
                handleProfilePage(request, response);
                break;
            default:
                response.sendRedirect("views/login.jsp");
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("views/login.jsp");
            return;
        }

        switch (action) {
            case "login":
                handleLogin(request, response);
                break;
            case "forgotPassword":
                handleForgotPassword(request, response);
                break;
            case "resetPassword":
                handlePasswordReset(request, response);
                break;
            case "firstTimeSetup":
                handleFirstTimeSetup(request, response);
                break;
            case "changePassword":
                handleChangePassword(request, response);
                break;
            case "createUser":
                handleCreateUser(request, response);
                break;
            case "updateUser":
                handleUpdateUser(request, response);
                break;
            case "resetUserPassword":
                handleResetUserPassword(request, response);
                break;
            default:
                response.sendRedirect("views/login.jsp");
                break;
        }
    }

    
    //LOGIN METHOD WITH ROLE-BASED REDIRECTS
     
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        System.out.println("=== LOGIN ATTEMPT START ===");
        System.out.println("Username: " + username);
        System.out.println("Client IP: " + getClientIP(request));
        
        // Basic validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.out.println("LOGIN FAILED: Missing credentials");
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("views/login.jsp").forward(request, response);
            return;
        }

        String clientIP = getClientIP(request);
        
        // Authenticate user
        AuthService.AuthResult authResult = authService.authenticateUser(username.trim(), password, clientIP);
        
        System.out.println("AuthService result: " + authResult.isSuccess());
        System.out.println("AuthService message: " + authResult.getMessage());
        
        if (authResult.isSuccess()) {
            User user = authResult.getUser();
            System.out.println("Login successful for user: " + user.getUsername() + ", role: " + user.getRole());
            
            // CRITICAL: Invalidate any existing session first
            HttpSession existingSession = request.getSession(false);
            if (existingSession != null) {
                System.out.println("Invalidating existing session: " + existingSession.getId());
                existingSession.invalidate();
            }
            
            // Create NEW session
            HttpSession session = request.getSession(true);
            System.out.println("Created new session: " + session.getId());
            
            // Create session token
            String sessionToken = sessionService.createSession(user, clientIP, request.getHeader("User-Agent"));
            
            // Set session attributes
            session.setMaxInactiveInterval(1800); // 30 minutes
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("sessionToken", sessionToken);
            session.setAttribute("lastActivity", System.currentTimeMillis());
            session.setAttribute("isAuthenticated", Boolean.TRUE);
            session.setAttribute("loginTime", System.currentTimeMillis());
            session.setAttribute("userAgent", request.getHeader("User-Agent"));
            session.setAttribute("clientIP", clientIP);

            System.out.println("Session attributes set for role: " + user.getRole());

            // Handle remember me
            if ("true".equals(rememberMe)) {
                setupRememberMeCookies(response, user);
            }

            // ROLE-BASED REDIRECT LOGIC
            String redirectUrl;
            if (user.isFirstLogin()) {
                redirectUrl = request.getContextPath() + "/views/firstTimeSetup.jsp";
                System.out.println("First login - redirecting to setup");
            } else {
                // Determine redirect based on role
                switch (user.getRole()) {
                    case "admin":
                    case "manager":
                        redirectUrl = request.getContextPath() + "/ManagerServlet?action=dashboard";
                        System.out.println("Manager/Admin redirecting to manager dashboard");
                        break;
                    case "staff":
                        redirectUrl = request.getContextPath() + "/billing";
                        System.out.println("Staff redirecting to billing dashboard");
                        break;
                    default:
                        redirectUrl = request.getContextPath() + "/views/login.jsp?error=Unknown user role";
                        System.out.println("Unknown role, redirecting to login");
                        break;
                }
            }

            System.out.println("Final redirect URL: " + redirectUrl);
            System.out.println("=== LOGIN SUCCESS END ===");
            
            response.sendRedirect(redirectUrl);
            
        } else {
            // Handle authentication failure
            System.out.println("LOGIN FAILED: " + authResult.getMessage());
            
            if (authResult.isAccountLocked()) {
                request.setAttribute("error", authResult.getMessage());
                request.setAttribute("lockoutTime", authResult.getRemainingLockoutMinutes());
            } else {
                request.setAttribute("error", authResult.getMessage());
            }
            request.setAttribute("username", username);
            request.getRequestDispatcher("views/login.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== LOGOUT DEBUG ===");
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            System.out.println("Logging out session: " + session.getId());
            
            // Log the logout activity
            User user = (User) session.getAttribute("user");
            String sessionToken = (String) session.getAttribute("sessionToken");
            
            if (user != null) {
                System.out.println("Logging activity for user: " + user.getUsername());
                try {
                    userDAO.logActivity(user.getUserId(), "LOGOUT", "User logged out from IP: " + 
                                      getClientIP(request));
                } catch (SQLException e) {
                    System.err.println("Failed to log logout: " + e.getMessage());
                }
            }
            
            // Invalidate session in SessionService
            if (sessionToken != null) {
                sessionService.invalidateSession(sessionToken);
            }
            
            session.invalidate();
        }

        // Clear all auth-related cookies
        clearAuthCookies(response);
        
        System.out.println("Logout completed - redirecting to login");
        response.sendRedirect("views/login.jsp?message=You have been logged out successfully");
    }

    
     // SESSION VALIDATION FOR PROTECTED PAGES
     
    private boolean validateSession(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            response.sendRedirect("views/login.jsp?error=Session expired. Please login again.");
            return false;
        }
        
        // Check required session attributes
        User user = (User) session.getAttribute("user");
        String sessionToken = (String) session.getAttribute("sessionToken");
        
        if (user == null) {
            response.sendRedirect("views/login.jsp?error=Session expired. Please login again.");
            return false;
        }
        
        // For first-time users, be more lenient with session token validation
        if (user.isFirstLogin()) {
            session.setAttribute("lastActivity", System.currentTimeMillis());
            return true;
        }
        
        // For regular users, validate session token
        if (sessionToken == null || !sessionService.isValidSession(sessionToken)) {
            session.invalidate();
            response.sendRedirect("views/login.jsp?error=Session expired. Please login again.");
            return false;
        }
        
        // Update last activity
        session.setAttribute("lastActivity", System.currentTimeMillis());
        return true;
    }

    private void handleManageUsersPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!validateSession(request, response)) {
            return;
        }
        
        if (!AuthorizationUtil.hasAdminAccess(request)) {
            response.sendRedirect("views/login.jsp?error=Admin access required");
            return;
        }

        User admin = AuthorizationUtil.getCurrentUser(request);
        List<User> users = authService.getAllUsers(admin);
        request.setAttribute("users", users);
        
        // Calculate statistics
        int totalUsers = users.size();
        int activeUsers = (int) users.stream().filter(u -> "active".equals(u.getStatus())).count();
        int pendingUsers = (int) users.stream().filter(u -> u.isFirstLogin()).count();
        int adminCount = (int) users.stream().filter(u -> "admin".equals(u.getRole())).count();
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("activeUsers", activeUsers);
        request.setAttribute("pendingUsers", pendingUsers);
        request.setAttribute("adminCount", adminCount);

        request.getRequestDispatcher("views/manager-dashboard.jsp").forward(request, response);
    }

    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!validateSession(request, response)) {
            return;
        }
        
        if (!AuthorizationUtil.hasAdminAccess(request)) {
            response.sendRedirect("views/login.jsp?error=Admin access required");
            return;
        }

        User adminUser = AuthorizationUtil.getCurrentUser(request);
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        String fullName = request.getParameter("fullName");
        String contactNumber = request.getParameter("contactNumber");

        AuthService.UserCreationResult result = authService.createUser(
            adminUser, username, email, role, fullName, contactNumber);

        if (result.isSuccess()) {
            String successMessage = result.getMessage();
            if (result.getTemporaryPassword() != null) {
                successMessage += " Temporary password: " + result.getTemporaryPassword();
            }
            response.sendRedirect("AuthServlet?action=manageUsers&success=" + 
                                java.net.URLEncoder.encode(successMessage, "UTF-8"));
        } else {
            response.sendRedirect("AuthServlet?action=manageUsers&error=" + 
                                java.net.URLEncoder.encode(result.getMessage(), "UTF-8"));
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!validateSession(request, response)) {
            return;
        }
        
        if (!AuthorizationUtil.hasAdminAccess(request)) {
            response.sendRedirect("views/login.jsp?error=Admin access required");
            return;
        }

        User adminUser = AuthorizationUtil.getCurrentUser(request);

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String email = request.getParameter("email");
            String role = request.getParameter("role");
            String fullName = request.getParameter("fullName");
            String contactNumber = request.getParameter("contactNumber");
            String status = request.getParameter("status");

          
          

        } catch (NumberFormatException e) {
            response.sendRedirect("AuthServlet?action=manageUsers&error=" + 
                                java.net.URLEncoder.encode("Invalid user ID", "UTF-8"));
        }
    }

    private void handleResetUserPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!validateSession(request, response)) {
            return;
        }
        
        if (!AuthorizationUtil.hasAdminAccess(request)) {
            response.sendRedirect("views/login.jsp?error=Admin access required");
            return;
        }

        User adminUser = AuthorizationUtil.getCurrentUser(request);

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));

            AuthService.UserCreationResult result = authService.resetUserPassword(adminUser, userId);

            if (result.isSuccess()) {
                String successMessage = result.getMessage() + " New password: " + result.getTemporaryPassword();
                response.sendRedirect("AuthServlet?action=manageUsers&success=" + 
                                    java.net.URLEncoder.encode(successMessage, "UTF-8"));
            } else {
                response.sendRedirect("AuthServlet?action=manageUsers&error=" + 
                                    java.net.URLEncoder.encode(result.getMessage(), "UTF-8"));
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("AuthServlet?action=manageUsers&error=" + 
                                java.net.URLEncoder.encode("Invalid user ID", "UTF-8"));
        }
    }

    private void handleProfilePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!validateSession(request, response)) {
            return;
        }
        
        User user = AuthorizationUtil.getCurrentUser(request);
        if (user == null) {
            response.sendRedirect("views/login.jsp?error=Please log in to continue");
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("views/profile.jsp").forward(request, response);
    }

    private void handleFirstTimeSetupPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!validateSession(request, response)) {
            return;
        }
        
        User user = AuthorizationUtil.getCurrentUser(request);
        if (user == null) {
            response.sendRedirect("views/login.jsp?error=Session expired. Please login again.");
            return;
        }

        if (!user.isFirstLogin()) {
            // Already completed setup, redirect based on role
            String redirectUrl = AuthorizationUtil.getDefaultRedirect(user);
            response.sendRedirect(redirectUrl);
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("views/firstTimeSetup.jsp").forward(request, response);
    }

    private void handleFirstTimeSetup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("views/login.jsp?error=Session expired. Please login again.");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("views/login.jsp?error=Session expired. Please login again.");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String companyName = request.getParameter("companyName");

        ValidationUtil.ValidationResult result = authService.completeFirstTimeSetup(
            user, newPassword, confirmPassword, email, companyName);
        
        if (!result.isValid()) {
            request.setAttribute("error", result.getFirstError());
            request.setAttribute("user", user);
            request.getRequestDispatcher("views/firstTimeSetup.jsp").forward(request, response);
            return;
        }
        
        try {
            // Reload user with updated information from database
            User updatedUser = userDAO.findById(user.getUserId());
            if (updatedUser != null) {
                // Update session with fresh user data
                session.setAttribute("user", updatedUser);
                
                // Create a proper session token for the updated user
                String clientIP = getClientIP(request);
                String sessionToken = sessionService.createSession(updatedUser, clientIP, request.getHeader("User-Agent"));
                session.setAttribute("sessionToken", sessionToken);
                session.setAttribute("lastActivity", System.currentTimeMillis());
                
                // Determine redirect URL based on user role
                String redirectUrl = AuthorizationUtil.getDefaultRedirect(updatedUser);
                
                response.sendRedirect(redirectUrl + "&success=" + 
                    java.net.URLEncoder.encode("Setup completed successfully!", "UTF-8"));
            } else {
                response.sendRedirect("views/login.jsp?error=Setup completed but please login again");
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to reload user after setup: " + e.getMessage());
            response.sendRedirect("views/login.jsp?error=Setup may have failed, please try logging in again");
        }
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String resetUsername = request.getParameter("resetUsername");

        if (resetUsername == null || resetUsername.trim().isEmpty()) {
            request.setAttribute("error", "Username is required");
            request.getRequestDispatcher("views/login.jsp").forward(request, response);
            return;
        }

        String baseUrl = request.getScheme() + "://" + 
                        request.getServerName() + ":" + 
                        request.getServerPort() + 
                        request.getContextPath();

        PasswordResetResult result = authService.initiatePasswordReset(resetUsername.trim(), baseUrl);
        
        if (result.isSuccess()) {
            request.setAttribute("success", "If an account with that username exists and has an email address, a password reset link has been sent.");
        } else {
            request.setAttribute("error", result.getMessage());
        }

        request.getRequestDispatcher("views/login.jsp").forward(request, response);
    }

    private void handlePasswordResetPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            response.sendRedirect("views/login.jsp?error=Invalid reset link");
            return;
        }

        try {
            if (authService.isValidResetToken(token)) {
                request.setAttribute("resetToken", token);
                request.getRequestDispatcher("views/resetPassword.jsp").forward(request, response);
            } else {
                response.sendRedirect("views/login.jsp?error=Invalid or expired reset link");
            }
        } catch (SQLException e) {
            System.err.println("Database error checking reset token: " + e.getMessage());
            response.sendRedirect("views/login.jsp?error=System error occurred");
        }
    }

    private void handlePasswordReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        ValidationUtil.ValidationResult result = authService.completePasswordReset(token, newPassword, confirmPassword);
        
        if (result.isValid()) {
            response.sendRedirect("views/login.jsp?success=Password reset successfully. Please login with your new password.");
        } else {
            request.setAttribute("error", result.getFirstError());
            request.setAttribute("resetToken", token);
            request.getRequestDispatcher("views/resetPassword.jsp").forward(request, response);
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!validateSession(request, response)) {
            return;
        }
        
        User user = AuthorizationUtil.getCurrentUser(request);
        
        if (user == null) {
            response.sendRedirect("views/login.jsp");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        ValidationUtil.ValidationResult result = authService.changePassword(
            user, currentPassword, newPassword, confirmPassword);
        
        if (result.isValid()) {
            request.setAttribute("success", "Password changed successfully");
        } else {
            request.setAttribute("error", result.getFirstError());
        }
        
        request.getRequestDispatcher("views/profile.jsp").forward(request, response);
    }

    // HELPER METHODS
    private void setupRememberMeCookies(HttpServletResponse response, User user) {
        String rememberToken = sessionService.createRememberMeToken(user.getUserId());
        
        Cookie rememberCookie = new Cookie("rememberToken", rememberToken);
        rememberCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        rememberCookie.setPath("/");
        rememberCookie.setHttpOnly(true);
        response.addCookie(rememberCookie);
        
        Cookie userCookie = new Cookie("rememberedUser", user.getUsername());
        userCookie.setMaxAge(7 * 24 * 60 * 60);
        userCookie.setPath("/");
        userCookie.setHttpOnly(true);
        response.addCookie(userCookie);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie rememberCookie = new Cookie("rememberToken", "");
        rememberCookie.setMaxAge(0);
        rememberCookie.setPath("/");
        response.addCookie(rememberCookie);

        Cookie userCookie = new Cookie("rememberedUser", "");
        userCookie.setMaxAge(0);
        userCookie.setPath("/");
        response.addCookie(userCookie);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}