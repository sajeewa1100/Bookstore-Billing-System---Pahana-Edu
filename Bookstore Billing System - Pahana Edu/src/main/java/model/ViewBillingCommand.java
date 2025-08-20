package model;


import service.BillingService;
import service.ClientService;
import service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

class ViewBillingCommand implements BillingCommand {
    
    private static final Logger LOGGER = Logger.getLogger(ViewBillingCommand.class.getName());
    private BillingService billingService;
    
    public ViewBillingCommand(BillingService billingService) {
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
            BillingDTO bill = billingService.getBillById(billId);
            
            if (bill == null) {
                throw new IllegalArgumentException("Bill not found with ID: " + billId);
            }
            
            request.setAttribute("bill", bill);
            LOGGER.info("ViewBillingCommand: Viewing bill: " + bill.getBillNumber());
            
            // Forward to bill view page
            request.getRequestDispatcher("/views/view-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error viewing bill", e);
            request.getSession().setAttribute("errorMessage", "Error viewing bill: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
        }
    }
}
