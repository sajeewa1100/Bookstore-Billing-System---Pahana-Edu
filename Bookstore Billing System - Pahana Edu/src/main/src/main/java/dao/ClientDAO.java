package dao;

import model.ClientDTO;
import util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    
    // CREATE - Insert new client
    public int createClient(ClientDTO client) {
        String sql = "INSERT INTO clients (account_number, first_name, last_name, email, phone, address_street, address_city, address_state, address_zip, send_mail_auto, loyalty_points, tier_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, client.getAccountNumber());
            stmt.setString(2, client.getFirstName());
            stmt.setString(3, client.getLastName());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getPhone());
            stmt.setString(6, client.getAddressStreet());
            stmt.setString(7, client.getAddressCity());
            stmt.setString(8, client.getAddressState());
            stmt.setString(9, client.getAddressZip());
            stmt.setBoolean(10, client.isSendMailAuto());
            stmt.setInt(11, client.getLoyaltyPoints());
            stmt.setString(12, client.getTierLevel());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // UPDATE - Update existing client
    public boolean updateClient(ClientDTO client) {
        String sql = "UPDATE clients SET account_number = ?, first_name = ?, last_name = ?, email = ?, phone = ?, address_street = ?, address_city = ?, address_state = ?, address_zip = ?, send_mail_auto = ?, tier_level = ? WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, client.getAccountNumber());
            stmt.setString(2, client.getFirstName());
            stmt.setString(3, client.getLastName());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getPhone());
            stmt.setString(6, client.getAddressStreet());
            stmt.setString(7, client.getAddressCity());
            stmt.setString(8, client.getAddressState());
            stmt.setString(9, client.getAddressZip());
            stmt.setBoolean(10, client.isSendMailAuto());
            stmt.setString(11, client.getTierLevel());
            stmt.setInt(12, client.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // DELETE - Delete client
    public boolean deleteClient(int clientId) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // READ ALL - Get all clients
    public List<ClientDTO> getAllClients() {
        List<ClientDTO> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients ORDER BY first_name, last_name";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
    
    // Generate next account number
    public String generateNextAccountNumber() {
        String sql = "SELECT MAX(CAST(account_number AS UNSIGNED)) as max_num FROM clients WHERE account_number REGEXP '^[0-9]+$'";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                return String.format("AC%08d", maxNum + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "AC00000001";
    }
    
    // Check if phone number already exists
    public boolean phoneExists(String phone, int excludeClientId) {
        String sql = "SELECT COUNT(*) FROM clients WHERE phone = ? AND id != ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            stmt.setInt(2, excludeClientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Existing methods remain the same...
    public ClientDTO findById(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToClient(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ClientDTO findByPhone(String phone) {
        String sql = "SELECT * FROM clients WHERE phone = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToClient(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<ClientDTO> searchClients(String searchTerm) {
        List<ClientDTO> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? OR account_number LIKE ? ORDER BY first_name";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
    
    public boolean updateLoyaltyPoints(int clientId, int newPoints) {
        String sql = "UPDATE clients SET loyalty_points = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newPoints);
            stmt.setInt(2, clientId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    
    private ClientDTO mapResultSetToClient(ResultSet rs) throws SQLException {
        ClientDTO client = new ClientDTO();
        client.setId(rs.getInt("id"));
        client.setAccountNumber(rs.getString("account_number"));
        client.setFirstName(rs.getString("first_name"));
        client.setLastName(rs.getString("last_name"));
        client.setEmail(rs.getString("email"));
        client.setPhone(rs.getString("phone"));
        client.setAddressStreet(rs.getString("address_street"));
        client.setAddressCity(rs.getString("address_city"));
        client.setAddressState(rs.getString("address_state"));
        client.setAddressZip(rs.getString("address_zip"));
        client.setSendMailAuto(rs.getBoolean("send_mail_auto"));
        client.setLoyaltyPoints(rs.getInt("loyalty_points"));
        client.setTierLevel(rs.getString("tier_level"));
        return client;
    }
}
