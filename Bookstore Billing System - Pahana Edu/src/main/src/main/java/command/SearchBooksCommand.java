package command;

import service.BookService;
import java.util.List;
import model.BookDTO;


public class SearchBooksCommand implements BookCommand {
    private BookService bookService;
    private String searchTerm;
    
    public SearchBooksCommand(BookService bookService, String searchTerm) {
        this.bookService = bookService;
        this.searchTerm = searchTerm;
    }
    
    @Override
    public CommandResult execute() {
        try {
            List<BookDTO> books = bookService.searchBooks(searchTerm);
            return new CommandResult(true, "Found " + books.size() + " books", books);
        } catch (Exception e) {
            return new CommandResult(false, "Error searching books: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "SEARCH_BOOKS";
    }
}
