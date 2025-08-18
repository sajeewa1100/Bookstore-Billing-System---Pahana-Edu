package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Date;

public class EmailUtils {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "pathumpc1100@gmail.com";  // Replace with your email
    private static final String EMAIL_PASSWORD = "Sajeewa@#425";    // Replace with your App Password
    private static final String FROM_EMAIL = "noreply@pahanaedu.com";
    private static final String FROM_NAME = "Pahana Edu Bookstore";

    // Send password reset email
    public static boolean sendPasswordResetEmail(String recipientEmail, String username, String resetLink) {
        try {
            Session session = createEmailSession();
            if (session == null) {
                return false;
            }

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Password Reset Request - Pahana Edu");
            message.setSentDate(new Date());
            
            String emailContent = "Dear " + username + ",\n\n" +
                                  "You have requested a password reset. Please click the following link to reset your password:\n\n" +
                                  resetLink + "\n\n" +
                                  "This link will expire in 1 hour.\n\n" +
                                  "If you did not request this, please ignore this email.";
            message.setText(emailContent, "utf-8");

            Transport.send(message);
            System.out.println("✅ Password reset email sent successfully to: " + recipientEmail);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Session createEmailSession() {
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            return Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
