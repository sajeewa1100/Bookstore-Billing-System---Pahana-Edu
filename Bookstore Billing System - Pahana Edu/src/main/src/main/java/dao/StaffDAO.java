package dao;

import model.StaffDTO;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    
    public int createStaff(StaffDTO staff) {
        String sql = "INSERT INTO staff (employee_id, first_name, last_name, email, phone, position) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, staff.getEmployeeId());
            statement.setString(2, staff.getFirstName());
            statement.setString(3, staff.getLastName());
            statement.setString(4, staff.getEmail());
            statement.setString(5, staff.getPhone());
            statement.setString(6, staff.getPosition());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int staffId = generatedKeys.getInt(1);
                        System.out.println("Staff created successfully with ID: " + staffId);
                        return staffId;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error creating staff: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create staff", e);
        }
        
        return 0;
    }
    
    public boolean updateStaff(StaffDTO staff) {
        String sql = "UPDATE staff SET employee_id = ?, first_name = ?, last_name = ?, email = ?, phone = ?, position = ? WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
            statement.setString(1, staff.getEmployeeId());
            statement.setString(2, staff.getFirstName());
            statement.setString(3, staff.getLastName());
            statement.setString(4, staff.getEmail());
            statement.setString(5, staff.getPhone());
            statement.setString(6, staff.getPosition());
            statement.setInt(7, staff.getId());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Staff updated successfully: " + staff.getId());
                return true;
            } else {
                System.out.println("No staff found with ID: " + staff.getId());
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating staff: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update staff", e);
        }
    }
    
    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
            statement.setInt(1, staffId);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Staff deleted successfully: " + staffId);
                return true;
            } else {
                System.out.println("No staff found with ID: " + staffId);
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("Error deleting staff: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete staff", e);
        }
    }
    
    public StaffDTO findById(int staffId) {
        String sql = "SELECT s.*, u.user_id as linked_user_id FROM staff s LEFT JOIN users u ON s.user_id = u.user_id WHERE s.id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
            statement.setInt(1, staffId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToStaff(resultSet);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding staff by ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to find staff by ID", e);
        }
        
        return null;
    }
    
    public StaffDTO findByEmployeeId(String employeeId) {
        String sql = "SELECT s.*, u.user_id as linked_user_id FROM staff s LEFT JOIN users u ON s.user_id = u.user_id WHERE s.employee_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
            statement.setString(1, employeeId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToStaff(resultSet);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding staff by employee ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to find staff by employee ID", e);
        }
        
        return null;
    }
    
    public List<StaffDTO> getAllStaff() {
        String sql = "SELECT s.*, u.user_id as linked_user_id FROM staff s LEFT JOIN users u ON s.user_id = u.user_id ORDER BY s.first_name, s.last_name";
        List<StaffDTO> staffList = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                StaffDTO staff = mapResultSetToStaff(resultSet);
                staffList.add(staff);
            }
            
            System.out.println("Retrieved " + staffList.size() + " staff members");
            
        } catch (SQLException e) {
            System.out.println("Error getting all staff: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get all staff", e);
        }
        
        return staffList;
    }
    
    /**
     * Link staff member to user account
     */
    public boolean linkStaffToUser(int staffId, int userId) throws SQLException {
        String sql = "UPDATE staff SET user_id = ? WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, staffId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Linked staff ID " + staffId + " to user ID " + userId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error linking staff to user: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Remove link between staff and user account
     */
    public boolean unlinkStaffFromUser(int staffId) throws SQLException {
        String sql = "UPDATE staff SET user_id = NULL WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, staffId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Unlinked staff ID " + staffId + " from user account");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error unlinking staff from user: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get user ID associated with staff member
     */
    public int getUserIdForStaff(int staffId) throws SQLException {
        String sql = "SELECT user_id FROM staff WHERE id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, staffId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    return rs.wasNull() ? 0 : userId; // Return 0 if NULL
                }
                return 0; // Staff not found
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user ID for staff: " + e.getMessage());
            throw e;
        }
    }
    
    public boolean employeeIdExists(String employeeId) {
        String sql = "SELECT COUNT(*) FROM staff WHERE employee_id = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
            statement.setString(1, employeeId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking employee ID existence: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to check employee ID existence", e);
        }
        
        return false;
    }
    
    public List<StaffDTO> searchStaff(String searchTerm) {
        String sql = "SELECT s.*, u.user_id as linked_user_id FROM staff s " +
                    "LEFT JOIN users u ON s.user_id = u.user_id WHERE " +
                    "s.first_name LIKE ? OR " +
                    "s.last_name LIKE ? OR " +
                    "s.employee_id LIKE ? OR " +
                    "s.email LIKE ? OR " +
                    "s.phone LIKE ? " +
                    "ORDER BY s.first_name, s.last_name";
        
        List<StaffDTO> staffList = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    StaffDTO staff = mapResultSetToStaff(resultSet);
                    staffList.add(staff);
                }
            }
            
            System.out.println("Search '" + searchTerm + "' returned " + staffList.size() + " staff members");
            
        } catch (SQLException e) {
            System.out.println("Error searching staff: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to search staff", e);
        }
        
        return staffList;
    }
    
    /**
     * Mapping method to convert ResultSet to StaffDTO
     */
    private StaffDTO mapResultSetToStaff(ResultSet resultSet) throws SQLException {
        StaffDTO staff = new StaffDTO();
        
        staff.setId(resultSet.getInt("id"));
        staff.setEmployeeId(resultSet.getString("employee_id"));
        staff.setFirstName(resultSet.getString("first_name"));
        staff.setLastName(resultSet.getString("last_name"));
        staff.setEmail(resultSet.getString("email"));
        staff.setPhone(resultSet.getString("phone"));
        staff.setPosition(resultSet.getString("position"));
        staff.setCreatedAt(resultSet.getTimestamp("created_at"));
        
        // Handle user account linking
        try {
            int linkedUserId = resultSet.getInt("linked_user_id");
            if (!resultSet.wasNull()) {
                staff.setUserId(linkedUserId);
                staff.setHasUserAccount(true);
            } else {
                staff.setUserId(0);
                staff.setHasUserAccount(false);
            }
        } catch (SQLException e) {
            // Column might not exist in some queries, set defaults
            staff.setUserId(0);
            staff.setHasUserAccount(false);
        }
        
        return staff;
    }
}