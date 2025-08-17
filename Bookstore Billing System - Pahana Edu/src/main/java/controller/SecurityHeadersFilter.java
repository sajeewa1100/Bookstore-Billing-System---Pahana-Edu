package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security Headers Filter to add security headers to all HTTP responses
 */
@WebFilter("/*") // Apply to all URLs
public class SecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Filter initialization if needed
        System.out.println("🛡️ SecurityHeadersFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Cast to HTTP-specific interfaces
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Add security headers before processing the request
        addSecurityHeaders(httpRequest, httpResponse);
        
        // Continue with the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
        System.out.println("🛡️ SecurityHeadersFilter destroyed");
    }

    /**
     * Add security headers to the HTTP response
     */
    private void addSecurityHeaders(HttpServletRequest request, HttpServletResponse response) {
        
        // Prevent clickjacking attacks
        response.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Strict Transport Security (HTTPS only)
        // Only add HSTS if the connection is secure
        if (request.isSecure()) {
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
        
        // Content Security Policy - adjust based on your needs
        String csp = "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self' https://fonts.gstatic.com; " +
                    "connect-src 'self'";
        response.setHeader("Content-Security-Policy", csp);
        
        // Referrer Policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions Policy (formerly Feature Policy)
        response.setHeader("Permissions-Policy", 
            "camera=(), microphone=(), geolocation=(), payment=()");
        
        // Remove server information
        response.setHeader("Server", "");
        
        // Cache control for sensitive pages
        String requestURI = request.getRequestURI();
        if (isSensitivePage(requestURI)) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
        
        // Add custom security header
        response.setHeader("X-Security-Filter", "Active");
    }
    
    /**
     * Check if the current page contains sensitive information
     */
    private boolean isSensitivePage(String requestURI) {
        return requestURI != null && (
            requestURI.contains("/admin") ||
            requestURI.contains("/login") ||
            requestURI.contains("/user") ||
            requestURI.contains("/dashboard") ||
            requestURI.contains("/profile") ||
            requestURI.endsWith(".jsp") // All JSP pages
        );
    }
}