package service;

import model.InvoiceDTO;
import model.ClientDTO;
import model.InvoiceItemDTO;
import model.BookDTO;
import dao.BookDAO;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class EmailService {
    
    // Email configuration - These should be in a properties file or environment variables
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "pathumsrathnayake@gmail.com";
    private static final String EMAIL_PASSWORD = "bgmghfsaisidgvri"; // Use App Password, not regular password
    private static final String FROM_EMAIL = "pathumsrathnayake@gmail.com";
    private static final String FROM_NAME = "Sajeewa";
    
    private BookDAO bookDAO;
    private DecimalFormat currencyFormatter;
    
    public EmailService() {
        this.bookDAO = new BookDAO();
        this.currencyFormatter = new DecimalFormat("#,##0.00");
    }
    
    /**
     * Send invoice email to client with enhanced error handling
     */
    public boolean sendInvoiceEmail(InvoiceDTO invoice, ClientDTO client) {
        System.out.println("=== EMAIL SERVICE DEBUG ===");
        System.out.println("EmailService.sendInvoiceEmail called for invoice " + 
                          invoice.getInvoiceNumber() + " to " + client.getEmail());
        
        try {
            // Enhanced validation
            if (invoice == null) {
                System.out.println("ERROR: Invoice is null");
                return false;
            }
            
            if (client == null) {
                System.out.println("ERROR: Client is null");
                return false;
            }
            
            if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
                System.out.println("ERROR: Client email is empty or null");
                return false;
            }
            
            System.out.println("Client email: " + client.getEmail());
            System.out.println("Auto-email enabled: " + client.isSendMailAuto());
            
            if (!client.isSendMailAuto()) {
                System.out.println("INFO: Client has auto-email disabled - skipping email");
                return false;
            }
            
            // Test email configuration first
            System.out.println("Testing email configuration...");
            if (!testEmailConfiguration()) {
                System.out.println("ERROR: Email configuration test failed");
                return false;
            }
            System.out.println("Email configuration test passed");
            
            // Create email session
            System.out.println("Creating email session...");
            Session session = createEmailSession();
            if (session == null) {
                System.out.println("ERROR: Failed to create email session");
                return false;
            }
            System.out.println("Email session created successfully");
            
            // Create email message
            System.out.println("Creating email message...");
            Message message = createInvoiceEmailMessage(session, invoice, client);
            System.out.println("Email message created successfully");
            
            // Send email with detailed logging
            System.out.println("Sending email...");
            System.out.println("From: " + FROM_EMAIL);
            System.out.println("To: " + client.getEmail());
            System.out.println("Subject: Invoice " + invoice.getInvoiceNumber() + " - " + FROM_NAME);
            
            Transport.send(message);
            
            System.out.println("SUCCESS: Invoice email sent successfully to: " + client.getEmail());
            System.out.println("=== EMAIL SERVICE DEBUG END ===");
            return true;
            
        } catch (AuthenticationFailedException e) {
            System.out.println("ERROR: Email authentication failed!");
            System.out.println("Check your email credentials and app password");
            System.out.println("Error details: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } catch (MessagingException e) {
            System.out.println("ERROR: Email messaging error occurred");
            System.out.println("Error type: " + e.getClass().getSimpleName());
            System.out.println("Error message: " + e.getMessage());
            
            // Check for specific error types
            if (e.getMessage() != null) {
                String errorMsg = e.getMessage().toLowerCase();
                if (errorMsg.contains("authentication")) {
                    System.out.println("SUGGESTION: Check your email credentials and enable 2FA with App Password");
                } else if (errorMsg.contains("connection")) {
                    System.out.println("SUGGESTION: Check your internet connection and SMTP settings");
                } else if (errorMsg.contains("ssl") || errorMsg.contains("tls")) {
                    System.out.println("SUGGESTION: Check SSL/TLS settings");
                }
            }
            
            e.printStackTrace();
            return false;
            
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error occurred while sending email");
            System.out.println("Error type: " + e.getClass().getSimpleName());
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Enhanced email session creation with better error handling
     */
    private Session createEmailSession() {
        try {
            System.out.println("Configuring SMTP properties...");
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            
            // Additional properties for better compatibility
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.connectiontimeout", "10000");
            
            // Enable debug mode for more detailed logging
            props.put("mail.debug", "true");
            
            System.out.println("SMTP Host: " + SMTP_HOST);
            System.out.println("SMTP Port: " + SMTP_PORT);
            System.out.println("Username: " + EMAIL_USERNAME);
            System.out.println("Password length: " + (EMAIL_PASSWORD != null ? EMAIL_PASSWORD.length() : 0));
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
            
            // Enable debug output for the session
            session.setDebug(true);
            
            return session;
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to create email session");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create the email message with invoice details
     */
    private Message createInvoiceEmailMessage(Session session, InvoiceDTO invoice, ClientDTO client) 
            throws MessagingException, UnsupportedEncodingException {
        
        Message message = new MimeMessage(session);
        
        // Set sender
        message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
        
        // Set recipient
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(client.getEmail()));
        
        // Set subject
        String subject = "Invoice " + invoice.getInvoiceNumber() + " - " + FROM_NAME;
        message.setSubject(subject);
        
        // Create email body
        String emailBody = createInvoiceEmailBody(invoice, client);
        message.setContent(emailBody, "text/html; charset=utf-8");
        
        return message;
    }
    
    /**
     * Create HTML email body with invoice details
     */
    private String createInvoiceEmailBody(InvoiceDTO invoice, ClientDTO client) {
        StringBuilder html = new StringBuilder();
        
        // Email header
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='utf-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".header { background-color: #f8f9fa; padding: 20px; border-bottom: 3px solid #007bff; }");
        html.append(".invoice-info { margin: 20px 0; }");
        html.append(".items-table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append(".items-table th, .items-table td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
        html.append(".items-table th { background-color: #f8f9fa; font-weight: bold; }");
        html.append(".total-section { margin: 20px 0; padding: 15px; background-color: #f8f9fa; border-left: 4px solid #007bff; }");
        html.append(".footer { margin-top: 30px; padding: 20px; background-color: #f8f9fa; color: #666; font-size: 12px; }");
        html.append(".amount { font-weight: bold; }");
        html.append("</style>");
        html.append("</head><body>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>").append(FROM_NAME).append("</h1>");
        html.append("<h2>Invoice ").append(invoice.getInvoiceNumber()).append("</h2>");
        html.append("</div>");
        
        // Greeting
        html.append("<p>Dear ").append(client.getFullName()).append(",</p>");
        html.append("<p>Thank you for your purchase! Please find your invoice details below.</p>");
        
        // Invoice information
        html.append("<div class='invoice-info'>");
        html.append("<table>");
        html.append("<tr><td><strong>Invoice Number:</strong></td><td>").append(invoice.getInvoiceNumber()).append("</td></tr>");
        html.append("<tr><td><strong>Customer:</strong></td><td>").append(client.getFullName()).append("</td></tr>");
        html.append("<tr><td><strong>Account Number:</strong></td><td>").append(client.getAccountNumber()).append("</td></tr>");
        html.append("</table>");
        html.append("</div>");
        
        // Items table
        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            html.append("<h3>Items Purchased</h3>");
            html.append("<table class='items-table'>");
            html.append("<thead>");
            html.append("<tr>");
            html.append("<th>Item</th>");
            html.append("<th>Quantity</th>");
            html.append("<th>Unit Price</th>");
            html.append("<th>Total</th>");
            html.append("</tr>");
            html.append("</thead>");
            html.append("<tbody>");
            
            for (InvoiceItemDTO item : invoice.getItems()) {
                BookDTO book = bookDAO.findById(item.getBookId());
                String bookTitle = (book != null) ? book.getTitle() : "Book ID: " + item.getBookId();
                String bookAuthor = (book != null && book.getAuthor() != null) ? " by " + book.getAuthor() : "";
                
                html.append("<tr>");
                html.append("<td>").append(bookTitle).append(bookAuthor).append("</td>");
                html.append("<td>").append(item.getQuantity()).append("</td>");
                html.append("<td>$").append(currencyFormatter.format(item.getUnitPrice())).append("</td>");
                html.append("<td class='amount'>$").append(currencyFormatter.format(item.getTotalPrice())).append("</td>");
                html.append("</tr>");
            }
            
            html.append("</tbody>");
            html.append("</table>");
        }
        
        // Totals section
        html.append("<div class='total-section'>");
        html.append("<h3>Invoice Summary</h3>");
        html.append("<table>");
        html.append("<tr><td><strong>Subtotal:</strong></td><td class='amount'>$").append(
            currencyFormatter.format(invoice.getSubtotal() != null ? invoice.getSubtotal() : BigDecimal.ZERO)
        ).append("</td></tr>");
        
        if (invoice.getLoyaltyDiscount() != null && invoice.getLoyaltyDiscount().compareTo(BigDecimal.ZERO) > 0) {
            html.append("<tr><td><strong>Loyalty Discount:</strong></td><td class='amount'>-$").append(
                currencyFormatter.format(invoice.getLoyaltyDiscount())
            ).append("</td></tr>");
        }
        
        html.append("<tr><td><strong>Total Amount:</strong></td><td class='amount'>$").append(
            currencyFormatter.format(invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO)
        ).append("</td></tr>");
        
        if (invoice.getLoyaltyPointsEarned() > 0) {
            html.append("<tr><td><strong>Loyalty Points Earned:</strong></td><td class='amount'>").append(
                invoice.getLoyaltyPointsEarned()
            ).append(" points</td></tr>");
        }
        
        if (invoice.getCashGiven() != null && invoice.getCashGiven().compareTo(BigDecimal.ZERO) > 0) {
            html.append("<tr><td><strong>Cash Given:</strong></td><td class='amount'>$").append(
                currencyFormatter.format(invoice.getCashGiven())
            ).append("</td></tr>");
            
            if (invoice.getChangeAmount() != null && invoice.getChangeAmount().compareTo(BigDecimal.ZERO) > 0) {
                html.append("<tr><td><strong>Change:</strong></td><td class='amount'>$").append(
                    currencyFormatter.format(invoice.getChangeAmount())
                ).append("</td></tr>");
            }
        }
        
        html.append("</table>");
        html.append("</div>");
        
        // Closing message
        html.append("<p>Thank you for your business! We appreciate your loyalty.</p>");
        
        if (client.getLoyaltyPoints() > 0) {
            html.append("<p><strong>Your current loyalty points balance: ").append(client.getLoyaltyPoints()).append(" points</strong></p>");
        }
        
        // Footer
        html.append("<div class='footer'>");
        html.append("<p>This is an automated email. Please do not reply to this message.</p>");
        html.append("<p>If you have any questions about this invoice, please contact us.</p>");
        html.append("<p><strong>").append(FROM_NAME).append("</strong></p>");
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }
    
    /**
     * Enhanced email configuration test
     */
    public boolean testEmailConfiguration() {
        try {
            System.out.println("=== TESTING EMAIL CONFIGURATION ===");
            System.out.println("SMTP Host: " + SMTP_HOST);
            System.out.println("SMTP Port: " + SMTP_PORT);
            System.out.println("Email Username: " + EMAIL_USERNAME);
            System.out.println("Password provided: " + (EMAIL_PASSWORD != null && !EMAIL_PASSWORD.isEmpty()));
            
            Session session = createEmailSession();
            if (session == null) {
                System.out.println("Failed to create email session");
                return false;
            }
            
            // Try to connect to the transport
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, EMAIL_USERNAME, EMAIL_PASSWORD);
            transport.close();
            
            System.out.println("Email configuration test PASSED");
            System.out.println("=== EMAIL CONFIGURATION TEST END ===");
            return true;
            
        } catch (AuthenticationFailedException e) {
            System.out.println("AUTHENTICATION FAILED!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("Make sure you're using an App Password, not your regular Gmail password");
            return false;
            
        } catch (Exception e) {
            System.out.println("Email configuration test FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Send a simple test email
     */
    public boolean sendTestEmail(String toEmail) {
        try {
            System.out.println("Sending test email to: " + toEmail);
            
            Session session = createEmailSession();
            if (session == null) {
                return false;
            }
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Test Email from " + FROM_NAME);
            message.setText("This is a test email to verify email configuration is working correctly.");
            
            Transport.send(message);
            
            System.out.println("Test email sent successfully!");
            return true;
            
        } catch (Exception e) {
            System.out.println("Failed to send test email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}