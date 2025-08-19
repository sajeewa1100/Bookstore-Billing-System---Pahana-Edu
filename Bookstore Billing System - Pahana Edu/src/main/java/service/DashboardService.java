package service;


import dao.BookDAO;
import model.BookDTO;

import java.util.List;

/**
 * Service Layer for Dashboard operations.
 * Handles all business logic for fetching and processing data for the dashboard.
 */
public class DashboardService {
    private BookDAO bookDAO;

    public DashboardService() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Get the total count of books in the system.
     */
    public int getTotalBooksCount() {
        return bookDAO.getTotalBooksCount();
    }

    /**
     * Get a list of book categories.
     */
    public List<String> getBookCategories() {
        return bookDAO.getAllCategories();
    }

    /**
     * Get a list of books that are low on stock (<= 5).
     */
    public List<BookDTO> getLowStockBooks(int limit) {
        return bookDAO.getLowStockBooks(limit);
    }

    /**
     * Get all books (can be limited).
     */
    public List<BookDTO> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    // You can add other methods related to the dashboard here, such as fetching clients, sales data, etc.
}

