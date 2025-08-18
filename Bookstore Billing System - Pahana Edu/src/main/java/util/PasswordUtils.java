package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Utility class for password operations including hashing, verification, and strength checking
 */
public class PasswordUtils {

    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom random = new SecureRandom();

    // Password patterns
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern NUMBERS = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[^A-Za-z0-9]");

    /**
     * Hash a password with salt using SHA-256
     * @param password plain text password
     * @return hashed password with salt
     */
    public static String hashPassword(String password) {
        String salt = generateSalt();
        return hashPasswordWithSalt(password, salt);
    }

    /**
     * Hash password with provided salt
     * @param password plain text password
     * @param salt salt string
     * @return hashed password with salt prefix
     */
    private static String hashPasswordWithSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());

            // Combine salt and hash
            String hash = Base64.getEncoder().encodeToString(hashedPassword);
            return salt + ":" + hash;  // The salt and the hash are combined with ':' separator
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verify password against stored hash
     * @param password plain text password
     * @param storedHash stored hash with salt
     * @return true if password matches
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null || !storedHash.contains(":")) {
            return false;
        }

        String[] parts = storedHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }

        String salt = parts[0];
        String expectedHash = hashPasswordWithSalt(password, salt);

        return expectedHash.equals(storedHash);
    }

    /**
     * Generate random salt
     * @return random salt string
     */
    private static String generateSalt() {
        StringBuilder salt = new StringBuilder(SALT_LENGTH);
        for (int i = 0; i < SALT_LENGTH; i++) {
            salt.append(SALT_CHARS.charAt(random.nextInt(SALT_CHARS.length())));
        }
        return salt.toString();
    }

    /**
     * Generate random reset token
     * @return secure random token
     */
    public static String generateResetToken() {
        byte[] token = new byte[32];
        random.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    /**
     * Check password strength
     * @param password password to check
     * @return PasswordStrength object with details
     */
    public static PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return new PasswordStrength(true, "Password cannot be empty");
        }

        int score = 0;
        StringBuilder feedback = new StringBuilder();

        // Length check
        if (password.length() >= 8) {
            score += 2;
        } else if (password.length() >= 6) {
            score += 1;
            feedback.append("Use at least 8 characters. ");
        } else {
            feedback.append("Password too short (minimum 6 characters). ");
        }

        // Character variety checks
        if (UPPERCASE.matcher(password).find()) {
            score += 1;
        } else {
            feedback.append("Add uppercase letters. ");
        }

        if (LOWERCASE.matcher(password).find()) {
            score += 1;
        } else {
            feedback.append("Add lowercase letters. ");
        }

        if (NUMBERS.matcher(password).find()) {
            score += 1;
        } else {
            feedback.append("Add numbers. ");
        }

        if (SPECIAL_CHARS.matcher(password).find()) {
            score += 1;
        } else {
            feedback.append("Add special characters. ");
        }

        // Determine strength
        boolean isWeak = score < 4;
        String message = feedback.toString().trim();

        if (score >= 6) {
            message = "Strong password";
        } else if (score >= 4) {
            message = "Medium strength password";
        }

        return new PasswordStrength(isWeak, message);
    }

    /**
     * Password strength result class
     */
    public static class PasswordStrength {
        private final boolean weak;
        private final String message;

        public PasswordStrength(boolean weak, String message) {
            this.weak = weak;
            this.message = message;
        }

        public boolean isWeak() {
            return weak;
        }

        public String getMessage() {
            return message;
        }

        public boolean isStrong() {
            return !weak;
        }
    }
}
