package model;


import service.BillingService;
import service.ClientService;
import service.BookService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ViewBillingCommand implements BillingCommand {

    private BillingService billingService;
    private ClientService clientService;  // Add ClientService
    private BookService bookService;     // Add BookService

    public ViewBillingCommand(BillingService billingService, ClientService clientService, BookService bookService) {
        this.billingService = billingService;
        this.clientService = clientService;
        this.bookService = bookService;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Fetch and filter billings based on the request parameters
            List<BillingDTO> billings = billingService.getAllBills(); // Default fetch all bills
            
            // Fetch clients and books
            List<ClientDTO> clients = clientService.getAllClients();
            List<BookDTO> books = bookService.getAllBooks();

            // Set all data for JSP
            request.setAttribute("clients", clients);
            request.setAttribute("books", books);
            request.setAttribute("billings", billings);

            request.getRequestDispatcher("views/billings.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading billings: " + e.getMessage());
            request.getRequestDispatcher("views/billings.jsp").forward(request, response);
        }
    }
}