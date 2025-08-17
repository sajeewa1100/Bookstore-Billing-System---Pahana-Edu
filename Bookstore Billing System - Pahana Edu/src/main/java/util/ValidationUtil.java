package util;

import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Utility class for input validation
 * Provides comprehensive validation methods for user inputs
 */
public class ValidationUtil {
    
    // Regular expression patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,30}$"
    );
    
    private static final Pattern ISBN_PATTERN = Pattern.compile(
        "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$"
    );
    
    // Validation constants
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 30;
    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_PHONE_LENGTH = 20;
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        private Map<String, String> fieldErrors;
        
        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
            this.fieldErrors = new HashMap<>();
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public Map<String, String> getFieldErrors() { return fieldErrors; }
        
        public void addError(String error) {
            this.valid = false;
            this.errors.add(error);
        }
        
        public void addFieldError(String field, String error) {
            this.valid = false;
            this.fieldErrors.put(field, error);
            this.errors.add(field + ": " + error);
        }
        
        public String getFirstError() {
            return errors.isEmpty() ? null : errors.get(0);
        }
    }
    
    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        if (email.length() > MAX_EMAIL_LENGTH) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) return false;
        if (phone.length() > MAX_PHONE_LENGTH) return false;
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validate username
     */
    public static boolean isValidUsername(String username) {
        if (isNullOrEmpty(username)) return false;
        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) return false;
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * Validate password strength
     */
    public static ValidationResult validatePassword(String password) {
        ValidationResult result = new ValidationResult();
        
        if (isNullOrEmpty(password)) {
            result.addFieldError("password", "Password is required");
            return result;
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            result.addFieldError("password", "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            result.addFieldError("password", "Password cannot be longer than " + MAX_PASSWORD_LENGTH + " characters");
        }
        
        // Use PasswordUtils for comprehensive strength checking
        PasswordUtils.PasswordStrength strength = PasswordUtils.checkPasswordStrength(password);
        if (strength.isWeak()) {
            result.addFieldError("password", strength.getMessage());
        }
        
        return result;
    }
    
    /**
     * Validate ISBN
     */
    public static boolean isValidISBN(String isbn) {
        if (isNullOrEmpty(isbn)) return true; // ISBN is optional
        return ISBN_PATTERN.matcher(isbn.replaceAll("[\\s-]", "")).matches();
    }
    
    /**
     * Validate name (person name, company name, book title, etc.)
     */
    public static boolean isValidName(String name) {
        if (isNullOrEmpty(name)) return false;
        if (name.trim().length() > MAX_NAME_LENGTH) return false;
        // Allow letters, numbers, spaces, and common punctuation
        return name.trim().matches("^[\\p{L}\\p{N}\\s.,'\"\\-()&]+$");
    }
    
    /**
     * Validate decimal number (price, cost, etc.)
     */
    public static boolean isValidDecimal(String value) {
        if (isNullOrEmpty(value)) return false;
        try {
            double num = Double.parseDouble(value);
            return num >= 0 && num <= 999999.99;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate integer (quantity, etc.)
     */
    public static boolean isValidInteger(String value) {
        if (isNullOrEmpty(value)) return false;
        try {
            int num = Integer.parseInt(value);
            return num >= 0 && num <= 999999;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Comprehensive user validation
     */
    public static ValidationResult validateUser(String username, String password, String confirmPassword, 
                                               String email, String role) {
        ValidationResult result = new ValidationResult();
        
        // Username validation
        if (isNullOrEmpty(username)) {
            result.addFieldError("username", "Username is required");
        } else if (!isValidUsername(username)) {
            result.addFieldError("username", "Username must be 3-30 characters, letters, numbers, and underscores only");
        }
        
        // Password validation
        ValidationResult passwordResult = validatePassword(password);
        if (!passwordResult.isValid()) {
            result.addFieldError("password", passwordResult.getFirstError().replace("password: ", ""));
        }
        
        // Confirm password validation
        if (isNullOrEmpty(confirmPassword)) {
            result.addFieldError("confirmPassword", "Please confirm your password");
        } else if (!password.equals(confirmPassword)) {
            result.addFieldError("confirmPassword", "Passwords do not match");
        }
        
        // Email validation (optional for some roles)
        if (!isNullOrEmpty(email) && !isValidEmail(email)) {
            result.addFieldError("email", "Please enter a valid email address");
        }
        
        // Role validation
        if (isNullOrEmpty(role) || (!role.equals("manager") && !role.equals("staff"))) {
            result.addFieldError("role", "Valid role is required (manager or staff)");
        }
        
        return result;
    }
    
    /**
     * Comprehensive book validation
     */
    public static ValidationResult validateBook(String title, String author, String isbn, String price, 
                                               String cost, String quantity, String category) {
        ValidationResult result = new ValidationResult();
        
        // Title validation
        if (isNullOrEmpty(title)) {
            result.addFieldError("title", "Book title is required");
        } else if (!isValidName(title)) {
            result.addFieldError("title", "Invalid book title format");
        }
        
        // Author validation
        if (isNullOrEmpty(author)) {
            result.addFieldError("author", "Author name is required");
        } else if (!isValidName(author)) {
            result.addFieldError("author", "Invalid author name format");
        }
        
        // ISBN validation (optional)
        if (!isNullOrEmpty(isbn) && !isValidISBN(isbn)) {
            result.addFieldError("isbn", "Invalid ISBN format");
        }
        
        // Price validation
        if (isNullOrEmpty(price)) {
            result.addFieldError("price", "Price is required");
        } else if (!isValidDecimal(price)) {
            result.addFieldError("price", "Invalid price format");
        } else if (Double.parseDouble(price) <= 0) {
            result.addFieldError("price", "Price must be greater than zero");
        }
        
        // Cost validation
        if (isNullOrEmpty(cost)) {
            result.addFieldError("cost", "Cost is required");
        } else if (!isValidDecimal(cost)) {
            result.addFieldError("cost", "Invalid cost format");
        } else if (Double.parseDouble(cost) < 0) {
            result.addFieldError("cost", "Cost cannot be negative");
        }
        
        // Quantity validation
        if (isNullOrEmpty(quantity)) {
            result.addFieldError("quantity", "Quantity is required");
        } else if (!isValidInteger(quantity)) {
            result.addFieldError("quantity", "Invalid quantity format");
        } else if (Integer.parseInt(quantity) < 0) {
            result.addFieldError("quantity", "Quantity cannot be negative");
        }
        
        // Category validation (optional)
        if (!isNullOrEmpty(category) && !isValidName(category)) {
            result.addFieldError("category", "Invalid category format");
        }
        
        // Business logic validation
        if (!result.getFieldErrors().containsKey("price") && !result.getFieldErrors().containsKey("cost")) {
            double priceValue = Double.parseDouble(price);
            double costValue = Double.parseDouble(cost);
            if (priceValue < costValue) {
                result.addFieldError("price", "Price should typically be higher than cost");
            }
        }
        
        return result;
    }
    
    /**
     * Comprehensive client validation
     */
    public static ValidationResult validateClient(String name, String email, String phone, String address) {
        ValidationResult result = new ValidationResult();
        
        // Name validation
        if (isNullOrEmpty(name)) {
            result.addFieldError("name", "Client name is required");
        } else if (!isValidName(name)) {
            result.addFieldError("name", "Invalid name format");
        }
        
        // Email validation (optional)
        if (!isNullOrEmpty(email) && !isValidEmail(email)) {
            result.addFieldError("email", "Please enter a valid email address");
        }
        
        // Phone validation (optional)
        if (!isNullOrEmpty(phone) && !isValidPhone(phone)) {
            result.addFieldError("phone", "Please enter a valid phone number");
        }
        
        // Address validation (optional)
        if (!isNullOrEmpty(address) && address.trim().length() > 500) {
            result.addFieldError("address", "Address is too long (maximum 500 characters)");
        }
        
        return result;
    }
    
    /**
     * Validate bill creation
     */
    public static ValidationResult validateBill(String clientName, String discount, String tax, 
                                               List<Map<String, String>> items) {
        ValidationResult result = new ValidationResult();
        
        // Client name validation (for walk-in customers)
        if (isNullOrEmpty(clientName)) {
            clientName = "Walk-in Customer"; // Default for anonymous sales
        } else if (!isValidName(clientName)) {
            result.addFieldError("clientName", "Invalid client name format");
        }
        
        // Discount validation (optional)
        if (!isNullOrEmpty(discount)) {
            if (!isValidDecimal(discount)) {
                result.addFieldError("discount", "Invalid discount format");
            } else if (Double.parseDouble(discount) < 0 || Double.parseDouble(discount) > 100) {
                result.addFieldError("discount", "Discount must be between 0 and 100");
            }
        }
        
        // Tax validation (optional)
        if (!isNullOrEmpty(tax)) {
            if (!isValidDecimal(tax)) {
                result.addFieldError("tax", "Invalid tax format");
            } else if (Double.parseDouble(tax) < 0 || Double.parseDouble(tax) > 100) {
                result.addFieldError("tax", "Tax must be between 0 and 100");
            }
        }
        
        // Items validation
        if (items == null || items.isEmpty()) {
            result.addFieldError("items", "At least one item is required");
        } else {
            for (int i = 0; i < items.size(); i++) {
                Map<String, String> item = items.get(i);
                String quantity = item.get("quantity");
                String price = item.get("price");
                
                if (isNullOrEmpty(quantity) || !isValidInteger(quantity) || Integer.parseInt(quantity) <= 0) {
                    result.addFieldError("item" + i + "_quantity", "Invalid quantity for item " + (i + 1));
                }
                
                if (isNullOrEmpty(price) || !isValidDecimal(price) || Double.parseDouble(price) <= 0) {
                    result.addFieldError("item" + i + "_price", "Invalid price for item " + (i + 1));
                }
            }
        }
        
        return result;
    }
    
    /**
     * Sanitize input to prevent XSS
     */
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        
        return input.trim()
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;")
                   .replace("&", "&amp;");
    }
    
    /**
     * Validate search query
     */
    public static boolean isValidSearchQuery(String query) {
        if (isNullOrEmpty(query)) return false;
        if (query.trim().length() < 2) return false;
        if (query.trim().length() > 100) return false;
        // Prevent potential SQL injection patterns
        String[] dangerousPatterns = {"'", "\"", ";", "--", "/*", "*/", "xp_", "sp_"};
        String lowerQuery = query.toLowerCase();
        for (String pattern : dangerousPatterns) {
            if (lowerQuery.contains(pattern)) return false;
        }
        return true;
    }
    
    /**
     * Validate date range
     */
    public static boolean isValidDateRange(String startDate, String endDate) {
        if (isNullOrEmpty(startDate) || isNullOrEmpty(endDate)) return false;
        try {
            // Basic date format validation (YYYY-MM-DD)
            return startDate.matches("\\d{4}-\\d{2}-\\d{2}") && 
                   endDate.matches("\\d{4}-\\d{2}-\\d{2}") &&
                   startDate.compareTo(endDate) <= 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate pagination parameters
     */
    public static ValidationResult validatePagination(String page, String limit) {
        ValidationResult result = new ValidationResult();
        
        // Page validation
        if (isNullOrEmpty(page)) {
            page = "1"; // Default
        } else {
            try {
                int pageNum = Integer.parseInt(page);
                if (pageNum < 1 || pageNum > 10000) {
                    result.addFieldError("page", "Page number must be between 1 and 10000");
                }
            } catch (NumberFormatException e) {
                result.addFieldError("page", "Invalid page number format");
            }
        }
        
        // Limit validation
        if (isNullOrEmpty(limit)) {
            limit = "20"; // Default
        } else {
            try {
                int limitNum = Integer.parseInt(limit);
                if (limitNum < 1 || limitNum > 100) {
                    result.addFieldError("limit", "Limit must be between 1 and 100");
                }
            } catch (NumberFormatException e) {
                result.addFieldError("limit", "Invalid limit format");
            }
        }
        
        return result;
    }
    
    /**
     * Validate file upload
     */
    public static ValidationResult validateFileUpload(String fileName, long fileSize, String contentType) {
        ValidationResult result = new ValidationResult();
        
        // File name validation
        if (isNullOrEmpty(fileName)) {
            result.addFieldError("fileName", "File name is required");
            return result;
        }
        
        // File extension validation (for images, documents, etc.)
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".pdf", ".doc", ".docx", ".xls", ".xlsx"};
        boolean validExtension = false;
        String lowerFileName = fileName.toLowerCase();
        for (String ext : allowedExtensions) {
            if (lowerFileName.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }
        
        if (!validExtension) {
            result.addFieldError("fileName", "File type not allowed. Allowed types: " + String.join(", ", allowedExtensions));
        }
        
        // File size validation (max 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (fileSize > maxSize) {
            result.addFieldError("fileSize", "File size cannot exceed 5MB");
        }
        
        // Content type validation
        String[] allowedContentTypes = {"image/jpeg", "image/png", "image/gif", "application/pdf", 
                                       "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        boolean validContentType = false;
        if (contentType != null) {
            for (String type : allowedContentTypes) {
                if (contentType.startsWith(type)) {
                    validContentType = true;
                    break;
                }
            }
        }
        
        if (!validContentType) {
            result.addFieldError("contentType", "Invalid file content type");
        }
        
        return result;
    }
    
    /**
     * Validate system settings
     */
    public static ValidationResult validateSystemSettings(Map<String, String> settings) {
        ValidationResult result = new ValidationResult();
        
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            switch (key) {
                case "session_timeout":
                    if (!isValidInteger(value) || Integer.parseInt(value) < 300 || Integer.parseInt(value) > 7200) {
                        result.addFieldError(key, "Session timeout must be between 300 and 7200 seconds");
                    }
                    break;
                    
                case "max_login_attempts":
                    if (!isValidInteger(value) || Integer.parseInt(value) < 3 || Integer.parseInt(value) > 10) {
                        result.addFieldError(key, "Max login attempts must be between 3 and 10");
                    }
                    break;
                    
                case "backup_frequency":
                    if (!value.matches("daily|weekly|monthly")) {
                        result.addFieldError(key, "Backup frequency must be daily, weekly, or monthly");
                    }
                    break;
                    
                case "low_stock_threshold":
                    if (!isValidInteger(value) || Integer.parseInt(value) < 0 || Integer.parseInt(value) > 100) {
                        result.addFieldError(key, "Low stock threshold must be between 0 and 100");
                    }
                    break;
                    
                case "default_tax_rate":
                    if (!isValidDecimal(value) || Double.parseDouble(value) < 0 || Double.parseDouble(value) > 50) {
                        result.addFieldError(key, "Default tax rate must be between 0 and 50");
                    }
                    break;
            }
        }
        
        return result;
    }
    
    /**
     * Validate bulk operation parameters
     */
    public static ValidationResult validateBulkOperation(String operation, List<String> ids) {
        ValidationResult result = new ValidationResult();
        
        // Operation validation
        String[] allowedOperations = {"delete", "activate", "deactivate", "export", "update_status"};
        boolean validOperation = false;
        for (String op : allowedOperations) {
            if (op.equals(operation)) {
                validOperation = true;
                break;
            }
        }
        
        if (!validOperation) {
            result.addFieldError("operation", "Invalid bulk operation");
        }
        
        // IDs validation
        if (ids == null || ids.isEmpty()) {
            result.addFieldError("ids", "At least one item must be selected");
        } else if (ids.size() > 100) {
            result.addFieldError("ids", "Cannot process more than 100 items at once");
        } else {
            for (String id : ids) {
                if (!isValidInteger(id)) {
                    result.addFieldError("ids", "Invalid ID format: " + id);
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Validate report parameters
     */
    public static ValidationResult validateReportParameters(String reportType, String startDate, 
                                                           String endDate, String format) {
        ValidationResult result = new ValidationResult();
        
        // Report type validation
        String[] allowedTypes = {"sales", "inventory", "clients", "revenue", "staff_activity"};
        boolean validType = false;
        for (String type : allowedTypes) {
            if (type.equals(reportType)) {
                validType = true;
                break;
            }
        }
        
        if (!validType) {
            result.addFieldError("reportType", "Invalid report type");
        }
        
        // Date range validation
        if (!isValidDateRange(startDate, endDate)) {
            result.addFieldError("dateRange", "Invalid date range");
        }
        
        // Format validation
        if (isNullOrEmpty(format)) {
            format = "html"; // Default
        } else if (!format.matches("html|pdf|excel|csv")) {
            result.addFieldError("format", "Invalid report format. Allowed: html, pdf, excel, csv");
        }
        
        return result;
    }
    
    /**
     * Clean and validate JSON input
     */
    public static String cleanJsonInput(String jsonInput) {
        if (isNullOrEmpty(jsonInput)) return null;
        
        // Remove potential XSS and injection patterns
        return jsonInput.trim()
                       .replaceAll("(?i)<script[^>]*>.*?</script>", "")
                       .replaceAll("(?i)<iframe[^>]*>.*?</iframe>", "")
                       .replaceAll("(?i)javascript:", "")
                       .replaceAll("(?i)vbscript:", "")
                       .replaceAll("(?i)onload=", "")
                       .replaceAll("(?i)onerror=", "");
    }
    
    /**
     * Validate API key format
     */
    public static boolean isValidApiKey(String apiKey) {
        if (isNullOrEmpty(apiKey)) return false;
        // API key should be 32-64 characters, alphanumeric + hyphens
        return apiKey.matches("^[A-Za-z0-9\\-]{32,64}$");
    }
    
    /**
     * Validate session token
     */
    public static boolean isValidSessionToken(String token) {
        if (isNullOrEmpty(token)) return false;
        // Session token should be base64-like format
        return token.matches("^[A-Za-z0-9+/=]{20,}$");
    }
    
    /**
     * Check if string is null or empty
     */
    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validate multiple fields with custom rules
     */
    public static ValidationResult validateWithRules(Map<String, String> fields, Map<String, String[]> rules) {
        ValidationResult result = new ValidationResult();
        
        for (Map.Entry<String, String[]> entry : rules.entrySet()) {
            String fieldName = entry.getKey();
            String[] fieldRules = entry.getValue();
            String fieldValue = fields.get(fieldName);
            
            for (String rule : fieldRules) {
                if (!validateSingleRule(fieldValue, rule)) {
                    result.addFieldError(fieldName, "Field " + fieldName + " failed validation rule: " + rule);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Validate single rule
     */
    private static boolean validateSingleRule(String value, String rule) {
        switch (rule.toLowerCase()) {
            case "required":
                return !isNullOrEmpty(value);
            case "email":
                return isNullOrEmpty(value) || isValidEmail(value);
            case "phone":
                return isNullOrEmpty(value) || isValidPhone(value);
            case "username":
                return isNullOrEmpty(value) || isValidUsername(value);
            case "numeric":
                return isNullOrEmpty(value) || isValidDecimal(value);
            case "integer":
                return isNullOrEmpty(value) || isValidInteger(value);
            case "name":
                return isNullOrEmpty(value) || isValidName(value);
            default:
                // Handle min/max length rules
                if (rule.startsWith("min:")) {
                    int min = Integer.parseInt(rule.substring(4));
                    return isNullOrEmpty(value) || value.trim().length() >= min;
                }
                if (rule.startsWith("max:")) {
                    int max = Integer.parseInt(rule.substring(4));
                    return isNullOrEmpty(value) || value.trim().length() <= max;
                }
                return true; // Unknown rule, pass by default
        }
    }
    
    /**
     * Generate validation summary
     */
    public static String getValidationSummary(ValidationResult result) {
        if (result.isValid()) {
            return "Validation passed";
        }
        
        StringBuilder summary = new StringBuilder("Validation failed:\n");
        for (String error : result.getErrors()) {
            summary.append("- ").append(error).append("\n");
        }
        
        return summary.toString();
    }
}