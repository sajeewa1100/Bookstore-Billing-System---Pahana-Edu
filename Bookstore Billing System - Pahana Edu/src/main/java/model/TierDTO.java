package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Tier entity
 * Represents loyalty tier information in the bookstore system
 */
public class TierDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String tierName;
    private Integer minPoints;
    private Integer maxPoints; // Can be null for highest tier
    private BigDecimal discountRate; // As decimal (e.g., 0.05 for 5%)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public TierDTO() {
        this.discountRate = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with essential fields
    public TierDTO(String tierName, Integer minPoints, Integer maxPoints, BigDecimal discountRate) {
        this();
        this.tierName = tierName;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.discountRate = discountRate != null ? discountRate : BigDecimal.ZERO;
    }
    
    // Constructor with double discount rate (convenience)
    public TierDTO(String tierName, Integer minPoints, Integer maxPoints, double discountRate) {
        this(tierName, minPoints, maxPoints, BigDecimal.valueOf(discountRate));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getMinPoints() {
        return minPoints != null ? minPoints : 0;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getDiscountRate() {
        return discountRate != null ? discountRate : BigDecimal.ZERO;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate != null ? discountRate : BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Convenience setter for double
    public void setDiscountRate(double discountRate) {
        this.discountRate = BigDecimal.valueOf(discountRate);
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility Methods

    /**
     * Get discount percentage as string for display (e.g., "5.00")
     */
    public String getDiscountPercentage() {
        if (discountRate == null) {
            return "0.00";
        }
        // Convert decimal to percentage (e.g., 0.05 -> 5.00)
        double percentage = discountRate.doubleValue() * 100;
        return String.format("%.2f", percentage);
    }
    
    /**
     * Get discount rate as percentage double (e.g., 5.0 for 5%)
     */
    public double getDiscountPercentageAsDouble() {
        if (discountRate == null) {
            return 0.0;
        }
        return discountRate.doubleValue() * 100;
    }
    
    /**
     * Get points range for display (e.g., "0 - 499 points" or "3000+ points")
     */
    public String getPointsRange() {
        if (minPoints == null) {
            return "No points required";
        }
        
        if (maxPoints == null) {
            return minPoints + "+ points";
        }
        
        return minPoints + " - " + maxPoints + " points";
    }
    
    /**
     * Get formatted points range for display
     */
    public String getFormattedPointsRange() {
        if (minPoints == null) {
            return "Any points";
        }
        
        if (maxPoints == null) {
            return String.format("%,d+ points", minPoints);
        }
        
        return String.format("%,d - %,d points", minPoints, maxPoints);
    }
    
    /**
     * Check if given points qualify for this tier
     */
    public boolean qualifiesForTier(int points) {
        if (minPoints != null && points < minPoints) {
            return false;
        }
        if (maxPoints != null && points > maxPoints) {
            return false;
        }
        return true;
    }
    
    /**
     * Check if this tier has a points limit (max points set)
     */
    public boolean hasPointsLimit() {
        return maxPoints != null;
    }
    
    /**
     * Check if this tier offers a discount
     */
    public boolean hasDiscount() {
        return discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Calculate discount amount for given subtotal
     */
    public double calculateDiscountAmount(double subtotal) {
        if (discountRate == null || subtotal <= 0) {
            return 0.0;
        }
        return subtotal * discountRate.doubleValue();
    }
    
    /**
     * Get tier description for display
     */
    public String getTierDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(tierName);
        
        if (hasDiscount()) {
            desc.append(" - ").append(getDiscountPercentage()).append("% discount");
        }
        
        desc.append(" (").append(getPointsRange()).append(")");
        
        return desc.toString();
    }
    
    /**
     * Get tier color class for CSS styling
     */
    public String getTierColorClass() {
        if (tierName == null) {
            return "tier-default";
        }
        
        switch (tierName.toLowerCase()) {
            case "bronze":
                return "tier-bronze";
            case "silver":
                return "tier-silver";
            case "gold":
                return "tier-gold";
            case "platinum":
                return "tier-platinum";
            case "diamond":
                return "tier-diamond";
            default:
                return "tier-default";
        }
    }
    
    /**
     * Get tier icon for display
     */
    public String getTierIcon() {
        if (tierName == null) {
            return "fas fa-medal";
        }
        
        switch (tierName.toLowerCase()) {
            case "bronze":
                return "fas fa-medal bronze";
            case "silver":
                return "fas fa-medal silver";
            case "gold":
                return "fas fa-medal gold";
            case "platinum":
                return "fas fa-crown platinum";
            case "diamond":
                return "fas fa-gem diamond";
            default:
                return "fas fa-medal";
        }
    }
    
    /**
     * Check if this is the highest tier (no max points)
     */
    public boolean isHighestTier() {
        return maxPoints == null;
    }
    
    /**
     * Check if this is the lowest tier (0 min points)
     */
    public boolean isLowestTier() {
        return minPoints != null && minPoints == 0;
    }
    
    /**
     * Get next tier points requirement (for progress display)
     */
    public Integer getNextTierPoints() {
        return maxPoints != null ? maxPoints + 1 : null;
    }
    
    /**
     * Validate tier data
     */
    public boolean isValid() {
        return tierName != null && !tierName.trim().isEmpty() &&
               minPoints != null && minPoints >= 0 &&
               (maxPoints == null || maxPoints > minPoints) &&
               discountRate != null && discountRate.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    /**
     * Get tier level as integer (for sorting)
     */
    public int getTierLevel() {
        if (tierName == null) {
            return 0;
        }
        
        switch (tierName.toLowerCase()) {
            case "bronze":
                return 1;
            case "silver":
                return 2;
            case "gold":
                return 3;
            case "platinum":
                return 4;
            case "diamond":
                return 5;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "TierDTO{" +
                "id=" + id +
                ", tierName='" + tierName + '\'' +
                ", minPoints=" + minPoints +
                ", maxPoints=" + maxPoints +
                ", discountRate=" + discountRate +
                ", discountPercentage=" + getDiscountPercentage() + "%" +
                ", pointsRange='" + getPointsRange() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TierDTO tierDTO = (TierDTO) obj;

        if (id != null) {
            return id.equals(tierDTO.id);
        }

        return tierName != null && tierName.equals(tierDTO.tierName);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (tierName != null ? tierName.hashCode() : 0);
    }
}