package command;


import model.BookDTO;
import service.BookService;


public class CreateBookCommand implements BookCommand {
    private BookService bookService;
    private BookDTO book;
    
    public CreateBookCommand(BookService bookService, BookDTO book) {
        this.bookService = bookService;
        this.book = book;
    }
    
    @Override
    public CommandResult execute() {
        try {
            // Validate ISBN uniqueness
            if (bookService.isbnExists(book.getIsbn(), 0)) {
                return new CommandResult(false, "ISBN already exists");
            }
            
            int bookId = bookService.createBook(book);
            if (bookId > 0) {
                return new CommandResult(true, "Book created successfully", bookId);
            } else {
                return new CommandResult(false, "Failed to create book");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error creating book: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return "CREATE_BOOK";
    }
}
