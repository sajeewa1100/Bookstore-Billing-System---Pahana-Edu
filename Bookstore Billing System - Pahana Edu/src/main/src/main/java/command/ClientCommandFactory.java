package command;

import service.ClientService;
import model.ClientDTO;

public class ClientCommandFactory {
    private ClientService clientService;
    
    public ClientCommandFactory(ClientService clientService) {
        this.clientService = clientService;
    }
    
    public ClientCommand createCommand(String commandType, Object... params) {
        switch (commandType.toUpperCase()) {
            case "CREATE_CLIENT":
                if (params.length > 0 && params[0] instanceof ClientDTO) {
                    return new CreateClientCommand(clientService, (ClientDTO) params[0]);
                }
                break;
                
            case "UPDATE_CLIENT":
                if (params.length > 0 && params[0] instanceof ClientDTO) {
                    return new UpdateClientCommand(clientService, (ClientDTO) params[0]);
                }
                break;
                
            case "DELETE_CLIENT":
                if (params.length > 0 && params[0] instanceof Integer) {
                    return new DeleteClientCommand(clientService, (Integer) params[0]);
                }
                break;
                
            case "GET_CLIENT_BY_ID":
                if (params.length > 0 && params[0] instanceof Integer) {
                    return new GetClientCommand(clientService, (Integer) params[0]);
                }
                break;
                
            case "GET_CLIENT_BY_PHONE":
                if (params.length > 0 && params[0] instanceof String) {
                    return new GetClientCommand(clientService, (String) params[0]);
                }
                break;
                
            case "LIST_CLIENTS":
                return new ListClientsCommand(clientService);
                
            case "SEARCH_CLIENTS":
                if (params.length > 0 && params[0] instanceof String) {
                    return new SearchClientsCommand(clientService, (String) params[0]);
                }
                break;
                
            case "UPDATE_CLIENT_LOYALTY_POINTS":
                if (params.length > 1 && params[0] instanceof Integer && params[1] instanceof Integer) {
                    return new UpdateClientLoyaltyPointsCommand(clientService, (Integer) params[0], (Integer) params[1]);
                }
                break;
                
            default:
                throw new IllegalArgumentException("Unknown command type: " + commandType);
        }
        
        throw new IllegalArgumentException("Invalid parameters for command: " + commandType);
    }
}