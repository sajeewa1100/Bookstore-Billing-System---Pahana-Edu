package command;

import service.LoyaltySettingsService;
import model.LoyaltySettingsDTO;

public class UpdateLoyaltySettingsCommand implements LoyaltySettingsCommand {
    private LoyaltySettingsService loyaltySettingsService;
    private LoyaltySettingsDTO loyaltySettings;
    
    public UpdateLoyaltySettingsCommand(LoyaltySettingsService loyaltySettingsService, LoyaltySettingsDTO loyaltySettings) {
        this.loyaltySettingsService = loyaltySettingsService;
        this.loyaltySettings = loyaltySettings;
    }
    
    @Override
    public CommandResult execute() {
        try {
            boolean success = loyaltySettingsService.updateLoyaltySettings(loyaltySettings);
            if (success) {
                return new CommandResult(true, "Loyalty settings updated successfully");
            } else {
                return new CommandResult(false, "Failed to update loyalty settings");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error updating loyalty settings: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "UPDATE_LOYALTY_SETTINGS";
    }
}
