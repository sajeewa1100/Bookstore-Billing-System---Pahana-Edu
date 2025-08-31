<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Invoice - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
   <style>
    :root {
        --primary-color: #D86C36;
        --primary-dark: #C4552C;
        --primary-darker: #A63F22;
        --accent-color: #f2a23f;
        --background-light: #F2E7DC;
    }

    .billing-header {
        background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
        color: white;
        padding: 2rem;
        margin: -30px -30px 30px -30px;
        border-radius: 0 0 20px 20px;
        box-shadow: 0 8px 32px rgba(79, 70, 229, 0.3);
    }
    
    .header-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        max-width: 1200px;
        margin: 0 auto;
    }
    
    .header-title h1 {
        font-size: 2.5rem;
        margin: 0 0 10px 0;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }
    
    .header-title p {
        opacity: 0.9;
        font-size: 1.1rem;
        margin: 0;
    }
    
    .btn-back {
        background: rgba(255, 255, 255, 0.2);
        color: white;
        border: 2px solid rgba(255, 255, 255, 0.3);
        padding: 15px 25px;
        border-radius: 10px;
        cursor: pointer;
        font-size: 1.1rem;
        font-weight: 600;
        transition: all 0.3s ease;
        backdrop-filter: blur(10px);
        text-decoration: none;
        display: inline-block;
    }
    
    
    
    .btn-back:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: translateY(-2px);
        color: white;
        text-decoration: none;
    }
    
    .invoice-form {
        background: white;
        border-radius: 15px;
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
        padding: 30px;
        margin-top: 20px;
    }

    .form-section {
        background: #d1fae5;
        border-radius: 12px;
        padding: 25px;
        margin-bottom: 20px;
        border: 1px solid #e5e7eb;
    }
    
    .form-section h4 {
        font-size: 1.1rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        margin-bottom: 15px;
    }
    
    .form-group {
        display: flex;
        flex-direction: column;
        gap: 8px;
    }
    
    .form-group label {
        font-weight: 600;
        color: #374151;
    }
    
    .form-group input,
    .form-group select {
        padding: 12px 15px;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        font-size: 1rem;
        transition: all 0.3s ease;
    }
    
    .form-group input:focus,
    .form-group select:focus {
        outline: none;
        border-color: var(--primary-color);
        box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
    }

    .search-section {
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 15px;
    }
    
    .search-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
    }
    
    .search-form {
        display: flex;
        gap: 10px;
        align-items: end;
    }
    
    .search-results {
        background: #f8fafc;
        border: 1px solid #e5e7eb;
        border-radius: 6px;
        max-height: 200px;
        overflow-y: auto;
    }
    
    .search-result-item {
        padding: 12px 15px;
        border-bottom: 1px solid #f1f5f9;
        cursor: pointer;
        transition: background-color 0.2s;
    }
    
    .search-result-item:hover {
        background-color: #e2e8f0;
    }
    
    .search-result-item:last-child {
        border-bottom: none;
    }
    
    .search-result-main {
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 2px;
    }
    
    .search-result-detail {
        font-size: 0.9rem;
        color: #6b7280;
    }
    
    .selected-item {
        background: #f0f9ff;
        border: 2px solid #38bdf8;
        border-radius: 8px;
        padding: 15px;
        margin-bottom: 15px;
        position: relative;
    }
    
    .selected-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    .selected-main {
        font-weight: 600;
        color: #1f2937;
    }
    
    .selected-detail {
        font-size: 0.9rem;
        color: #6b7280;
        margin-top: 2px;
    }
    
    .btn-clear {
        background: #ef4444;
        color: white;
        border: none;
        border-radius: 4px;
        padding: 6px 12px;
        cursor: pointer;
        font-size: 0.85rem;
        text-decoration: none;
    }
    
    .btn-clear:hover {
        background: #dc2626;
        text-decoration: none;
        color: white;
    }
    
    .btn-search {
        background: var(--primary-color);
        color: white;
        border: none;
        padding: 12px 20px;
        border-radius: 8px;
        cursor: pointer;
        font-size: 0.9rem;
        font-weight: 600;
        transition: all 0.3s ease;
    }
    
    .btn-search:hover {
        background: #4338ca;
    }

    .btn-add {
        background: #16a34a;
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 0.85rem;
        font-weight: 600;
        transition: all 0.3s ease;
        text-decoration: none;
    }
    
    .btn-add:hover {
        background: #15803d;
        color: white;
        text-decoration: none;
    }
    
    .items-container {
        display: flex;
        flex-direction: column;
        gap: 15px;
    }
    
    .item-row {
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        padding: 20px;
        position: relative;
    }
    
    .item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
    }
    
    .item-number {
        font-weight: 600;
        color: #1f2937;
    }
    
    .btn-remove-item {
        background: #ef4444;
        color: white;
        border: none;
        border-radius: 6px;
        padding: 8px 12px;
        cursor: pointer;
        font-size: 0.85rem;
        text-decoration: none;
    }
    
    .btn-remove-item:hover {
        background: #dc2626;
        text-decoration: none;
        color: white;
    }

    .calculation-display {
        background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
        color: white;
        border-radius: 12px;
        padding: 25px;
        margin-top: 20px;
    }
    
    .calc-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid rgba(255, 255, 255, 0.2);
    }
    
    .calc-row:last-child {
        border-bottom: none;
    }
    
    .calc-row.total {
        font-size: 1.2rem;
        font-weight: 700;
        border-top: 2px solid rgba(255, 255, 255, 0.3);
        margin-top: 10px;
        padding-top: 15px;
    }

    .form-footer {
        display: flex;
        justify-content: flex-end;
        gap: 15px;
        padding-top: 20px;
        border-top: 1px solid #e5e7eb;
        margin-top: 20px;
    }
    
    .btn-primary {
        background: var(--primary-color);
        color: white;
        border: none;
        padding: 12px 25px;
        border-radius: 8px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        text-decoration: none;
        display: inline-block;
    }
    
    .btn-primary:hover {
        background: #4338ca;
        transform: translateY(-1px);
        color: white;
        text-decoration: none;
    }
    
    .btn-secondary {
        background: #6b7280;
        color: white;
        border: none;
        padding: 12px 25px;
        border-radius: 8px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        text-decoration: none;
        display: inline-block;
    }
    
    .btn-secondary:hover {
        background: #4b5563;
        color: white;
        text-decoration: none;
    }

    .alert {
        padding: 15px 20px;
        border-radius: 8px;
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    .alert-success {
        background: #d1fae5;
        color: #065f46;
        border: 1px solid #a7f3d0;
    }
    
    .alert-error {
        background: #fee2e2;
        color: #991b1b;
        border: 1px solid #fca5a5;
    }
    
    .alert-info {
        background: #dbeafe;
        color: #1e40af;
        border: 1px solid #93c5fd;
    }

    .walkIn-option {
        background: #f3f4f6;
        border: 1px solid #d1d5db;
        border-radius: 8px;
        padding: 15px;
        text-align: center;
        margin-bottom: 15px;
    }
    
    .walkIn-option label {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        cursor: pointer;
        font-weight: 600;
        color: #374151;
    }

    .quick-add-section {
        background: #F2E7DC;
        border: 2px solid #A63F22;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 20px;
    }
    
    @media (max-width: 768px) {
        .billing-header {
            margin: -20px -20px 20px -20px;
            padding: 1.5rem;
        }
        
        .header-content {
            flex-direction: column;
            gap: 20px;
            text-align: center;
        }
        
        .header-title h1 {
            font-size: 2rem;
        }
        
        .form-row {
            grid-template-columns: 1fr;
        }
        
        .search-form {
            flex-direction: column;
            align-items: stretch;
        }
        
        .btn-print {
    background: var(--accent-color);
    color: white;
    border: none;
    padding: 12px 25px;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    text-decoration: none;
    display: inline-block;
}

.btn-print:hover {
    background: #e19a2d;
    transform: translateY(-1px);
    color: white;
    text-decoration: none;
}

/* Two-button layout for create invoice */
.form-footer .btn-primary + .btn-primary {
    margin-left: 10px;
}

/* Print icon spacing */
.btn-primary .fas + .fas {
    margin-left: 5px;
}
    }
</style>

</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <!-- Header -->
    <div class="billing-header">
        <div class="header-content">
            <div class="header-title">
                <h1><i class="fas fa-plus"></i> Create New Invoice</h1>
                <p>Search customers and scan/search books by ISBN</p>
            </div>
          </div>
    </div>

    <!-- Alert Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> ${successMessage}
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i> ${errorMessage}
        </div>
    </c:if>

    <div class="invoice-form">
        <!-- Customer Selection Section -->
        <div class="form-section">
            <h4><i class="fas fa-user"></i> Customer Selection</h4>
            
            <!-- Walk-in Customer Option -->
            <div class="walkIn-option">
                <label>
                    <input type="radio" name="customerType" value="walkIn" 
                           ${empty selectedClient ? 'checked' : ''}
                           onchange="window.location.href='${pageContext.request.contextPath}/create-invoice'">
                    <i class="fas fa-walking"></i>
                    Walk-in Customer (No loyalty discount)
                </label>
            </div>
            
            <!-- Client Search Section -->
            <div class="search-section">
                <div class="search-header">
                    <h5>Search Registered Customer</h5>
                </div>
                
                <!-- Search Form -->
                <form method="GET" action="${pageContext.request.contextPath}/create-invoice" class="search-form">
                    <input type="hidden" name="action" value="searchClient">
                    <!-- Preserve existing selected books -->
                    <c:forEach var="book" items="${selectedBooks}" varStatus="status">
                        <input type="hidden" name="selectedBookId" value="${book.id}">
                        <input type="hidden" name="selectedBookQuantity" value="${bookQuantities[status.index]}">
                    </c:forEach>
                    
                    <div class="form-group" style="flex: 1; margin-bottom: 0;">
                        <label>Search by:</label>
                        <select name="clientSearchType" style="width: 120px;">
                            
                            <option value="phone" ${param.clientSearchType == 'phone' ? 'selected' : ''}>Phone</option>
                            <option value="id" ${param.clientSearchType == 'id' ? 'selected' : ''}>ID</option>
                            <option value="name" ${param.clientSearchType == 'name' ? 'selected' : ''}>Name</option>
                        </select>
                    </div>
                    
                    <div class="form-group" style="flex: 2; margin-bottom: 0;">
                        <label>Search term:</label>
                        <input type="text" name="clientSearch" value="${param.clientSearch}" 
                               placeholder="Enter search term..." required>
                    </div>
                    
                    <div class="form-group" style="margin-bottom: 0;">
                        <label>&nbsp;</label>
                        <button type="submit" class="btn-search">
                            <i class="fas fa-search"></i> Search
                        </button>
                    </div>
                </form>
                
                <!-- Search Results -->
                <c:if test="${not empty clientSearchResults}">
                    <div class="search-results">
                        <c:forEach var="client" items="${clientSearchResults}">
                            <div class="search-result-item">
                                <form method="GET" action="${pageContext.request.contextPath}/create-invoice" style="margin: 0;">
                                    <input type="hidden" name="action" value="selectClient">
                                    <input type="hidden" name="clientId" value="${client.id}">
                                    <!-- Preserve existing selected books -->
                                    <c:forEach var="book" items="${selectedBooks}" varStatus="status">
                                        <input type="hidden" name="selectedBookId" value="${book.id}">
                                        <input type="hidden" name="selectedBookQuantity" value="${bookQuantities[status.index]}">
                                    </c:forEach>
                                    
                                    <button type="submit" style="background: none; border: none; width: 100%; text-align: left; cursor: pointer;">
                                        <div class="search-result-main">${client.fullName}</div>
                                        <div class="search-result-detail">
                                            ${client.phone} • ${client.tierLevel} • ${client.loyaltyPoints} loyalty points
                                        </div>
                                    </button>
                                </form>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                
                <!-- No Results Message -->
                <c:if test="${param.action == 'searchClient' && empty clientSearchResults && not empty param.clientSearch}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i>
                        No customers found matching "${param.clientSearch}". Try a different search term.
                    </div>
                </c:if>
            </div>
            
            <!-- Selected Client Display -->
            <c:if test="${not empty selectedClient}">
                <div class="selected-item">
                    <div class="selected-info">
                        <div>
                            <div class="selected-main">
                                <i class="fas fa-user-check"></i> ${selectedClient.fullName}
                            </div>
                            <div class="selected-detail">
                                ${selectedClient.phone} • ${selectedClient.tierLevel} • ${selectedClient.loyaltyPoints} points
                                <c:if test="${selectedClient.tierLevel == 'GOLD'}"> • 10% Discount</c:if>
                                <c:if test="${selectedClient.tierLevel == 'SILVER'}"> • 5% Discount</c:if>
                                <c:if test="${selectedClient.tierLevel == 'PLATINUM'}"> • 15% Discount</c:if>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/create-invoice" class="btn-clear">
                            <i class="fas fa-times"></i> Clear
                        </a>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- Quick Book Search - Always Visible -->
        <div class="form-section">
            <h4><i class="fas fa-barcode"></i> Add Books to Invoice</h4>
            
            <div class="quick-add-section">
                <div class="search-header">
                    <h5><i class="fas fa-search"></i> Quick Book Search</h5>
                    <small style="color: #6b7280;">Scan barcode or search by ISBN/title</small>
                </div>
                
                <!-- Book Search Form - Always visible with ISBN as default -->
                <form method="GET" action="${pageContext.request.contextPath}/create-invoice" class="search-form">
                    <input type="hidden" name="action" value="searchBook">
                    <input type="hidden" name="clientId" value="${selectedClient.id}">
                    <!-- Preserve existing selected books -->
                    <c:forEach var="book" items="${selectedBooks}" varStatus="status">
                        <input type="hidden" name="selectedBookId" value="${book.id}">
                        <input type="hidden" name="selectedBookQuantity" value="${bookQuantities[status.index]}">
                    </c:forEach>
                    
                    <div class="form-group" style="flex: 1; margin-bottom: 0;">
                        <label>Search by:</label>
                        <select name="bookSearchType" style="width: 120px;" id="searchTypeSelect">
                            <option value="isbn" ${param.bookSearchType == 'isbn' || empty param.bookSearchType ? 'selected' : ''}>ISBN</option>
                            <option value="title" ${param.bookSearchType == 'title' ? 'selected' : ''}>Title</option>
                        </select>
                    </div>
                    
                    <div class="form-group" style="flex: 2; margin-bottom: 0;">
                        <label>ISBN / Title:</label>
                        <input type="text" name="bookSearch" value="${param.bookSearch}" 
                               placeholder="Scan or type ISBN/title..." id="bookSearchInput" required>
                    </div>
                    
                    <div class="form-group" style="margin-bottom: 0;">
                        <label>&nbsp;</label>
                        <button type="submit" class="btn-search">
                            <i class="fas fa-search"></i> Search
                        </button>
                    </div>
                </form>
                
                <!-- Book Search Results with Quick Add -->
                <c:if test="${not empty bookSearchResults}">
                    <div class="search-results" style="margin-top: 15px;">
                        <c:forEach var="book" items="${bookSearchResults}">
                            <div class="search-result-item" style="display: flex; justify-content: space-between; align-items: center;">
                                <div style="flex: 1;">
                                    <div class="search-result-main">${book.title}</div>
                                    <div class="search-result-detail">
                                        by ${book.author} • ISBN: ${book.isbn} • Rs. <fmt:formatNumber value="${book.price}" pattern="#,##0.00" />
                                    </div>
                                </div>
                                
                                <form method="GET" action="${pageContext.request.contextPath}/create-invoice" style="margin: 0;">
                                    <input type="hidden" name="action" value="addBookToInvoice">
                                    <input type="hidden" name="bookId" value="${book.id}">
                                    <input type="hidden" name="clientId" value="${selectedClient.id}">
                                    <!-- Preserve existing selected books -->
                                    <c:forEach var="existingBook" items="${selectedBooks}" varStatus="status">
                                        <input type="hidden" name="selectedBookId" value="${existingBook.id}">
                                        <input type="hidden" name="selectedBookQuantity" value="${bookQuantities[status.index]}">
                                    </c:forEach>
                                    
                                    <button type="submit" class="btn-add">
                                        <i class="fas fa-plus"></i> Add
                                    </button>
                                </form>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                
                <!-- No Book Results Message -->
                <c:if test="${param.action == 'searchBook' && empty bookSearchResults && not empty param.bookSearch}">
                    <div class="alert alert-info" style="margin-top: 15px;">
                        <i class="fas fa-info-circle"></i>
                        No books found matching "${param.bookSearch}". Check the ISBN or try searching by title.
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Invoice Items Section -->
        <div class="form-section">
            <h4><i class="fas fa-shopping-cart"></i> Invoice Items</h4>
            
            <div class="items-container">
                <c:choose>
                    <c:when test="${not empty selectedBooks}">
                        <c:forEach var="book" items="${selectedBooks}" varStatus="status">
                            <div class="item-row">
                                <div class="item-header">
                                    <div class="item-number">Item #${status.index + 1}</div>
                                    <a href="${pageContext.request.contextPath}/create-invoice?action=removeItem&itemIndex=${status.index}&clientId=${selectedClient.id}" 
                                       class="btn-remove-item">
                                        <i class="fas fa-trash"></i> Remove
                                    </a>
                                </div>
                                
                                <!-- Selected Book Display -->
                                <div class="selected-item" style="margin-bottom: 15px;">
                                    <div class="selected-info">
                                        <div>
                                            <div class="selected-main">
                                                <i class="fas fa-book"></i> ${book.title}
                                            </div>
                                            <div class="selected-detail">
                                                by ${book.author} • ISBN: ${book.isbn} • Rs. <fmt:formatNumber value="${book.price}" pattern="#,##0.00" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Quantity -->
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Quantity:</label>
                                        <form method="GET" action="${pageContext.request.contextPath}/create-invoice" style="display: flex; gap: 10px;">
                                            <input type="hidden" name="action" value="updateQuantity">
                                            <input type="hidden" name="itemIndex" value="${status.index}">
                                            <input type="hidden" name="clientId" value="${selectedClient.id}">
                                            <!-- Preserve other items -->
                                            <c:forEach var="otherBook" items="${selectedBooks}" varStatus="otherStatus">
                                                <c:if test="${otherStatus.index != status.index}">
                                                    <input type="hidden" name="selectedBookId" value="${otherBook.id}">
                                                    <input type="hidden" name="selectedBookQuantity" value="${bookQuantities[otherStatus.index]}">
                                                </c:if>
                                            </c:forEach>
                                            <input type="hidden" name="selectedBookId" value="${book.id}">
                                            
                                            <input type="number" name="selectedBookQuantity" value="${bookQuantities[status.index]}" 
                                                   min="1" style="width: 100px;" onchange="this.form.submit()">
                                        </form>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label>Line Total:</label>
                                        <div style="padding: 12px 15px; background: #f3f4f6; border-radius: 8px; font-weight: 600;">
                                            Rs. <fmt:formatNumber value="${book.price * bookQuantities[status.index]}" pattern="#,##0.00" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle"></i>
                            No items added yet. Use the book search above to add books to this invoice.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Calculation Display -->
        <c:if test="${not empty selectedBooks}">
            <div class="calculation-display">
                <div class="calc-row">
                    <span>Subtotal:</span>
                    <span>Rs. <fmt:formatNumber value="${invoiceSubtotal}" pattern="#,##0.00" /></span>
                </div>
                <div class="calc-row">
                    <span>Discount 
                        <c:if test="${selectedClient.tierLevel == 'GOLD'}">(Gold 10%)</c:if>
                        <c:if test="${selectedClient.tierLevel == 'SILVER'}">(Silver 5%)</c:if>
                        <c:if test="${selectedClient.tierLevel == 'PLATINUM'}">(Platinum 15%)</c:if>:
                    </span>
                    <span>Rs. <fmt:formatNumber value="${invoiceDiscount}" pattern="#,##0.00" /></span>
                </div>
                <div class="calc-row total">
                    <span>Total Amount:</span>
                    <span>Rs. <fmt:formatNumber value="${invoiceTotal}" pattern="#,##0.00" /></span>
                </div>
                <div class="calc-row">
                    <span>Points Earned:</span>
                    <span>${pointsEarned} points</span>
                </div>
            </div>
        </c:if>

        <!-- Payment and Create Invoice -->
        <c:if test="${not empty selectedBooks}">
            <div class="form-section">
                <h4><i class="fas fa-credit-card"></i> Complete Invoice</h4>
                
                <form method="POST" action="${pageContext.request.contextPath}/create-invoice">
                    <input type="hidden" name="action" value="createInvoice">
                    
                    <!-- Customer Information -->
                    <c:if test="${not empty selectedClient}">
                        <input type="hidden" name="clientId" value="${selectedClient.id}">
                    </c:if>
                    
                    <!-- Book Items -->
                    <c:forEach var="book" items="${selectedBooks}" varStatus="status">
                        <input type="hidden" name="bookIds" value="${book.id}">
                        <input type="hidden" name="quantities" value="${bookQuantities[status.index]}">
                        <input type="hidden" name="unitPrices" value="${book.price}">
                    </c:forEach>
                    
                    <!-- Calculation values -->
                    <input type="hidden" name="subtotal" value="${invoiceSubtotal}">
                    <input type="hidden" name="discount" value="${invoiceDiscount}">
                    <input type="hidden" name="totalAmount" value="${invoiceTotal}">
                    <input type="hidden" name="loyaltyPoints" value="${pointsEarned}">
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="cashGiven">Cash Given:</label>
                            <input type="number" name="cashGiven" id="cashGiven" step="0.01" 
                                   min="${invoiceTotal}" placeholder="${invoiceTotal}" required>
                        </div>
                        <div class="form-group">
                            <label>Change:</label>
                            <input type="text" id="changeDisplay" readonly placeholder="Rs. 0.00" 
                                   style="background: #f3f4f6;">
                        </div>
                    </div>
                    
                    <div class="form-footer">
    <a href="${pageContext.request.contextPath}/billing" class="btn-secondary">
        <i class="fas fa-times"></i> Cancel
    </a>
    
    <!-- Regular Create Invoice Button -->
    <button type="submit" class="btn-primary" name="printAfterCreate" value="false">
        <i class="fas fa-save"></i> Create Invoice
    </button>
    
    <!-- NEW: Create and Print Button -->
    <button type="submit" class="btn-primary" name="printAfterCreate" value="true" 
            style="background: var(--accent-color);">
        <i class="fas fa-save"></i><i class="fas fa-print" style="margin-left: 5px;"></i> Create & Print
    </button>
</div>
                </form>
            </div>
        </c:if>
    </div>
</main>

<script>
// Simple change calculation
document.addEventListener('DOMContentLoaded', function() {
    const cashInput = document.getElementById('cashGiven');
    const changeDisplay = document.getElementById('changeDisplay');
    const totalAmount = ${invoiceTotal != null ? invoiceTotal : 0};
    
    if (cashInput && changeDisplay) {
        cashInput.addEventListener('input', function() {
            const cashGiven = parseFloat(this.value) || 0;
            const change = Math.max(0, cashGiven - totalAmount);
            changeDisplay.value = 'Rs. ' + change.toFixed(2);
        });
    }

    // Focus on book search input for quick scanning
    const bookSearchInput = document.getElementById('bookSearchInput');
    if (bookSearchInput) {
        bookSearchInput.focus();
        
        // Update placeholder based on search type
        const searchTypeSelect = document.getElementById('searchTypeSelect');
        if (searchTypeSelect) {
            searchTypeSelect.addEventListener('change', function() {
                if (this.value === 'isbn') {
                    bookSearchInput.placeholder = 'Scan or type ISBN...';
                } else {
                    bookSearchInput.placeholder = 'Type book title...';
                }
            });
        }
    }
});

// Auto-hide alerts
setTimeout(function() {
    document.querySelectorAll('.alert').forEach(function(alert) {
        alert.style.opacity = '0';
        setTimeout(function() {
            if (alert.parentNode) {
                alert.parentNode.removeChild(alert);
            }
        }, 300);
    });
}, 5000);

document.addEventListener('DOMContentLoaded', function() {
    // Check if we should print a newly created invoice
    const urlParams = new URLSearchParams(window.location.search);
    const printNewInvoice = urlParams.get('printNewInvoice');
    
    if (printNewInvoice === 'true') {
        // Get the invoice ID from session (set by servlet)
        <c:if test="${not empty printInvoiceId}">
            const invoiceId = ${printInvoiceId};
            
            // Clear the session attribute to prevent repeated prints
            <% 
                session.removeAttribute("printInvoiceId");
            %>
            
            // Show confirmation dialog
            if (confirm('Invoice created successfully! Would you like to print it now?')) {
                // Open print dialog
                const printUrl = '${pageContext.request.contextPath}/print-invoice?id=' + invoiceId;
                window.open(printUrl, '_blank');
            }
            
            // Clean the URL to remove the print parameter
            const newUrl = window.location.protocol + "//" + window.location.host + 
                          window.location.pathname + window.location.search.replace(/[?&]printNewInvoice=true/, '');
            window.history.replaceState({}, document.title, newUrl);
        </c:if>
    }

    // Existing code continues...
    // Auto-hide alerts (keep existing code)
    setTimeout(function() {
        document.querySelectorAll('.alert').forEach(function(alert) {
            alert.style.opacity = '0';
            setTimeout(function() {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 300);
        });
    }, 5000);
    
    // Rest of existing JavaScript...
});
</script>

</body>
</html>