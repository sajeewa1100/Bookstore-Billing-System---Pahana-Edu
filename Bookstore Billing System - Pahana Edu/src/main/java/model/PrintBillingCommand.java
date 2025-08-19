package model;


import service.BillingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Command to print billing
 */
class PrintBillingCommand implements BillingCommand {
    
    private BillingService billingService;
    
    public PrintBillingCommand(BillingService billingService) {
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
            String printableHtml = billingService.generatePrintableBill(billId);
            
            System.out.println("PrintBillingCommand: Generating printable bill for ID: " + billId);
            
            request.setAttribute("printableHtml", printableHtml);
            request.getRequestDispatcher("views/print-billing.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("PrintBillingCommand: Error - " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error generating printable bill: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/BillingServlet?action=billings");
        }
    }
}