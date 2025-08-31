package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.sql.Timestamp;


public class InvoiceDTO {
    private int id;
    private String invoiceNumber;
    private int clientId;
    private int staffId;
    private Date invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal loyaltyDiscount;
    private BigDecimal totalAmount;
    private BigDecimal cashGiven;
    private BigDecimal changeAmount;
    private int loyaltyPointsEarned;
    private Timestamp createdAt;
    
    // Additional fields for display
    private ClientDTO client;
    private List<InvoiceItemDTO> items;
    private String staffName;
    
    // Constructors
    public InvoiceDTO() {}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    
    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getLoyaltyDiscount() { return loyaltyDiscount; }
    public void setLoyaltyDiscount(BigDecimal loyaltyDiscount) { this.loyaltyDiscount = loyaltyDiscount; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getCashGiven() { return cashGiven; }
    public void setCashGiven(BigDecimal cashGiven) { this.cashGiven = cashGiven; }
    
    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }
    
    public int getLoyaltyPointsEarned() { return loyaltyPointsEarned; }
    public void setLoyaltyPointsEarned(int loyaltyPointsEarned) { this.loyaltyPointsEarned = loyaltyPointsEarned; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public ClientDTO getClient() { return client; }
    public void setClient(ClientDTO client) { this.client = client; }
    
    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }
    
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
}

