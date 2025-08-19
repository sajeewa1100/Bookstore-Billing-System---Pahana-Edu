package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import model.User;
import model.TierDTO;
import service.TierService;

/**
 * Tier Management Servlet - Handles all tier-related operations
 */
@WebServlet("/TierServlet")
public class TierServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TierService tierService;

    @Override
    public void init() throws ServletException {
        super.init();
        tierService = new TierService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Check authentication and manager role
        if (!isManagerAuthenticated(request, response)) {
            return;
        }
        
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action.toLowerCase()) {
                case "list":
                case "tiers":
                    handleTierList(request, response);
                    break;
                case "view":
                    handleViewTier(request, response);
                    break;
                default:
                    handleTierList(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, "Error processing tier request: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Check authentication and manager role
        if (!isManagerAuthenticated(request, response)) {
            return;
        }

        try {
            switch (action.toLowerCase()) {
                case "create":
                    handleCreateTier(request, response);
                    break;
                case "update":
                    handleUpdateTier(request, response);
                    break;
                case "delete":
                    handleDeleteTier(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/TierServlet?action=list");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, "Error processing tier request: " + e.getMessage());
        }
    }

    /**
     * Handle tier list display
     */
    private void handleTierList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            List<TierDTO> tiers = tierService.getAllTiers();
            request.setAttribute("tiers", tiers);
            request.getRequestDispatcher("/views/tiers.jsp").forward(request, response);
        } catch (Exception e) {
            handleError(request, response, "Error loading tiers: " + e.getMessage());
        }
    }

    /**
     * Handle viewing a specific tier
     */
    private void handleViewTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String tierIdStr = request.getParameter("id");
            if (tierIdStr == null || tierIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Tier ID is required");
            }
            
            long tierId = Long.parseLong(tierIdStr);
            TierDTO tier = tierService.getTierById(tierId);
            
            if (tier == null) {
                throw new IllegalArgumentException("Tier not found");
            }
            
            request.setAttribute("tier", tier);
            request.getRequestDispatcher("/views/tier-detail.jsp").forward(request, response);
        } catch (Exception e) {
            handleError(request, response, "Error loading tier details: " + e.getMessage());
        }
    }

    /**
     * Handle tier creation
     */
    private void handleCreateTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Extract form parameters
            String tierName = request.getParameter("tierName");
            String minPointsStr = request.getParameter("minPoints");
            String maxPointsStr = request.getParameter("maxPoints");
            String discountRateStr = request.getParameter("discountRate");
            
            // Validate inputs
            if (tierName == null || tierName.trim().isEmpty()) {
                throw new IllegalArgumentException("Tier name is required");
            }
            
            int minPoints = Integer.parseInt(minPointsStr);
            int maxPoints = Integer.parseInt(maxPointsStr);
            double discountRate = Double.parseDouble(discountRateStr);
            
            // Validate business logic
            if (minPoints < 0 || maxPoints < 0) {
                throw new IllegalArgumentException("Points cannot be negative");
            }
            if (minPoints >= maxPoints) {
                throw new IllegalArgumentException("Minimum points must be less than maximum points");
            }
            if (discountRate < 0 || discountRate > 100) {
                throw new IllegalArgumentException("Discount rate must be between 0 and 100");
            }
            
            // Create tier
            TierDTO newTier = new TierDTO(tierName.trim(), minPoints, maxPoints, discountRate);
            boolean success = tierService.createTier(newTier);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Tier '" + tierName + "' created successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to create tier. Please try again.");
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Please enter valid numeric values");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error creating tier: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/TierServlet?action=list");
    }

    /**
     * Handle tier update
     */
    private void handleUpdateTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Extract form parameters
            String tierIdStr = request.getParameter("id");
            String tierName = request.getParameter("tierName");
            String minPointsStr = request.getParameter("minPoints");
            String maxPointsStr = request.getParameter("maxPoints");
            String discountRateStr = request.getParameter("discountRate");
            
            // Validate inputs
            if (tierIdStr == null || tierIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Tier ID is required");
            }
            if (tierName == null || tierName.trim().isEmpty()) {
                throw new IllegalArgumentException("Tier name is required");
            }
            
            long tierId = Long.parseLong(tierIdStr);
            int minPoints = Integer.parseInt(minPointsStr);
            int maxPoints = Integer.parseInt(maxPointsStr);
            double discountRate = Double.parseDouble(discountRateStr);
            
            // Validate business logic
            if (minPoints < 0 || maxPoints < 0) {
                throw new IllegalArgumentException("Points cannot be negative");
            }
            if (minPoints >= maxPoints) {
                throw new IllegalArgumentException("Minimum points must be less than maximum points");
            }
            if (discountRate < 0 || discountRate > 100) {
                throw new IllegalArgumentException("Discount rate must be between 0 and 100");
            }
            
            // Update tier
            TierDTO updatedTier = new TierDTO(tierName.trim(), minPoints, maxPoints, discountRate);
            boolean success = tierService.updateTier(updatedTier, tierId);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Tier '" + tierName + "' updated successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to update tier. Please try again.");
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Please enter valid numeric values");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error updating tier: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/TierServlet?action=list");
    }

    /**
     * Handle tier deletion
     */
    private void handleDeleteTier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String tierIdStr = request.getParameter("id");
            if (tierIdStr == null || tierIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Tier ID is required");
            }
            
            long tierId = Long.parseLong(tierIdStr);
            boolean success = tierService.deleteTier(tierId);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Tier deleted successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete tier. It may be in use by clients.");
            }
            
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error deleting tier: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/TierServlet?action=list");
    }

    /**
     * Check if user is authenticated and has manager role
     */
    private boolean isManagerAuthenticated(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp?error=Please login to access this page");
            return false;
        }
        
        User user = (User) session.getAttribute("user");
        if (!"manager".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/DashboardServlet?error=Access denied. Manager role required.");
            return false;
        }
        
        // Set user attributes for JSPs
        request.setAttribute("currentUser", user);
        request.setAttribute("isManager", true);
        
        return true;
    }

    /**
     * Handle errors by setting error message and redirecting
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.getSession().setAttribute("errorMessage", errorMessage);
        response.sendRedirect(request.getContextPath() + "/TierServlet?action=list");
    }
    
    
}
