package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BillingService;
import service.BookService;
import service.ClientService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command to show create billing form
 * This demonstrates how to properly fetch and set data for billing operations
 */
public class CreateBillingCommand implements BillingCommand {
    
    private static final Logger LOGGER = Logger.getLogger(CreateBillingCommand.class.getName());
    
    private final BillingService billingService;
    private final ClientService clientService;
    private final BookService bookService;
    
    public CreateBillingCommand(BillingService billingService, ClientService clientService, BookService bookService) {
        this.billingService = billingService;
        this.clientService = clientService;
        this.bookService = bookService;
    }
    
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            LOGGER.info("CreateBillingCommand: Executing create billing command");
            
            // 1. Fetch all books for selection (CRITICAL!)
            List<BookDTO> allBooks = bookService.getAllBooks();
            request.setAttribute("allBooks", allBooks);
            LOGGER.info("CreateBillingCommand: Set " + allBooks.size() + " books in request");
            
            // 2. Fetch all clients for selection (CRITICAL!)
            List<ClientDTO> allClients = clientService.getAllClients();
            request.setAttribute("allClients", allClients);
            LOGGER.info("CreateBillingCommand: Set " + allClients.size() + " clients in request");
            
            // 3. Fetch book categories for filtering
            List<String> categories = bookService.getBookCategories();
            request.setAttribute("bookCategories", categories);
            LOGGER.info("CreateBillingCommand: Set " + categories.size() + " categories in request");
            
            // 4. Create new billing object for form
            BillingDTO newBilling = new BillingDTO();
            newBilling.generateBillNumber();
            request.setAttribute("billing", newBilling);
            LOGGER.info("CreateBillingCommand: Created new billing with number: " + newBilling.getBillNumber());
            
            // 5. Set form mode
            request.setAttribute("mode", "create");
            
            // 6. Debug verification
            LOGGER.info("CreateBillingCommand: Data verification:");
            LOGGER.info("  - Books count: " + (allBooks != null ? allBooks.size() : "NULL"));
            LOGGER.info("  - Clients count: " + (allClients != null ? allClients.size() : "NULL"));
            LOGGER.info("  - Categories count: " + (categories != null ? categories.size() : "NULL"));
            
            // Test book service connectivity
            if (allBooks.isEmpty()) {
                LOGGER.warning("CreateBillingCommand: No books found! Testing BookService...");
                bookService.testServiceConnectivity();
            }
            
            // 7. Forward to create billing JSP
            LOGGER.info("CreateBillingCommand: Forwarding to create-billing.jsp");
            request.getRequestDispatcher("/views/create-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "CreateBillingCommand: Error executing command", e);
            
            // Set empty lists as fallback to prevent JSP errors
            request.setAttribute("allBooks", new java.util.ArrayList<BookDTO>());
            request.setAttribute("allClients", new java.util.ArrayList<ClientDTO>());
            request.setAttribute("bookCategories", new java.util.ArrayList<String>());
            request.setAttribute("errorMessage", "Error loading billing form data: " + e.getMessage());
            
            // Still forward to the page but with error message
            request.getRequestDispatcher("/views/create-billing.jsp").forward(request, response);
        }
    }
}