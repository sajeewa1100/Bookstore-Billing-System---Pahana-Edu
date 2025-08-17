package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BookDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Dummy categories
        List<String> categories = new ArrayList<>();
        categories.add("Fiction");
        categories.add("Science");
        categories.add("Technology");

        // Dummy books
        List<BookDTO> books = new ArrayList<>();
        books.add(new BookDTO(1, "Java Basics", "John Doe", 500.0, "Technology", "1234567890", 10, "Tech Publisher"));
        books.add(new BookDTO(2, "Science 101", "Jane Smith", 400.0, "Science", "0987654321", 5, "Science House"));

        // Send data to JSP
        request.setAttribute("categories", categories);
        request.setAttribute("books", books);

        // Forward to test JSP
        request.getRequestDispatcher("views/testBooks.jsp").forward(request, response);
    }
}
