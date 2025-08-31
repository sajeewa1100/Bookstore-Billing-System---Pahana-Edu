package command;

import service.ClientService;

public class UpdateClientLoyaltyPointsCommand implements ClientCommand {
    private ClientService clientService;
    private int clientId;
    private int newPoints;
    
    public UpdateClientLoyaltyPointsCommand(ClientService clientService, int clientId, int newPoints) {
        this.clientService = clientService;
        this.clientId = clientId;
        this.newPoints = newPoints;
    }
    
    @Override
    public CommandResult execute() {
        try {
            boolean success = clientService.updateLoyaltyPoints(clientId, newPoints);
            if (success) {
                return new CommandResult(true, "Loyalty points updated successfully");
            } else {
                return new CommandResult(false, "Failed to update loyalty points");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error updating loyalty points: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "UPDATE_CLIENT_LOYALTY_POINTS";
    }
}

