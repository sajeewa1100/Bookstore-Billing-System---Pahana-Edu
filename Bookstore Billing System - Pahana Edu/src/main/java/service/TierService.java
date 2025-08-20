package service;

import model.TierDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TierService {
    
    private static final Logger LOGGER = Logger.getLogger(TierService.class.getName());
    
    public TierService() {
        System.out.println("✅ TierService: Initialized");
    }
    
    /**
     * Get tier for specific loyalty points
     */
    public TierDTO getTierForPoints(int loyaltyPoints) {
        try {
            String sql = """
                SELECT * FROM tiers 
                WHERE ? >= min_points AND (max_points IS NULL OR ? <= max_points)
                ORDER BY min_points DESC 
                LIMIT 1
                """;
            
            try (Connection conn = ConnectionManager.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, loyaltyPoints);
                pstmt.setInt(2, loyaltyPoints);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        TierDTO tier = mapResultSetToTier(rs);
                        System.out.println("🏆 TierService: Found tier " + tier.getTierName() + " for " + loyaltyPoints + " points");
                        return tier;
                    }
                }
            }
            
            // Return default Bronze tier if no tier found
            System.out.println("⚠️ TierService: No tier found for " + loyaltyPoints + " points, returning Bronze");
            return getDefaultBronzeTier();
            
        } catch (SQLException e) {
            System.err.println("❌ TierService: Error getting tier for points: " + e.getMessage());
            e.printStackTrace();
            return getDefaultBronzeTier();
        }
    }
    
    /**
     * Get all tiers
     */
    public List<TierDTO> getAllTiers() {
        List<TierDTO> tiers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tiers ORDER BY min_points ASC";
            
            try (Connection conn = ConnectionManager.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                while (rs.next()) {
                    TierDTO tier = mapResultSetToTier(rs);
                    tiers.add(tier);
                }
            }
            
            System.out.println("📊 TierService: Retrieved " + tiers.size() + " tiers");
            
            // If no tiers in database, create default ones
            if (tiers.isEmpty()) {
                System.out.println("⚠️ TierService: No tiers found, creating defaults");
                createDefaultTiers();
                return getAllTiers(); // Recursive call to get the newly created tiers
            }
            
            return tiers;
            
        } catch (SQLException e) {
            System.err.println("❌ TierService: Error getting all tiers: " + e.getMessage());
            e.printStackTrace();
            return getDefaultTiersList();
        }
    }
    
    /**
     * Get tier by ID
     */
    public TierDTO getTierById(Long tierId) {
        try {
            String sql = "SELECT * FROM tiers WHERE id = ?";
            
            try (Connection conn = ConnectionManager.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setLong(1, tierId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        TierDTO tier = mapResultSetToTier(rs);
                        System.out.println("✅ TierService: Retrieved tier by ID " + tierId + ": " + tier.getTierName());
                        return tier;
                    }
                }
            }
            
            System.out.println("⚠️ TierService: Tier not found with ID: " + tierId);
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ TierService: Error getting tier by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create default tiers if none exist
     */
    private void createDefaultTiers() {
        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            
            // Check if tiers table exists and is empty
            String checkSql = "SELECT COUNT(*) FROM tiers";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 ResultSet rs = checkStmt.executeQuery()) {
                
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("ℹ️ TierService: Tiers already exist, skipping default creation");
                    return;
                }
            }
            
            String insertSql = """
                INSERT INTO tiers (tier_name, min_points, max_points, discount_rate, description) 
                VALUES (?, ?, ?, ?, ?)
                """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                
                // Bronze Tier
                pstmt.setString(1, "Bronze");
                pstmt.setInt(2, 0);
                pstmt.setInt(3, 499);
                pstmt.setBigDecimal(4, new java.math.BigDecimal("0.00"));
                pstmt.setString(5, "Entry level tier with standard benefits");
                pstmt.addBatch();
                
                // Silver Tier
                pstmt.setString(1, "Silver");
                pstmt.setInt(2, 500);
                pstmt.setInt(3, 1499);
                pstmt.setBigDecimal(4, new java.math.BigDecimal("0.05"));
                pstmt.setString(5, "Silver tier with 5% discount on purchases");
                pstmt.addBatch();
                
                // Gold Tier
                pstmt.setString(1, "Gold");
                pstmt.setInt(2, 1500);
                pstmt.setInt(3, 2999);
                pstmt.setBigDecimal(4, new java.math.BigDecimal("0.10"));
                pstmt.setString(5, "Gold tier with 10% discount on purchases");
                pstmt.addBatch();
                
                // Platinum Tier
                pstmt.setString(1, "Platinum");
                pstmt.setInt(2, 3000);
                pstmt.setNull(3, Types.INTEGER); // No max points
                pstmt.setBigDecimal(4, new java.math.BigDecimal("0.15"));
                pstmt.setString(5, "Premium tier with 15% discount on purchases");
                pstmt.addBatch();
                
                int[] results = pstmt.executeBatch();
                System.out.println("✅ TierService: Created " + results.length + " default tiers");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ TierService: Error creating default tiers: " + e.getMessage());
            e.printStackTrace();
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
        tier.setDescription(rs.getString("description"));
        
        return tier;
    }
    
    /**
     * Get default Bronze tier (fallback)
     */
    private TierDTO getDefaultBronzeTier() {
        TierDTO bronze = new TierDTO();
        bronze.setId(1L);
        bronze.setTierName("Bronze");
        bronze.setMinPoints(0);
        bronze.setMaxPoints(499);
        bronze.setDiscountRate(new java.math.BigDecimal("0.00"));
        bronze.setDescription("Entry level tier");
        return bronze;
    }
    
    /**
     * Get default tiers list (fallback)
     */
    private List<TierDTO> getDefaultTiersList() {
        List<TierDTO> tiers = new ArrayList<>();
        
        // Bronze
        TierDTO bronze = new TierDTO();
        bronze.setId(1L);
        bronze.setTierName("Bronze");
        bronze.setMinPoints(0);
        bronze.setMaxPoints(499);
        bronze.setDiscountRate(new java.math.BigDecimal("0.00"));
        tiers.add(bronze);
        
        // Silver
        TierDTO silver = new TierDTO();
        silver.setId(2L);
        silver.setTierName("Silver");
        silver.setMinPoints(500);
        silver.setMaxPoints(1499);
        silver.setDiscountRate(new java.math.BigDecimal("0.05"));
        tiers.add(silver);
        
        // Gold
        TierDTO gold = new TierDTO();
        gold.setId(3L);
        gold.setTierName("Gold");
        gold.setMinPoints(1500);
        gold.setMaxPoints(2999);
        gold.setDiscountRate(new java.math.BigDecimal("0.10"));
        tiers.add(gold);
        
        // Platinum
        TierDTO platinum = new TierDTO();
        platinum.setId(4L);
        platinum.setTierName("Platinum");
        platinum.setMinPoints(3000);
        platinum.setMaxPoints(null);
        platinum.setDiscountRate(new java.math.BigDecimal("0.15"));
        tiers.add(platinum);
        
        return tiers;
    }
    
    /**
     * Test service connectivity
     */
    public void testServiceConnectivity() {
        System.out.println("🧪 TierService: Testing service connectivity...");
        try {
            List<TierDTO> tiers = getAllTiers();
            System.out.println("📊 Total tiers: " + tiers.size());
            
            for (TierDTO tier : tiers) {
                System.out.println("🏆 " + tier.getTierName() + ": " + 
                                 tier.getMinPoints() + "-" + 
                                 (tier.getMaxPoints() != null ? tier.getMaxPoints() : "∞") + 
                                 " points, " + 
                                 tier.getDiscountPercentage() + "% discount");
            }
            
            // Test tier lookup
            TierDTO testTier = getTierForPoints(750);
            System.out.println("🔍 Tier for 750 points: " + 
                             (testTier != null ? testTier.getTierName() : "None"));
            
            System.out.println("✅ TierService: All tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ TierService: Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}