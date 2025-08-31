package service;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;

public class BillingService {
    private ClientDAO clientDAO;
    private BookDAO bookDAO;
    private InvoiceDAO invoiceDAO;
    private InvoiceItemDAO invoiceItemDAO;
    private LoyaltySettingsService loyaltySettingsService;
    
    public BillingService() {
        this.clientDAO = new ClientDAO();
        this.bookDAO = new BookDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.invoiceItemDAO = new InvoiceItemDAO();
        this.loyaltySettingsService = new LoyaltySettingsService();
    }
    
    // ===== CLIENT METHODS =====
    
    /**
     * Search clients by name, phone, or account number
     */
    public List<ClientDTO> searchClients(String searchTerm) {
        System.out.println("BillingService.searchClients called with: '" + searchTerm + "'");
        try {
            List<ClientDTO> results = clientDAO.searchClients(searchTerm);
            System.out.println("DAO returned: " + results.size() + " clients");
            for (ClientDTO client : results) {
                System.out.println("  - " + client.getFullName() + " (" + client.getPhone() + ") - Tier: " + client.getTierLevel());
            }
            return results;
        } catch (Exception e) {
            System.out.println("Error in searchClients: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get client by ID
     */
    public ClientDTO getClientById(int clientId) {
        System.out.println("BillingService.getClientById called with ID: " + clientId);
        try {
            ClientDTO client = clientDAO.findById(clientId);
            if (client != null) {
                System.out.println("Found client: " + client.getFullName());
            } else {
                System.out.println("No client found with ID: " + clientId);
            }
            return client;
        } catch (Exception e) {
            System.out.println("Error in getClientById: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get client by phone number
     */
    public ClientDTO getClientByPhone(String phone) {
        System.out.println("BillingService.getClientByPhone called with: '" + phone + "'");
        try {
            ClientDTO client = clientDAO.findByPhone(phone);
            if (client != null) {
                System.out.println("Found client by phone: " + client.getFullName());
            } else {
                System.out.println("No client found with phone: " + phone);
            }
            return client;
        } catch (Exception e) {
            System.out.println("Error in getClientByPhone: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get all clients
     */
    public List<ClientDTO> getAllClients() {
        try {
            return clientDAO.getAllClients();
        } catch (Exception e) {
            System.out.println("Error in getAllClients: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // ===== BOOK METHODS =====
    
    /**
     * Search books by title, author, ISBN
     */
    public List<BookDTO> searchBooks(String searchTerm) {
        System.out.println("BillingService.searchBooks called with: '" + searchTerm + "'");
        try {
            List<BookDTO> results = bookDAO.searchBooks(searchTerm);
            System.out.println("DAO returned: " + results.size() + " books");
            for (BookDTO book : results) {
                System.out.println("  - " + book.getTitle() + " by " + book.getAuthor() + " - Rs. " + book.getPrice());
            }
            return results;
        } catch (Exception e) {
            System.out.println("Error in searchBooks: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get book by ISBN
     */
    public BookDTO getBookByISBN(String isbn) {
        System.out.println("BillingService.getBookByISBN called with: '" + isbn + "'");
        try {
            BookDTO book = bookDAO.findByISBN(isbn);
            if (book != null) {
                System.out.println("Found book by ISBN: " + book.getTitle());
            } else {
                System.out.println("No book found with ISBN: " + isbn);
            }
            return book;
        } catch (Exception e) {
            System.out.println("Error in getBookByISBN: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get book by ID
     */
    public BookDTO getBookById(int bookId) {
        System.out.println("BillingService.getBookById called with ID: " + bookId);
        try {
            BookDTO book = bookDAO.findById(bookId);
            if (book != null) {
                System.out.println("Found book: " + book.getTitle());
            } else {
                System.out.println("No book found with ID: " + bookId);
            }
            return book;
        } catch (Exception e) {
            System.out.println("Error in getBookById: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get all books
     */
    public List<BookDTO> getAllBooks() {
        try {
            List<BookDTO> books = bookDAO.getAllBooks();
            System.out.println("BillingService.getAllBooks returned: " + books.size() + " books");
            return books;
        } catch (Exception e) {
            System.out.println("Error in getAllBooks: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // ===== INVOICE CALCULATION =====
    
    /**
     * Calculate invoice totals with automatic discounts and points
     */
    public InvoiceCalculationResult calculateInvoice(List<InvoiceItemDTO> items, ClientDTO client) {
        System.out.println("BillingService.calculateInvoice called");
        
        InvoiceCalculationResult result = new InvoiceCalculationResult();
        
        // Step 1: Calculate subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceItemDTO item : items) {
            subtotal = subtotal.add(item.getTotalPrice());
        }
        result.setSubtotal(subtotal);
        System.out.println("Subtotal: Rs. " + subtotal);
        
        // Step 2: Apply loyalty discount based on client's CURRENT tier
        BigDecimal loyaltyDiscount = BigDecimal.ZERO;
        if (client != null) {
            BigDecimal discountRate = loyaltySettingsService.getDiscountForTier(client.getTierLevel());
            loyaltyDiscount = subtotal.multiply(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            System.out.println("Client tier: " + client.getTierLevel() + ", Discount rate: " + discountRate + "%");
        }
        result.setLoyaltyDiscount(loyaltyDiscount);
        System.out.println("Loyalty discount: Rs. " + loyaltyDiscount);
        
        // Step 3: Calculate total amount after discount
        BigDecimal totalAmount = subtotal.subtract(loyaltyDiscount);
        result.setTotalAmount(totalAmount);
        System.out.println("Total amount: Rs. " + totalAmount);
        
        // Step 4: Calculate loyalty points earned
        int pointsEarned = 0;
        if (client != null) {
            pointsEarned = loyaltySettingsService.calculatePointsEarned(totalAmount);
        }
        result.setLoyaltyPointsEarned(pointsEarned);
        System.out.println("Points earned: " + pointsEarned);
        
        return result;
    }
    
    // ===== INVOICE CRUD OPERATIONS =====
    
    /**
     * Create invoice with automatic point accumulation and tier update
     */
    public int createInvoice(InvoiceDTO invoice) {
        System.out.println("BillingService.createInvoice called");
        
        try {
            // Generate invoice number
            String invoiceNumber = invoiceDAO.generateNextInvoiceNumber();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setInvoiceDate(new Date(System.currentTimeMillis()));
            
            System.out.println("Creating invoice: " + invoiceNumber);
            
            // Create invoice record
            int invoiceId = invoiceDAO.createInvoice(invoice);
            if (invoiceId > 0) {
                System.out.println("Invoice created with ID: " + invoiceId);
                
                // Create invoice items
                for (InvoiceItemDTO item : invoice.getItems()) {
                    item.setInvoiceId(invoiceId);
                    invoiceItemDAO.createInvoiceItem(item);
                }
                System.out.println("Created " + invoice.getItems().size() + " invoice items");
                
                // AUTOMATIC POINT ACCUMULATION AND TIER UPDATE
                if (invoice.getClientId() > 0 && invoice.getLoyaltyPointsEarned() > 0) {
                    automaticallyUpdateClientLoyaltyStatus(invoice.getClientId(), invoice.getLoyaltyPointsEarned(), true);
                }
                
                System.out.println("Invoice creation completed successfully");
            } else {
                System.out.println("Failed to create invoice");
            }
            
            return invoiceId;
            
        } catch (Exception e) {
            System.out.println("Error creating invoice: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get invoice by ID with full details
     */
    public InvoiceDTO getInvoiceById(int invoiceId) {
        try {
            InvoiceDTO invoice = invoiceDAO.findById(invoiceId);
            if (invoice != null) {
                // Load client details
                if (invoice.getClientId() > 0) {
                    ClientDTO client = clientDAO.findById(invoice.getClientId());
                    invoice.setClient(client);
                }
                
                // Load invoice items
                List<InvoiceItemDTO> items = invoiceItemDAO.getInvoiceItems(invoiceId);
                for (InvoiceItemDTO item : items) {
                    BookDTO book = bookDAO.findById(item.getBookId());
                    item.setBook(book);
                }
                invoice.setItems(items);
            }
            return invoice;
        } catch (Exception e) {
            System.out.println("Error getting invoice: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Get all invoices
     */
    public List<InvoiceDTO> getAllInvoices() {
        try {
            List<InvoiceDTO> invoices = invoiceDAO.getAllInvoices();
            
            // Load client details for each invoice
            for (InvoiceDTO invoice : invoices) {
                if (invoice.getClientId() > 0) {
                    ClientDTO client = clientDAO.findById(invoice.getClientId());
                    invoice.setClient(client);
                }
            }
            
            return invoices;
        } catch (Exception e) {
            System.out.println("Error getting all invoices: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Delete invoice with automatic point reversal
     */
    public boolean deleteInvoice(int invoiceId) {
        System.out.println("BillingService.deleteInvoice called for ID: " + invoiceId);
        
        try {
            // Get invoice details before deletion
            InvoiceDTO invoice = getInvoiceById(invoiceId);
            if (invoice == null) {
                System.out.println("Invoice not found for deletion");
                return false;
            }
            
            // AUTOMATICALLY reverse loyalty points and recalculate tier
            if (invoice.getClientId() > 0 && invoice.getLoyaltyPointsEarned() > 0) {
                automaticallyUpdateClientLoyaltyStatus(invoice.getClientId(), invoice.getLoyaltyPointsEarned(), false);
            }
            
            // Delete invoice items and invoice
            invoiceItemDAO.deleteInvoiceItems(invoiceId);
            boolean deleted = invoiceDAO.deleteInvoice(invoiceId);
            
            if (deleted) {
                System.out.println("Invoice deleted successfully");
            } else {
                System.out.println("Failed to delete invoice");
            }
            
            return deleted;
            
        } catch (Exception e) {
            System.out.println("Error deleting invoice: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
    /**
     * CORE METHOD: AUTOMATICALLY UPDATE CLIENT LOYALTY STATUS
     */
    private void automaticallyUpdateClientLoyaltyStatus(int clientId, int points, boolean isAddition) {
        System.out.println("Updating loyalty status for client " + clientId + ": " + 
                         (isAddition ? "adding" : "removing") + " " + points + " points");
        
        try {
            ClientDTO client = clientDAO.findById(clientId);
            if (client == null) {
                System.out.println("Client not found for loyalty update");
                return;
            }
            
            // Step 1: Calculate new points total
            int currentPoints = client.getLoyaltyPoints();
            int newPoints;
            
            if (isAddition) {
                newPoints = currentPoints + points;
            } else {
                newPoints = Math.max(0, currentPoints - points); // Never go below 0
            }
            
            System.out.println("Points update: " + currentPoints + " -> " + newPoints);
            
            // Step 2: Update points in database
            clientDAO.updateLoyaltyPoints(clientId, newPoints);
            
            // Step 3: AUTOMATICALLY recalculate tier based on new points
            String currentTier = client.getTierLevel();
            String newTier = loyaltySettingsService.calculateClientTier(newPoints);
            
            System.out.println("Tier calculation: " + currentTier + " -> " + newTier);
            
            // Step 4: Update tier if it changed
            if (!newTier.equals(currentTier)) {
                client.setTierLevel(newTier);
                client.setLoyaltyPoints(newPoints); // Update points in object too
                clientDAO.updateClient(client); // Update tier in database
                
                System.out.println("Client " + client.getFullName() + " tier automatically updated: " + 
                                 currentTier + " → " + newTier + " (Points: " + currentPoints + " → " + newPoints + ")");
            }
            
        } catch (Exception e) {
            System.out.println("Error updating client loyalty status: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Search invoices by ID or client phone number
     */
    public List<InvoiceDTO> searchInvoices(String searchTerm, String searchType) {
        System.out.println("BillingService.searchInvoices called with: '" + searchTerm + "', type: '" + searchType + "'");
        
        try {
            List<InvoiceDTO> invoices = invoiceDAO.searchInvoices(searchTerm, searchType);
            
            // Load client details for each invoice
            for (InvoiceDTO invoice : invoices) {
                if (invoice.getClientId() > 0) {
                    ClientDTO client = clientDAO.findById(invoice.getClientId());
                    invoice.setClient(client);
                }
                
                // Load invoice items for search results
                List<InvoiceItemDTO> items = invoiceItemDAO.getInvoiceItems(invoice.getId());
                for (InvoiceItemDTO item : items) {
                    BookDTO book = bookDAO.findById(item.getBookId());
                    item.setBook(book);
                }
                invoice.setItems(items);
            }
            
            System.out.println("Search returned: " + invoices.size() + " invoices");
            return invoices;
            
        } catch (Exception e) {
            System.out.println("Error in searchInvoices: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get invoices with pagination
     */
    public List<InvoiceDTO> getInvoicesWithPagination(int limit, int offset) {
        try {
            List<InvoiceDTO> invoices = invoiceDAO.getAllInvoicesWithPagination(limit, offset);
            
            // Load client details for each invoice
            for (InvoiceDTO invoice : invoices) {
                if (invoice.getClientId() > 0) {
                    ClientDTO client = clientDAO.findById(invoice.getClientId());
                    invoice.setClient(client);
                }
            }
            
            return invoices;
        } catch (Exception e) {
            System.out.println("Error in getInvoicesWithPagination: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get total invoice count for pagination
     */
    public int getTotalInvoiceCount() {
        try {
            return invoiceDAO.getAllInvoices().size(); // Simple implementation
        } catch (Exception e) {
            System.out.println("Error getting total invoice count: " + e.getMessage());
            return 0;
        }
    }
    
    // ===== INNER CLASS FOR CALCULATION RESULTS =====
    
    public static class InvoiceCalculationResult {
        private BigDecimal subtotal;
        private BigDecimal loyaltyDiscount;
        private BigDecimal totalAmount;
        private int loyaltyPointsEarned;
        
        // Getters and Setters
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
        
        public BigDecimal getLoyaltyDiscount() { return loyaltyDiscount; }
        public void setLoyaltyDiscount(BigDecimal loyaltyDiscount) { this.loyaltyDiscount = loyaltyDiscount; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public int getLoyaltyPointsEarned() { return loyaltyPointsEarned; }
        public void setLoyaltyPointsEarned(int loyaltyPointsEarned) { this.loyaltyPointsEarned = loyaltyPointsEarned; }
    }
}