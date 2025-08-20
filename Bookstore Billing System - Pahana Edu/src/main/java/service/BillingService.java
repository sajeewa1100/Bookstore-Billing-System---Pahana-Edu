package service;

import dao.BillingDAO;
import dao.ClientDAO;
import dao.BookDAO;
import model.BillingDTO;
import model.BillingItemDTO;
import model.ClientDTO;
import model.BookDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillingService {

    private static final Logger LOGGER = Logger.getLogger(BillingService.class.getName());
    
    private final BillingDAO billingDAO;
    private final ClientDAO clientDAO;
    private final BookDAO bookDAO;

    public BillingService() {
        this.billingDAO = new BillingDAO();
        this.clientDAO = new ClientDAO();
        this.bookDAO = new BookDAO();
    }

    /**
     * Create a new billing record
     */
    public boolean createBilling(BillingDTO billing) {
        try {
            // Validate billing data
            if (!validateBilling(billing)) {
                LOGGER.warning("BillingService: Invalid billing data provided");
                return false;
            }

            // Generate bill number if not provided
            if (billing.getBillNumber() == null || billing.getBillNumber().isEmpty()) {
                billing.generateBillNumber();
            }

            // Recalculate amounts to ensure accuracy
            billing.recalculateAmounts();

            // Create billing record
            boolean created = billingDAO.createBilling(billing);
            
            if (created) {
                LOGGER.info("BillingService: Billing created successfully - " + billing.getBillNumber());
                
                // Update client loyalty points if applicable
                updateClientLoyaltyPoints(billing);
                
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error creating billing", e);
            return false;
        }
    }

    /**
     * Get all billings
     */
    public List<BillingDTO> getAllBillings() {
        try {
            return billingDAO.getAllBillings();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error retrieving all billings", e);
            throw new RuntimeException("Error retrieving billings: " + e.getMessage(), e);
        }
    }

    /**
     * Get billing by ID with full details
     */
    public BillingDTO getBillingById(Long billingId) {
        try {
            if (billingId == null) {
                return null;
            }
            return billingDAO.getBillingById(billingId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error retrieving billing by ID: " + billingId, e);
            throw new RuntimeException("Error retrieving billing: " + e.getMessage(), e);
        }
    }

    /**
     * Update billing status
     */
    public boolean updateBillingStatus(Long billingId, String status) {
        try {
            if (billingId == null || status == null) {
                return false;
            }

            boolean updated = billingDAO.updateBillingStatus(billingId, status.toUpperCase());
            
            if (updated) {
                LOGGER.info("BillingService: Billing status updated - ID: " + billingId + ", Status: " + status);
            }

            return updated;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error updating billing status", e);
            return false;
        }
    }

    /**
     * Delete billing
     */
    public boolean deleteBilling(Long billingId) {
        try {
            if (billingId == null) {
                return false;
            }

            boolean deleted = billingDAO.deleteBilling(billingId);
            
            if (deleted) {
                LOGGER.info("BillingService: Billing deleted successfully - ID: " + billingId);
            }

            return deleted;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error deleting billing", e);
            return false;
        }
    }

    /**
     * Get billings by client ID
     */
    public List<BillingDTO> getBillingsByClientId(Long clientId) {
        try {
            if (clientId == null) {
                return List.of();
            }
            return billingDAO.getBillingsByClientId(clientId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error retrieving billings by client ID", e);
            throw new RuntimeException("Error retrieving client billings: " + e.getMessage(), e);
        }
    }

    /**
     * Get billings by status
     */
    public List<BillingDTO> getBillingsByStatus(String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return List.of();
            }
            return billingDAO.getBillingsByStatus(status.toUpperCase());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error retrieving billings by status", e);
            throw new RuntimeException("Error retrieving billings by status: " + e.getMessage(), e);
        }
    }

    /**
     * Search billings by bill number
     */
    public List<BillingDTO> searchBillingsByBillNumber(String billNumber) {
        try {
            if (billNumber == null || billNumber.trim().isEmpty()) {
                return List.of();
            }
            return billingDAO.searchBillingsByBillNumber(billNumber.trim());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error searching billings", e);
            throw new RuntimeException("Error searching billings: " + e.getMessage(), e);
        }
    }

    /**
     * Find client by account number
     */
    public ClientDTO findClientByAccountNumber(String accountNumber) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return null;
            }

            List<ClientDTO> clients = clientDAO.searchClients("id", accountNumber.trim());
            return clients.isEmpty() ? null : clients.get(0);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error finding client by account number", e);
            return null;
        }
    }

    /**
     * Find client by phone number
     */
    public ClientDTO findClientByPhone(String phone) {
        try {
            if (phone == null || phone.trim().isEmpty()) {
                return null;
            }

            List<ClientDTO> clients = clientDAO.searchClients("phone", phone.trim());
            return clients.isEmpty() ? null : clients.get(0);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error finding client by phone", e);
            return null;
        }
    }

    /**
     * Find book by ISBN
     */
    public BookDTO findBookByISBN(String isbn) {
        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                return null;
            }

            return bookDAO.searchBookByISBN(isbn.trim());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error finding book by ISBN", e);
            return null;
        }
    }

    /**
     * Get total billings count
     */
    public int getTotalBillingsCount() {
        try {
            return billingDAO.getTotalBillingsCount();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error getting total billings count", e);
            return 0;
        }
    }

    /**
     * Generate next bill number
     */
    public String generateNextBillNumber() {
        try {
            int sequence = billingDAO.getNextBillSequence();
            String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            return String.format("BILL-%s-%04d", today, sequence);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingService: Error generating bill number", e);
            return "BILL-" + System.currentTimeMillis();
        }
    }

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
                LOGGER.warning("BillingService: Invalid billing item found");
                return false;
            }

            // Check stock availability
            if (!item.hasSufficientStock()) {
                LOGGER.warning("BillingService: Insufficient stock for item: " + item.getBookTitle());
                return false;
            }
        }

        return true;
    }

    /**
     * Update client loyalty points based on billing total
     */
    private void updateClientLoyaltyPoints(BillingDTO billing) {
        try {
            if (billing.getClientId() == null || billing.getTotalAmount() == null) {
                return;
            }

            // Calculate points (1 point per 100 LKR spent)
            int pointsToAdd = billing.getTotalAmount().intValue() / 100;
            
            if (pointsToAdd > 0) {
                ClientDTO client = clientDAO.getClientById(billing.getClientId());
                if (client != null) {
                    int newPoints = client.getLoyaltyPointsAsInt() + pointsToAdd;
                    clientDAO.updateClientLoyaltyPointsAndTier(billing.getClientId(), newPoints, client.getTierId());
                    LOGGER.info("BillingService: Added " + pointsToAdd + " loyalty points to client " + client.getAccountNumber());
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillingService: Error updating client loyalty points", e);
            // Don't fail the billing creation for loyalty points update failure
        }
    }

    /**
     * Mark billing as completed
     */
    public boolean completeBilling(Long billingId) {
        return updateBillingStatus(billingId, "COMPLETED");
    }

    /**
     * Mark billing as cancelled
     */
    public boolean cancelBilling(Long billingId) {
        return updateBillingStatus(billingId, "CANCELLED");
    }

    /**
     * Check if billing can be modified
     */
    public boolean canModifyBilling(BillingDTO billing) {
        return billing != null && "PENDING".equals(billing.getStatus());
    }

    /**
     * Validate billing item stock
     */
    public boolean validateItemStock(BillingItemDTO item) {
        try {
            if (item.getBookId() == null) {
                return false;
            }

            BookDTO book = bookDAO.getBookById(item.getBookId());
            if (book == null) {
                return false;
            }

            item.setBook(book);
            return item.hasSufficientStock();

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "BillingService: Error validating item stock", e);
            return false;
        }
    }

    /**
     * Get available payment methods
     */
    public String[] getAvailablePaymentMethods() {
        return new String[]{"CASH", "CARD", "MOBILE"};
    }

    /**
     * Get available billing statuses
     */
    public String[] getAvailableStatuses() {
        return new String[]{"PENDING", "COMPLETED", "CANCELLED"};
    }
}