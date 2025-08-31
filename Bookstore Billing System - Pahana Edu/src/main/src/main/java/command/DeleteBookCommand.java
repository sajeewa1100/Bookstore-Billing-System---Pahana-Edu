package command;

import service.BookService;


public class DeleteBookCommand implements BookCommand {
    private BookService bookService;
    private int bookId;
    
    public DeleteBookCommand(BookService bookService, int bookId) {
        this.bookService = bookService;
        this.bookId = bookId;
    }
    
    @Override
    public CommandResult execute() {
        try {
            boolean success = bookService.deleteBook(bookId);
            if (success) {
                return new CommandResult(true, "Book deleted successfully");
            } else {
                return new CommandResult(false, "Failed to delete book");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error deleting book: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "DELETE_BOOK";
    }
}