package service;

import model.TierDTO;
import util.ConnectionManager;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TierService {
    
    private static final Logger LOGGER = Logger.getLogger(TierService.class.getName());

    /**
     * Get all tiers - FIXED
     */
    public List<TierDTO> getAllTiers() {
        List<TierDTO> tiers = new ArrayList<>();
        String sql = "SELECT * FROM tiers ORDER BY min_points ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                TierDTO tier = mapResultSetToTier(rs);
                tiers.add(tier);
            }
            
            LOGGER.info("TierService: Retrieved " + tiers.size() + " tiers");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierService: Error getting tiers: " + e.getMessage(), e);
        }
        
        // If no tiers exist, create default ones
        if (tiers.isEmpty()) {
            LOGGER.info("TierService: No tiers found, creating default tiers");
            createDefaultTiers();
            return getAllTiers(); // Try again
        }
        
        return tiers;
    }

    /**
     * Get tier by ID - FIXED
     */
    public TierDTO getTierById(long tierId) {
        String sql = "SELECT * FROM tiers WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTier(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierService: Error getting tier by ID: " + e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * Find tier for specific points
     */
    public TierDTO getTierForPoints(int points) {
        List<TierDTO> tiers = getAllTiers();
        
        for (TierDTO tier : tiers) {
            if (tier.isPointsInRange(points)) {
                return tier;
            }
        }
        
        // Return first tier (Bronze) as default
        return tiers.isEmpty() ? null : tiers.get(0);
    }

    /**
     * Create new tier - FIXED
     */
    public boolean createTier(TierDTO tier) {
        if (tier == null) {
            throw new IllegalArgumentException("Tier cannot be null");
        }
        
        // Validate tier data
        if (!tier.isValid()) {
            throw new IllegalArgumentException("Invalid tier data");
        }
        
        String sql = "INSERT INTO tiers (tier_name, min_points, max_points, discount_rate) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tier.getTierName().trim());
            pstmt.setInt(2, tier.getMinPoints());
            
            if (tier.getMaxPoints() != null) {
                pstmt.setInt(3, tier.getMaxPoints());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            
            pstmt.setBigDecimal(4, tier.getDiscountRate());

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tier.setId(generatedKeys.getLong(1));
                    }
                }
                LOGGER.info("TierService: Tier created successfully: " + tier.getTierName());
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierService: Error creating tier: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Keep the original addTier method for backward compatibility
     */
    public boolean addTier(TierDTO tier) {
        return createTier(tier);
    }

    /**
     * Update tier - FIXED
     */
    public boolean updateTier(TierDTO tier) {
        if (tier == null || tier.getId() == null || tier.getId() <= 0) {
            throw new IllegalArgumentException("Tier and tier ID cannot be null or zero");
        }

        if (!tier.isValid()) {
            throw new IllegalArgumentException("Invalid tier data");
        }

        String sql = "UPDATE tiers SET tier_name = ?, min_points = ?, max_points = ?, discount_rate = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tier.getTierName().trim());
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
                LOGGER.info("TierService: Tier updated successfully: " + tier.getTierName());
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierService: Error updating tier: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Delete tier - FIXED
     */
    public boolean deleteTier(long tierId) {
        // First check if tier is in use by clients
        if (isTierInUse(tierId)) {
            LOGGER.warning("TierService: Cannot delete tier " + tierId + " - it is in use by clients");
            return false;
        }
        
        String sql = "DELETE FROM tiers WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tierId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("TierService: Tier deleted successfully - ID: " + tierId);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierService: Error deleting tier: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if tier is in use by clients
     */
    private boolean isTierInUse(long tierId) {
        String sql = "SELECT COUNT(*) FROM clients WHERE tier_id = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "TierService: Error checking if tier is in use: " + e.getMessage(), e);
        }
        
        return false;
    }

    /**
     * Create default tiers if none exist - FIXED
     */
    private void createDefaultTiers() {
        // First create tiers table if it doesn't exist
        createTiersTable();
        
        // Create default tiers (discount rates as decimals)
        try {
            createTier(new TierDTO("Bronze", 0, 999, 0.00));        // 0% discount
            createTier(new TierDTO("Silver", 1000, 4999, 0.05));    // 5% discount
            createTier(new TierDTO("Gold", 5000, 9999, 0.10));      // 10% discount
            createTier(new TierDTO("Platinum", 10000, null, 0.15)); // 15% discount
            
            LOGGER.info("TierService: Default tiers created successfully!");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "TierService: Error creating default tiers: " + e.getMessage(), e);
        }
    }

    /**
     * Create tiers table if it doesn't exist
     */
    private void createTiersTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS tiers (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                tier_name VARCHAR(50) NOT NULL UNIQUE,
                min_points INT DEFAULT 0,
                max_points INT NULL,
                discount_rate DECIMAL(5,4) DEFAULT 0.0000,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_tier_name (tier_name),
                INDEX idx_points_range (min_points, max_points)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
            LOGGER.info("TierService: Tiers table ensured to exist");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "TierService: Error creating tiers table: " + e.getMessage(), e);
        }
    }

    /**
     * Map ResultSet to TierDTO - FIXED
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