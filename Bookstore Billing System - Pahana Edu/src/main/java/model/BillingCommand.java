package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Command interface for billing operations
 * Follows the Command pattern for billing-related actions
 */
public interface BillingCommand {
    
    /**
     * Execute the billing command
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException;
}