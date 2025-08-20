package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import service.BillingService;
import service.ClientService;
import service.BookService;
import model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/BillingServlet")
public class BillingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BillingService billingService;
    private ClientService clientService;
    private BookService bookService;

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("BillingServlet: Initializing servlet...");
        try {
            this.billingService = new BillingService();
            this.clientService = new ClientService();
            this.bookService = new BookService();
            System.out.println("BillingServlet: Services initialized successfully");
        } catch (Exception e) {
            System.err.println("BillingServlet: Error initializing services: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Failed to initialize services", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        System.out.println("BillingServlet GET: Received action = " + action);
        System.out.println("BillingServlet GET: Request URI = " + request.getRequestURI());
        System.out.println("BillingServlet GET: Context Path = " + request.getContextPath());

        try {
            if ("billings".equals(action) || "list".equals(action) || action == null) {
                handleListBillings(request, response);
            } else if ("view".equals(action)) {
                handleViewBilling(request, response);
            } else if ("search".equals(action)) {
                handleSearchBillings(request, response);
            } else if ("find-client".equals(action)) {
                handleFindClient(request, response);
            } else if ("find-book".equals(action)) {
                handleFindBook(request, response);
            } else if ("print".equals(action)) {
                handlePrintBilling(request, response);
            } else if ("debug".equals(action)) {
                handleDebugInfo(request, response);
            } else {
                System.out.println("BillingServlet GET: Unknown action, redirecting to billings");
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
            }
        } catch (Exception e) {
            System.err.println("BillingServlet GET: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing request: " + e.getMessage());
        }
    }

    /**
     * Debug method to show what's happening
     */
    private void handleDebugInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Billing Debug Info</title></head>");
            out.println("<body>");
            out.println("<h1>🔍 Billing Servlet Debug Information</h1>");
            
            // Test services
            out.println("<h2>Service Status:</h2>");
            out.println("<p>BillingService: " + (billingService != null ? "✅ Initialized" : "❌ NULL") + "</p>");
            out.println("<p>ClientService: " + (clientService != null ? "✅ Initialized" : "❌ NULL") + "</p>");
            out.println("<p>BookService: " + (bookService != null ? "✅ Initialized" : "❌ NULL") + "</p>");
            
            // Test data retrieval
            try {
                List<BillingDTO> billings = billingService.getAllBillings();
                out.println("<h2>Billing Data:</h2>");
                out.println("<p>Total Billings: " + billings.size() + "</p>");
                
                if (!billings.isEmpty()) {
                    out.println("<p>Sample billing: " + billings.get(0).getBillNumber() + "</p>");
                }
            } catch (Exception e) {
                out.println("<h2>❌ Error getting billings:</h2>");
                out.println("<p>" + e.getMessage() + "</p>");
            }
            
            out.println("<h2>Request Info:</h2>");
            out.println("<p>Context Path: " + request.getContextPath() + "</p>");
            out.println("<p>Servlet Path: " + request.getServletPath() + "</p>");
            out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
            
            out.println("<br><a href='" + request.getContextPath() + "/BillingServlet?action=billings'>Try Billing Page</a>");
            out.println("<br><a href='" + request.getContextPath() + "/TestDataServlet'>Test Data Page</a>");
            
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handle listing all billings - WITH EXTENSIVE DEBUGGING
     */
    private void handleListBillings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("BillingServlet: handleListBillings() called");
        
        try {
            // Get billings data
            System.out.println("BillingServlet: Getting all billings...");
            List<BillingDTO> billings = billingService.getAllBillings();
            System.out.println("BillingServlet: Retrieved " + billings.size() + " billings");
            
            // Set basic attributes
            request.setAttribute("billings", billings);
            request.setAttribute("totalBillings", billings.size());
            request.setAttribute("pageTitle", "Billing Management");
            
            System.out.println("BillingServlet: Set billings attribute with " + billings.size() + " items");
            
            // Calculate and set statistics
            setBillingStatistics(request, billings);
            System.out.println("BillingServlet: Statistics calculated and set");
            
            // Print all attributes for debugging
            System.out.println("BillingServlet: All request attributes:");
            java.util.Enumeration<String> attributeNames = request.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attrName = attributeNames.nextElement();
                Object attrValue = request.getAttribute(attrName);
                System.out.println("  " + attrName + " = " + attrValue);
            }
            
            // Forward to JSP
            String jspPath = "views/billing.jsp";
            System.out.println("BillingServlet: Forwarding to " + jspPath);
            request.getRequestDispatcher(jspPath).forward(request, response);
            System.out.println("BillingServlet: Forward completed successfully");
            
        } catch (Exception e) {
            System.err.println("BillingServlet: Error in handleListBillings: " + e.getMessage());
            e.printStackTrace();
            
            // Set empty data to prevent JSP errors
            request.setAttribute("billings", new ArrayList<BillingDTO>());
            request.setAttribute("totalBillings", 0);
            request.setAttribute("pendingBillingsCount", 0);
            request.setAttribute("completedBillingsCount", 0);
            request.setAttribute("cancelledBillingsCount", 0);
            request.setAttribute("totalRevenue", BigDecimal.ZERO);
            request.setAttribute("errorMessage", "Failed to retrieve billings: " + e.getMessage());
            
            System.out.println("BillingServlet: Set empty attributes due to error, forwarding to JSP");
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
        }
    }

    /**
     * Set billing statistics with debugging
     */
    private void setBillingStatistics(HttpServletRequest request, List<BillingDTO> billings) {
        System.out.println("BillingServlet: setBillingStatistics() called with " + billings.size() + " billings");
        
        try {
            int totalBillings = billings.size();
            int pendingCount = 0;
            int completedCount = 0;
            int cancelledCount = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (BillingDTO billing : billings) {
                String status = billing.getStatus();
                System.out.println("BillingServlet: Processing billing " + billing.getBillNumber() + " with status: " + status);
                
                switch (status != null ? status : "PENDING") {
                    case "PENDING":
                        pendingCount++;
                        break;
                    case "COMPLETED":
                        completedCount++;
                        if (billing.getTotalAmount() != null) {
                            totalRevenue = totalRevenue.add(billing.getTotalAmount());
                        }
                        break;
                    case "CANCELLED":
                        cancelledCount++;
                        break;
                    default:
                        System.out.println("BillingServlet: Unknown status: " + status);
                        pendingCount++; // Default to pending
                        break;
                }
            }

            // Set attributes
            request.setAttribute("pendingBillingsCount", pendingCount);
            request.setAttribute("completedBillingsCount", completedCount);
            request.setAttribute("cancelledBillingsCount", cancelledCount);
            request.setAttribute("totalRevenue", totalRevenue);

            // Calculate percentages
            if (totalBillings > 0) {
                request.setAttribute("pendingPercentage", Math.round((pendingCount * 100.0) / totalBillings));
                request.setAttribute("completedPercentage", Math.round((completedCount * 100.0) / totalBillings));
                request.setAttribute("cancelledPercentage", Math.round((cancelledCount * 100.0) / totalBillings));
            } else {
                request.setAttribute("pendingPercentage", 0);
                request.setAttribute("completedPercentage", 0);
                request.setAttribute("cancelledPercentage", 0);
            }

            System.out.println("BillingServlet: Statistics set - Pending: " + pendingCount + 
                             ", Completed: " + completedCount + ", Cancelled: " + cancelledCount + 
                             ", Revenue: " + totalRevenue);

        } catch (Exception e) {
            System.err.println("BillingServlet: Error calculating statistics: " + e.getMessage());
            e.printStackTrace();
            
            // Set default values if calculation fails
            request.setAttribute("pendingBillingsCount", 0);
            request.setAttribute("completedBillingsCount", 0);
            request.setAttribute("cancelledBillingsCount", 0);
            request.setAttribute("totalRevenue", BigDecimal.ZERO);
            request.setAttribute("pendingPercentage", 0);
            request.setAttribute("completedPercentage", 0);
            request.setAttribute("cancelledPercentage", 0);
        }
    }

    // ... (include all other methods from the previous servlet)
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // For now, redirect POST to GET for debugging
        String action = request.getParameter("action");
        System.out.println("BillingServlet POST: Received action = " + action);
        System.out.println("BillingServlet POST: Redirecting to GET for debugging");
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
    }

    /**
     * Find client - simplified for debugging
     */
    private void handleFindClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        String searchValue = request.getParameter("searchValue");
        System.out.println("BillingServlet: Finding client with search value: " + searchValue);
        
        try (PrintWriter out = response.getWriter()) {
            if (searchValue == null || searchValue.trim().isEmpty()) {
                out.print("ERROR:Search value is required");
                return;
            }

            // For debugging, just return a mock client
            out.print("SUCCESS:1|ACC-2025-0001|John Doe|john@email.com|0771234567|100|Bronze|5");
            
        } catch (Exception e) {
            System.err.println("BillingServlet: Error finding client: " + e.getMessage());
            try (PrintWriter out = response.getWriter()) {
                out.print("ERROR:Error finding client: " + e.getMessage());
            }
        }
    }

    /**
     * Find book - simplified for debugging
     */
    private void handleFindBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        String isbn = request.getParameter("isbn");
        System.out.println("BillingServlet: Finding book with ISBN: " + isbn);
        
        try (PrintWriter out = response.getWriter()) {
            if (isbn == null || isbn.trim().isEmpty()) {
                out.print("ERROR:ISBN is required");
                return;
            }

            // For debugging, just return a mock book
            out.print("SUCCESS:1|Sample Book|Sample Author|978-1234567890|1500.00|10|Fiction|Sample Publisher");
            
        } catch (Exception e) {
            System.err.println("BillingServlet: Error finding book: " + e.getMessage());
            try (PrintWriter out = response.getWriter()) {
                out.print("ERROR:Error finding book: " + e.getMessage());
            }
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage) 
            throws ServletException, IOException {
        System.err.println("BillingServlet: Handling error: " + errorMessage);
        
        // Set empty attributes to prevent JSP errors
        request.setAttribute("billings", new ArrayList<BillingDTO>());
        request.setAttribute("totalBillings", 0);
        request.setAttribute("pendingBillingsCount", 0);
        request.setAttribute("completedBillingsCount", 0);
        request.setAttribute("cancelledBillingsCount", 0);
        request.setAttribute("totalRevenue", BigDecimal.ZERO);
        request.setAttribute("errorMessage", errorMessage);
        
        try {
            request.getRequestDispatcher("views/billing.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("BillingServlet: Error forwarding to JSP: " + e.getMessage());
            // Last resort - send simple HTML response
            response.setContentType("text/html");
            try (PrintWriter out = response.getWriter()) {
                out.println("<html><body>");
                out.println("<h1>Billing System Error</h1>");
                out.println("<p>" + errorMessage + "</p>");
                out.println("<p>JSP Error: " + e.getMessage() + "</p>");
                out.println("<a href='" + request.getContextPath() + "/TestDataServlet'>Test Database</a>");
                out.println("</body></html>");
            }
        }
    }

    // Placeholder methods for other actions
    private void handleViewBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("BillingServlet: handleViewBilling() - Not implemented yet");
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
    }

    private void handleSearchBillings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("BillingServlet: handleSearchBillings() - Not implemented yet");
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
    }

    private void handlePrintBilling(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("BillingServlet: handlePrintBilling() - Not implemented yet");
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
    }
}