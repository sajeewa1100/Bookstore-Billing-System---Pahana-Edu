package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ClientService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command implementation for viewing clients.
 * This command handles the display of all clients in the system.
 */
public class ViewClientsCommand implements ClientCommand {
    
    private static final Logger LOGGER = Logger.getLogger(ViewClientsCommand.class.getName());
    private final ClientService clientService;
    
    /**
     * Constructor for ViewClientsCommand
     * 
     * @param clientService The ClientService instance to use for client operations
     * @throws IllegalArgumentException if clientService is null
     */
    public ViewClientsCommand(ClientService clientService) {
        if (clientService == null) {
            throw new IllegalArgumentException("ClientService cannot be null");
        }
        this.clientService = clientService;
    }
    
    /**
     * Execute the view clients command
     * 
     * @param request The HttpServletRequest 
     * @param response The HttpServletResponse for forwarding to JSP
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        LOGGER.info("ViewClientsCommand: Executing view clients command");
        
        try {
            // Retrieve all clients from the service
            List<ClientDTO> clients = clientService.getAllClients();
            
            // Set clients list as request attribute for JSP
            request.setAttribute("clients", clients);
            
            // Log the operation
            LOGGER.info("ViewClientsCommand: Retrieved " + clients.size() + " clients successfully");
            
            // Additional attributes for JSP display
            request.setAttribute("totalClients", clients.size());
            request.setAttribute("pageTitle", "Client Management");
            
            // Calculate some basic statistics for display
            setClientStatistics(request, clients);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ViewClientsCommand: Error retrieving clients - " + e.getMessage(), e);
            
            // Set empty list to prevent JSP errors
            request.setAttribute("clients", List.of());
            request.setAttribute("totalClients", 0);
            
            // Set error message for display
            request.setAttribute("errorMessage", "Failed to retrieve clients: " + e.getMessage());
        }
        
        // Forward to the clients JSP page
        request.getRequestDispatcher("views/clients.jsp").forward(request, response);
    }
    
    /**
     * Calculate and set basic client statistics for display
     * 
     * @param request The HttpServletRequest to set attributes on
     * @param clients The list of clients to analyze
     */
    private void setClientStatistics(HttpServletRequest request, List<ClientDTO> clients) {
        try {
            int totalClients = clients.size();
            int autoMailEnabled = 0;
            int clientsWithPhone = 0;
            int clientsWithAddress = 0;
            
            // Count clients with various attributes
            for (ClientDTO client : clients) {
                if (client.isSendMailAuto()) {
                    autoMailEnabled++;
                }
                
                if (client.getPhone() != null && !client.getPhone().trim().isEmpty()) {
                    clientsWithPhone++;
                }
                
                if (hasCompleteAddress(client)) {
                    clientsWithAddress++;
                }
            }
            
            // Set statistics as request attributes
            request.setAttribute("autoMailEnabledCount", autoMailEnabled);
            request.setAttribute("clientsWithPhoneCount", clientsWithPhone);
            request.setAttribute("clientsWithAddressCount", clientsWithAddress);
            
            // Calculate percentages
            if (totalClients > 0) {
                request.setAttribute("autoMailPercentage", Math.round((autoMailEnabled * 100.0) / totalClients));
                request.setAttribute("phonePercentage", Math.round((clientsWithPhone * 100.0) / totalClients));
                request.setAttribute("addressPercentage", Math.round((clientsWithAddress * 100.0) / totalClients));
            } else {
                request.setAttribute("autoMailPercentage", 0);
                request.setAttribute("phonePercentage", 0);
                request.setAttribute("addressPercentage", 0);
            }
            
            LOGGER.info("ViewClientsCommand: Statistics calculated - Total: " + totalClients + 
                       ", AutoMail: " + autoMailEnabled + ", WithPhone: " + clientsWithPhone + 
                       ", WithAddress: " + clientsWithAddress);
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "ViewClientsCommand: Error calculating statistics - " + e.getMessage(), e);
            // Set default values if calculation fails
            request.setAttribute("autoMailEnabledCount", 0);
            request.setAttribute("clientsWithPhoneCount", 0);
            request.setAttribute("clientsWithAddressCount", 0);
            request.setAttribute("autoMailPercentage", 0);
            request.setAttribute("phonePercentage", 0);
            request.setAttribute("addressPercentage", 0);
        }
    }
    
    /**
     * Check if client has a complete address
     * 
     * @param client The ClientDTO to check
     * @return true if client has street, city, and state
     */
    private boolean hasCompleteAddress(ClientDTO client) {
        return client.getStreet() != null && !client.getStreet().trim().isEmpty() &&
               client.getCity() != null && !client.getCity().trim().isEmpty() &&
               client.getState() != null && !client.getState().trim().isEmpty();
    }
}