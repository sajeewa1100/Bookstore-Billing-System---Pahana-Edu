package model;

import service.BillingService;
import util.PDFBillGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Command to print/download billing as PDF
 */
public class PrintBillingCommand implements BillingCommand {
    
    private static final Logger LOGGER = Logger.getLogger(PrintBillingCommand.class.getName());
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
                throw new IllegalArgumentException("Bill ID is required for printing");
            }
            
            Long billId = Long.parseLong(idStr);
            LOGGER.info("PrintBillingCommand: Generating PDF for bill ID: " + billId);
            
            // Get the bill
            BillingDTO bill = billingService.getBillById(billId);
            if (bill == null) {
                throw new IllegalArgumentException("Bill not found with ID: " + billId);
            }
            
            // Generate PDF
            byte[] pdfBytes = PDFBillGenerator.generateBillPDF(bill);
            
            // Set response headers for PDF download
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", 
                             "attachment; filename=\"Bill_" + bill.getBillNumber() + ".pdf\"");
            response.setContentLength(pdfBytes.length);
            
            // Write PDF to response
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
            
            LOGGER.info("PrintBillingCommand: PDF generated successfully for bill: " + bill.getBillNumber());
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid bill ID format", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid bill ID");
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "PrintBillingCommand error: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating PDF", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Error generating PDF: " + e.getMessage());
        }
    }
}