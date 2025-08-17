package service;

import dao.BillingDAO;
import dao.BookDAO;
import model.BillingDTO;
import model.BillItemDTO;
import model.BookDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service layer for Billing operations
 * Contains business logic for billing transactions
 */
public class BillingService {
    
    private static final Logger LOGGER = Logger.getLogger(BillingService.class.getName());
    
    // DAO instances
    private BillingDAO billingDAO;
    private BookDAO bookDAO;
    
    /**
     * Constructor
     */
    public BillingService() {
        this.billingDAO = new BillingDAO();
        this.bookDAO = new BookDAO();
    }
    
    /**
     * Create a new bill
     */
    public boolean createBill(BillingDTO bill) {
        try {
            // Validate bill data
            String validationError = validateBill(bill);
            if (validationError != null) {
                LOGGER.warning("Bill validation failed: " + validationError);
                throw new IllegalArgumentException(validationError);
            }
            
            // Check if bill number already exists
            if (billingDAO.billNumberExists(bill.getBillNumber())) {
                bill.generateBillNumber(); // Generate new bill number
            }
            
            // Validate book availability and quantities
            if (!validateBookAvailability(bill)) {
                throw new IllegalArgumentException("Some books are not available in requested quantities");
            }
            
            // Calculate totals
            bill.calculateTotals();
            
            boolean result = billingDAO.createBill(bill);
            
            if (result) {
                LOGGER.info("Bill created successfully: " + bill.getBillNumber());
            } else {
                LOGGER.warning("Failed to create bill: " + bill.getBillNumber());
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while creating bill: " + e.getMessage(), e);
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to create bill due to system error", e);
        }
    }
    
    /**
     * Get all bills
     */
    public List<BillingDTO> getAllBills() {
        try {
            List<BillingDTO> bills = billingDAO.getAllBills();
            LOGGER.info("Retrieved " + bills.size() + " bills");
            return bills;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while retrieving bills: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve bills due to system error", e);
        }
    }
    
    /**
     * Get bill by ID
     */
    public BillingDTO getBillById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid bill ID");
        }
        
