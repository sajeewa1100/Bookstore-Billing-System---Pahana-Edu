// BookService.java - Fixed Implementation for Billing Integration
package service;

import dao.BookDAO;
import model.BookDTO;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookService {
    
    private final BookDAO bookDAO;
    
    public BookService() {
        this.bookDAO = new BookDAO();
        System.out.println("✅ BookService: Initialized with BookDAO");
    }
    
    /**
     * Get all books - Used by BillingServlet
     */
    public List<BookDTO> getAllBooks() {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT id, title, author, category, price, isbn, quantity, publisher FROM books ORDER BY title";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BookDTO book = new BookDTO(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBigDecimal("price"),
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getInt("quantity"),
                    rs.getString("publisher")
                );
                books.add(book);
            }

            // Check if books are retrieved
            if (books.isEmpty()) {
                System.out.println("⚠️ No books found in the database.");
            } else {
                System.out.println("📚 Retrieved " + books.size() + " books.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting all books: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting all books: " + e.getMessage(), e);
        }

        return books;
    }
    
    /**
     * Get book by ID - CRITICAL for BillingServlet
     */
    public BookDTO getBookById(int bookId) {
        try {
            System.out.println("🔍 BookService: Getting book by ID: " + bookId);
            BookDTO book = bookDAO.getBookById(bookId);
            if (book != null) {
                System.out.println("✅ BookService: Found book ID " + bookId + " - " + book.getTitle() + 
                                 " (Stock: " + book.getQuantity() + ", Price: Rs." + book.getPrice() + ")");
            } else {
                System.out.println("⚠️ BookService: No book found with ID " + bookId);
            }
            return book;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error getting book by ID " + bookId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get book by ID: " + e.getMessage(), e);
        }
    }
    
    /**
     * Search book by ISBN - FIXED to use DAO method
     */
    public BookDTO searchBookByISBN(String isbn) {
        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                System.out.println("⚠️ BookService: Empty ISBN provided");
                return null;
            }
            
            System.out.println("🔍 BookService: Searching for book by ISBN: " + isbn.trim());
            
            // Use the DAO method directly - THIS WAS THE MISSING PIECE!
            BookDTO book = bookDAO.searchBookByISBN(isbn.trim());
            
            if (book != null) {
                System.out.println("✅ BookService: Found book by ISBN " + isbn + ": " + book.getTitle() + 
                                 " (ID: " + book.getId() + ", Stock: " + book.getQuantity() + 
                                 ", Price: Rs." + book.getPrice() + ")");
            } else {
                System.out.println("⚠️ BookService: No book found with ISBN: " + isbn);
            }
            
            return book;
            
        } catch (Exception e) {
            System.err.println("❌ BookService: Error searching book by ISBN: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all categories
     */
    public List<String> getBookCategories() {
        try {
            List<String> categories = bookDAO.getAllCategories();
            System.out.println("📂 BookService: Retrieved " + categories.size() + " categories");
            return categories;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error getting categories: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get books by category
     */
    public List<BookDTO> getBooksByCategory(String category) {
        try {
            List<BookDTO> books = bookDAO.getBooksByCategory(category);
            System.out.println("📚 BookService: Retrieved " + books.size() + " books for category: " + category);
            return books;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error getting books by category: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get total books count
     */
    public int getTotalBooksCount() {
        try {
            int count = bookDAO.getTotalBooksCount();
            System.out.println("📊 BookService: Total books count: " + count);
            return count;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error getting total books count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Add a new book
     */
    public boolean addBook(BookDTO book) {
        try {
            boolean result = bookDAO.addBook(book);
            if (result) {
                System.out.println("✅ BookService: Added book: " + book.getTitle());
            } else {
                System.out.println("⚠️ BookService: Failed to add book: " + book.getTitle());
            }
            return result;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error adding book: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Update an existing book
     */
    public boolean updateBook(BookDTO book) {
        try {
            boolean result = bookDAO.updateBook(book);
            if (result) {
                System.out.println("✅ BookService: Updated book: " + book.getTitle());
            } else {
                System.out.println("⚠️ BookService: Failed to update book: " + book.getTitle());
            }
            return result;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error updating book: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Delete a book
     */
    public boolean deleteBook(int bookId) {
        try {
            // Get book info for logging before deletion
            BookDTO book = bookDAO.getBookById(bookId);
            String bookTitle = (book != null) ? book.getTitle() : "Unknown";
            
            boolean result = bookDAO.deleteBook(bookId);
            if (result) {
                System.out.println("✅ BookService: Deleted book: " + bookTitle + " (ID: " + bookId + ")");
            } else {
                System.out.println("⚠️ BookService: Failed to delete book ID: " + bookId);
            }
            return result;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error deleting book: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Check if book exists
     */
    public boolean bookExists(int bookId) {
        try {
            boolean exists = bookDAO.bookExists(bookId);
            System.out.println("🔍 BookService: Book ID " + bookId + " exists: " + exists);
            return exists;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error checking book existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Search books by title
     */
    public List<BookDTO> searchBooksByTitle(String title) {
        try {
            List<BookDTO> books = bookDAO.searchBooksByTitle(title);
            System.out.println("🔍 BookService: Found " + books.size() + " books matching title: " + title);
            return books;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error searching books by title: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Search books by author
     */
    public List<BookDTO> searchBooksByAuthor(String author) {
        try {
            List<BookDTO> books = bookDAO.searchBooksByAuthor(author);
            System.out.println("🔍 BookService: Found " + books.size() + " books by author: " + author);
            return books;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error searching books by author: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get low stock books
     */
    public List<BookDTO> getLowStockBooks(int threshold) {
        try {
            List<BookDTO> books = bookDAO.getLowStockBooks(threshold);
            System.out.println("📉 BookService: Found " + books.size() + " books with low stock (< " + threshold + ")");
            return books;
        } catch (Exception e) {
            System.err.println("❌ BookService: Error getting low stock books: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Update book quantity after sale - Used by BillingServlet
     */
    public boolean updateBookQuantity(int bookId, int newQuantity) {
        try {
            BookDTO book = getBookById(bookId);
            if (book == null) {
                System.err.println("⚠️ BookService: Cannot update quantity - book not found: " + bookId);
                return false;
            }
            
            book.setQuantity(newQuantity);
            boolean result = updateBook(book);
            
            if (result) {
                System.out.println("✅ BookService: Updated quantity for " + book.getTitle() + " to " + newQuantity);
            }
            
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ BookService: Error updating book quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Validate book availability for billing
     */
    public boolean isBookAvailable(int bookId, int requestedQuantity) {
        try {
            BookDTO book = getBookById(bookId);
            if (book == null) {
                System.out.println("⚠️ BookService: Book not found for availability check: " + bookId);
                return false;
            }
            
            boolean available = book.getQuantity() >= requestedQuantity;
            System.out.println("📦 BookService: Book " + book.getTitle() + 
                             " availability check - Requested: " + requestedQuantity + 
                             ", Available: " + book.getQuantity() + 
                             ", Result: " + available);
            
            return available;
            
        } catch (Exception e) {
            System.err.println("❌ BookService: Error checking book availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Debug method to test service connectivity
     */
    public void testServiceConnectivity() {
        System.out.println("🧪 BookService: Testing service connectivity...");
        try {
            // Test total count
            int totalCount = getTotalBooksCount();
            System.out.println("📊 Total books: " + totalCount);
            
            if (totalCount == 0) {
                System.out.println("⚠️ Database appears to be empty!");
                return;
            }
            
            // Test getAllBooks
            List<BookDTO> allBooks = getAllBooks();
            System.out.println("📚 getAllBooks() returned: " + allBooks.size() + " books");
            
            if (allBooks.size() > 0) {
                // Test getBookById on first book
                BookDTO firstBook = allBooks.get(0);
                BookDTO testBook = getBookById(firstBook.getId());
                System.out.println("🔍 getBookById(" + firstBook.getId() + ") test: " + 
                                 (testBook != null ? "SUCCESS" : "FAILED"));
                
                // Test searchBookByISBN if ISBN exists
                if (firstBook.getIsbn() != null && !firstBook.getIsbn().trim().isEmpty()) {
                    BookDTO isbnBook = searchBookByISBN(firstBook.getIsbn());
                    System.out.println("🔍 searchBookByISBN('" + firstBook.getIsbn() + "') test: " + 
                                     (isbnBook != null ? "SUCCESS" : "FAILED"));
                }
            }
            
            System.out.println("✅ BookService: All tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ BookService: Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}