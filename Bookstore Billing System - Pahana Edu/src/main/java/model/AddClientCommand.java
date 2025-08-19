package model;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.ClientService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddClientCommand implements ClientCommand {

    private static final Logger LOGGER = Logger.getLogger(AddClientCommand.class.getName());
    private final ClientService clientService;

    public AddClientCommand(ClientService clientService) {
        if (clientService == null) {
            throw new IllegalArgumentException("ClientService cannot be null");
        }
        this.clientService = clientService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            ClientDTO client = extractClientFromRequest(request);
            validateClientData(client);
            
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

        response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
    }

    private ClientDTO extractClientFromRequest(HttpServletRequest request) {
        ClientDTO client = new ClientDTO();
        client.setFirstName(request.getParameter("firstName"));
        client.setLastName(request.getParameter("lastName"));
        client.setEmail(request.getParameter("email"));
        client.setPhone(request.getParameter("phone"));
        client.setStreet(request.getParameter("street"));
        client.setCity(request.getParameter("city"));
        client.setState(request.getParameter("state"));
        client.setZip(request.getParameter("zip"));
        client.setSendMailAuto("true".equals(request.getParameter("sendMailAuto")));
        client.setLoyaltyPoints(0); // Default loyalty points for new clients
        return client;
    }

    private void validateClientData(ClientDTO client) {
        if (client.getFirstName() == null || client.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (client.getLastName() == null || client.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (client.getPhone() == null || client.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (client.getStreet() == null || client.getStreet().trim().isEmpty()) {
            throw new IllegalArgumentException("Street address is required");
        }
        if (client.getCity() == null || client.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (client.getState() == null || client.getState().trim().isEmpty()) {
            throw new IllegalArgumentException("State is required");
        }
        if (client.getZip() == null || client.getZip().trim().isEmpty()) {
            throw new IllegalArgumentException("ZIP code is required");
        }
        
        // Email format validation
        if (!isValidEmail(client.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Phone validation (Sri Lankan format)
        if (!isValidSriLankanPhone(client.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidSriLankanPhone(String phone) {
        if (phone == null) return false;
        String cleanPhone = phone.replaceAll("[^\\d+]", "");
        
        if (cleanPhone.startsWith("+94")) {
            String withoutCountry = cleanPhone.substring(3);
            return withoutCountry.length() == 9 && withoutCountry.startsWith("7");
        } else if (cleanPhone.startsWith("0")) {
            return cleanPhone.length() >= 9 && cleanPhone.length() <= 10;
        } else if (cleanPhone.length() == 9 && cleanPhone.startsWith("7")) {
            return true;
        }
        
        return false;
    }

    private void setSuccessMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("successMessage", message);
    }

    private void setErrorMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", message);
    }
}