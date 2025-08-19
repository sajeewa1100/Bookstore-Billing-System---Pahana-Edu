package model;

public class TierStatisticsDTO {
    private int totalTiers;
    private int totalClientsInBronze;
    private int totalClientsInSilver;
    private int totalClientsInGold;
    private int totalClientsInPlatinum;
    private String mostPopularTier;
    private double averageDiscountRate;

    // Constructors
    public TierStatisticsDTO() {}

    public TierStatisticsDTO(int totalTiers, int totalClientsInBronze, int totalClientsInSilver, 
                           int totalClientsInGold, int totalClientsInPlatinum, 
                           String mostPopularTier, double averageDiscountRate) {
        this.totalTiers = totalTiers;
        this.totalClientsInBronze = totalClientsInBronze;
        this.totalClientsInSilver = totalClientsInSilver;
        this.totalClientsInGold = totalClientsInGold;
        this.totalClientsInPlatinum = totalClientsInPlatinum;
        this.mostPopularTier = mostPopularTier;
        this.averageDiscountRate = averageDiscountRate;
    }

    // Getters and Setters
    public int getTotalTiers() {
        return totalTiers;
    }

    public void setTotalTiers(int totalTiers) {
        this.totalTiers = totalTiers;
    }

    public int getTotalClientsInBronze() {
        return totalClientsInBronze;
    }

    public void setTotalClientsInBronze(int totalClientsInBronze) {
        this.totalClientsInBronze = totalClientsInBronze;
    }

    public int getTotalClientsInSilver() {
        return totalClientsInSilver;
    }

    public void setTotalClientsInSilver(int totalClientsInSilver) {
        this.totalClientsInSilver = totalClientsInSilver;
    }

    public int getTotalClientsInGold() {
        return totalClientsInGold;
    }

    public void setTotalClientsInGold(int totalClientsInGold) {
        this.totalClientsInGold = totalClientsInGold;
    }

    public int getTotalClientsInPlatinum() {
        return totalClientsInPlatinum;
    }

    public void setTotalClientsInPlatinum(int totalClientsInPlatinum) {
        this.totalClientsInPlatinum = totalClientsInPlatinum;
    }

    public String getMostPopularTier() {
        return mostPopularTier;
    }

    public void setMostPopularTier(String mostPopularTier) {
        this.mostPopularTier = mostPopularTier;
    }

    public double getAverageDiscountRate() {
        return averageDiscountRate;
    }

    public void setAverageDiscountRate(double averageDiscountRate) {
        this.averageDiscountRate = averageDiscountRate;
    }

    public int getTotalClients() {
        return totalClientsInBronze + totalClientsInSilver + totalClientsInGold + totalClientsInPlatinum;
    }
}