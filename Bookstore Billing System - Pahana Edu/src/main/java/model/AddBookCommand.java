package model;

import service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AddBookCommand implements BookCommand {

    private BookService bookService;

    public AddBookCommand(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Validate and fetch book details from the form
            String title = validateParameter(request.getParameter("title"), "Title");
            String author = validateParameter(request.getParameter("author"), "Author");
            String priceStr = validateParameter(request.getParameter("price"), "Price");
            String category = validateParameter(request.getParameter("category"), "Category");
            String isbn = validateParameter(request.getParameter("isbn"), "ISBN");
            String publisher = validateParameter(request.getParameter("publisher"), "Publisher");
            String quantityStr = validateParameter(request.getParameter("quantity"), "Quantity");

            // Parse numeric values with proper error handling
            double price;
            int quantity;
            
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) {
                    throw new IllegalArgumentException("Price cannot be negative");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format");
            }
            
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity < 0) {
                    throw new IllegalArgumentException("Quantity cannot be negative");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid quantity format");
            }

            // Create BookDTO object and add it
            BookDTO bookDTO = new BookDTO();
            bookService.addBook(bookDTO);

            // Set success message
            request.getSession().setAttribute("successMessage", "Book added successfully!");
            
            // Redirect to the books page after adding
            response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            request.setAttribute("errorMessage", e.getMessage());
            forwardToAddBookForm(request, response);
        } catch (Exception e) {
            // Handle other errors
            request.setAttribute("errorMessage", "Error adding book: " + e.getMessage());
            forwardToAddBookForm(request, response);
        }
    }
    
    /**
     * Validate that a parameter is not null or empty
     */
    private String validateParameter(String value, String fieldName) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required and cannot be empty");
        }
        return value.trim();
    }
    
    /**
     * Forward to add book form with error message
     */
    private void forwardToAddBookForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // You might want to forward to an add book form instead of books.jsp
        request.getRequestDispatcher("views/books.jsp").forward(request, response);
    }
}