<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Billing Management - Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>

<body>
    <%-- Include Sidebar --%>
    <jsp:include page="sidebar.jsp" flush="true" />

    <main class="main-content">
        <!-- Display Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="success-message alert-dismissible">
                <i class="fas fa-check-circle"></i>
                ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert">&times;</button>
            </div>
            <c:remove var="successMessage" scope="session" />
        </c:if>
        
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="error-message alert-dismissible">
                <i class="fas fa-exclamation-circle"></i>
                ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert">&times;</button>
            </div>
            <c:remove var="errorMessage" scope="session" />
        </c:if>

        <div class="billing-header">
            <div class="billing-header-content">
                <div class="billing-title-section">
                    <h1><i class="fas fa-receipt"></i>Billing Management</h1>
                    <p>Create, manage, and track all billing operations</p>
                </div>
                <div class="billing-action-section">
                    <button class="btn-add-book" data-bs-toggle="modal" data-bs-target="#createBillModal">
                        <i class="fas fa-plus"></i>Create New Bill
                    </button>
                </div>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="stats-container">
            <div class="stats-card">
                <i class="fas fa-file-invoice"></i>
                <h3>${totalBillings != null ? totalBillings : 0}</h3>
                <p>Total Bills</p>
            </div>
            <div class="stats-card stats-blue">
                <i class="fas fa-clock"></i>
                <h3>${pendingBills != null ? pendingBills : 0}</h3>
                <p>Pending Bills</p>
            </div>
            <div class="stats-card stats-orange">
                <i class="fas fa-check-circle"></i>
                <h3>${completedBills != null ? completedBills : 0}</h3>
                <p>Completed Bills</p>
            </div>
            <div class="stats-card stats-purple">
                <i class="fas fa-dollar-sign"></i>
                <h3>Rs. <fmt:formatNumber value="${totalRevenue != null ? totalRevenue : 0}" type="number" minFractionDigits="2"/></h3>
                <p>Total Revenue</p>
            </div>
        </div>

        <!-- Filter Section -->
        <div class="filter-section billing-filter">
            <form method="get" action="BillingServlet">
                <div class="billing-filter-row">
                    <div class="filter-group">
                        <label class="form-label">Status Filter</label>
                        <select name="status" class="form-select">
                            <option value="">All Statuses</option>
                            <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                            <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label class="form-label">Search by Client</label>
                        <input type="text" name="clientSearch" class="form-control" placeholder="Enter client name..." value="${param.clientSearch}">
                    </div>
                    <div class="filter-group">
                        <label class="form-label">From Date</label>
                        <input type="date" name="fromDate" class="form-control" value="${param.fromDate}">
                    </div>
                    <div class="filter-group">
                        <label class="form-label">To Date</label>
                        <input type="date" name="toDate" class="form-control" value="${param.toDate}">
                    </div>
                    <div class="filter-group">
                        <button type="submit" class="btn-filter">
                            <i class="fas fa-filter"></i>Filter
                        </button>
                    </div>
                </div>
            </form>
        </div>

        <!-- Bills List -->
        <div class="bills-grid">
            <c:choose>
                <c:when test="${empty billings}">
                    <div class="no-books">
                        <i class="fas fa-receipt"></i>
                        <h3>No bills found</h3>
                        <p>Create your first bill to get started</p>
                        <button class="btn-add-book" data-bs-toggle="modal" data-bs-target="#createBillModal">
                            Create New Bill
                        </button>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="bill" items="${billings}">
                        <div class="bill-card">
                            <div class="bill-card-header">
                                <h6><i class="fas fa-hashtag"></i> ${bill.billNumber}</h6>
                                <span class="status-badge status-${fn:toLowerCase(bill.status)}">${bill.status}</span>
                            </div>
                            <div class="bill-card-body">
                                <div class="bill-info-item">
                                    <strong><i class="fas fa-user"></i> Client:</strong>
                                    <span>${bill.clientName}</span>
                                </div>
                                <div class="bill-info-item">
                                    <strong><i class="fas fa-calendar"></i> Date:</strong>
                                    <span>${bill.formattedBillDateShort}</span>
                                </div>
                                <div class="bill-info-item">
                                    <strong><i class="fas fa-book"></i> Items:</strong>
                                    <span>${bill.totalItemsCount} book(s)</span>
                                </div>
                                <div class="bill-total">
                                    <strong><i class="fas fa-dollar-sign"></i> Total:</strong>
                                    <span class="bill-amount">Rs. <fmt:formatNumber value="${bill.totalAmount}" type="number" minFractionDigits="2"/></span>
                                </div>
                                <div class="bill-actions">
                                    <button class="btn-view" onclick="viewBillDetails(${bill.id})">
                                        <i class="fas fa-eye"></i> View
                                    </button>
                                    <button class="btn-print" onclick="printBill(${bill.id})">
                                        <i class="fas fa-print"></i> Print
                                    </button>
                                    <c:if test="${bill.status == 'PENDING'}">
                                        <button class="btn-complete" onclick="completeBill(${bill.id})">
                                            <i class="fas fa-check"></i> Complete
                                        </button>
                                        <button class="btn-cancel" onclick="cancelBill(${bill.id})">
                                            <i class="fas fa-times"></i> Cancel
                                        </button>
                                    </c:if>
                                    <c:if test="${bill.status == 'CANCELLED'}">
                                        <button class="btn-delete" onclick="deleteBill(${bill.id})">
                                            <i class="fas fa-trash"></i> Delete
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <!-- Create Bill Modal -->
    <div id="createBillModal" class="modal">
        <div class="modal-content modal-xl">
            <div class="modal-header">
                <h3><i class="fas fa-plus"></i>Create New Bill</h3>
                <span class="close" data-bs-dismiss="modal">&times;</span>
            </div>
            <form id="createBillForm" method="post" action="BillingServlet">
                <div class="modal-body">
                    <input type="hidden" name="action" value="create">
                    
                    <div class="bill-form-row">
                        <div class="form-group">
                            <label class="form-label">Select Client *</label>
                            <select name="clientId" class="form-select" required>
                                <option value="">Choose a client...</option>
                                <c:forEach var="client" items="${clients}">
                                    <option value="${client.id}">${client.firstName} ${client.lastName} - ${client.email}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Payment Method</label>
                            <select name="paymentMethod" class="form-select">
                                <option value="CASH">Cash</option>
                                <option value="CARD">Card</option>
                                <option value="ONLINE">Online</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Discount (%)</label>
                            <input type="number" name="discountPercentage" class="form-control" min="0" max="100" value="0" step="0.01" onchange="calculateTotal()">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Notes (Optional)</label>
                        <textarea name="notes" class="form-control" rows="2" placeholder="Add any additional notes..."></textarea>
                    </div>

                    <hr class="modal-divider">
                    <h6 class="modal-section-title"><i class="fas fa-books"></i>Add Books to Bill</h6>
                    
                    <div id="bookItems">
                        <div class="book-item-row">
                            <div class="book-row-fields">
                                <div class="form-group book-select-group">
                                    <label class="form-label">Book</label>
                                    <select name="bookIds" class="form-select book-select" required onchange="updateBookPrice(this)">
                                        <option value="">Select a book...</option>
                                        <c:forEach var="book" items="${books}">
                                            <option value="${book.id}" data-price="${book.price}" data-stock="${book.quantity}" data-title="${book.title}" data-author="${book.author}" data-isbn="${book.isbn}">
                                                ${book.title} - Rs. <fmt:formatNumber value="${book.price}" type="number" minFractionDigits="2"/> (Stock: ${book.quantity})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Price</label>
                                    <input type="number" name="prices" class="form-control price-input" step="0.01" readonly>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Quantity</label>
                                    <input type="number" name="quantities" class="form-control quantity-input" min="1" value="1" required onchange="calculateItemTotal(this)">
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Subtotal</label>
                                    <input type="number" class="form-control subtotal-input" readonly>
                                </div>
                                <div class="form-group book-remove-group">
                                    <button type="button" class="btn-delete btn-remove-book" onclick="removeBookItem(this)">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="add-book-section">
                        <button type="button" class="btn-add-book" onclick="addBookItem()">
                            <i class="fas fa-plus"></i>Add Another Book
                        </button>
                    </div>

                    <div class="bill-summary">
                        <div class="summary-content">
                            <div class="summary-title">
                                <h6>Bill Summary</h6>
                            </div>
                            <div class="summary-details">
                                <div class="summary-item">
                                    <strong>Subtotal: Rs. <span id="billSubtotal">0.00</span></strong>
                                </div>
                                <div class="summary-item">
                                    <span>Discount: -Rs. <span id="billDiscount">0.00</span></span>
                                </div>
                                <div class="summary-item">
                                    <span>Tax (8%): +Rs. <span id="billTax">0.00</span></span>
                                </div>
                                <hr class="summary-divider">
                                <div class="summary-total">
                                    <h5>Total: Rs. <span id="billTotal">0.00</span></h5>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn-cancel" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn-save">
                        <i class="fas fa-save"></i>Create Bill
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Bill Details Modal -->
    <div id="billDetailsModal" class="modal">
        <div class="modal-content modal-lg">
            <div class="modal-header">
                <h3><i class="fas fa-receipt"></i>Bill Details</h3>
                <span class="close" data-bs-dismiss="modal">&times;</span>
            </div>
            <div class="modal-body" id="billDetailsContent">
                <!-- Bill details will be loaded here via AJAX -->
                <div class="loading-spinner">
                    <div class="spinner"></div>
                    <span>Loading...</span>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn-save no-print" onclick="window.print()">
                    <i class="fas fa-print"></i>Print
                </button>
            </div>
        </div>
    </div>

    <script>
        // Add book item functionality
        function addBookItem() {
            const bookItemsContainer = document.getElementById('bookItems');
            const firstItem = document.querySelector('.book-item-row');
            const newItem = firstItem.cloneNode(true);
            
            // Reset values in the new item
            const select = newItem.querySelector('.book-select');
            const priceInput = newItem.querySelector('.price-input');
            const quantityInput = newItem.querySelector('.quantity-input');
            const subtotalInput = newItem.querySelector('.subtotal-input');
            
            select.value = '';
            priceInput.value = '';
            quantityInput.value = '1';
            subtotalInput.value = '';
            
            bookItemsContainer.appendChild(newItem);
        }

        // Remove book item
        function removeBookItem(button) {
            const bookItems = document.querySelectorAll('.book-item-row');
            if (bookItems.length > 1) {
                button.closest('.book-item-row').remove();
                calculateTotal();
            } else {
                alert('At least one book item is required.');
            }
        }

        // Update book price when book is selected
        function updateBookPrice(select) {
            const selectedOption = select.options[select.selectedIndex];
            const row = select.closest('.book-item-row');
            const priceInput = row.querySelector('.price-input');
            const quantityInput = row.querySelector('.quantity-input');
            
            if (selectedOption.value) {
                const price = parseFloat(selectedOption.getAttribute('data-price')) || 0;
                const stock = parseInt(selectedOption.getAttribute('data-stock')) || 0;
                
                priceInput.value = price.toFixed(2);
                quantityInput.max = stock;
                
                // Reset quantity if it exceeds available stock
                if (parseInt(quantityInput.value) > stock) {
                    quantityInput.value = Math.min(1, stock);
                }
                
                calculateItemTotal(quantityInput);
            } else {
                priceInput.value = '';
                quantityInput.max = '';
                calculateItemTotal(quantityInput);
            }
        }

        // Calculate item subtotal
        function calculateItemTotal(element) {
            const row = element.closest('.book-item-row');
            const price = parseFloat(row.querySelector('.price-input').value) || 0;
            const quantity = parseInt(row.querySelector('.quantity-input').value) || 0;
            const subtotal = price * quantity;
            row.querySelector('.subtotal-input').value = subtotal.toFixed(2);
            calculateTotal();
        }

        // Calculate bill total
        function calculateTotal() {
            let subtotal = 0;
            document.querySelectorAll('.subtotal-input').forEach(input => {
                subtotal += parseFloat(input.value) || 0;
            });

            const discountPercentage = parseFloat(document.querySelector('input[name="discountPercentage"]').value) || 0;
            const taxPercentage = 8; // Fixed 8% tax as per your backend

            const discountAmount = (subtotal * discountPercentage) / 100;
            const taxableAmount = subtotal - discountAmount;
            const taxAmount = (taxableAmount * taxPercentage) / 100;
            const total = taxableAmount + taxAmount;

            document.getElementById('billSubtotal').textContent = subtotal.toFixed(2);
            document.getElementById('billDiscount').textContent = discountAmount.toFixed(2);
            document.getElementById('billTax').textContent = taxAmount.toFixed(2);
            document.getElementById('billTotal').textContent = total.toFixed(2);
        }

        // Bill action functions
        function viewBillDetails(billId) {
            const modal = document.getElementById('billDetailsModal');
            const content = document.getElementById('billDetailsContent');
            
            // Show loading spinner
            content.innerHTML = '<div class="loading-spinner"><div class="spinner"></div><span>Loading...</span></div>';
            modal.style.display = 'block';
            
            fetch(`BillingServlet?action=view&id=${billId}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.text();
                })
                .then(html => {
                    content.innerHTML = html;
                })
                .catch(error => {
                    console.error('Error:', error);
                    content.innerHTML = '<div class="error-message">Error loading bill details. Please try again.</div>';
                });
        }

        function printBill(billId) {
            const printWindow = window.open(`BillingServlet?action=print&id=${billId}`, '_blank');
            if (printWindow) {
                printWindow.onload = function() {
                    printWindow.print();
                };
            }
        }

        function completeBill(billId) {
            if (confirm('Are you sure you want to mark this bill as completed?')) {
                submitAction('complete', billId);
            }
        }

        function cancelBill(billId) {
            if (confirm('Are you sure you want to cancel this bill?')) {
                submitAction('cancel', billId);
            }
        }

        function deleteBill(billId) {
            if (confirm('Are you sure you want to delete this bill? This action cannot be undone.')) {
                submitAction('delete', billId);
            }
        }

        function submitAction(action, billId) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'BillingServlet';
            
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = action;
            
            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = billId;
            
            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }

        // Form validation
        document.getElementById('createBillForm').addEventListener('submit', function(e) {
            const bookSelects = document.querySelectorAll('.book-select');
            let hasValidBook = false;
            let hasEmptyRow = false;
            
            bookSelects.forEach(select => {
                if (select.value) {
                    hasValidBook = true;
                } else {
                    // Check if this row has any data
                    const row = select.closest('.book-item-row');
                    const quantity = row.querySelector('.quantity-input').value;
                    if (quantity && quantity > 0) {
                        hasEmptyRow = true;
                    }
                }
            });
            
            if (!hasValidBook) {
                e.preventDefault();
                alert('Please select at least one book for the bill.');
                return false;
            }
            
            if (hasEmptyRow) {
                e.preventDefault();
                alert('Please select a book for all rows or remove empty rows.');
                return false;
            }
            
            // Validate quantities don't exceed stock
            let stockError = false;
            let stockErrorMessage = '';
            
            bookSelects.forEach(select => {
                if (select.value) {
                    const row = select.closest('.book-item-row');
                    const quantity = parseInt(row.querySelector('.quantity-input').value) || 0;
                    const stock = parseInt(select.options[select.selectedIndex].getAttribute('data-stock')) || 0;
                    const title = select.options[select.selectedIndex].getAttribute('data-title');
                    
                    if (quantity > stock) {
                        stockError = true;
                        stockErrorMessage += `"${title}" - Requested: ${quantity}, Available: ${stock}\n`;
                    }
                }
            });
            
            if (stockError) {
                e.preventDefault();
                alert('Some items exceed available stock:\n' + stockErrorMessage);
                return false;
            }
        });

        // Modal functionality
        function openModal(modalId) {
            document.getElementById(modalId).style.display = 'block';
        }

        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }

        // Close modal when clicking outside or on close button
        window.onclick = function(event) {
            const modals = document.querySelectorAll('.modal');
            modals.forEach(modal => {
                if (event.target === modal) {
                    modal.style.display = 'none';
                }
            });
        }

        document.querySelectorAll('.close').forEach(closeBtn => {
            closeBtn.onclick = function() {
                this.closest('.modal').style.display = 'none';
            }
        });

        document.querySelectorAll('[data-bs-dismiss="modal"]').forEach(btn => {
            btn.onclick = function() {
                this.closest('.modal').style.display = 'none';
            }
        });

        // Initialize calculations when page loads
        document.addEventListener('DOMContentLoaded', function() {
            calculateTotal();
            
            // Auto-dismiss alerts after 5 seconds
            setTimeout(function() {
                const alerts = document.querySelectorAll('.alert-dismissible');
                alerts.forEach(alert => {
                    alert.style.transition = 'opacity 0.3s';
                    alert.style.opacity = '0';
                    setTimeout(() => alert.style.display = 'none', 300);
                });
            }, 5000);
        });

        // Close alert manually
        document.querySelectorAll('.btn-close').forEach(btn => {
            btn.onclick = function() {
                const alert = this.closest('.alert-dismissible');
                alert.style.transition = 'opacity 0.3s';
                alert.style.opacity = '0';
                setTimeout(() => alert.style.display = 'none', 300);
            }
        });
    </script>
</body>
</html>