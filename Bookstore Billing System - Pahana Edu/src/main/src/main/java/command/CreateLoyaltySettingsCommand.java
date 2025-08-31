package command;


import service.LoyaltySettingsService;
import model.LoyaltySettingsDTO;

public class CreateLoyaltySettingsCommand implements LoyaltySettingsCommand {
    private LoyaltySettingsService loyaltySettingsService;
    private LoyaltySettingsDTO loyaltySettings;
    
    public CreateLoyaltySettingsCommand(LoyaltySettingsService loyaltySettingsService, LoyaltySettingsDTO loyaltySettings) {
        this.loyaltySettingsService = loyaltySettingsService;
        this.loyaltySettings = loyaltySettings;
    }
    
    @Override
    public CommandResult execute() {
        try {
            int settingsId = loyaltySettingsService.createLoyaltySettings(loyaltySettings);
            if (settingsId > 0) {
                return new CommandResult(true, "Loyalty settings created successfully", settingsId);
            } else {
                return new CommandResult(false, "Failed to create loyalty settings");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error creating loyalty settings: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "CREATE_LOYALTY_SETTINGS";
    }
}