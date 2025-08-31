package dao;

import model.InvoiceItemDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO {

    public int createInvoiceItem(InvoiceItemDTO item) {
        String sql = "INSERT INTO invoice_items (invoice_id, book_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, item.getInvoiceId());
            stmt.setInt(2, item.getBookId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getUnitPrice());
            stmt.setBigDecimal(5, item.getTotalPrice());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating invoice item: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<InvoiceItemDTO> getInvoiceItems(int invoiceId) {
        List<InvoiceItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM invoice_items WHERE invoice_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToInvoiceItem(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting invoice items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    public boolean deleteInvoiceItems(int invoiceId) {
        String sql = "DELETE FROM invoice_items WHERE invoice_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);
            stmt.executeUpdate(); // Don't check result - might be 0 if no items exist
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting invoice items: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Additional useful methods for completeness
    public boolean deleteInvoiceItem(int itemId) {
        String sql = "DELETE FROM invoice_items WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting invoice item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateInvoiceItem(InvoiceItemDTO item) {
        String sql = "UPDATE invoice_items SET book_id = ?, quantity = ?, unit_price = ?, total_price = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getBookId());
            stmt.setInt(2, item.getQuantity());
            stmt.setBigDecimal(3, item.getUnitPrice());
            stmt.setBigDecimal(4, item.getTotalPrice());
            stmt.setInt(5, item.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating invoice item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public InvoiceItemDTO findById(int itemId) {
        String sql = "SELECT * FROM invoice_items WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoiceItem(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding invoice item by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private InvoiceItemDTO mapResultSetToInvoiceItem(ResultSet rs) throws SQLException {
        InvoiceItemDTO item = new InvoiceItemDTO();
        item.setId(rs.getInt("id"));
        item.setInvoiceId(rs.getInt("invoice_id"));
        item.setBookId(rs.getInt("book_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setTotalPrice(rs.getBigDecimal("total_price"));
        return item;
    }
}