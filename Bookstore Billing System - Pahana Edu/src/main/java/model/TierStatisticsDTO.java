package model;

import java.io.Serializable;

/**
 * Data Transfer Object for Tier Statistics
 * Used for dashboard and reporting purposes
 */
public class TierStatisticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int totalTiers;
    private int totalClientsInBronze;
    private int totalClientsInSilver;
    private int totalClientsInGold;
    private int totalClientsInPlatinum;
    private double averageDiscountRate;
    private String mostPopularTier;

    // Default constructor
    public TierStatisticsDTO() {
        this.totalTiers = 0;
        this.totalClientsInBronze = 0;
        this.totalClientsInSilver = 0;
        this.totalClientsInGold = 0;
        this.totalClientsInPlatinum = 0;
        this.averageDiscountRate = 0.0;
        this.mostPopularTier = "Bronze";
    }

    // Getters and Setters
    public int getTotalTiers() { return totalTiers; }
    public void setTotalTiers(int totalTiers) { this.totalTiers = totalTiers; }

    public int getTotalClientsInBronze() { return totalClientsInBronze; }
    public void setTotalClientsInBronze(int totalClientsInBronze) { this.totalClientsInBronze = totalClientsInBronze; }

    public int getTotalClientsInSilver() { return totalClientsInSilver; }
    public void setTotalClientsInSilver(int totalClientsInSilver) { this.totalClientsInSilver = totalClientsInSilver; }

    public int getTotalClientsInGold() { return totalClientsInGold; }
    public void setTotalClientsInGold(int totalClientsInGold) { this.totalClientsInGold = totalClientsInGold; }

    public int getTotalClientsInPlatinum() { return totalClientsInPlatinum; }
    public void setTotalClientsInPlatinum(int totalClientsInPlatinum) { this.totalClientsInPlatinum = totalClientsInPlatinum; }

    public double getAverageDiscountRate() { return averageDiscountRate; }
    public void setAverageDiscountRate(double averageDiscountRate) { this.averageDiscountRate = averageDiscountRate; }

    public String getMostPopularTier() { return mostPopularTier; }
    public void setMostPopularTier(String mostPopularTier) { this.mostPopularTier = mostPopularTier; }

    // Utility methods
    public int getTotalClients() {
        return totalClientsInBronze + totalClientsInSilver + totalClientsInGold + totalClientsInPlatinum;
    }
    
    public double getBronzePercentage() {
        int total = getTotalClients();
        return total > 0 ? (totalClientsInBronze * 100.0) / total : 0.0;
    }
    
    public double getSilverPercentage() {
        int total = getTotalClients();
        return total > 0 ? (totalClientsInSilver * 100.0) / total : 0.0;
    }
    
    public double getGoldPercentage() {
        int total = getTotalClients();
        return total > 0 ? (totalClientsInGold * 100.0) / total : 0.0;
    }
    
    public double getPlatinumPercentage() {
        int total = getTotalClients();
        return total > 0 ? (totalClientsInPlatinum * 100.0) / total : 0.0;
    }

    @Override
    public String toString() {
        return "TierStatisticsDTO{" +
                "totalTiers=" + totalTiers +
                ", totalClients=" + getTotalClients() +
                ", bronze=" + totalClientsInBronze +
                ", silver=" + totalClientsInSilver +
                ", gold=" + totalClientsInGold +
                ", platinum=" + totalClientsInPlatinum +
                ", averageDiscountRate=" + averageDiscountRate +
                ", mostPopularTier='" + mostPopularTier + '\'' +
                '}';
    }
}