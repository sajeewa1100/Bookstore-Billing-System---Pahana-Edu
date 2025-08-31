package service;

import dao.ClientDAO;
import model.ClientDTO;

import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private ClientDAO clientDAO;
    private LoyaltySettingsService loyaltySettingsService;

    public ClientService() {
        this.clientDAO = new ClientDAO();
        this.loyaltySettingsService = new LoyaltySettingsService();
    }

    /**
     * Create a new client with proper validation and defaults
     */
    public int createClient(ClientDTO client) {
        System.out.println("ClientService.createClient called for: " + client.getFullName());
        
        try {
            // Validate required fields
            if (!isValidClient(client)) {
                throw new IllegalArgumentException("Invalid client data");
            }
            
            // Check for duplicate phone number
            if (phoneExists(client.getPhone(), 0)) {
                throw new IllegalArgumentException("Phone number already exists");
            }
            
            // Generate account number if not provided
            if (client.getAccountNumber() == null || client.getAccountNumber().trim().isEmpty()) {
                client.setAccountNumber(generateNextAccountNumber());
            }

            // Set default values for new clients
            if (client.getTierLevel() == null || client.getTierLevel().trim().isEmpty()) {
                client.setTierLevel("SILVER"); // Use uppercase for consistency
            }
            
            if (client.getLoyaltyPoints() <= 0) {
                client.setLoyaltyPoints(0);
            }
            
         
            // Set default for sendMailAuto if not specified
            // Assuming the DTO has a proper boolean field, not just checking null
            
            int clientId = clientDAO.createClient(client);
            
            if (clientId > 0) {
                System.out.println("Client created successfully with ID: " + clientId);
                
                // Recalculate tier based on loyalty points (in case points were manually set)
                if (client.getLoyaltyPoints() > 0) {
                    updateClientTierBasedOnPoints(clientId);
                }
            }
            
            return clientId;
            
        } catch (Exception e) {
            System.out.println("Error creating client: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Update existing client with validation
     */
    public boolean updateClient(ClientDTO client) {
        System.out.println("ClientService.updateClient called for ID: " + client.getId());
        
        try {
            // Validate client exists
            ClientDTO existingClient = clientDAO.findById(client.getId());
            if (existingClient == null) {
                throw new IllegalArgumentException("Client not found with ID: " + client.getId());
            }
            
            // Validate required fields
            if (!isValidClient(client)) {
                throw new IllegalArgumentException("Invalid client data");
            }
            
            // Check for duplicate phone number (excluding current client)
            if (phoneExists(client.getPhone(), client.getId())) {
                throw new IllegalArgumentException("Phone number already exists for another client");
            }
            
            // Preserve certain fields that shouldn't be changed through normal updates
            client.setAccountNumber(existingClient.getAccountNumber());
  
            
            // If loyalty points changed, recalculate tier
            if (client.getLoyaltyPoints() != existingClient.getLoyaltyPoints()) {
                String newTier = loyaltySettingsService.calculateClientTier(client.getLoyaltyPoints());
                client.setTierLevel(newTier);
                System.out.println("Tier recalculated based on points: " + newTier);
            }
            
            boolean updated = clientDAO.updateClient(client);
            
            if (updated) {
                System.out.println("Client updated successfully");
            } else {
                System.out.println("Failed to update client");
            }
            
            return updated;
            
        } catch (Exception e) {
            System.out.println("Error updating client: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Delete client with proper validation
     */
    public boolean deleteClient(int clientId) {
        System.out.println("ClientService.deleteClient called for ID: " + clientId);
        
        try {
            // Validate client exists
            ClientDTO client = clientDAO.findById(clientId);
            if (client == null) {
                throw new IllegalArgumentException("Client not found with ID: " + clientId);
            }
            
            // Check if client has any invoices (optional business rule)
            // You might want to prevent deletion if client has transaction history
            // This would require a method in your DAO to check for existing invoices
            
            boolean deleted = clientDAO.deleteClient(clientId);
            
            if (deleted) {
                System.out.println("Client deleted successfully: " + client.getFullName());
            } else {
                System.out.println("Failed to delete client");
            }
            
            return deleted;
            
        } catch (Exception e) {
            System.out.println("Error deleting client: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get client by ID with error handling
     */
    public ClientDTO getClientById(int clientId) {
        System.out.println("ClientService.getClientById called for ID: " + clientId);
        
        try {
            if (clientId <= 0) {
                throw new IllegalArgumentException("Invalid client ID: " + clientId);
            }
            
            ClientDTO client = clientDAO.findById(clientId);
            
            if (client != null) {
                System.out.println("Found client: " + client.getFullName());
            } else {
                System.out.println("No client found with ID: " + clientId);
            }
            
            return client;
            
        } catch (Exception e) {
            System.out.println("Error getting client by ID: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get client by phone with validation
     */
    public ClientDTO getClientByPhone(String phone) {
        System.out.println("ClientService.getClientByPhone called for: " + phone);
        
        try {
            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("Phone number cannot be empty");
            }
            
            // Clean phone number (remove spaces, dashes, etc.)
            String cleanPhone = cleanPhoneNumber(phone);
            
            ClientDTO client = clientDAO.findByPhone(cleanPhone);
            
            if (client != null) {
                System.out.println("Found client by phone: " + client.getFullName());
            } else {
                System.out.println("No client found with phone: " + cleanPhone);
            }
            
            return client;
            
        } catch (Exception e) {
            System.out.println("Error getting client by phone: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get all clients with error handling
     */
    public List<ClientDTO> getAllClients() {
        System.out.println("ClientService.getAllClients called");
        
        try {
            List<ClientDTO> clients = clientDAO.getAllClients();
            System.out.println("Retrieved " + clients.size() + " clients");
            return clients;
            
        } catch (Exception e) {
            System.out.println("Error getting all clients: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Search clients with specific search types: phone (default), name, or ID
     */
    public List<ClientDTO> searchClients(String searchTerm, String searchType) {
        System.out.println("ClientService.searchClients called with term: '" + searchTerm + "', type: '" + searchType + "'");
        
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getAllClients(); // Return all clients if no search term
            }
            
            String cleanSearchTerm = searchTerm.trim();
            List<ClientDTO> results = new ArrayList<>();
            
            // Default to phone search if no type specified
            if (searchType == null || searchType.trim().isEmpty()) {
                searchType = "phone";
            }
            
            switch (searchType.toLowerCase()) {
                case "phone":
                    // Search by phone number - exact match
                    String cleanPhone = cleanPhoneNumber(cleanSearchTerm);
                    ClientDTO clientByPhone = clientDAO.findByPhone(cleanPhone);
                    if (clientByPhone != null) {
                        results.add(clientByPhone);
                    }
                    System.out.println("Phone search returned " + results.size() + " client(s)");
                    break;
                    
                case "name":
                    // Search by name using existing searchClients method (filters for name matches)
                    List<ClientDTO> allResults = clientDAO.searchClients(cleanSearchTerm);
                    // Filter results to only include name matches (first_name or last_name)
                    for (ClientDTO client : allResults) {
                        String fullName = (client.getFirstName() + " " + client.getLastName()).toLowerCase();
                        String firstName = client.getFirstName().toLowerCase();
                        String lastName = client.getLastName().toLowerCase();
                        String searchLower = cleanSearchTerm.toLowerCase();
                        
                        if (firstName.contains(searchLower) || lastName.contains(searchLower) || fullName.contains(searchLower)) {
                            results.add(client);
                        }
                    }
                    System.out.println("Name search returned " + results.size() + " client(s)");
                    break;
                    
                case "id":
                    // Search by client ID - exact match
                    try {
                        int clientId = Integer.parseInt(cleanSearchTerm);
                        ClientDTO clientById = clientDAO.findById(clientId);
                        if (clientById != null) {
                            results.add(clientById);
                        }
                        System.out.println("ID search returned " + results.size() + " client(s)");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format: " + cleanSearchTerm);
                        // results remains empty for invalid ID
                    }
                    break;
                    
                default:
                    // Fallback to phone search
                    String defaultCleanPhone = cleanPhoneNumber(cleanSearchTerm);
                    ClientDTO defaultClientByPhone = clientDAO.findByPhone(defaultCleanPhone);
                    if (defaultClientByPhone != null) {
                        results.add(defaultClientByPhone);
                    }
                    System.out.println("Default phone search returned " + results.size() + " client(s)");
                    break;
            }
            
            return results;
            
        } catch (Exception e) {
            System.out.println("Error searching clients: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Overloaded method for backward compatibility (defaults to phone search)
     */
    public List<ClientDTO> searchClients(String searchTerm) {
        return searchClients(searchTerm, "phone");
    }

    /**
     * Update loyalty points with tier recalculation
     */
    public boolean updateLoyaltyPoints(int clientId, int newPoints) {
        System.out.println("ClientService.updateLoyaltyPoints called for client " + clientId + ": " + newPoints + " points");
        
        try {
            if (clientId <= 0) {
                throw new IllegalArgumentException("Invalid client ID");
            }
            
            if (newPoints < 0) {
                throw new IllegalArgumentException("Loyalty points cannot be negative");
            }
            
            // Get current client data
            ClientDTO client = clientDAO.findById(clientId);
            if (client == null) {
                throw new IllegalArgumentException("Client not found");
            }
            
            // Update points
            boolean pointsUpdated = clientDAO.updateLoyaltyPoints(clientId, newPoints);
            
            if (pointsUpdated) {
                // Recalculate and update tier based on new points
                updateClientTierBasedOnPoints(clientId);
                System.out.println("Loyalty points updated successfully");
            }
            
            return pointsUpdated;
            
        } catch (Exception e) {
            System.out.println("Error updating loyalty points: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Check if phone number exists (excluding specific client)
     */
    public boolean phoneExists(String phone, int excludeClientId) {
        try {
            if (phone == null || phone.trim().isEmpty()) {
                return false;
            }
            
            String cleanPhone = cleanPhoneNumber(phone);
            return clientDAO.phoneExists(cleanPhone, excludeClientId);
            
        } catch (Exception e) {
            System.out.println("Error checking phone existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate next account number
     */
    public String generateNextAccountNumber() {
        try {
            return clientDAO.generateNextAccountNumber();
        } catch (Exception e) {
            System.out.println("Error generating account number: " + e.getMessage());
            // Return a fallback account number based on timestamp
            return "ACC" + System.currentTimeMillis();
        }
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
    /**
     * Validate client data
     */
    private boolean isValidClient(ClientDTO client) {
        if (client == null) {
            System.out.println("Validation failed: Client is null");
            return false;
        }
        
        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            System.out.println("Validation failed: First name is required");
            return false;
        }
        
        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            System.out.println("Validation failed: Last name is required");
            return false;
        }
        
        if (client.getPhone() == null || client.getPhone().trim().isEmpty()) {
            System.out.println("Validation failed: Phone number is required");
            return false;
        }
        
        // Validate phone number format
        String cleanPhone = cleanPhoneNumber(client.getPhone());
        if (!isValidPhoneNumber(cleanPhone)) {
            System.out.println("Validation failed: Invalid phone number format");
            return false;
        }
        
        // Update the client object with cleaned phone
        client.setPhone(cleanPhone);
        
        // Validate email format if provided
        if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
            if (!isValidEmail(client.getEmail())) {
                System.out.println("Validation failed: Invalid email format");
                return false;
            }
        }
        
        // Validate tier level
        if (client.getTierLevel() != null && !client.getTierLevel().trim().isEmpty()) {
            String tier = client.getTierLevel().toUpperCase();
            if (!tier.equals("SILVER") && !tier.equals("GOLD") && !tier.equals("PLATINUM")) {
                System.out.println("Validation failed: Invalid tier level");
                return false;
            }
            client.setTierLevel(tier); // Ensure uppercase
        }
        
        return true;
    }
    
    /**
     * Clean phone number (remove non-digits)
     */
    private String cleanPhoneNumber(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^0-9]", "");
    }
    
    /**
     * Validate phone number format
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.length() < 10 || phone.length() > 15) {
            return false;
        }
        return phone.matches("^[0-9]+$");
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Update client tier based on current loyalty points
     */
    private void updateClientTierBasedOnPoints(int clientId) {
        try {
            ClientDTO client = clientDAO.findById(clientId);
            if (client == null) return;
            
            String currentTier = client.getTierLevel();
            String newTier = loyaltySettingsService.calculateClientTier(client.getLoyaltyPoints());
            
            if (!newTier.equals(currentTier)) {
                client.setTierLevel(newTier);
                clientDAO.updateClient(client);
                
                System.out.println("Client tier updated: " + currentTier + " -> " + newTier + 
                                 " (Points: " + client.getLoyaltyPoints() + ")");
            }
            
        } catch (Exception e) {
            System.out.println("Error updating client tier: " + e.getMessage());
        }
    }
}