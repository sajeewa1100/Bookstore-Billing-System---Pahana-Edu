package service;

import model.TierDTO;
import util.ConnectionManager;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TierService {

    /**
     * Get all tiers
     */
    public List<TierDTO> getAllTiers() {
        List<TierDTO> tiers = new ArrayList<>();
        String sql = "SELECT * FROM tiers ORDER BY min_points ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                TierDTO tier = new TierDTO();
                tier.setId(rs.getLong("id"));
                tier.setTierName(rs.getString("tier_name"));
                tier.setMinPoints(rs.getInt("min_points"));
                
                int maxPoints = rs.getInt("max_points");
                if (!rs.wasNull()) {
                    tier.setMaxPoints(maxPoints);
                }
                
                tier.setDiscountRate(rs.getBigDecimal("discount_rate"));
                tiers.add(tier);
            }
        } catch (SQLException e) {
            System.err.println("Error getting tiers: " + e.getMessage());
        }
        
        // If no tiers exist, create default ones
        if (tiers.isEmpty()) {
            createDefaultTiers();
            return getAllTiers(); // Try again
        }
        
        return tiers;
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
        
        // Return Bronze as default
        return tiers.isEmpty() ? null : tiers.get(0);
    }

    /**
     * Add new tier
     */
    public boolean addTier(TierDTO tier) {
        String sql = "INSERT INTO tiers (tier_name, min_points, max_points, discount_rate) VALUES (?, ?, ?, ?)";

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

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding tier: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create default tiers if none exist
     */
    private void createDefaultTiers() {
        // First create tiers table if it doesn't exist
        createTiersTable();
        
        // Create default tiers
        addTier(new TierDTO("Bronze", 0, 999, new BigDecimal("0.00")));
        addTier(new TierDTO("Silver", 1000, 4999, new BigDecimal("0.05")));
        addTier(new TierDTO("Gold", 5000, 9999, new BigDecimal("0.10")));
        addTier(new TierDTO("Platinum", 10000, null, new BigDecimal("0.15")));
        
        System.out.println("✅ Default tiers created!");
    }

    /**
     * Create tiers table
     */
    private void createTiersTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS tiers (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                tier_name VARCHAR(50) NOT NULL UNIQUE,
                min_points INT DEFAULT 0,
                max_points INT NULL,
                discount_rate DECIMAL(5,2) DEFAULT 0.00,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
            System.out.println("✅ Tiers table created!");
            
        } catch (SQLException e) {
            System.err.println("Error creating tiers table: " + e.getMessage());
        }
    }

    /**
     * Update clients table to add tier_id column
     */
    public void updateClientsTable() {
        String sql = "ALTER TABLE clients ADD COLUMN IF NOT EXISTS tier_id BIGINT NULL";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
            System.out.println("✅ Clients table updated with tier_id!");
            
        } catch (SQLException e) {
            System.err.println("Error updating clients table: " + e.getMessage());
        }
    }

    /**
     * Update the tier (now accepts TierDTO and long tierId)
     */
    public boolean updateTier(TierDTO tier, long tierId) throws SQLException {
        if (tier == null || tierId == 0) {
            throw new IllegalArgumentException("Tier or tier ID cannot be null or zero");
        }

        tier.setId(tierId);  // Ensure that the tier object has the ID set

        String sql = "UPDATE tiers SET tier_name = ?, min_points = ?, max_points = ?, discount_rate = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

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

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating tier: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete the tier by ID
     */
    public boolean deleteTier(long tierId) throws SQLException {
        String sql = "DELETE FROM tiers WHERE id = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tierId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Get a tier by its ID.
     */
    public TierDTO getTierById(long tierId) throws SQLException {
        String sql = "SELECT * FROM tiers WHERE id = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTier(rs);
                }
            }
        }
        return null;
    }

    /**
     * Map a ResultSet to a TierDTO object.
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
        return tier;
    }
    
    
}
