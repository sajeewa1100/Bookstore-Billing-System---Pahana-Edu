package dao;

import model.BillingDTO;
import model.BillItemDTO;
import util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Billing operations
 * Handles all database interactions related to billing and bill items
 */
public class BillingDAO {
    
    private static final Logger LOGGER = Logger.getLogger(BillingDAO.class.getName());
    
    // Database connection manager
    private ConnectionManager connectionManager;
    
    // SQL Queries for Billing
    private static final String INSERT_BILL = 
        "INSERT INTO billings (bill_number, client_id, client_name, client_email, client_phone, " +
        "bill_date, subtotal, tax_amount, discount_amount, total_amount, status, payment_method, " +
        "notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    
    private static final String SELECT_ALL_BILLS = 
        "SELECT * FROM billings ORDER BY created_at DESC";
    
    private static final String SELECT_BILL_BY_ID = 
        "SELECT * FROM billings WHERE id = ?";
    
    private static final String SELECT_BILL_BY_NUMBER = 
        "SELECT * FROM billings WHERE bill_number = ?";
    
    private static final String UPDATE_BILL_STATUS = 
        "UPDATE billings SET status = ?, updated_at = NOW() WHERE id = ?";
    
    private static final String UPDATE_BILL = 
        "UPDATE billings SET client_id = ?, client_name = ?, client_email = ?, client_phone = ?, " +
        "subtotal = ?, tax_amount = ?, discount_amount = ?, total_amount = ?, status = ?, " +
        "payment_method = ?, notes = ?, updated_at = NOW() WHERE id = ?";
    
    private static final String DELETE_BILL = 
        "DELETE FROM billings WHERE id = ?";
    
    private static final String SEARCH_BILLS_BY_CLIENT = 
        "SELECT * FROM billings WHERE client_name LIKE ? ORDER BY created_at DESC";
    
    private static final String SEARCH_BILLS_BY_STATUS = 
        "SELECT * FROM billings WHERE status = ? ORDER BY created_at DESC";
    
    private static final String SEARCH_BILLS_BY_DATE_RANGE = 
        "SELECT * FROM billings WHERE DATE(bill_date) BETWEEN ? AND ? ORDER BY created_at DESC";
    
    // SQL Queries for Bill Items
    private static final String INSERT_BILL_ITEM = 
        "INSERT INTO bill_items (bill_id, book_id, book_title, book_author, book_isbn, " +
        "unit_price, quantity, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BILL_ITEMS_BY_BILL_ID = 
        "SELECT * FROM bill_items WHERE bill_id = ? ORDER BY id";
    
    private static final String DELETE_BILL_ITEMS_BY_BILL_ID = 
        "DELETE FROM bill_items WHERE bill_id = ?";
    
    private static final String UPDATE_BOOK_QUANTITY = 
        "UPDATE books SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
    
    /**
     * Constructor
     */
    public BillingDAO() {
        this.connectionManager = ConnectionManager.getInstance();
    }
    
    /**
     * Get database connection
     */
    private Connection getConnection() {
        return connectionManager.getConnection();
    }
    
