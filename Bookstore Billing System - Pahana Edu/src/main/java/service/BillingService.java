package service;

import dao.BillingDAO;
import dao.BookDAO;
import dao.ClientDAO;
import model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business logic service for billing operations
 * Handles all billing-related business rules and operations
 */
public class BillingService {

    private static final Logger LOGGER = Logger.getLogger(BillingService.class.getName());
    private static final BigDecimal TAX_RATE = new BigDecimal("0.08"); // 8% tax
    
    private final BillingDAO billingDAO;
    private final BookDAO bookDAO;
    private final ClientDAO clientDAO;
    
    public BillingService() throws SQLException {
        this.billingDAO = new BillingDAO();
        this.bookDAO = new BookDAO();
        this.clientDAO = new ClientDAO();
        LOGGER.info("BillingService: Service initialized successfully");
    }

    /**
     * Create a new billing record with validation and stock updates
     */
    public boolean createBilling(BillingDTO billing) throws SQLException {
        LOGGER.info("BillingService: Creating billing for client ID: " + billing.getClientId());
        
        // Validate billing
        if (!validateBilling(billing)) {
            LOGGER.warning("BillingService: Billing validation failed");
            return false;
        }
        
        // Generate bill number if not set
        if (billing.getBillNumber() == null || billing.getBillNumber().isEmpty()) {
            billing.setBillNumber(generateBillNumber());
        }
        
        // Validate and update stock
        for (BillingItemDTO item : billing.getItems()) {
            BookDTO book = bookDAO.getBookById(item.getBookId());
            if (book == null) {
                throw new SQLException("Book not found: " + item.getBookId());
            }
            
            if (book.getQuantity() < item.getQuantity()) {
                throw new SQLException("Insufficient stock for book: " + book.getTitle() + 
                    " (Available: " + book.getQuantity() + ", Required: " + item.getQuantity() + ")");
            }
        }
        
        // Calculate amounts
        calculateBillingAmounts(billing);
        
        // Create billing record
        boolean created = billingDAO.createBilling(billing);
        
        if (created) {
            // Update book stock
            updateBookStock(billing.getItems(), false); // Decrease stock
            
            // Update client loyalty points
            updateClientLoyaltyPoints(billing);
            
            LOGGER.info("BillingService: Billing created successfully - " + billing.getBillNumber());
        }
        
        return created;
    }

    /**
     * Get all billings with client information
     */
    public List<BillingDTO> getAllBillings() throws SQLException {
        LOGGER.info("BillingService: Retrieving all billings");
        return billingDAO.getAllBillings();
    }

    /**
     * Get billing by ID with complete details
     */
    public BillingDTO getBillingById(Long billingId) throws SQLException {
        LOGGER.info("BillingService: Retrieving billing by ID: " + billingId);
        return billingDAO.getBillingById(billingId);
    }

    /**
     * Update billing (only for PENDING status)
     */
    public boolean updateBilling(BillingDTO billing) throws SQLException {
        LOGGER.info("BillingService: Updating billing ID: " + billing.getId());
        
        // Get existing billing
        BillingDTO existingBilling = billingDAO.getBillingById(billing.getId());
        if (existingBilling == null) {
            LOGGER.warning("BillingService: Billing not found for update: " + billing.getId());
            return false;
        }
        
        // Only allow updates for PENDING billings
        if (!"PENDING".equals(existingBilling.getStatus())) {
            LOGGER.warning("BillingService: Cannot update non-pending billing: " + billing.getId());
            return false;
        }
        
        // Validate updated billing
        if (!validateBilling(billing)) {
            LOGGER.warning("BillingService: Updated billing validation failed");
            return false;
        }
        
        // Recalculate amounts
        calculateBillingAmounts(billing);
        billing.setUpdatedAt(LocalDateTime.now());
        
        // Update in database (simplified - in real app, handle item changes)
        return billingDAO.updateBillingStatus(billing.getId(), billing.getStatus());
    }

    /**
     * Complete a billing (change status to COMPLETED)
     */
    public boolean completeBilling(Long billingId) throws SQLException {
        LOGGER.info("BillingService: Completing billing ID: " + billingId);
        
        BillingDTO billing = billingDAO.getBillingById(billingId);
        if (billing == null) {
            LOGGER.warning("BillingService: Billing not found: " + billingId);
            return false;
        }
        
        if ("COMPLETED".equals(billing.getStatus())) {
            LOGGER.info("BillingService: Billing already completed: " + billingId);
            return true;
        }
        
        if ("CANCELLED".equals(billing.getStatus())) {
            LOGGER.warning("BillingService: Cannot complete cancelled billing: " + billingId);
            return false;
        }
        
        boolean updated = billingDAO.updateBillingStatus(billingId, "COMPLETED");
        
        if (updated) {
            LOGGER.info("BillingService: Billing completed successfully: " + billingId);
        }
        
        return updated;
    }

