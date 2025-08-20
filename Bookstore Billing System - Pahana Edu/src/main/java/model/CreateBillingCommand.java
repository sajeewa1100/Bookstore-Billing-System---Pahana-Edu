package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.BillingService;
import service.ClientService;
import service.BookService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreateBillingCommand implements BillingCommand {

    private final BillingService billingService;
    private final ClientService clientService;
    private final BookService bookService;

    public CreateBillingCommand(BillingService billingService, ClientService clientService, BookService bookService) {
        if (billingService == null || clientService == null || bookService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        this.billingService = billingService;
        this.clientService = clientService;
        this.bookService = bookService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        try {
            // Get client ID
            String clientIdStr = request.getParameter("clientId");
            if (clientIdStr == null || clientIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Client ID is required");
            }
            Long clientId = Long.parseLong(clientIdStr);

            // Get payment method
            String paymentMethod = request.getParameter("paymentMethod");
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                paymentMethod = "CASH";
            }

            // Get notes
            String notes = request.getParameter("notes");

            // Create billing object
            BillingDTO billing = new BillingDTO(clientId, paymentMethod);
            billing.setNotes(notes);

            // Get billing items from request
            List<BillingItemDTO> items = parseBillingItems(request);
            if (items.isEmpty()) {
                throw new IllegalArgumentException("At least one item is required");
            }

            // Set items and calculate amounts
            for (BillingItemDTO item : items) {
                billing.addItem(item);
            }

            // Get client information
            ClientDTO client = clientService.getClientById(clientId);
            if (client != null) {
                billing.setClient(client);
            }

            // Create billing
            boolean created = billingService.createBilling(billing);
            
            HttpSession session = request.getSession();
            if (created) {
                session.setAttribute("successMessage", "Billing created successfully: " + billing.getBillNumber());
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=view&id=" + billing.getId());
            } else {
                session.setAttribute("errorMessage", "Failed to create billing");
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
            }

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error creating billing: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
        }
    }

    /**
     * Parse billing items from request parameters
     */
    private List<BillingItemDTO> parseBillingItems(HttpServletRequest request) {
        List<BillingItemDTO> items = new ArrayList<>();
        
        String[] bookIds = request.getParameterValues("bookId");
        String[] quantities = request.getParameterValues("quantity");
        String[] unitPrices = request.getParameterValues("unitPrice");
        String[] bookTitles = request.getParameterValues("bookTitle");
        String[] bookAuthors = request.getParameterValues("bookAuthor");
        String[] bookIsbns = request.getParameterValues("bookIsbn");
        
        if (bookIds != null) {
            for (int i = 0; i < bookIds.length; i++) {
                try {
                    BillingItemDTO item = new BillingItemDTO();
                    item.setBookId(Integer.parseInt(bookIds[i]));
                    item.setQuantity(Integer.parseInt(quantities[i]));
                    item.setUnitPrice(new BigDecimal(unitPrices[i]));
                    item.setBookTitle(bookTitles[i]);
                    item.setBookAuthor(bookAuthors[i]);
                    item.setBookIsbn(bookIsbns[i]);
                    
                    // Calculate total price
                    BigDecimal total = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    item.setTotalPrice(total);
                    
                    items.add(item);
                } catch (Exception e) {
                    System.err.println("Error parsing billing item at index " + i + ": " + e.getMessage());
                }
            }
        }
        
        return items;
    }
}