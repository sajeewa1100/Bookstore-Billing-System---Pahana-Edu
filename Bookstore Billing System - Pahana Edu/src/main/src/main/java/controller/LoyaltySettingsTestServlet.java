package controller;

import command.*;
import service.LoyaltySettingsService;
import service.ClientService;
import model.LoyaltySettingsDTO;
import model.ClientDTO;import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/loyaltySettingsTest")
public class LoyaltySettingsTestServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LoyaltySettingsService loyaltySettingsService;
    private LoyaltySettingsCommandFactory commandFactory;
    
    @Override
    public void init() throws ServletException {
        super.init();
        loyaltySettingsService = new LoyaltySettingsService();
        commandFactory = new LoyaltySettingsCommandFactory(loyaltySettingsService);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Loyalty Settings Management System Test</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }");
        out.println("h1 { color: #333; text-align: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; }");
        out.println("h2 { color: #666; margin-top: 30px; border-bottom: 2px solid #ddd; padding-bottom: 10px; }");
        out.println(".test-section { background: white; padding: 20px; margin: 15px 0; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println(".settings-card { background: #f8f9fa; padding: 15px; margin: 10px 0; border-left: 4px solid #007bff; border-radius: 5px; }");
        out.println(".client-tier { background: #e8f5e8; padding: 10px; margin: 5px 0; border-left: 4px solid #28a745; border-radius: 3px; }");
        out.println(".success { color: #28a745; font-weight: bold; padding: 8px; background: #d4edda; border-radius: 4px; }");
        out.println(".error { color: #dc3545; font-weight: bold; padding: 8px; background: #f8d7da; border-radius: 4px; }");
        out.println(".operation { background: #fff3cd; padding: 10px; margin: 8px 0; border-radius: 5px; border-left: 4px solid #ffc107; }");
        out.println(".tier-platinum { background-color: #e1f5fe; border-left-color: #01579b; }");
        out.println(".tier-gold { background-color: #fff8e1; border-left-color: #ff8f00; }");
        out.println(".tier-silver { background-color: #f3e5f5; border-left-color: #4a148c; }");
        out.println("table { width: 100%; border-collapse: collapse; margin: 10px 0; }");
        out.println("th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; font-weight: bold; }");
        out.println(".calculation-example { background: #e3f2fd; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<h1>🏆 Loyalty Settings Management System - Test Results</h1>");
        
        // Test 1: Show current active settings
        showCurrentActiveSettings(out);
        
        // Test 2: Create new loyalty settings
        testCreateNewSettings(out);
        
        // Test 3: Update existing settings
        testUpdateSettings(out);
        
        // Test 4: Test discount calculations with current settings
        testDiscountCalculations(out);
        
        // Test 5: Show how settings affect client tiers
        showClientTierImpact(out);
        
        // Test 6: Test points calculation
        testPointsCalculation(out);
        
        // Test 7: List all settings history
        showSettingsHistory(out);
        
        out.println("</body>");
        out.println("</html>");
    }
    
    private void showCurrentActiveSettings(PrintWriter out) {
        out.println("<h2>📋 Current Active Loyalty Settings</h2>");
        out.println("<div class='test-section'>");
        
        LoyaltySettingsCommand getActiveCommand = commandFactory.createCommand("GET_ACTIVE_LOYALTY_SETTINGS");
        CommandResult result = getActiveCommand.execute();
        
        if (result.isSuccess() && result.getData() != null) {
            LoyaltySettingsDTO settings = (LoyaltySettingsDTO) result.getData();
            displayLoyaltySettings(out, settings, "Current Active Settings");
        } else {
            out.println("<div class='error'>No active loyalty settings found!</div>");
        }
        
        out.println("</div>");
    }
    
    private void testCreateNewSettings(PrintWriter out) {
        out.println("<h2>➕ Test: Creating New Loyalty Settings</h2>");
        out.println("<div class='test-section'>");
        
        // Create new settings with different values
        LoyaltySettingsDTO newSettings = new LoyaltySettingsDTO();
        newSettings.setPointsPer100Rs(2); // 2 points per 100 Rs (increased from 1)
        newSettings.setSilverDiscount(new BigDecimal("7.50")); // 7.5% for Silver
        newSettings.setGoldThreshold(4000); // Reduced threshold to 4000 points
        newSettings.setGoldDiscount(new BigDecimal("12.50")); // 12.5% for Gold
        newSettings.setPlatinumThreshold(12000); // Reduced threshold to 12000 points
        newSettings.setPlatinumDiscount(new BigDecimal("18.00")); // 18% for Platinum
        
        out.println("<div class='operation'>Creating new loyalty settings with enhanced benefits...</div>");
        displayLoyaltySettings(out, newSettings, "New Settings to Create");
        
        LoyaltySettingsCommand createCommand = commandFactory.createCommand("CREATE_LOYALTY_SETTINGS", newSettings);
        CommandResult result = createCommand.execute();
        
        out.println("<div class='" + (result.isSuccess() ? "success" : "error") + "'>");
        out.println("Result: " + result.getMessage());
        if (result.getData() != null) {
            out.println(" (ID: " + result.getData() + ")");
        }
        out.println("</div>");
        
        // Show the new active settings
        if (result.isSuccess()) {
            showCurrentActiveSettings(out);
        }
        
        out.println("</div>");
    }
    
    private void testUpdateSettings(PrintWriter out) {
        out.println("<h2>✏️ Test: Updating Loyalty Settings</h2>");
        out.println("<div class='test-section'>");
        
        // Get current active settings first
        LoyaltySettingsCommand getActiveCommand = commandFactory.createCommand("GET_ACTIVE_LOYALTY_SETTINGS");
        CommandResult getResult = getActiveCommand.execute();
        
        if (getResult.isSuccess() && getResult.getData() != null) {
            LoyaltySettingsDTO currentSettings = (LoyaltySettingsDTO) getResult.getData();
            
            out.println("<div class='operation'>Updating existing settings (ID: " + currentSettings.getId() + ")...</div>");
            
            // Modify the settings
            currentSettings.setSilverDiscount(new BigDecimal("6.00")); // Change Silver to 6%
            currentSettings.setGoldDiscount(new BigDecimal("11.00")); // Change Gold to 11%
            currentSettings.setPlatinumDiscount(new BigDecimal("16.50")); // Change Platinum to 16.5%
            
            displayLoyaltySettings(out, currentSettings, "Updated Settings");
            
            LoyaltySettingsCommand updateCommand = commandFactory.createCommand("UPDATE_LOYALTY_SETTINGS", currentSettings);
            CommandResult result = updateCommand.execute();
            
            out.println("<div class='" + (result.isSuccess() ? "success" : "error") + "'>");
            out.println("Update Result: " + result.getMessage());
            out.println("</div>");
            
            // Show updated active settings
            if (result.isSuccess()) {
                showCurrentActiveSettings(out);
            }
        } else {
            out.println("<div class='error'>No active settings found to update</div>");
        }
        
        out.println("</div>");
    }
    
    private void testDiscountCalculations(PrintWriter out) {
        out.println("<h2>💰 Test: Discount Calculations</h2>");
        out.println("<div class='test-section'>");
        
        LoyaltySettingsDTO settings = loyaltySettingsService.getActiveLoyaltySettings();
        if (settings == null) {
            out.println("<div class='error'>No active settings found for discount calculations</div>");
            return;
        }
        
        // Test different purchase amounts
        BigDecimal[] testAmounts = {
            new BigDecimal("1000.00"), 
            new BigDecimal("2500.00"), 
            new BigDecimal("5000.00"),
            new BigDecimal("10000.00")
        };
        
        String[] tiers = {"SILVER", "GOLD", "PLATINUM"};
        
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>Purchase Amount (Rs)</th>");
        out.println("<th>Silver Tier Discount</th>");
        out.println("<th>Gold Tier Discount</th>");
        out.println("<th>Platinum Tier Discount</th>");
        out.println("</tr>");
        
        for (BigDecimal amount : testAmounts) {
            out.println("<tr>");
            out.println("<td>Rs " + amount + "</td>");
            
            for (String tier : tiers) {
                BigDecimal discountRate = loyaltySettingsService.getDiscountForTier(tier);
                BigDecimal discountAmount = amount.multiply(discountRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                BigDecimal finalAmount = amount.subtract(discountAmount);
                
                out.println("<td>");
                out.println(discountRate + "% = Rs " + discountAmount + "<br>");
                out.println("<strong>Final: Rs " + finalAmount + "</strong>");
                out.println("</td>");
            }
            
            out.println("</tr>");
        }
        
        out.println("</table>");
        
        out.println("</div>");
    }
    
    private void testPointsCalculation(PrintWriter out) {
        out.println("<h2>🎯 Test: Points Calculation Examples</h2>");
        out.println("<div class='test-section'>");
        
        LoyaltySettingsDTO settings = loyaltySettingsService.getActiveLoyaltySettings();
        if (settings == null) {
            out.println("<div class='error'>No active settings found for points calculations</div>");
            return;
        }
        
        out.println("<div class='calculation-example'>");
        out.println("<h4>Points Earning Rate: " + settings.getPointsPer100Rs() + " points per Rs 100 spent</h4>");
        out.println("</div>");
        
        BigDecimal[] purchaseAmounts = {
            new BigDecimal("150.00"),
            new BigDecimal("350.00"), 
            new BigDecimal("750.00"),
            new BigDecimal("1250.00"),
            new BigDecimal("2500.00"),
            new BigDecimal("5000.00")
        };
        
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>Purchase Amount (Rs)</th>");
        out.println("<th>Points Earned</th>");
        out.println("<th>Calculation</th>");
        out.println("</tr>");
        
        for (BigDecimal amount : purchaseAmounts) {
            int pointsEarned = loyaltySettingsService.calculatePointsEarned(amount);
            int hundreds = amount.divide(new BigDecimal("100"), 0, java.math.RoundingMode.DOWN).intValue();
            
            out.println("<tr>");
            out.println("<td>Rs " + amount + "</td>");
            out.println("<td><strong>" + pointsEarned + " points</strong></td>");
            out.println("<td>" + hundreds + " × " + settings.getPointsPer100Rs() + " = " + pointsEarned + "</td>");
            out.println("</tr>");
        }
        
        out.println("</table>");
        
        out.println("</div>");
    }
    
    private void showClientTierImpact(PrintWriter out) {
        out.println("<h2>👥 How Current Settings Affect Client Tiers</h2>");
        out.println("<div class='test-section'>");
        
        LoyaltySettingsDTO settings = loyaltySettingsService.getActiveLoyaltySettings();
        if (settings == null) {
            out.println("<div class='error'>No active settings found</div>");
            return;
        }
        
        out.println("<div class='calculation-example'>");
        out.println("<h4>Tier Thresholds:</h4>");
        out.println("🥈 Silver: 0 - " + (settings.getGoldThreshold() - 1) + " points (" + settings.getSilverDiscount() + "% discount)<br>");
        out.println("🥇 Gold: " + settings.getGoldThreshold() + " - " + (settings.getPlatinumThreshold() - 1) + " points (" + settings.getGoldDiscount() + "% discount)<br>");
        out.println("💎 Platinum: " + settings.getPlatinumThreshold() + "+ points (" + settings.getPlatinumDiscount() + "% discount)");
        out.println("</div>");
        
        // Get some sample clients and show their tiers
        ClientService clientService = new ClientService();
        List<ClientDTO> sampleClients = clientService.getAllClients();
        
        out.println("<h4>Sample Clients with Current Tier Status:</h4>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>Client Name</th>");
        out.println("<th>Current Points</th>");
        out.println("<th>Current Tier</th>");
        out.println("<th>Calculated Tier</th>");
        out.println("<th>Discount Rate</th>");
        out.println("</tr>");
        
        int count = 0;
        for (ClientDTO client : sampleClients) {
            if (count >= 8) break; // Show only first 8 clients
            
            String calculatedTier = loyaltySettingsService.calculateClientTier(client.getLoyaltyPoints());
            BigDecimal discountRate = loyaltySettingsService.getDiscountForTier(calculatedTier);
            
            String tierClass = "";
            switch (calculatedTier) {
                case "PLATINUM": tierClass = "tier-platinum"; break;
                case "GOLD": tierClass = "tier-gold"; break;
                case "SILVER": tierClass = "tier-silver"; break;
            }
            
            out.println("<tr class='" + tierClass + "'>");
            out.println("<td>" + client.getFullName() + "</td>");
            out.println("<td>" + client.getLoyaltyPoints() + "</td>");
            out.println("<td>" + client.getTierLevel() + "</td>");
            out.println("<td><strong>" + calculatedTier + "</strong></td>");
            out.println("<td>" + discountRate + "%</td>");
            out.println("</tr>");
            
            count++;
        }
        
        out.println("</table>");
        
        out.println("</div>");
    }
    
    private void showSettingsHistory(PrintWriter out) {
        out.println("<h2>📚 All Loyalty Settings History</h2>");
        out.println("<div class='test-section'>");
        
        LoyaltySettingsCommand listCommand = commandFactory.createCommand("LIST_ALL_LOYALTY_SETTINGS");
        CommandResult result = listCommand.execute();
        
        if (result.isSuccess() && result.getData() != null) {
            @SuppressWarnings("unchecked")
            List<LoyaltySettingsDTO> settingsList = (List<LoyaltySettingsDTO>) result.getData();
            
            out.println("<p>Total Settings Records: " + settingsList.size() + "</p>");
            
            for (int i = 0; i < settingsList.size(); i++) {
                LoyaltySettingsDTO settings = settingsList.get(i);
                String status = settings.isActive() ? " (ACTIVE)" : " (Inactive)";
                displayLoyaltySettings(out, settings, "Settings #" + (i + 1) + " - ID: " + settings.getId() + status);
            }
        } else {
            out.println("<div class='error'>Failed to retrieve settings history</div>");
        }
        
        out.println("</div>");
    }
    
    private void displayLoyaltySettings(PrintWriter out, LoyaltySettingsDTO settings, String title) {
        String activeClass = settings.isActive() ? " style='border-left-color: #28a745;'" : "";
        out.println("<div class='settings-card'" + activeClass + ">");
        out.println("<h4>" + title + (settings.isActive() ? " ✅" : "") + "</h4>");
        out.println("<table style='width: 100%;'>");
        out.println("<tr><td><strong>Points per Rs 100:</strong></td><td>" + settings.getPointsPer100Rs() + " points</td></tr>");
        out.println("<tr><td><strong>Silver Discount:</strong></td><td>" + settings.getSilverDiscount() + "%</td></tr>");
        out.println("<tr><td><strong>Gold Threshold:</strong></td><td>" + settings.getGoldThreshold() + " points</td></tr>");
        out.println("<tr><td><strong>Gold Discount:</strong></td><td>" + settings.getGoldDiscount() + "%</td></tr>");
        out.println("<tr><td><strong>Platinum Threshold:</strong></td><td>" + settings.getPlatinumThreshold() + " points</td></tr>");
        out.println("<tr><td><strong>Platinum Discount:</strong></td><td>" + settings.getPlatinumDiscount() + "%</td></tr>");
        out.println("<tr><td><strong>Status:</strong></td><td>" + (settings.isActive() ? "ACTIVE" : "Inactive") + "</td></tr>");
        out.println("</table>");
        out.println("</div>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