    /**
     * Cancel a billing (change status to CANCELLED and restore stock)
     */
    public boolean cancelBilling(Long billingId) throws SQLException {
        LOGGER.info("BillingService: Cancelling billing ID: " + billingId);
        
        BillingDTO billing = billingDAO.getBillingById(billingId);
        if (billing == null) {
            LOGGER.warning("BillingService: Billing not found: " + billingId);
            return false;
        }
        
        if ("CANCELLED".equals(billing.getStatus())) {
            LOGGER.info("BillingService: Billing already cancelled: " + billingId);
            return true;
        }
        
        if ("COMPLETED".equals(billing.getStatus())) {
            LOGGER.warning("BillingService: Cannot cancel completed billing: " + billingId);
            return false;
        }
        
        boolean updated = billingDAO.updateBillingStatus(billingId, "CANCELLED");
        
        if (updated) {
            // Restore book stock
            updateBookStock(billing.getItems(), true); // Increase stock
            
            // Reverse loyalty points (if any were added)
            reverseClientLoyaltyPoints(billing);
            
            LOGGER.info("BillingService: Billing cancelled successfully: " + billingId);
        }
        
        return updated;
    }

    /**
     * Delete a billing record
     */
    public boolean deleteBilling(Long billingId) throws SQLException {
        LOGGER.info("BillingService: Deleting billing ID: " + billingId);
        
        BillingDTO billing = billingDAO.getBillingById(billingId);
        if (billing == null) {
            LOGGER.warning("BillingService: Billing not found for deletion: " + billingId);
            return false;
        }
        
        // If billing was not cancelled, restore stock first
        if (!"CANCELLED".equals(billing.getStatus())) {
            updateBookStock(billing.getItems(), true); // Increase stock
            reverseClientLoyaltyPoints(billing);
        }
        
        boolean deleted = billingDAO.deleteBilling(billingId);
        
        if (deleted) {
            LOGGER.info("BillingService: Billing deleted successfully: " + billingId);
        }
        
        return deleted;
    }

    /**
     * Get billings by client ID
     */
    public List<BillingDTO> getBillingsByClientId(Long clientId) throws SQLException {
        LOGGER.info("BillingService: Retrieving billings for client ID: " + clientId);
        return billingDAO.getBillingsByClientId(clientId);
    }

    /**
     * Get billings by status
     */
    public List<BillingDTO> getBillingsByStatus(String status) throws SQLException {
        LOGGER.info("BillingService: Retrieving billings by status: " + status);
        return billingDAO.getBillingsByStatus(status);
    }

    /**
     * Search billings by bill number
     */
    public List<BillingDTO> searchBillingsByBillNumber(String billNumber) throws SQLException {
        LOGGER.info("BillingService: Searching billings by bill number: " + billNumber);
        return billingDAO.searchBillingsByBillNumber(billNumber);
    }

    /**
     * Get total billings count
     */
    public int getTotalBillingsCount() throws SQLException {
        return billingDAO.getTotalBillingsCount();
    }

    /**
     * Get billing statistics
     */
    public BillingStatsDTO getBillingStatistics() throws SQLException {
        LOGGER.info("BillingService: Calculating billing statistics");
        
        List<BillingDTO> allBillings = billingDAO.getAllBillings();
        
        int totalBillings = allBillings.size();
        int pendingCount = 0;
        int completedCount = 0;
        int cancelledCount = 0;
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        
        for (BillingDTO billing : allBillings) {
            switch (billing.getStatus()) {
                case "PENDING":
                    pendingCount++;
                    pendingAmount = pendingAmount.add(billing.getTotalAmount());
                    break;
                case "COMPLETED":
                    completedCount++;
                    totalRevenue = totalRevenue.add(billing.getTotalAmount());
                    break;
                case "CANCELLED":
                    cancelledCount++;
                    break;
            }
        }
        
        BillingStatsDTO stats = new BillingStatsDTO();
        stats.setTotalBillings(totalBillings);
        stats.setPendingCount(pendingCount);
        stats.setCompletedCount(completedCount);
        stats.setCancelledCount(cancelledCount);
        stats.setTotalRevenue(totalRevenue);
        stats.setPendingAmount(pendingAmount);
        
        return stats;
    }

    // Private helper methods
    
    /**
     * Validate billing data
     */
    private boolean validateBilling(BillingDTO billing) {
        if (billing == null) {
            LOGGER.warning("BillingService: Billing object is null");
            return false;
        }
        
        if (billing.getClientId() == null) {
            LOGGER.warning("BillingService: Client ID is required");
            return false;
        }
        
        if (billing.getItems() == null || billing.getItems().isEmpty()) {
            LOGGER.warning("BillingService: Billing must have at least one item");
            return false;
        }
        
        // Validate each item
        for (BillingItemDTO item : billing.getItems()) {
            if (!item.isValid()) {
                LOGGER.warning("BillingService: Invalid billing item: " + item);
                return false;
            }
        }
        
        return true;
    }

    /**
     * Calculate billing amounts (subtotal, discount, tax, total)
     */
    private void calculateBillingAmounts(BillingDTO billing) throws SQLException {
        LOGGER.info("BillingService: Calculating billing amounts");
        
        // Calculate subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (BillingItemDTO item : billing.getItems()) {
            item.calculateTotalPrice();
            subtotal = subtotal.add(item.getTotalPrice());
        }
        billing.setSubtotal(subtotal);
        
