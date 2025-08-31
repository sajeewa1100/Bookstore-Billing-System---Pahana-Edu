package command;

import service.ClientService;
import model.ClientDTO;


public class GetClientCommand implements ClientCommand {
    private ClientService clientService;
    private int clientId;
    private String phone;
    private boolean searchByPhone;
    
    public GetClientCommand(ClientService clientService, int clientId) {
        this.clientService = clientService;
        this.clientId = clientId;
        this.searchByPhone = false;
    }
    
    public GetClientCommand(ClientService clientService, String phone) {
        this.clientService = clientService;
        this.phone = phone;
        this.searchByPhone = true;
    }
    
    @Override
    public CommandResult execute() {
        try {
            ClientDTO client;
            if (searchByPhone) {
                client = clientService.getClientByPhone(phone);
            } else {
                client = clientService.getClientById(clientId);
            }
            
            if (client != null) {
                return new CommandResult(true, "Client found", client);
            } else {
                return new CommandResult(false, "Client not found");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error retrieving client: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return searchByPhone ? "GET_CLIENT_BY_PHONE" : "GET_CLIENT_BY_ID";
    }
}
