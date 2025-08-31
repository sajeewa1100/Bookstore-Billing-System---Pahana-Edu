package util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import model.User;
import service.SessionService;


/**
 * Security filter to validate sessions and handle authentication
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private SessionService sessionService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.sessionService = new SessionService();
        System.out.println("✅ AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        String queryString = httpRequest.getQueryString();
        String fullPath = queryString != null ? path + "?" + queryString : path;
        
        System.out.println("=== FILTER DEBUG ===");
        System.out.println("Path: " + path);
        System.out.println("Full path: " + fullPath);
        System.out.println("Method: " + httpRequest.getMethod());
        
        // Allow access to public URLs, static resources, and authentication-related actions
        if (isPublicUrl(path) || isAuthServletPublicAction(httpRequest) || isFirstTimeSetupAccess(httpRequest)) {
            System.out.println("PUBLIC ACCESS GRANTED");
            
            // Check for remember-me token on login page only
            if (path.equals("/views/login.jsp") || path.equals("/")) {
                User rememberedUser = checkRememberMeToken(httpRequest);
                if (rememberedUser != null) {
                    // Auto-login user
                    createAutoLoginSession(httpRequest, httpResponse, rememberedUser);
                    httpResponse.sendRedirect(contextPath + "/DashboardServlet?action=dashboard");
                    return;
                }
            }
            chain.doFilter(request, response);
            return;
        }
        
        // Get current session
        HttpSession session = httpRequest.getSession(false);
        User user = null;
        String sessionToken = null;
        
        if (session != null) {
            user = (User) session.getAttribute("user");
            sessionToken = (String) session.getAttribute("sessionToken");
        }
        
        System.out.println("Session validation - User: " + (user != null ? user.getUsername() : "null"));
        System.out.println("Session token present: " + (sessionToken != null));
        
        // SPECIAL CASE: Users with first_login = true should be allowed to access first-time setup
        if (user != null && user.isFirstLogin() && isFirstTimeSetupRelated(httpRequest)) {
            System.out.println("FIRST-TIME SETUP ACCESS GRANTED for user: " + user.getUsername());
            // Update session activity and continue
            session.setAttribute("lastActivity", System.currentTimeMillis());
            addSecurityHeaders(httpResponse);
            chain.doFilter(request, response);
            return;
        }
        
        // Validate session for normal authenticated users
        if (user == null || sessionToken == null || !sessionService.isValidSession(sessionToken)) {
            System.out.println("SESSION INVALID - redirecting to login");
            handleUnauthenticatedRequest(httpRequest, httpResponse, contextPath, fullPath);
            return;
        }
        
        // Check role-based access
        if (isAdminUrl(fullPath) && !"admin".equals(user.getRole())) {
            System.out.println("ADMIN ACCESS DENIED for user: " + user.getUsername());
            httpResponse.sendRedirect(contextPath + "/DashboardServlet?action=dashboard&error=Access denied");
            return;
        }
        
        // Update session activity
        session.setAttribute("lastActivity", System.currentTimeMillis());
        
        // Add security headers
        addSecurityHeaders(httpResponse);
        
        System.out.println("ACCESS GRANTED for user: " + user.getUsername());
        
        // Continue with request
        chain.doFilter(request, response);
    }

    /**
     * Check if URL is public (doesn't require authentication)
     */
    private boolean isPublicUrl(String path) {
        // Root path access
        if (path.equals("/") || path.isEmpty()) {
            return true;
        }
        
        // Static file extensions - allow all common static resources
        String[] staticExtensions = {
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".svg", 
            ".woff", ".woff2", ".ttf", ".eot", ".map", ".json"
        };
        
        String lowerPath = path.toLowerCase();
        for (String ext : staticExtensions) {
            if (lowerPath.endsWith(ext)) {
                System.out.println("Static resource allowed: " + path);
                return true;
            }
        }
        
        // Specific public pages
        String[] publicPages = {
            "/views/login.jsp",
            "/views/resetPassword.jsp"
        };
        
        for (String publicPage : publicPages) {
            if (path.equals(publicPage)) {
                System.out.println("Public page allowed: " + path);
                return true;
            }
        }
        
        // Public directories - flexible path matching
        String[] publicDirs = {
            "/assets/", "/views/assets/", 
            "/js/", "/views/js/",
            "/css/", "/views/css/",
            "/images/", "/views/images/",
            "/static/", "/views/static/",
            "/resources/", "/views/resources/"
        };
        
        for (String publicDir : publicDirs) {
            if (path.startsWith(publicDir)) {
                System.out.println("Public directory allowed: " + path);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Check if this is an AuthServlet action that should bypass authentication
     */
    private boolean isAuthServletPublicAction(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String action = request.getParameter("action");
        
        return path.equals("/AuthServlet") && 
               (action == null || "login".equals(action) || 
                "forgotPassword".equals(action) || "resetPassword".equals(action));
    }

    /**
     * Check if this is first-time setup related access
     */
    private boolean isFirstTimeSetupAccess(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String action = request.getParameter("action");
        
        // Allow access to first-time setup page
        if (path.equals("/views/firstTimeSetup.jsp")) {
            System.out.println("First-time setup page access allowed");
            return true;
        }
        
        // Allow AuthServlet first-time setup actions
        if (path.equals("/AuthServlet") && "firstTimeSetup".equals(action)) {
            System.out.println("First-time setup action allowed");
            return true;
        }
        
        return false;
    }

    /**
     * Check if request is related to first-time setup process
     */
    private boolean isFirstTimeSetupRelated(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String action = request.getParameter("action");
        
        return path.equals("/views/firstTimeSetup.jsp") || 
               (path.equals("/AuthServlet") && "firstTimeSetup".equals(action));
    }

    /**
     * Check if URL requires admin role
     */
    private boolean isAdminUrl(String fullPath) {
        String[] adminPatterns = {
            "manageUsers", "getUsersData", "createUser", "updateUser", 
            "resetUserPassword", "/views/admin/"
        };
        
        for (String pattern : adminPatterns) {
            if (fullPath.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Handle unauthenticated request
     */
    private void handleUnauthenticatedRequest(HttpServletRequest request, HttpServletResponse response, 
                                            String contextPath, String fullPath) throws IOException {
        
        // Invalidate session if exists
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        // Store original URL for redirect after login
        String redirectUrl = fullPath;
        if (redirectUrl.length() > 200) { // Limit redirect URL length
            redirectUrl = "/DashboardServlet?action=dashboard";
        }
        
        // Redirect to login with return URL
        response.sendRedirect(contextPath + "/views/login.jsp?returnUrl=" + 
                            java.net.URLEncoder.encode(redirectUrl, "UTF-8"));
    }

    /**
     * Check for remember-me token
     */
    private User checkRememberMeToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberToken".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (token != null && !token.isEmpty()) {
                        User user = sessionService.getUserByRememberToken(token);
                        if (user != null && "active".equals(user.getStatus())) {
                            return user;
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }

    /**
     * Create auto-login session for remembered user
     */
    private void createAutoLoginSession(HttpServletRequest request, HttpServletResponse response, User user) {
        // Create new session
        String clientIP = getClientIP(request);
        String sessionToken = sessionService.createSession(user, clientIP, request.getHeader("User-Agent"));
        
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(1800); // 30 minutes
        session.setAttribute("user", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("sessionToken", sessionToken);
        session.setAttribute("lastActivity", System.currentTimeMillis());
        session.setAttribute("autoLogin", true); // Mark as auto-login
        
        System.out.println("✅ Auto-login successful for user: " + user.getUsername());
    }

    /**
     * Add security headers to response
     */
    private void addSecurityHeaders(HttpServletResponse response) {
        // Prevent caching of sensitive pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // Security headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Content Security Policy (adjust based on your needs)
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com; " +
            "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com; " +
            "font-src 'self' https://cdnjs.cloudflare.com; " +
            "img-src 'self' data: https:; " +
            "connect-src 'self'"
        );
    }

    /**
     * Get client IP address
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

    @Override
    public void destroy() {
        if (sessionService != null) {
            sessionService.shutdown();
        }
        System.out.println("✅ AuthenticationFilter destroyed");
    }
}