        // Apply tier-based discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (billing.getClient() != null && billing.getClient().getTier() != null) {
            BigDecimal discountRate = billing.getClient().getTier().getDiscountRate();
            if (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
                discountAmount = subtotal.multiply(discountRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }
        billing.setDiscountAmount(discountAmount);
        
        // Calculate tax on discounted amount
        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = taxableAmount.multiply(TAX_RATE).setScale(2, BigDecimal.ROUND_HALF_UP);
        billing.setTaxAmount(taxAmount);
        
        // Calculate total
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(taxAmount);
        billing.setTotalAmount(totalAmount);
        
        LOGGER.info("BillingService: Amounts calculated - Subtotal: " + subtotal + 
                   ", Discount: " + discountAmount + ", Tax: " + taxAmount + ", Total: " + totalAmount);
    }

    /**
     * Update book stock quantities
     */
    private void updateBookStock(List<BillingItemDTO> items, boolean increase) throws SQLException {
        LOGGER.info("BillingService: Updating book stock - Increase: " + increase);
        
        for (BillingItemDTO item : items) {
            BookDTO book = bookDAO.getBookById(item.getBookId());
            if (book != null) {
                int newQuantity = increase ? 
                    book.getQuantity() + item.getQuantity() : 
                    book.getQuantity() - item.getQuantity();
                
                if (newQuantity < 0) {
                    throw new SQLException("Cannot reduce stock below zero for book: " + book.getTitle());
                }
                
                book.setQuantity(newQuantity);
                bookDAO.updateBook(book);
                
                LOGGER.info("BillingService: Updated stock for book " + book.getTitle() + 
                           " - New quantity: " + newQuantity);
            }
        }
    }

    /**
     * Update client loyalty points based on purchase amount
     */
    private void updateClientLoyaltyPoints(BillingDTO billing) throws SQLException {
        if (billing.getClientId() == null || billing.getTotalAmount() == null) {
            return;
        }
        
        try {
            ClientDTO client = clientDAO.getClientById(billing.getClientId());
            if (client != null) {
                // Award 1 point per Rs. 100 spent
                int pointsToAdd = billing.getTotalAmount().divide(new BigDecimal("100"), 0, BigDecimal.ROUND_DOWN).intValue();
                
                if (pointsToAdd > 0) {
                    int newPoints = client.getLoyaltyPointsAsInt() + pointsToAdd;
                    // Fix: Use Integer instead of BigDecimal
                    client.setLoyaltyPoints(newPoints);
                    clientDAO.updateClient(client);
                    
                    LOGGER.info("BillingService: Added " + pointsToAdd + " loyalty points to client " + client.getFullName());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "BillingService: Failed to update loyalty points", e);
            // Don't fail the billing process for loyalty point errors
        }
    }

    /**
     * Reverse client loyalty points (for cancelled billings)
     */
    private void reverseClientLoyaltyPoints(BillingDTO billing) throws SQLException {
        if (billing.getClientId() == null || billing.getTotalAmount() == null) {
            return;
        }
        
        try {
            ClientDTO client = clientDAO.getClientById(billing.getClientId());
            if (client != null) {
                // Subtract the points that were originally awarded
                int pointsToSubtract = billing.getTotalAmount().divide(new BigDecimal("100"), 0, BigDecimal.ROUND_DOWN).intValue();
                
                if (pointsToSubtract > 0) {
                    int newPoints = Math.max(0, client.getLoyaltyPointsAsInt() - pointsToSubtract);
                    // Fix: Use Integer instead of BigDecimal
                    client.setLoyaltyPoints(newPoints);
                    clientDAO.updateClient(client);
                    
                    LOGGER.info("BillingService: Subtracted " + pointsToSubtract + " loyalty points from client " + client.getFullName());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "BillingService: Failed to reverse loyalty points", e);
            // Don't fail the process for loyalty point errors
        }
    }

    /**
     * Generate unique bill number
     */
    private String generateBillNumber() throws SQLException {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int sequence = billingDAO.getNextBillSequence();
        return String.format("BILL-%s-%04d", today, sequence);
    }

    /**
     * Inner class for billing statistics
     */
    public static class BillingStatsDTO {
        private int totalBillings;
        private int pendingCount;
        private int completedCount;
        private int cancelledCount;
        private BigDecimal totalRevenue;
        private BigDecimal pendingAmount;
        
        // Getters and Setters
        public int getTotalBillings() { return totalBillings; }
        public void setTotalBillings(int totalBillings) { this.totalBillings = totalBillings; }
        
        public int getPendingCount() { return pendingCount; }
        public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }
        
        public int getCompletedCount() { return completedCount; }
        public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
        
        public int getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(int cancelledCount) { this.cancelledCount = cancelledCount; }
        
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public BigDecimal getPendingAmount() { return pendingAmount; }
        public void setPendingAmount(BigDecimal pendingAmount) { this.pendingAmount = pendingAmount; }
    }
}