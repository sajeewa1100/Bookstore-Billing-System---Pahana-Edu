package model;

import service.BookService;
import service.ClientService;
import service.BillingService;

/**
 * Factory class to create command objects based on action
 * Updated to support billing operations
 */
public class CommandFactory {

    // Book Actions
    private static final String[] BOOK_ACTIONS = { "books", "add", "edit", "delete", "search" };

    // Client Actions
    private static final String[] CLIENT_ACTIONS = { "clients", "add", "edit", "delete", "search", "view" };

    // Billing Actions
    private static final String[] BILLING_ACTIONS = { "billings", "create", "view", "print", "complete", "cancel", "delete" };

    /**
     * Create BookCommand based on action
     */
    public static BookCommand createCommand(String action, BookService bookService) {
        if (action == null) return null;

        switch (action.toLowerCase()) {
            case "books":
                return new ViewBooksCommand(bookService);
            case "add":
                return new AddBookCommand(bookService);
            case "edit":
                return new EditBookCommand(bookService);
            case "delete":
                return new DeleteBookCommand(bookService);
            default:
                return null;
        }
    }

    /**
     * Create ClientCommand based on action
     */
    public static ClientCommand createClientCommand(String action, ClientService clientService) {
        if (action == null) return null;

        switch (action.toLowerCase()) {
            case "clients":
            case "list":
                return new ViewClientsCommand(clientService);
            case "add":
            case "create":
                return new AddClientCommand(clientService);
            case "edit":
            case "update":
                return new UpdateClientCommand(clientService);
            case "view":
                return new ViewClientsCommand(clientService);
            case "delete":
                return new DeleteClientCommand(clientService);
            case "search":
                return new SearchClientsCommand(clientService);
            default:
                return null;
        }
    }

    /**
     * Create BillingCommand based on action
     */
    public static BillingCommand createBillingCommand(String action, BillingService billingService,
            ClientService clientService, BookService bookService) {
        if (action == null) return null;

        switch (action.toLowerCase()) {
            case "billings":
                return new ViewBillingCommand(billingService, clientService, bookService);
            case "create":
                return new CreateBillingCommand(billingService, clientService, bookService);
            case "view":
                return new ViewSingleBillingCommand(billingService);
            case "print":
                return new PrintBillingCommand(billingService);
            case "complete":
                return new CompleteBillingCommand(billingService);
            case "cancel":
                return new CancelBillingCommand(billingService);
            case "delete":
                return new DeleteBillingCommand(billingService);
            default:
                return null;
        }
    }

    /**
     * Check if book action is valid
     */
    public static boolean isValidAction(String action) {
        if (action == null) return false;

        for (String validAction : BOOK_ACTIONS) {
            if (validAction.equalsIgnoreCase(action)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if client action is valid
     */
    public static boolean isValidClientAction(String action) {
        if (action == null) return false;

        for (String validAction : CLIENT_ACTIONS) {
            if (validAction.equalsIgnoreCase(action)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if billing action is valid
     */
    public static boolean isValidBillingAction(String action) {
        if (action == null) return false;

        for (String validAction : BILLING_ACTIONS) {
            if (validAction.equalsIgnoreCase(action)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get default client action
     */
    public static String getDefaultClientAction() {
        return "clients";
    }

    /**
     * Get default billing action
     */
    public static String getDefaultBillingAction() {
        return "billings";
    }

    /**
     * Get available client actions
     */
    public static String[] getAvailableClientActions() {
        return CLIENT_ACTIONS.clone();
    }

    /**
     * Get available billing actions
     */
    public static String[] getAvailableBillingActions() {
        return BILLING_ACTIONS.clone();
    }

    /**
     * Get client action description
     */
    public static String getClientActionDescription(String action) {
        switch (action.toLowerCase()) {
            case "clients":
                return "View all clients";
            case "add":
                return "Add new client";
            case "edit":
                return "Edit existing client";
            case "view":
                return "View client details";
            case "delete":
                return "Delete client";
            case "search":
                return "Search clients";
            default:
                return "Unknown action";
        }
    }

    /**
     * Get billing action description
     */
    public static String getBillingActionDescription(String action) {
        switch (action.toLowerCase()) {
            case "billings":
                return "View all billing records";
            case "create":
                return "Create new billing";
            case "view":
                return "View single billing";
            case "print":
                return "Print billing";
            case "complete":
                return "Mark billing as completed";
            case "cancel":
                return "Cancel billing";
            case "delete":
                return "Delete billing";
            default:
                return "Unknown action";
        }
    }
}