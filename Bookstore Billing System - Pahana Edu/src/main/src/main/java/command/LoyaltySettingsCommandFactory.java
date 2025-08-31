package command;


import service.LoyaltySettingsService;
import model.LoyaltySettingsDTO;

public class LoyaltySettingsCommandFactory {
    private LoyaltySettingsService loyaltySettingsService;
    
    public LoyaltySettingsCommandFactory(LoyaltySettingsService loyaltySettingsService) {
        this.loyaltySettingsService = loyaltySettingsService;
    }
    
    public LoyaltySettingsCommand createCommand(String commandType, Object... params) {
        switch (commandType.toUpperCase()) {
            case "CREATE_LOYALTY_SETTINGS":
                if (params.length > 0 && params[0] instanceof LoyaltySettingsDTO) {
                    return new CreateLoyaltySettingsCommand(loyaltySettingsService, (LoyaltySettingsDTO) params[0]);
                }
                break;
                
            case "UPDATE_LOYALTY_SETTINGS":
                if (params.length > 0 && params[0] instanceof LoyaltySettingsDTO) {
                    return new UpdateLoyaltySettingsCommand(loyaltySettingsService, (LoyaltySettingsDTO) params[0]);
                }
                break;
                
            case "DELETE_LOYALTY_SETTINGS":
                if (params.length > 0 && params[0] instanceof Integer) {
                    return new DeleteLoyaltySettingsCommand(loyaltySettingsService, (Integer) params[0]);
                }
                break;
                
            case "GET_ACTIVE_LOYALTY_SETTINGS":
                return new GetActiveLoyaltySettingsCommand(loyaltySettingsService);
                
            case "LIST_ALL_LOYALTY_SETTINGS":
                return new ListAllLoyaltySettingsCommand(loyaltySettingsService);
                
            default:
                throw new IllegalArgumentException("Unknown command type: " + commandType);
        }
        
        throw new IllegalArgumentException("Invalid parameters for command: " + commandType);
    }
}