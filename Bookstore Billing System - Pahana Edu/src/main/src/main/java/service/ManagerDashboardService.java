package service;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ManagerDashboardService {
    private StaffDAO staffDAO;
    private InvoiceDAO invoiceDAO;
    private ClientDAO clientDAO;
    private UserDAO userDAO;
    private LoyaltySettingsDAO loyaltySettingsDAO;
    
    public ManagerDashboardService() {
        this.staffDAO = new StaffDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.clientDAO = new ClientDAO();
        this.userDAO = new UserDAO();
        this.loyaltySettingsDAO = new LoyaltySettingsDAO();
    }
    
    // ===== DASHBOARD STATISTICS =====
    
    public Map<String, Object> getDashboardStatistics() {
        System.out.println("ManagerDashboardService.getDashboardStatistics called");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            LocalDate now = LocalDate.now();
            
            // Revenue Statistics
            BigDecimal todayRevenue = getTodayRevenue();
            BigDecimal monthlyRevenue = getMonthlyRevenue(now.getMonthValue(), now.getYear());
            BigDecimal yearlyRevenue = getYearlyRevenue(now.getYear());
            
            // Counts
            int totalStaff = getAllStaff().size();
            int totalClients = getTotalClients();
            int todayInvoices = getTodayInvoicesCount();
            int monthlyInvoices = getMonthlyInvoicesCount();
            
            // Set all statistics
            stats.put("todayRevenue", todayRevenue);
            stats.put("monthlyRevenue", monthlyRevenue);
            stats.put("yearlyRevenue", yearlyRevenue);
            stats.put("totalStaff", totalStaff);
            stats.put("totalClients", totalClients);
            stats.put("todayInvoices", todayInvoices);
            stats.put("monthlyInvoices", monthlyInvoices);
            
            System.out.println("Dashboard stats generated successfully");
            
        } catch (Exception e) {
            System.out.println("Error getting dashboard statistics: " + e.getMessage());
            e.printStackTrace();
            // Return safe defaults
            stats.put("todayRevenue", BigDecimal.ZERO);
            stats.put("monthlyRevenue", BigDecimal.ZERO);
            stats.put("yearlyRevenue", BigDecimal.ZERO);
            stats.put("totalStaff", 0);
            stats.put("totalClients", 0);
            stats.put("todayInvoices", 0);
            stats.put("monthlyInvoices", 0);
        }
        
        return stats;
    }
    
    // ===== REVENUE CALCULATIONS =====
    
    public BigDecimal getTodayRevenue() {
        LocalDate today = LocalDate.now();
        return calculateRevenueForDate(today);
    }
    
    public BigDecimal getMonthlyRevenue(int month, int year) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        return calculateRevenueForPeriod(monthStart, monthEnd);
    }
    
    public BigDecimal getYearlyRevenue(int year) {
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);
        return calculateRevenueForPeriod(yearStart, yearEnd);
    }
    
    private BigDecimal calculateRevenueForDate(LocalDate date) {
        try {
            List<InvoiceDTO> allInvoices = invoiceDAO.getAllInvoices();
            BigDecimal revenue = BigDecimal.ZERO;
            
            for (InvoiceDTO invoice : allInvoices) {
                if (invoice.getInvoiceDate() != null && 
                    invoice.getInvoiceDate().toLocalDate().equals(date)) {
                    if (invoice.getTotalAmount() != null) {
                        revenue = revenue.add(invoice.getTotalAmount());
                    }
                }
            }
            
            return revenue;
        } catch (Exception e) {
            System.out.println("Error calculating revenue for date: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    private BigDecimal calculateRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            List<InvoiceDTO> allInvoices = invoiceDAO.getAllInvoices();
            BigDecimal revenue = BigDecimal.ZERO;
            
            for (InvoiceDTO invoice : allInvoices) {
                if (invoice.getInvoiceDate() != null) {
                    LocalDate invoiceDate = invoice.getInvoiceDate().toLocalDate();
                    if (!invoiceDate.isBefore(startDate) && !invoiceDate.isAfter(endDate)) {
                        if (invoice.getTotalAmount() != null) {
                            revenue = revenue.add(invoice.getTotalAmount());
                        }
                    }
                }
            }
            
            return revenue;
        } catch (Exception e) {
            System.out.println("Error calculating revenue for period: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    public int getTodayInvoicesCount() {
        LocalDate today = LocalDate.now();
        return getInvoicesCountForDate(today);
    }
    
    public int getMonthlyInvoicesCount() {
        LocalDate now = LocalDate.now();
        LocalDate monthStart = LocalDate.of(now.getYear(), now.getMonth(), 1);
        return getInvoicesCountForPeriod(monthStart, now);
    }
    
    private int getInvoicesCountForDate(LocalDate date) {
        try {
            List<InvoiceDTO> allInvoices = invoiceDAO.getAllInvoices();
            int count = 0;
            
            for (InvoiceDTO invoice : allInvoices) {
                if (invoice.getInvoiceDate() != null && 
                    invoice.getInvoiceDate().toLocalDate().equals(date)) {
                    count++;
                }
            }
            
            return count;
        } catch (Exception e) {
            System.out.println("Error counting invoices for date: " + e.getMessage());
            return 0;
        }
    }
    
    private int getInvoicesCountForPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            List<InvoiceDTO> allInvoices = invoiceDAO.getAllInvoices();
            int count = 0;
            
            for (InvoiceDTO invoice : allInvoices) {
                if (invoice.getInvoiceDate() != null) {
                    LocalDate invoiceDate = invoice.getInvoiceDate().toLocalDate();
                    if (!invoiceDate.isBefore(startDate) && !invoiceDate.isAfter(endDate)) {
                        count++;
                    }
                }
            }
            
            return count;
        } catch (Exception e) {
            System.out.println("Error counting invoices for period: " + e.getMessage());
            return 0;
        }
    }
    
    private int getTotalClients() {
        try {
            return clientDAO.getAllClients().size();
        } catch (Exception e) {
            System.out.println("Error getting total clients: " + e.getMessage());
            return 0;
        }
    }
    
    
    
    // ===== STAFF MANAGEMENT =====
    
    public List<StaffDTO> getAllStaff() {
        try {
            return staffDAO.getAllStaff();
        } catch (Exception e) {
            System.out.println("Error getting all staff: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public int createStaff(StaffDTO staff) {
        System.out.println("Creating staff: " + staff.getFirstName() + " " + staff.getLastName());
        
        if (!staff.isValid()) {
            throw new IllegalArgumentException(staff.getValidationError());
        }
        
        if (staffDAO.employeeIdExists(staff.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists: " + staff.getEmployeeId());
        }
        
        try {
            return staffDAO.createStaff(staff);
        } catch (Exception e) {
            System.out.println("Error creating staff: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create staff member", e);
        }
    }
    
    public boolean updateStaff(StaffDTO staff) {
        System.out.println("Updating staff ID: " + staff.getId());
        
        if (!staff.isValid()) {
            throw new IllegalArgumentException(staff.getValidationError());
        }
        
        // Check if another staff member has the same employee ID (excluding current staff)
        try {
            StaffDTO existingStaff = staffDAO.findByEmployeeId(staff.getEmployeeId());
            if (existingStaff != null && existingStaff.getId() != staff.getId()) {
                throw new IllegalArgumentException("Employee ID already exists: " + staff.getEmployeeId());
            }
        } catch (Exception e) {
            System.out.println("Error checking employee ID: " + e.getMessage());
        }
        
        try {
            return staffDAO.updateStaff(staff);
        } catch (Exception e) {
            System.out.println("Error updating staff: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update staff member", e);
        }
    }
    
    public boolean deleteStaff(int staffId) {
        System.out.println("Deleting staff ID: " + staffId);
        
        StaffDTO staff = getStaffById(staffId);
        if (staff == null) {
            System.out.println("Staff not found: " + staffId);
            return false;
        }
        
        try {
            return staffDAO.deleteStaff(staffId);
        } catch (Exception e) {
            System.out.println("Error deleting staff: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete staff member", e);
        }
    }
    
    public StaffDTO getStaffById(int staffId) {
        try {
            return staffDAO.findById(staffId);
        } catch (Exception e) {
            System.out.println("Error getting staff by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Link staff member to user account
     */
    public boolean linkStaffToUser(int staffId, int userId) {
        System.out.println("Linking staff ID " + staffId + " to user ID " + userId);
        
        try {
            return staffDAO.linkStaffToUser(staffId, userId);
        } catch (Exception e) {
            System.err.println("Error linking staff to user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get user ID associated with staff member
     */
    public int getUserIdForStaff(int staffId) {
        System.out.println("Getting user ID for staff ID: " + staffId);
        
        try {
            return staffDAO.getUserIdForStaff(staffId);
        } catch (Exception e) {
            System.err.println("Error getting user ID for staff: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Get all user accounts for admin/manager view
     */
    public List<User> getAllUserAccounts() {
        try {
            return userDAO.getAllUsers();
        } catch (Exception e) {
            System.err.println("Error getting user accounts: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Create user account
     */
    public int createUserAccount(User user, String tempPassword) {
        try {
            user.setPassword(util.PasswordUtils.hashPassword(tempPassword));
            return userDAO.createUser(user);
        } catch (Exception e) {
            System.err.println("Error creating user account: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Delete user account
     */
    public boolean deleteUserAccount(int userId) {
        try {
            return userDAO.deleteUser(userId);
        } catch (Exception e) {
            System.err.println("Error deleting user account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reset user password
     */
    public boolean resetUserPassword(int userId, String newPassword) {
        try {
            String hashedPassword = util.PasswordUtils.hashPassword(newPassword);
            userDAO.updatePassword(userId, hashedPassword);
            return true;
        } catch (Exception e) {
            System.err.println("Error resetting user password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user status
     */
    public boolean updateUserStatus(int userId, String status) {
        try {
            User user = userDAO.findById(userId);
            if (user != null) {
                userDAO.updateUserStatus(userId, status);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating user status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate temporary password
     */
    public String generateTemporaryPassword() {
        return util.PasswordUtils.generateTemporaryPassword();
    }
    
 // Replace the loyalty settings methods in your ManagerDashboardService with these:

 // ===== LOYALTY SETTINGS METHODS =====

 public LoyaltySettingsDTO getCurrentLoyaltySettings() {
     try {
         LoyaltySettingsDTO settings = loyaltySettingsDAO.getCurrentSettings();
         if (settings == null || !settings.isActive()) {
             return getDefaultLoyaltySettings();
         }
         return settings;
     } catch (Exception e) {
         System.out.println("Error getting loyalty settings: " + e.getMessage());
         e.printStackTrace();
         return getDefaultLoyaltySettings();
     }
 }

 public boolean updateLoyaltySettings(LoyaltySettingsDTO settings) {
     System.out.println("Updating loyalty settings");
     
     // Validate settings
     if (!isValidLoyaltySettings(settings)) {
         throw new IllegalArgumentException("Invalid loyalty settings provided");
     }
     
     try {
         // Ensure the settings are marked as active
         settings.setActive(true);
         
         // Get current settings to preserve ID if updating existing
         LoyaltySettingsDTO current = loyaltySettingsDAO.getCurrentSettings();
         if (current != null) {
             settings.setId(current.getId());
         }
         
         return loyaltySettingsDAO.updateSettings(settings);
     } catch (Exception e) {
         System.out.println("Error updating loyalty settings: " + e.getMessage());
         e.printStackTrace();
         throw new RuntimeException("Failed to update loyalty settings", e);
     }
 }

 private boolean isValidLoyaltySettings(LoyaltySettingsDTO settings) {
     if (settings == null) return false;
     
     // Validate points per 100 Rs
     if (settings.getPointsPer100Rs() < 1 || settings.getPointsPer100Rs() > 10) {
         return false;
     }
     
     // Validate discount percentages (0-50%)
     if (settings.getSilverDiscount() == null || 
         settings.getSilverDiscount().compareTo(BigDecimal.ZERO) < 0 || 
         settings.getSilverDiscount().compareTo(BigDecimal.valueOf(50)) > 0) {
         return false;
     }
     
     if (settings.getGoldDiscount() == null || 
         settings.getGoldDiscount().compareTo(BigDecimal.ZERO) < 0 || 
         settings.getGoldDiscount().compareTo(BigDecimal.valueOf(50)) > 0) {
         return false;
     }
     
     if (settings.getPlatinumDiscount() == null || 
         settings.getPlatinumDiscount().compareTo(BigDecimal.ZERO) < 0 || 
         settings.getPlatinumDiscount().compareTo(BigDecimal.valueOf(50)) > 0) {
         return false;
     }
     
     // Validate thresholds
     if (settings.getGoldThreshold() < 100 || settings.getPlatinumThreshold() < 1000) {
         return false;
     }
     
     // Ensure platinum threshold is higher than gold threshold
     if (settings.getPlatinumThreshold() <= settings.getGoldThreshold()) {
         return false;
     }
     
     // Ensure discounts are progressive (higher tier = higher discount)
     if (settings.getGoldDiscount().compareTo(settings.getSilverDiscount()) < 0 ||
         settings.getPlatinumDiscount().compareTo(settings.getGoldDiscount()) < 0) {
         return false;
     }
     
     return true;
 }

 private LoyaltySettingsDTO getDefaultLoyaltySettings() {
     LoyaltySettingsDTO defaults = new LoyaltySettingsDTO();
     defaults.setPointsPer100Rs(1);
     defaults.setSilverDiscount(new BigDecimal("5.00"));
     defaults.setGoldThreshold(5000);
     defaults.setGoldDiscount(new BigDecimal("10.00"));
     defaults.setPlatinumThreshold(15000);
     defaults.setPlatinumDiscount(new BigDecimal("15.00"));
     defaults.setActive(true);
     return defaults;
 }

 public String determineTierLevel(int loyaltyPoints, LoyaltySettingsDTO settings) {
     if (settings == null) {
         settings = getCurrentLoyaltySettings();
     }
     
     if (loyaltyPoints >= settings.getPlatinumThreshold()) {
         return "PLATINUM";
     } else if (loyaltyPoints >= settings.getGoldThreshold()) {
         return "GOLD";
     } else {
         return "SILVER";
     }
 }

 public BigDecimal calculateTierDiscount(int loyaltyPoints) {
     LoyaltySettingsDTO settings = getCurrentLoyaltySettings();
     String tier = determineTierLevel(loyaltyPoints, settings);
     
     return settings.getDiscountForTier(tier);
 }

 // Additional utility method for calculating points earned
 public int calculatePointsEarned(BigDecimal amountSpent) {
     LoyaltySettingsDTO settings = getCurrentLoyaltySettings();
     
     // Calculate points based on Rs. 100 spending units
     BigDecimal hundredRsUnits = amountSpent.divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_DOWN);
     return hundredRsUnits.intValue() * settings.getPointsPer100Rs();
 }

 // Method to get tier information for display
 public java.util.Map<String, Object> getTierInfo(int loyaltyPoints) {
     LoyaltySettingsDTO settings = getCurrentLoyaltySettings();
     String currentTier = determineTierLevel(loyaltyPoints, settings);
     BigDecimal currentDiscount = calculateTierDiscount(loyaltyPoints);
     
     java.util.Map<String, Object> tierInfo = new java.util.HashMap<>();
     tierInfo.put("currentTier", currentTier);
     tierInfo.put("currentDiscount", currentDiscount);
     tierInfo.put("currentPoints", loyaltyPoints);
     
     // Calculate points needed for next tier
     if ("SILVER".equals(currentTier)) {
         int pointsNeeded = settings.getGoldThreshold() - loyaltyPoints;
         tierInfo.put("nextTier", "GOLD");
         tierInfo.put("pointsNeededForNextTier", Math.max(0, pointsNeeded));
     } else if ("GOLD".equals(currentTier)) {
         int pointsNeeded = settings.getPlatinumThreshold() - loyaltyPoints;
         tierInfo.put("nextTier", "PLATINUM");
         tierInfo.put("pointsNeededForNextTier", Math.max(0, pointsNeeded));
     } else {
         tierInfo.put("nextTier", "MAXIMUM");
         tierInfo.put("pointsNeededForNextTier", 0);
     }
     
     return tierInfo;
 }
}