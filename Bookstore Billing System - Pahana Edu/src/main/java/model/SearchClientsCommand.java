package model;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ClientService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchClientsCommand implements ClientCommand {

    private static final Logger LOGGER = Logger.getLogger(SearchClientsCommand.class.getName());
    private final ClientService clientService;

    public SearchClientsCommand(ClientService clientService) {
        if (clientService == null) {
            throw new IllegalArgumentException("ClientService cannot be null");
        }
        this.clientService = clientService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchType = request.getParameter("searchType");
        String searchQuery = request.getParameter("searchQuery");

        LOGGER.info("SearchClientsCommand: Executing search - Type: " + searchType + ", Query: " + searchQuery);

        try {
            List<ClientDTO> clients;
            
            if (searchQuery == null || searchQuery.trim().isEmpty()) {
                // If no search query, return all clients
                clients = clientService.getAllClients();
                LOGGER.info("SearchClientsCommand: Empty search query, returning all clients");
            } else {
                // Perform search
                clients = clientService.searchClients(searchType, searchQuery);
                LOGGER.info("SearchClientsCommand: Found " + clients.size() + " clients matching search criteria");
            }

            // Set search results and parameters
            request.setAttribute("clients", clients);
            request.setAttribute("searchType", searchType);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("totalClients", clients.size());

            // Calculate search statistics
            setSearchStatistics(request, clients);

            // Forward to JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("views/clients.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "SearchClientsCommand: Error performing search - " + e.getMessage(), e);
            
            // Set empty list to prevent JSP errors
            request.setAttribute("clients", List.of());
            request.setAttribute("totalClients", 0);
            request.setAttribute("searchType", searchType);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("errorMessage", "Error performing search: " + e.getMessage());

            // Forward to JSP with error
            RequestDispatcher dispatcher = request.getRequestDispatcher("views/clients.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void setSearchStatistics(HttpServletRequest request, List<ClientDTO> clients) {
        try {
            int totalClients = clients.size();
            int autoMailEnabled = 0;
            int clientsWithPhone = 0;
            int clientsWithAddress = 0;

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

            request.setAttribute("autoMailEnabledCount", autoMailEnabled);
            request.setAttribute("clientsWithPhoneCount", clientsWithPhone);
            request.setAttribute("clientsWithAddressCount", clientsWithAddress);

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
            LOGGER.log(Level.WARNING, "SearchClientsCommand: Error calculating statistics - " + e.getMessage(), e);
            // Set default values if calculation fails
            request.setAttribute("autoMailEnabledCount", 0);
            request.setAttribute("clientsWithPhoneCount", 0);
            request.setAttribute("clientsWithAddressCount", 0);
            request.setAttribute("autoMailPercentage", 0);
            request.setAttribute("phonePercentage", 0);
            request.setAttribute("addressPercentage", 0);
        }
    }

    private boolean hasCompleteAddress(ClientDTO client) {
        return client.getStreet() != null && !client.getStreet().trim().isEmpty() &&
                client.getCity() != null && !client.getCity().trim().isEmpty() &&
                client.getState() != null && !client.getState().trim().isEmpty();
    }
}