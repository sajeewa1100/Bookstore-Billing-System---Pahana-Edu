package controller;

import service.BillingService;
import service.ClientService;
import service.EmailService;
import model.InvoiceDTO;
import model.ClientDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/send-invoice-email"})
public class EmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private BillingService billingService;
    private ClientService clientService;
    private EmailService emailService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        billingService = new BillingService();
        clientService = new ClientService();
        emailService = new EmailService();
        System.out.println("EmailServlet initialized");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("sendInvoiceEmail".equals(action)) {
            handleSendInvoiceEmail(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/billing");
        }
    }
    
    private void handleSendInvoiceEmail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("EmailServlet.handleSendInvoiceEmail called");
        
        try {
            String invoiceIdStr = request.getParameter("invoiceId");
            
            if (invoiceIdStr == null || invoiceIdStr.isEmpty()) {
                System.out.println("No invoice ID provided for email");
                request.getSession().setAttribute("errorMessage", "Invoice ID is required for email");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            int invoiceId = Integer.parseInt(invoiceIdStr);
            System.out.println("Processing email for invoice ID: " + invoiceId);
            
            // Get the invoice details
            InvoiceDTO invoice = billingService.getInvoiceById(invoiceId);
            if (invoice == null) {
                System.out.println("Invoice not found: " + invoiceId);
                request.getSession().setAttribute("errorMessage", "Invoice not found");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            // Check if invoice has a client (not walk-in customer)
            if (invoice.getClientId() <= 0) {
                System.out.println("No client associated with invoice " + invoiceId + " - cannot send email");
                request.getSession().setAttribute("errorMessage", "Cannot send email: This is a walk-in customer invoice");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            // Get client details
            ClientDTO client = clientService.getClientById(invoice.getClientId());
            if (client == null) {
                System.out.println("Client not found for invoice " + invoiceId);
                request.getSession().setAttribute("errorMessage", "Client not found for this invoice");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            // Check if client has email and auto-send enabled
            if (!client.isSendMailAuto()) {
                System.out.println("Client " + client.getFullName() + " has auto-email disabled");
                request.getSession().setAttribute("errorMessage", 
                    "Cannot send email: " + client.getFullName() + " has automatic email disabled");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
                System.out.println("Client " + client.getFullName() + " has no email address");
                request.getSession().setAttribute("errorMessage", 
                    "Cannot send email: " + client.getFullName() + " has no email address");
                response.sendRedirect(request.getContextPath() + "/billing");
                return;
            }
            
            // Send the email
            System.out.println("Sending invoice email to: " + client.getEmail());
            boolean emailSent = emailService.sendInvoiceEmail(invoice, client);
            
            if (emailSent) {
                System.out.println("Invoice email sent successfully to " + client.getEmail());
                request.getSession().setAttribute("successMessage", 
                    "Invoice emailed successfully to " + client.getFullName() + " (" + client.getEmail() + ")");
            } else {
                System.out.println("Failed to send invoice email to " + client.getEmail());
                request.getSession().setAttribute("errorMessage", 
                    "Failed to send email to " + client.getFullName() + ". Please try again.");
            }
            
            // Redirect back to billing dashboard
            response.sendRedirect(request.getContextPath() + "/billing");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid invoice ID format: " + request.getParameter("invoiceId"));
            request.getSession().setAttribute("errorMessage", "Invalid invoice ID");
            response.sendRedirect(request.getContextPath() + "/billing");
        } catch (Exception e) {
            System.out.println("Error sending invoice email: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error sending email: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/billing");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect GET requests to billing page
        response.sendRedirect(request.getContextPath() + "/billing");
    }
}