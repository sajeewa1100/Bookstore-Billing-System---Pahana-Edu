package command;

import model.BookDTO;
import service.BookService;

public class UpdateBookCommand implements BookCommand {
    private BookService bookService;
    private BookDTO book;
    
    public UpdateBookCommand(BookService bookService, BookDTO book) {
        this.bookService = bookService;
        this.book = book;
    }
    
    @Override
    public CommandResult execute() {
        try {
            // Validate ISBN uniqueness (excluding current book)
            if (bookService.isbnExists(book.getIsbn(), book.getId())) {
                return new CommandResult(false, "ISBN already exists for another book");
            }
            
            boolean success = bookService.updateBook(book);
            if (success) {
                return new CommandResult(true, "Book updated successfully");
            } else {
                return new CommandResult(false, "Failed to update book");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error updating book: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "UPDATE_BOOK";
    }
}
