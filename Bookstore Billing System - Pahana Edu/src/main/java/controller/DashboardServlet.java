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
import service.BookService;
import model.BookDTO;

/**
 * Dashboard Servlet - Handles dashboard operations and data loading
 */
@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookService bookService;

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("🚀 DashboardServlet: Initializing...");
        bookService = new BookService();
        System.out.println("✅ DashboardServlet: BookService initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("🔄 DashboardServlet: Processing action = " + action);

        // Ensure user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp?error=Please login to access this page");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        // Set common attributes for all pages
        request.setAttribute("currentUser", user);
        request.setAttribute("isManager", "manager".equals(user.getRole()));

        if (action == null || action.trim().isEmpty()) {
            action = "dashboard";
        }

        try {
            switch (action.toLowerCase()) {
                case "dashboard":
                    handleDashboard(request, response);
                    break;
                default:
                    System.out.println("⚠️ DashboardServlet: Unknown action, defaulting to dashboard");
                    handleDashboard(request, response);
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ DashboardServlet: Error processing request: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "An error occurred while loading the dashboard");
        }
    }

    /**
     * Handle dashboard page with summary data
     */
    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📊 DashboardServlet: Loading dashboard data");

        try {
            // Load dashboard statistics
            int totalBooks = bookService.getTotalBooksCount();
            List<String> categories = bookService.getBookCategories();
            List<BookDTO> lowStockBooks = bookService.getLowStockBooks(5); // Books with stock <= 5
            List<BookDTO> recentBooks = bookService.getAllBooks(); // You can limit this if needed

            // Set attributes for JSP
            request.setAttribute("totalBooks", totalBooks);
            request.setAttribute("totalCategories", categories.size());
            request.setAttribute("categories", categories);
            request.setAttribute("lowStockBooks", lowStockBooks);
            request.setAttribute("lowStockCount", lowStockBooks.size());
            request.setAttribute("recentBooks", recentBooks);

            // Check for success message from redirects
            String successMessage = request.getParameter("success");
            if (successMessage != null && !successMessage.trim().isEmpty()) {
                request.setAttribute("successMessage", successMessage);
            }

            System.out.println("📊 DashboardServlet: Dashboard data loaded successfully");
            System.out.println("📊 Total Books: " + totalBooks + ", Categories: " + categories.size());
            
            // FIXED: Forward to dashboard JSP instead of login.jsp
            request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ DashboardServlet: Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading dashboard data");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔄 DashboardServlet: POST request - delegating to GET");
        doGet(request, response);
    }

    /**
     * Handle errors by forwarding to an error page or dashboard with error message
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        System.out.println("❌ DashboardServlet: Handling error: " + errorMessage);
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
    }
}