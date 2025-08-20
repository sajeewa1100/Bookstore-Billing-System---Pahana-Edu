package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import dao.BookDAO;
import dao.ClientDAO;
import model.BookDTO;
import model.ClientDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/TestDataServlet")
public class TestDataServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BookDAO bookDAO;
    private ClientDAO clientDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("TestDataServlet: Initializing...");
        this.bookDAO = new BookDAO();
        this.clientDAO = new ClientDAO();
        System.out.println("TestDataServlet: DAOs initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Database Test Results</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
            out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println(".success { color: green; font-weight: bold; }");
            out.println(".error { color: red; font-weight: bold; }");
            out.println(".section { margin: 30px 0; }");
            out.println(".count { background: #e3f2fd; padding: 10px; border-radius: 5px; margin: 10px 0; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<h1>📊 Database Connection Test</h1>");
            out.println("<p>Testing database connectivity and data retrieval...</p>");
            
            // Test Books
            testBooks(out);
            
            // Test Clients
            testClients(out);
            
            // Test specific searches for billing
            testBillingSearches(out);
            
            out.println("<div class='section'>");
            out.println("<h2>✅ Test Complete</h2>");
            out.println("<p>If you see data above, your database connection is working correctly!</p>");
            out.println("<p><a href='" + request.getContextPath() + "/BillingServlet?action=billings'>Go to Billing System</a></p>");
            out.println("</div>");
            
            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception e) {
            e.printStackTrace();
            handleError(response, "Error in TestDataServlet: " + e.getMessage());
        }
    }

    private void testBooks(PrintWriter out) {
        out.println("<div class='section'>");
        out.println("<h2>📚 Books Data Test</h2>");
        
        try {
            // Get all books
            List<BookDTO> books = bookDAO.getAllBooks();
            
            out.println("<div class='count'>");
            out.println("<strong>Total Books Found: " + books.size() + "</strong>");
            out.println("</div>");
            
            if (books.isEmpty()) {
                out.println("<p class='error'>❌ No books found in database!</p>");
                out.println("<p>Please add some books to test the billing system.</p>");
            } else {
                out.println("<p class='success'>✅ Books data retrieved successfully!</p>");
                
                // Display books table
                out.println("<table>");
                out.println("<tr>");
                out.println("<th>ID</th><th>Title</th><th>Author</th><th>ISBN</th><th>Price</th><th>Quantity</th><th>Category</th>");
                out.println("</tr>");
                
                // Show first 10 books
                int count = Math.min(books.size(), 10);
                for (int i = 0; i < count; i++) {
                    BookDTO book = books.get(i);
                    out.println("<tr>");
                    out.println("<td>" + book.getId() + "</td>");
                    out.println("<td>" + (book.getTitle() != null ? book.getTitle() : "N/A") + "</td>");
                    out.println("<td>" + (book.getAuthor() != null ? book.getAuthor() : "N/A") + "</td>");
                    out.println("<td>" + (book.getIsbn() != null ? book.getIsbn() : "N/A") + "</td>");
                    out.println("<td>Rs. " + (book.getPrice() != null ? book.getPrice() : "0.00") + "</td>");
                    out.println("<td>" + book.getQuantity() + "</td>");
                    out.println("<td>" + (book.getCategory() != null ? book.getCategory() : "N/A") + "</td>");
                    out.println("</tr>");
                }
                
                out.println("</table>");
                
                if (books.size() > 10) {
                    out.println("<p><em>Showing first 10 books. Total: " + books.size() + "</em></p>");
                }
            }
            
        } catch (Exception e) {
            out.println("<p class='error'>❌ Error retrieving books: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        out.println("</div>");
    }

    private void testClients(PrintWriter out) {
        out.println("<div class='section'>");
        out.println("<h2>👥 Clients Data Test</h2>");
        
        try {
            // Get all clients
            List<ClientDTO> clients = clientDAO.getAllClients();
            
            out.println("<div class='count'>");
            out.println("<strong>Total Clients Found: " + clients.size() + "</strong>");
            out.println("</div>");
            
            if (clients.isEmpty()) {
                out.println("<p class='error'>❌ No clients found in database!</p>");
                out.println("<p>Please add some clients to test the billing system.</p>");
            } else {
                out.println("<p class='success'>✅ Clients data retrieved successfully!</p>");
                
                // Display clients table
                out.println("<table>");
                out.println("<tr>");
                out.println("<th>ID</th><th>Account Number</th><th>Name</th><th>Email</th><th>Phone</th><th>Loyalty Points</th><th>Tier</th>");
                out.println("</tr>");
                
                // Show first 10 clients
                int count = Math.min(clients.size(), 10);
                for (int i = 0; i < count; i++) {
                    ClientDTO client = clients.get(i);
                    out.println("<tr>");
                    out.println("<td>" + client.getId() + "</td>");
                    out.println("<td>" + (client.getAccountNumber() != null ? client.getAccountNumber() : "N/A") + "</td>");
                    out.println("<td>" + client.getFullName() + "</td>");
                    out.println("<td>" + (client.getEmail() != null ? client.getEmail() : "N/A") + "</td>");
                    out.println("<td>" + (client.getPhone() != null ? client.getPhone() : "N/A") + "</td>");
                    out.println("<td>" + client.getLoyaltyPointsAsInt() + "</td>");
                    out.println("<td>" + client.getTierLevel() + "</td>");
                    out.println("</tr>");
                }
                
                out.println("</table>");
                
                if (clients.size() > 10) {
                    out.println("<p><em>Showing first 10 clients. Total: " + clients.size() + "</em></p>");
                }
            }
            
        } catch (Exception e) {
            out.println("<p class='error'>❌ Error retrieving clients: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        out.println("</div>");
    }

    private void testBillingSearches(PrintWriter out) {
        out.println("<div class='section'>");
        out.println("<h2>🔍 Billing Search Functions Test</h2>");
        
        // Test client search functions
        out.println("<h3>Client Search Tests:</h3>");
        testClientSearch(out);
        
        // Test book search functions
        out.println("<h3>Book Search Tests:</h3>");
        testBookSearch(out);
        
        out.println("</div>");
    }

    private void testClientSearch(PrintWriter out) {
        try {
            // Get a sample client for testing
            List<ClientDTO> clients = clientDAO.getAllClients();
            
            if (!clients.isEmpty()) {
                ClientDTO sampleClient = clients.get(0);
                out.println("<div class='count'>");
                out.println("<strong>Testing with sample client: " + sampleClient.getFullName() + "</strong>");
                out.println("</div>");
                
                // Test search by account number
                if (sampleClient.getAccountNumber() != null) {
                    List<ClientDTO> accountResults = clientDAO.searchClients("id", sampleClient.getAccountNumber());
                    out.println("<p>🔹 Search by Account Number (" + sampleClient.getAccountNumber() + "): ");
                    if (!accountResults.isEmpty()) {
                        out.println("<span class='success'>✅ Found " + accountResults.size() + " result(s)</span></p>");
                    } else {
                        out.println("<span class='error'>❌ No results found</span></p>");
                    }
                }
                
                // Test search by phone
                if (sampleClient.getPhone() != null) {
                    List<ClientDTO> phoneResults = clientDAO.searchClients("phone", sampleClient.getPhone());
                    out.println("<p>🔹 Search by Phone (" + sampleClient.getPhone() + "): ");
                    if (!phoneResults.isEmpty()) {
                        out.println("<span class='success'>✅ Found " + phoneResults.size() + " result(s)</span></p>");
                    } else {
                        out.println("<span class='error'>❌ No results found</span></p>");
                    }
                }
                
                // Test search by name
                if (sampleClient.getFirstName() != null) {
                    List<ClientDTO> nameResults = clientDAO.searchClients("name", sampleClient.getFirstName());
                    out.println("<p>🔹 Search by Name (" + sampleClient.getFirstName() + "): ");
                    if (!nameResults.isEmpty()) {
                        out.println("<span class='success'>✅ Found " + nameResults.size() + " result(s)</span></p>");
                    } else {
                        out.println("<span class='error'>❌ No results found</span></p>");
                    }
                }
                
            } else {
                out.println("<p class='error'>❌ No clients available for search testing</p>");
            }
            
        } catch (Exception e) {
            out.println("<p class='error'>❌ Error testing client search: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }

    private void testBookSearch(PrintWriter out) {
        try {
            // Get a sample book for testing
            List<BookDTO> books = bookDAO.getAllBooks();
            
            if (!books.isEmpty()) {
                BookDTO sampleBook = books.get(0);
                out.println("<div class='count'>");
                out.println("<strong>Testing with sample book: " + sampleBook.getTitle() + "</strong>");
                out.println("</div>");
                
                // Test search by ISBN
                if (sampleBook.getIsbn() != null) {
                    BookDTO isbnResult = bookDAO.searchBookByISBN(sampleBook.getIsbn());
                    out.println("<p>🔹 Search by ISBN (" + sampleBook.getIsbn() + "): ");
                    if (isbnResult != null) {
                        out.println("<span class='success'>✅ Found book: " + isbnResult.getTitle() + "</span></p>");
                    } else {
                        out.println("<span class='error'>❌ No book found</span></p>");
                    }
                }
                
                // Test search by title
                if (sampleBook.getTitle() != null) {
                    List<BookDTO> titleResults = bookDAO.searchBooksByTitle(sampleBook.getTitle().substring(0, Math.min(5, sampleBook.getTitle().length())));
                    out.println("<p>🔹 Search by Title (partial): ");
                    if (!titleResults.isEmpty()) {
                        out.println("<span class='success'>✅ Found " + titleResults.size() + " result(s)</span></p>");
                    } else {
                        out.println("<span class='error'>❌ No results found</span></p>");
                    }
                }
                
                // Test search by author
                if (sampleBook.getAuthor() != null) {
                    List<BookDTO> authorResults = bookDAO.searchBooksByAuthor(sampleBook.getAuthor());
                    out.println("<p>🔹 Search by Author (" + sampleBook.getAuthor() + "): ");
                    if (!authorResults.isEmpty()) {
                        out.println("<span class='success'>✅ Found " + authorResults.size() + " result(s)</span></p>");
                    } else {
                        out.println("<span class='error'>❌ No results found</span></p>");
                    }
                }
                
            } else {
                out.println("<p class='error'>❌ No books available for search testing</p>");
            }
            
        } catch (Exception e) {
            out.println("<p class='error'>❌ Error testing book search: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }

    private void handleError(HttpServletResponse response, String errorMessage) 
            throws IOException {
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Test Error</title></head>");
            out.println("<body>");
            out.println("<h1 style='color: red;'>❌ Test Failed</h1>");
            out.println("<p>" + errorMessage + "</p>");
            out.println("<p>Please check your database connection and configuration.</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}