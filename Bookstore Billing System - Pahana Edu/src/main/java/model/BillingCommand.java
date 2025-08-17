
package model;

import service.BillingService;
import service.ClientService;
import service.BookService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Interface for Billing Commands
 */
public interface BillingCommand {
    void execute(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException;
}