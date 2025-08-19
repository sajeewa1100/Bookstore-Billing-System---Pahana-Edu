package service;

import dao.ClientDAO;
import model.ClientDTO;
import model.TierDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientService {
    
    private static final Logger LOGGER = Logger.getLogger(ClientService.class.getName());
    private ClientDAO clientDAO;
    private TierService tierService;

    public ClientService() {
        this.clientDAO = new ClientDAO();
        this.tierService = new TierService();
    }

    /**
     * Get all clients with tier information
     */
    public List<ClientDTO> getAllClients() {
        try {
            List<ClientDTO> clients = clientDAO.getAllClients();
            LOGGER.info("ClientService: Retrieved " + clients.size() + " clients");
            return clients;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error getting all clients", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get client by ID
     */
    public ClientDTO getClientById(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }

        try {
            ClientDTO client = clientDAO.getClientById(clientId);
            if (client != null) {
                LOGGER.info("ClientService: Retrieved client - " + client.getFullName());
            }
            return client;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error getting client by ID: " + clientId, e);
            return null;
        }
    }

    /**
     * Add new client with automatic tier assignment
     */
    public boolean addClient(ClientDTO client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }

        try {
            // Generate account number
            client.setAccountNumber(generateAccountNumber());
            
            // Assign tier based on loyalty points (0 for new clients = Bronze)
            assignTierToClient(client);

            boolean success = clientDAO.addClient(client);
            
            if (success) {
                LOGGER.info("ClientService: Client added successfully - " + client.getFullName() + 
                           " (Tier: " + client.getTierLevel() + ")");
            }
            
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error adding client", e);
            return false;
        }
    }

    /**
     * Update client
     */
    public boolean updateClient(ClientDTO client) {
        if (client == null || client.getId() == null) {
            throw new IllegalArgumentException("Client and client ID cannot be null");
        }

        try {
            // Keep existing loyalty points and recalculate tier
            ClientDTO existing = clientDAO.getClientById(client.getId());
            if (existing != null) {
                client.setLoyaltyPoints(existing.getLoyaltyPoints());
            }
            assignTierToClient(client);

            boolean success = clientDAO.updateClient(client);
            
            if (success) {
                LOGGER.info("ClientService: Client updated successfully - " + client.getFullName() + 
                           " (Tier: " + client.getTierLevel() + ")");
            }
            
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error updating client", e);
            return false;
        }
    }

    /**
     * Delete client
     */
    public boolean deleteClient(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }

        try {
            boolean success = clientDAO.deleteClient(clientId);
            
            if (success) {
                LOGGER.info("ClientService: Client deleted successfully - ID: " + clientId);
            }
            
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error deleting client", e);
            return false;
        }
    }

    /**
     * Search clients
     */
    public List<ClientDTO> searchClients(String searchType, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return getAllClients();
        }

        try {
            List<ClientDTO> clients = clientDAO.searchClients(searchType, searchQuery);
            LOGGER.info("ClientService: Found " + clients.size() + " clients matching search criteria");
            return clients;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error searching clients", e);
            return new ArrayList<>();
        }
    }

    /**
     * Update loyalty points (called from billing)
     */
    public boolean updateLoyaltyPoints(Long clientId, int additionalPoints) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }

        try {
            ClientDTO client = clientDAO.getClientById(clientId);
            if (client == null) {
                return false;
            }

            int newPoints = client.getLoyaltyPoints() + additionalPoints;
            
            // Find new tier for updated points
            TierDTO newTier = tierService.getTierForPoints(newPoints);
            
            boolean success = clientDAO.updateClientLoyaltyPointsAndTier(
                clientId, 
                newPoints, 
                newTier != null ? newTier.getId() : 1L
            );
            
            if (success) {
                LOGGER.info("ClientService: Updated loyalty points for client " + clientId + 
                          ": " + newPoints + " points, Tier: " + 
                          (newTier != null ? newTier.getTierName() : "Bronze"));
            }
            
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error updating loyalty points", e);
            return false;
        }
    }

    /**
     * Get total clients count
     */
    public int getTotalClientsCount() {
        try {
            return clientDAO.getTotalClientsCount();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error getting client count", e);
            return 0;
        }
    }

    /**
     * Generate account number
     */
    private String generateAccountNumber() {
        try {
            int year = java.time.Year.now().getValue();
            int nextSequence = clientDAO.getNextAccountSequence();
            return String.format("ACC-%d-%04d", year, nextSequence);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "ClientService: Error generating account number, using fallback", e);
            return "ACC-" + System.currentTimeMillis();
        }
    }

    /**
     * Assign tier to client based on loyalty points
     */
    private void assignTierToClient(ClientDTO client) {
        try {
            int points = client.getLoyaltyPoints() != null ? client.getLoyaltyPoints() : 0;
            TierDTO tier = tierService.getTierForPoints(points);
            
            if (tier != null) {
                client.setTierId(tier.getId());
                client.setTierLevel(tier.getTierName());
            } else {
                client.setTierId(1L); // Default to Bronze
                client.setTierLevel("Bronze");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "ClientService: Error assigning tier to client", e);
            client.setTierId(1L);
            client.setTierLevel("Bronze");
        }
    }

    /**
     * Get client profile HTML for AJAX
     */
    public String getClientProfileHtml(Long clientId) {
        try {
            ClientDTO client = getClientById(clientId);
            if (client == null) {
                return "<p class='error'>Client not found</p>";
            }

            StringBuilder html = new StringBuilder();
            html.append("<div class='client-profile'>");
            html.append("<div class='profile-header'>");
            html.append("<h4>").append(client.getFullName()).append("</h4>");
            html.append("<span class='account-number'>Account: ").append(client.getAccountNumber()).append("</span>");
            html.append("</div>");
            
            html.append("<div class='profile-details'>");
            html.append("<div class='detail-row'>");
            html.append("<span class='label'>Email:</span>");
            html.append("<span class='value'>").append(client.getEmail()).append("</span>");
            html.append("</div>");
            
            html.append("<div class='detail-row'>");
            html.append("<span class='label'>Phone:</span>");
            html.append("<span class='value'>").append(client.getPhone()).append("</span>");
            html.append("</div>");
            
            html.append("<div class='detail-row'>");
            html.append("<span class='label'>Address:</span>");
            html.append("<span class='value'>").append(getFullAddress(client)).append("</span>");
            html.append("</div>");
            
            html.append("<div class='detail-row'>");
            html.append("<span class='label'>Loyalty Points:</span>");
            html.append("<span class='value loyalty-points'>").append(client.getLoyaltyPoints()).append("</span>");
            html.append("</div>");
            
            html.append("<div class='detail-row'>");
            html.append("<span class='label'>Tier Level:</span>");
            html.append("<span class='value tier-badge tier-").append(client.getTierLevel().toLowerCase()).append("'>");
            html.append(client.getTierLevel());
            html.append("</span>");
            html.append("</div>");
            
            html.append("<div class='detail-row'>");
            html.append("<span class='label'>Auto Mail:</span>");
            html.append("<span class='value auto-mail ").append(client.isSendMailAuto() ? "enabled" : "disabled").append("'>");
            html.append("<i class='fas ").append(client.isSendMailAuto() ? "fa-check" : "fa-times").append("'></i> ");
            html.append(client.isSendMailAuto() ? "Enabled" : "Disabled");
            html.append("</span>");
            html.append("</div>");
            
            html.append("</div>");
            html.append("</div>");

            return html.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ClientService: Error generating client profile HTML", e);
            return "<p class='error'>Error loading client profile</p>";
        }
    }

    /**
     * Get full address string
     */
    private String getFullAddress(ClientDTO client) {
        StringBuilder address = new StringBuilder();
        if (client.getStreet() != null && !client.getStreet().trim().isEmpty()) {
            address.append(client.getStreet());
        }
        if (client.getCity() != null && !client.getCity().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(client.getCity());
        }
        if (client.getState() != null && !client.getState().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(client.getState());
        }
        if (client.getZip() != null && !client.getZip().trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(client.getZip());
        }
        return address.toString();
    }
}