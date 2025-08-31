package command;

import service.LoyaltySettingsService;
import java.util.List;
import model.LoyaltySettingsDTO;

public class ListAllLoyaltySettingsCommand implements LoyaltySettingsCommand {
    private LoyaltySettingsService loyaltySettingsService;
    
    public ListAllLoyaltySettingsCommand(LoyaltySettingsService loyaltySettingsService) {
        this.loyaltySettingsService = loyaltySettingsService;
    }
    
    @Override
    public CommandResult execute() {
        try {
            List<LoyaltySettingsDTO> settingsList = loyaltySettingsService.getAllLoyaltySettings();
            return new CommandResult(true, "Retrieved " + settingsList.size() + " loyalty settings", settingsList);
        } catch (Exception e) {
            return new CommandResult(false, "Error retrieving loyalty settings list: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "LIST_ALL_LOYALTY_SETTINGS";
    }
}

