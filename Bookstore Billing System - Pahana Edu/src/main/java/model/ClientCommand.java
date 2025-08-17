package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Command interface for client operations following the Command Pattern.
 * This interface defines the contract for all client-related commands.
 */
public interface ClientCommand {
    
    /**
     * Execute the command with the given request and response objects
     * 
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException;
}