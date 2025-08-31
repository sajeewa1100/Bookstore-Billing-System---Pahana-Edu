package command;

import service.BookService;
import java.util.List;
import model.BookDTO;


public class ListBooksCommand implements BookCommand {
    private BookService bookService;
    
    public ListBooksCommand(BookService bookService) {
        this.bookService = bookService;
    }
    
    @Override
    public CommandResult execute() {
        try {
            List<BookDTO> books = bookService.getAllBooks();
            return new CommandResult(true, "Retrieved " + books.size() + " books", books);
        } catch (Exception e) {
            return new CommandResult(false, "Error retrieving books: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "LIST_BOOKS";
    }
}
