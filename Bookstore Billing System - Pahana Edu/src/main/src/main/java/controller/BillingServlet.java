package controller;

import service.BillingService;
import service.EmailService;
import command.BillingCommand;
import command.CommandResult;
import command.BillingCommandFactory;
import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import util.AuthorizationUtil;


@WebServlet(urlPatterns = {"/billing", "/create-invoice"})
public class BillingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    
    
    private BillingService billingService;
    private BillingCommandFactory commandFactory;
    
    @Override
    public void init() throws ServletException {
        super.init();
        billingService = new BillingService();
        commandFactory = new BillingCommandFactory(billingService);
        
        // Test on startup
        try {
            List<ClientDTO> allClients = billingService.getAllClients();
            System.out.println("BillingServlet initialized - Found " + allClients.size() + " clients");
            
            List<BookDTO> allBooks = billingService.getAllBooks();
            System.out.println("BillingServlet initialized - Found " + allBooks.size() + " books");
        } catch (Exception e) {
            System.out.println("BillingServlet initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	if (!AuthorizationUtil.hasStaffAccess(request)) {
    	    response.sendRedirect("views/login.jsp?error=Access denied");
    	    return;
    	}
        
        String servletPath = request.getServletPath();
        
        try {
            if ("/create-invoice".equals(servletPath)) {
                handleCreateInvoicePage(request, response);
            } else {
                handleBillingPage(request, response);
            }
        } catch (Exception e) {
            System.out.println("Error in doGet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	
    	// Staff+ access control
    	if (!AuthorizationUtil.hasStaffAccess(request)) {
    	    response.sendRedirect("views/login.jsp?error=Access denied");
    	    return;
    	}
        
        String servletPath = request.getServletPath();
        
        try {
            if ("/create-invoice".equals(servletPath)) {
                handleCreateInvoicePost(request, response);
            } else {
                handleBillingPost(request, response);
            }
        } catch (Exception e) {
            System.out.println("Error in doPost: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/billing");
        }
    }
    
    // ===== BILLING DASHBOARD METHODS =====
    
    private void handleBillingPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "dashboard";
        
        System.out.println("BillingServlet.handleBillingPage - Action: " + action);
        
        switch (action) {
            case "dashboard":
                showDashboard(request, response);
                break;
            case "view":
                showInvoiceDetails(request, response);
                break;
            case "search":
                handleInvoiceSearch(request, response);
                break;
            default:
                showDashboard(request, response);
                break;
        }
    }
    
    private void handleBillingPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("BillingServlet.handleBillingPost - Action: " + action);
        
        switch (action) {
            case "delete":
                deleteInvoice(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/billing");
                break;
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("BillingServlet.showDashboard called");
        
        try {
            String searchTerm = request.getParameter("search");
            String searchType = request.getParameter("searchType");
            
            List<InvoiceDTO> invoices;
            
            // Handle search
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // Default search type to "id" if not specified
                if (searchType == null || searchType.isEmpty()) {
                    searchType = "id";
                }
                
                System.out.println("Searching invoices - Term: '" + searchTerm + "', Type: '" + searchType + "'");
                invoices = billingService.searchInvoices(searchTerm.trim(), searchType);
                
                if (invoices.isEmpty()) {
                    String searchTypeLabel = "id".equals(searchType) ? "ID/Invoice Number" : "Phone Number";
                    request.setAttribute("errorMessage", 
                        "No invoices found for " + searchTypeLabel + ": '" + searchTerm + "'");
                } else {
                    request.setAttribute("successMessage", 
                        "Found " + invoices.size() + " invoice(s) matching '" + searchTerm + "'");
                }
                
                // Preserve search parameters for the form
                request.setAttribute("currentSearch", searchTerm);
                request.setAttribute("currentSearchType", searchType);
                
            } else {
                // No search - get all invoices
                System.out.println("Getting all invoices");
                invoices = billingService.getAllInvoices();
            }
            
            request.setAttribute("invoices", invoices);
            System.out.println("Retrieved " + invoices.size() + " invoices");
            
            // Calculate statistics (only from displayed invoices for search results)
            BigDecimal todayRevenue = BigDecimal.ZERO;
            int displayedInvoices = 0;
            
            // For simplicity, we'll calculate from all displayed invoices
            for (InvoiceDTO invoice : invoices) {
                if (invoice.getTotalAmount() != null) {
                    todayRevenue = todayRevenue.add(invoice.getTotalAmount());
                }
                displayedInvoices++;
            }
            
            // Get total invoice count for statistics (from all invoices, not search results)
            int totalInvoices;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // If searching, show total count separately
                totalInvoices = billingService.getTotalInvoiceCount();
            } else {
                // If not searching, total is same as displayed
                totalInvoices = displayedInvoices;
            }
            
            request.setAttribute("todayInvoices", displayedInvoices);
            request.setAttribute("todayRevenue", todayRevenue);
            request.setAttribute("totalInvoices", totalInvoices);
            
            System.out.println("Statistics - Displayed: " + displayedInvoices + ", Total: " + totalInvoices + ", Revenue: " + todayRevenue);
            
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.out.println("Error in showDashboard: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
        }
    }
    
    private void handleInvoiceSearch(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String searchTerm = request.getParameter("search");
        String searchType = request.getParameter("searchType");
        
        System.out.println("BillingServlet.handleInvoiceSearch - Term: '" + searchTerm + "', Type: '" + searchType + "'");
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Default to ID search if type not specified
            if (searchType == null || searchType.isEmpty()) {
                searchType = "id";
            }
            
            String encodedSearchTerm = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/billing?search=" + encodedSearchTerm + 
                                "&searchType=" + searchType);
        } else {
            response.sendRedirect(request.getContextPath() + "/billing");
        }
    }
    
    private void showInvoiceDetails(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("BillingServlet.showInvoiceDetails called");
        
        try {
            String invoiceIdStr = request.getParameter("id");
            if (invoiceIdStr == null || invoiceIdStr.isEmpty()) {
                System.out.println("No invoice ID provided, redirecting to billing dashboard");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            int invoiceId = Integer.parseInt(invoiceIdStr);
            System.out.println("Loading invoice details for ID: " + invoiceId);
            
            InvoiceDTO invoice = billingService.getInvoiceById(invoiceId);
            
            if (invoice == null) {
                System.out.println("Invoice not found for ID: " + invoiceId);
                request.getSession().setAttribute("errorMessage", "Invoice not found");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            System.out.println("Found invoice: " + invoice.getInvoiceNumber() + 
                             " with " + (invoice.getItems() != null ? invoice.getItems().size() : 0) + " items");
            
            request.setAttribute("invoice", invoice);
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid invoice ID format: " + request.getParameter("id"));
            request.getSession().setAttribute("errorMessage", "Invalid invoice ID");
            response.sendRedirect(request.getContextPath() + "/billing");
        } catch (Exception e) {
            System.out.println("Error loading invoice details: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error loading invoice: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/billing");
        }
    }
    
    private void deleteInvoice(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("BillingServlet.deleteInvoice called");
        
        try {
            String invoiceIdStr = request.getParameter("invoiceId");
            if (invoiceIdStr == null || invoiceIdStr.isEmpty()) {
                request.getSession().setAttribute("errorMessage", "Invoice ID is required");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            int invoiceId = Integer.parseInt(invoiceIdStr);
            System.out.println("Deleting invoice with ID: " + invoiceId);
            
            BillingCommand deleteCommand = commandFactory.createCommand("DELETE_INVOICE", invoiceId);
            CommandResult result = deleteCommand.execute();
            
            if (result.isSuccess()) {
                System.out.println("Invoice deleted successfully");
                request.getSession().setAttribute("successMessage", "Invoice deleted successfully!");
            } else {
                System.out.println("Failed to delete invoice: " + result.getMessage());
                request.getSession().setAttribute("errorMessage", "Failed to delete invoice: " + result.getMessage());
            }
            
            response.sendRedirect(request.getContextPath() + "/billing");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid invoice ID format for deletion: " + request.getParameter("invoiceId"));
            request.getSession().setAttribute("errorMessage", "Invalid invoice ID");
            response.sendRedirect(request.getContextPath() + "/billing");
        } catch (Exception e) {
            System.out.println("Error deleting invoice: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error deleting invoice: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/billing");
        }
    }
    
    // ===== CREATE INVOICE METHODS =====
    
    private void handleCreateInvoicePage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        System.out.println("BillingServlet.handleCreateInvoicePage - Action: " + action);
        
        // Initialize session objects if needed
        initializeSessionObjects(session);
        
        try {
            switch (action == null ? "" : action) {
                case "searchClient":
                    handleClientSearch(request, response);
                    break;
                case "selectClient":
                    handleClientSelection(request, response);
                    break;
                case "searchBook":
                    handleBookSearch(request, response);
                    break;
                case "addBookToInvoice":
                    handleAddBookToInvoice(request, response);
                    break;
                case "removeItem":
                    handleRemoveItem(request, response);
                    break;
                case "updateQuantity":
                    handleUpdateQuantity(request, response);
                    break;
                default:
                    showCreateInvoiceForm(request, response);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error in handleCreateInvoicePage: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("views/create-invoice.jsp").forward(request, response);
        }
    }
    
    private void handleCreateInvoicePost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("BillingServlet.handleCreateInvoicePost - Action: " + action);
        
        if ("createInvoice".equals(action)) {
            createInvoiceFromForm(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/create-invoice");
        }
    }
    
    private void initializeSessionObjects(HttpSession session) {
        if (session.getAttribute("selectedBooks") == null) {
            session.setAttribute("selectedBooks", new ArrayList<BookDTO>());
        }
        if (session.getAttribute("bookQuantities") == null) {
            session.setAttribute("bookQuantities", new ArrayList<Integer>());
        }
    }
    
    private void showCreateInvoiceForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        ClientDTO selectedClient = (ClientDTO) session.getAttribute("selectedClient");
        @SuppressWarnings("unchecked")
        List<BookDTO> selectedBooks = (List<BookDTO>) session.getAttribute("selectedBooks");
        @SuppressWarnings("unchecked")
        List<Integer> bookQuantities = (List<Integer>) session.getAttribute("bookQuantities");
        
        if (selectedBooks != null && !selectedBooks.isEmpty()) {
            calculateInvoiceTotals(request, selectedClient, selectedBooks, bookQuantities);
        }
        
        request.getRequestDispatcher("views/create-invoice.jsp").forward(request, response);
    }
    
    // ===== CLIENT SEARCH METHODS =====
    
    private void handleClientSearch(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String searchTerm = request.getParameter("clientSearch");
        String searchType = request.getParameter("clientSearchType");
        
        System.out.println("=== CLIENT SEARCH ===");
        System.out.println("Search Term: '" + searchTerm + "'");
        System.out.println("Search Type: '" + searchType + "'");
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<ClientDTO> results = searchClients(searchTerm.trim(), searchType);
                request.setAttribute("clientSearchResults", results);
                
                if (results.isEmpty()) {
                    request.setAttribute("errorMessage", "No clients found matching '" + searchTerm + "'");
                } else {
                    System.out.println("Found " + results.size() + " clients");
                    request.setAttribute("successMessage", "Found " + results.size() + " client(s)");
                }
            } catch (Exception e) {
                System.out.println("Client search error: " + e.getMessage());
                request.setAttribute("errorMessage", "Error searching clients: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        showCreateInvoiceForm(request, response);
    }
    
    private List<ClientDTO> searchClients(String searchTerm, String searchType) {
        List<ClientDTO> results = new ArrayList<>();
        
        try {
            switch (searchType) {
                case "id":
                    try {
                        int clientId = Integer.parseInt(searchTerm);
                        ClientDTO client = billingService.getClientById(clientId);
                        if (client != null) {
                            results.add(client);
                            System.out.println("Found client by ID: " + client.getFullName());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format: " + searchTerm);
                    }
                    break;
                    
                case "phone":
                    // Try exact phone match first
                    ClientDTO phoneClient = billingService.getClientByPhone(searchTerm);
                    if (phoneClient != null) {
                        results.add(phoneClient);
                        System.out.println("Found exact phone match: " + phoneClient.getFullName());
                    }
                    
                    // Also try general search for partial phone matches
                    List<ClientDTO> generalResults = billingService.searchClients(searchTerm);
                    for (ClientDTO client : generalResults) {
                        if (client.getPhone() != null && client.getPhone().contains(searchTerm)) {
                            // Avoid duplicates
                            boolean alreadyExists = false;
                            for (ClientDTO existing : results) {
                                if (existing.getId() == client.getId()) {
                                    alreadyExists = true;
                                    break;
                                }
                            }
                            if (!alreadyExists) {
                                results.add(client);
                            }
                        }
                    }
                    break;
                    
                default: // name search
                    results = billingService.searchClients(searchTerm);
                    System.out.println("Name search returned: " + results.size() + " results");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Client search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    private void handleClientSelection(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String clientIdStr = request.getParameter("clientId");
        
        if (clientIdStr != null && !clientIdStr.isEmpty()) {
            try {
                int clientId = Integer.parseInt(clientIdStr);
                ClientDTO client = billingService.getClientById(clientId);
                
                if (client != null) {
                    request.getSession().setAttribute("selectedClient", client);
                    System.out.println("Selected client: " + client.getFullName());
                    request.getSession().setAttribute("successMessage", "Selected client: " + client.getFullName());
                } else {
                    request.setAttribute("errorMessage", "Client not found");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid client ID");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/create-invoice");
    }
    
    // ===== BOOK SEARCH METHODS =====
    
    private void handleBookSearch(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String searchTerm = request.getParameter("bookSearch");
        String searchType = request.getParameter("bookSearchType");
        
        System.out.println("=== BOOK SEARCH ===");
        System.out.println("Search Term: '" + searchTerm + "'");
        System.out.println("Search Type: '" + searchType + "'");
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<BookDTO> results = searchBooks(searchTerm.trim(), searchType);
                request.setAttribute("bookSearchResults", results);
                
                if (results.isEmpty()) {
                    request.setAttribute("errorMessage", "No books found matching '" + searchTerm + "'");
                } else {
                    System.out.println("Found " + results.size() + " books");
                    request.setAttribute("successMessage", "Found " + results.size() + " book(s)");
                    for (BookDTO book : results) {
                        System.out.println("  - " + book.getTitle() + " by " + book.getAuthor() + " (ISBN: " + book.getIsbn() + ")");
                    }
                }
            } catch (Exception e) {
                System.out.println("Book search error: " + e.getMessage());
                request.setAttribute("errorMessage", "Error searching books: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        showCreateInvoiceForm(request, response);
    }
    
    private List<BookDTO> searchBooks(String searchTerm, String searchType) {
        List<BookDTO> results = new ArrayList<>();
        
        try {
            if ("isbn".equals(searchType)) {
                System.out.println("Searching by ISBN: " + searchTerm);
                BookDTO book = billingService.getBookByISBN(searchTerm);
                if (book != null) {
                    results.add(book);
                    System.out.println("Found book by ISBN: " + book.getTitle());
                } else {
                    System.out.println("No book found with ISBN: " + searchTerm);
                }
            } else {
                // Default to title/author search
                System.out.println("Searching by title/author: " + searchTerm);
                results = billingService.searchBooks(searchTerm);
                System.out.println("Title/author search returned: " + results.size() + " results");
            }
        } catch (Exception e) {
            System.out.println("Book search error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    @SuppressWarnings("unchecked")
    private void handleAddBookToInvoice(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String bookIdStr = request.getParameter("bookId");
        
        if (bookIdStr != null && !bookIdStr.isEmpty()) {
            try {
                int bookId = Integer.parseInt(bookIdStr);
                BookDTO book = billingService.getBookById(bookId);
                
                if (book != null) {
                    HttpSession session = request.getSession();
                    List<BookDTO> selectedBooks = (List<BookDTO>) session.getAttribute("selectedBooks");
                    List<Integer> bookQuantities = (List<Integer>) session.getAttribute("bookQuantities");
                    
                    // Check if book already exists
                    boolean bookExists = false;
                    for (BookDTO existingBook : selectedBooks) {
                        if (existingBook.getId() == bookId) {
                            bookExists = true;
                            break;
                        }
                    }
                    
                    if (!bookExists) {
                        selectedBooks.add(book);
                        bookQuantities.add(1); // Default quantity
                        session.setAttribute("selectedBooks", selectedBooks);
                        session.setAttribute("bookQuantities", bookQuantities);
                        System.out.println("Added book to invoice: " + book.getTitle());
                        
                        // Set success message
                        request.getSession().setAttribute("successMessage", "Book added: " + book.getTitle());
                    } else {
                        request.setAttribute("errorMessage", "Book already added to invoice");
                    }
                } else {
                    request.setAttribute("errorMessage", "Book not found");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid book ID");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/create-invoice");
    }
    
    @SuppressWarnings("unchecked")
    private void handleRemoveItem(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String indexStr = request.getParameter("itemIndex");
        
        if (indexStr != null && !indexStr.isEmpty()) {
            try {
                int index = Integer.parseInt(indexStr);
                HttpSession session = request.getSession();
                List<BookDTO> selectedBooks = (List<BookDTO>) session.getAttribute("selectedBooks");
                List<Integer> bookQuantities = (List<Integer>) session.getAttribute("bookQuantities");
                
                if (index >= 0 && index < selectedBooks.size()) {
                    String removedBook = selectedBooks.get(index).getTitle();
                    selectedBooks.remove(index);
                    bookQuantities.remove(index);
                    session.setAttribute("selectedBooks", selectedBooks);
                    session.setAttribute("bookQuantities", bookQuantities);
                    System.out.println("Removed book from invoice: " + removedBook);
                    request.getSession().setAttribute("successMessage", "Removed: " + removedBook);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid item index");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/create-invoice");
    }
    
    @SuppressWarnings("unchecked")
    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String indexStr = request.getParameter("itemIndex");
        String quantityStr = request.getParameter("selectedBookQuantity");
        
        if (indexStr != null && quantityStr != null) {
            try {
                int index = Integer.parseInt(indexStr);
                int quantity = Integer.parseInt(quantityStr);
                
                if (quantity > 0) {
                    HttpSession session = request.getSession();
                    List<Integer> bookQuantities = (List<Integer>) session.getAttribute("bookQuantities");
                    
                    if (index >= 0 && index < bookQuantities.size()) {
                        bookQuantities.set(index, quantity);
                        session.setAttribute("bookQuantities", bookQuantities);
                        System.out.println("Updated quantity for item " + index + " to " + quantity);
                    }
                } else {
                    request.getSession().setAttribute("errorMessage", "Quantity must be greater than 0");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid quantity");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/create-invoice");
    }
    
    // ===== INVOICE CALCULATION AND CREATION =====
    
    private void calculateInvoiceTotals(HttpServletRequest request, ClientDTO selectedClient, 
                                      List<BookDTO> selectedBooks, List<Integer> bookQuantities) {
        
        if (selectedBooks == null || selectedBooks.isEmpty()) return;
        
        // Create invoice items for calculation
        List<InvoiceItemDTO> items = new ArrayList<>();
        for (int i = 0; i < selectedBooks.size(); i++) {
            BookDTO book = selectedBooks.get(i);
            Integer quantity = i < bookQuantities.size() ? bookQuantities.get(i) : 1;
            
            InvoiceItemDTO item = new InvoiceItemDTO();
            item.setBookId(book.getId());
            item.setQuantity(quantity);
            item.setUnitPrice(book.getPrice());
            item.setTotalPrice(book.getPrice().multiply(new BigDecimal(quantity)));
            items.add(item);
        }
        
        // Use BillingService calculation method
        BillingService.InvoiceCalculationResult result = billingService.calculateInvoice(items, selectedClient);
        
        // Set attributes
        request.setAttribute("invoiceSubtotal", result.getSubtotal());
        request.setAttribute("invoiceDiscount", result.getLoyaltyDiscount());
        request.setAttribute("invoiceTotal", result.getTotalAmount());
        request.setAttribute("pointsEarned", result.getLoyaltyPointsEarned());
        
        System.out.println("Invoice calculation - Subtotal: " + result.getSubtotal() + 
                          ", Discount: " + result.getLoyaltyDiscount() + 
                          ", Total: " + result.getTotalAmount() + 
                          ", Points: " + result.getLoyaltyPointsEarned());
    }
    
    private void createInvoiceFromForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("BillingServlet.createInvoiceFromForm called");
        
        try {
            HttpSession session = request.getSession();
            
            // Build invoice from form data
            InvoiceDTO invoice = new InvoiceDTO();
            
            // Client
            String clientIdStr = request.getParameter("clientId");
            ClientDTO selectedClient = null;
            if (clientIdStr != null && !clientIdStr.isEmpty()) {
                invoice.setClientId(Integer.parseInt(clientIdStr));
                selectedClient = (ClientDTO) session.getAttribute("selectedClient");
                System.out.println("Invoice for client ID: " + clientIdStr);
            } else {
                System.out.println("Walk-in customer invoice");
            }
            
            // Staff (default to 1)
            invoice.setStaffId(1);
            
            // Financial data
            invoice.setSubtotal(new BigDecimal(request.getParameter("subtotal")));
            invoice.setLoyaltyDiscount(new BigDecimal(request.getParameter("discount")));
            invoice.setTotalAmount(new BigDecimal(request.getParameter("totalAmount")));
            invoice.setLoyaltyPointsEarned(Integer.parseInt(request.getParameter("loyaltyPoints")));
            
            // Cash handling
            String cashGivenStr = request.getParameter("cashGiven");
            if (cashGivenStr != null && !cashGivenStr.isEmpty()) {
                BigDecimal cashGiven = new BigDecimal(cashGivenStr);
                invoice.setCashGiven(cashGiven);
                invoice.setChangeAmount(cashGiven.subtract(invoice.getTotalAmount()));
                System.out.println("Cash payment - Given: " + cashGiven + ", Change: " + invoice.getChangeAmount());
            }
            
            // Build items
            String[] bookIds = request.getParameterValues("bookIds");
            String[] quantities = request.getParameterValues("quantities");
            String[] unitPrices = request.getParameterValues("unitPrices");
            
            List<InvoiceItemDTO> items = new ArrayList<>();
            if (bookIds != null && quantities != null && unitPrices != null) {
                for (int i = 0; i < bookIds.length; i++) {
                    InvoiceItemDTO item = new InvoiceItemDTO();
                    item.setBookId(Integer.parseInt(bookIds[i]));
                    item.setQuantity(Integer.parseInt(quantities[i]));
                    item.setUnitPrice(new BigDecimal(unitPrices[i]));
                    item.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
                    items.add(item);
                }
            }
            invoice.setItems(items);
            
            System.out.println("Creating invoice with " + items.size() + " items, total: " + invoice.getTotalAmount());
            
            // Create invoice using command pattern
            BillingCommand createCommand = commandFactory.createCommand("CREATE_INVOICE", invoice);
            CommandResult result = createCommand.execute();
            
            if (result.isSuccess()) {
                int createdInvoiceId = (Integer) result.getData();
                System.out.println("Invoice created successfully with ID: " + createdInvoiceId);
                
                // Check if client has automatic email enabled and send email
                boolean emailSent = false;
                if (selectedClient != null && selectedClient.isSendMailAuto() && 
                    selectedClient.getEmail() != null && !selectedClient.getEmail().trim().isEmpty()) {
                    
                    System.out.println("Client has automatic email enabled, attempting to send invoice email...");
                    
                    try {
                        // Get the complete invoice with all details for email
                        InvoiceDTO completeInvoice = billingService.getInvoiceById(createdInvoiceId);
                        
                        if (completeInvoice != null) {
                            // Import the EmailService (you'll need to add this import at the top of your BillingServlet)
                            // import service.EmailService;
                            EmailService emailService = new EmailService();
                            emailSent = emailService.sendInvoiceEmail(completeInvoice, selectedClient);
                            
                            if (emailSent) {
                                System.out.println("Invoice email sent automatically to: " + selectedClient.getEmail());
                            } else {
                                System.out.println("Failed to send automatic invoice email to: " + selectedClient.getEmail());
                            }
                        }
                    } catch (Exception emailException) {
                        System.out.println("Error sending automatic invoice email: " + emailException.getMessage());
                        emailException.printStackTrace();
                    }
                }
                
                // Clear session data
                session.removeAttribute("selectedClient");
                session.removeAttribute("selectedBooks");
                session.removeAttribute("bookQuantities");
                
                // Check if print was requested
                String printAfterCreate = request.getParameter("printAfterCreate");
                if ("true".equals(printAfterCreate)) {
                    // Set success message and redirect with print parameter
                    String successMessage = "Invoice created successfully! Invoice ID: " + createdInvoiceId;
                    if (emailSent) {
                        successMessage += " Email sent to " + selectedClient.getFullName() + ".";
                    } else if (selectedClient != null && selectedClient.isSendMailAuto()) {
                        successMessage += " (Email sending failed - check email configuration)";
                    }
                    
                    session.setAttribute("successMessage", successMessage);
                    session.setAttribute("printInvoiceId", createdInvoiceId);
                    response.sendRedirect(request.getContextPath() + "/billing?printNewInvoice=true");
                } else {
                    // Normal redirect without print
                    String successMessage = "Invoice created successfully! Invoice ID: " + createdInvoiceId;
                    if (emailSent) {
                        successMessage += " Email sent to " + selectedClient.getFullName() + ".";
                    } else if (selectedClient != null && selectedClient.isSendMailAuto()) {
                        successMessage += " (Email sending failed - check email configuration)";
                    }
                    
                    session.setAttribute("successMessage", successMessage);
                    response.sendRedirect(request.getContextPath() + "/billing");
                }
            } else {
                System.out.println("Failed to create invoice: " + result.getMessage());
                request.setAttribute("errorMessage", "Failed to create invoice: " + result.getMessage());
                showCreateInvoiceForm(request, response);
            }
            
        } catch (Exception e) {
            System.out.println("Error creating invoice: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error creating invoice: " + e.getMessage());
            showCreateInvoiceForm(request, response);
        }
    }
}