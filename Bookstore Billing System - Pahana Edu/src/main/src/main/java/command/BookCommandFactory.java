package command;


import service.BookService;
import model.BookDTO;

public class BookCommandFactory {
    private BookService bookService;
    
    public BookCommandFactory(BookService bookService) {
        this.bookService = bookService;
    }
    
    public BookCommand createCommand(String commandType, Object... params) {
        switch (commandType.toUpperCase()) {
            case "CREATE_BOOK":
                if (params.length > 0 && params[0] instanceof BookDTO) {
                    return new CreateBookCommand(bookService, (BookDTO) params[0]);
                }
                break;
                
            case "UPDATE_BOOK":
                if (params.length > 0 && params[0] instanceof BookDTO) {
                    return new UpdateBookCommand(bookService, (BookDTO) params[0]);
                }
                break;
                
            case "DELETE_BOOK":
                if (params.length > 0 && params[0] instanceof Integer) {
                    return new DeleteBookCommand(bookService, (Integer) params[0]);
                }
                break;
                
            case "GET_BOOK_BY_ID":
                if (params.length > 0 && params[0] instanceof Integer) {
                    return new GetBookCommand(bookService, (Integer) params[0]);
                }
                break;
                
            case "GET_BOOK_BY_ISBN":
                if (params.length > 0 && params[0] instanceof String) {
                    return new GetBookCommand(bookService, (String) params[0]);
                }
                break;
                
            case "LIST_BOOKS":
                return new ListBooksCommand(bookService);
                
            case "SEARCH_BOOKS":
                if (params.length > 0 && params[0] instanceof String) {
                    return new SearchBooksCommand(bookService, (String) params[0]);
                }
                break;
                
       
                
            default:
                throw new IllegalArgumentException("Unknown command type: " + commandType);
        }
        
        throw new IllegalArgumentException("Invalid parameters for command: " + commandType);
    }
}
