package model;


import service.BillingService;
import service.ClientService;
import service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Command to create new billing
 */
class CreateBillingCommand implements BillingCommand {
    
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
        
        System.out.println("CreateBillingCommand: Executing command to create billing");
        
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
            java.util.List<ClientDTO> clients = clientService.getAllClients();
            java.util.List<BookDTO> books = bookService.getAllBooks();
            
            request.setAttribute("clients", clients);
            request.setAttribute("books", books);
            
            System.out.println("CreateBillingCommand: Showing billing form with " + 
                             clients.size() + " clients and " + books.size() + " books");
            
            request.getRequestDispatcher("views/create-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("CreateBillingCommand: Error showing form - " + e.getMessage());
            e.printStackTrace();
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
            String discountAmountStr = request.getParameter("discountAmount");
            String notes = request.getParameter("notes");
            
            // Get selected books and quantities
            String[] bookIds = request.getParameterValues("bookId");
            String[] quantities = request.getParameterValues("quantity");
            
            System.out.println("CreateBillingCommand: Processing billing creation");
            System.out.println("Client ID: " + clientIdStr);
            System.out.println("Books selected: " + (bookIds != null ? bookIds.length : 0));
            
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
            
            // Add discount if specified
            if (discountAmountStr != null && !discountAmountStr.trim().isEmpty()) {
                try {
                    java.math.BigDecimal discountAmount = new java.math.BigDecimal(discountAmountStr);
                    billing.applyDiscountAmount(discountAmount);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid discount amount, ignoring: " + discountAmountStr);
                }
            }
            
            // Add bill items
            for (int i = 0; i < bookIds.length; i++) {
                if (quantities[i] == null || quantities[i].trim().isEmpty() || "0".equals(quantities[i])) {
                    continue; // Skip items with zero or empty quantity
                }
                
                int bookId = Integer.parseInt(bookIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                
                if (quantity <= 0) {
                    continue; // Skip invalid quantities
                }
                
                BookDTO book = bookService.getBookById(bookId);
                if (book != null) {
                    BillItemDTO item = new BillItemDTO(book, quantity);
                    billing.addItem(item);
                    System.out.println("Added item: " + book.getTitle() + " x " + quantity);
                }
            }
            
            if (billing.getItems().isEmpty()) {
                throw new IllegalArgumentException("No valid items found for billing");
            }
            
            // Create the bill
            boolean success = billingService.createBill(billing);
            
            if (success) {
                request.getSession().setAttribute("successMessage", 
                    "Bill created successfully! Bill Number: " + billing.getBillNumber());
                System.out.println("CreateBillingCommand: Bill created successfully - " + billing.getBillNumber());
                
                // Redirect to view the created bill
                response.sendRedirect(request.getContextPath() + 
                    "/BillingServlet?action=view&id=" + billing.getId());
            } else {
                throw new RuntimeException("Failed to create bill");
            }
            
        } catch (Exception e) {
            System.err.println("CreateBillingCommand: Error creating bill - " + e.getMessage());
            e.printStackTrace();
            
            request.getSession().setAttribute("errorMessage", 
                "Error creating bill: " + e.getMessage());
            
            // Redirect back to billing form
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=create");
        }
    }
}

