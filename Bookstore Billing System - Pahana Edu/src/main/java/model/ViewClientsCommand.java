package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ClientService;
import java.io.IOException;
import java.util.List;

public class ViewClientsCommand implements ClientCommand {

    private final ClientService clientService;

    public ViewClientsCommand(ClientService clientService) {
        if (clientService == null) {
            throw new IllegalArgumentException("ClientService cannot be null");
        }
        this.clientService = clientService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get all clients
            List<ClientDTO> clients = clientService.getAllClients();
            
            // Set attributes for JSP
            request.setAttribute("clients", clients);
            request.setAttribute("totalClients", clients.size());
            request.setAttribute("pageTitle", "Client Management");

            // Calculate basic statistics
            setClientStatistics(request, clients);

            // Forward to the clients JSP page
            request.getRequestDispatcher("views/clients.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();

            // Set empty list to prevent JSP errors
            request.setAttribute("clients", List.of());
            request.setAttribute("totalClients", 0);

            // Set error message for display
            request.setAttribute("errorMessage", "Failed to retrieve clients: " + e.getMessage());

            // Forward to the clients JSP page with error message
            request.getRequestDispatcher("views/clients.jsp").forward(request, response);
        }
    }

    /**
     * Calculate and set basic client statistics for display
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

        } catch (Exception e) {
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
     */
    private boolean hasCompleteAddress(ClientDTO client) {
        return client.getStreet() != null && !client.getStreet().trim().isEmpty() &&
                client.getCity() != null && !client.getCity().trim().isEmpty() &&
                client.getState() != null && !client.getState().trim().isEmpty();
    }
}