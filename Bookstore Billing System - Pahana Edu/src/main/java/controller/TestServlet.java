package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import service.BillingService;
import service.BookService;
import service.ClientService;
import model.BookDTO;
import model.ClientDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Quick test servlet to verify services are working
 * Access via: /TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Service Test Results</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".success { color: green; background: #d4edda; padding: 10px; margin: 5px 0; border-radius: 5px; }");
        out.println(".error { color: #721c24; background: #f8d7da; padding: 10px; margin: 5px 0; border-radius: 5px; }");
        out.println(".info { color: #004085; background: #cce7ff; padding: 10px; margin: 5px 0; border-radius: 5px; }");
        out.println("table { border-collapse: collapse; width: 100%; margin: 10px 0; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println("</style>");
        out.println("</head><body>");
        
        out.println("<h1>🧪 Pahana Bookstore - Service Test Results</h1>");
        out.println("<p><strong>Test Date:</strong> " + new java.util.Date() + "</p>");
        
        // Test BookService
        out.println("<h2>📚 BookService Test</h2>");
        try {
            BookService bookService = new BookService();
            out.println("<div class='success'>✅ BookService created successfully</div>");
            
            // Test getAllBooks
            List<BookDTO> books = bookService.getAllBooks();
            if (books != null) {
                out.println("<div class='success'>✅ getAllBooks() returned " + books.size() + " books</div>");
                
                if (!books.isEmpty()) {
                    out.println("<h3>Sample Books (first 5):</h3>");
                    out.println("<table>");
                    out.println("<tr><th>ID</th><th>Title</th><th>Author</th><th>Price</th><th>Stock</th></tr>");
                    
                    for (int i = 0; i < Math.min(5, books.size()); i++) {
                        BookDTO book = books.get(i);
                        out.println("<tr>");
                        out.println("<td>" + book.getId() + "</td>");
                        out.println("<td>" + book.getTitle() + "</td>");
                        out.println("<td>" + book.getAuthor() + "</td>");
                        out.println("<td>Rs. " + book.getPrice() + "</td>");
                        out.println("<td>" + book.getQuantity() + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                    
                    // Test getBookById with first book
                    BookDTO firstBook = books.get(0);
                    BookDTO testBook = bookService.getBookById(firstBook.getId());
                    if (testBook != null) {
                        out.println("<div class='success'>✅ getBookById(" + firstBook.getId() + ") works correctly</div>");
                    } else {
                        out.println("<div class='error'>❌ getBookById(" + firstBook.getId() + ") returned null</div>");
                    }
                    
                    // Test searchBookByISBN if available
                    if (firstBook.getIsbn() != null && !firstBook.getIsbn().trim().isEmpty()) {
                        BookDTO isbnBook = bookService.searchBookByISBN(firstBook.getIsbn());
                        if (isbnBook != null) {
                            out.println("<div class='success'>✅ searchBookByISBN('" + firstBook.getIsbn() + "') works correctly</div>");
                        } else {
                            out.println("<div class='error'>❌ searchBookByISBN('" + firstBook.getIsbn() + "') returned null</div>");
                        }
                    }
                } else {
                    out.println("<div class='info'>ℹ️ No books found in database - please add some books first</div>");
                }
            } else {
                out.println("<div class='error'>❌ getAllBooks() returned null</div>");
            }
            
        } catch (Exception e) {
            out.println("<div class='error'>❌ BookService Error: " + e.getMessage() + "</div>");
            out.println("<div class='error'>Stack trace: " + getStackTrace(e) + "</div>");
        }
        
        // Test ClientService
        out.println("<h2>👥 ClientService Test</h2>");
        try {
            ClientService clientService = new ClientService();
            out.println("<div class='success'>✅ ClientService created successfully</div>");
            
            List<ClientDTO> clients = clientService.getAllClients();
            if (clients != null) {
                out.println("<div class='success'>✅ getAllClients() returned " + clients.size() + " clients</div>");
                
                if (!clients.isEmpty()) {
                    out.println("<h3>Available Clients:</h3>");
                    out.println("<table>");
                    out.println("<tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Tier</th><th>Points</th></tr>");
                    
                    for (ClientDTO client : clients) {
                        out.println("<tr>");
                        out.println("<td>" + client.getId() + "</td>");
                        out.println("<td>" + client.getFullName() + "</td>");
                        out.println("<td>" + (client.getEmail() != null ? client.getEmail() : "N/A") + "</td>");
                        out.println("<td>" + (client.getPhone() != null ? client.getPhone() : "N/A") + "</td>");
                        out.println("<td>" + (client.getTierLevel() != null ? client.getTierLevel() : "N/A") + "</td>");
                        out.println("<td>" + (client.getLoyaltyPoints() != null ? client.getLoyaltyPoints() : 0) + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                    
                    // Test getClientById with first client
                    ClientDTO firstClient = clients.get(0);
                    ClientDTO testClient = clientService.getClientById(firstClient.getId());
                    if (testClient != null) {
                        out.println("<div class='success'>✅ getClientById(" + firstClient.getId() + ") works correctly</div>");
                    } else {
                        out.println("<div class='error'>❌ getClientById(" + firstClient.getId() + ") returned null</div>");
                    }
                } else {
                    out.println("<div class='info'>ℹ️ No clients found - using fallback clients</div>");
                }
            } else {
                out.println("<div class='error'>❌ getAllClients() returned null</div>");
            }
            
        } catch (Exception e) {
            out.println("<div class='error'>❌ ClientService Error: " + e.getMessage() + "</div>");
            out.println("<div class='error'>Stack trace: " + getStackTrace(e) + "</div>");
        }
        
        // Test BillingService
        out.println("<h2>🧾 BillingService Test</h2>");
        try {
            BillingService billingService = new BillingService();
            out.println("<div class='success'>✅ BillingService created successfully</div>");
            
            // Test basic methods
            int totalBills = billingService.getAllBills().size();
            out.println("<div class='success'>✅ getAllBills() returned " + totalBills + " bills</div>");
            
        } catch (Exception e) {
            out.println("<div class='error'>❌ BillingService Error: " + e.getMessage() + "</div>");
            out.println("<div class='error'>Stack trace: " + getStackTrace(e) + "</div>");
        }
        
        // Database Connection Test
        out.println("<h2>🗄️ Database Connection Test</h2>");
        try {
            java.sql.Connection conn = util.ConnectionManager.getInstance().getConnection();
            if (conn != null && !conn.isClosed()) {
                out.println("<div class='success'>✅ Database connection successful</div>");
                
                // Test basic query
                java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as count FROM books");
                java.sql.ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int bookCount = rs.getInt("count");
                    out.println("<div class='success'>✅ Database query successful - Found " + bookCount + " books in database</div>");
                }
                rs.close();
                stmt.close();
            } else {
                out.println("<div class='error'>❌ Database connection failed or is closed</div>");
            }
        } catch (Exception e) {
            out.println("<div class='error'>❌ Database Error: " + e.getMessage() + "</div>");
            out.println("<div class='error'>Stack trace: " + getStackTrace(e) + "</div>");
        }
        
        // Summary and Next Steps
        out.println("<h2>📋 Test Summary & Next Steps</h2>");
        out.println("<div class='info'>");
        out.println("<h3>✅ If all tests passed:</h3>");
        out.println("<ul>");
        out.println("<li>Your services are working correctly</li>");
        out.println("<li>Try accessing: <a href='" + request.getContextPath() + "/BillingServlet?action=create'>Create New Bill</a></li>");
        out.println("<li>Try accessing: <a href='" + request.getContextPath() + "/BillingServlet?action=billings'>View All Bills</a></li>");
        out.println("<li>Try accessing: <a href='" + request.getContextPath() + "/BookServlet?action=books'>Manage Books</a></li>");
        out.println("</ul>");
        out.println("</div>");
        
        out.println("<div class='info'>");
        out.println("<h3>❌ If any tests failed:</h3>");
        out.println("<ul>");
        out.println("<li>Check your database connection settings</li>");
        out.println("<li>Ensure all required tables exist (books, billings, bill_items, etc.)</li>");
        out.println("<li>Verify your DAO classes are properly configured</li>");
        out.println("<li>Check the server logs for more detailed error messages</li>");
        out.println("</ul>");
        out.println("</div>");
        
        out.println("<p><a href='" + request.getContextPath() + "'>← Back to Home</a></p>");
        out.println("</body></html>");
    }
    
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }
}