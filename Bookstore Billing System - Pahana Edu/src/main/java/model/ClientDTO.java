package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Client entity
 * Represents client information in the bookstore system
 */
public class ClientDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String accountNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String zip;
    private int loyaltyPoints;
    private String tierLevel;
    private boolean sendMailAuto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public ClientDTO() {
        this.loyaltyPoints = 0;
        this.tierLevel = "BRONZE";
        this.sendMailAuto = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with essential fields
    public ClientDTO(String firstName, String lastName, String email, String phone) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
    
    // Full constructor
    public ClientDTO(Long id, String accountNumber, String firstName, String lastName, 
                    String email, String phone, String street, String city, String state, 
                    String zip, int loyaltyPoints, String tierLevel, boolean sendMailAuto) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.loyaltyPoints = loyaltyPoints;
        this.tierLevel = tierLevel;
        this.sendMailAuto = sendMailAuto;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZip() {
        return zip;
    }
    
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
        updateTierLevel();
    }
    
    public String getTierLevel() {
        return tierLevel;
    }
    
    public void setTierLevel(String tierLevel) {
        this.tierLevel = tierLevel;
    }
    
    public boolean isSendMailAuto() {
        return sendMailAuto;
    }
    
    public void setSendMailAuto(boolean sendMailAuto) {
        this.sendMailAuto = sendMailAuto;
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
    
    // Utility methods
    
    /**
     * Get full name
     */
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
    
    /**
     * Get full address
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (street != null) address.append(street);
        if (city != null) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (state != null) {
            if (address.length() > 0) address.append(", ");
            address.append(state);
        }
        if (zip != null) {
            if (address.length() > 0) address.append(" ");
            address.append(zip);
        }
        return address.toString();
    }
    
    /**
     * Update tier level based on loyalty points
     */
    private void updateTierLevel() {
        if (loyaltyPoints >= 10000) {
            this.tierLevel = "PLATINUM";
        } else if (loyaltyPoints >= 5000) {
            this.tierLevel = "GOLD";
        } else if (loyaltyPoints >= 1000) {
            this.tierLevel = "SILVER";
        } else {
            this.tierLevel = "BRONZE";
        }
    }
    
    /**
     * Add loyalty points and update tier
     */
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        updateTierLevel();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Generate account number if not exists
     */
    public void generateAccountNumber() {
        if (this.accountNumber == null || this.accountNumber.isEmpty()) {
            this.accountNumber = "ACC" + System.currentTimeMillis();
        }
    }
    
    /**
     * Validate client data
     */
    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty() &&
               street != null && !street.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               state != null && !state.trim().isEmpty() &&
               zip != null && !zip.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", tierLevel='" + tierLevel + '\'' +
                ", sendMailAuto=" + sendMailAuto +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ClientDTO clientDTO = (ClientDTO) obj;
        
        if (id != null) {
            return id.equals(clientDTO.id);
        }
        
        return email != null && email.equals(clientDTO.email);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (email != null ? email.hashCode() : 0);
    }
}