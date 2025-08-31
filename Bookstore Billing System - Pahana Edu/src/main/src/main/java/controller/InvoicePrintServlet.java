package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import service.BillingService;
import service.InvoicePDFGenerator;
import model.*;


@WebServlet("/print-invoice")
public class InvoicePrintServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BillingService billingService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.billingService = new BillingService();
        System.out.println("InvoicePrintServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String invoiceIdParam = request.getParameter("id");

        if (invoiceIdParam == null || invoiceIdParam.trim().isEmpty()) {
            System.err.println("Invoice ID parameter is missing");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invoice ID is required");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdParam);
            System.out.println("Generating PDF for invoice ID: " + invoiceId);

            // Get invoice data from BillingService
            InvoiceDTO invoiceDTO = billingService.getInvoiceById(invoiceId);

            if (invoiceDTO == null) {
                System.err.println("Invoice not found for ID: " + invoiceId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invoice not found");
                return;
            }

            System.out.println("Invoice found: " + invoiceDTO.getInvoiceNumber());

            // Load logo as InputStream (most reliable for web apps)
            InputStream logoStream = null;
            try {
                // Try to load logo from webapp/assets/Logo.png
                logoStream = getServletContext().getResourceAsStream("/assets/Logo.png");
                
                if (logoStream == null) {
                    // Try alternative case
                    System.out.println("Trying alternative logo path...");
                    logoStream = getServletContext().getResourceAsStream("/assets/logo.png");
                }
                
                if (logoStream == null) {
                    // Try without leading slash
                    System.out.println("Trying logo path without leading slash...");
                    logoStream = getServletContext().getResourceAsStream("assets/Logo.png");
                }

                if (logoStream != null) {
                    System.out.println("Logo loaded successfully as InputStream");
                } else {
                    System.out.println("Logo not found, will use placeholder in PDF");
                }

            } catch (Exception e) {
                System.err.println("Error loading logo: " + e.getMessage());
                logoStream = null;
            }

            // Generate PDF with logo stream
            InvoicePDFGenerator.generateInvoicePDFWithStream(invoiceDTO, response, logoStream);

            System.out.println("PDF generated successfully for invoice: " + invoiceId);

        } catch (NumberFormatException e) {
            System.err.println("Invalid invoice ID format: " + invoiceIdParam);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid invoice ID format");

        } catch (Exception e) {
            System.err.println("Error generating PDF for invoice: " + invoiceIdParam);
            e.printStackTrace();

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to generate invoice PDF: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect POST requests to GET
        doGet(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("InvoicePrintServlet destroyed");
        super.destroy();
    }
}