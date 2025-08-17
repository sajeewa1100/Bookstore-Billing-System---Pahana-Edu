package model;

import service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class EditBookCommand implements BookCommand {

    private BookService bookService;

    public EditBookCommand(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get book ID for update
            String idStr = validateParameter(request.getParameter("id"), "Book ID");
            int bookId;
            
            try {
                bookId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid book ID format");
            }

            // Validate and fetch book details from the form
            String title = validateParameter(request.getParameter("title"), "Title");
            String author = validateParameter(request.getParameter("author"), "Author");
            String priceStr = validateParameter(request.getParameter("price"), "Price");
            String category = validateParameter(request.getParameter("category"), "Category");
            String isbn = request.getParameter("isbn"); // ISBN can be optional
            String publisher = request.getParameter("publisher"); // Publisher can be optional
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

            // Create BookDTO object with ID for update
            BookDTO bookDTO = new BookDTO();
            bookDTO.setId(bookId); // Set the ID for update
            
            // Update the book via the service
            bookService.updateBook(bookDTO);

            // Set success message
            request.getSession().setAttribute("successMessage", "Book updated successfully!");
            
            // Redirect to the books page after updating
            response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            request.setAttribute("errorMessage", e.getMessage());
            forwardToBooksPage(request, response);
        } catch (Exception e) {
            // Handle other errors
            request.setAttribute("errorMessage", "Error updating book: " + e.getMessage());
            forwardToBooksPage(request, response);
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
     * Forward to books page with error message
     */
    private void forwardToBooksPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Load books and categories for the page
        try {
            request.setAttribute("books", bookService.getAllBooks());
            request.setAttribute("categories", bookService.getBookCategories());
        } catch (Exception e) {
            // If we can't load books, just set an additional error
            request.setAttribute("errorMessage", "Error loading books: " + e.getMessage());
        }
        request.getRequestDispatcher("/views/books.jsp").forward(request, response);
    }
}