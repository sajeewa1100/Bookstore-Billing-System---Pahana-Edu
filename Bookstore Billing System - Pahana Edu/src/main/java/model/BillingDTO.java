package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Billing
 * Represents a billing transaction with items and client information
 */
public class BillingDTO {
    
    private Long id;
    private String billNumber;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private LocalDateTime billDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String status; // PENDING, COMPLETED, CANCELLED
    private String paymentMethod; // CASH, CARD, ONLINE
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Bill Items
    private List<BillItemDTO> items;
    
    /**
     * Default constructor
     */
    public BillingDTO() {
        this.items = new ArrayList<>();
        this.status = "PENDING";
        this.billDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
    }
    
    /**
     * Constructor with basic info
     */
    public BillingDTO(Long clientId, String clientName, String clientEmail, String clientPhone) {
        this();
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        generateBillNumber();
    }
    
    /**
     * Generate unique bill number
     */
    public void generateBillNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        this.billNumber = "BILL-" + LocalDateTime.now().format(formatter);
    }
    
    /**
     * Add item to bill
     */
    public void addItem(BillItemDTO item) {
        if (item != null) {
            this.items.add(item);
            calculateTotals();
        }
    }
    
    /**
     * Remove item from bill
     */
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            this.items.remove(index);
            calculateTotals();
        }
    }
    
    /**
     * Calculate totals based on items
     */
    public void calculateTotals() {
        BigDecimal itemsTotal = BigDecimal.ZERO;
        
        for (BillItemDTO item : items) {
            itemsTotal = itemsTotal.add(item.getTotal());
        }
        
        this.subtotal = itemsTotal;
        
        // Calculate tax (assuming 8% tax rate)
        this.taxAmount = this.subtotal.multiply(new BigDecimal("0.08"));
        
        // Calculate total
        this.totalAmount = this.subtotal.add(this.taxAmount).subtract(this.discountAmount);
        
        // Ensure total is not negative
        if (this.totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.totalAmount = BigDecimal.ZERO;
        }
    }
    
    /**
     * Apply discount percentage
     */
    public void applyDiscountPercentage(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) >= 0 && percentage.compareTo(new BigDecimal("100")) <= 0) {
            this.discountAmount = this.subtotal.multiply(percentage).divide(new BigDecimal("100"));
            calculateTotals();
        }
    }
    
    /**
     * Apply fixed discount amount
     */
    public void applyDiscountAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            this.discountAmount = amount;
            calculateTotals();
        }
    }
    
    /**
     * Get formatted bill date
     */
    public String getFormattedBillDate() {
        if (billDate != null) {
            return billDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return "";
    }
    
    /**
     * Get formatted bill date (short)
     */
    public String getFormattedBillDateShort() {
        if (billDate != null) {
            return billDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return "";
    }
    
    /**
     * Get total items count
     */
    public int getTotalItemsCount() {
        return items.stream().mapToInt(BillItemDTO::getQuantity).sum();
    }
    
    /**
     * Check if bill is completed
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }
    
    /**
     * Check if bill is pending
     */
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
    
    /**
     * Check if bill is cancelled
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(this.status);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBillNumber() {
        return billNumber;
    }
    
    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getClientEmail() {
        return clientEmail;
    }
    
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    
    public String getClientPhone() {
        return clientPhone;
    }
    
    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }
    
    public LocalDateTime getBillDate() {
        return billDate;
    }
    
    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public List<BillItemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<BillItemDTO> items) {
        this.items = items;
        calculateTotals();
    }
    
    @Override
    public String toString() {
        return "BillingDTO{" +
                "id=" + id +
                ", billNumber='" + billNumber + '\'' +
                ", clientName='" + clientName + '\'' +
                ", billDate=" + billDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", itemsCount=" + items.size() +
                '}';
    }
}