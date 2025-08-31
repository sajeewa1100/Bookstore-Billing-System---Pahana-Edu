package model;



public class ClientDTO {
    private int id;
    private String accountNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressZip;
    private boolean sendMailAuto;
    private int loyaltyPoints;
    private String tierLevel;
    
    // Constructors
    public ClientDTO() {}
    
    public ClientDTO(int id, String accountNumber, String firstName, String lastName, 
                     String email, String phone, int loyaltyPoints, String tierLevel) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.loyaltyPoints = loyaltyPoints;
        this.tierLevel = tierLevel;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddressStreet() { return addressStreet; }
    public void setAddressStreet(String addressStreet) { this.addressStreet = addressStreet; }
    
    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }
    
    public String getAddressState() { return addressState; }
    public void setAddressState(String addressState) { this.addressState = addressState; }
    
    public String getAddressZip() { return addressZip; }
    public void setAddressZip(String addressZip) { this.addressZip = addressZip; }
    
    public boolean isSendMailAuto() { return sendMailAuto; }
    public void setSendMailAuto(boolean sendMailAuto) { this.sendMailAuto = sendMailAuto; }
    
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    
    public String getTierLevel() { return tierLevel; }
    public void setTierLevel(String tierLevel) { this.tierLevel = tierLevel; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
