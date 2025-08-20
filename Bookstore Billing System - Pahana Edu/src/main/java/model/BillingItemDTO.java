package model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object for Billing Item entity
 * Represents individual items in a billing record
 */
public class BillingItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long billingId;
    private Integer bookId;
    private BookDTO book;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    
    // Default constructor
    public BillingItemDTO() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
    }
    
    // Constructor with book and quantity
    public BillingItemDTO(BookDTO book, Integer quantity) {
        this();
        setBook(book);
        setQuantity(quantity);
    }
    
    // Constructor with book details
    public BillingItemDTO(Integer bookId, String bookTitle, String bookAuthor, 
                         String bookIsbn, BigDecimal unitPrice, Integer quantity) {
        this();
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookIsbn = bookIsbn;
        this.unitPrice = unitPrice;
        setQuantity(quantity);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getBillingId() { return billingId; }
    public void setBillingId(Long billingId) { this.billingId = billingId; }
    
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    
    public BookDTO getBook() { return book; }
    public void setBook(BookDTO book) {
        this.book = book;
        if (book != null) {
            this.bookId = book.getId();
            this.bookTitle = book.getTitle();
            this.bookAuthor = book.getAuthor();
            this.bookIsbn = book.getIsbn();
            this.unitPrice = book.getPrice();
            calculateTotalPrice();
        }
    }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    
    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        calculateTotalPrice();
    }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity != null && quantity > 0 ? quantity : 1;
        calculateTotalPrice();
    }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    // Utility methods
    
    /**
     * Calculate total price based on unit price and quantity
     */
    private void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
    
    /**
     * Increase quantity by 1
     */
    public void increaseQuantity() {
        setQuantity(this.quantity + 1);
    }
    
    /**
     * Decrease quantity by 1 (minimum 1)
     */
    public void decreaseQuantity() {
        if (this.quantity > 1) {
            setQuantity(this.quantity - 1);
        }
    }
    
    /**
     * Check if this item has valid book information
     */
    public boolean hasValidBook() {
        return bookId != null && bookTitle != null && !bookTitle.trim().isEmpty();
    }
    
    /**
     * Get book display name (title - author)
     */
    public String getBookDisplayName() {
        StringBuilder displayName = new StringBuilder();
        if (bookTitle != null && !bookTitle.trim().isEmpty()) {
            displayName.append(bookTitle);
        }
        if (bookAuthor != null && !bookAuthor.trim().isEmpty()) {
            if (displayName.length() > 0) {
                displayName.append(" - ");
            }
            displayName.append(bookAuthor);
        }
        return displayName.toString();
    }
    
    /**
     * Get formatted unit price
     */
    public String getFormattedUnitPrice() {
        return unitPrice != null ? "Rs. " + unitPrice.toString() : "Rs. 0.00";
    }
    
    /**
     * Get formatted total price
     */
    public String getFormattedTotalPrice() {
        return totalPrice != null ? "Rs. " + totalPrice.toString() : "Rs. 0.00";
    }
    
    /**
     * Validate item data
     */
    public boolean isValid() {
        return bookId != null && 
               bookTitle != null && !bookTitle.trim().isEmpty() &&
               unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0 &&
               quantity != null && quantity > 0;
    }
    
    /**
     * Check if sufficient stock is available
     */
    public boolean hasSufficientStock() {
        return book != null && book.getQuantity() >= quantity;
    }
    
    /**
     * Get stock status message
     */
    public String getStockStatus() {
        if (book == null) {
            return "Book information not available";
        }
        
        if (book.getQuantity() == 0) {
            return "Out of stock";
        } else if (book.getQuantity() < quantity) {
            return "Insufficient stock (Available: " + book.getQuantity() + ")";
        } else if (book.getQuantity() <= 5) {
            return "Low stock (" + book.getQuantity() + " remaining)";
        } else {
            return "In stock (" + book.getQuantity() + " available)";
        }
    }
    
    /**
     * Get stock status color for UI
     */
    public String getStockStatusColor() {
        if (book == null || book.getQuantity() == 0) {
            return "danger";
        } else if (book.getQuantity() < quantity) {
            return "danger";
        } else if (book.getQuantity() <= 5) {
            return "warning";
        } else {
            return "success";
        }
    }
    
    @Override
    public String toString() {
        return "BillingItemDTO{" +
                "id=" + id +
                ", billingId=" + billingId +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
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
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (bookId != null ? bookId.hashCode() : 0);
        result = 31 * result + (billingId != null ? billingId.hashCode() : 0);
        return result;
    }
}