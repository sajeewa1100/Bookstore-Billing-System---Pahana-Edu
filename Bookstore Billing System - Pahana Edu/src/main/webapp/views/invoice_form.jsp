<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Invoice - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        .invoice-container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .invoice-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem;
            text-align: center;
        }
        
        .invoice-content {
            padding: 2rem;
        }
        
        .form-section {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            padding: 1.5rem;
            margin-bottom: 2rem;
        }
        
        .section-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #495057;
            margin-bottom: 1rem;
            border-bottom: 2px solid #007bff;
            padding-bottom: 0.5rem;
        }
        
        .client-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .form-group {
            margin-bottom: 1rem;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: #495057;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 0.9rem;
            transition: border-color 0.15s ease-in-out;
        }
        
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
        }
        
        .books-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }
        
        .books-table th,
        .books-table td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }
        
        .books-table th {
            background-color: #e9ecef;
            font-weight: 600;
            color: #495057;
        }
        
        .quantity-input {
            width: 80px;
            text-align: center;
        }
        
        .remove-btn {
            background: #dc3545;
            color: white;
            border: none;
            padding: 0.25rem 0.5rem;
            border-radius: 3px;
            cursor: pointer;
            font-size: 0.8rem;
        }
        
        .remove-btn:hover {
            background: #c82333;
        }
        
        .add-book-section {
            display: flex;
            gap: 1rem;
            align-items: end;
            flex-wrap: wrap;
            margin-bottom: 1rem;
        }
        
        .add-book-btn {
            background: #28a745;
            color: white;
            border: none;
            padding: 0.75rem 1rem;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 500;
        }
        
        .add-book-btn:hover {
            background: #218838;
        }
        
        .invoice-summary {
            background: #f8f9fa;
            border: 2px solid #007bff;
            border-radius: 6px;
            padding: 1.5rem;
            margin-top: 2rem;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
            padding: 0.25rem 0;
        }
        
        .summary-row.total {
            font-size: 1.2rem;
            font-weight: bold;
            border-top: 2px solid #007bff;
            padding-top: 0.75rem;
            margin-top: 0.75rem;
            color: #007bff;
        }
        
        .form-actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-top: 2rem;
            padding-top: 2rem;
            border-top: 1px solid #dee2e6;
        }
        
        .btn-primary {
            background: #007bff;
            color: white;
            border: none;
            padding: 0.75rem 2rem;
            border-radius: 4px;
            font-size: 1rem;
            font-weight: 500;
            cursor: pointer;
            transition: background 0.15s;
        }
        
        .btn-primary:hover {
            background: #0056b3;
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
            border: none;
            padding: 0.75rem 2rem;
            border-radius: 4px;
            font-size: 1rem;
            font-weight: 500;
            cursor: pointer;
            transition: background 0.15s;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }
        
        .btn-secondary:hover {
            background: #545b62;
        }
        
        .client-preview {
            background: #e7f3ff;
            border: 1px solid #b8daff;
            border-radius: 4px;
            padding: 1rem;
            margin-top: 1rem;
        }
        
        .tier-badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .tier-bronze { background-color: #cd7f32; color: white; }
        .tier-silver { background-color: #c0c0c0; color: #333; }
        .tier-gold { background-color: #ffd700; color: #333; }
        .tier-platinum { background-color: #e5e4e2; color: #333; }
        .tier-member { background-color: #6c757d; color: white; }
        
        .error-message {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            border-radius: 4px;
            padding: 0.75rem;
            margin-bottom: 1rem;
        }
        
        .success-message {
            background: #d1eddc;
            color: #155724;
            border: 1px solid #c3e6cb;
            border-radius: 4px;
            padding: 0.75rem;
            margin-bottom: 1rem;
        }
        
        .loading {
            opacity: 0.6;
            pointer-events: none;
        }
        
        @media (max-width: 768px) {
            .invoice-content {
                padding: 1rem;
            }
            
            .client-info {
                grid-template-columns: 1fr;
            }
            
            .add-book-section {
                flex-direction: column;
                align-items: stretch;
            }
            
            .books-table {
                font-size: 0.9rem;
            }
            
            .form-actions {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <div class="invoice-container">
        <div class="invoice-header">
            <h1><i class="fas fa-file-invoice-dollar"></i> Create New Invoice</h1>
            <p>Generate a new invoice for book purchases</p>
        </div>
        
        <div class="invoice-content">
            <!-- Display Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty successMessage}">
                <div class="success-message">
                    <i class="fas fa-check-circle"></i> ${successMessage}
                </div>
            </c:if>
            
            <form id="invoiceForm" action="${pageContext.request.contextPath}/BillingServlet" method="post">
                <input type="hidden" name="action" value="create" />
                
                <!-- Client Selection Section -->
                <div class="form-section">
                    <h3 class="section-title"><i class="fas fa-user"></i> Client Information</h3>
                    
                    <div class="form-group">
                        <label for="clientSelect">Select Client: <span style="color: red;">*</span></label>
                        <select id="clientSelect" name="clientId" required onchange="loadClientInfo()">
                            <option value="">-- Select a Client --</option>
                            <c:forEach var="client" items="${clients}">
                                <option value="${client.id}" 
                                    data-name="${client.firstName} ${client.lastName}"
                                    data-email="${client.email}"
                                    data-phone="${client.phone}"
                                    data-points="${client.loyaltyPoints}"
                                    data-tier="${client.tierLevel}">
                                    ${client.accountNumber} - ${client.firstName} ${client.lastName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div id="clientPreview" class="client-preview" style="display: none;">
                        <div class="client-info">
                            <div>
                                <strong>Name:</strong> <span id="clientName">-</span>
                            </div>
                            <div>
                                <strong>Email:</strong> <span id="clientEmail">-</span>
                            </div>
                            <div>
                                <strong>Phone:</strong> <span id="clientPhone">-</span>
                            </div>
                            <div>
                                <strong>Loyalty Points:</strong> <span id="clientPoints">0</span>
                            </div>
                            <div>
                                <strong>Tier Level:</strong> <span id="clientTier" class="tier-badge">MEMBER</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Books Selection Section -->
                <div class="form-section">
                    <h3 class="section-title"><i class="fas fa-book"></i> Books Selection</h3>
                    
                    <div class="add-book-section">
                        <div class="form-group" style="flex: 2; margin-bottom: 0;">
                            <label for="bookSelect">Select Book:</label>
                            <select id="bookSelect">
                                <option value="">-- Select a Book --</option>
                                <c:forEach var="book" items="${books}">
                                    <option value="${book.id}" 
                                        data-title="${book.title}"
                                        data-author="${book.author}"
                                        data-price="${book.price}"
                                        data-stock="${book.quantity}">
                                        ${book.title} by ${book.author} (Stock: ${book.quantity}, Rs. ${book.price})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="form-group" style="margin-bottom: 0;">
                            <label for="quantityInput">Quantity:</label>
                            <input type="number" id="quantityInput" min="1" value="1" class="quantity-input">
                        </div>
                        
                        <button type="button" class="add-book-btn" onclick="addBookToInvoice()">
                            <i class="fas fa-plus"></i> Add Book
                        </button>
                    </div>
                    
                    <div id="selectedBooksSection" style="display: none;">
                        <h4>Selected Books:</h4>
                        <table class="books-table">
                            <thead>
                                <tr>
                                    <th>Book Title</th>
                                    <th>Author</th>
                                    <th>Unit Price</th>
                                    <th>Quantity</th>
                                    <th>Total</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody id="selectedBooksTable">
                                <!-- Selected books will be added here -->
                            </tbody>
                        </table>
                    </div>
                </div>
                
                <!-- Invoice Summary Section -->
                <div id="invoiceSummary" class="invoice-summary" style="display: none;">
                    <h3 class="section-title"><i class="fas fa-calculator"></i> Invoice Summary</h3>
                    
                    <div class="summary-row">
                        <span>Subtotal:</span>
                        <span>Rs. <span id="subtotalAmount">0.00</span></span>
                    </div>
                    
                    <div class="summary-row">
                        <span>Discount (<span id="discountPercent">0%</span>):</span>
                        <span>- Rs. <span id="discountAmount">0.00</span></span>
                    </div>
                    
                    <div class="summary-row total">
                        <span>Total Amount:</span>
                        <span>Rs. <span id="totalAmount">0.00</span></span>
                    </div>
                    
                    <div class="summary-row">
                        <span>Loyalty Points Earned:</span>
                        <span><span id="pointsEarned">0</span> points</span>
                    </div>
                </div>
                
                <!-- Additional Notes Section -->
                <div class="form-section">
                    <h3 class="section-title"><i class="fas fa-sticky-note"></i> Additional Notes</h3>
                    
                    <div class="form-group">
                        <label for="notes">Invoice Notes (Optional):</label>
                        <textarea id="notes" name="notes" rows="3" 
                            placeholder="Enter any additional notes for this invoice..."
                            style="width: 100%; padding: 0.75rem; border: 1px solid #ced4da; border-radius: 4px; resize: vertical;"></textarea>
                    </div>
                </div>
                
                <!-- Hidden fields for form submission -->
                <input type="hidden" id="selectedBooksData" name="selectedBooks" value="" />
                
                <!-- Form Actions -->
                <div class="form-actions">
                    <button type="button" class="btn-primary" onclick="previewInvoice()" id="previewBtn" disabled>
                        <i class="fas fa-eye"></i> Preview Invoice
                    </button>
                    
                    <button type="submit" class="btn-primary" id="createBtn" disabled>
                        <i class="fas fa-save"></i> Create Invoice
                    </button>
                    
                    <a href="${pageContext.request.contextPath}/BillingServlet?action=invoices" class="btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back to Invoices
                    </a>
                </div>
            </form>
        </div>
    </div>
</main>

<!-- Invoice Preview Modal -->
<div id="previewModal" class="modal" style="display: none;">
    <div class="modal-content" style="max-width: 800px;">
        <div class="modal-header">
            <h3><i class="fas fa-file-invoice"></i> Invoice Preview</h3>
            <span class="close" onclick="closePreviewModal()">&times;</span>
        </div>
        <hr />
        
        <div id="previewContent">
            <!-- Preview content will be loaded here -->
        </div>
        
        <div class="form-actions">
            <button type="button" class="btn-primary" onclick="confirmCreateInvoice()">
                <i class="fas fa-check"></i> Confirm & Create
            </button>
            <button type="button" class="btn-secondary" onclick="closePreviewModal()">
                <i class="fas fa-times"></i> Close Preview
            </button>
        </div>
    </div>
</div>

<script>
    // Global variables
    let selectedBooks = [];
    let currentClient = null;
    let tierDiscounts = {
        'MEMBER': 0,
        'BRONZE': 0.02,
        'SILVER': 0.05,
        'GOLD': 0.08,
        'PLATINUM': 0.12
    };
    
    // Load client information when selected
    function loadClientInfo() {
        const select = document.getElementById('clientSelect');
        const selectedOption = select.options[select.selectedIndex];
        const preview = document.getElementById('clientPreview');
        
        if (selectedOption.value) {
            currentClient = {
                id: selectedOption.value,
                name: selectedOption.dataset.name,
                email: selectedOption.dataset.email,
                phone: selectedOption.dataset.phone,
                points: parseInt(selectedOption.dataset.points),
                tier: selectedOption.dataset.tier
            };
            
            document.getElementById('clientName').textContent = currentClient.name;
            document.getElementById('clientEmail').textContent = currentClient.email;
            document.getElementById('clientPhone').textContent = currentClient.phone;
            document.getElementById('clientPoints').textContent = currentClient.points;
            
            const tierBadge = document.getElementById('clientTier');
            tierBadge.textContent = currentClient.tier;
            tierBadge.className = 'tier-badge tier-' + currentClient.tier.toLowerCase();
            
            preview.style.display = 'block';
        } else {
            currentClient = null;
            preview.style.display = 'none';
        }
        
        updateInvoiceSummary();
        updateFormButtons();
    }
    
    // Add book to invoice
    function addBookToInvoice() {
        const bookSelect = document.getElementById('bookSelect');
        const quantityInput = document.getElementById('quantityInput');
        const selectedOption = bookSelect.options[bookSelect.selectedIndex];
        
        if (!selectedOption.value) {
            alert('Please select a book');
            return;
        }
        
        const quantity = parseInt(quantityInput.value);
        if (quantity < 1) {
            alert('Please enter a valid quantity');
            return;
        }
        
        const stock = parseInt(selectedOption.dataset.stock);
        if (quantity > stock) {
            alert('Not enough stock available. Available: ' + stock);
            return;
        }
        
        const bookId = selectedOption.value;
        
        // Check if book is already added
        const existingBook = selectedBooks.find(book => book.id === bookId);
        if (existingBook) {
            const newQuantity = existingBook.quantity + quantity;
            if (newQuantity > stock) {
                alert('Total quantity would exceed available stock. Available: ' + stock);
                return;
            }
            existingBook.quantity = newQuantity;
        } else {
            selectedBooks.push({
                id: bookId,
                title: selectedOption.dataset.title,
                author: selectedOption.dataset.author,
                price: parseFloat(selectedOption.dataset.price),
                quantity: quantity,
                stock: stock
            });
        }
        
        // Reset selections
        bookSelect.selectedIndex = 0;
        quantityInput.value = 1;
        
        updateSelectedBooksTable();
        updateInvoiceSummary();
        updateFormButtons();
    }
    
    // Remove book from invoice
    function removeBook(bookId) {
        selectedBooks = selectedBooks.filter(book => book.id !== bookId);
        updateSelectedBooksTable();
        updateInvoiceSummary();
        updateFormButtons();
    }
    
    // Update quantity of selected book
    function updateBookQuantity(bookId, newQuantity) {
        const book = selectedBooks.find(b => b.id === bookId);
        if (book) {
            if (newQuantity > book.stock) {
                alert('Quantity cannot exceed available stock: ' + book.stock);
                return;
            }
            if (newQuantity < 1) {
                removeBook(bookId);
                return;
            }
            book.quantity = newQuantity;
            updateInvoiceSummary();
        }
    }
    
    // Update selected books table
    function updateSelectedBooksTable() {
        const tableBody = document.getElementById('selectedBooksTable');
        const section = document.getElementById('selectedBooksSection');
        
        if (selectedBooks.length === 0) {
            section.style.display = 'none';
            return;
        }
        
        section.style.display = 'block';
        
        tableBody.innerHTML = '';
        selectedBooks.forEach(book => {
            const total = book.price * book.quantity;
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>Rs. ${book.price.toFixed(2)}</td>
                <td>
                    <input type="number" value="${book.quantity}" min="1" max="${book.stock}" 
                           class="quantity-input" 
                           onchange="updateBookQuantity('${book.id}', parseInt(this.value))">
                </td>
                <td>Rs. ${total.toFixed(2)}</td>
                <td>
                    <button type="button" class="remove-btn" onclick="removeBook('${book.id}')">
                        <i class="fas fa-times"></i> Remove
                    </button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    }
    
    // Update invoice summary
    function updateInvoiceSummary() {
        const summarySection = document.getElementById('invoiceSummary');
        
        if (selectedBooks.length === 0 || !currentClient) {
            summarySection.style.display = 'none';
            return;
        }
        
        summarySection.style.display = 'block';
        
        // Calculate subtotal
        const subtotal = selectedBooks.reduce((sum, book) => sum + (book.price * book.quantity), 0);
        
        // Calculate discount
        const discountRate = tierDiscounts[currentClient.tier] || 0;
        const discountAmount = subtotal * discountRate;
        const total = subtotal - discountAmount;
        
        // Calculate points earned (1 point per dollar)
        const pointsEarned = Math.floor(total);
        
        // Update display
        document.getElementById('subtotalAmount').textContent = subtotal.toFixed(2);
        document.getElementById('discountPercent').textContent = (discountRate * 100).toFixed(0) + '%';
        document.getElementById('discountAmount').textContent = discountAmount.toFixed(2);
        document.getElementById('totalAmount').textContent = total.toFixed(2);
        document.getElementById('pointsEarned').textContent = pointsEarned;
    }
    
    // Update form buttons state
    function updateFormButtons() {
        const canCreate = selectedBooks.length > 0 && currentClient;
        document.getElementById('previewBtn').disabled = !canCreate;
        document.getElementById('createBtn').disabled = !canCreate;
    }
    
    // Preview invoice
    function previewInvoice() {
        if (!currentClient || selectedBooks.length === 0) {
            alert('Please select a client and add books to the invoice');
            return;
        }
        
        // Generate preview content
        const subtotal = selectedBooks.reduce((sum, book) => sum + (book.price * book.quantity), 0);
        const discountRate = tierDiscounts[currentClient.tier] || 0;
        const discountAmount = subtotal * discountRate;
        const total = subtotal - discountAmount;
        const pointsEarned = Math.floor(total);
        
        let previewHTML = `
            <div style="padding: 1rem;">
                <h4>Client Information</h4>
                <p><strong>Name:</strong> ${currentClient.name}</p>
                <p><strong>Email:</strong> ${currentClient.email}</p>
                <p><strong>Phone:</strong> ${currentClient.phone}</p>
                <p><strong>Current Tier:</strong> <span class="tier-badge tier-${currentClient.tier.toLowerCase()}">${currentClient.tier}</span></p>
                
                <h4 style="margin-top: 2rem;">Books</h4>
                <table class="books-table">
                    <thead>
                        <tr>
                            <th>Book</th>
                            <th>Author</th>
                            <th>Price</th>
                            <th>Qty</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
        `;
        
        selectedBooks.forEach(book => {
            const itemTotal = book.price * book.quantity;
            previewHTML += `
                <tr>
                    <td>${book.title}</td>
                    <td>${book.author}</td>
                    <td>Rs. ${book.price.toFixed(2)}</td>
                    <td>${book.quantity}</td>
                    <td>Rs. ${itemTotal.toFixed(2)}</td>
                </tr>
            `;
        });
        
        previewHTML += `
                    </tbody>
                </table>
                
                <div style="margin-top: 2rem; padding: 1rem; background: #f8f9fa; border-radius: 4px;">
                    <h4>Invoice Summary</h4>
                    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                        <span>Subtotal:</span>
                        <span>Rs. ${subtotal.toFixed(2)}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                        <span>Discount (${(discountRate * 100).toFixed(0)}%):</span>
                        <span>- Rs. ${discountAmount.toFixed(2)}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; font-weight: bold; font-size: 1.1rem; border-top: 1px solid #dee2e6; padding-top: 0.5rem;">
                        <span>Total:</span>
                        <span>Rs. ${total.toFixed(2)}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-top: 0.5rem; color: #007bff;">
                        <span>Points Earned:</span>
                        <span>${pointsEarned} points</span>
                    </div>
                </div>
            </div>
        `;
        
        document.getElementById('previewContent').innerHTML = previewHTML;
        document.getElementById('previewModal').style.display = 'block';
    }
    
    // Close preview modal
    function closePreviewModal() {
        document.getElementById('previewModal').style.display = 'none';
    }
    
    // Confirm and create invoice
    function confirmCreateInvoice() {
        closePreviewModal();
        submitInvoice();
    }
    
    // Submit invoice form
    function submitInvoice() {
        if (!currentClient || selectedBooks.length === 0) {
            alert('Please select a client and add books to the invoice');
            return;
        }
        
        // Prepare selected books data
        const booksData = selectedBooks.map(book => ({
            id: book.id,
            quantity: book.quantity
        }));
        
        document.getElementById('selectedBooksData').value = JSON.stringify(booksData);
        
        // Show loading state
        document.getElementById('invoiceForm').classList.add('loading');
        document.getElementById('createBtn').textContent = 'Creating...';
        document.getElementById('createBtn').disabled = true;
        
        // Submit form
        document.getElementById('invoiceForm').submit();
    }
    
    // Form submission handler
    document.getElementById('invoiceForm').addEventListener('submit', function(e) {
        e.preventDefault();
        submitInvoice();
    });
    
    // Close modal when clicking outside
    window.onclick = function(event) {
        const modal = document.getElementById('previewModal');
        if (event.target === modal) {
            closePreviewModal();
        }
    };
    
    // Initialize form
    document.addEventListener('DOMContentLoaded', function() {
        updateFormButtons();
        
        // Auto-hide messages after 5 seconds
        setTimeout(function() {
            const messages = document.querySelectorAll('.success-message, .error-message');
            messages.forEach(function(msg) {
                msg.style.transition = 'opacity 0.3s';
                msg.style.opacity = '0';
                setTimeout(function() { 
                    msg.style.display = 'none'; 
                }, 300);
            });
        }, 5000);
    });
</script>

</body>
</html>