package command;

import service.BookService;
import model.BookDTO;


public class GetBookCommand implements BookCommand {
    private BookService bookService;
    private int bookId;
    private String isbn;
    private boolean searchByISBN;
    
    public GetBookCommand(BookService bookService, int bookId) {
        this.bookService = bookService;
        this.bookId = bookId;
        this.searchByISBN = false;
    }
    
    public GetBookCommand(BookService bookService, String isbn) {
        this.bookService = bookService;
        this.isbn = isbn;
        this.searchByISBN = true;
    }
    
    @Override
    public CommandResult execute() {
        try {
            BookDTO book;
            if (searchByISBN) {
                book = bookService.getBookByISBN(isbn);
            } else {
                book = bookService.getBookById(bookId);
            }
            
            if (book != null) {
                return new CommandResult(true, "Book found", book);
            } else {
                return new CommandResult(false, "Book not found");
            }
        } catch (Exception e) {
            return new CommandResult(false, "Error retrieving book: " + e.getMessage());
        }
    }
    
    @Override
    public String getCommandName() {
        return searchByISBN ? "GET_BOOK_BY_ISBN" : "GET_BOOK_BY_ID";
    }
}