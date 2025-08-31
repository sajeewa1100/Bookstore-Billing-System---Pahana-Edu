package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Enhanced utility class for password operations including hashing, verification, 
 * strength checking, and temporary password generation
 */
public class PasswordUtils {
    
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final int SALT_LENGTH = 32; // 32 bytes = 256 bits
    private static final int ITERATIONS = 100000; // Multiple iterations for security
    
    /**
     * Hash a password using SHA-256 with salt and iterations
     * Format: iterations:salt:hash
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt and iterations
            byte[] hash = hashWithSaltAndIterations(password, salt, ITERATIONS);
            
            // Encode salt and hash to Base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);
            
            // Store format: iterations:salt:hash
            String result = ITERATIONS + ":" + saltBase64 + ":" + hashBase64;
            
            System.out.println("=== HASH DEBUG ===");
            System.out.println("Original password: " + password);
            System.out.println("Salt (Base64): " + saltBase64);
            System.out.println("Hash (Base64): " + hashBase64);
            System.out.println("Final stored string: " + result);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Verify a password against its stored hash
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            System.out.println("=== VERIFY DEBUG ===");
            System.out.println("Null input - password: " + (password == null ? "null" : "provided"));
            System.out.println("Null input - hash: " + (storedHash == null ? "null" : "provided"));
            return false;
        }
        
        try {
            System.out.println("=== VERIFY DEBUG ===");
            System.out.println("Input password: '" + password + "'");
            System.out.println("Stored hash: '" + storedHash + "'");
            
            // Parse stored hash format: iterations:salt:hash
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                System.out.println("Invalid hash format - expected 3 parts, got: " + parts.length);
                return false;
            }
            
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[2]);
            
            System.out.println("Parsed iterations: " + iterations);
            System.out.println("Parsed salt length: " + salt.length);
            System.out.println("Parsed hash length: " + storedHashBytes.length);
            
            // Hash the input password with the same salt and iterations
            byte[] inputHash = hashWithSaltAndIterations(password, salt, iterations);
            
            // Compare hashes
            boolean result = MessageDigest.isEqual(storedHashBytes, inputHash);
            System.out.println("Hash comparison result: " + result);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Hash password with salt and iterations using SHA-256
     * @throws UnsupportedEncodingException 
     */
    private static byte[] hashWithSaltAndIterations(String password, byte[] salt, int iterations) 
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        // Combine password and salt
        byte[] passwordBytes = password.getBytes("UTF-8");
        byte[] combined = new byte[passwordBytes.length + salt.length];
        System.arraycopy(passwordBytes, 0, combined, 0, passwordBytes.length);
        System.arraycopy(salt, 0, combined, passwordBytes.length, salt.length);
        
        // Apply multiple iterations
        byte[] hash = combined;
        for (int i = 0; i < iterations; i++) {
            digest.reset();
            hash = digest.digest(hash);
        }
        
        return hash;
    }
    
    /**
     * Generate a temporary password
     */
    public static String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(TEMP_PASSWORD_LENGTH);
        
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARSET.length());
            password.append(CHARSET.charAt(index));
        }
        
        return password.toString();
    }
    
    /**
     * Test method to verify implementation
     */
    public static void testImplementation() {
        System.out.println("=== SHA-256 PASSWORD UTILS TEST ===");
        
        String testPassword = "admin123";
        System.out.println("Test password: " + testPassword);
        
        // Hash the password
        String hashedPassword = hashPassword(testPassword);
        System.out.println("Hashed password: " + hashedPassword);
        
        // Verify the password
        boolean verification1 = verifyPassword(testPassword, hashedPassword);
        System.out.println("Verification test 1 (correct password): " + verification1);
        
        // Test with wrong password
        boolean verification2 = verifyPassword("wrongpassword", hashedPassword);
        System.out.println("Verification test 2 (wrong password): " + verification2);
        
        // Test with your actual stored hash
        String actualStoredHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye.K8z1qc2hRZoNRs1e3mQ5j4M5w9H4Oa";
        System.out.println("Testing with actual stored hash...");
        boolean verification3 = verifyPassword(testPassword, actualStoredHash);
        System.out.println("Verification with stored BCrypt hash: " + verification3);
    }
}