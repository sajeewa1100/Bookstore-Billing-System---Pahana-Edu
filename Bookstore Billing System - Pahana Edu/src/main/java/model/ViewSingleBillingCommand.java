package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.BillingService;

import java.io.IOException;

public class ViewSingleBillingCommand implements BillingCommand {

    private final BillingService billingService;

    public ViewSingleBillingCommand(BillingService billingService) {
        if (billingService == null) {
            throw new IllegalArgumentException("BillingService cannot be null");
        }
        this.billingService = billingService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String billingIdStr = request.getParameter("id");
        
        if (billingIdStr == null || billingIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
            return;
        }

        try {
            Long billingId = Long.parseLong(billingIdStr);
            BillingDTO billing = billingService.getBillingById(billingId);
            
            if (billing == null) {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Billing not found");
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
                return;
            }

            request.setAttribute("billing", billing);
            request.setAttribute("pageTitle", "View Billing - " + billing.getBillNumber());
            request.getRequestDispatcher("views/billing-view.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invalid billing ID");
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error retrieving billing: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
        }
    }
}