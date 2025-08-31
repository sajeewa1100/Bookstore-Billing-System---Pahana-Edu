package controller;

import service.BookService;
import model.BookDTO;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/books")
public class BookServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BookService bookService;

    @Override
    public void init() throws ServletException {
        super.init();
        bookService = new BookService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "list") {
                case "new":
                    showNewBookForm(request, response);
                    break;
                case "edit":
                    showEditBookForm(request, response);
                    break;
                case "view":
                    showBookDetails(request, response);
                    break;
                case "list":
                default:
                    listBooks(request, response);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error in BookServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listBooks(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    createBook(request, response);
                    break;
                case "update":
                    updateBook(request, response);
                    break;
                case "delete":
                    deleteBook(request, response);
                    break;
                default:
                    response.sendRedirect("books");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error in BookServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listBooks(request, response);
        }
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String searchTerm = request.getParameter("search");
        String searchType = request.getParameter("searchType");
        
        List<BookDTO> books;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Perform search with specified type (defaults to isbn if not specified)
            books = bookService.searchBooks(searchTerm, searchType);
            
            // Set search parameters back to request for form persistence
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("searchType", searchType != null ? searchType : "isbn");
        } else {
            // No search term, get all books
            books = bookService.getAllBooks();
        }
        
        // Calculate statistics - compatible with DAO that includes stock_quantity
        int totalBooks = books.size();
        BigDecimal totalInventoryValue = BigDecimal.ZERO;
        
        for (BookDTO book : books) {
            // Calculate inventory value using cost_price only (ignore stock since we don't use it)
            if (book.getCostPrice() != null) {
                totalInventoryValue = totalInventoryValue.add(book.getCostPrice());
            }
        }
        
        // Set attributes for JSP
        request.setAttribute("books", books);
        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalInventoryValue", totalInventoryValue);
        
        request.getRequestDispatcher("views/books.jsp").forward(request, response);
    }

    private void showNewBookForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("views/books.jsp").forward(request, response);
    }

    private void showEditBookForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int bookId = Integer.parseInt(request.getParameter("id"));
            BookDTO book = bookService.getBookById(bookId);
            
            if (book != null) {
                request.setAttribute("book", book);
                request.getRequestDispatcher("views/books.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Book not found with ID: " + bookId);
                listBooks(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid book ID");
            listBooks(request, response);
        }
    }

    private void showBookDetails(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int bookId = Integer.parseInt(request.getParameter("id"));
            BookDTO book = bookService.getBookById(bookId);
            
            if (book != null) {
                request.setAttribute("book", book);
                request.getRequestDispatcher("views/books.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Book not found with ID: " + bookId);
                listBooks(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid book ID");
            listBooks(request, response);
        }
    }

    private void createBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            BookDTO book = extractBookFromRequest(request);
            
            int bookId = bookService.createBook(book);
            
            if (bookId > 0) {
                request.getSession().setAttribute("successMessage", "Book '" + book.getTitle() + "' created successfully!");
                response.sendRedirect("books");
            } else {
                request.setAttribute("errorMessage", "Failed to create book. Please check your input.");
                request.setAttribute("book", book); // Keep form data
                showNewBookForm(request, response);
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("book", extractBookFromRequest(request)); // Keep form data
            showNewBookForm(request, response);
        }
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            BookDTO book = extractBookFromRequest(request);
            book.setId(Integer.parseInt(request.getParameter("id")));
            
            boolean updated = bookService.updateBook(book);
            
            if (updated) {
                request.getSession().setAttribute("successMessage", "Book '" + book.getTitle() + "' updated successfully!");
                response.sendRedirect("books");
            } else {
                request.setAttribute("errorMessage", "Failed to update book. Please check your input.");
                request.setAttribute("book", book); // Keep form data
                showEditBookForm(request, response);
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("book", extractBookFromRequest(request)); // Keep form data
            showEditBookForm(request, response);
        }
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int bookId = Integer.parseInt(request.getParameter("id"));
            
            // Get book details for success message
            BookDTO book = bookService.getBookById(bookId);
            String bookTitle = (book != null) ? book.getTitle() : "Unknown Book";
            
            boolean deleted = bookService.deleteBook(bookId);
            
            if (deleted) {
                request.getSession().setAttribute("successMessage", "Book '" + bookTitle + "' deleted successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete book. Please try again.");
            }
            
            response.sendRedirect("books");
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid book ID");
            listBooks(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            listBooks(request, response);
        }
    }

    
     // Extract book data from request parameters - compatible with DAO that expects stock_quantity
     
    private BookDTO extractBookFromRequest(HttpServletRequest request) {
        BookDTO book = new BookDTO();
        
        try {
            book.setIsbn(getParameterValue(request, "isbn"));
            book.setTitle(getParameterValue(request, "title"));
            book.setAuthor(getParameterValue(request, "author"));
            
            // Parse price
            String priceStr = getParameterValue(request, "price");
            if (priceStr != null && !priceStr.trim().isEmpty()) {
                book.setPrice(new BigDecimal(priceStr));
            } else {
                book.setPrice(BigDecimal.ZERO);
            }
            
            // Parse cost price
            String costPriceStr = getParameterValue(request, "costPrice");
            if (costPriceStr != null && !costPriceStr.trim().isEmpty()) {
                book.setCostPrice(new BigDecimal(costPriceStr));
            } else {
                book.setCostPrice(BigDecimal.ZERO);
            }
            
            // Set default stock quantity to 0 for DAO compatibility
            book.setStockQuantity(0);
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in form data");
        }
        
        return book;
    }

    /**
     * Get parameter value, handling null and empty strings
     */
    private String getParameterValue(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }
}