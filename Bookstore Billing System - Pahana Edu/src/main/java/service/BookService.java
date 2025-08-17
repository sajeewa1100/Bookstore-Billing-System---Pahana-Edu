package service;

import dao.BookDAO;
import model.BookDTO;

import java.util.List;

public class BookService {

    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO(); // Initialize DAO
    }

    /**
     * Add a new book to the database
     */
    public void addBook(BookDTO book) {
        if (!bookDAO.addBook(book)) {
            throw new RuntimeException("Failed to add book to database");
        }
    }

    /**
     * Get all books from the database
     */
    public List<BookDTO> getAllBooks() {
        return bookDAO.getAllBooks(); // Fetch all books
    }

    /**
     * Get all unique book categories
     */
    public List<String> getBookCategories() {
        return bookDAO.getAllCategories(); // Fetch all book categories
    }

    /**
     * Get books filtered by category
     */
    public List<BookDTO> getBooksByCategory(String category) {
        // Logic to filter books by category
        return bookDAO.getBooksByCategory(category);
    }

    /**
     * Update an existing book
     */
    public void updateBook(BookDTO book) {
        if (book.getId() <= 0) {
            throw new IllegalArgumentException("Invalid book ID for update");
        }
        if (!bookDAO.updateBook(book)) {
            throw new RuntimeException("Failed to update book - book may not exist");
        }
    }

    /**
     * Delete a book by ID
     */
    public boolean deleteBook(int bookId) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Invalid book ID for deletion");
        }
        return bookDAO.deleteBook(bookId);
    }

    /**
     * Get a specific book by ID
     */
    public BookDTO getBookById(int bookId) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Invalid book ID");
        }
        return bookDAO.getBookById(bookId);
    }

    /**
     * Check if a book exists by ID
     */
    public boolean bookExists(int bookId) {
        return bookDAO.bookExists(bookId);
    }

    /**
     * Search books by title (partial match)
     */
    public List<BookDTO> searchBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.searchBooksByTitle(title.trim());
    }

    /**
     * Search books by author (partial match)
     */
    public List<BookDTO> searchBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.searchBooksByAuthor(author.trim());
    }

    /**
     * Get total count of books
     */
    public int getTotalBooksCount() {
        return bookDAO.getTotalBooksCount();
    }

    /**
     * Get books with low stock
     */
    public List<BookDTO> getLowStockBooks(int threshold) {
        return bookDAO.getLowStockBooks(threshold);
    }
}