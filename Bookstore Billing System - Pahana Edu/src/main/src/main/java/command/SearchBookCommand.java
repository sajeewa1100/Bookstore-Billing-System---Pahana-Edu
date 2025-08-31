package command;

import service.BillingService;
import java.util.List;
import model.BookDTO;

public class SearchBookCommand implements BillingCommand {
    private BillingService billingService;
    private String searchTerm;
    
    public SearchBookCommand(BillingService billingService, String searchTerm) {
        this.billingService = billingService;
        this.searchTerm = searchTerm;
    }
    
    @Override
    public CommandResult execute() {
        try {
            List<BookDTO> books = billingService.searchBooks(searchTerm);
            return new CommandResult(true, "Found " + books.size() + " books", books);
        } catch (Exception e) {
            return new CommandResult(false, "Error searching books: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "SEARCH_BOOK";
    }
}
