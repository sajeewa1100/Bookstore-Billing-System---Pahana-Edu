package command;

import model.ClientDTO;
import service.ClientService;


public class UpdateClientCommand implements ClientCommand {
    private ClientService clientService;
    private ClientDTO client;
    
    public UpdateClientCommand(ClientService clientService, ClientDTO client) {
        this.clientService = clientService;
        this.client = client;
    }
    
    @Override
    public CommandResult execute() {
        try {
            // Validate phone uniqueness (excluding current client)
            if (clientService.phoneExists(client.getPhone(), client.getId())) {
                return new CommandResult(false, "Phone number already exists for another client");
            }
            
            boolean success = clientService.updateClient(client);
            if (success) {
                return new CommandResult(true, "Client updated successfully");
            } else {
                return new CommandResult(false, "Failed to update client");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error updating client: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "UPDATE_CLIENT";
    }
}

