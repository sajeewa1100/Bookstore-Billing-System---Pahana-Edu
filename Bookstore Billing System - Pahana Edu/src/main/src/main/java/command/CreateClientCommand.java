package command;

import model.ClientDTO;
import service.ClientService;


public class CreateClientCommand implements ClientCommand {
    private ClientService clientService;
    private ClientDTO client;
    
    public CreateClientCommand(ClientService clientService, ClientDTO client) {
        this.clientService = clientService;
        this.client = client;
    }
    
    @Override
    public CommandResult execute() {
        try {
            // Validate phone uniqueness
            if (clientService.phoneExists(client.getPhone(), 0)) {
                return new CommandResult(false, "Phone number already exists");
            }
            
            int clientId = clientService.createClient(client);
            if (clientId > 0) {
                return new CommandResult(true, "Client created successfully", clientId);
            } else {
                return new CommandResult(false, "Failed to create client");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error creating client: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "CREATE_CLIENT";
    }
}