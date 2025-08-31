package command;

import service.ClientService;


public class DeleteClientCommand implements ClientCommand {
    private ClientService clientService;
    private int clientId;
    
    public DeleteClientCommand(ClientService clientService, int clientId) {
        this.clientService = clientService;
        this.clientId = clientId;
    }
    
    @Override
    public CommandResult execute() {
        try {
            boolean success = clientService.deleteClient(clientId);
            if (success) {
                return new CommandResult(true, "Client deleted successfully");
            } else {
                return new CommandResult(false, "Failed to delete client");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error deleting client: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "DELETE_CLIENT";
    }
}
