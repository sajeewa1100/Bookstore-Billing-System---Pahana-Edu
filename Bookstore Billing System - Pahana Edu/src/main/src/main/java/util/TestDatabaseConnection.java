package util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/test-connection")
public class TestDatabaseConnection extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Establishing database connection
            Connection connection = ConnectionManager.getConnection();
            
            // Fetching data from the 'clients' table
            String query = "SELECT * FROM clients";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            // Displaying the results
            out.println("<h1>Client Data:</h1>");
            out.println("<table border='1'>");
            out.println("<tr><th>Client ID</th><th>Account Number</th><th>First Name</th><th>Last Name</th><th>Email</th><th>Phone</th></tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("account_number") + "</td>");
                out.println("<td>" + rs.getString("first_name") + "</td>");
                out.println("<td>" + rs.getString("last_name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("<td>" + rs.getString("phone") + "</td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
            
            // Closing the resources
            rs.close();
            stmt.close();
            connection.close();

        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
        } finally {
            out.close();
        }
    }
}
