package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.ClientService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command implementation for adding a new client.
 * This command handles the creation of new client records in the system.
 */
public class AddClientCommand implements ClientCommand {
    
    private static final Logger LOGGER = Logger.getLogger(AddClientCommand.class.getName());
    private final ClientService clientService;
    
    /**
     * Constructor for AddClientCommand
     * 
     * @param clientService The ClientService instance to use for client operations
     * @throws IllegalArgumentException if clientService is null
     */
    public AddClientCommand(ClientService clientService) {
        if (clientService == null) {
            throw new IllegalArgumentException("ClientService cannot be null");
        }
        this.clientService = clientService;
    }
    
    /**
     * Execute the add client command
     * 
     * @param request The HttpServletRequest containing client data
     * @param response The HttpServletResponse for redirecting after operation
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        LOGGER.info("AddClientCommand: Executing add client command");
        
        try {
            // Extract client data from request
            ClientDTO client = extractClientFromRequest(request);
            
            // Validate client data
            validateClientData(client);
            
            // Add the client using the service
            boolean success = clientService.addClient(client);
            
            if (success) {
                setSuccessMessage(request, "Client added successfully: " + client.getFullName());
                LOGGER.info("AddClientCommand: Client added successfully - " + client.getFullName());
            } else {
                setErrorMessage(request, "Failed to add client. Please check the data and try again.");
                LOGGER.warning("AddClientCommand: Failed to add client - " + client.getFullName());
            }
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "AddClientCommand: Validation error - " + e.getMessage(), e);
            setErrorMessage(request, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "AddClientCommand: Unexpected error - " + e.getMessage(), e);
            setErrorMessage(request, "An unexpected error occurred while adding the client: " + e.getMessage());
        }
        
        // Redirect to clients list to avoid form resubmission
        response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
    }
    
    /**
     * Extract client data from HTTP request parameters
     * 
     * @param request The HttpServletRequest containing form data
     * @return ClientDTO populated with request data
     */
    private ClientDTO extractClientFromRequest(HttpServletRequest request) {
        ClientDTO client = new ClientDTO();
        
        // Extract basic client information
        client.setFirstName(getParameterValue(request, "firstName"));
        client.setLastName(getParameterValue(request, "lastName"));
        client.setEmail(getParameterValue(request, "email"));
        client.setPhone(getParameterValue(request, "phone"));
        
        // Extract address information
        client.setStreet(getParameterValue(request, "street"));
        client.setCity(getParameterValue(request, "city"));
        client.setState(getParameterValue(request, "state"));
        client.setZip(getParameterValue(request, "zip"));
        
        // Handle checkbox for auto mail
        String sendMailAuto = request.getParameter("sendMailAuto");
        client.setSendMailAuto("true".equals(sendMailAuto) || "on".equals(sendMailAuto));
        
        // Set default loyalty points for new clients
        client.setLoyaltyPoints(0);
        
        LOGGER.info("AddClientCommand: Extracted client data - " + client.getFirstName() + " " + client.getLastName());
        
        return client;
    }
    
    /**
     * Get parameter value with null safety and trimming
     * 
     * @param request The HttpServletRequest
     * @param paramName The parameter name
     * @return Trimmed parameter value or null if empty/null
     */
    private String getParameterValue(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value != null) {
            value = value.trim();
            if (value.isEmpty()) {
                value = null;
            }
        }
        return value;
    }
    
    /**
     * Validate client data before saving
     * 
     * @param client The ClientDTO to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateClientData(ClientDTO client) throws IllegalArgumentException {
        StringBuilder errors = new StringBuilder();
        
        // Validate required fields
        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            errors.append("First name is required. ");
        }
        
        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            errors.append("Last name is required. ");
        }
        
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            errors.append("Email is required. ");
        } else if (!isValidEmail(client.getEmail())) {
            errors.append("Email format is invalid. ");
        }
        
        // Validate optional fields format if provided
        if (client.getPhone() != null && !client.getPhone().isEmpty() && !isValidPhone(client.getPhone())) {
            errors.append("Phone number format is invalid. ");
        }
        
        if (client.getZip() != null && !client.getZip().isEmpty() && !isValidZip(client.getZip())) {
            errors.append("ZIP code format is invalid. ");
        }
        
        // Validate field lengths
        if (client.getFirstName() != null && client.getFirstName().length() > 50) {
            errors.append("First name must be 50 characters or less. ");
        }
        
        if (client.getLastName() != null && client.getLastName().length() > 50) {
            errors.append("Last name must be 50 characters or less. ");
        }
        
        if (client.getEmail() != null && client.getEmail().length() > 100) {
            errors.append("Email must be 100 characters or less. ");
        }
        
        if (errors.length() > 0) {
            throw new IllegalArgumentException("Validation failed: " + errors.toString().trim());
        }
    }
    
    /**
     * Validate email format
     * 
     * @param email The email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        // Simple email validation - in production, consider using a more robust validator
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validate phone number format
     * 
     * @param phone The phone number to validate
     * @return true if phone format is valid
     */
    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        // Allow various phone formats: (123) 456-7890, 123-456-7890, 1234567890, etc.
        String phoneRegex = "^[\\+]?[1-9]?[0-9]{7,15}$|^\\([0-9]{3}\\)\\s?[0-9]{3}-[0-9]{4}$|^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
        return phone.replaceAll("[\\s\\-\\(\\)]", "").matches("[0-9]{10,15}");
    }
    
    /**
     * Validate ZIP code format
     * 
     * @param zip The ZIP code to validate
     * @return true if ZIP format is valid
     */
    private boolean isValidZip(String zip) {
        if (zip == null) return false;
        // Allow US ZIP codes: 12345 or 12345-6789
        String zipRegex = "^[0-9]{5}(-[0-9]{4})?$";
        return zip.matches(zipRegex);
    }
    
    /**
     * Set success message in session
     * 
     * @param request The HttpServletRequest
     * @param message The success message to set
     */
    private void setSuccessMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("successMessage", message);
    }
    
    /**
     * Set error message in session
     * 
     * @param request The HttpServletRequest
     * @param message The error message to set
     */
    private void setErrorMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", message);
    }
}