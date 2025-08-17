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
 * Command to delete billing
 */
class DeleteBillingCommand implements BillingCommand {
    
    private BillingService billingService;
    
    public DeleteBillingCommand(BillingService billingService) {
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
            boolean success = billingService.deleteBill(billId);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Bill deleted successfully");
                System.out.println("DeleteBillingCommand: Bill deleted - ID: " + billId);
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete bill");
            }
            
        } catch (Exception e) {
            System.err.println("DeleteBillingCommand: Error - " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error deleting bill: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
    }
}