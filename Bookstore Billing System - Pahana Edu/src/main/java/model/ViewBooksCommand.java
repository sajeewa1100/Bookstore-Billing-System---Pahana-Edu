package model;

import service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ViewBooksCommand implements BookCommand {

    private BookService bookService;

    public ViewBooksCommand(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get category filter if provided
            String categoryFilter = request.getParameter("category");
            
            List<BookDTO> books;
            if (categoryFilter != null && !categoryFilter.trim().isEmpty() && !"all".equalsIgnoreCase(categoryFilter)) {
                books = bookService.getBooksByCategory(categoryFilter);
                request.setAttribute("selectedCategory", categoryFilter);
            } else {
                books = bookService.getAllBooks();
            }
            
            // Get all categories for the dropdown
            List<String> categories = bookService.getBookCategories();
            
            // Set attributes for the JSP
            request.setAttribute("books", books);
            request.setAttribute("categories", categories);
            
            // Forward to the books view
            request.getRequestDispatcher("/views/books.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Handle any errors
            request.setAttribute("errorMessage", "Error retrieving books: " + e.getMessage());
            request.getRequestDispatcher("/views/books.jsp").forward(request, response);
        }
    }
}