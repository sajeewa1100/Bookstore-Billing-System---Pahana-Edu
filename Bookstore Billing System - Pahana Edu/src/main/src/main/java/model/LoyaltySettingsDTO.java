package model;

import java.math.BigDecimal;

public class LoyaltySettingsDTO {
    private int id;
    private int pointsPer100Rs;
    private BigDecimal silverDiscount;
    private int goldThreshold;
    private BigDecimal goldDiscount;
    private int platinumThreshold;
    private BigDecimal platinumDiscount;
    private boolean isActive;
    
    // Constructors
    public LoyaltySettingsDTO() {}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getPointsPer100Rs() { return pointsPer100Rs; }
    public void setPointsPer100Rs(int pointsPer100Rs) { this.pointsPer100Rs = pointsPer100Rs; }
    
    public BigDecimal getSilverDiscount() { return silverDiscount; }
    public void setSilverDiscount(BigDecimal silverDiscount) { this.silverDiscount = silverDiscount; }
    
    public int getGoldThreshold() { return goldThreshold; }
    public void setGoldThreshold(int goldThreshold) { this.goldThreshold = goldThreshold; }
    
    public BigDecimal getGoldDiscount() { return goldDiscount; }
    public void setGoldDiscount(BigDecimal goldDiscount) { this.goldDiscount = goldDiscount; }
    
    public int getPlatinumThreshold() { return platinumThreshold; }
    public void setPlatinumThreshold(int platinumThreshold) { this.platinumThreshold = platinumThreshold; }
    
    public BigDecimal getPlatinumDiscount() { return platinumDiscount; }
    public void setPlatinumDiscount(BigDecimal platinumDiscount) { this.platinumDiscount = platinumDiscount; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public BigDecimal getDiscountForTier(String tierLevel) {
        switch (tierLevel.toUpperCase()) {
            case "GOLD": return goldDiscount;
            case "PLATINUM": return platinumDiscount;
            case "SILVER":
            default: return silverDiscount;
        }
    }
}