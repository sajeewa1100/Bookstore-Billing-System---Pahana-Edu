package command;

import service.BillingService;
import java.util.List;
import model.ClientDTO;

public class SearchClientCommand implements BillingCommand {
    private BillingService billingService;
    private String searchTerm;
    
    public SearchClientCommand(BillingService billingService, String searchTerm) {
        this.billingService = billingService;
        this.searchTerm = searchTerm;
    }
    
    @Override
    public CommandResult execute() {
        try {
            List<ClientDTO> clients = billingService.searchClients(searchTerm);
            return new CommandResult(true, "Found " + clients.size() + " clients", clients);
        } catch (Exception e) {
            return new CommandResult(false, "Error searching clients: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "SEARCH_CLIENT";
    }
}