        try {
            BillingDTO bill = billingDAO.getBillById(id);
            
            if (bill != null) {
                LOGGER.info("Bill found: " + bill.getBillNumber());
            } else {
                LOGGER.info("No bill found with ID: " + id);
            }
            
            return bill;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while retrieving bill: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve bill due to system error", e);
        }
    }
    
    /**
     * Get bill by bill number
     */
    public BillingDTO getBillByNumber(String billNumber) {
        if (billNumber == null || billNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill number is required");
        }
        
        try {
            return billingDAO.getBillByNumber(billNumber.trim());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while retrieving bill by number: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve bill due to system error", e);
        }
    }
    
    /**
     * Complete a bill (mark as COMPLETED)
     */
    public boolean completeBill(Long billId) {
        if (billId == null || billId <= 0) {
            throw new IllegalArgumentException("Invalid bill ID");
        }
        
        try {
            // Check if bill exists and is in PENDING status
            BillingDTO bill = billingDAO.getBillById(billId);
            if (bill == null) {
                throw new IllegalArgumentException("Bill not found with ID: " + billId);
            }
            
            if (!"PENDING".equals(bill.getStatus())) {
                throw new IllegalArgumentException("Only pending bills can be completed");
            }
            
            boolean result = billingDAO.updateBillStatus(billId, "COMPLETED");
            
            if (result) {
                LOGGER.info("Bill completed successfully: " + bill.getBillNumber());
            } else {
                LOGGER.warning("Failed to complete bill with ID: " + billId);
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while completing bill: " + e.getMessage(), e);
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to complete bill due to system error", e);
        }
    }
    
    /**
     * Cancel a bill (mark as CANCELLED)
     */
    public boolean cancelBill(Long billId) {
        if (billId == null || billId <= 0) {
            throw new IllegalArgumentException("Invalid bill ID");
        }
        
        try {
            // Check if bill exists and can be cancelled
            BillingDTO bill = billingDAO.getBillById(billId);
            if (bill == null) {
                throw new IllegalArgumentException("Bill not found with ID: " + billId);
            }
            
            if ("CANCELLED".equals(bill.getStatus())) {
                throw new IllegalArgumentException("Bill is already cancelled");
            }
            
            boolean result = billingDAO.updateBillStatus(billId, "CANCELLED");
            
            if (result) {
                LOGGER.info("Bill cancelled successfully: " + bill.getBillNumber());
                
                // TODO: In future, restore book quantities when cancelling a bill
                // restoreBookQuantities(bill);
            } else {
                LOGGER.warning("Failed to cancel bill with ID: " + billId);
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while cancelling bill: " + e.getMessage(), e);
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to cancel bill due to system error", e);
        }
    }
    
    /**
     * Delete a bill
     */
    public boolean deleteBill(Long billId) {
        if (billId == null || billId <= 0) {
            throw new IllegalArgumentException("Invalid bill ID for deletion");
        }
        
        try {
            // Check if bill exists
            BillingDTO existingBill = billingDAO.getBillById(billId);
            if (existingBill == null) {
                throw new IllegalArgumentException("Bill not found with ID: " + billId);
            }
            
            // Only allow deletion of cancelled or pending bills
            if ("COMPLETED".equals(existingBill.getStatus())) {
                throw new IllegalArgumentException("Cannot delete completed bills");
            }
            
            boolean result = billingDAO.deleteBill(billId);
            
            if (result) {
                LOGGER.info("Bill deleted successfully with ID: " + billId);
            } else {
                LOGGER.warning("Failed to delete bill with ID: " + billId);
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while deleting bill: " + e.getMessage(), e);
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to delete bill due to system error", e);
        }
    }
    
    /**
     * Search bills by client name
     */
    public List<BillingDTO> searchBillsByClient(String clientName) {
        if (clientName == null || clientName.trim().isEmpty()) {
            return getAllBills();
        }
        
        try {
            List<BillingDTO> bills = billingDAO.searchBillsByClient(clientName.trim());
            LOGGER.info("Search returned " + bills.size() + " bills for client: " + clientName);
            return bills;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while searching bills by client: " + e.getMessage(), e);
            throw new RuntimeException("Failed to search bills due to system error", e);
        }
    }
    
    /**
     * Get bills by status
     */
    public List<BillingDTO> getBillsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return getAllBills();
        }
        
        try {
            List<BillingDTO> bills = billingDAO.getBillsByStatus(status.trim().toUpperCase());
            LOGGER.info("Retrieved " + bills.size() + " bills with status: " + status);
            return bills;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while retrieving bills by status: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve bills due to system error", e);
        }
    }
    
    /**
     * Get bills by date range
     */
    public List<BillingDTO> getBillsByDateRange(String fromDate, String toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("From date and to date are required");
        }
        
        try {
            List<BillingDTO> bills = billingDAO.getBillsByDateRange(fromDate, toDate);
            LOGGER.info("Retrieved " + bills.size() + " bills for date range: " + fromDate + " to " + toDate);
            return bills;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while retrieving bills by date range: " + e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve bills due to system error", e);
        }
    }
    
    /**
     * Get billing statistics
     */
    public BillingStatistics getBillingStatistics() {
        try {
            int totalBills = billingDAO.getTotalBillsCount();
            BigDecimal totalSales = billingDAO.getTotalSalesAmount();
            
            List<BillingDTO> pendingBills = billingDAO.getBillsByStatus("PENDING");
            List<BillingDTO> completedBills = billingDAO.getBillsByStatus("COMPLETED");
            List<BillingDTO> cancelledBills = billingDAO.getBillsByStatus("CANCELLED");
            
            return new BillingStatistics(totalBills, totalSales, 
                                       pendingBills.size(), completedBills.size(), cancelledBills.size());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while getting billing statistics: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get billing statistics due to system error", e);
        }
    }
    
    /**
     * Generate printable bill HTML
     */
    public String generatePrintableBill(Long billId) {
        BillingDTO bill = getBillById(billId);
        if (bill == null) {
            throw new IllegalArgumentException("Bill not found");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<div class='printable-bill'>");
        
        // Header
        html.append("<div class='bill-header'>");
        html.append("<h1>Pahana Bookstore</h1>");
        html.append("<p>123 Main Street, Colombo, Sri Lanka</p>");
        html.append("<p>Phone: +94 11 234 5678 | Email: info@pahanabookstore.lk</p>");
        html.append("<hr>");
        html.append("</div>");
        
        // Bill Info
        html.append("<div class='bill-info'>");
        html.append("<div class='bill-details'>");
        html.append("<h3>Bill #: ").append(bill.getBillNumber()).append("</h3>");
        html.append("<p><strong>Date:</strong> ").append(bill.getFormattedBillDate()).append("</p>");
        html.append("<p><strong>Status:</strong> ").append(bill.getStatus()).append("</p>");
        if (bill.getPaymentMethod() != null) {
            html.append("<p><strong>Payment:</strong> ").append(bill.getPaymentMethod()).append("</p>");
        }
        html.append("</div>");
        
        // Client Info
        html.append("<div class='client-details'>");
        html.append("<h4>Bill To:</h4>");
        html.append("<p><strong>").append(bill.getClientName()).append("</strong></p>");
        if (bill.getClientEmail() != null) {
            html.append("<p>").append(bill.getClientEmail()).append("</p>");
        }
        if (bill.getClientPhone() != null) {
            html.append("<p>").append(bill.getClientPhone()).append("</p>");
        }
        html.append("</div>");
        html.append("</div>");
        
        // Items Table
        html.append("<div class='bill-items'>");
        html.append("<table class='items-table'>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>Item</th>");
        html.append("<th>Qty</th>");
        html.append("<th>Price</th>");
        html.append("<th>Total</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");
        
        for (BillItemDTO item : bill.getItems()) {
            html.append("<tr>");
            html.append("<td>");
            html.append("<strong>").append(item.getBookTitle()).append("</strong><br>");
            html.append("<small>by ").append(item.getBookAuthor()).append("</small>");
            if (item.getBookIsbn() != null && !item.getBookIsbn().isEmpty()) {
                html.append("<br><small>ISBN: ").append(item.getBookIsbn()).append("</small>");
            }
            html.append("</td>");
            html.append("<td>").append(item.getQuantity()).append("</td>");
            html.append("<td>Rs. ").append(item.getUnitPrice()).append("</td>");
            html.append("<td>Rs. ").append(item.getTotal()).append("</td>");
            html.append("</tr>");
        }
        
        html.append("</tbody>");
        html.append("</table>");
        html.append("</div>");
        
        // Totals
        html.append("<div class='bill-totals'>");
        html.append("<table class='totals-table'>");
        html.append("<tr><td>Subtotal:</td><td>Rs. ").append(bill.getSubtotal()).append("</td></tr>");
        
        if (bill.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            html.append("<tr><td>Discount:</td><td>-Rs. ").append(bill.getDiscountAmount()).append("</td></tr>");
        }
        
        html.append("<tr><td>Tax (8%):</td><td>Rs. ").append(bill.getTaxAmount()).append("</td></tr>");
        html.append("<tr class='total-row'><td><strong>Total:</strong></td><td><strong>Rs. ").append(bill.getTotalAmount()).append("</strong></td></tr>");
        html.append("</table>");
        html.append("</div>");
        
        // Footer
        html.append("<div class='bill-footer'>");
        if (bill.getNotes() != null && !bill.getNotes().isEmpty()) {
            html.append("<p><strong>Notes:</strong> ").append(bill.getNotes()).append("</p>");
        }
        html.append("<hr>");
        html.append("<p><center>Thank you for your business!</center></p>");
        html.append("</div>");
        
        html.append("</div>");
        
        return html.toString();
    }
    
    /**
     * Validate bill data
     */
    private String validateBill(BillingDTO bill) {
        if (bill == null) {
            return "Bill data is required";
        }
        
        if (bill.getClientId() == null || bill.getClientId() <= 0) {
            return "Valid client is required";
        }
        
        if (bill.getClientName() == null || bill.getClientName().trim().isEmpty()) {
            return "Client name is required";
        }
        
        if (bill.getItems() == null || bill.getItems().isEmpty()) {
            return "At least one item is required";
        }
        
        // Validate each item
        for (BillItemDTO item : bill.getItems()) {
            if (item.getBookId() == null || item.getBookId() <= 0) {
                return "Invalid book in bill items";
            }
            
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return "Invalid quantity for item: " + item.getBookTitle();
            }
            
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return "Invalid price for item: " + item.getBookTitle();
            }
        }
        
        return null; // No validation errors
    }
    
    /**
     * Validate book availability
     */
    private boolean validateBookAvailability(BillingDTO bill) {
        for (BillItemDTO item : bill.getItems()) {
            BookDTO book = bookDAO.getBookById(item.getBookId());
            if (book == null) {
                LOGGER.warning("Book not found: " + item.getBookId());
                return false;
            }
            
            if (book.getQuantity() < item.getQuantity()) {
                LOGGER.warning("Insufficient stock for book: " + book.getTitle() + 
                             " (Available: " + book.getQuantity() + ", Required: " + item.getQuantity() + ")");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Inner class for billing statistics
     */
    public static class BillingStatistics {
        private int totalBills;
        private BigDecimal totalSales;
        private int pendingBills;
        private int completedBills;
        private int cancelledBills;
        
        public BillingStatistics(int totalBills, BigDecimal totalSales, 
                               int pendingBills, int completedBills, int cancelledBills) {
            this.totalBills = totalBills;
            this.totalSales = totalSales;
            this.pendingBills = pendingBills;
            this.completedBills = completedBills;
            this.cancelledBills = cancelledBills;
        }
        
        // Getters
        public int getTotalBills() { return totalBills; }
        public BigDecimal getTotalSales() { return totalSales; }
        public int getPendingBills() { return pendingBills; }
        public int getCompletedBills() { return completedBills; }
        public int getCancelledBills() { return cancelledBills; }
    }
}