package model;

import java.math.BigDecimal;

public class BookDTO {
    private int id;
    private String title;
    private String author;
    private BigDecimal price;  // Keep as BigDecimal for consistency
    private String category;
    private String isbn;
    private int quantity;
    private String publisher;

    // Default constructor
    public BookDTO() {}

    // Constructor without ID (for new books) - Updated to use BigDecimal
    public BookDTO(String title, String author, BigDecimal price, String category,
                   String isbn, int quantity, String publisher) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.category = category;
        this.isbn = isbn;
        this.quantity = quantity;
        this.publisher = publisher;
    }
    
    // Convenience constructor with double (converts to BigDecimal)
    public BookDTO(String title, String author, double price, String category,
                   String isbn, int quantity, String publisher) {
        this(title, author, BigDecimal.valueOf(price), category, isbn, quantity, publisher);
    }

    // Constructor with ID (for existing books) - Updated to use BigDecimal
    public BookDTO(int id, String title, String author, BigDecimal price, String category,
                   String isbn, int quantity, String publisher) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.category = category;
        this.isbn = isbn;
        this.quantity = quantity;
        this.publisher = publisher;
    }
    
    // Convenience constructor with double (converts to BigDecimal)
    public BookDTO(int id, String title, String author, double price, String category,
                   String isbn, int quantity, String publisher) {
        this(id, title, author, BigDecimal.valueOf(price), category, isbn, quantity, publisher);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    // Updated setter to handle BigDecimal
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    // Convenience setter for double
    public void setPrice(double price) {
        this.price = BigDecimal.valueOf(price);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", isbn='" + isbn + '\'' +
                ", quantity=" + quantity +
                ", publisher='" + publisher + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookDTO bookDTO = (BookDTO) o;

        return id == bookDTO.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}