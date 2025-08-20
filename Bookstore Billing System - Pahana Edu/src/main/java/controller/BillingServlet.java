package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import service.BillingService;
import service.BookService;
import service.ClientService;
import model.BillingCommand;
import model.BillingCommandFactory;
import model.BookDTO;
import model.ClientDTO;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/BillingServlet")
public class BillingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BillingService billingService;
    private BookService bookService;
    private ClientService clientService;
    private static final Logger LOGGER = Logger.getLogger(BillingServlet.class.getName());

    @Override
    public void init() {
        LOGGER.info("BillingServlet: Initializing servlet...");
        billingService = new BillingService();
        bookService = new BookService();
        clientService = new ClientService(); // Initialize ClientService
        LOGGER.info("BillingServlet: All services initialized successfully");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        LOGGER.info("BillingServlet POST: Received action = " + action);
        
        // Print all parameters for debugging
        request.getParameterMap().forEach((key, values) -> {
            LOGGER.info("BillingServlet POST: " + key + " = " + String.join(", ", values));
        });
        
        // Validate action parameter
        if (action == null || action.trim().isEmpty()) {
            LOGGER.warning("BillingServlet POST: No action specified, redirecting to billings");
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
            return;
        }

        try {
            // CRITICAL: Always ensure data is available for billing operations
            setupBillingData(request);
            
            LOGGER.info("BillingServlet POST: Creating command for action: " + action);
            BillingCommand command = BillingCommandFactory.createCommand(action, billingService, clientService, bookService);

            if (command != null) {
                LOGGER.info("BillingServlet POST: Executing command: " + command.getClass().getSimpleName());
                command.execute(request, response);
                LOGGER.info("BillingServlet POST: Command executed successfully");
            } else {
                LOGGER.warning("BillingServlet POST: Command factory returned null for action: " + action);
                setupBillingData(request); // Ensure data is available before forwarding
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "BillingServlet POST: Exception occurred: " + e.getMessage(), e);
            setupBillingData(request); // Ensure data is available for error page
            handleError(request, response, "Error executing command: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        LOGGER.info("BillingServlet GET: Received action = " + action);

        // Default action if none specified
        if (action == null || action.trim().isEmpty()) {
            action = "billings"; // Default action
            LOGGER.info("BillingServlet GET: Using default action = " + action);
        }

        try {
            // Fetch data from the services
            List<ClientDTO> clients = clientService.getAllClients();
            List<BookDTO> books = bookService.getAllBooks();
            
            // Set data as request attributes
            request.setAttribute("clients", clients);
            request.setAttribute("books", books);

            LOGGER.info("BillingServlet GET: Fetched " + clients.size() + " clients and " + books.size() + " books");

            // Create command for action (ensure the correct action is passed)
            BillingCommand command = BillingCommandFactory.createCommand(action, billingService, clientService, bookService);
            if (command != null) {
                command.execute(request, response);
                LOGGER.info("BillingServlet GET: Command executed successfully");
            } else {
                LOGGER.warning("BillingServlet GET: Command factory returned null for action: " + action);
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "BillingServlet GET: Exception occurred: " + e.getMessage(), e);
            handleError(request, response, "Error executing command: " + e.getMessage());
        }
    }

    

    /**
     * CRITICAL METHOD: Setup billing data for JSP pages
     * This ensures books and clients data are always available
     */
    private void setupBillingData(HttpServletRequest request) {
        try {
            LOGGER.info("BillingServlet: Setting up billing data...");
            
            // 1. Fetch all books for billing
            List<BookDTO> allBooks = bookService.getAllBooks();
            request.setAttribute("allBooks", allBooks);
            LOGGER.info("BillingServlet: Set " + allBooks.size() + " books in request");
            
            // 2. Fetch all clients for billing
            List<ClientDTO> allClients = clientService.getAllClients();
            request.setAttribute("allClients", allClients);
            LOGGER.info("BillingServlet: Set " + allClients.size() + " clients in request");
            
            // 3. Set book categories
            List<String> categories = bookService.getBookCategories();
            request.setAttribute("bookCategories", categories);
            LOGGER.info("BillingServlet: Set " + categories.size() + " categories in request");
            
            // 4. Debug logging
            LOGGER.info("BillingServlet: Data setup completed successfully");
            LOGGER.info("BillingServlet: Books available: " + (allBooks != null ? allBooks.size() : 0));
            LOGGER.info("BillingServlet: Clients available: " + (allClients != null ? allClients.size() : 0));
            
            // 5. Test first few items for debugging
            if (allBooks != null && !allBooks.isEmpty()) {
                BookDTO firstBook = allBooks.get(0);
                LOGGER.info("BillingServlet: First book - ID: " + firstBook.getId() + 
                           ", Title: " + firstBook.getTitle() + 
                           ", Stock: " + firstBook.getQuantity());
            }
            
            if (allClients != null && !allClients.isEmpty()) {
                ClientDTO firstClient = allClients.get(0);
                LOGGER.info("BillingServlet: First client - ID: " + firstClient.getId() + 
                           ", Name: " + firstClient.getFullName());
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "BillingServlet: Error setting up billing data", e);
            
            // Set empty lists as fallback
            request.setAttribute("allBooks", new java.util.ArrayList<BookDTO>());
            request.setAttribute("allClients", new java.util.ArrayList<ClientDTO>());
            request.setAttribute("bookCategories", new java.util.ArrayList<String>());
        }
    }

    /**
     * Centralized error handling with data setup
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage) 
            throws ServletException, IOException {
        LOGGER.warning("BillingServlet: Handling error: " + errorMessage);
        
        // Ensure data is available even on error pages
        setupBillingData(request);
        
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("views/billings.jsp").forward(request, response);
    }
    
    /**
     * Helper method to get book by ISBN for AJAX calls
     */
    public BookDTO getBookByISBN(String isbn) {
        try {
            return bookService.searchBookByISBN(isbn);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error fetching book by ISBN: " + isbn, e);
            return null;
        }
    }
    
    /**
     * Helper method to get client by ID for AJAX calls
     */
    public ClientDTO getClientById(Long clientId) {
        try {
            return clientService.getClientById(clientId);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error fetching client by ID: " + clientId, e);
            return null;
        }
    }
}