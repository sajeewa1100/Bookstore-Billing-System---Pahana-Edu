package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Billing entity
 * Represents billing information in the bookstore system
 */
public class BillingDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String billNumber;
    private Long clientId;
    private ClientDTO client;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String status; // PENDING, COMPLETED, CANCELLED
    private String paymentMethod; // CASH, CARD, MOBILE
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BillingItemDTO> items;
    
    // Default constructor
    public BillingDTO() {
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.status = "PENDING";
        this.paymentMethod = "CASH";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.items = new ArrayList<>();
    }
    
    // Constructor with essential fields
    public BillingDTO(Long clientId, String paymentMethod) {
        this();
        this.clientId = clientId;
        this.paymentMethod = paymentMethod;
        generateBillNumber();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
    
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    
    public ClientDTO getClient() { return client; }
    public void setClient(ClientDTO client) { 
        this.client = client;
        if (client != null) {
            this.clientId = client.getId();
        }
    }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<BillingItemDTO> getItems() { return items; }
    public void setItems(List<BillingItemDTO> items) { this.items = items; }
    
    // Utility methods
    
    /**
     * Generate unique bill number
     */
    public void generateBillNumber() {
        if (this.billNumber == null || this.billNumber.isEmpty()) {
            this.billNumber = "BILL-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
                            + "-" + System.currentTimeMillis() % 10000;
        }
    }
    
    /**
     * Add item to billing
     */
    public void addItem(BillingItemDTO item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        item.setBillingId(this.id);
        items.add(item);
        recalculateAmounts();
    }
    
    /**
     * Remove item from billing
     */
    public void removeItem(BillingItemDTO item) {
        if (items != null) {
            items.remove(item);
            recalculateAmounts();
        }
    }
    
    /**
     * Remove item by index
     */
    public void removeItem(int index) {
        if (items != null && index >= 0 && index < items.size()) {
            items.remove(index);
            recalculateAmounts();
        }
    }
    
    /**
     * Clear all items
     */
    public void clearItems() {
        if (items != null) {
            items.clear();
            recalculateAmounts();
        }
    }
    
    /**
     * Get total items count
     */
    public int getItemsCount() {
        return items != null ? items.size() : 0;
    }
    
    /**
     * Get total quantity of all items
     */
    public int getTotalQuantity() {
        if (items == null) return 0;
        return items.stream().mapToInt(BillingItemDTO::getQuantity).sum();
    }
    
    /**
     * Recalculate all amounts based on items
     */
    public void recalculateAmounts() {
        if (items == null || items.isEmpty()) {
            this.subtotal = BigDecimal.ZERO;
            this.discountAmount = BigDecimal.ZERO;
            this.taxAmount = BigDecimal.ZERO;
            this.totalAmount = BigDecimal.ZERO;
            return;
        }
        
        // Calculate subtotal
        this.subtotal = items.stream()
                .map(BillingItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Apply client tier discount if available
        if (client != null && client.getTier() != null) {
            BigDecimal discountRate = client.getTier().getDiscountRate();
            if (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
                this.discountAmount = subtotal.multiply(discountRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }
        
        // Calculate tax (assuming 8% tax rate)
        BigDecimal taxRate = new BigDecimal("0.08");
        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        this.taxAmount = taxableAmount.multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        // Calculate total
        this.totalAmount = subtotal.subtract(discountAmount).add(taxAmount);
        
        // Update timestamp
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Apply custom discount
     */
    public void applyDiscount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        recalculateTotal();
    }
    
    /**
     * Apply discount percentage
     */
    public void applyDiscountPercentage(BigDecimal discountPercentage) {
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            this.discountAmount = subtotal.multiply(discountPercentage.divide(new BigDecimal("100")))
                                          .setScale(2, BigDecimal.ROUND_HALF_UP);
            recalculateTotal();
        }
    }
    
    /**
     * Recalculate total only (when discount or tax is manually set)
     */
    private void recalculateTotal() {
        this.totalAmount = subtotal.subtract(discountAmount).add(taxAmount);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if billing is completed
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    /**
     * Check if billing is cancelled
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }
    
    /**
     * Check if billing is pending
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    /**
     * Mark billing as completed
     */
    public void markAsCompleted() {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Mark billing as cancelled
     */
    public void markAsCancelled() {
        this.status = "CANCELLED";
        this.updatedAt = LocalDateTime.now();
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
    
    /**
     * Get formatted created time
     */
    public String getFormattedCreatedTime() {
        if (createdAt != null) {
            return createdAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        return "";
    }
    
    /**
     * Get client name for display
     */
    public String getClientName() {
        return client != null ? client.getFullName() : "Unknown Client";
    }
    
    /**
     * Get client account number for display
     */
    public String getClientAccountNumber() {
        return client != null ? client.getAccountNumber() : "";
    }
    
    /**
     * Get discount percentage
     */
    public BigDecimal getDiscountPercentage() {
        if (subtotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return discountAmount.multiply(new BigDecimal("100"))
                           .divide(subtotal, 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Get tax percentage (8%)
     */
    public BigDecimal getTaxPercentage() {
        return new BigDecimal("8.00");
    }
    
    /**
     * Validate billing data
     */
    public boolean isValid() {
        return clientId != null && 
               items != null && !items.isEmpty() &&
               totalAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Get status color for UI
     */
    public String getStatusColor() {
        switch (status) {
            case "COMPLETED":
                return "success";
            case "CANCELLED":
                return "danger";
            case "PENDING":
            default:
                return "warning";
        }
    }
    
    /**
     * Get payment method icon
     */
    public String getPaymentMethodIcon() {
        switch (paymentMethod) {
            case "CARD":
                return "fas fa-credit-card";
            case "MOBILE":
                return "fas fa-mobile-alt";
            case "CASH":
            default:
                return "fas fa-money-bill-wave";
        }
    }
    
    @Override
    public String toString() {
        return "BillingDTO{" +
                "id=" + id +
                ", billNumber='" + billNumber + '\'' +
                ", clientId=" + clientId +
                ", subtotal=" + subtotal +
                ", discountAmount=" + discountAmount +
                ", taxAmount=" + taxAmount +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", itemsCount=" + getItemsCount() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        BillingDTO that = (BillingDTO) o;
        
        if (id != null) {
            return id.equals(that.id);
        }
        
        return billNumber != null && billNumber.equals(that.billNumber);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (billNumber != null ? billNumber.hashCode() : 0);
    }
}