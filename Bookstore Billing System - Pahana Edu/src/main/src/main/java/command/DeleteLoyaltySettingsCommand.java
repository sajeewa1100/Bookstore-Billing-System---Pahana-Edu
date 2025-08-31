package command;

import service.LoyaltySettingsService;

public class DeleteLoyaltySettingsCommand implements LoyaltySettingsCommand {
    private LoyaltySettingsService loyaltySettingsService;
    private int settingsId;
    
    public DeleteLoyaltySettingsCommand(LoyaltySettingsService loyaltySettingsService, int settingsId) {
        this.loyaltySettingsService = loyaltySettingsService;
        this.settingsId = settingsId;
    }
    
    @Override
    public CommandResult execute() {
        try {
            boolean success = loyaltySettingsService.deleteLoyaltySettings(settingsId);
            if (success) {
                return new CommandResult(true, "Loyalty settings deleted successfully");
            } else {
                return new CommandResult(false, "Failed to delete loyalty settings");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error deleting loyalty settings: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "DELETE_LOYALTY_SETTINGS";
    }
}
