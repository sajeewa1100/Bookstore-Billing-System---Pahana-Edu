package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.ClientService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command implementation for deleting a client.
 * This command handles the removal of client records from the system.
 */
public class DeleteClientCommand implements ClientCommand {
    
    private static final Logger LOGGER = Logger.getLogger(DeleteClientCommand.class.getName());
    private final ClientService clientService;
    
    /**
     * Constructor for DeleteClientCommand
     * 
     * @param clientService The ClientService instance to use for client operations
     * @throws IllegalArgumentException if clientService is null
     */
    public DeleteClientCommand(ClientService clientService) {
        if (clientService == null) {
            throw new IllegalArgumentException("ClientService cannot be null");
        }
        this.clientService = clientService;
    }
    
    /**
     * Execute the delete client command
     * 
     * @param request The HttpServletRequest containing client ID to delete
     * @param response The HttpServletResponse for redirecting after operation
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        LOGGER.info("DeleteClientCommand: Executing delete client command");
        
        try {
            // Get client ID from request
            String idStr = request.getParameter("id");
            
            // Validate ID parameter
            if (idStr == null || idStr.trim().isEmpty()) {
                setErrorMessage(request, "Client ID is required for deletion");
                LOGGER.warning("DeleteClientCommand: No client ID provided");
                response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
                return;
            }
            
            Long clientId = parseClientId(idStr);
            
            // Get client information before deletion for success message
            ClientDTO client = null;
            try {
                client = clientService.getClientById(clientId);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "DeleteClientCommand: Could not retrieve client before deletion: " + e.getMessage());
            }
            
            String clientName = (client != null) ? client.getFullName() : "Client ID " + clientId;
            
            // Perform the deletion
            boolean success = clientService.deleteClient(clientId);
            
            if (success) {
                setSuccessMessage(request, "Client deleted successfully: " + clientName);
                LOGGER.info("DeleteClientCommand: Client deleted successfully - " + clientName);
            } else {
                setErrorMessage(request, "Failed to delete client. The client may not exist or may be referenced by other records.");
                LOGGER.warning("DeleteClientCommand: Failed to delete client with ID: " + clientId);
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "DeleteClientCommand: Invalid client ID format - " + e.getMessage(), e);
            setErrorMessage(request, "Invalid client ID format. Please provide a valid numeric ID.");
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "DeleteClientCommand: Validation error - " + e.getMessage(), e);
            setErrorMessage(request, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DeleteClientCommand: Unexpected error - " + e.getMessage(), e);
            setErrorMessage(request, "An unexpected error occurred while deleting the client: " + e.getMessage());
        }
        
        // Redirect to clients list to avoid form resubmission
        response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
    }
    
    /**
     * Parse client ID from string parameter
     * 
     * @param idStr The ID string to parse
     * @return Parsed Long ID
     * @throws NumberFormatException if ID is not a valid number
     * @throws IllegalArgumentException if ID is not positive
     */
    private Long parseClientId(String idStr) throws NumberFormatException, IllegalArgumentException {
        try {
            Long id = Long.parseLong(idStr.trim());
            
            if (id <= 0) {
                throw new IllegalArgumentException("Client ID must be a positive number");
            }
            
            return id;
            
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Client ID must be a valid number");
        }
    }
    
    /**
     * Set success message in session
     * 
     * @param request The HttpServletRequest
     * @param message The success message to set
     */
    private void setSuccessMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("successMessage", message);
        LOGGER.info("DeleteClientCommand: Success message set - " + message);
    }
    
    /**
     * Set error message in session
     * 
     * @param request The HttpServletRequest
     * @param message The error message to set
     */
    private void setErrorMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", message);
        LOGGER.warning("DeleteClientCommand: Error message set - " + message);
    }
}