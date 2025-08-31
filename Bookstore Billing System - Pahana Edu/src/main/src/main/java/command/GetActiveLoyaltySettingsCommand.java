package command;

import service.LoyaltySettingsService;
import model.LoyaltySettingsDTO;

public class GetActiveLoyaltySettingsCommand implements LoyaltySettingsCommand {
    private LoyaltySettingsService loyaltySettingsService;
    
    public GetActiveLoyaltySettingsCommand(LoyaltySettingsService loyaltySettingsService) {
        this.loyaltySettingsService = loyaltySettingsService;
    }
    
    @Override
    public CommandResult execute() {
        try {
            LoyaltySettingsDTO settings = loyaltySettingsService.getActiveLoyaltySettings();
            if (settings != null) {
                return new CommandResult(true, "Active loyalty settings retrieved", settings);
            } else {
                return new CommandResult(false, "No active loyalty settings found");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error retrieving loyalty settings: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "GET_ACTIVE_LOYALTY_SETTINGS";
    }
}

