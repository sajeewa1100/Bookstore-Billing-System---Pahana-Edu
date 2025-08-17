package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import service.BookService;
import model.BookCommand;
import model.CommandFactory;
import java.io.IOException;

@WebServlet("/BookServlet")
public class BookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BookService bookService;

    @Override
    public void init() {
        System.out.println("BookServlet: Initializing servlet...");
        bookService = new BookService();
        System.out.println("BookServlet: BookService initialized successfully");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("BookServlet POST: Received action = " + action);
        
        // Print all parameters for debugging
        System.out.println("BookServlet POST: All parameters:");
        request.getParameterMap().forEach((key, values) -> {
            System.out.println("  " + key + " = " + String.join(", ", values));
        });
        
        // Validate action parameter
        if (action == null || action.trim().isEmpty()) {
            System.out.println("BookServlet POST: No action specified, redirecting to books");
            response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
            return;
        }

        try {
            // Check if action is valid before creating command
            if (!CommandFactory.isValidAction(action)) {
                System.out.println("BookServlet POST: Invalid action: " + action);
                handleError(request, response, "Invalid action: " + action);
                return;
            }

            System.out.println("BookServlet POST: Creating command for action: " + action);
            BookCommand command = CommandFactory.createCommand(action, bookService);

            if (command != null) {
                System.out.println("BookServlet POST: Executing command: " + command.getClass().getSimpleName());
                command.execute(request, response);
                System.out.println("BookServlet POST: Command executed successfully");
            } else {
                System.out.println("BookServlet POST: Command factory returned null for action: " + action);
                response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
            }
        } catch (Exception e) {
            System.out.println("BookServlet POST: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error executing command: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("BookServlet GET: Received action = " + action);
        
        // Default action if none specified
        if (action == null || action.trim().isEmpty()) {
            action = "books";
            System.out.println("BookServlet GET: Using default action = " + action);
        }

        try {
            // Check if action is valid before creating command
            if (!CommandFactory.isValidAction(action)) {
                System.out.println("BookServlet GET: Invalid action: " + action + ", redirecting to books");
                response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
                return;
            }

            System.out.println("BookServlet GET: Creating command for action: " + action);
            BookCommand command = CommandFactory.createCommand(action, bookService);

            if (command != null) {
                System.out.println("BookServlet GET: Executing command: " + command.getClass().getSimpleName());
                command.execute(request, response);
                System.out.println("BookServlet GET: Command executed successfully");
            } else {
                System.out.println("BookServlet GET: Command factory returned null for action: " + action);
                response.sendRedirect(request.getContextPath() + "/BookServlet?action=books");
            }
        } catch (Exception e) {
            System.out.println("BookServlet GET: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error executing command: " + e.getMessage());
        }
    }
    
    /**
     * Centralized error handling
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage) 
            throws ServletException, IOException {
        System.out.println("BookServlet: Handling error: " + errorMessage);
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("views/books.jsp").forward(request, response);
    }
}