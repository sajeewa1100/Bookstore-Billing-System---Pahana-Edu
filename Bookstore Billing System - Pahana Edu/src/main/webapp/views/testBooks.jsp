<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.BookDTO" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Books</title>
</head>
<body>
    <h2>Categories:</h2>
    <%
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) request.getAttribute("categories");
        for (String cat : categories) {
            out.println("<p>" + cat + "</p>");
        }
    %>

    <h2>Books:</h2>
    <%
        @SuppressWarnings("unchecked")
        List<BookDTO> books = (List<BookDTO>) request.getAttribute("books");
        for (BookDTO book : books) {
            out.println("<div>");
            out.println("<p>Title: " + book.getTitle() + "</p>");
            out.println("<p>Author: " + book.getAuthor() + "</p>");
            out.println("<p>Category: " + book.getCategory() + "</p>");
            out.println("<p>Price: Rs. " + book.getPrice() + "</p>");
            out.println("</div><hr/>");
        }
    %>
</body>
</html>
