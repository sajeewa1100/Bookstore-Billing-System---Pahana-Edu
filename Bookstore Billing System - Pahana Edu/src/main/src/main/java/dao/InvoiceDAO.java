package dao;

import model.InvoiceDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    
    public int createInvoice(InvoiceDTO invoice) {
        String sql = "INSERT INTO invoices (invoice_number, client_id, staff_id, invoice_date, subtotal, loyalty_discount, total_amount, cash_given, change_amount, loyalty_points_earned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, invoice.getInvoiceNumber());
            if (invoice.getClientId() > 0) {
                stmt.setInt(2, invoice.getClientId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, invoice.getStaffId());
            stmt.setDate(4, invoice.getInvoiceDate());
            stmt.setBigDecimal(5, invoice.getSubtotal());
            stmt.setBigDecimal(6, invoice.getLoyaltyDiscount());
            stmt.setBigDecimal(7, invoice.getTotalAmount());
            stmt.setBigDecimal(8, invoice.getCashGiven());
            stmt.setBigDecimal(9, invoice.getChangeAmount());
            stmt.setInt(10, invoice.getLoyaltyPointsEarned());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating invoice: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    public InvoiceDTO findById(int id) {
        String sql = "SELECT * FROM invoices WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding invoice by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<InvoiceDTO> getAllInvoices() {
        List<InvoiceDTO> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY created_at DESC";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all invoices: " + e.getMessage());
            e.printStackTrace();
        }
        return invoices;
    }
    
    // NEW SEARCH METHOD - Main addition for search functionality
    public List<InvoiceDTO> searchInvoices(String searchTerm, String searchType) {
        List<InvoiceDTO> invoices = new ArrayList<>();
        String sql;
        
        switch (searchType.toLowerCase()) {
            case "id":
                sql = "SELECT * FROM invoices WHERE id = ? OR invoice_number = ? ORDER BY created_at DESC";
                try (Connection conn = ConnectionManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    
                    // Try to parse as integer for ID search
                    try {
                        int invoiceId = Integer.parseInt(searchTerm);
                        stmt.setInt(1, invoiceId);
                        stmt.setString(2, searchTerm); // Also search by invoice number
                    } catch (NumberFormatException e) {
                        // If not a number, just search by invoice number
                        stmt.setInt(1, -1); // Non-existent ID
                        stmt.setString(2, searchTerm);
                    }
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            invoices.add(mapResultSetToInvoice(rs));
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error searching invoices by ID: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
                
            case "phone":
                sql = """
                    SELECT DISTINCT i.* FROM invoices i 
                    INNER JOIN clients c ON i.client_id = c.id 
                    WHERE c.phone LIKE ? 
                    ORDER BY i.created_at DESC
                    """;
                try (Connection conn = ConnectionManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    
                    stmt.setString(1, "%" + searchTerm + "%");
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            invoices.add(mapResultSetToInvoice(rs));
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error searching invoices by phone: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
                
            default:
                // Default to searching by invoice number or ID
                return searchInvoices(searchTerm, "id");
        }
        
        return invoices;
    }

    // NEW PAGINATION METHOD - For future use
    public List<InvoiceDTO> getAllInvoicesWithPagination(int limit, int offset) {
        List<InvoiceDTO> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting invoices with pagination: " + e.getMessage());
            e.printStackTrace();
        }
        return invoices;
    }
    
    public String generateNextInvoiceNumber() {
        String sql = "SELECT MAX(CAST(SUBSTRING(invoice_number, 1) AS UNSIGNED)) as max_num FROM invoices";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                return String.format("%010d", maxNum + 1);
            }
        } catch (SQLException e) {
            System.out.println("Error generating invoice number: " + e.getMessage());
            e.printStackTrace();
        }
        return "0000000001";
    }
    
    public boolean deleteInvoice(int invoiceId) {
        String sql = "DELETE FROM invoices WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, invoiceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting invoice: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private InvoiceDTO mapResultSetToInvoice(ResultSet rs) throws SQLException {
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setId(rs.getInt("id"));
        invoice.setInvoiceNumber(rs.getString("invoice_number"));
        invoice.setClientId(rs.getInt("client_id"));
        invoice.setStaffId(rs.getInt("staff_id"));
        invoice.setInvoiceDate(rs.getDate("invoice_date"));
        invoice.setSubtotal(rs.getBigDecimal("subtotal"));
        invoice.setLoyaltyDiscount(rs.getBigDecimal("loyalty_discount"));
        invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
        invoice.setCashGiven(rs.getBigDecimal("cash_given"));
        invoice.setChangeAmount(rs.getBigDecimal("change_amount"));
        invoice.setLoyaltyPointsEarned(rs.getInt("loyalty_points_earned"));
        invoice.setCreatedAt(rs.getTimestamp("created_at"));
        return invoice;
    }
}