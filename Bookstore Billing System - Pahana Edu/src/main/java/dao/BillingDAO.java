package dao;

import model.BillingDTO;
import model.BillingItemDTO;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillingDAO {

    private static final Logger LOGGER = Logger.getLogger(BillingDAO.class.getName());

    /**
     * Create a new billing record
     */
    public boolean createBilling(BillingDTO billing) throws SQLException {
        String billingSql = """
            INSERT INTO billings (bill_number, client_id, subtotal, discount_amount, 
                                tax_amount, total_amount, status, payment_method, notes) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String itemSql = """
            INSERT INTO billing_items (billing_id, book_id, book_title, book_author, 
                                     book_isbn, unit_price, quantity, total_price) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Insert billing record
            try (PreparedStatement billStmt = conn.prepareStatement(billingSql, Statement.RETURN_GENERATED_KEYS)) {
                billStmt.setString(1, billing.getBillNumber());
                billStmt.setLong(2, billing.getClientId());
                billStmt.setBigDecimal(3, billing.getSubtotal());
                billStmt.setBigDecimal(4, billing.getDiscountAmount());
                billStmt.setBigDecimal(5, billing.getTaxAmount());
                billStmt.setBigDecimal(6, billing.getTotalAmount());
                billStmt.setString(7, billing.getStatus());
                billStmt.setString(8, billing.getPaymentMethod());
                billStmt.setString(9, billing.getNotes());

                int billRowsAffected = billStmt.executeUpdate();
                if (billRowsAffected == 0) {
                    conn.rollback();
                    return false;
                }

                // Get generated billing ID
                try (ResultSet generatedKeys = billStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        billing.setId(generatedKeys.getLong(1));
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Insert billing items
            if (billing.getItems() != null && !billing.getItems().isEmpty()) {
                try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                    for (BillingItemDTO item : billing.getItems()) {
                        itemStmt.setLong(1, billing.getId());
                        itemStmt.setInt(2, item.getBookId());
                        itemStmt.setString(3, item.getBookTitle());
                        itemStmt.setString(4, item.getBookAuthor());
                        itemStmt.setString(5, item.getBookIsbn());
                        itemStmt.setBigDecimal(6, item.getUnitPrice());
                        itemStmt.setInt(7, item.getQuantity());
                        itemStmt.setBigDecimal(8, item.getTotalPrice());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }
            }

            conn.commit();
            LOGGER.info("BillingDAO: Billing created successfully - " + billing.getBillNumber());
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            LOGGER.log(Level.SEVERE, "BillingDAO: Error creating billing", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Get all billings with client information
     */
    public List<BillingDTO> getAllBillings() throws SQLException {
        List<BillingDTO> billings = new ArrayList<>();
        String sql = """
            SELECT b.*, c.account_number, c.first_name, c.last_name, c.email, c.phone
            FROM billings b 
            LEFT JOIN clients c ON b.client_id = c.id 
            ORDER BY b.created_at DESC
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BillingDTO billing = mapResultSetToBilling(rs);
                billings.add(billing);
            }

            LOGGER.info("BillingDAO: Retrieved " + billings.size() + " billings from database");
            return billings;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingDAO: Error retrieving all billings", e);
            throw e;
        }
    }

    /**
     * Get billing by ID with items
     */
    public BillingDTO getBillingById(Long billingId) throws SQLException {
        String billingSql = """
            SELECT b.*, c.account_number, c.first_name, c.last_name, c.email, c.phone
            FROM billings b 
            LEFT JOIN clients c ON b.client_id = c.id 
            WHERE b.id = ?
            """;

        String itemsSql = """
            SELECT * FROM billing_items WHERE billing_id = ? ORDER BY id
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            BillingDTO billing = null;

            // Get billing record
            try (PreparedStatement pstmt = conn.prepareStatement(billingSql)) {
                pstmt.setLong(1, billingId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        billing = mapResultSetToBilling(rs);
                    }
                }
            }

            if (billing != null) {
                // Get billing items
                List<BillingItemDTO> items = new ArrayList<>();
                try (PreparedStatement pstmt = conn.prepareStatement(itemsSql)) {
                    pstmt.setLong(1, billingId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            BillingItemDTO item = mapResultSetToBillingItem(rs);
                            items.add(item);
                        }
                    }
                }
                billing.setItems(items);
            }

            return billing;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingDAO: Error retrieving billing by ID: " + billingId, e);
            throw e;
        }
    }

    /**
     * Update billing status
     */
    public boolean updateBillingStatus(Long billingId, String status) throws SQLException {
        String sql = "UPDATE billings SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setLong(2, billingId);

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("BillingDAO: Billing status updated - ID: " + billingId + ", Status: " + status);
                return true;
            }

            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingDAO: Error updating billing status", e);
            throw e;
        }
    }

    /**
     * Delete billing and its items
     */
    public boolean deleteBilling(Long billingId) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Delete billing items first
            String deleteItemsSql = "DELETE FROM billing_items WHERE billing_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteItemsSql)) {
                pstmt.setLong(1, billingId);
                pstmt.executeUpdate();
            }

            // Delete billing record
            String deleteBillingSql = "DELETE FROM billings WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteBillingSql)) {
                pstmt.setLong(1, billingId);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    conn.commit();
                    LOGGER.info("BillingDAO: Billing deleted successfully - ID: " + billingId);
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            LOGGER.log(Level.SEVERE, "BillingDAO: Error deleting billing", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Get billings by client ID
     */
    public List<BillingDTO> getBillingsByClientId(Long clientId) throws SQLException {
        List<BillingDTO> billings = new ArrayList<>();
        String sql = """
            SELECT b.*, c.account_number, c.first_name, c.last_name, c.email, c.phone
            FROM billings b 
            LEFT JOIN clients c ON b.client_id = c.id 
            WHERE b.client_id = ?
            ORDER BY b.created_at DESC
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, clientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BillingDTO billing = mapResultSetToBilling(rs);
                    billings.add(billing);
                }
            }

            LOGGER.info("BillingDAO: Retrieved " + billings.size() + " billings for client ID: " + clientId);
            return billings;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingDAO: Error retrieving billings by client ID", e);
            throw e;
        }
    }

    /**
     * Get billings by status
     */
    public List<BillingDTO> getBillingsByStatus(String status) throws SQLException {
        List<BillingDTO> billings = new ArrayList<>();
        String sql = """
            SELECT b.*, c.account_number, c.first_name, c.last_name, c.email, c.phone
            FROM billings b 
            LEFT JOIN clients c ON b.client_id = c.id 
            WHERE b.status = ?
            ORDER BY b.created_at DESC
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BillingDTO billing = mapResultSetToBilling(rs);
                    billings.add(billing);
                }
            }

            LOGGER.info("BillingDAO: Retrieved " + billings.size() + " billings with status: " + status);
            return billings;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingDAO: Error retrieving billings by status", e);
            throw e;
        }
    }

    /**
     * Search billings by bill number
     */
    public List<BillingDTO> searchBillingsByBillNumber(String billNumber) throws SQLException {
        List<BillingDTO> billings = new ArrayList<>();
        String sql = """
            SELECT b.*, c.account_number, c.first_name, c.last_name, c.email, c.phone
            FROM billings b 
            LEFT JOIN clients c ON b.client_id = c.id 
            WHERE b.bill_number LIKE ?
            ORDER BY b.created_at DESC
            """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + billNumber + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BillingDTO billing = mapResultSetToBilling(rs);
                    billings.add(billing);
                }
            }

            LOGGER.info("BillingDAO: Found " + billings.size() + " billings matching bill number: " + billNumber);
            return billings;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingDAO: Error searching billings by bill number", e);
            throw e;
        }
    }

    /**
     * Get total billings count
     */
    public int getTotalBillingsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM billings";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingDAO: Error getting total billings count", e);
            throw e;
        }
    }

    /**
     * Get next bill sequence number
     */
    public int getNextBillSequence() throws SQLException {
        String sql = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(bill_number, -4) AS UNSIGNED)), 0) + 1 
            FROM billings 
            WHERE bill_number LIKE ?
            """;
        String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String billPattern = "BILL-" + today + "-%";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, billPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 1; // Start from 1 if no bills found for today

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "BillingDAO: Error getting next bill sequence", e);
            throw e;
        }
    }

    /**
     * Map ResultSet to BillingDTO
     */
    private BillingDTO mapResultSetToBilling(ResultSet rs) throws SQLException {
        BillingDTO billing = new BillingDTO();
        
        billing.setId(rs.getLong("id"));
        billing.setBillNumber(rs.getString("bill_number"));
        billing.setClientId(rs.getLong("client_id"));
        billing.setSubtotal(rs.getBigDecimal("subtotal"));
        billing.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        billing.setTaxAmount(rs.getBigDecimal("tax_amount"));
        billing.setTotalAmount(rs.getBigDecimal("total_amount"));
        billing.setStatus(rs.getString("status"));
        billing.setPaymentMethod(rs.getString("payment_method"));
        billing.setNotes(rs.getString("notes"));
        
        // Handle timestamp fields safely
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            billing.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            billing.setUpdatedAt(updatedTs.toLocalDateTime());
        }
        
        // Set client information if available
        String accountNumber = rs.getString("account_number");
        if (accountNumber != null) {
            model.ClientDTO client = new model.ClientDTO();
            client.setId(billing.getClientId());
            client.setAccountNumber(accountNumber);
            client.setFirstName(rs.getString("first_name"));
            client.setLastName(rs.getString("last_name"));
            client.setEmail(rs.getString("email"));
            client.setPhone(rs.getString("phone"));
            billing.setClient(client);
        }
        
        return billing;
    }

    /**
     * Map ResultSet to BillingItemDTO
     */
    private BillingItemDTO mapResultSetToBillingItem(ResultSet rs) throws SQLException {
        BillingItemDTO item = new BillingItemDTO();
        
        item.setId(rs.getLong("id"));
        item.setBillingId(rs.getLong("billing_id"));
        item.setBookId(rs.getInt("book_id"));
        item.setBookTitle(rs.getString("book_title"));
        item.setBookAuthor(rs.getString("book_author"));
        item.setBookIsbn(rs.getString("book_isbn"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setQuantity(rs.getInt("quantity"));
        item.setTotalPrice(rs.getBigDecimal("total_price"));
        
        return item;
    }
}