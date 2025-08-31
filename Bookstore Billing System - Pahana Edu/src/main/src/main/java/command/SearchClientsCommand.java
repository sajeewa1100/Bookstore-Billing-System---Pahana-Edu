package command;

import service.ClientService;
import java.util.List;
import model.ClientDTO;


public class SearchClientsCommand implements ClientCommand {
    private ClientService clientService;
    private String searchTerm;
    
    public SearchClientsCommand(ClientService clientService, String searchTerm) {
        this.clientService = clientService;
        this.searchTerm = searchTerm;
    }
    
    @Override
    public CommandResult execute() {
        try {
            List<ClientDTO> clients = clientService.searchClients(searchTerm);
            return new CommandResult(true, "Found " + clients.size() + " clients", clients);
        } catch (Exception e) {
            return new CommandResult(false, "Error searching clients: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "SEARCH_CLIENTS";
    }
}
