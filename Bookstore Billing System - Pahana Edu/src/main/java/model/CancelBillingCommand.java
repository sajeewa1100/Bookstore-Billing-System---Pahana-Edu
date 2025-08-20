package model;

import service.BillingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

class CancelBillingCommand implements BillingCommand {
    
    private static final Logger LOGGER = Logger.getLogger(CancelBillingCommand.class.getName());
    private BillingService billingService;
    
    public CancelBillingCommand(BillingService billingService) {
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
            boolean success = billingService.cancelBill(billId);
            
            if (success) {
                request.getSession().setAttribute("successMessage", "Bill cancelled successfully");
                LOGGER.info("CancelBillingCommand: Bill cancelled - ID: " + billId);
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to cancel bill");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling bill", e);
            request.getSession().setAttribute("errorMessage", "Error cancelling bill: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
    }
}