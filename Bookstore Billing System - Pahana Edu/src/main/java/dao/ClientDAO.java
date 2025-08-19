package dao;

import model.ClientDTO;
import model.ClientStatisticsDTO;
import model.TierDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAO {

    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    /**
     * Get all clients with tier information
     */
    public List<ClientDTO> getAllClients() throws SQLException {
        List<ClientDTO> clients = new ArrayList<>();
        String sql = """
            SELECT c.*, t.tier_name, t.min_points, t.max_points, t.discount_rate 
            FROM clients c 
            LEFT JOIN tiers t ON c.tier_id = t.id 
            ORDER BY c.created_at DESC
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ClientDTO client = mapResultSetToClient(rs);
                clients.add(client);
            }

            LOGGER.info("ClientDAO: Retrieved " + clients.size() + " clients from database");
            return clients;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error retrieving all clients", e);
            throw e;
        }
    }

    /**
     * Get client by ID with tier information
     */
    public ClientDTO getClientById(Long clientId) throws SQLException {
        String sql = """
            SELECT c.*, t.tier_name, t.min_points, t.max_points, t.discount_rate 
            FROM clients c 
            LEFT JOIN tiers t ON c.tier_id = t.id 
            WHERE c.id = ?
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, clientId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ClientDTO client = mapResultSetToClient(rs);
                    LOGGER.info("ClientDAO: Retrieved client by ID: " + clientId);
                    return client;
                }
            }

            LOGGER.warning("ClientDAO: Client not found with ID: " + clientId);
            return null;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error retrieving client by ID: " + clientId, e);
            throw e;
        }
    }

    /**
     * Add a new client
     */
    public boolean addClient(ClientDTO client) throws SQLException {
        String sql = """
            INSERT INTO clients (account_number, first_name, last_name, email, phone, 
                               street, city, state, zip, loyalty_points, tier_id, send_mail_auto) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, client.getAccountNumber());
            pstmt.setString(2, client.getFirstName());
            pstmt.setString(3, client.getLastName());
            pstmt.setString(4, client.getEmail());
            pstmt.setString(5, client.getPhone());
            pstmt.setString(6, client.getStreet());
            pstmt.setString(7, client.getCity());
            pstmt.setString(8, client.getState());
            pstmt.setString(9, client.getZip());
            pstmt.setInt(10, client.getLoyaltyPoints() != null ? client.getLoyaltyPoints() : 0);
            
            if (client.getTierId() != null) {
                pstmt.setLong(11, client.getTierId());
            } else {
                pstmt.setNull(11, Types.BIGINT);
            }
            
            pstmt.setBoolean(12, client.getSendMailAuto() != null ? client.getSendMailAuto() : true);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        client.setId(generatedKeys.getLong(1));
                    }
                }
                LOGGER.info("ClientDAO: Client added successfully - " + client.getFullName());
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error adding client", e);
            throw e;
        }
    }

    /**
     * Update an existing client
     */
    public boolean updateClient(ClientDTO client) throws SQLException {
        String sql = """
            UPDATE clients SET first_name = ?, last_name = ?, email = ?, phone = ?, 
                             street = ?, city = ?, state = ?, zip = ?, 
                             loyalty_points = ?, tier_id = ?, send_mail_auto = ? 
            WHERE id = ?
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getFirstName());
            pstmt.setString(2, client.getLastName());
            pstmt.setString(3, client.getEmail());
            pstmt.setString(4, client.getPhone());
            pstmt.setString(5, client.getStreet());
            pstmt.setString(6, client.getCity());
            pstmt.setString(7, client.getState());
            pstmt.setString(8, client.getZip());
            pstmt.setInt(9, client.getLoyaltyPoints() != null ? client.getLoyaltyPoints() : 0);
            
            if (client.getTierId() != null) {
                pstmt.setLong(10, client.getTierId());
            } else {
                pstmt.setNull(10, Types.BIGINT);
            }
            
            pstmt.setBoolean(11, client.getSendMailAuto() != null ? client.getSendMailAuto() : true);
            pstmt.setLong(12, client.getId());

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("ClientDAO: Client updated successfully - " + client.getFullName());
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error updating client", e);
            throw e;
        }
    }

    /**
     * Update client loyalty points and tier
     */
    public boolean updateClientLoyaltyPointsAndTier(Long clientId, int loyaltyPoints, Long tierId) throws SQLException {
        String sql = "UPDATE clients SET loyalty_points = ?, tier_id = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loyaltyPoints);
            if (tierId != null) {
                pstmt.setLong(2, tierId);
            } else {
                pstmt.setNull(2, Types.BIGINT);
            }
            pstmt.setLong(3, clientId);

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("ClientDAO: Updated loyalty points and tier for client ID: " + clientId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error updating client loyalty points and tier", e);
            throw e;
        }
    }

    /**
     * Update client tier only
     */
    public boolean updateClientTier(Long clientId, Long tierId) throws SQLException {
        String sql = "UPDATE clients SET tier_id = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (tierId != null) {
                pstmt.setLong(1, tierId);
            } else {
                pstmt.setNull(1, Types.BIGINT);
            }
            pstmt.setLong(2, clientId);

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("ClientDAO: Updated tier for client ID: " + clientId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error updating client tier", e);
            throw e;
        }
    }

    /**
     * Delete a client
     */
    public boolean deleteClient(Long clientId) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, clientId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("ClientDAO: Client deleted successfully - ID: " + clientId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error deleting client", e);
            throw e;
        }
    }

    /**
     * Search clients with tier information
     */
    public List<ClientDTO> searchClients(String searchType, String searchQuery) throws SQLException {
        List<ClientDTO> clients = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT c.*, t.tier_name, t.min_points, t.max_points, t.discount_rate 
            FROM clients c 
            LEFT JOIN tiers t ON c.tier_id = t.id 
            WHERE 
            """);

        // Build WHERE clause based on search type
        switch (searchType.toLowerCase()) {
            case "phone":
                sql.append("c.phone LIKE ?");
                break;
            case "name":
                sql.append("(c.first_name LIKE ? OR c.last_name LIKE ? OR CONCAT(c.first_name, ' ', c.last_name) LIKE ?)");
                break;
            case "email":
                sql.append("c.email LIKE ?");
                break;
            case "id":
                sql.append("c.account_number LIKE ?");
                break;
            default:
                sql.append("(c.first_name LIKE ? OR c.last_name LIKE ? OR c.email LIKE ? OR c.phone LIKE ? OR c.account_number LIKE ?)");
                break;
        }

        sql.append(" ORDER BY c.created_at DESC");

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            String searchPattern = "%" + searchQuery + "%";

            // Set parameters based on search type
            switch (searchType.toLowerCase()) {
                case "phone":
                case "email":
                case "id":
                    pstmt.setString(1, searchPattern);
                    break;
                case "name":
                    pstmt.setString(1, searchPattern);
                    pstmt.setString(2, searchPattern);
                    pstmt.setString(3, searchPattern);
                    break;
                default:
                    // General search
                    pstmt.setString(1, searchPattern);
                    pstmt.setString(2, searchPattern);
                    pstmt.setString(3, searchPattern);
                    pstmt.setString(4, searchPattern);
                    pstmt.setString(5, searchPattern);
                    break;
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ClientDTO client = mapResultSetToClient(rs);
                    clients.add(client);
                }
            }

            LOGGER.info("ClientDAO: Found " + clients.size() + " clients matching search criteria");
            return clients;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error searching clients", e);
            throw e;
        }
    }

    /**
     * Get clients by tier
     */
    public List<ClientDTO> getClientsByTier(String tierName) throws SQLException {
        List<ClientDTO> clients = new ArrayList<>();
        String sql = """
            SELECT c.*, t.tier_name, t.min_points, t.max_points, t.discount_rate 
            FROM clients c 
            JOIN tiers t ON c.tier_id = t.id 
            WHERE t.tier_name = ? 
            ORDER BY c.created_at DESC
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tierName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ClientDTO client = mapResultSetToClient(rs);
                    clients.add(client);
                }
            }

            LOGGER.info("ClientDAO: Retrieved " + clients.size() + " clients in " + tierName + " tier");
            return clients;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error retrieving clients by tier", e);
            throw e;
        }
    }

    /**
     * Get total clients count
     */
    public int getTotalClientsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM clients";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error getting total clients count", e);
            throw e;
        }
    }

    /**
     * Get next account sequence number
     */
    public int getNextAccountSequence() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(account_number, -4) AS UNSIGNED)), 0) + 1 FROM clients WHERE account_number LIKE ?";
        int currentYear = java.time.Year.now().getValue();
        String yearPattern = "ACC-" + currentYear + "-%";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, yearPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 1; // Start from 1 if no accounts found for current year

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error getting next account sequence", e);
            throw e;
        }
    }

    /**
     * Get client statistics
     */
    public ClientStatisticsDTO getClientStatistics() throws SQLException {
        String sql = """
            SELECT 
                COUNT(*) as total_clients,
                SUM(CASE WHEN send_mail_auto = true THEN 1 ELSE 0 END) as auto_mail_enabled,
                SUM(CASE WHEN phone IS NOT NULL AND phone != '' THEN 1 ELSE 0 END) as clients_with_phone,
                SUM(CASE WHEN street IS NOT NULL AND street != '' AND city IS NOT NULL AND city != '' 
                         AND state IS NOT NULL AND state != '' THEN 1 ELSE 0 END) as clients_with_address,
                SUM(loyalty_points) as total_loyalty_points,
                AVG(loyalty_points) as avg_loyalty_points,
                (SELECT t.tier_name FROM tiers t 
                 JOIN clients c ON t.id = c.tier_id 
                 GROUP BY t.tier_name 
                 ORDER BY COUNT(*) DESC 
                 LIMIT 1) as most_popular_tier
            FROM clients
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                ClientStatisticsDTO stats = new ClientStatisticsDTO();
                int totalClients = rs.getInt("total_clients");
                
                stats.setTotalClients(totalClients);
                stats.setAutoMailEnabledCount(rs.getInt("auto_mail_enabled"));
                stats.setClientsWithPhoneCount(rs.getInt("clients_with_phone"));
                stats.setClientsWithAddressCount(rs.getInt("clients_with_address"));
                stats.setTotalLoyaltyPoints(rs.getInt("total_loyalty_points"));
                stats.setAverageLoyaltyPoints(rs.getDouble("avg_loyalty_points"));
                stats.setMostPopularTier(rs.getString("most_popular_tier"));

                // Calculate percentages
                if (totalClients > 0) {
                    stats.setAutoMailPercentage(Math.round((stats.getAutoMailEnabledCount() * 100.0f) / totalClients));
                    stats.setPhonePercentage(Math.round((stats.getClientsWithPhoneCount() * 100.0f) / totalClients));
                    stats.setAddressPercentage(Math.round((stats.getClientsWithAddressCount() * 100.0f) / totalClients));
                }

                return stats;
            }

            return new ClientStatisticsDTO();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "ClientDAO: Error retrieving client statistics", e);
            throw e;
        }
    }

    /**
     * Map ResultSet to ClientDTO
     */
    private ClientDTO mapResultSetToClient(ResultSet rs) throws SQLException {
        ClientDTO client = new ClientDTO();
        
        // Basic client information
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
        
        // Tier information
        Long tierId = rs.getLong("tier_id");
        if (!rs.wasNull()) {
            client.setTierId(tierId);
            
            // If tier information is available in the result set, create TierDTO
            String tierName = rs.getString("tier_name");
            if (tierName != null) {
                TierDTO tier = new TierDTO();
                tier.setId(tierId);
                tier.setTierName(tierName);
                tier.setMinPoints(rs.getInt("min_points"));
                
                int maxPoints = rs.getInt("max_points");
                if (!rs.wasNull()) {
                    tier.setMaxPoints(maxPoints);
                }
                
                tier.setDiscountRate(rs.getBigDecimal("discount_rate"));
                client.setTier(tier);
                client.setTierLevel(tierName);
            }
        } else {
            // Set default tier if no tier assigned
            client.setTierLevel("Bronze");
        }
        
        client.setSendMailAuto(rs.getBoolean("send_mail_auto"));
        
        // Handle timestamp fields safely
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            client.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            client.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return client;
    }
}