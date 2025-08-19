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

import model.User;
import dao.UserDAO;
import service.AuthService;
import util.ValidationUtil;

@WebServlet("/AuthServlet")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        authService = new AuthService();
        
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
            default:
                response.sendRedirect("views/login.jsp");
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        // Basic validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("views/login.jsp").forward(request, response);
            return;
        }

        // Get client IP
        String clientIP = getClientIP(request);
        
        // Use AuthService for authentication with security features
        AuthService.AuthResult authResult = authService.authenticateUser(username.trim(), password, clientIP);
        
        if (authResult.isSuccess()) {
            User user = authResult.getUser();
            
            // Create session
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(1800); // 30 minutes
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            session.setAttribute("userId", user.getUserId());

            // Handle remember me
            if ("true".equals(rememberMe)) {
                Cookie userCookie = new Cookie("rememberedUser", username);
                userCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                userCookie.setPath("/");
                userCookie.setHttpOnly(true); // Security enhancement
                response.addCookie(userCookie);
            }

            // Check if first time login for manager
            if ("manager".equals(user.getRole()) && user.isFirstLogin()) {
                response.sendRedirect("AuthServlet?action=firstTimeSetup");
            } else {
                // FIXED: Redirect based on role - GO TO DASHBOARD FOR MANAGERS
                if ("manager".equals(user.getRole())) {
                    System.out.println("🔄 AuthServlet: Redirecting manager to DashboardServlet");
                    response.sendRedirect("DashboardServlet?action=dashboard"); // CHANGED FROM BookServlet
                } else {
                    response.sendRedirect("BillingServlet?action=invoices");
                }
            }
        } else {
            // Handle authentication failure
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
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Log the logout activity
            User user = (User) session.getAttribute("user");
            if (user != null) {
                try {
                    userDAO.logActivity(user.getUserId(), "LOGOUT", "User logged out from IP: " + 
                                      getClientIP(request));
                } catch (SQLException e) {
                    System.err.println("Failed to log logout: " + e.getMessage());
                }
            }
            
            session.invalidate();
        }

        // Clear remember me cookie
        Cookie userCookie = new Cookie("rememberedUser", "");
        userCookie.setMaxAge(0);
        userCookie.setPath("/");
        response.addCookie(userCookie);

        response.sendRedirect("views/login.jsp?message=You have been logged out successfully");
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String resetUsername = request.getParameter("resetUsername");

        if (resetUsername == null || resetUsername.trim().isEmpty()) {
            request.setAttribute("error", "Username is required");
            request.getRequestDispatcher("views/login.jsp").forward(request, response);
            return;
        }

        // Create base URL for reset link
        String baseUrl = request.getScheme() + "://" + 
                        request.getServerName() + ":" + 
                        request.getServerPort() + 
                        request.getContextPath();

        // Use AuthService for password reset
        ValidationUtil.ValidationResult result = authService.initiatePasswordReset(resetUsername.trim(), baseUrl);
        
        if (result.isValid()) {
            request.setAttribute("success", "If an account with that username exists and has an email address, a password reset link has been sent.");
        } else {
            request.setAttribute("error", result.getFirstError());
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
            if (userDAO.isValidResetToken(token)) {
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

        // Use AuthService for password reset completion
        ValidationUtil.ValidationResult result = authService.completePasswordReset(token, newPassword, confirmPassword);
        
        if (result.isValid()) {
            response.sendRedirect("views/login.jsp?success=Password reset successfully. Please login with your new password.");
        } else {
            request.setAttribute("error", result.getFirstError());
            request.setAttribute("resetToken", token);
            request.getRequestDispatcher("views/resetPassword.jsp").forward(request, response);
        }
    }

    private void handleFirstTimeSetupPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("views/login.jsp?error=Session expired. Please login again.");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"manager".equals(user.getRole()) || !user.isFirstLogin()) {
            response.sendRedirect("BookServlet?action=dashboard");
            return;
        }

        request.getRequestDispatcher("views/firstTimeSetup.jsp").forward(request, response);
    }

    private void handleFirstTimeSetup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("views/login.jsp?error=Session expired. Please login again.");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"manager".equals(user.getRole())) {
            response.sendRedirect("BookServlet?action=dashboard");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String companyName = request.getParameter("companyName");

        // Use AuthService for first-time setup
        ValidationUtil.ValidationResult result = authService.completeFirstTimeSetup(
            user, newPassword, confirmPassword, email, companyName);
        
        if (result.isValid()) {
            // Update session with new information
            session.setAttribute("user", user);
            response.sendRedirect("BookServlet?action=dashboard&success=Setup completed successfully! Welcome to Pahana Edu.");
        } else {
            request.setAttribute("error", result.getFirstError());
            request.getRequestDispatcher("views/firstTimeSetup.jsp").forward(request, response);
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("views/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Use AuthService for password change
        ValidationUtil.ValidationResult result = authService.changePassword(
            user, currentPassword, newPassword, confirmPassword);
        
        if (result.isValid()) {
            request.setAttribute("success", "Password changed successfully");
        } else {
            request.setAttribute("error", result.getFirstError());
        }
        
        request.getRequestDispatcher("views/profile.jsp").forward(request, response);
    }

    /**
     * Get client IP address with proxy support
     */
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