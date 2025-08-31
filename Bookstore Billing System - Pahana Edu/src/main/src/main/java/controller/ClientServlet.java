package controller;

import model.ClientDTO;
import service.ClientService;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/clients")
public class ClientServlet extends HttpServlet {
   
	private static final long serialVersionUID = 1L;
	private ClientService clientService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        clientService = new ClientService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("ClientServlet GET - Action: " + action);
        
        try {
            if ("new".equals(action)) {
                showNewClientForm(request, response);
            } else if ("edit".equals(action)) {
                showEditClientForm(request, response);
            } else if ("view".equals(action)) {
                showClientProfile(request, response);
            } else {
                // Default: show client list with optional search
                showClientList(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error in ClientServlet GET: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            showClientList(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("ClientServlet POST - Action: " + action);
        
        try {
            if ("create".equals(action)) {
                createClient(request, response);
            } else if ("update".equals(action)) {
                updateClient(request, response);
            } else if ("delete".equals(action)) {
                deleteClient(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (Exception e) {
            System.err.println("Error in ClientServlet POST: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            showClientList(request, response);
        }
    }
    
    // ===== GET REQUEST HANDLERS =====
    
    private void showNewClientForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Showing new client form");
        
        // Load statistics for header
        loadClientStatistics(request);
        
        request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
    }
    
    private void showEditClientForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        System.out.println("Showing edit client form for ID: " + idParam);
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Client ID is required");
            showClientList(request, response);
            return;
        }
        
        try {
            int clientId = Integer.parseInt(idParam);
            ClientDTO client = clientService.getClientById(clientId);
            
            if (client == null) {
                request.setAttribute("errorMessage", "Client not found with ID: " + clientId);
                showClientList(request, response);
                return;
            }
            
            request.setAttribute("client", client);
            loadClientStatistics(request);
            
            request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid client ID format");
            showClientList(request, response);
        }
    }
    
    private void showClientProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        System.out.println("Showing client profile for ID: " + idParam);
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Client ID is required");
            showClientList(request, response);
            return;
        }
        
        try {
            int clientId = Integer.parseInt(idParam);
            ClientDTO client = clientService.getClientById(clientId);
            
            if (client == null) {
                request.setAttribute("errorMessage", "Client not found with ID: " + clientId);
                showClientList(request, response);
                return;
            }
            
            request.setAttribute("client", client);
            loadClientStatistics(request);
            
            request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid client ID format");
            showClientList(request, response);
        }
    }
    
    private void showClientList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Showing client list");
        
        String searchTerm = request.getParameter("search");
        String searchType = request.getParameter("searchType");
        
        List<ClientDTO> clients;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            System.out.println("Searching clients with term: '" + searchTerm + "', type: '" + searchType + "'");
            clients = clientService.searchClients(searchTerm.trim());
        } else {
            clients = clientService.getAllClients();
        }
        
        request.setAttribute("clients", clients);
        loadClientStatistics(request);
        
        request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
    }
    
    // ===== POST REQUEST HANDLERS =====
    
    private void createClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Creating new client");
        
        try {
            ClientDTO client = buildClientFromRequest(request, true);
            
            // Server-side validation
            String validationError = validateClient(client, true);
            if (validationError != null) {
                request.setAttribute("errorMessage", validationError);
                request.setAttribute("client", client); // Preserve form data
                loadClientStatistics(request);
                request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
                return;
            }
            
            int clientId = clientService.createClient(client);
            
            if (clientId > 0) {
                System.out.println("Client created successfully with ID: " + clientId);
                request.setAttribute("successMessage", 
                    "Client '" + client.getFullName() + "' has been created successfully!");
                response.sendRedirect("clients");
            } else {
                request.setAttribute("errorMessage", "Failed to create client. Please try again.");
                request.setAttribute("client", client);
                loadClientStatistics(request);
                request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            loadClientStatistics(request);
            request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
        }
    }
    
