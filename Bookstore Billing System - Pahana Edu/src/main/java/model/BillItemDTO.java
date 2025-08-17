package model;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Bill Items
 * Represents individual items in a billing transaction
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
        this.quantity = 0;
        this.unitPrice = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }
    
    /**
     * Constructor with book information
     */
    public BillItemDTO(Integer bookId, String bookTitle, String bookAuthor, 
                      String bookIsbn, BigDecimal unitPrice, Integer quantity) {
        this();
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookIsbn = bookIsbn;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        calculateTotal();
    }
    
    /**
     * Constructor from BookDTO
     */
    public BillItemDTO(BookDTO book, Integer quantity) {
        this(book.getId(), book.getTitle(), book.getAuthor(), 
             book.getIsbn(), book.getPrice(), quantity);
    }
    
    /**
     * Calculate total amount for this item
     */
    public void calculateTotal() {
        if (unitPrice != null && quantity != null) {
            this.total = unitPrice.multiply(new BigDecimal(quantity));
        } else {
            this.total = BigDecimal.ZERO;
        }
    }
    
    /**
     * Set quantity and recalculate total
     */
    public void setQuantityAndCalculate(Integer quantity) {
        this.quantity = quantity;
        calculateTotal();
    }
    
    /**
     * Set unit price and recalculate total
     */
    public void setUnitPriceAndCalculate(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotal();
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
                ", bookTitle='" + bookTitle + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", total=" + total +
                '}';
    }
}