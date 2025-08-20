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
 * Command to complete billing
 */
class CompleteBillingCommand implements BillingCommand {
    
    private BillingService billingService;
    
    public CompleteBillingCommand(BillingService billingService) {
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
            boolean success = billingService.completeBill(billId);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Bill completed successfully");
                System.out.println("CompleteBillingCommand: Bill completed - ID: " + billId);
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to complete bill");
            }
            
        } catch (Exception e) {
            System.err.println("CompleteBillingCommand: Error - " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error completing bill: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
    }
}
