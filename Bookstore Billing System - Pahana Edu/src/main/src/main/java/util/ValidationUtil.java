package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simplified validation utility class for user input validation
 */
public class ValidationUtil {

    // Simplified regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,30}$"
    );
    
    // Relaxed password pattern - just check for basic requirements
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s'-]{2,50}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[\\+]?[1-9]?[0-9]{7,15}$"
    );

    // Constants
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 30;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;

    /**
     * Simple validation result class
     */
    public static class ValidationResult {
        private List<String> errors;
        private boolean valid;

        public ValidationResult() {
            this.errors = new ArrayList<>();
            this.valid = true;
        }

        public void addError(String error) {
            this.errors.add(error);
            this.valid = false;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public String getFirstError() {
            return errors.isEmpty() ? null : errors.get(0);
        }

        public String getAllErrors() {
            return String.join("; ", errors);
        }

        @Override
        public String toString() {
            return "ValidationResult{valid=" + valid + ", errors=" + errors + "}";
        }
    }

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate username
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        username = username.trim();
        return username.length() >= MIN_USERNAME_LENGTH && 
               username.length() <= MAX_USERNAME_LENGTH &&
               USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * FIXED: Simplified password validation
     * Must be at least 8 characters with uppercase, lowercase, and number
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Check length first
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }
        
        // Check for at least one uppercase, lowercase, and digit
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        return hasUpper && hasLower && hasDigit;
    }

    /**
     * ALTERNATIVE: Even simpler password validation for testing
     */
    public static boolean isValidPasswordSimple(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Just check minimum length - useful for initial setup/testing
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * Validate name
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        name = name.trim();
        return name.length() >= MIN_NAME_LENGTH && 
               name.length() <= MAX_NAME_LENGTH &&
               NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validate phone number (optional)
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional
        }
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validate role
     */
    public static boolean isValidRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        String normalizedRole = role.trim().toLowerCase();
        return "admin".equals(normalizedRole) || 
               "manager".equals(normalizedRole) || 
               "staff".equals(normalizedRole);
    }

    /**
     * SIMPLIFIED: User setup validation (for first-time setup)
     */
    public static ValidationResult validateUserSetup(String newPassword, String confirmPassword, String email) {
        ValidationResult result = new ValidationResult();

        System.out.println("=== VALIDATION DEBUG ===");
        System.out.println("Password length: " + (newPassword != null ? newPassword.length() : "null"));
        System.out.println("Confirm length: " + (confirmPassword != null ? confirmPassword.length() : "null"));
        System.out.println("Email: " + email);

        // Validate password
        if (newPassword == null || newPassword.isEmpty()) {
            result.addError("Password is required");
        } else if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            result.addError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        } else if (!isValidPassword(newPassword)) {
            result.addError("Password must contain at least one uppercase letter, one lowercase letter, and one number");
        }

        // Validate password confirmation
        if (confirmPassword == null || !confirmPassword.equals(newPassword)) {
            result.addError("Password confirmation does not match");
        }

        // Validate email (optional but must be valid if provided)
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            result.addError("Please enter a valid email address");
        }

        System.out.println("Validation result: " + result.isValid());
        System.out.println("Validation errors: " + result.getErrors());
        System.out.println("=== VALIDATION DEBUG END ===");

        return result;
    }

    /**
     * SIMPLIFIED: Login validation
     */
    public static ValidationResult validateLogin(String username, String password) {
        ValidationResult result = new ValidationResult();

        if (username == null || username.trim().isEmpty()) {
            result.addError("Username is required");
        }

        if (password == null || password.isEmpty()) {
            result.addError("Password is required");
        }

        return result;
    }

    /**
     * SIMPLIFIED: User creation validation
     */
    public static ValidationResult validateUserCreation(String username, String email, String password, 
                                                       String fullName, String phoneNumber, String role) {
        ValidationResult result = new ValidationResult();

        // Validate username
        if (!isValidUsername(username)) {
            if (username == null || username.trim().isEmpty()) {
                result.addError("Username is required");
            } else {
                result.addError("Username must be 3-30 characters and contain only letters, numbers, and underscores");
            }
        }

        // Validate email (optional)
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            result.addError("Please enter a valid email address");
        }

        // Validate password
        if (!isValidPassword(password)) {
            if (password == null || password.isEmpty()) {
                result.addError("Password is required");
            } else if (password.length() < MIN_PASSWORD_LENGTH) {
                result.addError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
            } else {
                result.addError("Password must contain uppercase, lowercase, and number");
            }
        }

        // Validate full name (optional)
        if (fullName != null && !fullName.trim().isEmpty() && !isValidName(fullName)) {
            result.addError("Name must be 2-50 characters with letters, spaces, hyphens, and apostrophes only");
        }

        // Validate phone (optional)
        if (!isValidPhoneNumber(phoneNumber)) {
            result.addError("Please enter a valid phone number");
        }

        // Validate role
        if (!isValidRole(role)) {
            result.addError("Role must be admin, manager, or staff");
        }

        return result;
    }

    /**
     * Get simple password requirements
     */
    public static List<String> getPasswordRequirements() {
        List<String> requirements = new ArrayList<>();
        requirements.add("At least " + MIN_PASSWORD_LENGTH + " characters long");
        requirements.add("At least one uppercase letter");
        requirements.add("At least one lowercase letter");
        requirements.add("At least one number");
        return requirements;
    }

    /**
     * Sanitize input string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
                   .replaceAll("<script[^>]*>.*?</script>", "")
                   .replaceAll("<[^>]*>", "")
                   .replaceAll("[\\r\\n\\t]", " ");
    }

    /**
     * Check if required field is not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}