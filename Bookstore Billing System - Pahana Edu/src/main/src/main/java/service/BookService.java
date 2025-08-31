package service;

import dao.BookDAO;
import model.BookDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookService {
    private BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Create a new book with proper validation
     */
    public int createBook(BookDTO book) {
        System.out.println("BookService.createBook called for: " + book.getTitle());
        
        try {
            // Validate required fields
            if (!isValidBook(book)) {
                throw new IllegalArgumentException("Invalid book data");
            }
            
            // Check for duplicate ISBN
            if (isbnExists(book.getIsbn(), 0)) {
                throw new IllegalArgumentException("ISBN already exists");
            }
            
            // Set default stock quantity to 0 for DAO compatibility
            book.setStockQuantity(0);
            
            int bookId = bookDAO.createBook(book);
            
            if (bookId > 0) {
                System.out.println("Book created successfully with ID: " + bookId);
            }
            
            return bookId;
            
        } catch (Exception e) {
            System.out.println("Error creating book: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Update existing book with validation
     */
    public boolean updateBook(BookDTO book) {
        System.out.println("BookService.updateBook called for ID: " + book.getId());
        
        try {
            // Validate book exists
            BookDTO existingBook = bookDAO.findById(book.getId());
            if (existingBook == null) {
                throw new IllegalArgumentException("Book not found with ID: " + book.getId());
            }
            
            // Validate required fields
            if (!isValidBook(book)) {
                throw new IllegalArgumentException("Invalid book data");
            }
            
            // Check for duplicate ISBN (excluding current book)
            if (isbnExists(book.getIsbn(), book.getId())) {
                throw new IllegalArgumentException("ISBN already exists for another book");
            }
            
            // Keep existing stock quantity from database
            book.setStockQuantity(existingBook.getStockQuantity());
            
            boolean updated = bookDAO.updateBook(book);
            
            if (updated) {
                System.out.println("Book updated successfully");
            } else {
                System.out.println("Failed to update book");
            }
            
            return updated;
            
        } catch (Exception e) {
            System.out.println("Error updating book: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Delete book with proper validation
     */
    public boolean deleteBook(int bookId) {
        System.out.println("BookService.deleteBook called for ID: " + bookId);
        
        try {
            // Validate book exists
            BookDTO book = bookDAO.findById(bookId);
            if (book == null) {
                throw new IllegalArgumentException("Book not found with ID: " + bookId);
            }
            
            // Check if book has any related records (optional business rule)
            // You might want to prevent deletion if book is referenced in invoices
            
            boolean deleted = bookDAO.deleteBook(bookId);
            
            if (deleted) {
                System.out.println("Book deleted successfully: " + book.getTitle());
            } else {
                System.out.println("Failed to delete book");
            }
            
            return deleted;
            
        } catch (Exception e) {
            System.out.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get book by ID with error handling
     */
    public BookDTO getBookById(int bookId) {
        System.out.println("BookService.getBookById called for ID: " + bookId);
        
        try {
            if (bookId <= 0) {
                throw new IllegalArgumentException("Invalid book ID: " + bookId);
            }
            
            BookDTO book = bookDAO.findById(bookId);
            
            if (book != null) {
                System.out.println("Found book: " + book.getTitle());
            } else {
                System.out.println("No book found with ID: " + bookId);
            }
            
            return book;
            
        } catch (Exception e) {
            System.out.println("Error getting book by ID: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get book by ISBN with validation
     */
    public BookDTO getBookByISBN(String isbn) {
        System.out.println("BookService.getBookByISBN called for: " + isbn);
        
        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                throw new IllegalArgumentException("ISBN cannot be empty");
            }
            
            String cleanISBN = cleanISBN(isbn);
            BookDTO book = bookDAO.findByISBN(cleanISBN);
            
            if (book != null) {
                System.out.println("Found book by ISBN: " + book.getTitle());
            } else {
                System.out.println("No book found with ISBN: " + cleanISBN);
            }
            
            return book;
            
        } catch (Exception e) {
            System.out.println("Error getting book by ISBN: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get all books with error handling
     */
    public List<BookDTO> getAllBooks() {
        System.out.println("BookService.getAllBooks called");
        
        try {
            List<BookDTO> books = bookDAO.getAllBooks();
            System.out.println("Retrieved " + books.size() + " books");
            return books;
            
        } catch (Exception e) {
            System.out.println("Error getting all books: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Search books with specific search types: isbn (default), title, or author
     */
    public List<BookDTO> searchBooks(String searchTerm, String searchType) {
        System.out.println("BookService.searchBooks called with term: '" + searchTerm + "', type: '" + searchType + "'");
        
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getAllBooks(); // Return all books if no search term
            }
            
            String cleanSearchTerm = searchTerm.trim();
            List<BookDTO> results = new ArrayList<>();
            
            // Default to isbn search if no type specified
            if (searchType == null || searchType.trim().isEmpty()) {
                searchType = "isbn";
            }
            
            switch (searchType.toLowerCase()) {
                case "isbn":
                    // FIXED: Try multiple ISBN formats and use general search as fallback
                    String cleanISBN = cleanISBN(cleanSearchTerm);
                    
                    // First try: exact match with cleaned ISBN
                    BookDTO bookByCleanISBN = bookDAO.findByISBN(cleanISBN);
                    if (bookByCleanISBN != null) {
                        results.add(bookByCleanISBN);
                    }
                    
                    // Second try: exact match with original ISBN (in case DB stores with hyphens)
                    if (results.isEmpty()) {
                        BookDTO bookByOriginalISBN = bookDAO.findByISBN(cleanSearchTerm);
                        if (bookByOriginalISBN != null) {
                            results.add(bookByOriginalISBN);
                        }
                    }
                    
                    // Third try: use general search as fallback (like BillingService does)
                    if (results.isEmpty()) {
                        List<BookDTO> searchResults = bookDAO.searchBooks(cleanSearchTerm);
                        // Filter results to only include ISBN matches
                        for (BookDTO book : searchResults) {
                            String bookISBN = cleanISBN(book.getIsbn());
                            if (bookISBN.equals(cleanISBN) || 
                                book.getIsbn().equals(cleanSearchTerm)) {
                                results.add(book);
                            }
                        }
                    }
                    
                    System.out.println("ISBN search returned " + results.size() + " book(s)");
                    break;
                    
                case "title":
                    // Search by title using existing searchBooks method, then filter for title matches
                    List<BookDTO> allResults = bookDAO.searchBooks(cleanSearchTerm);
                    for (BookDTO book : allResults) {
                        String title = book.getTitle().toLowerCase();
                        String searchLower = cleanSearchTerm.toLowerCase();
                        if (title.contains(searchLower)) {
                            results.add(book);
                        }
                    }
                    System.out.println("Title search returned " + results.size() + " book(s)");
                    break;
                    
                case "author":
                    // Search by author using existing searchBooks method, then filter for author matches
                    List<BookDTO> authorResults = bookDAO.searchBooks(cleanSearchTerm);
                    for (BookDTO book : authorResults) {
                        String author = book.getAuthor().toLowerCase();
                        String searchLower = cleanSearchTerm.toLowerCase();
                        if (author.contains(searchLower)) {
                            results.add(book);
                        }
                    }
                    System.out.println("Author search returned " + results.size() + " book(s)");
                    break;
                    
                default:
                    // Fallback: use the general search method like BillingService does
                    results = bookDAO.searchBooks(cleanSearchTerm);
                    System.out.println("General search returned " + results.size() + " book(s)");
                    break;
            }
            
            return results;
            
        } catch (Exception e) {
            System.out.println("Error searching books: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

   

    // OPTIONAL: Add debug method to check ISBN storage format
    public void debugISBNSearch(String searchTerm) {
        System.out.println("=== ISBN SEARCH DEBUG ===");
        System.out.println("Original search term: '" + searchTerm + "'");
        System.out.println("Cleaned search term: '" + cleanISBN(searchTerm) + "'");
        
        try {
            // Check what's actually in the database
            List<BookDTO> allBooks = getAllBooks();
            System.out.println("All ISBNs in database:");
            for (BookDTO book : allBooks) {
                System.out.println("  - Stored: '" + book.getIsbn() + "' | Cleaned: '" + cleanISBN(book.getIsbn()) + "'");
            }
            
            // Test different search approaches
            System.out.println("\nTesting findByISBN with cleaned term:");
            BookDTO result1 = bookDAO.findByISBN(cleanISBN(searchTerm));
            System.out.println("Result: " + (result1 != null ? result1.getTitle() : "null"));
            
            System.out.println("\nTesting findByISBN with original term:");
            BookDTO result2 = bookDAO.findByISBN(searchTerm);
            System.out.println("Result: " + (result2 != null ? result2.getTitle() : "null"));
            
            System.out.println("\nTesting general searchBooks:");
            List<BookDTO> result3 = bookDAO.searchBooks(searchTerm);
            System.out.println("Results: " + result3.size());
            
        } catch (Exception e) {
            System.out.println("Debug error: " + e.getMessage());
        }
        
        System.out.println("=== END DEBUG ===");
    }

    /**
     * Overloaded method for backward compatibility (defaults to isbn search)
     */
    public List<BookDTO> searchBooks(String searchTerm) {
        return searchBooks(searchTerm, "isbn");
    }

    /**
     * Check if ISBN exists (excluding specific book)
     */
    public boolean isbnExists(String isbn, int excludeBookId) {
        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                return false;
            }
            
            String cleanISBN = cleanISBN(isbn);
            return bookDAO.isbnExists(cleanISBN, excludeBookId);
            
        } catch (Exception e) {
            System.out.println("Error checking ISBN existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get books by category (if you add this feature later)
     */
    public List<BookDTO> getBooksByCategory(String category) {
        System.out.println("BookService.getBooksByCategory called for: " + category);
        
        try {
            // This would require a new DAO method if implemented
            // For now, return empty list
            return new ArrayList<>();
            
        } catch (Exception e) {
            System.out.println("Error getting books by category: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get books by price range
     */
    public List<BookDTO> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        System.out.println("BookService.getBooksByPriceRange called: " + minPrice + " - " + maxPrice);
        
        try {
            List<BookDTO> allBooks = getAllBooks();
            List<BookDTO> filteredBooks = new ArrayList<>();
            
            for (BookDTO book : allBooks) {
                BigDecimal price = book.getPrice();
                if (price != null && 
                    (minPrice == null || price.compareTo(minPrice) >= 0) &&
                    (maxPrice == null || price.compareTo(maxPrice) <= 0)) {
                    filteredBooks.add(book);
                }
            }
            
            System.out.println("Price range search returned " + filteredBooks.size() + " book(s)");
            return filteredBooks;
            
        } catch (Exception e) {
            System.out.println("Error getting books by price range: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Calculate profit margin for a book
     */
    public BigDecimal calculateProfitMargin(BookDTO book) {
        try {
            if (book == null || book.getPrice() == null || book.getCostPrice() == null) {
                return BigDecimal.ZERO;
            }
            
            if (book.getCostPrice().compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO; // Avoid division by zero
            }
            
            BigDecimal profit = book.getPrice().subtract(book.getCostPrice());
            BigDecimal margin = profit.divide(book.getCostPrice(), 4, BigDecimal.ROUND_HALF_UP)
                                   .multiply(new BigDecimal("100"));
            
            return margin;
            
        } catch (Exception e) {
            System.out.println("Error calculating profit margin: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Get books with low profit margin (less than specified percentage)
     */
    public List<BookDTO> getBooksWithLowMargin(BigDecimal thresholdPercentage) {
        System.out.println("BookService.getBooksWithLowMargin called with threshold: " + thresholdPercentage + "%");
        
        try {
            List<BookDTO> allBooks = getAllBooks();
            List<BookDTO> lowMarginBooks = new ArrayList<>();
            
            for (BookDTO book : allBooks) {
                BigDecimal margin = calculateProfitMargin(book);
                if (margin.compareTo(thresholdPercentage) < 0) {
                    lowMarginBooks.add(book);
                }
            }
            
            System.out.println("Found " + lowMarginBooks.size() + " books with low margin");
            return lowMarginBooks;
            
        } catch (Exception e) {
            System.out.println("Error getting low margin books: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
    /**
     * Validate book data
     */
    private boolean isValidBook(BookDTO book) {
        if (book == null) {
            System.out.println("Validation failed: Book is null");
            return false;
        }
        
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            System.out.println("Validation failed: Title is required");
            return false;
        }
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            System.out.println("Validation failed: Author is required");
            return false;
        }
        
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            System.out.println("Validation failed: ISBN is required");
            return false;
        }
        
        // Validate ISBN format
        String cleanISBN = cleanISBN(book.getIsbn());
        if (!isValidISBN(cleanISBN)) {
            System.out.println("Validation failed: Invalid ISBN format");
            return false;
        }
        
        // Update the book object with cleaned ISBN
        book.setIsbn(cleanISBN);
        
        // Validate price
        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Validation failed: Price must be non-negative");
            return false;
        }
        
        // Validate cost price
        if (book.getCostPrice() == null || book.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Validation failed: Cost price must be non-negative");
            return false;
        }
        
        return true;
    }
    
    /**
     * Clean ISBN (remove spaces, dashes, etc.)
     */
    private String cleanISBN(String isbn) {
        if (isbn == null) return "";
        return isbn.replaceAll("[^0-9X]", "").toUpperCase();
    }
    
    /**
     * Validate ISBN format (ISBN-10 or ISBN-13)
     */
    private boolean isValidISBN(String isbn) {
        if (isbn == null) return false;
        
        // Remove any remaining non-alphanumeric characters
        isbn = isbn.replaceAll("[^0-9X]", "").toUpperCase();
        
        // Check length - ISBN-10 or ISBN-13
        if (isbn.length() != 10 && isbn.length() != 13) {
            return false;
        }
        
        // Basic format validation - you could add checksum validation later
        return isbn.matches("^[0-9]{9}[0-9X]$") || isbn.matches("^[0-9]{13}$");
    }
    public List<BookDTO> getAvailableBooks() {
        return getAllBooks(); // Simple implementation
    }
}