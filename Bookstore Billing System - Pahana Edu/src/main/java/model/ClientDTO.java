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
    private Integer loyaltyPoints;
    private String tierLevel;
    private Boolean sendMailAuto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long tierId;
    private TierDTO tier;
    
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
                    String zip, Integer loyaltyPoints, String tierLevel, Boolean sendMailAuto) {
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
        this.loyaltyPoints = loyaltyPoints != null ? loyaltyPoints : 0;
        this.tierLevel = tierLevel != null ? tierLevel : "BRONZE";
        this.sendMailAuto = sendMailAuto != null ? sendMailAuto : false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with tier information
    public ClientDTO(Long id, String accountNumber, String firstName, String lastName, 
                    String email, String phone, String street, String city, String state, 
                    String zip, Integer loyaltyPoints, String tierLevel, Boolean sendMailAuto,
                    Long tierId, TierDTO tier) {
        this(id, accountNumber, firstName, lastName, email, phone, street, city, state,
             zip, loyaltyPoints, tierLevel, sendMailAuto);
        this.tierId = tierId;
        this.tier = tier;
        if (tier != null) {
            this.tierLevel = tier.getTierName();
        }
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
    
    /**
     * Get loyalty points with null safety
     */
    public Integer getLoyaltyPoints() {
        return loyaltyPoints != null ? loyaltyPoints : 0;
    }
    
    /**
     * Set loyalty points with null safety
     */
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints != null ? loyaltyPoints : 0;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Helper method to get loyalty points as primitive int
     */
    public int getLoyaltyPointsAsInt() {
        return loyaltyPoints != null ? loyaltyPoints : 0;
    }
    
    /**
     * Get tier level with null safety
     */
    public String getTierLevel() {
        return tierLevel != null ? tierLevel : "BRONZE";
    }
    
    /**
     * Set tier level
     */
    public void setTierLevel(String tierLevel) {
        this.tierLevel = tierLevel != null ? tierLevel : "BRONZE";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Get tier ID
     */
    public Long getTierId() {
        return tierId;
    }
    
    /**
     * Set tier ID
     */
    public void setTierId(Long tierId) {
        this.tierId = tierId;
    }
    
    /**
     * Get tier object
     */
    public TierDTO getTier() {
        return tier;
    }
    
    /**
     * Set tier object and update tier level
     */
    public void setTier(TierDTO tier) {
        this.tier = tier;
        if (tier != null) {
            this.tierId = tier.getId();
            this.tierLevel = tier.getTierName();
        }
    }
    
    /**
     * Get send mail auto with null safety (Boolean getter)
     */
    public Boolean getSendMailAuto() {
        return sendMailAuto != null ? sendMailAuto : false;
    }
    
    /**
     * Set send mail auto with null safety
     */
    public void setSendMailAuto(Boolean sendMailAuto) {
        this.sendMailAuto = sendMailAuto != null ? sendMailAuto : false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if auto mail is enabled (boolean getter for JSP compatibility)
     */
    public boolean isSendMailAuto() {
        return sendMailAuto != null && sendMailAuto;
    }
    
    /**
     * Helper method to check if auto mail is enabled
     */
    public boolean isAutoMailEnabled() {
        return sendMailAuto != null && sendMailAuto;
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
        return (firstName != null ? firstName : "").trim() + " " + 
               (lastName != null ? lastName : "").trim();
    }
    
    /**
     * Get full address
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (street != null && !street.trim().isEmpty()) {
            address.append(street);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(state);
        }
        if (zip != null && !zip.trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(zip);
        }
        return address.toString();
    }
    
    /**
     * Add loyalty points
     */
    public void addLoyaltyPoints(int points) {
        if (this.loyaltyPoints == null) {
            this.loyaltyPoints = 0;
        }
        this.loyaltyPoints += points;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Subtract loyalty points (for redemptions)
     */
    public boolean subtractLoyaltyPoints(int points) {
        if (this.loyaltyPoints == null) {
            this.loyaltyPoints = 0;
        }
        if (this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
            this.updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
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
     * Validate essential client data
     */
    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               isValidEmail() &&
               phone != null && !phone.trim().isEmpty();
    }
    
    /**
     * Validate complete client data including address
     */
    public boolean isCompletelyValid() {
        return isValid() &&
               street != null && !street.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               state != null && !state.trim().isEmpty() &&
               zip != null && !zip.trim().isEmpty();
    }
    
    /**
     * Basic email validation
     */
    public boolean isValidEmail() {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Validate Sri Lankan phone number
     */
    public boolean isValidSriLankanPhone() {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        String cleanPhone = phone.replaceAll("[^\\d+]", "");
        
        // Sri Lankan phone patterns
        if (cleanPhone.startsWith("+94")) {
            String withoutCountry = cleanPhone.substring(3);
            return withoutCountry.length() == 9 && withoutCountry.startsWith("7");
        } else if (cleanPhone.startsWith("0")) {
            return cleanPhone.length() >= 9 && cleanPhone.length() <= 10;
        } else if (cleanPhone.length() == 9 && cleanPhone.startsWith("7")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Format phone number for display
     */
    public String getFormattedPhone() {
        if (phone == null) return "";
        
        String cleanPhone = phone.replaceAll("[^\\d+]", "");
        
        if (cleanPhone.startsWith("+94") && cleanPhone.length() == 12) {
            return cleanPhone.replaceAll("(\\+94)(\\d{2})(\\d{3})(\\d{4})", "$1 $2 $3 $4");
        } else if (cleanPhone.startsWith("0") && cleanPhone.length() == 10) {
            return cleanPhone.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1 $2 $3");
        }
        
        return phone; // Return original if no formatting rule matches
    }
    
    /**
     * Get initials from first and last name
     */
    public String getInitials() {
        StringBuilder initials = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            initials.append(firstName.trim().charAt(0));
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            initials.append(lastName.trim().charAt(0));
        }
        return initials.toString().toUpperCase();
    }
    
    /**
     * Check if client has sufficient loyalty points
     */
    public boolean hasSufficientPoints(int requiredPoints) {
        return getLoyaltyPointsAsInt() >= requiredPoints;
    }
    
    /**
     * Get tier display name with fallback
     */
    public String getTierDisplayName() {
        if (tier != null && tier.getTierName() != null) {
            return tier.getTierName();
        }
        return getTierLevel();
    }
    
    /**
     * Check if client is in a specific tier
     */
    public boolean isInTier(String tierName) {
        if (tierName == null) return false;
        return tierName.equalsIgnoreCase(getTierLevel());
    }
    
    /**
     * Get discount percentage from tier
     */
    public double getDiscountPercentage() {
        if (tier != null && tier.getDiscountPercentage() != null) {
            return tier.getDiscountPercentage();
        }
        return 0.0;
    }
    
    /**
     * Check if address is complete
     */
    public boolean hasCompleteAddress() {
        return street != null && !street.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               state != null && !state.trim().isEmpty() &&
               zip != null && !zip.trim().isEmpty();
    }
    
    /**
     * Update timestamp
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
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
                ", tierId=" + tierId +
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