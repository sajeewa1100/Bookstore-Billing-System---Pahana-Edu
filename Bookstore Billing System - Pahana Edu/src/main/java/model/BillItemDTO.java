package model;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Bill Items
 */
public class BillItemDTO {
    
    private Long id;
    private Long billId;
    private Integer bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal total;
    
    /**
     * Default constructor
     */
    public BillItemDTO() {
        this.quantity = 1;
        this.unitPrice = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }
    
    /**
     * Constructor with BookDTO and quantity
     */
    public BillItemDTO(BookDTO book, Integer quantity) {
        this.bookId = book.getId();
        this.bookTitle = book.getTitle();
        this.bookAuthor = book.getAuthor();
        this.bookIsbn = book.getIsbn();
        this.unitPrice = book.getPrice();
        this.quantity = quantity;
        calculateTotal();
    }
    
    /**
     * Constructor with basic details
     */
    public BillItemDTO(Integer bookId, String bookTitle, String bookAuthor, 
                      String bookIsbn, BigDecimal unitPrice, Integer quantity) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookIsbn = bookIsbn;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        calculateTotal();
    }
    
    /**
     * Calculate total amount for this item
     */
    public void calculateTotal() {
        if (unitPrice != null && quantity != null) {
            this.total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.total = BigDecimal.ZERO;
        }
    }
    
    /**
     * Get formatted unit price
     */
    public String getFormattedUnitPrice() {
        return "Rs. " + (unitPrice != null ? unitPrice.toString() : "0.00");
    }
    
    /**
     * Get formatted total
     */
    public String getFormattedTotal() {
        return "Rs. " + (total != null ? total.toString() : "0.00");
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getBillId() {
        return billId;
    }
    
    public void setBillId(Long billId) {
        this.billId = billId;
    }
    
    public Integer getBookId() {
        return bookId;
    }
    
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public String getBookAuthor() {
        return bookAuthor;
    }
    
    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    
    public String getBookIsbn() {
        return bookIsbn;
    }
    
    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotal();
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotal();
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    @Override
    public String toString() {
        return "BillItemDTO{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", total=" + total +
                '}';
    }
}