package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import util.AuthorizationUtil;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Access Control Filter to protect servlets based on user roles
 * This filter ensures only authorized users can access specific servlets
*/ 
@WebFilter(urlPatterns = {
    "/ManagerServlet", "/ManagerServlet/*",
    "/billing", "/billing/*", "/create-invoice", 
    "/books", "/books/*", "/clients", "/clients/*"
})
public class AccessControlFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AccessControlFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String servletPath = requestURI.substring(contextPath.length());
        
        System.out.println("AccessControlFilter: Checking access to " + servletPath);
        
        // Check if user is authenticated
        if (!AuthorizationUtil.isAuthenticated(httpRequest)) {
            System.out.println("AccessControlFilter: User not authenticated, redirecting to login");
            redirectToLogin(httpResponse, contextPath, "Please login to continue");
            return;
        }
        
        User user = AuthorizationUtil.getCurrentUser(httpRequest);
        if (user == null) {
            System.out.println("AccessControlFilter: User session invalid, redirecting to login");
            redirectToLogin(httpResponse, contextPath, "Session expired");
            return;
        }
        
        System.out.println("AccessControlFilter: User " + user.getUsername() + " with role " + user.getRole() + 
                          " accessing " + servletPath);
        
        // Check servlet-specific access permissions
        boolean hasAccess = checkServletAccess(servletPath, user, httpRequest);
        
        if (!hasAccess) {
            System.out.println("AccessControlFilter: Access denied for user " + user.getUsername() + 
                              " to " + servletPath);
            handleAccessDenied(httpResponse, contextPath, user.getRole(), servletPath);
            return;
        }
        
        System.out.println("AccessControlFilter: Access granted to " + servletPath);
        
        // Continue with the request
        chain.doFilter(request, response);
    }
    
    private boolean checkServletAccess(String servletPath, User user, HttpServletRequest request) {
        String role = user.getRole();
        
        // Extract servlet name
        String servletName = extractServletName(servletPath);
        
        switch (servletName) {
            case "ManagerServlet":
                // Only admin and manager can access ManagerServlet
                return "admin".equals(role) || "manager".equals(role);
                
            case "billing":
            case "create-invoice":
                // Check specific billing actions
                String billingAction = request.getParameter("action");
                if ("invoices".equals(billingAction) || "manageInvoices".equals(billingAction) || 
                    "editInvoice".equals(billingAction) || "deleteInvoice".equals(billingAction)) {
                    // Only managers can manage all invoices
                    return "admin".equals(role) || "manager".equals(role);
                } else {
                    // Staff, manager, and admin can create invoices and view their own
                    return "admin".equals(role) || "manager".equals(role) || "staff".equals(role);
                }
                
            case "books":
            case "clients":
                // Staff, manager, and admin can access these
                return "admin".equals(role) || "manager".equals(role) || "staff".equals(role);
                
            default:
                // Default deny
                System.out.println("AccessControlFilter: Unknown servlet " + servletName + ", denying access");
                return false;
        }
    }
    
    private String extractServletName(String servletPath) {
        // Remove leading slash and extract servlet name
        if (servletPath.startsWith("/")) {
            servletPath = servletPath.substring(1);
        }
        
        // Extract servlet name (everything before first slash or end of string)
        int slashIndex = servletPath.indexOf('/');
        if (slashIndex != -1) {
            return servletPath.substring(0, slashIndex);
        } else {
            return servletPath;
        }
    }
    
    private void redirectToLogin(HttpServletResponse response, String contextPath, String errorMessage) 
            throws IOException {
        String encodedMessage = URLEncoder.encode(errorMessage, "UTF-8");
        String redirectUrl = contextPath + "/views/login.jsp?error=" + encodedMessage;
        response.sendRedirect(redirectUrl);
    }
    
    private void handleAccessDenied(HttpServletResponse response, String contextPath, String userRole, String requestedPath) 
            throws IOException {
        
        String errorMessage = "You don't have permission to access this resource.";
        String redirectUrl;
        
        switch (userRole) {
            case "admin":
                // Admin should have access to everything, this shouldn't happen
                errorMessage = "Access denied to this resource";
                redirectUrl = contextPath + "/ManagerServlet?action=dashboard";
                break;
                
            case "manager":
                // Redirect to manager dashboard with error
                redirectUrl = contextPath + "/ManagerServlet?action=dashboard&error=" + 
                             URLEncoder.encode(errorMessage, "UTF-8");
                break;
                
            case "staff":
                // Redirect to staff dashboard (billing page) with error
                // Avoid redirecting to the same page they're trying to access
                if (requestedPath.contains("/billing") || requestedPath.contains("/create-invoice")) {
                    // If they're trying to access billing but don't have permission for specific action
                    redirectUrl = contextPath + "/billing?action=dashboard&error=" + 
                                 URLEncoder.encode("Access denied to this billing function", "UTF-8");
                } else {
                    // For other resources, redirect to billing dashboard
                    redirectUrl = contextPath + "/billing?error=" + 
                                 URLEncoder.encode(errorMessage, "UTF-8");
                }
                break;
                
            default:
                // Unknown role or guest - redirect to login
                redirectUrl = contextPath + "/views/login.jsp?error=" + 
                             URLEncoder.encode("Access denied - insufficient permissions", "UTF-8");
                break;
        }
        
        System.out.println("AccessControlFilter: Redirecting " + userRole + " to " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Get the appropriate home page for a user role
     */
    private String getHomePageForRole(String role, String contextPath) {
        switch (role) {
            case "admin":
            case "manager":
                return contextPath + "/ManagerServlet?action=dashboard";
            case "staff":
                return contextPath + "/billing";
            default:
                return contextPath + "/views/login.jsp";
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("AccessControlFilter destroyed");
    }
}