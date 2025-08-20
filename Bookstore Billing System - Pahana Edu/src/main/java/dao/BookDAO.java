package dao;

import model.BookDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDAO {

    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());

    /**
     * Get all books
     */
    public List<BookDTO> getAllBooks() throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BookDTO book = mapResultSetToBook(rs);
                books.add(book);
            }

            LOGGER.info("BookDAO: Retrieved " + books.size() + " books from database");
            return books;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error retrieving all books", e);
            throw e;
        }
    }

    /**
     * Get book by ID
     */
    public BookDTO getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BookDTO book = mapResultSetToBook(rs);
                    LOGGER.info("BookDAO: Retrieved book by ID: " + bookId);
                    return book;
                }
            }

            LOGGER.warning("BookDAO: Book not found with ID: " + bookId);
            return null;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error retrieving book by ID: " + bookId, e);
            throw e;
        }
    }

    /**
     * Get book by ISBN - Required for BillingService
     */
    public BookDTO getBookByISBN(String isbn) throws SQLException {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, isbn.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BookDTO book = mapResultSetToBook(rs);
                    LOGGER.info("BookDAO: Retrieved book by ISBN: " + isbn);
                    return book;
                }
            }
            
            LOGGER.warning("BookDAO: Book not found with ISBN: " + isbn);
            return null;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error retrieving book by ISBN: " + isbn, e);
            throw e;
        }
    }

    /**
     * Add a new book
     */
    public boolean addBook(BookDTO book) throws SQLException {
        String sql = "INSERT INTO books (title, author, price, category, isbn, quantity, publisher) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBigDecimal(3, book.getPrice());
            pstmt.setString(4, book.getCategory());
            pstmt.setString(5, book.getIsbn());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setString(7, book.getPublisher());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
                LOGGER.info("BookDAO: Book added successfully - " + book.getTitle());
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error adding book", e);
            throw e;
        }
    }

    /**
     * Update an existing book
     */
    public boolean updateBook(BookDTO book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, price = ?, category = ?, isbn = ?, quantity = ?, publisher = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setBigDecimal(3, book.getPrice());
            pstmt.setString(4, book.getCategory());
            pstmt.setString(5, book.getIsbn());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setString(7, book.getPublisher());
            pstmt.setInt(8, book.getId());

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("BookDAO: Book updated successfully - " + book.getTitle());
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error updating book", e);
            throw e;
        }
    }

    /**
     * Delete a book
     */
    public boolean deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("BookDAO: Book deleted successfully - ID: " + bookId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error deleting book", e);
            throw e;
        }
    }

    /**
     * Search books by title, author, or ISBN
     */
    public List<BookDTO> searchBooks(String searchType, String searchQuery) throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql;
        
        switch (searchType) {
            case "title":
                sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title ASC";
                break;
            case "author":
                sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY author ASC";
                break;
            case "isbn":
                sql = "SELECT * FROM books WHERE isbn LIKE ? ORDER BY isbn ASC";
                break;
            case "category":
                sql = "SELECT * FROM books WHERE category LIKE ? ORDER BY category ASC";
                break;
            default:
                sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? ORDER BY title ASC";
                break;
        }

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchQuery + "%";
            
            if ("title".equals(searchType) || "author".equals(searchType) || 
                "isbn".equals(searchType) || "category".equals(searchType)) {
                pstmt.setString(1, searchPattern);
            } else {
                // Search in multiple fields
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookDTO book = mapResultSetToBook(rs);
                    books.add(book);
                }
            }

            LOGGER.info("BookDAO: Found " + books.size() + " books matching search criteria");
            return books;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error searching books", e);
            throw e;
        }
    }

    /**
     * Update book quantity (used after billing)
     */
    public boolean updateBookQuantity(int bookId, int newQuantity) throws SQLException {
        String sql = "UPDATE books SET quantity = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, bookId);

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("BookDAO: Book quantity updated - ID: " + bookId + ", New quantity: " + newQuantity);
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error updating book quantity", e);
            throw e;
        }
    }

    /**
     * Get books with low stock (quantity <= threshold)
     */
    public List<BookDTO> getLowStockBooks(int threshold) throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE quantity <= ? ORDER BY quantity ASC, title ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookDTO book = mapResultSetToBook(rs);
                    books.add(book);
                }
            }

            LOGGER.info("BookDAO: Found " + books.size() + " books with low stock (threshold: " + threshold + ")");
            return books;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error retrieving low stock books", e);
            throw e;
        }
    }

    /**
     * Get total books count
     */
    public int getTotalBooksCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM books";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error getting total books count", e);
            throw e;
        }
    }

    /**
     * Map ResultSet to BookDTO - Works with your existing BookDTO class
     */
    private BookDTO mapResultSetToBook(ResultSet rs) throws SQLException {
        BookDTO book = new BookDTO();
        
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setQuantity(rs.getInt("quantity"));
        book.setCategory(rs.getString("category"));
        book.setPublisher(rs.getString("publisher"));
        
        return book;
    }
    
    /**
     * Get all unique categories from books
     */
    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM books WHERE category IS NOT NULL AND category != '' ORDER BY category ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                if (category != null && !category.trim().isEmpty()) {
                    categories.add(category.trim());
                }
            }

            LOGGER.info("BookDAO: Retrieved " + categories.size() + " categories from database");
            return categories;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error retrieving categories", e);
            throw e;
        }
    }

    /**
     * Get books by category
     */
    public List<BookDTO> getBooksByCategory(String category) throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE category = ? ORDER BY title ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookDTO book = mapResultSetToBook(rs);
                    books.add(book);
                }
            }

            LOGGER.info("BookDAO: Retrieved " + books.size() + " books for category: " + category);
            return books;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error retrieving books by category: " + category, e);
            throw e;
        }
    }

    /**
     * Check if book exists
     */
    public boolean bookExists(int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error checking if book exists: " + bookId, e);
            throw e;
        }
    }

    /**
     * Search books by title
     */
    public List<BookDTO> searchBooksByTitle(String title) throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + title + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookDTO book = mapResultSetToBook(rs);
                    books.add(book);
                }
            }

            LOGGER.info("BookDAO: Found " + books.size() + " books matching title: " + title);
            return books;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error searching books by title", e);
            throw e;
        }
    }

    /**
     * Search books by author
     */
    public List<BookDTO> searchBooksByAuthor(String author) throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY author ASC, title ASC";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + author + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookDTO book = mapResultSetToBook(rs);
                    books.add(book);
                }
            }

            LOGGER.info("BookDAO: Found " + books.size() + " books by author: " + author);
            return books;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BookDAO: Error searching books by author", e);
            throw e;
        }
    }

    /**
     * Search book by ISBN - Alternative method name that BookService might be using
     */
    public BookDTO searchBookByISBN(String isbn) throws SQLException {
        // This just calls the existing getBookByISBN method
        return getBookByISBN(isbn);
    }
}