    private void updateClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Updating client");
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Client ID is required for update");
            showClientList(request, response);
            return;
        }
        
        try {
            int clientId = Integer.parseInt(idParam);
            ClientDTO client = buildClientFromRequest(request, false);
            client.setId(clientId);
            
            // Server-side validation
            String validationError = validateClient(client, false);
            if (validationError != null) {
                request.setAttribute("errorMessage", validationError);
                request.setAttribute("client", client);
                loadClientStatistics(request);
                request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
                return;
            }
            
            boolean updated = clientService.updateClient(client);
            
            if (updated) {
                System.out.println("Client updated successfully");
                request.setAttribute("successMessage", 
                    "Client '" + client.getFullName() + "' has been updated successfully!");
                response.sendRedirect("clients");
            } else {
                request.setAttribute("errorMessage", "Failed to update client. Please try again.");
                request.setAttribute("client", client);
                loadClientStatistics(request);
                request.getRequestDispatcher("/views/clients.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid client ID format");
            showClientList(request, response);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            showClientList(request, response);
        }
    }
    
    private void deleteClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        System.out.println("Deleting client with ID: " + idParam);
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Client ID is required for deletion");
            showClientList(request, response);
            return;
        }
        
        try {
            int clientId = Integer.parseInt(idParam);
            
            // Get client name before deletion for success message
            ClientDTO client = clientService.getClientById(clientId);
            if (client == null) {
                request.setAttribute("errorMessage", "Client not found with ID: " + clientId);
                showClientList(request, response);
                return;
            }
            
            boolean deleted = clientService.deleteClient(clientId);
            
            if (deleted) {
                System.out.println("Client deleted successfully");
                request.setAttribute("successMessage", 
                    "Client '" + client.getFullName() + "' has been deleted successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to delete client. Please try again.");
            }
            
            response.sendRedirect("clients");
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid client ID format");
            showClientList(request, response);
        }
    }
    
    // ===== HELPER METHODS =====
    
    private ClientDTO buildClientFromRequest(HttpServletRequest request, boolean isNew) {
        ClientDTO client = new ClientDTO();
        
        // Basic information
        client.setFirstName(getStringParameter(request, "firstName"));
        client.setLastName(getStringParameter(request, "lastName"));
        client.setEmail(getStringParameter(request, "email"));
        client.setPhone(getStringParameter(request, "phone"));
        
        // Address information
        client.setAddressStreet(getStringParameter(request, "addressStreet"));
        client.setAddressCity(getStringParameter(request, "addressCity"));
        client.setAddressState(getStringParameter(request, "addressState"));
        client.setAddressZip(getStringParameter(request, "addressZip"));
        
        // Account settings
        if (!isNew) {
            client.setAccountNumber(getStringParameter(request, "accountNumber"));
            client.setTierLevel(getStringParameter(request, "tierLevel"));
            
            // Loyalty points
            String loyaltyPointsParam = request.getParameter("loyaltyPoints");
            if (loyaltyPointsParam != null && !loyaltyPointsParam.trim().isEmpty()) {
                try {
                    client.setLoyaltyPoints(Integer.parseInt(loyaltyPointsParam));
                } catch (NumberFormatException e) {
                    client.setLoyaltyPoints(0);
                }
            }
        }
        
        // Send mail auto (checkbox)
        String sendMailAuto = request.getParameter("sendMailAuto");
        client.setSendMailAuto("true".equals(sendMailAuto));
        
        return client;
    }
    
    private String getStringParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }
    
    private String validateClient(ClientDTO client, boolean isNew) {
        // Required field validation
        if (client.getFirstName() == null || client.getFirstName().isEmpty()) {
            return "First name is required";
        }
        
        if (client.getLastName() == null || client.getLastName().isEmpty()) {
            return "Last name is required";
        }
        
        if (client.getPhone() == null || client.getPhone().isEmpty()) {
            return "Phone number is required";
        }
        
        // Phone format validation
        String cleanPhone = client.getPhone().replaceAll("[^0-9]", "");
        if (cleanPhone.length() < 10 || cleanPhone.length() > 15) {
            return "Phone number must be 10-15 digits";
        }
        
        // Email validation (if provided)
        if (client.getEmail() != null && !client.getEmail().isEmpty()) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!client.getEmail().matches(emailRegex)) {
                return "Please enter a valid email address";
            }
        }
        
        // Phone uniqueness validation
        int excludeId = isNew ? 0 : client.getId();
        if (clientService.phoneExists(cleanPhone, excludeId)) {
            return "Phone number already exists for another client";
        }
        
        // Tier level validation (for updates)
        if (!isNew && client.getTierLevel() != null) {
            String tier = client.getTierLevel().toUpperCase();
            if (!tier.equals("SILVER") && !tier.equals("GOLD") && !tier.equals("PLATINUM")) {
                return "Invalid tier level";
            }
        }
        
        // Loyalty points validation (for updates)
        if (!isNew && client.getLoyaltyPoints() < 0) {
            return "Loyalty points cannot be negative";
        }
        
        return null; // No validation errors
    }
    
    private void loadClientStatistics(HttpServletRequest request) {
        try {
            List<ClientDTO> allClients = clientService.getAllClients();
            
            // Calculate statistics
            int totalClients = allClients.size();
            
            Map<String, Long> tierCounts = allClients.stream()
                .collect(Collectors.groupingBy(
                    client -> client.getTierLevel() != null ? client.getTierLevel() : "SILVER",
                    Collectors.counting()
                ));
            
            int silverClients = tierCounts.getOrDefault("SILVER", 0L).intValue();
            int goldClients = tierCounts.getOrDefault("GOLD", 0L).intValue();
            int platinumClients = tierCounts.getOrDefault("PLATINUM", 0L).intValue();
            
            request.setAttribute("totalClients", totalClients);
            request.setAttribute("silverClients", silverClients);
            request.setAttribute("goldClients", goldClients);
            request.setAttribute("platinumClients", platinumClients);
            
            System.out.println("Client statistics loaded: Total=" + totalClients + 
                             ", Silver=" + silverClients + ", Gold=" + goldClients + 
                             ", Platinum=" + platinumClients);
            
        } catch (Exception e) {
            System.err.println("Error loading client statistics: " + e.getMessage());
            // Set default values
            request.setAttribute("totalClients", 0);
            request.setAttribute("silverClients", 0);
            request.setAttribute("goldClients", 0);
            request.setAttribute("platinumClients", 0);
        }
    }
}