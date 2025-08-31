package model;

import java.math.BigDecimal;


public class BookDTO {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private BigDecimal price;
    private BigDecimal costPrice;
    private int stockQuantity;
    
    // Constructors
    public BookDTO() {}
    
    public BookDTO(int id, String isbn, String title, String author, 
                   BigDecimal price, BigDecimal costPrice, int stockQuantity) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.costPrice = costPrice;
        this.stockQuantity = stockQuantity;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
}

