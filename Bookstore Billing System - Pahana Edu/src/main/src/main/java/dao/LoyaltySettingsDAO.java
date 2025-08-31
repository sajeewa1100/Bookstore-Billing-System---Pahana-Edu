package dao;

import model.LoyaltySettingsDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoyaltySettingsDAO {
    
    // CREATE - Insert new loyalty settings
    public int createLoyaltySettings(LoyaltySettingsDTO settings) {
        String sql = "INSERT INTO loyalty_settings (points_per_100_rs, silver_discount, gold_threshold, gold_discount, platinum_threshold, platinum_discount, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, settings.getPointsPer100Rs());
            stmt.setBigDecimal(2, settings.getSilverDiscount());
            stmt.setInt(3, settings.getGoldThreshold());
            stmt.setBigDecimal(4, settings.getGoldDiscount());
            stmt.setInt(5, settings.getPlatinumThreshold());
            stmt.setBigDecimal(6, settings.getPlatinumDiscount());
            stmt.setBoolean(7, settings.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // UPDATE - Update existing loyalty settings
    public boolean updateLoyaltySettings(LoyaltySettingsDTO settings) {
        String sql = "UPDATE loyalty_settings SET points_per_100_rs = ?, silver_discount = ?, gold_threshold = ?, gold_discount = ?, platinum_threshold = ?, platinum_discount = ?, is_active = ? WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, settings.getPointsPer100Rs());
            stmt.setBigDecimal(2, settings.getSilverDiscount());
            stmt.setInt(3, settings.getGoldThreshold());
            stmt.setBigDecimal(4, settings.getGoldDiscount());
            stmt.setInt(5, settings.getPlatinumThreshold());
            stmt.setBigDecimal(6, settings.getPlatinumDiscount());
            stmt.setBoolean(7, settings.isActive());
            stmt.setInt(8, settings.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // DELETE - Delete loyalty settings
    public boolean deleteLoyaltySettings(int settingsId) {
        String sql = "DELETE FROM loyalty_settings WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, settingsId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get active loyalty settings
    public LoyaltySettingsDTO getActiveLoyaltySettings() {
        String sql = "SELECT * FROM loyalty_settings WHERE is_active = TRUE ORDER BY id DESC LIMIT 1";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoyaltySettings(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // NEW METHOD: Alias for getCurrentSettings() - used by ManagerDashboardService
    public LoyaltySettingsDTO getCurrentSettings() {
        return getActiveLoyaltySettings();
    }
    
    // NEW METHOD: Alias for updateSettings() - used by ManagerDashboardService
    public boolean updateSettings(LoyaltySettingsDTO settings) {
        try {
            // If no ID is set, create new settings and deactivate old ones
            if (settings.getId() == 0) {
                // First deactivate all existing settings
                deactivateAllSettings();
                
                // Then create new settings
                int newId = createLoyaltySettings(settings);
                return newId > 0;
            } else {
                // Update existing settings
                return updateLoyaltySettings(settings);
            }
        } catch (Exception e) {
            System.out.println("Error in updateSettings: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all loyalty settings
    public List<LoyaltySettingsDTO> getAllLoyaltySettings() {
        List<LoyaltySettingsDTO> settingsList = new ArrayList<>();
        String sql = "SELECT * FROM loyalty_settings ORDER BY id DESC";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                settingsList.add(mapResultSetToLoyaltySettings(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settingsList;
    }
    
    // Get loyalty settings by ID
    public LoyaltySettingsDTO getLoyaltySettingsById(int id) {
        String sql = "SELECT * FROM loyalty_settings WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoyaltySettings(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Deactivate all existing settings
    public boolean deactivateAllSettings() {
        String sql = "UPDATE loyalty_settings SET is_active = FALSE";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            return stmt.executeUpdate() >= 0; // Returns true even if 0 rows updated
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // EXISTING METHOD (kept for backward compatibility)
    public LoyaltySettingsDTO getLoyaltySettings() {
        return getActiveLoyaltySettings();
    }
    
    private LoyaltySettingsDTO mapResultSetToLoyaltySettings(ResultSet rs) throws SQLException {
        LoyaltySettingsDTO settings = new LoyaltySettingsDTO();
        settings.setId(rs.getInt("id"));
        settings.setPointsPer100Rs(rs.getInt("points_per_100_rs"));
        settings.setSilverDiscount(rs.getBigDecimal("silver_discount"));
        settings.setGoldThreshold(rs.getInt("gold_threshold"));
        settings.setGoldDiscount(rs.getBigDecimal("gold_discount"));
        settings.setPlatinumThreshold(rs.getInt("platinum_threshold"));
        settings.setPlatinumDiscount(rs.getBigDecimal("platinum_discount"));
        settings.setActive(rs.getBoolean("is_active"));
        return settings;
    }
}