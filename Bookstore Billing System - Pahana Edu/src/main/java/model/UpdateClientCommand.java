package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.ClientService;
import java.io.IOException;

/**
 * Command to handle updating an existing client
 */
public class UpdateClientCommand implements ClientCommand {
    private final ClientService clientService;

    public UpdateClientCommand(ClientService clientService) {
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
            
            // Get existing client
            ClientDTO existingClient = clientService.getClientById(clientId);
            if (existingClient == null) {
                throw new IllegalArgumentException("Client not found");
            }
            
            // Extract form parameters
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String street = request.getParameter("street");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String zip = request.getParameter("zip");
            String sendMailAutoStr = request.getParameter("sendMailAuto");
            String loyaltyPointsStr = request.getParameter("loyaltyPoints");
            
            // Update client information
            existingClient.setFirstName(firstName != null ? firstName.trim() : "");
            existingClient.setLastName(lastName != null ? lastName.trim() : "");
            existingClient.setEmail(email != null ? email.trim() : "");
            existingClient.setPhone(phone != null ? phone.trim() : "");
            existingClient.setStreet(street != null ? street.trim() : "");
            existingClient.setCity(city != null ? city.trim() : "");
            existingClient.setState(state != null ? state.trim() : "");
            existingClient.setZip(zip != null ? zip.trim() : "");
            existingClient.setSendMailAuto("on".equals(sendMailAutoStr) || "true".equals(sendMailAutoStr));
            
            // Update loyalty points if provided
            if (loyaltyPointsStr != null && !loyaltyPointsStr.trim().isEmpty()) {
                try {
                    int loyaltyPoints = Integer.parseInt(loyaltyPointsStr);
                    existingClient.setLoyaltyPoints(loyaltyPoints);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid loyalty points value");
                }
            }
            
            // Validate updated data
            if (!existingClient.isValid()) {
                throw new IllegalArgumentException("Please fill in all required fields with valid data");
            }
            
            // Update client
            boolean success = clientService.updateClient(existingClient);
            
            HttpSession session = request.getSession();
            if (success) {
                session.setAttribute("successMessage", "Client '" + existingClient.getFullName() + "' updated successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to update client. Please try again.");
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid client ID or loyalty points format");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error updating client: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
    }
}