    /**
     * Create a new bill with items (Transaction)
     */
    public boolean createBill(BillingDTO bill) {
        Connection connection = null;
        PreparedStatement billStmt = null;
        PreparedStatement itemStmt = null;
        PreparedStatement updateBookStmt = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = getConnection();
            connection.setAutoCommit(false); // Start transaction
            
            // 1. Insert bill
            billStmt = connection.prepareStatement(INSERT_BILL, Statement.RETURN_GENERATED_KEYS);
            
            billStmt.setString(1, bill.getBillNumber());
            billStmt.setLong(2, bill.getClientId());
            billStmt.setString(3, bill.getClientName());
            billStmt.setString(4, bill.getClientEmail());
            billStmt.setString(5, bill.getClientPhone());
            billStmt.setTimestamp(6, Timestamp.valueOf(bill.getBillDate()));
            billStmt.setBigDecimal(7, bill.getSubtotal());
            billStmt.setBigDecimal(8, bill.getTaxAmount());
            billStmt.setBigDecimal(9, bill.getDiscountAmount());
            billStmt.setBigDecimal(10, bill.getTotalAmount());
            billStmt.setString(11, bill.getStatus());
            billStmt.setString(12, bill.getPaymentMethod());
            billStmt.setString(13, bill.getNotes());
            
            int billRowsAffected = billStmt.executeUpdate();
            
            if (billRowsAffected > 0) {
                // Get generated bill ID
                generatedKeys = billStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bill.setId(generatedKeys.getLong(1));
                }
                
                // 2. Insert bill items and update book quantities
                itemStmt = connection.prepareStatement(INSERT_BILL_ITEM);
                updateBookStmt = connection.prepareStatement(UPDATE_BOOK_QUANTITY);
                
                for (BillItemDTO item : bill.getItems()) {
                    // Insert bill item
                    itemStmt.setLong(1, bill.getId());
                    itemStmt.setInt(2, item.getBookId());
                    itemStmt.setString(3, item.getBookTitle());
                    itemStmt.setString(4, item.getBookAuthor());
                    itemStmt.setString(5, item.getBookIsbn());
                    itemStmt.setBigDecimal(6, item.getUnitPrice());
                    itemStmt.setInt(7, item.getQuantity());
                    itemStmt.setBigDecimal(8, item.getTotal());
                    
                    itemStmt.addBatch();
                    
                    // Update book quantity (reduce stock)
                    updateBookStmt.setInt(1, item.getQuantity()); // Quantity to subtract
                    updateBookStmt.setInt(2, item.getBookId());   // Book ID
                    updateBookStmt.setInt(3, item.getQuantity()); // Minimum required quantity
                    
                    updateBookStmt.addBatch();
                }
                
                // Execute batches
                int[] itemResults = itemStmt.executeBatch();
                int[] bookUpdateResults = updateBookStmt.executeBatch();
                
                // Check if all operations succeeded
                boolean allItemsInserted = true;
                for (int result : itemResults) {
                    if (result <= 0) {
                        allItemsInserted = false;
                        break;
                    }
                }
                
                boolean allBooksUpdated = true;
                for (int result : bookUpdateResults) {
                    if (result <= 0) {
                        allBooksUpdated = false;
                        break;
                    }
                }
                
                if (allItemsInserted && allBooksUpdated) {
                    connection.commit();
                    LOGGER.info("Bill created successfully: " + bill.getBillNumber());
                    return true;
                } else {
                    connection.rollback();
                    LOGGER.warning("Failed to update book quantities or insert items for bill: " + bill.getBillNumber());
                    return false;
                }
            } else {
                connection.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error during rollback: " + rollbackEx.getMessage(), rollbackEx);
            }
            LOGGER.log(Level.SEVERE, "Error creating bill: " + e.getMessage(), e);
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error resetting auto-commit: " + e.getMessage(), e);
            }
            closeResources(null, billStmt, generatedKeys);
            closeResources(null, itemStmt, null);
            closeResources(null, updateBookStmt, null);
        }
    }
    
    /**
     * Get all bills
     */
    public List<BillingDTO> getAllBills() {
        List<BillingDTO> bills = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SELECT_ALL_BILLS);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillingDTO bill = mapResultSetToBill(rs);
                // Load bill items
                bill.setItems(getBillItems(bill.getId()));
                bills.add(bill);
            }
            
            LOGGER.info("Retrieved " + bills.size() + " bills");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving bills: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, rs);
        }
        
        return bills;
    }
    
    /**
     * Get bill by ID
     */
    public BillingDTO getBillById(Long id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SELECT_BILL_BY_ID);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                BillingDTO bill = mapResultSetToBill(rs);
                // Load bill items
                bill.setItems(getBillItems(bill.getId()));
                LOGGER.info("Bill found: " + bill.getBillNumber());
                return bill;
            }
            
            LOGGER.warning("Bill not found with ID: " + id);
            return null;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving bill by ID: " + e.getMessage(), e);
            return null;
        } finally {
            closeResources(null, stmt, rs);
        }
    }
    
    /**
     * Get bill by bill number
     */
    public BillingDTO getBillByNumber(String billNumber) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SELECT_BILL_BY_NUMBER);
            stmt.setString(1, billNumber);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                BillingDTO bill = mapResultSetToBill(rs);
                // Load bill items
                bill.setItems(getBillItems(bill.getId()));
                return bill;
            }
            
            return null;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving bill by number: " + e.getMessage(), e);
            return null;
        } finally {
            closeResources(null, stmt, rs);
        }
    }
    
    /**
     * Update bill status
     */
    public boolean updateBillStatus(Long billId, String status) {
        Connection connection = null;
        PreparedStatement stmt = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(UPDATE_BILL_STATUS);
            stmt.setString(1, status);
            stmt.setLong(2, billId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info("Bill status updated successfully for ID: " + billId);
                return true;
            } else {
                LOGGER.warning("No bill found to update with ID: " + billId);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating bill status: " + e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, stmt, null);
        }
    }
    
    /**
     * Delete bill and its items
     */
    public boolean deleteBill(Long billId) {
        Connection connection = null;
        PreparedStatement deleteItemsStmt = null;
        PreparedStatement deleteBillStmt = null;
        
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            
            // 1. Delete bill items first
            deleteItemsStmt = connection.prepareStatement(DELETE_BILL_ITEMS_BY_BILL_ID);
            deleteItemsStmt.setLong(1, billId);
            deleteItemsStmt.executeUpdate();
            
            // 2. Delete bill
            deleteBillStmt = connection.prepareStatement(DELETE_BILL);
            deleteBillStmt.setLong(1, billId);
            
            int rowsAffected = deleteBillStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                connection.commit();
                LOGGER.info("Bill deleted successfully with ID: " + billId);
                return true;
            } else {
                connection.rollback();
                LOGGER.warning("No bill found to delete with ID: " + billId);
                return false;
            }
            
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error during rollback: " + rollbackEx.getMessage(), rollbackEx);
            }
            LOGGER.log(Level.SEVERE, "Error deleting bill: " + e.getMessage(), e);
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error resetting auto-commit: " + e.getMessage(), e);
            }
            closeResources(null, deleteItemsStmt, null);
            closeResources(null, deleteBillStmt, null);
        }
    }
    
    /**
     * Search bills by client name
     */
    public List<BillingDTO> searchBillsByClient(String clientName) {
        List<BillingDTO> bills = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SEARCH_BILLS_BY_CLIENT);
            stmt.setString(1, "%" + clientName + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillingDTO bill = mapResultSetToBill(rs);
                bills.add(bill);
            }
            
            LOGGER.info("Search returned " + bills.size() + " bills for client: " + clientName);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching bills by client: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, rs);
        }
        
        return bills;
    }
    
    /**
     * Get bill items by bill ID
     */
    private List<BillItemDTO> getBillItems(Long billId) {
        List<BillItemDTO> items = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SELECT_BILL_ITEMS_BY_BILL_ID);
            stmt.setLong(1, billId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillItemDTO item = mapResultSetToBillItem(rs);
                items.add(item);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving bill items: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, rs);
        }
        
        return items;
    }
    
    /**
     * Get bills by status
     */
    public List<BillingDTO> getBillsByStatus(String status) {
        List<BillingDTO> bills = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SEARCH_BILLS_BY_STATUS);
            stmt.setString(1, status);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillingDTO bill = mapResultSetToBill(rs);
                bills.add(bill);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving bills by status: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, rs);
        }
        
        return bills;
    }
    
    /**
     * Get bills by date range
     */
    public List<BillingDTO> getBillsByDateRange(String fromDate, String toDate) {
        List<BillingDTO> bills = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(SEARCH_BILLS_BY_DATE_RANGE);
            stmt.setString(1, fromDate);
            stmt.setString(2, toDate);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                BillingDTO bill = mapResultSetToBill(rs);
                bills.add(bill);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving bills by date range: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, rs);
        }
        
        return bills;
    }
    
    /**
     * Map ResultSet to BillingDTO
     */
    private BillingDTO mapResultSetToBill(ResultSet rs) throws SQLException {
        BillingDTO bill = new BillingDTO();
        
        bill.setId(rs.getLong("id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setClientId(rs.getLong("client_id"));
        bill.setClientName(rs.getString("client_name"));
        bill.setClientEmail(rs.getString("client_email"));
        bill.setClientPhone(rs.getString("client_phone"));
        
        // Handle timestamps
        Timestamp billDate = rs.getTimestamp("bill_date");
        if (billDate != null) {
            bill.setBillDate(billDate.toLocalDateTime());
        }
        
        bill.setSubtotal(rs.getBigDecimal("subtotal"));
        bill.setTaxAmount(rs.getBigDecimal("tax_amount"));
        bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setStatus(rs.getString("status"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        bill.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            bill.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            bill.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return bill;
    }
    
    /**
     * Map ResultSet to BillItemDTO
     */
    private BillItemDTO mapResultSetToBillItem(ResultSet rs) throws SQLException {
        BillItemDTO item = new BillItemDTO();
        
        item.setId(rs.getLong("id"));
        item.setBillId(rs.getLong("bill_id"));
        item.setBookId(rs.getInt("book_id"));
        item.setBookTitle(rs.getString("book_title"));
        item.setBookAuthor(rs.getString("book_author"));
        item.setBookIsbn(rs.getString("book_isbn"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setQuantity(rs.getInt("quantity"));
        item.setTotal(rs.getBigDecimal("total"));
        
        return item;
    }
    
    /**
     * Get total bills count
     */
    public int getTotalBillsCount() {
        String sql = "SELECT COUNT(*) FROM billings";
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total bills count: " + e.getMessage(), e);
            return 0;
        } finally {
            closeResources(null, stmt, rs);
        }
    }
    
    /**
     * Get total sales amount
     */
    public BigDecimal getTotalSalesAmount() {
        String sql = "SELECT SUM(total_amount) FROM billings WHERE status = 'COMPLETED'";
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return total != null ? total : BigDecimal.ZERO;
            }
            
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total sales amount: " + e.getMessage(), e);
            return BigDecimal.ZERO;
        } finally {
            closeResources(null, stmt, rs);
        }
    }
    
    /**
     * Check if bill number exists
     */
    public boolean billNumberExists(String billNumber) {
        String sql = "SELECT COUNT(*) FROM billings WHERE bill_number = ?";
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, billNumber);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking bill number existence: " + e.getMessage(), e);
            return true; // Return true to be safe and prevent duplicate bill numbers
        } finally {
            closeResources(null, stmt, rs);
        }
    }
    
    /**
     * Close database resources
     */
    public void close() {
        LOGGER.info("BillingDAO cleanup completed");
    }
    
    /**
     * Helper method to close resources
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet: " + e.getMessage(), e);
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing PreparedStatement: " + e.getMessage(), e);
            }
        }
        
        // Note: We don't close the connection here since it's managed by ConnectionManager
        // and may be reused by other operations
    }
}