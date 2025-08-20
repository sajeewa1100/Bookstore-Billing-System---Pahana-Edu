package service;

import dao.BookDAO;
import model.BookDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookService {
    
    private static final Logger LOGGER = Logger.getLogger(BookService.class.getName());
    private final BookDAO bookDAO;
    
    public BookService() {
        this.bookDAO = new BookDAO();
        System.out.println("✅ BookService: Initialized with BookDAO");
    }
    
    /**
     * Get all books - Used by BillingServlet
     */
    public List<BookDTO> getAllBooks() {
        try {
            List<BookDTO> books = bookDAO.getAllBooks();
            System.out.println("📚 BookService: Retrieved " + books.size() + " books");
            return books;
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error getting all books: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
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
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error getting book by ID " + bookId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Find book by ISBN - Used by BillingService
     */
    public BookDTO findBookByISBN(String isbn) {
        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                System.out.println("⚠️ BookService: Empty ISBN provided");
                return null;
            }
            
            System.out.println("🔍 BookService: Searching for book by ISBN: " + isbn.trim());
            BookDTO book = bookDAO.getBookByISBN(isbn.trim());
            
            if (book != null) {
                System.out.println("✅ BookService: Found book by ISBN " + isbn + ": " + book.getTitle() + 
                                 " (ID: " + book.getId() + ", Stock: " + book.getQuantity() + 
                                 ", Price: Rs." + book.getPrice() + ")");
            } else {
                System.out.println("⚠️ BookService: No book found with ISBN: " + isbn);
            }
            
            return book;
            
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error searching book by ISBN: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get all categories - FIXED with error handling
     */
    public List<String> getBookCategories() {
        try {
            List<String> categories = bookDAO.getAllCategories();
            System.out.println("📂 BookService: Retrieved " + categories.size() + " categories");
            return categories;
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error getting categories: " + e.getMessage());
            e.printStackTrace();
            // Return empty list instead of throwing exception
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ BookService: Unexpected error getting categories: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get books by category - FIXED with error handling
     */
    public List<BookDTO> getBooksByCategory(String category) {
        try {
            List<BookDTO> books = bookDAO.getBooksByCategory(category);
            System.out.println("📚 BookService: Retrieved " + books.size() + " books for category: " + category);
            return books;
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error getting books by category: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ BookService: Unexpected error getting books by category: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
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
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error adding book: " + e.getMessage());
            e.printStackTrace();
            return false;
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
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error updating book: " + e.getMessage());
            e.printStackTrace();
            return false;
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
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
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
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error searching books by title: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
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
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error searching books by author: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
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
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error getting low stock books: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
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
     * Search books with general search (used by existing servlet)
     */
    public List<BookDTO> searchBooks(String searchType, String searchQuery) {
        try {
            List<BookDTO> books = bookDAO.searchBooks(searchType, searchQuery);
            System.out.println("🔍 BookService: Found " + books.size() + " books for search: " + searchQuery);
            return books;
        } catch (SQLException e) {
            System.err.println("❌ BookService: Error searching books: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
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
            
            // Test categories
            List<String> categories = getBookCategories();
            System.out.println("📂 getBookCategories() returned: " + categories.size() + " categories");
            
            if (allBooks.size() > 0) {
                // Test getBookById on first book
                BookDTO firstBook = allBooks.get(0);
                BookDTO testBook = getBookById(firstBook.getId());
                System.out.println("🔍 getBookById(" + firstBook.getId() + ") test: " + 
                                 (testBook != null ? "SUCCESS" : "FAILED"));
                
                // Test findBookByISBN if ISBN exists
                if (firstBook.getIsbn() != null && !firstBook.getIsbn().trim().isEmpty()) {
                    BookDTO isbnBook = findBookByISBN(firstBook.getIsbn());
                    System.out.println("🔍 findBookByISBN('" + firstBook.getIsbn() + "') test: " + 
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