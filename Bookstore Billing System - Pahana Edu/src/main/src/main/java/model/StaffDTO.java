package model;

import java.sql.Timestamp;

/**
 * Simplified Staff DTO for basic staff information
 */
public class StaffDTO {
    
    // Primary fields
    private int id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String position;
    private Timestamp createdAt;
    
    // User account linking fields
    private int userId;
    private boolean hasUserAccount;
    
    // Constructors
    public StaffDTO() {
        this.hasUserAccount = false;
        this.userId = 0;
        this.position = "staff"; // Default position
    }
    
    public StaffDTO(String employeeId, String firstName, String lastName) {
        this();
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Primary getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    // User account linking methods
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
        this.hasUserAccount = (userId > 0);
    }
    
    public boolean hasUserAccount() {
        return hasUserAccount;
    }
    
    public void setHasUserAccount(boolean hasUserAccount) {
        this.hasUserAccount = hasUserAccount;
        if (!hasUserAccount) {
            this.userId = 0;
        }
    }
    
    // Utility methods
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName.trim());
        }
        return fullName.toString();
    }
    
    public String getDisplayName() {
        String fullName = getFullName();
        if (fullName.isEmpty()) {
            return employeeId != null ? employeeId : "Unknown Staff";
        }
        return fullName;
    }
    
    // Validation methods
    public boolean isValid() {
        return employeeId != null && !employeeId.trim().isEmpty() &&
               firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty();
    }
    
    public String getValidationError() {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return "Employee ID is required";
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            return "First name is required";
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            return "Last name is required";
        }
        
        // Validate email format if provided
        if (email != null && !email.trim().isEmpty()) {
            if (!isValidEmail(email)) {
                return "Invalid email format";
            }
        }
        
        // Validate phone format if provided
        if (phone != null && !phone.trim().isEmpty()) {
            if (!isValidPhone(phone)) {
                return "Invalid phone format";
            }
        }
        
        return null; // No validation errors
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidPhone(String phone) {
        // Allow various phone formats
        String phoneRegex = "^[+]?[0-9\\s\\-\\(\\)]{7,20}$";
        return phone.matches(phoneRegex);
    }
    
    // Search method
    public boolean matchesSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true;
        }
        
        String term = searchTerm.toLowerCase().trim();
        
        return (employeeId != null && employeeId.toLowerCase().contains(term)) ||
               (firstName != null && firstName.toLowerCase().contains(term)) ||
               (lastName != null && lastName.toLowerCase().contains(term)) ||
               (email != null && email.toLowerCase().contains(term)) ||
               (phone != null && phone.contains(term)) ||
               (position != null && position.toLowerCase().contains(term));
    }
    
    // Object methods
    @Override
    public String toString() {
        return "StaffDTO{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", position='" + position + '\'' +
                ", hasUserAccount=" + hasUserAccount +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        StaffDTO staffDTO = (StaffDTO) obj;
        
        if (id != 0 && staffDTO.id != 0) {
            return id == staffDTO.id;
        }
        
        // If IDs not available, compare by employee ID
        return employeeId != null && employeeId.equals(staffDTO.employeeId);
    }
    
    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return employeeId != null ? employeeId.hashCode() : 0;
    }
}