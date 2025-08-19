package dao;

import model.BookDTO;
import util.ConnectionManager;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    /**
     * Get all categories from the categories table
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM categories ORDER BY name";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all categories: " + e.getMessage(), e);
        }
        return categories;
    }

    /**
     * Get all books from the database
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
                    rs.getBigDecimal("price"),  // Use getBigDecimal instead of getDouble
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getInt("quantity"),
                    rs.getString("publisher")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all books: " + e.getMessage(), e);
        }
        return books;
    }

    /**
     * Add a new book to the database
     */
    public boolean addBook(BookDTO book) {
        String sql = "INSERT INTO books (title, author, price, category, isbn, quantity, publisher) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setBigDecimal(3, book.getPrice());  // Use setBigDecimal instead of setDouble
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getIsbn());
            stmt.setInt(6, book.getQuantity());
            stmt.setString(7, book.getPublisher());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding book: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing book in the database
     */
    public boolean updateBook(BookDTO book) {
        String sql = "UPDATE books SET title=?, author=?, price=?, category=?, isbn=?, quantity=?, publisher=? WHERE id=?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setBigDecimal(3, book.getPrice());  // Use setBigDecimal
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getIsbn());
            stmt.setInt(6, book.getQuantity());
            stmt.setString(7, book.getPublisher());
            stmt.setInt(8, book.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error updating book: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a book by ID
     */
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting book: " + e.getMessage(), e);
        }
    }

    /**
     * Get a specific book by ID
     */
    public BookDTO getBookById(int bookId) {
        String sql = "SELECT id, title, author, category, price, isbn, quantity, publisher FROM books WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new BookDTO(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBigDecimal("price"),  // Use getBigDecimal
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getInt("quantity"),
                    rs.getString("publisher")
                );
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error getting book by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a book exists by ID
     */
    public boolean bookExists(int bookId) {
        String sql = "SELECT COUNT(*) FROM books WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking book existence: " + e.getMessage(), e);
        }
    }

    /**
     * Get books by category
     */
    public List<BookDTO> getBooksByCategory(String category) {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT id, title, author, category, price, isbn, quantity, publisher FROM books WHERE category = ? ORDER BY title";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BookDTO book = new BookDTO(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBigDecimal("price"),  // Use getBigDecimal
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getInt("quantity"),
                    rs.getString("publisher")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting books by category: " + e.getMessage(), e);
        }
        
        return books;
    }

    /**
     * Search books by title (partial match)
     */
    public List<BookDTO> searchBooksByTitle(String title) {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT id, title, author, category, price, isbn, quantity, publisher FROM books WHERE title LIKE ? ORDER BY title";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BookDTO book = new BookDTO(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBigDecimal("price"),  // Use getBigDecimal
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getInt("quantity"),
                    rs.getString("publisher")
                );
                books.add(book);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error searching books by title: " + e.getMessage(), e);
        }
        
        return books;
    }

    /**
     * Search books by author (partial match)
     */
    public List<BookDTO> searchBooksByAuthor(String author) {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT id, title, author, category, price, isbn, quantity, publisher FROM books WHERE author LIKE ? ORDER BY author";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + author + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BookDTO book = new BookDTO(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBigDecimal("price"),  // Use getBigDecimal
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getInt("quantity"),
                    rs.getString("publisher")
                );
                books.add(book);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error searching books by author: " + e.getMessage(), e);
        }
        
        return books;
    }

    /**
     * Get total count of books
     */
    public int getTotalBooksCount() {
        String sql = "SELECT COUNT(*) FROM books";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error getting total books count: " + e.getMessage(), e);
        }
    }

    /**
     * Get books with low stock (quantity less than specified threshold)
     */
    public List<BookDTO> getLowStockBooks(int threshold) {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT id, title, author, category, price, isbn, quantity, publisher FROM books WHERE quantity < ? ORDER BY quantity ASC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                BookDTO book = new BookDTO(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBigDecimal("price"),  // Use getBigDecimal
                    rs.getString("category"),
                    rs.getString("isbn"),
                    rs.getInt("quantity"),
                    rs.getString("publisher")
                );
                books.add(book);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error getting low stock books: " + e.getMessage(), e);
        }
        
        return books;
    }
}