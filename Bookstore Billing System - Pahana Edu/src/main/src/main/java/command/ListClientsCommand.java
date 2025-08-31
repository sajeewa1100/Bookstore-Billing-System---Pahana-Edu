package command;

import service.ClientService;
import java.util.List;
import model.ClientDTO;


public class ListClientsCommand implements ClientCommand {
    private ClientService clientService;
    
    public ListClientsCommand(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @Override
    public CommandResult execute() {
        try {
            List<ClientDTO> clients = clientService.getAllClients();
            return new CommandResult(true, "Retrieved " + clients.size() + " clients", clients);
        } catch (Exception e) {
            return new CommandResult(false, "Error retrieving clients: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "LIST_CLIENTS";
    }
}
