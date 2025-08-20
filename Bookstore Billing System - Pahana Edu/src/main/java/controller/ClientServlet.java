package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import service.ClientService;
import model.*;
import java.io.IOException;

@WebServlet("/ClientServlet")
public class ClientServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clientService = new ClientService();
    }

    /**
     * Handle GET requests (list, view, search)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("clients".equals(action) || "list".equals(action) || action == null) {
            handleListClients(request, response);
        } else if ("view".equals(action)) {
            handleViewClient(request, response);
        } else if ("search".equals(action)) {
            handleSearchClients(request, response);
        } else if ("profile".equals(action)) {
            handleClientProfile(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
        }
    }

    /**
     * Handle POST requests (add, update, delete)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("add".equals(action) || "create".equals(action)) {
                handleAddClient(request, response);
            } else if ("update".equals(action) || "edit".equals(action)) {
                handleUpdateClient(request, response);
            } else if ("delete".equals(action)) {
                handleDeleteClient(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ClientServlet?action=clients");
        }
    }

    /**
     * Handle listing all clients using Command pattern
     */
    private void handleListClients(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClientCommand viewClientsCommand = new ViewClientsCommand(clientService);
        viewClientsCommand.execute(request, response);
    }

    /**
     * Handle adding a new client using Command pattern
     */
    private void handleAddClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClientCommand addClientCommand = new AddClientCommand(clientService);
        addClientCommand.execute(request, response);
    }

    /**
     * Handle viewing a specific client using Command pattern
     */
    private void handleViewClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClientCommand viewClientCommand = new ViewClientsCommand(clientService);
        viewClientCommand.execute(request, response);
    }

    /**
     * Handle updating an existing client using Command pattern
     */
    private void handleUpdateClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClientCommand updateClientCommand = new UpdateClientCommand(clientService);
        updateClientCommand.execute(request, response);
    }

    /**
     * Handle deleting a client using Command pattern
     */
    private void handleDeleteClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClientCommand deleteClientCommand = new DeleteClientCommand(clientService);
        deleteClientCommand.execute(request, response);
    }

    /**
     * Handle client profile (AJAX) - This doesn't need Command pattern as it's a simple response
     */
    private void handleClientProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String clientId = request.getParameter("id");
        try {
            Long clientIdLong = Long.parseLong(clientId);
          
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
       
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<p class='error'>Error loading client profile.</p>");
        }
    }

    /**
     * Handle searching clients using Command pattern
     */
    private void handleSearchClients(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ClientCommand searchClientsCommand = new SearchClientsCommand(clientService);
        searchClientsCommand.execute(request, response);
    }
}