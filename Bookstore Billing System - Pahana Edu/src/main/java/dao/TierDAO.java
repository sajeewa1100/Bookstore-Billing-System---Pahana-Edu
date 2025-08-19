package dao;

import model.TierDTO;
import model.TierStatisticsDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TierDAO {

    private static final Logger LOGGER = Logger.getLogger(TierDAO.class.getName());

    /**
     * Get all tiers ordered by minimum points
     */
    public List<TierDTO> getAllTiers() throws SQLException {
        List<TierDTO> tiers = new ArrayList<>();
        String sql = "SELECT * FROM tiers ORDER BY min_points ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                TierDTO tier = mapResultSetToTier(rs);
                tiers.add(tier);
            }

            LOGGER.info("TierDAO: Retrieved " + tiers.size() + " tiers from database");
            return tiers;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierDAO: Error retrieving all tiers", e);
            throw e;
        }
    }

    /**
     * Get tier by ID
     */
    public TierDTO getTierById(Long tierId) throws SQLException {
        String sql = "SELECT * FROM tiers WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TierDTO tier = mapResultSetToTier(rs);
                    LOGGER.info("TierDAO: Retrieved tier by ID: " + tierId);
                    return tier;
                }
            }

            LOGGER.warning("TierDAO: Tier not found with ID: " + tierId);
            return null;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierDAO: Error retrieving tier by ID: " + tierId, e);
            throw e;
        }
    }

    /**
     * Add a new tier
     */
    public boolean addTier(TierDTO tier) throws SQLException {
        String sql = "INSERT INTO tiers (tier_name, min_points, max_points, discount_rate) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tier.getTierName());
            pstmt.setInt(2, tier.getMinPoints());
            if (tier.getMaxPoints() != null) {
                pstmt.setInt(3, tier.getMaxPoints());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setBigDecimal(4, tier.getDiscountRate());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tier.setId(generatedKeys.getLong(1));
                    }
                }
                LOGGER.info("TierDAO: Tier added successfully - " + tier.getTierName());
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierDAO: Error adding tier", e);
            throw e;
        }
    }

    /**
     * Update an existing tier
     */
    public boolean updateTier(TierDTO tier) throws SQLException {
        String sql = "UPDATE tiers SET tier_name = ?, min_points = ?, max_points = ?, discount_rate = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tier.getTierName());
            pstmt.setInt(2, tier.getMinPoints());
            if (tier.getMaxPoints() != null) {
                pstmt.setInt(3, tier.getMaxPoints());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setBigDecimal(4, tier.getDiscountRate());
            pstmt.setLong(5, tier.getId());

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("TierDAO: Tier updated successfully - " + tier.getTierName());
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierDAO: Error updating tier", e);
            throw e;
        }
    }

    /**
     * Delete a tier
     */
    public boolean deleteTier(Long tierId) throws SQLException {
        String sql = "DELETE FROM tiers WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tierId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("TierDAO: Tier deleted successfully - ID: " + tierId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierDAO: Error deleting tier", e);
            throw e;
        }
    }

    /**
     * Check if tier is in use by clients
     */
    public boolean isTierInUse(Long tierId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clients WHERE tier_id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierDAO: Error checking if tier is in use", e);
            throw e;
        }
    }

    /**
     * Get tier statistics
     */
    public TierStatisticsDTO getTierStatistics() throws SQLException {
        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM tiers) as total_tiers,
                (SELECT COUNT(*) FROM clients c JOIN tiers t ON c.tier_id = t.id WHERE t.tier_name = 'Bronze') as bronze_clients,
                (SELECT COUNT(*) FROM clients c JOIN tiers t ON c.tier_id = t.id WHERE t.tier_name = 'Silver') as silver_clients,
                (SELECT COUNT(*) FROM clients c JOIN tiers t ON c.tier_id = t.id WHERE t.tier_name = 'Gold') as gold_clients,
                (SELECT COUNT(*) FROM clients c JOIN tiers t ON c.tier_id = t.id WHERE t.tier_name = 'Platinum') as platinum_clients,
                (SELECT AVG(discount_rate) FROM tiers) as avg_discount_rate
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                TierStatisticsDTO stats = new TierStatisticsDTO();
                stats.setTotalTiers(rs.getInt("total_tiers"));
                stats.setTotalClientsInBronze(rs.getInt("bronze_clients"));
                stats.setTotalClientsInSilver(rs.getInt("silver_clients"));
                stats.setTotalClientsInGold(rs.getInt("gold_clients"));
                stats.setTotalClientsInPlatinum(rs.getInt("platinum_clients"));
                stats.setAverageDiscountRate(rs.getDouble("avg_discount_rate"));

                // Determine most popular tier
                int maxClients = Math.max(Math.max(stats.getTotalClientsInBronze(), stats.getTotalClientsInSilver()),
                                         Math.max(stats.getTotalClientsInGold(), stats.getTotalClientsInPlatinum()));
                
                if (maxClients == stats.getTotalClientsInBronze()) {
                    stats.setMostPopularTier("Bronze");
                } else if (maxClients == stats.getTotalClientsInSilver()) {
                    stats.setMostPopularTier("Silver");
                } else if (maxClients == stats.getTotalClientsInGold()) {
                    stats.setMostPopularTier("Gold");
                } else {
                    stats.setMostPopularTier("Platinum");
                }

                return stats;
            }

            return new TierStatisticsDTO();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierDAO: Error retrieving tier statistics", e);
            throw e;
        }
    }

    /**
     * Map ResultSet to TierDTO
     */
    private TierDTO mapResultSetToTier(ResultSet rs) throws SQLException {
        TierDTO tier = new TierDTO();
        tier.setId(rs.getLong("id"));
        tier.setTierName(rs.getString("tier_name"));
        tier.setMinPoints(rs.getInt("min_points"));
        
        int maxPoints = rs.getInt("max_points");
        if (!rs.wasNull()) {
            tier.setMaxPoints(maxPoints);
        }
        
        tier.setDiscountRate(rs.getBigDecimal("discount_rate"));
        
        // Handle timestamp fields safely
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            tier.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            tier.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        return tier;
    }
}