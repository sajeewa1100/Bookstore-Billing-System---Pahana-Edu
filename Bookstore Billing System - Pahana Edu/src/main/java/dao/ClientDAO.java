package dao;

import model.ClientDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Client operations
 * Handles all database interactions related to clients
 */
public class ClientDAO {
    
    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());
    
    // Database connection manager
    private ConnectionManager connectionManager;
    
    // SQL Queries
    private static final String INSERT_CLIENT = 
        "INSERT INTO clients (account_number, first_name, last_name, email, phone, " +
        "street, city, state, zip, loyalty_points, tier_level, send_mail_auto, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    
    private static final String SELECT_ALL_CLIENTS = 
        "SELECT * FROM clients ORDER BY created_at DESC";
    
    private static final String SELECT_CLIENT_BY_ID = 
        "SELECT * FROM clients WHERE id = ?";
    
    private static final String UPDATE_CLIENT = 
        "UPDATE clients SET first_name = ?, last_name = ?, email = ?, phone = ?, " +
        "street = ?, city = ?, state = ?, zip = ?, send_mail_auto = ?, updated_at = NOW() WHERE id = ?";
    
    private static final String DELETE_CLIENT = 
        "DELETE FROM clients WHERE id = ?";
    
    private static final String SEARCH_CLIENTS_BY_NAME = 
        "SELECT * FROM clients WHERE CONCAT(first_name, ' ', last_name) LIKE ? ORDER BY created_at DESC";
    
    private static final String SEARCH_CLIENTS_BY_EMAIL = 
        "SELECT * FROM clients WHERE email LIKE ? ORDER BY created_at DESC";
    
    private static final String SEARCH_CLIENTS_BY_PHONE = 
        "SELECT * FROM clients WHERE phone LIKE ? ORDER BY created_at DESC";
    
    private static final String SEARCH_CLIENTS_BY_ID = 
        "SELECT * FROM clients WHERE account_number LIKE ? ORDER BY created_at DESC";
    
    private static final String UPDATE_LOYALTY_POINTS = 
        "UPDATE clients SET loyalty_points = ?, tier_level = ?, updated_at = NOW() WHERE id = ?";
    
    private static final String CHECK_EMAIL_EXISTS = 
        "SELECT COUNT(*) FROM clients WHERE email = ? AND id != ?";
    
    /**
     * Constructor
     */
    public ClientDAO() {
        this.connectionManager = ConnectionManager.getInstance();
    }
    
    /**
     * Get database connection
     */
    private Connection getConnection() {
        return connectionManager.getConnection();
    }
    
    /**
     * Create a new client
     */
    public boolean createClient(ClientDTO client) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = getConnection();
            
            // Generate account number if not exists
            if (client.getAccountNumber() == null || client.getAccountNumber().isEmpty()) {
                client.generateAccountNumber();
            }
            
            stmt = connection.prepareStatement(INSERT_CLIENT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, client.getAccountNumber());
            stmt.setString(2, client.getFirstName());
            stmt.setString(3, client.getLastName());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getPhone());
            stmt.setString(6, client.getStreet());
            stmt.setString(7, client.getCity());
            stmt.setString(8, client.getState());
            stmt.setString(9, client.getZip());
            stmt.setInt(10, client.getLoyaltyPoints());
            stmt.setString(11, client.getTierLevel());
            stmt.setBoolean(12, client.isSendMailAuto());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getLong(1));
                }
                LOGGER.info("Client created successfully: " + client.getFullName());
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating client: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, stmt, generatedKeys);
        }
    }
    
    /**
     * Get all clients
     */
    public List<ClientDTO> getAllClients() {
        List<ClientDTO> clients = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SELECT_ALL_CLIENTS);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ClientDTO client = mapResultSetToClient(rs);
                clients.add(client);
            }
            
            LOGGER.info("Retrieved " + clients.size() + " clients");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving clients: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, rs);
        }
        
        return clients;
    }
    
    /**
     * Get client by ID
     */
    public ClientDTO getClientById(Long id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SELECT_CLIENT_BY_ID);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            
            ClientDTO client = null;
            if (rs.next()) {
                client = mapResultSetToClient(rs);
            }
            
            if (client != null) {
                LOGGER.info("Client found: " + client.getFullName());
            } else {
                LOGGER.warning("Client not found with ID: " + id);
            }
            
            return client;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving client by ID: " + e.getMessage(), e);
            return null;
        } finally {
            closeResources(null, stmt, rs);
        }
    }
    
    /**
     * Update client
     */
    public boolean updateClient(ClientDTO client) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(UPDATE_CLIENT);
            
            stmt.setString(1, client.getFirstName());
            stmt.setString(2, client.getLastName());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getStreet());
            stmt.setString(6, client.getCity());
            stmt.setString(7, client.getState());
            stmt.setString(8, client.getZip());
            stmt.setBoolean(9, client.isSendMailAuto());
            stmt.setLong(10, client.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Client updated successfully: " + client.getFullName());
                return true;
            } else {
                LOGGER.warning("No client found to update with ID: " + client.getId());
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating client: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, stmt, null);
        }
    }
    
    /**
     * Delete client
     */
    public boolean deleteClient(Long id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(DELETE_CLIENT);
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Client deleted successfully with ID: " + id);
                return true;
            } else {
                LOGGER.warning("No client found to delete with ID: " + id);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting client: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, stmt, null);
        }
    }
    
    /**
     * Search clients by criteria
     */
    public List<ClientDTO> searchClients(String searchType, String searchQuery) {
        List<ClientDTO> clients = new ArrayList<>();
        String query = "";
        
        // Determine which query to use based on search type
        switch (searchType.toLowerCase()) {
            case "name":
                query = SEARCH_CLIENTS_BY_NAME;
                searchQuery = "%" + searchQuery + "%";
                break;
            case "email":
                query = SEARCH_CLIENTS_BY_EMAIL;
                searchQuery = "%" + searchQuery + "%";
                break;
            case "phone":
                query = SEARCH_CLIENTS_BY_PHONE;
                searchQuery = "%" + searchQuery + "%";
                break;
            case "id":
                query = SEARCH_CLIENTS_BY_ID;
                searchQuery = "%" + searchQuery + "%";
                break;
            default:
                LOGGER.warning("Invalid search type: " + searchType);
                return clients;
        }
        
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(query);
            stmt.setString(1, searchQuery);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ClientDTO client = mapResultSetToClient(rs);
                clients.add(client);
            }
            
            LOGGER.info("Search returned " + clients.size() + " clients for: " + searchQuery);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching clients: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, rs);
        }
        
        return clients;
    }
    
    /**
     * Update loyalty points
     */
    public boolean updateLoyaltyPoints(Long clientId, int points) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            ClientDTO client = getClientById(clientId);
            if (client == null) {
                return false;
            }
            
            client.setLoyaltyPoints(points);
            
            connection = getConnection();
            stmt = connection.prepareStatement(UPDATE_LOYALTY_POINTS);
            stmt.setInt(1, client.getLoyaltyPoints());
            stmt.setString(2, client.getTierLevel());
            stmt.setLong(3, clientId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Loyalty points updated for client ID: " + clientId);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating loyalty points: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, stmt, null);
        }
    }
    
    /**
     * Check if email exists (for duplicate validation)
     */
    public boolean emailExists(String email, Long excludeId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(CHECK_EMAIL_EXISTS);
            stmt.setString(1, email);
            stmt.setLong(2, excludeId != null ? excludeId : -1);
            
            rs = stmt.executeQuery();
            boolean exists = false;
            
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            
            return exists;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email existence: " + e.getMessage(), e);
            return true; // Return true to be safe and prevent duplicate emails
        } finally {
            closeResources(null, stmt, rs);
        }
    }
    
    /**
     * Map ResultSet to ClientDTO
     */
    private ClientDTO mapResultSetToClient(ResultSet rs) throws SQLException {
        ClientDTO client = new ClientDTO();
        
        client.setId(rs.getLong("id"));
        client.setAccountNumber(rs.getString("account_number"));
        client.setFirstName(rs.getString("first_name"));
        client.setLastName(rs.getString("last_name"));
        client.setEmail(rs.getString("email"));
        client.setPhone(rs.getString("phone"));
        client.setStreet(rs.getString("street"));
        client.setCity(rs.getString("city"));
        client.setState(rs.getString("state"));
        client.setZip(rs.getString("zip"));
        client.setLoyaltyPoints(rs.getInt("loyalty_points"));
        client.setTierLevel(rs.getString("tier_level"));
        client.setSendMailAuto(rs.getBoolean("send_mail_auto"));
        
        // Handle timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            client.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            client.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return client;
    }
    
    /**
     * Close database connection
     */
    public void close() {
        // Note: Since we're using a singleton ConnectionManager,
        // we don't close the connection here as it may be used by other DAOs
        LOGGER.info("ClientDAO cleanup completed");
    }
    
    /**
     * Helper method to close resources
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet: " + e.getMessage(), e);
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing PreparedStatement: " + e.getMessage(), e);
            }
        }
        
        // Note: We don't close the connection here since it's managed by ConnectionManager
        // and may be reused by other operations
    }
}