// Fixed CreateBillingCommand
package model;

import service.BillingService;
import service.ClientService;
import service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CreateBillingCommand implements BillingCommand {
    
    private static final Logger LOGGER = Logger.getLogger(CreateBillingCommand.class.getName());
    private BillingService billingService;
    private ClientService clientService;
    private BookService bookService;
    
    public CreateBillingCommand(BillingService billingService, ClientService clientService, BookService bookService) {
        this.billingService = billingService;
        this.clientService = clientService;
        this.bookService = bookService;
    }
    
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        LOGGER.info("CreateBillingCommand: Executing command to create billing");
        
        String method = request.getMethod();
        
        if ("GET".equals(method)) {
            // Show billing creation form
            showBillingForm(request, response);
        } else if ("POST".equals(method)) {
            // Process billing creation
            processBillingCreation(request, response);
        }
    }
    
    private void showBillingForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Get all clients and books for the form
            List<ClientDTO> clients = clientService.getAllClients();
            List<BookDTO> books = bookService.getAllBooks();
            
            request.setAttribute("clients", clients);
            request.setAttribute("books", books);
            
            LOGGER.info("CreateBillingCommand: Showing billing form with " + 
                       clients.size() + " clients and " + books.size() + " books");
            
            request.getRequestDispatcher("views/create-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "CreateBillingCommand: Error showing form", e);
            request.setAttribute("errorMessage", "Error loading billing form: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
        }
    }
    
    private void processBillingCreation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Get form parameters
            String clientIdStr = request.getParameter("clientId");
            String paymentMethod = request.getParameter("paymentMethod");
            String discountPercentageStr = request.getParameter("discountPercentage");
            String notes = request.getParameter("notes");
            
            // Get selected books and quantities - Fixed parameter names
            String[] bookIds = request.getParameterValues("bookIds");
            String[] quantities = request.getParameterValues("quantities");
            
            LOGGER.info("CreateBillingCommand: Processing billing creation");
            LOGGER.info("Client ID: " + clientIdStr);
            LOGGER.info("Books selected: " + (bookIds != null ? bookIds.length : 0));
            
            // Validation
            if (clientIdStr == null || clientIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Please select a client");
            }
            
            if (bookIds == null || bookIds.length == 0) {
                throw new IllegalArgumentException("Please select at least one book");
            }
            
            Long clientId = Long.parseLong(clientIdStr);
            ClientDTO client = clientService.getClientById(clientId);
            if (client == null) {
                throw new IllegalArgumentException("Selected client not found");
            }
            
            // Create billing DTO
            BillingDTO billing = new BillingDTO(clientId, client.getFullName(), 
                                              client.getEmail(), client.getPhone());
            billing.setPaymentMethod(paymentMethod);
            billing.setNotes(notes);
            
            // Add bill items
            for (int i = 0; i < bookIds.length; i++) {
                String bookIdStr = bookIds[i];
                String quantityStr = quantities[i];
                
                if (bookIdStr == null || bookIdStr.trim().isEmpty() || 
                    quantityStr == null || quantityStr.trim().isEmpty() || "0".equals(quantityStr)) {
                    continue; // Skip items with zero or empty values
                }
                
                int bookId = Integer.parseInt(bookIdStr);
                int quantity = Integer.parseInt(quantityStr);
                
                if (quantity <= 0) {
                    continue; // Skip invalid quantities
                }
                
                BookDTO book = bookService.getBookById(bookId);
                if (book != null) {
                    BillItemDTO item = new BillItemDTO(book, quantity);
                    billing.addItem(item);
                    LOGGER.info("Added item: " + book.getTitle() + " x " + quantity);
                }
            }
            
            if (billing.getItems().isEmpty()) {
                throw new IllegalArgumentException("No valid items found for billing");
            }
            
            // Apply discount percentage if specified
            if (discountPercentageStr != null && !discountPercentageStr.trim().isEmpty()) {
                try {
                    BigDecimal discountPercentage = new BigDecimal(discountPercentageStr);
                    billing.applyDiscountPercentage(discountPercentage);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Invalid discount percentage, ignoring: " + discountPercentageStr);
                }
            }
            
            // Create the bill
            boolean success = billingService.createBill(billing);
            
            if (success) {
                request.getSession().setAttribute("successMessage", 
                    "Bill created successfully! Bill Number: " + billing.getBillNumber());
                LOGGER.info("CreateBillingCommand: Bill created successfully - " + billing.getBillNumber());
                
                // Redirect to billing list
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
            } else {
                throw new RuntimeException("Failed to create bill");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "CreateBillingCommand: Error creating bill", e);
            
            request.getSession().setAttribute("errorMessage", 
                "Error creating bill: " + e.getMessage());
            
            // Redirect back to billing form
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=create");
        }
    }
}