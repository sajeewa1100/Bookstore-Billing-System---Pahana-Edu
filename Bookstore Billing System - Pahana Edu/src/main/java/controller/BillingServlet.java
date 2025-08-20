package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import service.BillingService;
import service.ClientService;
import service.BookService;
import service.TierService;
import model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/BillingServlet")
public class BillingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(BillingServlet.class.getName());
    
    private BillingService billingService;
    private ClientService clientService;
    private BookService bookService;
    private TierService tierService;

    @Override
    public void init() throws ServletException {
        super.init();
        LOGGER.info("BillingServlet: Initializing servlet...");
        try {
            this.billingService = new BillingService();
            this.clientService = new ClientService();
            this.bookService = new BookService();
            this.tierService = new TierService();
            LOGGER.info("BillingServlet: Services initialized successfully");
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error initializing services: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Failed to initialize services", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        LOGGER.info("BillingServlet GET: Received action = " + action);

        try {
            switch (action != null ? action : "list") {
                case "list":
                case "billings":
                    handleListBillings(request, response);
                    break;
                case "view":
                    handleViewBilling(request, response);
                    break;
                case "create":
                    handleShowCreateForm(request, response);
                    break;
                case "search-client":
                    handleSearchClient(request, response);
                    break;
                case "search-book":
                    handleSearchBook(request, response);
                    break;
                case "print":
                    handlePrintBilling(request, response);
                    break;
                case "search":
                    handleSearchBillings(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/BillingServlet?action=list");
            }
        } catch (Exception e) {
            LOGGER.severe("BillingServlet GET: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing request: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        LOGGER.info("BillingServlet POST: Received action = " + action);

        try {
            switch (action != null ? action : "") {
                case "create":
                    handleCreateBilling(request, response);
                    break;
                case "complete":
                    handleCompleteBilling(request, response);
                    break;
                case "cancel":
                    handleCancelBilling(request, response);
                    break;
                case "delete":
                    handleDeleteBilling(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/BillingServlet?action=list");
            }
        } catch (Exception e) {
            LOGGER.severe("BillingServlet POST: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing request: " + e.getMessage());
        }
    }

    /**
     * Handle listing all billings
     */
    private void handleListBillings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("BillingServlet: handleListBillings() called");
        
        try {
            List<BillingDTO> billings = billingService.getAllBillings();
            LOGGER.info("BillingServlet: Retrieved " + billings.size() + " billings");
            
            request.setAttribute("billings", billings);
            request.setAttribute("totalBillings", billings.size());
            request.setAttribute("pageTitle", "Billing Management");
            
            // Calculate and set statistics
            setBillingStatistics(request, billings);
            
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error in handleListBillings: " + e.getMessage());
            e.printStackTrace();
            
            // Set empty data to prevent JSP errors
            request.setAttribute("billings", new ArrayList<BillingDTO>());
            request.setAttribute("totalBillings", 0);
            request.setAttribute("errorMessage", "Failed to retrieve billings: " + e.getMessage());
            setBillingStatisticsDefault(request);
            
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
        }
    }

    /**
     * Show create billing form
     */
    private void handleShowCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("BillingServlet: handleShowCreateForm() called");
        
        try {
            // Get all tiers for discount calculation display
            List<TierDTO> tiers = tierService.getAllTiers();
            request.setAttribute("tiers", tiers);
            request.setAttribute("pageTitle", "Create New Bill");
            
            request.getRequestDispatcher("views/create-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error showing create form: " + e.getMessage());
            handleError(request, response, "Error loading create form: " + e.getMessage());
        }
    }

    /**
     * Handle creating new billing
     */
    private void handleCreateBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        LOGGER.info("BillingServlet: handleCreateBilling() called");
        
        try {
            // Get form parameters
            String clientSearchValue = request.getParameter("clientSearchValue");
            String clientSearchType = request.getParameter("clientSearchType");
            String paymentMethod = request.getParameter("paymentMethod");
            String notes = request.getParameter("notes");
            
            // Validate client
            ClientDTO client = findClient(clientSearchValue, clientSearchType);
            if (client == null) {
                request.setAttribute("errorMessage", "Client not found. Please verify the search criteria.");
                handleShowCreateForm(request, response);
                return;
            }
            
            // Get book items
            List<BillingItemDTO> items = extractBillingItems(request);
            if (items.isEmpty()) {
                request.setAttribute("errorMessage", "At least one book item is required.");
                handleShowCreateForm(request, response);
                return;
            }
            
            // Create billing DTO
            BillingDTO billing = new BillingDTO();
            billing.setClientId(client.getId());
            billing.setClient(client);
            billing.setItems(items);
            billing.setPaymentMethod(paymentMethod != null ? paymentMethod : "CASH");
            billing.setNotes(notes);
            billing.setStatus("PENDING");
            
            // Create billing
            boolean created = billingService.createBilling(billing);
            
            if (created) {
                request.getSession().setAttribute("successMessage", 
                    "Billing created successfully! Bill Number: " + billing.getBillNumber());
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=view&id=" + billing.getId());
            } else {
                request.setAttribute("errorMessage", "Failed to create billing. Please try again.");
                handleShowCreateForm(request, response);
            }
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error creating billing: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error creating billing: " + e.getMessage());
            handleShowCreateForm(request, response);
        }
    }

    /**
     * Handle viewing billing details
     */
    private void handleViewBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billingIdStr = request.getParameter("id");
        LOGGER.info("BillingServlet: handleViewBilling() called with ID: " + billingIdStr);
        
        try {
            if (billingIdStr == null || billingIdStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Billing ID is required.");
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=list");
                return;
            }
            
            Long billingId = Long.parseLong(billingIdStr);
            BillingDTO billing = billingService.getBillingById(billingId);
            
            if (billing == null) {
                request.setAttribute("errorMessage", "Billing not found.");
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=list");
                return;
            }
            
            request.setAttribute("billing", billing);
            request.setAttribute("pageTitle", "Bill Details - " + billing.getBillNumber());
            
            request.getRequestDispatcher("views/view-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error viewing billing: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error viewing billing: " + e.getMessage());
        }
    }

    /**
     * Handle printing billing
     */
    private void handlePrintBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billingIdStr = request.getParameter("id");
        LOGGER.info("BillingServlet: handlePrintBilling() called with ID: " + billingIdStr);
        
        try {
            if (billingIdStr == null || billingIdStr.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Billing ID is required.");
                return;
            }
            
            Long billingId = Long.parseLong(billingIdStr);
            BillingDTO billing = billingService.getBillingById(billingId);
            
            if (billing == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Billing not found.");
                return;
            }
            
            request.setAttribute("billing", billing);
            request.getRequestDispatcher("views/print-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error printing billing: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error printing billing: " + e.getMessage());
        }
    }

    /**
     * Handle completing billing
     */
    private void handleCompleteBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billingIdStr = request.getParameter("billingId");
        LOGGER.info("BillingServlet: handleCompleteBilling() called with ID: " + billingIdStr);
        
        try {
            Long billingId = Long.parseLong(billingIdStr);
            boolean completed = billingService.completeBilling(billingId);
            
            if (completed) {
                request.getSession().setAttribute("successMessage", "Billing completed successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to complete billing.");
            }
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error completing billing: " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error completing billing: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=list");
    }

    /**
     * Handle cancelling billing
     */
    private void handleCancelBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billingIdStr = request.getParameter("billingId");
        LOGGER.info("BillingServlet: handleCancelBilling() called with ID: " + billingIdStr);
        
        try {
            Long billingId = Long.parseLong(billingIdStr);
            boolean cancelled = billingService.cancelBilling(billingId);
            
            if (cancelled) {
                request.getSession().setAttribute("successMessage", "Billing cancelled successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to cancel billing.");
            }
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error cancelling billing: " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error cancelling billing: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=list");
    }

    /**
     * Handle deleting billing
     */
    private void handleDeleteBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String billingIdStr = request.getParameter("billingId");
        LOGGER.info("BillingServlet: handleDeleteBilling() called with ID: " + billingIdStr);
        
        try {
            Long billingId = Long.parseLong(billingIdStr);
            boolean deleted = billingService.deleteBilling(billingId);
            
            if (deleted) {
                request.getSession().setAttribute("successMessage", "Billing deleted successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete billing.");
            }
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error deleting billing: " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error deleting billing: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=list");
    }

    /**
     * Handle searching billings
     */
    private void handleSearchBillings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String searchType = request.getParameter("searchType");
        String searchQuery = request.getParameter("searchQuery");
        
        LOGGER.info("BillingServlet: handleSearchBillings() - Type: " + searchType + ", Query: " + searchQuery);
        
        try {
            List<BillingDTO> billings = new ArrayList<>();
            
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                switch (searchType != null ? searchType : "billNumber") {
                    case "billNumber":
                        billings = billingService.searchBillingsByBillNumber(searchQuery.trim());
                        break;
                    case "status":
                        billings = billingService.getBillingsByStatus(searchQuery.trim().toUpperCase());
                        break;
                    default:
                        billings = billingService.getAllBillings();
                }
            } else {
                billings = billingService.getAllBillings();
            }
            
            request.setAttribute("billings", billings);
            request.setAttribute("totalBillings", billings.size());
            request.setAttribute("searchType", searchType);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("pageTitle", "Billing Search Results");
            
            setBillingStatistics(request, billings);
            
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error searching billings: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error searching billings: " + e.getMessage());
        }
    }

    /**
     * Handle client search (non-AJAX)
     */
    private void handleSearchClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String searchValue = request.getParameter("searchValue");
        String searchType = request.getParameter("searchType");
        
        LOGGER.info("BillingServlet: handleSearchClient() - Type: " + searchType + ", Value: " + searchValue);
        
        try {
            ClientDTO client = findClient(searchValue, searchType);
            
            // For non-AJAX, we return to the create form with client data
            if (client != null) {
                request.setAttribute("selectedClient", client);
                request.setAttribute("successMessage", "Client found: " + client.getFullName());
            } else {
                request.setAttribute("errorMessage", "Client not found with the given search criteria.");
            }
            
            // Preserve search parameters
            request.setAttribute("clientSearchValue", searchValue);
            request.setAttribute("clientSearchType", searchType);
            
            handleShowCreateForm(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error searching client: " + e.getMessage());
            request.setAttribute("errorMessage", "Error searching client: " + e.getMessage());
            handleShowCreateForm(request, response);
        }
    }

    /**
     * Handle book search (non-AJAX)
     */
    private void handleSearchBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String isbn = request.getParameter("isbn");
        
        LOGGER.info("BillingServlet: handleSearchBook() - ISBN: " + isbn);
        
        try {
            BookDTO book = null;
            if (isbn != null && !isbn.trim().isEmpty()) {
                book = bookService.findBookByISBN(isbn.trim());
            }
            
            if (book != null) {
                request.setAttribute("selectedBook", book);
                request.setAttribute("successMessage", "Book found: " + book.getTitle());
            } else {
                request.setAttribute("errorMessage", "Book not found with ISBN: " + isbn);
            }
            
            // Preserve search parameter
            request.setAttribute("bookIsbn", isbn);
            
            handleShowCreateForm(request, response);
            
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error searching book: " + e.getMessage());
            request.setAttribute("errorMessage", "Error searching book: " + e.getMessage());
            handleShowCreateForm(request, response);
        }
    }

    // Helper methods
    
    /**
     * Find client based on search criteria
     */
    private ClientDTO findClient(String searchValue, String searchType) throws Exception {
        if (searchValue == null || searchValue.trim().isEmpty()) {
            return null;
        }
        
        ClientDTO client = null;
        
        switch (searchType != null ? searchType : "phone") {
            case "phone":
                client = clientService.findClientByPhone(searchValue.trim());
                break;
            case "accountNumber":
                client = clientService.findClientByAccountNumber(searchValue.trim());
                break;
            case "name":
                List<ClientDTO> clients = clientService.searchClientsByName(searchValue.trim());
                if (!clients.isEmpty()) {
                    client = clients.get(0); // Return first match
                }
                break;
        }
        
        return client;
    }

    /**
     * Extract billing items from request parameters
     */
    private List<BillingItemDTO> extractBillingItems(HttpServletRequest request) throws Exception {
        List<BillingItemDTO> items = new ArrayList<>();
        
        String[] bookIds = request.getParameterValues("bookId");
        String[] quantities = request.getParameterValues("quantity");
        
        if (bookIds != null && quantities != null && bookIds.length == quantities.length) {
            for (int i = 0; i < bookIds.length; i++) {
                if (bookIds[i] != null && !bookIds[i].trim().isEmpty() && 
                    quantities[i] != null && !quantities[i].trim().isEmpty()) {
                    
                    int bookId = Integer.parseInt(bookIds[i]);
                    int quantity = Integer.parseInt(quantities[i]);
                    
                    if (quantity > 0) {
                        BookDTO book = bookService.getBookById(bookId);
                        if (book != null) {
                            BillingItemDTO item = new BillingItemDTO();
                            item.setBookId(bookId);
                            item.setBookTitle(book.getTitle());
                            item.setBookAuthor(book.getAuthor());
                            item.setBookIsbn(book.getIsbn());
                            item.setUnitPrice(book.getPrice());
                            item.setQuantity(quantity);
                            item.calculateTotalPrice();
                            
                            items.add(item);
                        }
                    }
                }
            }
        }
        
        return items;
    }

    /**
     * Set billing statistics
     */
    private void setBillingStatistics(HttpServletRequest request, List<BillingDTO> billings) {
        try {
            int totalBillings = billings.size();
            int pendingCount = 0;
            int completedCount = 0;
            int cancelledCount = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (BillingDTO billing : billings) {
                String status = billing.getStatus();
                
                switch (status != null ? status : "PENDING") {
                    case "PENDING":
                        pendingCount++;
                        break;
                    case "COMPLETED":
                        completedCount++;
                        if (billing.getTotalAmount() != null) {
                            totalRevenue = totalRevenue.add(billing.getTotalAmount());
                        }
                        break;
                    case "CANCELLED":
                        cancelledCount++;
                        break;
                    default:
                        pendingCount++;
                        break;
                }
            }

            request.setAttribute("pendingBillingsCount", pendingCount);
            request.setAttribute("completedBillingsCount", completedCount);
            request.setAttribute("cancelledBillingsCount", cancelledCount);
            request.setAttribute("totalRevenue", totalRevenue);

            if (totalBillings > 0) {
                request.setAttribute("pendingPercentage", Math.round((pendingCount * 100.0) / totalBillings));
                request.setAttribute("completedPercentage", Math.round((completedCount * 100.0) / totalBillings));
                request.setAttribute("cancelledPercentage", Math.round((cancelledCount * 100.0) / totalBillings));
            } else {
                request.setAttribute("pendingPercentage", 0);
                request.setAttribute("completedPercentage", 0);
                request.setAttribute("cancelledPercentage", 0);
            }

        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error calculating statistics: " + e.getMessage());
            setBillingStatisticsDefault(request);
        }
    }

    /**
     * Set default billing statistics
     */
    private void setBillingStatisticsDefault(HttpServletRequest request) {
        request.setAttribute("pendingBillingsCount", 0);
        request.setAttribute("completedBillingsCount", 0);
        request.setAttribute("cancelledBillingsCount", 0);
        request.setAttribute("totalRevenue", BigDecimal.ZERO);
        request.setAttribute("pendingPercentage", 0);
        request.setAttribute("completedPercentage", 0);
        request.setAttribute("cancelledPercentage", 0);
    }

    /**
     * Handle errors
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage) 
            throws ServletException, IOException {
        LOGGER.severe("BillingServlet: Handling error: " + errorMessage);
        
        request.setAttribute("billings", new ArrayList<BillingDTO>());
        request.setAttribute("totalBillings", 0);
        setBillingStatisticsDefault(request);
        request.setAttribute("errorMessage", errorMessage);
        
        try {
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.severe("BillingServlet: Error forwarding to JSP: " + e.getMessage());
            response.setContentType("text/html");
            try (PrintWriter out = response.getWriter()) {
                out.println("<html><body>");
                out.println("<h1>Billing System Error</h1>");
                out.println("<p>" + errorMessage + "</p>");
                out.println("<a href='" + request.getContextPath() + "/BillingServlet'>Back to Billing</a>");
                out.println("</body></html>");
            }
        }
    }
}