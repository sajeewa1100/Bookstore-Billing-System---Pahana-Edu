package command;

import service.BillingService;
import model.InvoiceDTO;


public class CreateInvoiceCommand implements BillingCommand {
    private BillingService billingService;
    private InvoiceDTO invoice;
    
    public CreateInvoiceCommand(BillingService billingService, InvoiceDTO invoice) {
        this.billingService = billingService;
        this.invoice = invoice;
    }
    
    @Override
    public CommandResult execute() {
        try {
            int invoiceId = billingService.createInvoice(invoice);
            if (invoiceId > 0) {
                return new CommandResult(true, "Invoice created successfully", invoiceId);
            } else {
                return new CommandResult(false, "Failed to create invoice");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error creating invoice: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "CREATE_INVOICE";
    }
}