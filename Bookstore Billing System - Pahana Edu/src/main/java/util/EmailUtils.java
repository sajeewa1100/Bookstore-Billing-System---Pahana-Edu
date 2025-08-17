package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Date;

/**
 * Utility class for sending emails
 * Handles password reset, welcome emails, and system notifications
 */
public class EmailUtils {
    
    // Email configuration - Update these with your SMTP settings
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "your-email@gmail.com"; // Update this
    private static final String EMAIL_PASSWORD = "your-app-password"; // Update this - use App Password for Gmail
    private static final String FROM_EMAIL = "noreply@pahanaedu.com";
    private static final String FROM_NAME = "Pahana Edu Bookstore";
    
    // Email templates
    private static final String PASSWORD_RESET_SUBJECT = "Password Reset Request - Pahana Edu";
    private static final String WELCOME_SUBJECT = "Welcome to Pahana Edu Bookstore";
    private static final String ACCOUNT_CREATED_SUBJECT = "Your Pahana Edu Account";
    
    /**
     * Send password reset email
     */
    public static boolean sendPasswordResetEmail(String recipientEmail, String username, String resetLink) {
        try {
            Session session = createEmailSession();
            MimeMessage message = new MimeMessage(session);
            
            // Set headers
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(PASSWORD_RESET_SUBJECT);
            message.setSentDate(new Date());
            
            // Create email content
            String htmlContent = createPasswordResetEmailContent(username, resetLink);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send email
            Transport.send(message);
            System.out.println("Password reset email sent to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Send welcome email after first-time setup
     */
    public static boolean sendWelcomeEmail(String recipientEmail, String username, String companyName) {
        try {
            Session session = createEmailSession();
            MimeMessage message = new MimeMessage(session);
            
            // Set headers
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(WELCOME_SUBJECT);
            message.setSentDate(new Date());
            
            // Create email content
            String htmlContent = createWelcomeEmailContent(username, companyName);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send email
            Transport.send(message);
            System.out.println("Welcome email sent to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Send new account creation email
     */
    public static boolean sendAccountCreatedEmail(String recipientEmail, String username, String temporaryPassword) {
        try {
            Session session = createEmailSession();
            MimeMessage message = new MimeMessage(session);
            
            // Set headers
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(ACCOUNT_CREATED_SUBJECT);
            message.setSentDate(new Date());
            
            // Create email content
            String htmlContent = createAccountCreatedEmailContent(username, temporaryPassword);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send email
            Transport.send(message);
            System.out.println("Account creation email sent to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to send account creation email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Send system notification email
     */
    public static boolean sendNotificationEmail(String recipientEmail, String subject, String message) {
        try {
            Session session = createEmailSession();
            MimeMessage emailMessage = new MimeMessage(session);
            
            // Set headers
            emailMessage.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            emailMessage.setSubject(subject);
            emailMessage.setSentDate(new Date());
            
            // Create email content
            String htmlContent = createNotificationEmailContent(message);
            emailMessage.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send email
            Transport.send(emailMessage);
            System.out.println("Notification email sent to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to send notification email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create email session with authentication
     */
    private static Session createEmailSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
    }
    
    /**
     * Create HTML content for password reset email
     */
    private static String createPasswordResetEmailContent(String username, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    .warning { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Password Reset Request</h1>
                        <p>Pahana Edu Bookstore Management System</p>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>We received a request to reset your password for your Pahana Edu manager account.</p>
                        <p>Click the button below to reset your password:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Reset Password</a>
                        </p>
                        <div class="warning">
                            <strong>⚠️ Security Notice:</strong>
                            <ul>
                                <li>This link will expire in 1 hour</li>
                                <li>If you didn't request this reset, please ignore this email</li>
                                <li>Never share this link with others</li>
                            </ul>
                        </div>
                        <p>If the button doesn't work, copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; font-family: monospace; background: #f0f0f0; padding: 10px;">%s</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated email from Pahana Edu Bookstore System</p>
                        <p>Please do not reply to this email</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, resetLink, resetLink);
    }
    
    /**
     * Create HTML content for welcome email
     */
    private static String createWelcomeEmailContent(String username, String companyName) {
        String companyInfo = companyName != null && !companyName.trim().isEmpty() 
            ? " for " + companyName : "";
            
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .feature { background: white; padding: 20px; margin: 15px 0; border-radius: 5px; border-left: 4px solid #27ae60; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Welcome to Pahana Edu!</h1>
                        <p>Your account setup is complete</p>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Congratulations! Your Pahana Edu manager account%s has been successfully set up.</p>
                        
                        <h3>🚀 You now have access to:</h3>
                        <div class="feature">
                            <strong>📚 Book Management</strong><br>
                            Add, edit, and track your book inventory with ease
                        </div>
                        <div class="feature">
                            <strong>👥 Client Management</strong><br>
                            Manage customer information and purchase history
                        </div>
                        <div class="feature">
                            <strong>📋 Billing System</strong><br>
                            Create professional invoices and track payments
                        </div>
                        <div class="feature">
                            <strong>📊 Reports & Analytics</strong><br>
                            Monitor sales, revenue, and business performance
                        </div>
                        <div class="feature">
                            <strong>⚙️ Staff Management</strong><br>
                            Create and manage staff accounts (manager only)
                        </div>
                        
                        <h3>🔒 Security Tips:</h3>
                        <ul>
                            <li>Keep your login credentials secure</li>
                            <li>Log out when you're done using the system</li>
                            <li>Regularly update your password</li>
                            <li>Monitor user activity logs</li>
                        </ul>
                        
                        <p><strong>Need help?</strong> Check the system documentation or contact your system administrator.</p>
                    </div>
                    <div class="footer">
                        <p>Thank you for choosing Pahana Edu Bookstore Management System</p>
                        <p>This is an automated email - please do not reply</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, companyInfo);
    }
    
    /**
     * Create HTML content for account creation email
     */
    private static String createAccountCreatedEmailContent(String username, String temporaryPassword) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .credentials { background: white; padding: 20px; border: 2px solid #3498db; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    .warning { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🆕 Your Pahana Edu Account</h1>
                        <p>Account Created Successfully</p>
                    </div>
                    <div class="content">
                        <h2>Welcome to Pahana Edu!</h2>
                        <p>Your staff account has been created by the system administrator. Here are your login credentials:</p>
                        
                        <div class="credentials">
                            <h3>🔑 Login Credentials</h3>
                            <p><strong>Username:</strong> %s</p>
                            <p><strong>Temporary Password:</strong> %s</p>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Important Security Notice:</strong>
                            <ul>
                                <li>Please change your password after your first login</li>
                                <li>Keep your credentials secure and don't share them</li>
                                <li>Contact your administrator if you have any issues</li>
                            </ul>
                        </div>
                        
                        <h3>📋 Getting Started:</h3>
                        <ol>
                            <li>Visit the Pahana Edu login page</li>
                            <li>Enter your username and temporary password</li>
                            <li>Update your password when prompted</li>
                            <li>Start managing books and creating bills!</li>
                        </ol>
                    </div>
                    <div class="footer">
                        <p>Pahana Edu Bookstore Management System</p>
                        <p>This is an automated email - please do not reply</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, temporaryPassword);
    }
    
    /**
     * Create HTML content for notification emails
     */
    private static String createNotificationEmailContent(String message) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📢 System Notification</h1>
                        <p>Pahana Edu Bookstore</p>
                    </div>
                    <div class="content">
                        %s
                    </div>
                    <div class="footer">
                        <p>Pahana Edu Bookstore Management System</p>
                        <p>This is an automated email - please do not reply</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(message);
    }
    
    /**
     * Test email configuration
     */
    public static boolean testEmailConfiguration() {
        try {
            Session session = createEmailSession();
            Transport transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, EMAIL_USERNAME, EMAIL_PASSWORD);
            transport.close();
            System.out.println("Email configuration test successful");
            return true;
        } catch (Exception e) {
            System.err.println("Email configuration test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate email address format
     */
    public static boolean isValidEmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
}