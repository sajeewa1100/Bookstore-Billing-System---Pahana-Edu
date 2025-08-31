package command;

import service.BillingService;

public class DeleteInvoiceCommand implements BillingCommand {
    private BillingService billingService;
    private int invoiceId;
    
    public DeleteInvoiceCommand(BillingService billingService, int invoiceId) {
        this.billingService = billingService;
        this.invoiceId = invoiceId;
    }
    
    @Override
    public CommandResult execute() {
        try {
            boolean success = billingService.deleteInvoice(invoiceId);
            if (success) {
                return new CommandResult(true, "Invoice deleted successfully");
            } else {
                return new CommandResult(false, "Failed to delete invoice");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error deleting invoice: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "DELETE_INVOICE";
    }
}
