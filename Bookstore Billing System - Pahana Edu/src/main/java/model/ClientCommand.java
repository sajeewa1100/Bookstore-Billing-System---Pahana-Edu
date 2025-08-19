package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Command pattern interface for client operations
 * All client command classes must implement this interface
 */
public interface ClientCommand {
    
    /**
     * Execute the command with the given request and response
     * 
     * @param request  The HTTP servlet request
     * @param response The HTTP servlet response
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException;
}