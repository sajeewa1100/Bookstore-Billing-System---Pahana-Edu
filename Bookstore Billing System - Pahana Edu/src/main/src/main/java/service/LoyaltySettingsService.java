package service;

import dao.LoyaltySettingsDAO;
import model.LoyaltySettingsDTO;
import java.util.List;
import java.math.BigDecimal;

public class LoyaltySettingsService {
    private LoyaltySettingsDAO loyaltySettingsDAO;
    
    public LoyaltySettingsService() {
        this.loyaltySettingsDAO = new LoyaltySettingsDAO();
    }
    
    public int createLoyaltySettings(LoyaltySettingsDTO loyaltySettings) {
        // Validate settings before creating
        if (!validateLoyaltySettings(loyaltySettings)) {
            throw new IllegalArgumentException("Invalid loyalty settings provided");
        }
        
        // Deactivate any existing active settings before creating new ones
        loyaltySettingsDAO.deactivateAllSettings();
        
        // Set new settings as active
        loyaltySettings.setActive(true);
        
        return loyaltySettingsDAO.createLoyaltySettings(loyaltySettings);
    }
    
    public boolean updateLoyaltySettings(LoyaltySettingsDTO loyaltySettings) {
        if (!validateLoyaltySettings(loyaltySettings)) {
            throw new IllegalArgumentException("Invalid loyalty settings provided");
        }
        
        return loyaltySettingsDAO.updateLoyaltySettings(loyaltySettings);
    }
    
    public boolean deleteLoyaltySettings(int settingsId) {
        return loyaltySettingsDAO.deleteLoyaltySettings(settingsId);
    }
    
    public LoyaltySettingsDTO getActiveLoyaltySettings() {
        return loyaltySettingsDAO.getActiveLoyaltySettings();
    }
    
    public List<LoyaltySettingsDTO> getAllLoyaltySettings() {
        return loyaltySettingsDAO.getAllLoyaltySettings();
    }
    
    public LoyaltySettingsDTO getLoyaltySettingsById(int id) {
        return loyaltySettingsDAO.getLoyaltySettingsById(id);
    }
    
    // Calculate client tier based on points
    public String calculateClientTier(int loyaltyPoints) {
        LoyaltySettingsDTO settings = getActiveLoyaltySettings();
        if (settings == null) {
            return "SILVER"; // Default tier
        }
        
        if (loyaltyPoints >= settings.getPlatinumThreshold()) {
            return "PLATINUM";
        } else if (loyaltyPoints >= settings.getGoldThreshold()) {
            return "GOLD";
        } else {
            return "SILVER";
        }
    }
    
    // Calculate discount for a client tier
    public BigDecimal getDiscountForTier(String tierLevel) {
        LoyaltySettingsDTO settings = getActiveLoyaltySettings();
        if (settings == null) {
            return BigDecimal.ZERO;
        }
        
        return settings.getDiscountForTier(tierLevel);
    }
    
    // Calculate points earned for a purchase amount
    public int calculatePointsEarned(BigDecimal purchaseAmount) {
        LoyaltySettingsDTO settings = getActiveLoyaltySettings();
        if (settings == null) {
            return 0;
        }
        
        int pointsPerRs100 = settings.getPointsPer100Rs();
        return purchaseAmount.divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.DOWN).intValue() * pointsPerRs100;
    }
    
    private boolean validateLoyaltySettings(LoyaltySettingsDTO settings) {
        // Basic validation
        if (settings == null) return false;
        
        // Points per 100 Rs should be positive
        if (settings.getPointsPer100Rs() <= 0) return false;
        
        // Discounts should be between 0 and 100
        if (settings.getSilverDiscount() == null || 
            settings.getSilverDiscount().compareTo(BigDecimal.ZERO) < 0 || 
            settings.getSilverDiscount().compareTo(BigDecimal.valueOf(100)) > 0) return false;
            
        if (settings.getGoldDiscount() == null || 
            settings.getGoldDiscount().compareTo(BigDecimal.ZERO) < 0 || 
            settings.getGoldDiscount().compareTo(BigDecimal.valueOf(100)) > 0) return false;
            
        if (settings.getPlatinumDiscount() == null || 
            settings.getPlatinumDiscount().compareTo(BigDecimal.ZERO) < 0 || 
            settings.getPlatinumDiscount().compareTo(BigDecimal.valueOf(100)) > 0) return false;
        
        // Thresholds should be positive and in ascending order
        if (settings.getGoldThreshold() <= 0 || settings.getPlatinumThreshold() <= 0) return false;
        if (settings.getGoldThreshold() >= settings.getPlatinumThreshold()) return false;
        
        // Higher tier discounts should be equal or greater
        if (settings.getGoldDiscount().compareTo(settings.getSilverDiscount()) < 0) return false;
        if (settings.getPlatinumDiscount().compareTo(settings.getGoldDiscount()) < 0) return false;
        
        return true;
    }
}