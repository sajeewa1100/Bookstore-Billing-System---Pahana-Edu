package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.User;

public class AuthorizationUtil {

    /**
     * Check if user has admin access
     */
    public static boolean hasAdminAccess(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && "admin".equals(user.getRole());
    }

    /**
     * Check if user has manager access (admin or manager)
     */
    public static boolean hasManagerAccess(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && ("admin".equals(user.getRole()) || "manager".equals(user.getRole()));
    }

    /**
     * Check if user has staff access (admin, manager, or staff)
     */
    public static boolean hasStaffAccess(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && ("admin".equals(user.getRole()) || 
                               "manager".equals(user.getRole()) || 
                               "staff".equals(user.getRole()));
    }

    /**
     * Check if user can manage invoices (admin or manager only)
     */
    public static boolean canManageInvoices(HttpServletRequest request) {
        return hasManagerAccess(request);
    }

    /**
     * Check if user can create invoices (staff, manager, or admin)
     */
    public static boolean canCreateInvoices(HttpServletRequest request) {
        return hasStaffAccess(request);
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    /**
     * Get current authenticated user
     */
    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    /**
     * Get default redirect URL based on user role
     */
    public static String getDefaultRedirect(User user) {
        if (user == null) {
            return "views/login.jsp";
        }

        switch (user.getRole()) {
            case "admin":
            case "manager":
                return "ManagerServlet?action=dashboard";
            case "staff":
                return "billing";
            default:
                return "views/login.jsp";
        }
    }

    /**
     * Get dashboard URL for user role
     */
    public static String getDashboardUrl(String role) {
        switch (role) {
            case "admin":
            case "manager":
                return "ManagerServlet?action=dashboard";
            case "staff":
                return "billing";
            default:
                return "views/login.jsp";
        }
    }

    /**
     * Check if user can access servlet based on role
     */
    public static boolean canAccessServlet(HttpServletRequest request, String servletName) {
        User user = getCurrentUser(request);
        if (user == null) {
            return false;
        }

        String role = user.getRole();

        switch (servletName.toLowerCase()) {
            case "managerservlet":
                return "admin".equals(role) || "manager".equals(role);
            
            case "billingservlet":
                return "admin".equals(role) || "manager".equals(role) || "staff".equals(role);
            
            case "bookservlet":
            case "clientservlet":
                return "admin".equals(role) || "manager".equals(role) || "staff".equals(role);
            
            case "authservlet":
                return true;
            
            default:
                return false;
        }
    }

    /**
     * Check if user can perform specific action
     */
    public static boolean canPerformAction(HttpServletRequest request, String action) {
        User user = getCurrentUser(request);
        if (user == null) {
            return false;
        }

        String role = user.getRole();

        switch (action.toLowerCase()) {
            case "viewinvoices":
            case "editinvoice":
            case "deleteinvoice":
            case "managefinances":
                return "admin".equals(role) || "manager".equals(role);
            
            case "createinvoice":
            case "managebooks":
            case "manageclients":
                return "admin".equals(role) || "manager".equals(role) || "staff".equals(role);
            
            case "managestaff":
            case "manageusers":
                return "admin".equals(role) || "manager".equals(role);
            
            default:
                return true;
        }
    }
}