package model;


import service.BillingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Command to view single billing
 */
class ViewSingleBillingCommand implements BillingCommand {
    
    private BillingService billingService;
    
    public ViewSingleBillingCommand(BillingService billingService) {
        this.billingService = billingService;
    }
    
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Bill ID is required");
            }
            
            Long billId = Long.parseLong(idStr);
            BillingDTO billing = billingService.getBillById(billId);
            
            if (billing == null) {
                request.getSession().setAttribute("errorMessage", "Bill not found");
                response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
                return;
            }
            
            System.out.println("ViewSingleBillingCommand: Viewing bill - " + billing.getBillNumber());
            
            request.setAttribute("billing", billing);
            request.getRequestDispatcher("views/view-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("ViewSingleBillingCommand: Error - " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error viewing bill: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
        }
    }
}