package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import model.User;
import model.StaffDTO;
import model.LoyaltySettingsDTO;
import service.ManagerDashboardService;
import util.AuthorizationUtil;
import util.PasswordUtils;
import dao.UserDAO;
import dao.StaffDAO;
import java.util.List;
import java.sql.SQLException;

@WebServlet("/ManagerServlet")
public class ManagerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ManagerDashboardService managerService;
    private UserDAO userDAO;
    private StaffDAO staffDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("ManagerServlet: Initializing servlet...");
        try {
            this.managerService = new ManagerDashboardService();
            this.userDAO = new UserDAO();
            this.staffDAO = new StaffDAO();
            System.out.println("ManagerServlet: Initialization successful");
        } catch (Exception e) {
            System.err.println("ManagerServlet: Initialization failed - " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Failed to initialize ManagerServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("ManagerServlet GET: Request received");
        
        // MANAGER ACCESS CONTROL
        if (!AuthorizationUtil.hasManagerAccess(request)) {
            System.out.println("ManagerServlet: Access denied - redirecting to login");
            response.sendRedirect("views/login.jsp?error=Manager access required");
            return;
        }

        String action = request.getParameter("action");
        System.out.println("ManagerServlet GET: Action parameter = " + action);
        
        if (action == null) {
            action = "dashboard";
            System.out.println("ManagerServlet GET: Action defaulted to dashboard");
        }

        try {
            switch (action) {
                case "dashboard":
                    System.out.println("ManagerServlet: Handling dashboard");
                    handleManagerDashboard(request, response);
                    break;
                case "manageStaff":
                    System.out.println("ManagerServlet: Handling manageStaff");
                    handleManageStaff(request, response);
                    break;
                case "loyaltySettings":
                    System.out.println("ManagerServlet: Handling loyaltySettings");
                    handleLoyaltySettings(request, response);
                    break;
                default:
                    System.out.println("ManagerServlet: Unknown action, redirecting to dashboard");
                    response.sendRedirect("ManagerServlet?action=dashboard");
                    break;
            }
        } catch (Exception e) {
            System.err.println("ManagerServlet GET error: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("ManagerServlet POST: Request received");
        
        if (!AuthorizationUtil.hasManagerAccess(request)) {
            System.out.println("ManagerServlet POST: Access denied");
            response.sendRedirect("views/login.jsp?error=Manager access required");
            return;
        }

        String action = request.getParameter("action");
        System.out.println("ManagerServlet POST: Action parameter = " + action);
        
        if (action == null) {
            System.out.println("ManagerServlet POST: No action specified");
            response.sendRedirect("ManagerServlet?action=dashboard");
            return;
        }

        try {
            switch (action) {
                case "createStaff":
                    System.out.println("ManagerServlet POST: Handling createStaff");
                    handleCreateStaff(request, response);
                    break;
                case "updateStaff":
                    System.out.println("ManagerServlet POST: Handling updateStaff");
                    handleUpdateStaff(request, response);
                    break;
                case "deleteStaff":
                    System.out.println("ManagerServlet POST: Handling deleteStaff");
                    handleDeleteStaff(request, response);
                    break;
                case "resetStaffPassword":
                    System.out.println("ManagerServlet POST: Handling resetStaffPassword");
                    handleResetStaffPassword(request, response);
                    break;
                case "updateLoyaltySettings":
                    System.out.println("ManagerServlet POST: Handling updateLoyaltySettings");
                    handleUpdateLoyaltySettings(request, response);
                    break;
                default:
                    System.out.println("ManagerServlet POST: Unknown action, redirecting to dashboard");
                    response.sendRedirect("ManagerServlet?action=dashboard");
                    break;
            }
        } catch (Exception e) {
            System.err.println("ManagerServlet POST error: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, e.getMessage());
        }
    }
    
    // === MANAGER DASHBOARD ===
    private void handleManagerDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("Loading manager dashboard...");
        
        try {
            // Get dashboard statistics
            System.out.println("Getting dashboard statistics...");
            request.setAttribute("dashboardStats", managerService.getDashboardStatistics());
            
            // Get staff summary
            System.out.println("Getting staff list...");
            List<StaffDTO> allStaff = managerService.getAllStaff();
            request.setAttribute("totalStaff", allStaff.size());
            request.setAttribute("staffList", allStaff);
            
            // Get current manager info
            System.out.println("Getting current user...");
            User manager = AuthorizationUtil.getCurrentUser(request);
            request.setAttribute("currentUser", manager);
            
            // Set the current action
            request.setAttribute("currentAction", "dashboard");
            
            System.out.println("Forwarding to manager-dashboard.jsp");
            request.getRequestDispatcher("views/manager-dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in handleManagerDashboard: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // === STAFF MANAGEMENT ===
    private void handleManageStaff(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("Loading manage staff...");
        
        try {
            List<StaffDTO> staffList = managerService.getAllStaff();
            request.setAttribute("staffList", staffList);
            request.setAttribute("totalStaff", staffList.size());
            
            // Get dashboard statistics for header
            request.setAttribute("dashboardStats", managerService.getDashboardStatistics());
            
            // Get current manager info
            User manager = AuthorizationUtil.getCurrentUser(request);
            request.setAttribute("currentUser", manager);
            
            // Set the current action
            request.setAttribute("currentAction", "manageStaff");
            
            // Add success/error messages
            addMessageAttributes(request);
            
            System.out.println("Forwarding to manager-dashboard.jsp");
            request.getRequestDispatcher("views/manager-dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in handleManageStaff: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // === LOYALTY SETTINGS ===
    private void handleLoyaltySettings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("Loading loyalty settings...");
        
        try {
            // Get current loyalty settings
            System.out.println("Getting current loyalty settings...");
            LoyaltySettingsDTO loyaltySettings = managerService.getCurrentLoyaltySettings();
            request.setAttribute("loyaltySettings", loyaltySettings);
            
            // Get dashboard statistics for header
            System.out.println("Getting dashboard statistics...");
            request.setAttribute("dashboardStats", managerService.getDashboardStatistics());
            
            // Get current manager info
            System.out.println("Getting current user...");
            User manager = AuthorizationUtil.getCurrentUser(request);
            request.setAttribute("currentUser", manager);
            
            // Set the current action
            request.setAttribute("currentAction", "loyaltySettings");
            
            // Add success/error messages
            addMessageAttributes(request);
            
            System.out.println("Forwarding to manager-dashboard.jsp");
            request.getRequestDispatcher("views/manager-dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in handleLoyaltySettings: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private void handleUpdateLoyaltySettings(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        System.out.println("Updating loyalty settings...");
        
        try {
            String pointsPer100RsStr = request.getParameter("pointsPer100Rs");
            String silverDiscountStr = request.getParameter("silverDiscount");
            String goldThresholdStr = request.getParameter("goldThreshold");
            String goldDiscountStr = request.getParameter("goldDiscount");
            String platinumThresholdStr = request.getParameter("platinumThreshold");
            String platinumDiscountStr = request.getParameter("platinumDiscount");
            
            System.out.println("Parameters received:");
            System.out.println("pointsPer100Rs: " + pointsPer100RsStr);
            System.out.println("silverDiscount: " + silverDiscountStr);
            System.out.println("goldThreshold: " + goldThresholdStr);
            System.out.println("goldDiscount: " + goldDiscountStr);
            System.out.println("platinumThreshold: " + platinumThresholdStr);
            System.out.println("platinumDiscount: " + platinumDiscountStr);
            
            // Validate input parameters
            if (pointsPer100RsStr == null || silverDiscountStr == null || goldThresholdStr == null ||
                goldDiscountStr == null || platinumThresholdStr == null || platinumDiscountStr == null) {
                System.err.println("Missing required parameters");
                response.sendRedirect("ManagerServlet?action=loyaltySettings&error=Missing required parameters");
                return;
            }
            
            // Create loyalty settings object
            LoyaltySettingsDTO settings = new LoyaltySettingsDTO();
            settings.setPointsPer100Rs(Integer.parseInt(pointsPer100RsStr.trim()));
            settings.setSilverDiscount(new BigDecimal(silverDiscountStr.trim()));
            settings.setGoldThreshold(Integer.parseInt(goldThresholdStr.trim()));
            settings.setGoldDiscount(new BigDecimal(goldDiscountStr.trim()));
            settings.setPlatinumThreshold(Integer.parseInt(platinumThresholdStr.trim()));
            settings.setPlatinumDiscount(new BigDecimal(platinumDiscountStr.trim()));
            
            System.out.println("Calling managerService.updateLoyaltySettings...");
            boolean success = managerService.updateLoyaltySettings(settings);
            
            if (success) {
                System.out.println("Loyalty settings updated successfully");
                response.sendRedirect("ManagerServlet?action=loyaltySettings&success=Loyalty settings updated successfully!");
            } else {
                System.err.println("Failed to update loyalty settings");
                response.sendRedirect("ManagerServlet?action=loyaltySettings&error=Failed to update loyalty settings");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in loyalty settings: " + e.getMessage());
            response.sendRedirect("ManagerServlet?action=loyaltySettings&error=Invalid number format in loyalty settings");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid loyalty settings: " + e.getMessage());
            response.sendRedirect("ManagerServlet?action=loyaltySettings&error=" + 
                                java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } catch (Exception e) {
            System.err.println("Error updating loyalty settings: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("ManagerServlet?action=loyaltySettings&error=Error updating loyalty settings");
        }
    }

    private void handleCreateStaff(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        System.out.println("Creating staff...");
        
        try {
            // Get staff information
            String employeeId = request.getParameter("employeeId");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String position = request.getParameter("position");
            
            System.out.println("Staff details - ID: " + employeeId + ", Name: " + firstName + " " + lastName);
            
            // Validate required fields
            if (employeeId == null || employeeId.trim().isEmpty()) {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=Employee ID is required");
                return;
            }
            
            if (firstName == null || firstName.trim().isEmpty()) {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=First name is required");
                return;
            }
            
            if (lastName == null || lastName.trim().isEmpty()) {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=Last name is required");
                return;
            }
            
            // Check if employee ID already exists
            if (staffDAO.employeeIdExists(employeeId.trim())) {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=Employee ID already exists");
                return;
            }
            
            // Create staff object
            StaffDTO staff = new StaffDTO();
            staff.setEmployeeId(employeeId.trim());
            staff.setFirstName(firstName.trim());
            staff.setLastName(lastName.trim());
            staff.setEmail(email != null ? email.trim() : "");
            staff.setPhone(phone != null ? phone.trim() : "");
            staff.setPosition(position != null ? position.trim() : "staff");
            
            // Create staff member
            int staffId = staffDAO.createStaff(staff);
            
            if (staffId > 0) {
                // Check if login credentials should be created
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                
                if (username != null && !username.trim().isEmpty() && 
                    password != null && !password.trim().isEmpty()) {
                    
                    // Create user account for staff
                    if (createStaffUserAccount(staffId, username.trim(), password, email.trim(), 
                                               firstName + " " + lastName, "staff")) {
                        response.sendRedirect("ManagerServlet?action=manageStaff&success=Staff member created with login access");
                    } else {
                        response.sendRedirect("ManagerServlet?action=manageStaff&success=Staff member created but failed to create login account");
                    }
                } else {
                    response.sendRedirect("ManagerServlet?action=manageStaff&success=Staff member created successfully");
                }
            } else {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=Failed to create staff member");
            }
            
        } catch (Exception e) {
            System.err.println("Error creating staff: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("ManagerServlet?action=manageStaff&error=" + 
                                java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private void handleUpdateStaff(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            
            // Get existing staff
            StaffDTO existingStaff = staffDAO.findById(staffId);
            if (existingStaff == null) {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=Staff member not found");
                return;
            }
            
            // Update staff information
            String employeeId = request.getParameter("employeeId");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String position = request.getParameter("position");
            
            existingStaff.setEmployeeId(employeeId != null ? employeeId.trim() : "");
            existingStaff.setFirstName(firstName != null ? firstName.trim() : "");
            existingStaff.setLastName(lastName != null ? lastName.trim() : "");
            existingStaff.setEmail(email != null ? email.trim() : "");
            existingStaff.setPhone(phone != null ? phone.trim() : "");
            existingStaff.setPosition(position != null ? position.trim() : "staff");
            
            if (staffDAO.updateStaff(existingStaff)) {
                response.sendRedirect("ManagerServlet?action=manageStaff&success=Staff member updated successfully");
            } else {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=Failed to update staff member");
            }
            
        } catch (Exception e) {
            response.sendRedirect("ManagerServlet?action=manageStaff&error=Invalid staff ID");
        }
    }

    private void handleDeleteStaff(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            
            // First disable user account if exists
            try {
                int userId = staffDAO.getUserIdForStaff(staffId);
                if (userId > 0) {
                    // Delete user account
                    userDAO.deleteUser(userId);
                    System.out.println("Deleted user account for staff ID: " + staffId);
                }
            } catch (Exception e) {
                System.err.println("Error deleting user account for staff: " + e.getMessage());
            }
            
            // Delete staff member
            if (staffDAO.deleteStaff(staffId)) {
                response.sendRedirect("ManagerServlet?action=manageStaff&success=Staff member deleted successfully");
            } else {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=Failed to delete staff member");
            }
            
        } catch (Exception e) {
            response.sendRedirect("ManagerServlet?action=manageStaff&error=Invalid staff ID");
        }
    }

    private void handleResetStaffPassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int staffId = Integer.parseInt(request.getParameter("staffId"));
            String newPassword = request.getParameter("newPassword");
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=New password is required");
                return;
            }
            
            // Get user ID for staff
            int userId = staffDAO.getUserIdForStaff(staffId);
            if (userId <= 0) {
                response.sendRedirect("ManagerServlet?action=manageStaff&error=No user account found for this staff member");
                return;
            }
            
            // Update password
            String hashedPassword = PasswordUtils.hashPassword(newPassword.trim());
            userDAO.updatePassword(userId, hashedPassword);
            
            response.sendRedirect("ManagerServlet?action=manageStaff&success=Password reset successfully");
            
        } catch (Exception e) {
            response.sendRedirect("ManagerServlet?action=manageStaff&error=Failed to reset password");
        }
    }
    
    // === HELPER METHODS ===
    
    private boolean createStaffUserAccount(int staffId, String username, String password, 
                                         String email, String fullName, String role) {
        try {
            // Check if username already exists
            User existingUser = userDAO.findByUsername(username.toLowerCase());
            if (existingUser != null) {
                System.err.println("Username already exists: " + username);
                return false;
            }
            
            // Check if email already exists (if provided)
            if (email != null && !email.trim().isEmpty()) {
                User existingEmailUser = userDAO.findByEmail(email);
                if (existingEmailUser != null) {
                    System.err.println("Email already exists: " + email);
                    return false;
                }
            }
            
            // Create user account
            User newUser = new User();
            newUser.setUsername(username.toLowerCase());
            newUser.setPassword(PasswordUtils.hashPassword(password));
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setRole(role);
            newUser.setStatus("active");
            newUser.setFirstLogin(false); // Staff can login immediately
            
            int userId = userDAO.createUser(newUser);
            
            if (userId > 0) {
                // Link staff to user account
                staffDAO.linkStaffToUser(staffId, userId);
                System.out.println("Created user account for staff ID: " + staffId + " with username: " + username);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating staff user account: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    private void addMessageAttributes(HttpServletRequest request) {
        String success = request.getParameter("success");
        String error = request.getParameter("error");
        if (success != null) {
            System.out.println("Adding success message: " + success);
            request.setAttribute("success", success);
        }
        if (error != null) {
            System.out.println("Adding error message: " + error);
            request.setAttribute("error", error);
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String message) 
            throws ServletException, IOException {
        System.err.println("Handling error: " + message);
        request.setAttribute("error", "System error: " + message);
        request.setAttribute("currentAction", "dashboard"); // Default to dashboard on error
        request.getRequestDispatcher("views/manager-dashboard.jsp").forward(request, response);
    }
}