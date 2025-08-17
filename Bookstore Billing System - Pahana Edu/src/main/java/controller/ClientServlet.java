package controller;

import model.ClientCommand;
import model.CommandFactory;
import service.ClientService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet for handling client management operations using the Command pattern.
 * 
 * This servlet delegates all client operations to appropriate Command implementations
 * created by the CommandFactory. This promotes better separation of concerns and
 * makes the code more maintainable and testable.
 * 
 * The servlet handles both GET and POST requests by determining the appropriate
 * command based on the action parameter and executing it.
 */
@WebServlet("/ClientServlet")
public class ClientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ClientServlet.class.getName());
    
    private ClientService clientService;
    
    /**
     * Initialize the servlet and create the ClientService instance
     */
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            clientService = new ClientService();
            LOGGER.info("ClientServlet initialized successfully with ClientService");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize ClientServlet: " + e.getMessage(), e);
            throw new ServletException("Failed to initialize ClientService", e);
        }
    }
    
    /**
     * Handle GET requests using the Command pattern
     * 
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = getActionFromRequest(request);
        LOGGER.info("ClientServlet: Processing GET request with action: " + action);
        
        try {
            // Create appropriate command using the factory
            ClientCommand command = CommandFactory.createClientCommand(action, clientService);
            
            if (command != null) {
                // Execute the command
                command.execute(request, response);
                LOGGER.info("ClientServlet: Successfully executed command for action: " + action);
            } else {
                // Handle unknown action
                handleUnknownAction(request, response, action);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ClientServlet: Error processing GET request with action '" + action + "': " + e.getMessage(), e);
            handleError(request, response, e, "processing your request");
        }
    }
    
    /**
     * Handle POST requests using the Command pattern
     * 
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = getActionFromRequest(request);
        LOGGER.info("ClientServlet: Processing POST request with action: " + action);
        
        try {
            // Create appropriate command using the factory
            ClientCommand command = CommandFactory.createClientCommand(action, clientService);
            
            if (command != null) {
                // Execute the command
                command.execute(request, response);
                LOGGER.info("ClientServlet: Successfully executed command for action: " + action);
            } else {
                // Handle unknown action
                handleUnknownAction(request, response, action);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ClientServlet: Error processing POST request with action '" + action + "': " + e.getMessage(), e);
            handleError(request, response, e, "processing your request");
        }
    }
    
    /**
     * Get the action parameter from the request, with fallback to default
     * 
     * @param request The HttpServletRequest
     * @return The action string, never null
     */
    private String getActionFromRequest(HttpServletRequest request) {
        String action = request.getParameter("action");
        
        if (action == null || action.trim().isEmpty()) {
            action = CommandFactory.getDefaultClientAction();
            LOGGER.info("ClientServlet: No action specified, using default: " + action);
        } else {
            action = action.trim();
        }
        
        return action;
    }
    
    /**
     * Handle unknown/unsupported actions
     * 
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param action The unknown action that was requested
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void handleUnknownAction(HttpServletRequest request, HttpServletResponse response, String action) 
            throws ServletException, IOException {
        
        LOGGER.warning("ClientServlet: Unknown action requested: " + action);
        
        // Set error message with helpful information
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Unknown action: '").append(action).append("'. ");
        errorMessage.append("Available actions are: ");
        
        String[] availableActions = CommandFactory.getAvailableClientActions();
        for (int i = 0; i < availableActions.length; i++) {
            errorMessage.append(availableActions[i]);
            if (i < availableActions.length - 1) {
                errorMessage.append(", ");
            }
        }
        
        setErrorMessage(request, errorMessage.toString());
        
        // Redirect to default action (view clients)
        String defaultAction = CommandFactory.getDefaultClientAction();
        ClientCommand defaultCommand = CommandFactory.createClientCommand(defaultAction, clientService);
        
        if (defaultCommand != null) {
            try {
                defaultCommand.execute(request, response);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "ClientServlet: Error executing default command: " + e.getMessage(), e);
                handleCriticalError(request, response, e);
            }
        } else {
            // This should never happen, but handle it gracefully
            handleCriticalError(request, response, new IllegalStateException("Default command not available"));
        }
    }
    
    /**
     * Handle general errors by setting error message and redirecting to default view
     * 
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param exception The exception that occurred
     * @param context Additional context about when the error occurred
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception exception, String context) 
            throws ServletException, IOException {
        
        String errorMessage = "An error occurred while " + context + ": " + exception.getMessage();
        setErrorMessage(request, errorMessage);
        
        // Try to redirect to the default view
        try {
            String defaultAction = CommandFactory.getDefaultClientAction();
            ClientCommand defaultCommand = CommandFactory.createClientCommand(defaultAction, clientService);
            
            if (defaultCommand != null) {
                defaultCommand.execute(request, response);
            } else {
                handleCriticalError(request, response, new IllegalStateException("Default command not available"));
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ClientServlet: Error in error handling: " + e.getMessage(), e);
            handleCriticalError(request, response, e);
        }
    }
    
    /**
     * Handle critical errors that prevent normal error recovery
     * 
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param exception The critical exception
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void handleCriticalError(HttpServletRequest request, HttpServletResponse response, Exception exception) 
            throws ServletException, IOException {
        
        LOGGER.log(Level.SEVERE, "ClientServlet: Critical error occurred: " + exception.getMessage(), exception);
        
        // Set basic error attributes
        request.setAttribute("errorMessage", "A critical system error occurred. Please contact support.");
        request.setAttribute("clients", java.util.List.of()); // Empty list to prevent JSP errors
        request.setAttribute("totalClients", 0);
        
        // Forward to error page or basic client view
        try {
            request.getRequestDispatcher("views/clients.jsp").forward(request, response);
        } catch (Exception e) {
            // Last resort - send error response
            LOGGER.log(Level.SEVERE, "ClientServlet: Failed to forward to error page: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "A critical system error occurred. Please contact support.");
        }
    }
    
    /**
     * Set error message in session for display to user
     * 
     * @param request The HttpServletRequest
     * @param message The error message to set
     */
    private void setErrorMessage(HttpServletRequest request, String message) {
        try {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", message);
            LOGGER.info("ClientServlet: Error message set - " + message);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "ClientServlet: Failed to set error message: " + e.getMessage(), e);
        }
    }
    
    /**
     * Set success message in session for display to user
     * 
     * @param request The HttpServletRequest
     * @param message The success message to set
     */
    private void setSuccessMessage(HttpServletRequest request, String message) {
        try {
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", message);
            LOGGER.info("ClientServlet: Success message set - " + message);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "ClientServlet: Failed to set success message: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get available actions for this servlet (useful for debugging/monitoring)
     * 
     * @return Array of available action strings
     */
    public String[] getAvailableActions() {
        return CommandFactory.getAvailableClientActions();
    }
    
    /**
     * Check if an action is supported by this servlet
     * 
     * @param action The action to check
     * @return true if action is supported, false otherwise
     */
    public boolean isActionSupported(String action) {
        return CommandFactory.isValidClientAction(action);
    }
    
    /**
     * Get a description of what an action does
     * 
     * @param action The action to describe
     * @return Human-readable description of the action
     */
    public String getActionDescription(String action) {
        return CommandFactory.getClientActionDescription(action);
    }
    
    /**
     * Clean up resources when servlet is destroyed
     */
    @Override
    public void destroy() {
        try {
            if (clientService != null) {
                // Perform any necessary cleanup on the service
                LOGGER.info("ClientServlet: Cleaning up ClientService");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "ClientServlet: Error during cleanup: " + e.getMessage(), e);
        } finally {
            clientService = null;
            LOGGER.info("ClientServlet destroyed successfully");
        }
        super.destroy();
    }
    
    /**
     * Get servlet info
     * 
     * @return Servlet information string
     */
    @Override
    public String getServletInfo() {
        return "ClientServlet - Handles client management operations using Command pattern";
    }
}