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
import util.PasswordUtils;
import util.EmailUtils;

@WebServlet("/AuthServlet")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect("views/index.jsp");
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
                response.sendRedirect("views/index.jsp");
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("views/index.jsp");
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
                response.sendRedirect("views/index.jsp");
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        // Validate input
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("views/index.jsp").forward(request, response);
            return;
        }

        try {
            User user = userDAO.authenticateUser(username.trim(), password);
            
            if (user != null) {
                // Check if account is active
                if (!"active".equals(user.getStatus())) {
                    request.setAttribute("error", "Your account has been deactivated. Please contact administrator.");
                    request.getRequestDispatcher("views/index.jsp").forward(request, response);
                    return;
                }

                // Create session
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(1800); // 30 minutes
                session.setAttribute("user", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                session.setAttribute("userId", user.getUserId());

                // Update last login
                userDAO.updateLastLogin(user.getUserId());

                // Handle remember me
                if ("true".equals(rememberMe)) {
                    Cookie userCookie = new Cookie("rememberedUser", username);
                    userCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                    userCookie.setPath("/");
                    response.addCookie(userCookie);
                }

                // Log successful login
                userDAO.logActivity(user.getUserId(), "LOGIN", "User logged in successfully");

                // Check if first time login for manager
                if ("manager".equals(user.getRole()) && user.isFirstLogin()) {
                    response.sendRedirect("AuthServlet?action=firstTimeSetup");
                } else {
                    // Redirect based on role
                    if ("manager".equals(user.getRole())) {
                        response.sendRedirect("BookServlet?action=dashboard");
                    } else {
                        response.sendRedirect("BillingServlet?action=invoices");
                    }
                }
            } else {
                // Log failed login attempt
                try {
                    User existingUser = userDAO.findByUsername(username.trim());
                    if (existingUser != null) {
                        userDAO.logActivity(existingUser.getUserId(), "LOGIN_FAILED", 
                                          "Failed login attempt - incorrect password");
                    }
                } catch (SQLException e) {
                    // Continue without logging if there's an error
                }

                request.setAttribute("error", "Invalid username or password");
                request.setAttribute("username", username);
                request.getRequestDispatcher("views/index.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "System error occurred. Please try again.");
            request.getRequestDispatcher("views/index.jsp").forward(request, response);
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
                    userDAO.logActivity(user.getUserId(), "LOGOUT", "User logged out");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            session.invalidate();
        }

        // Clear remember me cookie
        Cookie userCookie = new Cookie("rememberedUser", "");
        userCookie.setMaxAge(0);
        userCookie.setPath("/");
        response.addCookie(userCookie);

        response.sendRedirect("views/index.jsp?message=You have been logged out successfully");
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String resetUsername = request.getParameter("resetUsername");

        if (resetUsername == null || resetUsername.trim().isEmpty()) {
            request.setAttribute("error", "Username is required");
            request.getRequestDispatcher("views/index.jsp").forward(request, response);
            return;
        }

        try {
            User user = userDAO.findByUsername(resetUsername.trim());
            
            if (user != null && "manager".equals(user.getRole()) && user.getEmail() != null) {
                // Generate reset token
                String resetToken = PasswordUtils.generateResetToken();
                userDAO.savePasswordResetToken(user.getUserId(), resetToken);

                // Send reset email
                String resetLink = request.getScheme() + "://" + 
                                 request.getServerName() + ":" + 
                                 request.getServerPort() + 
                                 request.getContextPath() + 
                                 "/AuthServlet?action=resetPassword&token=" + resetToken;

                boolean emailSent = EmailUtils.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);

                if (emailSent) {
                    // Log password reset request
                    userDAO.logActivity(user.getUserId(), "PASSWORD_RESET_REQUEST", 
                                      "Password reset link sent to email");
                    
                    request.setAttribute("success", "Password reset link has been sent to your registered email address");
                } else {
                    request.setAttribute("error", "Failed to send reset email. Please try again later.");
                }
            } else {
                request.setAttribute("error", "Username not found, not a manager account, or no email registered");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "System error occurred. Please try again.");
        }

        request.getRequestDispatcher("views/index.jsp").forward(request, response);
    }

    private void handlePasswordResetPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            response.sendRedirect("views/index.jsp?error=Invalid reset link");
            return;
        }

        try {
            if (userDAO.isValidResetToken(token)) {
                request.setAttribute("resetToken", token);
                request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            } else {
                response.sendRedirect("views/index.jsp?error=Invalid or expired reset link");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("views/index.jsp?error=System error occurred");
        }
    }

    private void handlePasswordReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate input
        if (token == null || newPassword == null || confirmPassword == null ||
            token.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required");
            request.setAttribute("resetToken", token);
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.setAttribute("resetToken", token);
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        // Validate password strength
        PasswordUtils.PasswordStrength strength = PasswordUtils.checkPasswordStrength(newPassword);
        if (strength.isWeak()) {
            request.setAttribute("error", "Password is too weak. " + strength.getMessage());
            request.setAttribute("resetToken", token);
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        try {
            if (userDAO.isValidResetToken(token)) {
                String hashedPassword = PasswordUtils.hashPassword(newPassword);
                int userId = userDAO.resetPassword(token, hashedPassword);
                
                // Log password reset completion
                if (userId > 0) {
                    userDAO.logActivity(userId, "PASSWORD_RESET_COMPLETE", "Password reset successfully completed");
                }
                
                response.sendRedirect("views/index.jsp?success=Password reset successfully. Please login with your new password.");
            } else {
                request.setAttribute("error", "Invalid or expired reset token");
                request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "System error occurred. Please try again.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }

    private void handleFirstTimeSetupPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("views/index.jsp?error=Session expired. Please login again.");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"manager".equals(user.getRole()) || !user.isFirstLogin()) {
            response.sendRedirect("BookServlet?action=dashboard");
            return;
        }

        request.getRequestDispatcher("firstTimeSetup.jsp").forward(request, response);
    }

    private void handleFirstTimeSetup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("views/index.jsp?error=Session expired. Please login again.");
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

        // Validate input
        if (newPassword == null || confirmPassword == null || email == null ||
            newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty() || email.trim().isEmpty()) {
            request.setAttribute("error", "Password, confirm password, and email are required fields");
            request.getRequestDispatcher("firstTimeSetup.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("firstTimeSetup.jsp").forward(request, response);
            return;
        }

        // Validate password strength
        PasswordUtils.PasswordStrength strength = PasswordUtils.checkPasswordStrength(newPassword);
        if (strength.isWeak()) {
            request.setAttribute("error", "Password is too weak. " + strength.getMessage());
            request.getRequestDispatcher("firstTimeSetup.jsp").forward(request, response);
            return;
        }

        if (!isValidEmail(email)) {
            request.setAttribute("error", "Please enter a valid email address");
            request.getRequestDispatcher("firstTimeSetup.jsp").forward(request, response);
            return;
        }

        try {
            // Check if email is already used by another manager
            User existingEmailUser = userDAO.findByEmail(email.trim());
            if (existingEmailUser != null && existingEmailUser.getUserId() != user.getUserId()) {
                request.setAttribute("error", "This email is already registered with another account");
                request.getRequestDispatcher("firstTimeSetup.jsp").forward(request, response);
                return;
            }

            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            userDAO.completeFirstTimeSetup(user.getUserId(), hashedPassword, email.trim(), 
                                         companyName != null ? companyName.trim() : null);

            // Update session with new information
            user.setEmail(email.trim());
            user.setFirstLogin(false);
            if (companyName != null && !companyName.trim().isEmpty()) {
                user.setCompanyName(companyName.trim());
            }
            session.setAttribute("user", user);

            // Log successful first-time setup
            userDAO.logActivity(user.getUserId(), "FIRST_TIME_SETUP", 
                              "Completed first time setup - password updated and email configured");

            // Send welcome email (optional)
            try {
                String welcomeMessage = "Your Pahana Edu manager account setup has been completed successfully. " +
                                      "You can now access all system features including user management, " +
                                      "billing operations, and system reports.";
                // This could be expanded to send a proper welcome email
            } catch (Exception e) {
                // Log but don't fail the setup if email fails
                System.err.println("Failed to send welcome email: " + e.getMessage());
            }

            response.sendRedirect("BookServlet?action=dashboard&success=Setup completed successfully! Welcome to Pahana Edu.");
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "System error occurred during setup. Please try again.");
            request.getRequestDispatcher("firstTimeSetup.jsp").forward(request, response);
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("views/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate input
        if (currentPassword == null || newPassword == null || confirmPassword == null ||
            currentPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("profile.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New passwords do not match");
            request.getRequestDispatcher("profile.jsp").forward(request, response);
            return;
        }

        // Validate password strength
        PasswordUtils.PasswordStrength strength = PasswordUtils.checkPasswordStrength(newPassword);
        if (strength.isWeak()) {
            request.setAttribute("error", "New password is too weak. " + strength.getMessage());
            request.getRequestDispatcher("profile.jsp").forward(request, response);
            return;
        }

        try {
            // Verify current password
            User authenticatedUser = userDAO.authenticateUser(user.getUsername(), currentPassword);
            if (authenticatedUser == null) {
                request.setAttribute("error", "Current password is incorrect");
                request.getRequestDispatcher("profile.jsp").forward(request, response);
                return;
            }

            // Check if new password is same as current
            if (currentPassword.equals(newPassword)) {
                request.setAttribute("error", "New password must be different from current password");
                request.getRequestDispatcher("profile.jsp").forward(request, response);
                return;
            }

            // Update password
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            userDAO.updatePassword(user.getUserId(), hashedPassword);

            // Log activity
            userDAO.logActivity(user.getUserId(), "PASSWORD_CHANGE", "Password changed successfully");

            request.setAttribute("success", "Password changed successfully");
            request.getRequestDispatcher("profile.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "System error occurred. Please try again.");
            request.getRequestDispatcher("profile.jsp").forward(request, response);
        }
    }

    /**
     * Validate email address format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailPattern = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }

    /**
     * Generate secure session attributes
     */
    private void setupSecureSession(HttpSession session, User user) {
        session.setAttribute("user", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("loginTime", System.currentTimeMillis());
        session.setMaxInactiveInterval(1800); // 30 minutes
    }

    /**
     * Validate session and user permissions
     */
    private boolean isValidManagerSession(HttpSession session) {
        if (session == null) return false;
        
        User user = (User) session.getAttribute("user");
        return user != null && "manager".equals(user.getRole()) && "active".equals(user.getStatus());
    }

    /**
     * Clean sensitive data from request parameters for logging
     */
    private void logSecureActivity(int userId, String action, String details) {
        try {
            userDAO.logActivity(userId, action, details);
        } catch (SQLException e) {
            System.err.println("Failed to log user activity: " + e.getMessage());
        }
    }
}