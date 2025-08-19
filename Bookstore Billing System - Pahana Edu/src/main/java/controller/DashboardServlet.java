package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.User;
import model.TierDTO;
import service.TierService;
import service.ClientService;
import service.DashboardService;

/**
 * Dashboard Servlet - Handles dashboard operations and data loading
 */
@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());
    
    private TierService tierService;
    private ClientService clientService;
    private DashboardService dashboardService;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            tierService = new TierService();
            clientService = new ClientService();
            dashboardService = new DashboardService();
            LOGGER.info("DashboardServlet initialized successfully with services");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize DashboardServlet: " + e.getMessage(), e);
            throw new ServletException("Failed to initialize services", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = getActionFromRequest(request);
        LOGGER.info("DashboardServlet: Processing GET request with action: " + action);
        
        // Check if user is authenticated
        if (!isUserAuthenticated(request, response)) {
            return;
        }

        try {
            switch (action.toLowerCase()) {
                case "dashboard":
                case "home":
                    handleDashboard(request, response);
                    break;
                case "statistics":
                    handleStatistics(request, response);
                    break;
                case "tiermanagement":
                case "tiers":
                    handleTierManagement(request, response);
                    break;
                default:
                    handleDashboard(request, response);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DashboardServlet: Error processing GET request: " + e.getMessage(), e);
            handleError(request, response, "Error loading dashboard: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = getActionFromRequest(request);
        LOGGER.info("DashboardServlet: Processing POST request with action: " + action);
        
        // Check if user is authenticated
        if (!isUserAuthenticated(request, response)) {
            return;
        }

        try {
            switch (action.toLowerCase()) {
                case "refresh":
                case "dashboard":
                    handleDashboard(request, response);
                    break;
                case "createtier":
                    handleCreateTier(request, response);
                    break;
                case "updatetier":
                    handleUpdateTier(request, response);
                    break;
                case "deletetier":
                    handleDeleteTier(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/DashboardServlet?action=dashboard");
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DashboardServlet: Error processing POST request: " + e.getMessage(), e);
            handleError(request, response, "Error processing dashboard request: " + e.getMessage());
        }
    }

    /**
     * Handle tier management page
     */
    private void handleTierManagement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            User user = (User) request.getSession().getAttribute("user");
            
            if (user == null) {
                handleError(request, response, "User is not logged in.");
                return;
            }

            // Set user attributes
            request.setAttribute("currentUser", user);
            request.setAttribute("isManager", "manager".equals(user.getRole()));
            
            // Load tiers data
            List<TierDTO> tiers = tierService.getAllTiers();
            request.setAttribute("tiers", tiers);
            
            // Forward to tier management JSP (your current JSP)
            request.getRequestDispatcher("/views/tier-management.jsp").forward(request, response);
            
            LOGGER.info("DashboardServlet: Successfully loaded tier management for user: " + user.getUsername());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DashboardServlet: Error loading tier management: " + e.getMessage(), e);
            handleError(request, response, "Error loading tier management: " + e.getMessage());
        }
    }

    /**
     * Handle creating a new tier
     */
    private void handleCreateTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String tierName = request.getParameter("tierName");
            String minPointsStr = request.getParameter("minPoints");
            String maxPointsStr = request.getParameter("maxPoints");
            String discountRateStr = request.getParameter("discountRate");
            
            // Validate input
            if (tierName == null || tierName.trim().isEmpty() ||
                minPointsStr == null || maxPointsStr == null || discountRateStr == null) {
                setErrorMessage(request, "All fields are required");
                handleTierManagement(request, response);
                return;
            }
            
            int minPoints = Integer.parseInt(minPointsStr);
            int maxPoints = Integer.parseInt(maxPointsStr);
            double discountRate = Double.parseDouble(discountRateStr);
            
            // Validate business rules
            if (minPoints >= maxPoints) {
                setErrorMessage(request, "Maximum points must be greater than minimum points");
                handleTierManagement(request, response);
                return;
            }
            
            // Create tier using service
            TierDTO newTier = new TierDTO();
            newTier.setTierName(tierName.trim());
            newTier.setMinPoints(minPoints);
            newTier.setMaxPoints(maxPoints);
            newTier.setDiscountRate(java.math.BigDecimal.valueOf(discountRate));
            
            boolean success = tierService.createTier(newTier);
            
            if (success) {
                setSuccessMessage(request, "Tier created successfully");
            } else {
                setErrorMessage(request, "Failed to create tier");
            }
            
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid number format in input");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating tier: " + e.getMessage(), e);
            setErrorMessage(request, "Error creating tier: " + e.getMessage());
        }
        
        // Redirect back to tier management
        response.sendRedirect(request.getContextPath() + "/DashboardServlet?action=tiermanagement");
    }

    /**
     * Handle updating an existing tier
     */
    private void handleUpdateTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idStr = request.getParameter("id");
            String tierName = request.getParameter("tierName");
            String minPointsStr = request.getParameter("minPoints");
            String maxPointsStr = request.getParameter("maxPoints");
            String discountRateStr = request.getParameter("discountRate");
            
            // Validate input
            if (idStr == null || tierName == null || tierName.trim().isEmpty() ||
                minPointsStr == null || maxPointsStr == null || discountRateStr == null) {
                setErrorMessage(request, "All fields are required");
                handleTierManagement(request, response);
                return;
            }
            
            int id = Integer.parseInt(idStr);
            int minPoints = Integer.parseInt(minPointsStr);
            int maxPoints = Integer.parseInt(maxPointsStr);
            double discountRate = Double.parseDouble(discountRateStr);
            
            // Validate business rules
            if (minPoints >= maxPoints) {
                setErrorMessage(request, "Maximum points must be greater than minimum points");
                handleTierManagement(request, response);
                return;
            }
            
            // Update tier using service
            TierDTO tierToUpdate = new TierDTO();
            tierToUpdate.setId((long) id);
            tierToUpdate.setTierName(tierName.trim());
            tierToUpdate.setMinPoints(minPoints);
            tierToUpdate.setMaxPoints(maxPoints);
            tierToUpdate.setDiscountRate(java.math.BigDecimal.valueOf(discountRate));
            
            boolean success = tierService.updateTier(tierToUpdate);
            
            if (success) {
                setSuccessMessage(request, "Tier updated successfully");
            } else {
                setErrorMessage(request, "Failed to update tier");
            }
            
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid number format in input");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating tier: " + e.getMessage(), e);
            setErrorMessage(request, "Error updating tier: " + e.getMessage());
        }
        
        // Redirect back to tier management
        response.sendRedirect(request.getContextPath() + "/DashboardServlet?action=tiermanagement");
    }

    /**
     * Handle deleting a tier
     */
    private void handleDeleteTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String idStr = request.getParameter("id");
            
            if (idStr == null) {
                setErrorMessage(request, "Tier ID is required");
                handleTierManagement(request, response);
                return;
            }
            
            long id = Long.parseLong(idStr);
            
            boolean success = tierService.deleteTier(id);
            
            if (success) {
                setSuccessMessage(request, "Tier deleted successfully");
            } else {
                setErrorMessage(request, "Failed to delete tier");
            }
            
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid tier ID");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting tier: " + e.getMessage(), e);
            setErrorMessage(request, "Error deleting tier: " + e.getMessage());
        }
        
        // Redirect back to tier management
        response.sendRedirect(request.getContextPath() + "/DashboardServlet?action=tiermanagement");
    }

    /**
     * Get the action parameter from the request, with fallback to default
     */
    private String getActionFromRequest(HttpServletRequest request) {
        String action = request.getParameter("action");
        
        if (action == null || action.trim().isEmpty()) {
            action = "dashboard";
            LOGGER.info("DashboardServlet: No action specified, using default: " + action);
        } else {
            action = action.trim();
        }
        
        return action;
    }

    /**
     * Handle main dashboard page with summary data
     */
    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            User user = (User) request.getSession().getAttribute("user");
            
            // Ensure the user is logged in
            if (user == null) {
                handleError(request, response, "User is not logged in.");
                return;
            }

            // Set user attributes
            request.setAttribute("currentUser", user);
            request.setAttribute("isManager", "manager".equals(user.getRole()));
            
            // Load dashboard data
            loadDashboardData(request);
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
            
            LOGGER.info("DashboardServlet: Successfully loaded dashboard for user: " + user.getUsername());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DashboardServlet: Error loading dashboard: " + e.getMessage(), e);
            handleError(request, response, "Error loading dashboard data: " + e.getMessage());
        }
    }

    /**
     * Load all dashboard data including tiers and statistics
     */
    private void loadDashboardData(HttpServletRequest request) {
        try {
            // Load tiers data
            List<TierDTO> tiers = tierService.getAllTiers();
            request.setAttribute("tiers", tiers);
            
            // Load dashboard statistics
            loadDashboardStatistics(request);
            
            LOGGER.info("DashboardServlet: Dashboard data loaded successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "DashboardServlet: Error loading dashboard data: " + e.getMessage(), e);
            // Set empty values if data loading fails
            request.setAttribute("totalBooks", 0);
            request.setAttribute("totalClients", 0);
            request.setAttribute("totalOrders", 0);
            request.setAttribute("totalRevenue", "0.00");
            request.setAttribute("totalTiers", 0);
        }
    }

    /**
     * Load dashboard statistics with actual implementations
     */
    private void loadDashboardStatistics(HttpServletRequest request) {
        try {
            int totalTiers = getTotalTiers();
            int totalBooks = getTotalBooks();
            int totalOrders = getTotalOrders();
            String totalRevenue = getTotalRevenue();
            
            // Set attributes
            request.setAttribute("totalBooks", totalBooks);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("totalTiers", totalTiers);
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "DashboardServlet: Error loading statistics: " + e.getMessage(), e);
            // Set default values on error
            request.setAttribute("totalBooks", 0);
            request.setAttribute("totalClients", 0);
            request.setAttribute("totalOrders", 0);
            request.setAttribute("totalRevenue", "0.00");
            request.setAttribute("totalTiers", 0);
        }
    }

    /**
     * Handle statistics page
     */
    private void handleStatistics(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            User user = (User) request.getSession().getAttribute("user");
            
            // Set user attributes
            request.setAttribute("currentUser", user);
            request.setAttribute("isManager", "manager".equals(user.getRole()));
            
            // Load detailed statistics
            loadDetailedStatistics(request);
            
            // Forward to statistics JSP
            request.getRequestDispatcher("/views/statistics.jsp").forward(request, response);
            
            LOGGER.info("DashboardServlet: Successfully loaded statistics");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DashboardServlet: Error loading statistics: " + e.getMessage(), e);
            handleError(request, response, "Error loading statistics: " + e.getMessage());
        }
    }

    /**
     * Load detailed statistics for statistics page
     */
    private void loadDetailedStatistics(HttpServletRequest request) {
        try {
            // Basic statistics
            loadDashboardStatistics(request);
            
            // Additional detailed statistics
            List<TierDTO> tiers = tierService.getAllTiers();
            request.setAttribute("allTiers", tiers);
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "DashboardServlet: Error loading detailed statistics: " + e.getMessage(), e);
        }
    }

    /**
     * Get total tiers count
     */
    private int getTotalTiers() {
        try {
            return tierService.getAllTiers().size();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "DashboardServlet: Error getting total tiers count: " + e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Get total books count
     */
    private int getTotalBooks() {
        try {
            return dashboardService.getTotalBooksCount();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "DashboardServlet: Error getting total books count: " + e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Get total orders count - Placeholder
     */
    private int getTotalOrders() {
        return 0; // Replace with actual logic when OrderService is available
    }

    /**
     * Get total revenue - Placeholder
     */
    private String getTotalRevenue() {
        return "0.00"; // Replace with actual logic when OrderService is available
    }

    /**
     * Check if user is authenticated
     */
    private boolean isUserAuthenticated(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp?error=Please login to access this page");
            return false;
        }
        
        return true;
    }

    /**
     * Handle errors by setting error message and redirecting
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        
        // Set error message in session
        setErrorMessage(request, errorMessage);
        
        // Try to load basic dashboard data even on error
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user != null) {
                request.setAttribute("currentUser", user);
                request.setAttribute("isManager", "manager".equals(user.getRole()));
            }
            
            // Set default values
            request.setAttribute("totalBooks", 0);
            request.setAttribute("totalClients", 0);
            request.setAttribute("totalOrders", 0);
            request.setAttribute("totalRevenue", "0.00");
            request.setAttribute("totalTiers", 0);
            
            // Forward to dashboard
            request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DashboardServlet: Critical error in error handling: " + e.getMessage(), e);
            // If all else fails, redirect to login
            response.sendRedirect(request.getContextPath() + "/views/login.jsp?error=System error occurred");
        }
    }

    /**
     * Set error message in session for display to user
     */
    private void setErrorMessage(HttpServletRequest request, String message) {
        try {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", message);
            LOGGER.info("DashboardServlet: Error message set - " + message);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "DashboardServlet: Failed to set error message: " + e.getMessage(), e);
        }
    }

    /**
     * Set success message in session for display to user
     */
    private void setSuccessMessage(HttpServletRequest request, String message) {
        try {
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", message);
            LOGGER.info("DashboardServlet: Success message set - " + message);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "DashboardServlet: Failed to set success message: " + e.getMessage(), e);
        }
    }
}