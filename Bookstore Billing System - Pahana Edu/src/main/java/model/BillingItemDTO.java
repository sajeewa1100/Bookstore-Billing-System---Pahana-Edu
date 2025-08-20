package model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object for Billing Item entity
 * Represents individual items within a billing record
 * Based on your working database structure
 */
public class BillingItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long billingId;
    private Integer bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
    private BookDTO book; // Optional - for holding full book object
    
    // Default constructor
    public BillingItemDTO() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
    }
    
    // Constructor with essential fields
    public BillingItemDTO(Integer bookId, String bookTitle, String bookAuthor, 
                         String bookIsbn, BigDecimal unitPrice, int quantity) {
        this();
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookIsbn = bookIsbn;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        calculateTotalPrice();
    }
    
    // Constructor with book object (extracts info from BookDTO)
    public BillingItemDTO(BookDTO book, int quantity) {
        this();
        if (book != null) {
            this.bookId = book.getId();
            this.bookTitle = book.getTitle();
            this.bookAuthor = book.getAuthor();
            this.bookIsbn = book.getIsbn();
            this.unitPrice = book.getPrice();
            this.book = book;
        }
        this.quantity = quantity;
        calculateTotalPrice();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getBillingId() { return billingId; }
    public void setBillingId(Long billingId) { this.billingId = billingId; }
    
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    
    public String getBookTitle() { return bookTitle != null ? bookTitle : "Unknown Book"; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookAuthor() { return bookAuthor != null ? bookAuthor : "Unknown Author"; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    
    public String getBookIsbn() { return bookIsbn != null ? bookIsbn : ""; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        calculateTotalPrice();
    }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public BookDTO getBook() { return book; }
    public void setBook(BookDTO book) { 
        this.book = book;
        if (book != null) {
            this.bookId = book.getId();
            this.bookTitle = book.getTitle();
            this.bookAuthor = book.getAuthor();
            this.bookIsbn = book.getIsbn();
            if (this.unitPrice == null || this.unitPrice.compareTo(BigDecimal.ZERO) == 0) {
                this.unitPrice = book.getPrice();
                calculateTotalPrice();
            }
        }
    }
    
    // Utility methods
    
    /**
     * Calculate total price based on quantity and unit price
     */
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity > 0) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
    
    /**
     * Check if item has sufficient stock (only if book object is available)
     */
    public boolean hasSufficientStock() {
        if (book == null) {
            return true; // Assume available if we don't have book object
        }
        return book.getQuantity() >= quantity;
    }
    
    /**
     * Get available stock (only if book object is available)
     */
    public int getAvailableStock() {
        return book != null ? book.getQuantity() : 0;
    }
    
    /**
     * Validate billing item data
     */
    public boolean isValid() {
        return bookId != null && 
               bookTitle != null && !bookTitle.trim().isEmpty() &&
               quantity > 0 && 
               unitPrice != null && 
               unitPrice.compareTo(BigDecimal.ZERO) > 0 &&
               totalPrice != null &&
               totalPrice.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if quantity can be increased (only if book object is available)
     */
    public boolean canIncreaseQuantity() {
        if (book == null) {
            return true; // Allow if we don't have stock info
        }
        return (quantity + 1) <= book.getQuantity();
    }
    
    /**
     * Check if quantity can be decreased
     */
    public boolean canDecreaseQuantity() {
        return quantity > 1;
    }
    
    /**
     * Increase quantity by 1 if possible
     */
    public boolean increaseQuantity() {
        if (canIncreaseQuantity()) {
            setQuantity(quantity + 1);
            return true;
        }
        return false;
    }
    
    /**
     * Decrease quantity by 1 if possible
     */
    public boolean decreaseQuantity() {
        if (canDecreaseQuantity()) {
            setQuantity(quantity - 1);
            return true;
        }
        return false;
    }
    
    /**
     * Get formatted unit price
     */
    public String getFormattedUnitPrice() {
        return unitPrice != null ? String.format("Rs. %.2f", unitPrice) : "Rs. 0.00";
    }
    
    /**
     * Get formatted total price
     */
    public String getFormattedTotalPrice() {
        return totalPrice != null ? String.format("Rs. %.2f", totalPrice) : "Rs. 0.00";
    }
    
    /**
     * Get book display info
     */
    public String getBookDisplayInfo() {
        StringBuilder info = new StringBuilder();
        info.append(getBookTitle());
        if (bookAuthor != null && !bookAuthor.trim().isEmpty()) {
            info.append(" by ").append(bookAuthor);
        }
        if (bookIsbn != null && !bookIsbn.trim().isEmpty()) {
            info.append(" (ISBN: ").append(bookIsbn).append(")");
        }
        return info.toString();
    }
    
    /**
     * Create a copy of this billing item
     */
    public BillingItemDTO copy() {
        BillingItemDTO copy = new BillingItemDTO();
        copy.setId(this.id);
        copy.setBillingId(this.billingId);
        copy.setBookId(this.bookId);
        copy.setBookTitle(this.bookTitle);
        copy.setBookAuthor(this.bookAuthor);
        copy.setBookIsbn(this.bookIsbn);
        copy.setUnitPrice(this.unitPrice);
        copy.setQuantity(this.quantity);
        copy.setTotalPrice(this.totalPrice);
        copy.setBook(this.book);
        return copy;
    }
    
    @Override
    public String toString() {
        return "BillingItemDTO{" +
                "id=" + id +
                ", billingId=" + billingId +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        BillingItemDTO that = (BillingItemDTO) o;
        
        if (id != null) {
            return id.equals(that.id);
        }
        
        return bookId != null && bookId.equals(that.bookId) &&
               billingId != null && billingId.equals(that.billingId);
    }
    
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        int result = bookId != null ? bookId.hashCode() : 0;
        result = 31 * result + (billingId != null ? billingId.hashCode() : 0);
        return result;
    }
}