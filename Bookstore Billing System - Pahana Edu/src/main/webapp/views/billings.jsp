<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Bill - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        .billing-form { max-width: 1200px; margin: 0 auto; }
        .form-section { margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background: #f9f9f9; }
        .form-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-bottom: 20px; }
        .form-group { display: flex; flex-direction: column; }
        .form-group label { margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group select, .form-group textarea { padding: 10px; border: 1px solid #ccc; border-radius: 4px; }
        .search-section { display: flex; gap: 10px; align-items: end; }
        .btn-search { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .btn-search:hover { background-color: #0056b3; }
        .btn-search:disabled { background-color: #6c757d; cursor: not-allowed; }
        .client-info { background-color: #e8f4f8; padding: 15px; border-radius: 5px; margin-top: 15px; display: none; }
        .books-table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        .books-table th, .books-table td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }
        .books-table th { background-color: #f5f5f5; font-weight: bold; }
        .books-table tr:hover { background-color: #f9f9f9; }
        .quantity-input { width: 80px; text-align: center; }
        .totals-section { background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-top: 20px; }
        .total-row { display: flex; justify-content: space-between; margin-bottom: 10px; }
        .final-total { font-weight: bold; font-size: 18px; border-top: 2px solid #333; padding-top: 10px; }
        .form-actions { display: flex; gap: 15px; justify-content: flex-end; margin-top: 30px; }
        .btn-primary { background-color: #28a745; color: white; padding: 15px 30px; border: none; border-radius: 5px; font-size: 16px; cursor: pointer; }
        .btn-secondary { background-color: #6c757d; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-size: 16px; }
        .btn-primary:hover { background-color: #218838; }
        .btn-primary:disabled { background-color: #6c757d; cursor: not-allowed; }
        .btn-secondary:hover { background-color: #545b62; }
        .error-message { background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin: 10px 0; }
        .success-message { background-color: #d4edda; color: #155724; padding: 10px; border-radius: 5px; margin: 10px 0; }
        .debug-info { background-color: #fff3cd; padding: 10px; border-radius: 5px; margin: 10px 0; font-family: monospace; }
        .selected-books { margin-top: 20px; }
        .selected-book-item { background: #e8f5e8; padding: 10px; margin: 5px 0; border-radius: 5px; display: flex; justify-content: space-between; align-items: center; }
        .remove-book { background: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer; }
        .text-success { color: #28a745; }
        .text-danger { color: #dc3545; }
        .client-list { max-height: 150px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; border-radius: 5px; background: white; }
        .client-item { padding: 5px; border-bottom: 1px solid #eee; cursor: pointer; }
        .client-item:hover { background-color: #f0f0f0; }
        .client-item:last-child { border-bottom: none; }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <div class="top-bar">
        <h2 class="section-title">🧾 Create New Bill</h2>
    </div>
    
    <hr />

    <!-- Debug Information -->
    <div class="debug-info" id="debugInfo" style="display: none;"></div>

    <!-- Display Success/Error Messages -->
    <div class="success-message" id="successMessage" style="display: none;"></div>
    <div class="error-message" id="errorMessage" style="display: none;"></div>

    <div class="billing-form">
        <form id="billingForm" method="post" action="${pageContext.request.contextPath}/BillingServlet?action=create">
            
            <!-- Client Information Section -->
            <div class="form-section">
                <h3 class="section-title"><i class="fas fa-user"></i> Customer Information</h3>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="clientPhone">Phone Number (Optional)</label>
                        <div class="search-section">
                            <input type="text" id="clientPhone" name="clientPhone" placeholder="Enter phone number">
                            <button type="button" class="btn-search" onclick="searchClient()">
                                <i class="fas fa-search"></i> Search
                            </button>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="clientName">Customer Name *</label>
                        <input type="text" id="clientName" name="clientName" required placeholder="Enter customer name">
                        <input type="hidden" id="clientId" name="clientId">
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="clientEmail">Email (Optional)</label>
                        <input type="email" id="clientEmail" name="clientEmail" placeholder="customer@email.com">
                    </div>
                </div>
                
                <!-- Client Info Display -->
                <div id="clientInfo" class="client-info">
                    <h4><i class="fas fa-info-circle"></i> Customer Details</h4>
                    <div id="clientDetails"></div>
                </div>
                
                <div class="error-message" id="clientError" style="display: none;"></div>
                
                <!-- Clients List for Reference -->
                <div style="margin-top: 15px;" id="clientsList">
                    <h5>Available Clients:</h5>
                    <div class="client-list" id="clientListContainer">
                        <!-- Dynamically filled by JavaScript -->
                    </div>
                </div>
            </div>

            <!-- Book Selection Section -->
            <div class="form-section">
                <h3 class="section-title"><i class="fas fa-book"></i> Book Selection</h3>
                
                <div class="search-section" style="margin-bottom: 15px;">
                    <div class="form-group" style="flex: 1;">
                        <label for="bookIsbn">Search by ISBN</label>
                        <input type="text" id="bookIsbn" placeholder="Enter ISBN">
                    </div>
                    <button type="button" class="btn-search" onclick="searchBook()" style="margin-top: 25px;">
                        <i class="fas fa-search"></i> Search Book
                    </button>
                </div>

                <div class="error-message" id="bookError" style="display: none;"></div>

                <!-- Books Display Logic -->
                <div id="booksTableContainer" style="display: none;">
                    <h4>Available Books</h4>
                    <table class="books-table" id="booksTable">
                        <thead>
                            <tr>
                                <th>Book Details</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Quantity</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody id="booksTableBody">
                            <!-- Dynamically filled by JavaScript -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Payment Information Section -->
            <div class="form-section">
                <h3 class="section-title"><i class="fas fa-credit-card"></i> Payment Information</h3>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="paymentMethod">Payment Method *</label>
                        <select id="paymentMethod" name="paymentMethod" required>
                            <option value="CASH">Cash</option>
                            <option value="CARD">Card</option>
                            <option value="DIGITAL">Digital Payment</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="notes">Notes (Optional)</label>
                    <textarea id="notes" name="notes" rows="3" placeholder="Any additional notes..."></textarea>
                </div>
            </div>

            <!-- Bill Totals Section -->
            <div class="totals-section" id="totalsSection" style="display: none;">
                <h3 class="section-title"><i class="fas fa-calculator"></i> Bill Summary</h3>
                
                <div class="total-row">
                    <span>Subtotal:</span>
                    <span id="subtotal">Rs. 0.00</span>
                </div>
                <div class="total-row">
                    <span>Tax (8%):</span>
                    <span id="taxAmount">Rs. 0.00</span>
                </div>
                <div class="total-row final-total">
                    <span>Total Amount:</span>
                    <span id="totalAmount">Rs. 0.00</span>
                </div>
            </div>

            <!-- Form Actions -->
            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/BillingServlet?action=billings" class="btn-secondary">
                    <i class="fas fa-arrow-left"></i> Cancel
                </a>
                <button type="submit" class="btn-primary" id="submitBtn" disabled>
                    <i class="fas fa-save"></i> Create Bill
                </button>
            </div>

            <!-- Hidden inputs for selected books -->
            <div id="hiddenInputs"></div>
        </form>
    </div>
</main>

<script>
    let selectedBooks = [];
    let allBooks = [];
    let allClients = [];

    document.addEventListener('DOMContentLoaded', function() {
        // Populate allBooks and allClients dynamically
        allBooks = [
            // Example static data; replace with actual data from server-side
            { id: 1, title: "Book One", author: "Author A", price: 200, stock: 10, isbn: "1234567890", category: "Fiction" },
            { id: 2, title: "Book Two", author: "Author B", price: 150, stock: 5, isbn: "0987654321", category: "Non-Fiction" }
        ];

        allClients = [
            // Example static data; replace with actual data from server-side
            { id: 1, fullName: "Client One", email: "client1@example.com", phone: "111-111-1111", tierLevel: "Gold", loyaltyPoints: 100 },
            { id: 2, fullName: "Client Two", email: "client2@example.com", phone: "222-222-2222", tierLevel: "Silver", loyaltyPoints: 50 }
        ];

        // Populate clients list
        let clientListContainer = document.getElementById("clientListContainer");
        allClients.forEach(client => {
            let clientDiv = document.createElement("div");
            clientDiv.classList.add("client-item");
            clientDiv.innerHTML = `<strong>${client.fullName}</strong><br><small>Email: ${client.email}</small><br><small>Phone: ${client.phone}</small>`;
            clientDiv.onclick = function() { selectClient(client.id, client.fullName, client.email, client.phone); };
            clientListContainer.appendChild(clientDiv);
        });

        // Populate book table
        let booksTableBody = document.getElementById("booksTableBody");
        allBooks.forEach(book => {
            let row = document.createElement("tr");
            row.innerHTML = `
                <td><strong>${book.title}</strong><br><em>by ${book.author}</em></td>
                <td>Rs. ${book.price}</td>
                <td>${book.stock}</td>
                <td><input type="number" id="qty_${book.id}" min="1" max="${book.stock}" value="1"></td>
                <td><button type="button" class="btn-search" onclick="addBook(${book.id})"><i class="fas fa-plus"></i> Add</button></td>
            `;
            booksTableBody.appendChild(row);
        });
    });

    function selectClient(id, name, email, phone) {
        document.getElementById("clientId").value = id;
        document.getElementById("clientName").value = name;
        document.getElementById("clientEmail").value = email;
        document.getElementById("clientPhone").value = phone;
        document.getElementById("clientInfo").style.display = "block";
        document.getElementById("clientDetails").innerHTML = `
            <p><strong>Name:</strong> ${name}</p>
            <p><strong>Email:</strong> ${email}</p>
            <p><strong>Phone:</strong> ${phone}</p>
        `;
    }

    function searchClient() {
        let phone = document.getElementById("clientPhone").value.trim();
        let client = allClients.find(c => c.phone === phone);
        if (client) {
            selectClient(client.id, client.fullName, client.email, client.phone);
        } else {
            alert("Client not found!");
        }
    }

    function searchBook() {
        let isbn = document.getElementById("bookIsbn").value.trim();
        let book = allBooks.find(b => b.isbn === isbn);
        if (book) {
            alert(`Found book: ${book.title}`);
        } else {
            alert("Book not found!");
        }
    }

    function addBook(bookId) {
        let book = allBooks.find(b => b.id === bookId);
        let qty = document.getElementById(`qty_${bookId}`).value;
        if (qty <= 0 || qty > book.stock) {
            alert("Invalid quantity");
            return;
        }

        let existingBook = selectedBooks.find(b => b.id === bookId);
        if (existingBook) {
            existingBook.quantity += parseInt(qty);
        } else {
            selectedBooks.push({...book, quantity: qty});
        }
        updateSelectedBooks();
        calculateTotals();
    }

    function updateSelectedBooks() {
        let selectedBooksList = document.getElementById("selectedBooksList");
        selectedBooksList.innerHTML = "";
        selectedBooks.forEach(book => {
            let item = document.createElement("div");
            item.classList.add("selected-book-item");
            item.innerHTML = `
                <div><strong>${book.title}</strong> × ${book.quantity}</div>
                <button class="remove-book" onclick="removeBook(${book.id})">Remove</button>
            `;
            selectedBooksList.appendChild(item);
        });
    }

    function removeBook(bookId) {
        selectedBooks = selectedBooks.filter(b => b.id !== bookId);
        updateSelectedBooks();
        calculateTotals();
    }

    function calculateTotals() {
        let subtotal = selectedBooks.reduce((total, book) => total + (book.price * book.quantity), 0);
        let tax = subtotal * 0.08;
        let total = subtotal + tax;

        document.getElementById("subtotal").textContent = `Rs. ${subtotal.toFixed(2)}`;
        document.getElementById("taxAmount").textContent = `Rs. ${tax.toFixed(2)}`;
        document.getElementById("totalAmount").textContent = `Rs. ${total.toFixed(2)}`;

        document.getElementById("totalsSection").style.display = "block";
    }
</script>

</body>
</html>
