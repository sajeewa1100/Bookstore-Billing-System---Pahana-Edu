package model;

import service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DeleteBookCommand implements BookCommand {

    private BookService bookService;

    public DeleteBookCommand(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get book ID for deletion
            String idStr = request.getParameter("id");
            
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Book ID is required for deletion");
            }
            
            int bookId;
            try {
                bookId = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid book ID format");
            }

            // Delete the book via the service
            boolean deleted = bookService.deleteBook(bookId);
            
            if (deleted) {
                // Set success message
                request.getSession().setAttribute("successMessage", "Book deleted successfully!");
            } else {
                // Book not found or couldn't be deleted
                request.getSession().setAttribute("errorMessage", "Book not found or could not be deleted");
            }
            
            // Redirect to the books page after deletion
            response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
            
        } catch (IllegalArgumentException e) {
            // Handle validation errors
            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
        } catch (Exception e) {
            // Handle other errors
            request.getSession().setAttribute("errorMessage", "Error deleting book: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
        }
    }
}