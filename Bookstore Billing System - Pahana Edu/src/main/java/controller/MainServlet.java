package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

import dao.UserDAO;
import model.User;

/**
 * Main Servlet - Entry point for the application
 * Handles routing and session management with proper CSS loading
 */
@WebServlet(urlPatterns = {"/", "/index", "/MainServlet"})
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("🚀 MainServlet: Initializing...");
        
        userDAO = new UserDAO();

        try {
            userDAO.createDefaultAdmin();
            System.out.println("✅ MainServlet: Default admin check completed");
        } catch (SQLException e) {
            System.err.println("❌ MainServlet: Error creating default admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("✅ MainServlet: Initialization complete");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🔄 MainServlet: Processing GET request");
        System.out.println("📍 MainServlet: Request URI = " + request.getRequestURI());
        System.out.println("📍 MainServlet: Context Path = " + request.getContextPath());

        HttpSession session = request.getSession(false);
        System.out.println("🔐 MainServlet: Session exists = " + (session != null));

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            System.out.println("👤 MainServlet: User found = " + user.getUsername() + ", Role = " + user.getRole());

            // Check first login for manager
            if (user.isFirstLogin() && "manager".equals(user.getRole())) {
                System.out.println("🔄 MainServlet: Redirecting to first-time setup");
                response.sendRedirect(request.getContextPath() + "/AuthServlet?action=firstTimeSetup");
                return;
            }

            // Set user attributes for JSP access
            request.setAttribute("currentUser", user);
            request.setAttribute("isManager", "manager".equals(user.getRole()));
            
            // Route based on role
            if ("manager".equals(user.getRole())) {
                System.out.println("🔄 MainServlet: Forwarding manager to dashboard");
                // Forward to dashboard servlet to load data, then display books page
                request.getRequestDispatcher("/DashboardServlet?action=dashboard").forward(request, response);
            } else {
                System.out.println("🔄 MainServlet: Forwarding user to invoices");
                // Forward to billing servlet to load invoices
                request.getRequestDispatcher("/BillingServlet?action=invoices").forward(request, response);
            }
        } else {
            System.out.println("🔄 MainServlet: No authenticated user, forwarding to login");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔄 MainServlet: Processing POST request - delegating to GET");
        doGet(request, response);
    }
}