package model;

public class ClientStatisticsDTO {
    private int totalClients;
    private int autoMailEnabledCount;
    private int clientsWithPhoneCount;
    private int clientsWithAddressCount;
    private int autoMailPercentage;
    private int phonePercentage;
    private int addressPercentage;
    private int totalLoyaltyPoints;
    private double averageLoyaltyPoints;
    private String mostPopularTier;

    // Constructors
    public ClientStatisticsDTO() {}

    // Getters and Setters
    public int getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(int totalClients) {
        this.totalClients = totalClients;
    }

    public int getAutoMailEnabledCount() {
        return autoMailEnabledCount;
    }

    public void setAutoMailEnabledCount(int autoMailEnabledCount) {
        this.autoMailEnabledCount = autoMailEnabledCount;
    }

    public int getClientsWithPhoneCount() {
        return clientsWithPhoneCount;
    }

    public void setClientsWithPhoneCount(int clientsWithPhoneCount) {
        this.clientsWithPhoneCount = clientsWithPhoneCount;
    }

    public int getClientsWithAddressCount() {
        return clientsWithAddressCount;
    }

    public void setClientsWithAddressCount(int clientsWithAddressCount) {
        this.clientsWithAddressCount = clientsWithAddressCount;
    }

    public int getAutoMailPercentage() {
        return autoMailPercentage;
    }

    public void setAutoMailPercentage(int autoMailPercentage) {
        this.autoMailPercentage = autoMailPercentage;
    }

    public int getPhonePercentage() {
        return phonePercentage;
    }

    public void setPhonePercentage(int phonePercentage) {
        this.phonePercentage = phonePercentage;
    }

    public int getAddressPercentage() {
        return addressPercentage;
    }

    public void setAddressPercentage(int addressPercentage) {
        this.addressPercentage = addressPercentage;
    }

    public int getTotalLoyaltyPoints() {
        return totalLoyaltyPoints;
    }

    public void setTotalLoyaltyPoints(int totalLoyaltyPoints) {
        this.totalLoyaltyPoints = totalLoyaltyPoints;
    }

    public double getAverageLoyaltyPoints() {
        return averageLoyaltyPoints;
    }

    public void setAverageLoyaltyPoints(double averageLoyaltyPoints) {
        this.averageLoyaltyPoints = averageLoyaltyPoints;
    }

    public String getMostPopularTier() {
        return mostPopularTier;
    }

    public void setMostPopularTier(String mostPopularTier) {
        this.mostPopularTier = mostPopularTier;
    }
}