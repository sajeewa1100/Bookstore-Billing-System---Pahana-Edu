package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.ClientService;
import java.io.IOException;

/**
 * Command to handle deleting a client
 */
public class DeleteClientCommand implements ClientCommand {
    private final ClientService clientService;

    public DeleteClientCommand(ClientService clientService) {
        if (clientService == null) {
            throw new IllegalArgumentException("ClientService cannot be null");
        }
        this.clientService = clientService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Extract client ID
            String clientIdStr = request.getParameter("id");
            if (clientIdStr == null || clientIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Client ID is required");
            }
            
            Long clientId = Long.parseLong(clientIdStr);
            
            // Get client name for confirmation message
            ClientDTO client = clientService.getClientById(clientId);
            String clientName = client != null ? client.getFullName() : "Unknown Client";
            
            // Delete client
            boolean success = clientService.deleteClient(clientId);
            
            HttpSession session = request.getSession();
            if (success) {
                session.setAttribute("successMessage", "Client '" + clientName + "' deleted successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to delete client. Client may have active orders.");
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid client ID format");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error deleting client: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
    }
}