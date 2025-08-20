package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Data Transfer Object for Tier information
 * Represents customer loyalty tiers with discount rates and point requirements
 */
public class TierDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String tierName;
    private Integer minPoints;
    private Integer maxPoints;
    private BigDecimal discountRate; // Stored as decimal (0.05 for 5%)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public TierDTO() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.discountRate = BigDecimal.ZERO;
        this.minPoints = 0;
    }

    // Constructor for creating tiers with BigDecimal
    public TierDTO(String tierName, Integer minPoints, Integer maxPoints, BigDecimal discountRate) {
        this();
        this.tierName = tierName;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.discountRate = discountRate;
    }

    // Constructor for creating tiers with double (converts to BigDecimal)
    public TierDTO(String tierName, int minPoints, Integer maxPoints, double discountRate) {
        this();
        this.tierName = tierName;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.discountRate = BigDecimal.valueOf(discountRate);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTierName() { return tierName; }
    public void setTierName(String tierName) { 
        this.tierName = tierName;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getMinPoints() { return minPoints; }
    public void setMinPoints(Integer minPoints) { 
        this.minPoints = minPoints;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getMaxPoints() { return maxPoints; }
    public void setMaxPoints(Integer maxPoints) { 
        this.maxPoints = maxPoints;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { 
        this.discountRate = discountRate;
        this.updatedAt = LocalDateTime.now();
    }

    // CRITICAL: Methods required by JSP
    /**
     * Get discount rate as percentage (for JSP display)
     * Converts 0.05 -> 5.0
     */
    public double getDiscountRateAsPercentage() {
        if (discountRate == null) return 0.0;
        return discountRate.multiply(BigDecimal.valueOf(100)).doubleValue();
    }
    
    /**
     * Set discount rate from percentage
     * Converts 5.0 -> 0.05
     */
    public void setDiscountRateFromPercentage(double percentage) {
        this.discountRate = BigDecimal.valueOf(percentage / 100.0);
        this.updatedAt = LocalDateTime.now();
    }

    // Date compatibility methods for JSP
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Get createdAt as Date for JSTL compatibility
     */
    public Date getCreatedAtAsDate() {
        if (createdAt == null) return new Date();
        return java.sql.Timestamp.valueOf(createdAt);
    }

    /**
     * Get updatedAt as Date for JSTL compatibility
     */
    public Date getUpdatedAtAsDate() {
        if (updatedAt == null) return new Date();
        return java.sql.Timestamp.valueOf(updatedAt);
    }

    // Utility methods
    /**
     * Check if points are in this tier's range
     */
    public boolean isPointsInRange(int points) {
        if (minPoints != null && points < minPoints) return false;
        if (maxPoints != null && points > maxPoints) return false;
        return true;
    }
    
    /**
     * Get formatted discount rate as percentage string
     */
    public String getFormattedDiscountRate() {
        if (discountRate == null) return "0%";
        return getDiscountRateAsPercentage() + "%";
    }
    
    /**
     * Get points range as string
     */
    public String getPointsRange() {
        StringBuilder range = new StringBuilder();
        if (minPoints != null) {
            range.append(minPoints);
        } else {
            range.append("0");
        }
        range.append(" - ");
        if (maxPoints != null) {
            range.append(maxPoints);
        } else {
            range.append("∞");
        }
        return range.toString();
    }
    
    /**
     * Check if this is an unlimited tier (no max points)
     */
    public boolean isUnlimited() {
        return maxPoints == null;
    }
    
    /**
     * Validate tier data
     */
    public boolean isValid() {
        if (tierName == null || tierName.trim().isEmpty()) return false;
        if (minPoints == null || minPoints < 0) return false;
        if (maxPoints != null && maxPoints <= minPoints) return false;
        if (discountRate == null || discountRate.compareTo(BigDecimal.ZERO) < 0 || 
            discountRate.compareTo(BigDecimal.ONE) > 0) return false;
        return true;
    }

    /**
     * Check if a point amount qualifies for this tier
     */
    public boolean qualifiesForTier(int points) {
        if (points < minPoints) {
            return false;
        }
        
        if (maxPoints != null && points > maxPoints) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get formatted discount rate as percentage
     */
    public String getFormattedDiscountRateWithSymbol() {
        if (discountRate == null) {
            return "0%";
        }
        return discountRate.multiply(BigDecimal.valueOf(100)).toString() + "%";
    }
    
    /**
     * Get discount rate as decimal (for calculations)
     */
    public BigDecimal getDiscountRateDecimal() {
        if (discountRate == null) {
            return BigDecimal.ZERO;
        }
        return discountRate.divide(new BigDecimal("100"));
    }
    
    /**
     * Get formatted point range
     */
    public String getPointRange() {
        if (maxPoints != null) {
            return minPoints + " - " + maxPoints + " points";
        } else {
            return minPoints + "+ points";
        }
    }
    
    /**
     * Get tier color for UI display
     */
    public String getTierColor() {
        if (tierName == null) {
            return "#gray";
        }
        
        switch (tierName.toLowerCase()) {
            case "bronze":
                return "#CD7F32";
            case "silver":
                return "#C0C0C0";
            case "gold":
                return "#FFD700";
            case "platinum":
                return "#E5E4E2";
            default:
                return "#6B7280";
        }
    }
    
    /**
     * Get tier icon for UI display
     */
    public String getTierIcon() {
        if (tierName == null) {
            return "fas fa-star";
        }
        
        switch (tierName.toLowerCase()) {
            case "bronze":
                return "fas fa-medal";
            case "silver":
                return "fas fa-award";
            case "gold":
                return "fas fa-trophy";
            case "platinum":
                return "fas fa-crown";
            default:
                return "fas fa-star";
        }
    }
    
    /**
     * Get formatted created date
     */
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return "";
    }
    
    /**
     * Get formatted created date (short)
     */
    public String getFormattedCreatedAtShort() {
        if (createdAt != null) {
            return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "TierDTO{" +
                "id=" + id +
                ", tierName='" + tierName + '\'' +
                ", minPoints=" + minPoints +
                ", maxPoints=" + maxPoints +
                ", discountRate=" + discountRate +
                ", discountRateAsPercentage=" + getDiscountRateAsPercentage() +
                '}'; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        TierDTO tierDTO = (TierDTO) o;
        
        if (id != null ? !id.equals(tierDTO.id) : tierDTO.id != null) return false;
        return tierName != null ? tierName.equals(tierDTO.tierName) : tierDTO.tierName == null;
    }
    
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tierName != null ? tierName.hashCode() : 0);
        return result;
    }
}
