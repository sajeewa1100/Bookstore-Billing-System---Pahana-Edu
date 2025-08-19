package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.User;
import model.TierDTO;
import service.TierService;
import service.ClientService;
import service.DashboardService;

/**
 * Dashboard Servlet - Handles tier management operations
 * Following the same pattern as BookServlet for consistency
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
            // Set user information for all requests
            User user = (User) request.getSession().getAttribute("user");
            request.setAttribute("currentUser", user);
            request.setAttribute("isManager", "manager".equals(user.getRole()));

            // Handle different actions - simplified like BookServlet
            switch (action.toLowerCase()) {
                case "dashboard":
                case "tiers":
                case "tiermanagement":
                case "":
                default:
                    handleTierManagement(request, response);
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
                case "createtier":
                case "add":
                    handleCreateTier(request, response);
                    break;
                case "updatetier":
                case "update":
                    handleUpdateTier(request, response);
                    break;
                case "deletetier":
                case "delete":
                    handleDeleteTier(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DashboardServlet: Error processing POST request: " + e.getMessage(), e);
            setErrorMessage(request, "Error processing request: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/DashboardServlet");
        }
    }

    /**
     * Handle tier management page - FIXED to match BookServlet pattern
     */
    private void handleTierManagement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            User user = (User) request.getSession().getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/views/login.jsp?error=Please login to access this page");
                return;
            }

            // Set user attributes
            request.setAttribute("currentUser", user);
            request.setAttribute("isManager", "manager".equals(user.getRole()));

            // Load tiers data - FIXED: Handle null case properly like BookServlet
            List<TierDTO> tiers = null;
            try {
                tiers = tierService.getAllTiers();
                LOGGER.info("DashboardServlet: Loaded " + (tiers != null ? tiers.size() : 0) + " tiers");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error loading tiers: " + e.getMessage(), e);
                tiers = new ArrayList<>();
                setErrorMessage(request, "Error loading tiers: " + e.getMessage());
            }
            
            if (tiers == null) {
                tiers = new ArrayList<>();
            }
            
            request.setAttribute("tiers", tiers);
            request.setAttribute("pageTitle", "Tier Management");

            // Forward to dashboard JSP (same pattern as books.jsp)
            request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);

            LOGGER.info("DashboardServlet: Successfully loaded tier management for user: " + user.getUsername());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DashboardServlet: Error loading tier management: " + e.getMessage(), e);
            
            // Set empty tiers and continue like BookServlet does
            request.setAttribute("tiers", new ArrayList<>());
            request.setAttribute("pageTitle", "Tier Management");
            setErrorMessage(request, "Error loading tiers: " + e.getMessage());
            
            try {
                request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/views/login.jsp?error=System error");
            }
        }
    }

    /**
     * Handle creating a new tier - FIXED following BookServlet pattern
     */
    private void handleCreateTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String tierName = request.getParameter("tierName");
            String minPointsStr = request.getParameter("minPoints");
            String maxPointsStr = request.getParameter("maxPoints");
            String discountRateStr = request.getParameter("discountRate");

            // Validate input like BookServlet does
            if (tierName == null || tierName.trim().isEmpty()) {
                setErrorMessage(request, "Tier name is required");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            if (minPointsStr == null || minPointsStr.trim().isEmpty()) {
                setErrorMessage(request, "Minimum points is required");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            if (discountRateStr == null || discountRateStr.trim().isEmpty()) {
                setErrorMessage(request, "Discount rate is required");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            // Parse and validate numbers
            int minPoints;
            Integer maxPoints = null;
            double discountRate;
            
            try {
                minPoints = Integer.parseInt(minPointsStr.trim());
                if (maxPointsStr != null && !maxPointsStr.trim().isEmpty()) {
                    maxPoints = Integer.parseInt(maxPointsStr.trim());
                }
                discountRate = Double.parseDouble(discountRateStr.trim());
            } catch (NumberFormatException e) {
                setErrorMessage(request, "Invalid number format");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            // Validate business rules
            if (minPoints < 0) {
                setErrorMessage(request, "Minimum points cannot be negative");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }
            
            if (maxPoints != null && maxPoints <= minPoints) {
                setErrorMessage(request, "Maximum points must be greater than minimum points");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }
            
            if (discountRate < 0 || discountRate > 100) {
                setErrorMessage(request, "Discount rate must be between 0 and 100");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            // Create tier using service
            TierDTO newTier = new TierDTO();
            newTier.setTierName(tierName.trim());
            newTier.setMinPoints(minPoints);
            newTier.setMaxPoints(maxPoints);
            
            // CRITICAL FIX: Convert percentage to decimal
            newTier.setDiscountRate(java.math.BigDecimal.valueOf(discountRate / 100.0));

            boolean success = tierService.createTier(newTier);

            if (success) {
                setSuccessMessage(request, "Tier '" + tierName + "' created successfully!");
            } else {
                setErrorMessage(request, "Failed to create tier. Please try again.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating tier: " + e.getMessage(), e);
            setErrorMessage(request, "Error creating tier: " + e.getMessage());
        }

        // Redirect back like BookServlet does
        response.sendRedirect(request.getContextPath() + "/DashboardServlet");
    }

    /**
     * Handle updating an existing tier - FIXED following BookServlet pattern
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
            if (idStr == null || idStr.trim().isEmpty()) {
                setErrorMessage(request, "Tier ID is required");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            if (tierName == null || tierName.trim().isEmpty()) {
                setErrorMessage(request, "Tier name is required");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            // Parse numbers
            long id;
            int minPoints;
            Integer maxPoints = null;
            double discountRate;
            
            try {
                id = Long.parseLong(idStr.trim());
                minPoints = Integer.parseInt(minPointsStr.trim());
                if (maxPointsStr != null && !maxPointsStr.trim().isEmpty()) {
                    maxPoints = Integer.parseInt(maxPointsStr.trim());
                }
                discountRate = Double.parseDouble(discountRateStr.trim());
            } catch (NumberFormatException e) {
                setErrorMessage(request, "Invalid number format");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            // Validate business rules
            if (minPoints < 0) {
                setErrorMessage(request, "Minimum points cannot be negative");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }
            
            if (maxPoints != null && maxPoints <= minPoints) {
                setErrorMessage(request, "Maximum points must be greater than minimum points");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }
            
            if (discountRate < 0 || discountRate > 100) {
                setErrorMessage(request, "Discount rate must be between 0 and 100");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            // Update tier
            TierDTO tierToUpdate = new TierDTO();
            tierToUpdate.setId(id);
            tierToUpdate.setTierName(tierName.trim());
            tierToUpdate.setMinPoints(minPoints);
            tierToUpdate.setMaxPoints(maxPoints);
            tierToUpdate.setDiscountRate(java.math.BigDecimal.valueOf(discountRate / 100.0));

            boolean success = tierService.updateTier(tierToUpdate);

            if (success) {
                setSuccessMessage(request, "Tier '" + tierName + "' updated successfully!");
            } else {
                setErrorMessage(request, "Failed to update tier. Please try again.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating tier: " + e.getMessage(), e);
            setErrorMessage(request, "Error updating tier: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/DashboardServlet");
    }

    /**
     * Handle deleting a tier - FIXED following BookServlet pattern
     */
    private void handleDeleteTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idStr = request.getParameter("id");

            if (idStr == null || idStr.trim().isEmpty()) {
                setErrorMessage(request, "Tier ID is required");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            long id;
            try {
                id = Long.parseLong(idStr.trim());
            } catch (NumberFormatException e) {
                setErrorMessage(request, "Invalid tier ID");
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }

            // Get tier name for confirmation message
            TierDTO tier = tierService.getTierById(id);
            String tierName = tier != null ? tier.getTierName() : "Unknown";

            boolean success = tierService.deleteTier(id);

            if (success) {
                setSuccessMessage(request, "Tier '" + tierName + "' deleted successfully!");
            } else {
                setErrorMessage(request, "Failed to delete tier. It may be in use by clients.");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting tier: " + e.getMessage(), e);
            setErrorMessage(request, "Error deleting tier: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/DashboardServlet");
    }

    /**
     * Get action parameter with default - same as BookServlet
     */
    private String getActionFromRequest(HttpServletRequest request) {
        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            action = "tiers"; // Default action
        } else {
            action = action.trim();
        }

        return action;
    }

    /**
     * Check if user is authenticated - same as BookServlet pattern
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
     * Handle errors - simplified like BookServlet
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {

        setErrorMessage(request, errorMessage);
        
        // Set basic attributes and forward to JSP
        request.setAttribute("tiers", new ArrayList<>());
        request.setAttribute("pageTitle", "Tier Management");
        
        try {
            request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp?error=System error");
        }
    }

    /**
     * Set error message in session - same as BookServlet pattern
     */
    private void setErrorMessage(HttpServletRequest request, String message) {
        try {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", message);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to set error message: " + e.getMessage(), e);
        }
    }

    /**
     * Set success message in session - same as BookServlet pattern
     */
    private void setSuccessMessage(HttpServletRequest request, String message) {
        try {
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", message);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to set success message: " + e.getMessage(), e);
        }
    }
}