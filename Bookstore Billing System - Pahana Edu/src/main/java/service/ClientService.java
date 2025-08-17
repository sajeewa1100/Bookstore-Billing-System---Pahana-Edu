package service;



import dao.ClientDAO;
import model.ClientDTO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class ClientService {
    
    private static final Logger LOGGER = Logger.getLogger(ClientService.class.getName());
    
    // ClientDAO instance
    private ClientDAO clientDAO;
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[\\+]?[1-9][\\d]{0,15}$");
    
    private static final Pattern ZIP_PATTERN = 
        Pattern.compile("^\\d{4,10}$");
    
    /**
     * Constructor
     */
    public ClientService() {
        this.clientDAO = new ClientDAO();
    }
    
    /**
     * Add a new client
     */
    public boolean addClient(ClientDTO client) {
        // Validate client data
        String validationError = validateClient(client, true);
        if (validationError != null) {
            LOGGER.warning("Client validation failed: " + validationError);
            throw new IllegalArgumentException(validationError);
        }
        
        try {
            // Check for duplicate email
            if (clientDAO.emailExists(client.getEmail(), null)) {
                throw new IllegalArgumentException("Email already exists: " + client.getEmail());
            }
            
            // Set default values
            client.generateAccountNumber();
            client.setLoyaltyPoints(0);
            client.setTierLevel("BRONZE");
            
            boolean result = clientDAO.createClient(client);
            
            if (result) {
                LOGGER.info("Client added successfully: " + client.getFullName());
            } else {
                LOGGER.warning("Failed to add client: " + client.getFullName());
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while adding client: " + e.getMessage(), e);
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to add client due to database error", e);
        }
    }
    
    /**
     * Get all clients
     */
    public List<ClientDTO> getAllClients() {
        try {
            List<ClientDTO> clients = clientDAO.getAllClients();
            LOGGER.info("Retrieved " + clients.size() + " clients");
            return clients;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while retrieving clients: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve clients due to database error", e);
        }
    }
    
    /**
     * Get client by ID
     */
    public ClientDTO getClientById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid client ID");
        }
        
        try {
            ClientDTO client = clientDAO.getClientById(id);
            
            if (client != null) {
                LOGGER.info("Client found: " + client.getFullName());
            } else {
                LOGGER.info("No client found with ID: " + id);
            }
            
            return client;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while retrieving client: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve client due to database error", e);
        }
    }
    
    /**
     * Update client
     */
    public boolean updateClient(ClientDTO client) {
        if (client.getId() == null || client.getId() <= 0) {
            throw new IllegalArgumentException("Invalid client ID for update");
        }
        
        // Validate client data
        String validationError = validateClient(client, false);
        if (validationError != null) {
            LOGGER.warning("Client validation failed: " + validationError);
            throw new IllegalArgumentException(validationError);
        }
        
        try {
            // Check for duplicate email (excluding current client)
            if (clientDAO.emailExists(client.getEmail(), client.getId())) {
                throw new IllegalArgumentException("Email already exists: " + client.getEmail());
            }
            
            boolean result = clientDAO.updateClient(client);
            
            if (result) {
                LOGGER.info("Client updated successfully: " + client.getFullName());
            } else {
                LOGGER.warning("Failed to update client: " + client.getFullName());
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while updating client: " + e.getMessage(), e);
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to update client due to database error", e);
        }
    }
    
    /**
     * Delete client
     */
    public boolean deleteClient(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid client ID for deletion");
        }
        
        try {
            // Check if client exists before deleting
            ClientDTO existingClient = clientDAO.getClientById(id);
            if (existingClient == null) {
                throw new IllegalArgumentException("Client not found with ID: " + id);
            }
            
            boolean result = clientDAO.deleteClient(id);
            
            if (result) {
                LOGGER.info("Client deleted successfully with ID: " + id);
            } else {
                LOGGER.warning("Failed to delete client with ID: " + id);
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while deleting client: " + e.getMessage(), e);
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to delete client due to database error", e);
        }
    }
    
    /**
     * Search clients
     */
    public List<ClientDTO> searchClients(String searchType, String searchQuery) {
        if (searchType == null || searchType.trim().isEmpty()) {
            throw new IllegalArgumentException("Search type is required");
        }
        
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query is required");
        }
        
        // Validate search type
        String normalizedSearchType = searchType.toLowerCase().trim();
        if (!isValidSearchType(normalizedSearchType)) {
            throw new IllegalArgumentException("Invalid search type: " + searchType);
        }
        
        try {
            List<ClientDTO> clients = clientDAO.searchClients(normalizedSearchType, searchQuery.trim());
            LOGGER.info("Search returned " + clients.size() + " results for: " + searchQuery);
            return clients;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while searching clients: " + e.getMessage(), e);
            throw new RuntimeException("Failed to search clients due to database error", e);
        }
    }
    
    /**
     * Update loyalty points for a client
     */
    public boolean updateLoyaltyPoints(Long clientId, int additionalPoints) {
        if (clientId == null || clientId <= 0) {
            throw new IllegalArgumentException("Invalid client ID");
        }
        
        if (additionalPoints < 0) {
            throw new IllegalArgumentException("Additional points cannot be negative");
        }
        
        try {
            // Get current client data
            ClientDTO client = clientDAO.getClientById(clientId);
            if (client == null) {
                throw new IllegalArgumentException("Client not found with ID: " + clientId);
            }
            
            // Calculate new points total
            int newTotal = client.getLoyaltyPoints() + additionalPoints;
            
            boolean result = clientDAO.updateLoyaltyPoints(clientId, newTotal);
            
            if (result) {
                LOGGER.info("Loyalty points updated for client ID: " + clientId + 
                           " (+" + additionalPoints + " = " + newTotal + ")");
            } else {
                LOGGER.warning("Failed to update loyalty points for client ID: " + clientId);
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while updating loyalty points: " + e.getMessage(), e);
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to update loyalty points due to database error", e);
        }
    }
    
    /**
     * Get client profile data (for profile view)
     */
    public String getClientProfileHtml(Long clientId) {
        ClientDTO client = getClientById(clientId);
        if (client == null) {
            return "<p>Client not found</p>";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<div class='client-profile'>");
        html.append("<div class='profile-header'>");
        html.append("<h4>").append(client.getFullName()).append("</h4>");
        html.append("<p class='account-number'>Account: ").append(client.getAccountNumber()).append("</p>");
        html.append("</div>");
        
        html.append("<div class='profile-details'>");
        html.append("<div class='detail-group'>");
        html.append("<label>Email:</label> <span>").append(client.getEmail()).append("</span>");
        html.append("</div>");
        html.append("<div class='detail-group'>");
        html.append("<label>Phone:</label> <span>").append(client.getPhone()).append("</span>");
        html.append("</div>");
        html.append("<div class='detail-group'>");
        html.append("<label>Address:</label> <span>").append(client.getFullAddress()).append("</span>");
        html.append("</div>");
        html.append("<div class='detail-group'>");
        html.append("<label>Loyalty Points:</label> <span class='loyalty-points'>").append(client.getLoyaltyPoints()).append("</span>");
        html.append("</div>");
        html.append("<div class='detail-group'>");
        html.append("<label>Tier Level:</label> <span class='tier-badge tier-").append(client.getTierLevel().toLowerCase()).append("'>").append(client.getTierLevel()).append("</span>");
        html.append("</div>");
        html.append("<div class='detail-group'>");
        html.append("<label>Auto Email:</label> <span class='auto-mail ").append(client.isSendMailAuto() ? "enabled" : "disabled").append("'>");
        html.append("<i class='fas ").append(client.isSendMailAuto() ? "fa-check" : "fa-times").append("'></i> ");
        html.append(client.isSendMailAuto() ? "Enabled" : "Disabled").append("</span>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        
        return html.toString();
    }
    
    /**
     * Validate client data
     */
    private String validateClient(ClientDTO client, boolean isNewClient) {
        if (client == null) {
            return "Client data is required";
        }
        
        // First name validation
        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            return "First name is required";
        }
        if (client.getFirstName().trim().length() > 50) {
            return "First name cannot exceed 50 characters";
        }
        
        // Last name validation
        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            return "Last name is required";
        }
        if (client.getLastName().trim().length() > 50) {
            return "Last name cannot exceed 50 characters";
        }
        
        // Email validation
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            return "Email is required";
        }
        if (!EMAIL_PATTERN.matcher(client.getEmail().trim()).matches()) {
            return "Invalid email format";
        }
        if (client.getEmail().trim().length() > 100) {
            return "Email cannot exceed 100 characters";
        }
        
        // Phone validation
        if (client.getPhone() == null || client.getPhone().trim().isEmpty()) {
            return "Phone number is required";
        }
        String cleanPhone = client.getPhone().replaceAll("[\\s\\-\\(\\)]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            return "Invalid phone number format";
        }
        
        // Street validation
        if (client.getStreet() == null || client.getStreet().trim().isEmpty()) {
            return "Street address is required";
        }
        if (client.getStreet().trim().length() > 100) {
            return "Street address cannot exceed 100 characters";
        }
        
        // City validation
        if (client.getCity() == null || client.getCity().trim().isEmpty()) {
            return "City is required";
        }
        if (client.getCity().trim().length() > 50) {
            return "City cannot exceed 50 characters";
        }
        
        // State validation
        if (client.getState() == null || client.getState().trim().isEmpty()) {
            return "State is required";
        }
        if (client.getState().trim().length() > 50) {
            return "State cannot exceed 50 characters";
        }
        
        // ZIP validation
        if (client.getZip() == null || client.getZip().trim().isEmpty()) {
            return "ZIP code is required";
        }
        if (!ZIP_PATTERN.matcher(client.getZip().trim()).matches()) {
            return "Invalid ZIP code format";
        }
        
        return null; // No validation errors
    }
    
    /**
     * Check if search type is valid
     */
    private boolean isValidSearchType(String searchType) {
        return "id".equals(searchType) || 
               "name".equals(searchType) || 
               "email".equals(searchType) || 
               "phone".equals(searchType);
    }
    
    /**
     * Get available search types
     */
    public String[] getAvailableSearchTypes() {
        return new String[]{"id", "name", "email", "phone"};
    }
    
    /**
     * Get client statistics
     */
    public ClientStatistics getClientStatistics() {
        List<ClientDTO> allClients = getAllClients();
        
        int totalClients = allClients.size();
        int bronzeClients = 0;
        int silverClients = 0;
        int goldClients = 0;
        int platinumClients = 0;
        int autoMailEnabled = 0;
        
        for (ClientDTO client : allClients) {
            switch (client.getTierLevel().toUpperCase()) {
                case "BRONZE":
                    bronzeClients++;
                    break;
                case "SILVER":
                    silverClients++;
                    break;
                case "GOLD":
                    goldClients++;
                    break;
                case "PLATINUM":
                    platinumClients++;
                    break;
            }
            
            if (client.isSendMailAuto()) {
                autoMailEnabled++;
            }
        }
        
        return new ClientStatistics(totalClients, bronzeClients, silverClients, 
                                  goldClients, platinumClients, autoMailEnabled);
    }
    
    /**
     * Inner class for client statistics
     */
    public static class ClientStatistics {
        private int totalClients;
        private int bronzeClients;
        private int silverClients;
        private int goldClients;
        private int platinumClients;
        private int autoMailEnabled;
        
        public ClientStatistics(int totalClients, int bronzeClients, int silverClients,
                              int goldClients, int platinumClients, int autoMailEnabled) {
            this.totalClients = totalClients;
            this.bronzeClients = bronzeClients;
            this.silverClients = silverClients;
            this.goldClients = goldClients;
            this.platinumClients = platinumClients;
            this.autoMailEnabled = autoMailEnabled;
        }
        
        // Getters
        public int getTotalClients() { return totalClients; }
        public int getBronzeClients() { return bronzeClients; }
        public int getSilverClients() { return silverClients; }
        public int getGoldClients() { return goldClients; }
        public int getPlatinumClients() { return platinumClients; }
        public int getAutoMailEnabled() { return autoMailEnabled; }
    }
}