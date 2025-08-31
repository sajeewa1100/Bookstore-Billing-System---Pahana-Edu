package command;


import service.BillingService;
import model.InvoiceDTO;

public class BillingCommandFactory {
    private BillingService billingService;
    
    public BillingCommandFactory(BillingService billingService) {
        this.billingService = billingService;
    }
    
    public BillingCommand createCommand(String commandType, Object... params) {
        switch (commandType.toUpperCase()) {
            case "CREATE_INVOICE":
                if (params.length > 0 && params[0] instanceof InvoiceDTO) {
                    return new CreateInvoiceCommand(billingService, (InvoiceDTO) params[0]);
                }
                break;
                
            case "SEARCH_CLIENT":
                if (params.length > 0 && params[0] instanceof String) {
                    return new SearchClientCommand(billingService, (String) params[0]);
                }
                break;
                
            case "SEARCH_BOOK":
                if (params.length > 0 && params[0] instanceof String) {
                    return new SearchBookCommand(billingService, (String) params[0]);
                }
                break;
                
            case "DELETE_INVOICE":
                if (params.length > 0 && params[0] instanceof Integer) {
                    return new DeleteInvoiceCommand(billingService, (Integer) params[0]);
                }
                break;
                
            default:
                throw new IllegalArgumentException("Unknown command type: " + commandType);
        }
        
        throw new IllegalArgumentException("Invalid parameters for command: " + commandType);
    }
}