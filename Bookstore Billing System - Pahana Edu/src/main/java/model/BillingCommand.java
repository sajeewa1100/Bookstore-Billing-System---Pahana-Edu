package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Command interface for Billing operations
 * Implements Command Pattern for handling different billing actions
 */
public interface BillingCommand {
    
    /**
     * Execute the command with request and response
     * 
     * @param request  HTTP request
     * @param response HTTP response
     * @throws ServletException if servlet error occurs
     * @throws IOException      if I/O error occurs
     */
    void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException;
}