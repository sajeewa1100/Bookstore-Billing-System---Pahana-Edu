<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Billing System - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
   <style>
    /* Define custom color variables */
    :root {
        --primary-color: #D86C36;
        --primary-dark: #C4552C;
        --primary-darker: #A63F22;
        --accent-color: #f2a23f;
        --background-light: #F2E7DC;
    }

    /* Billing-specific styles */
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
    
    .btn-new-invoice {
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
    
    .btn-new-invoice:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: translateY(-2px);
        color: white;
        text-decoration: none;
    }
    
    /* Statistics Cards */
    .stats-container {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
        gap: 25px;
        margin-bottom: 30px;
    }
    
    .stat-card {
        background: white;
        border-radius: 15px;
        padding: 30px;
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
        border: 1px solid #e5e7eb;
        display: flex;
        align-items: center;
        gap: 20px;
        transition: all 0.3s ease;
    }
    
    .stat-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 12px 35px rgba(0, 0, 0, 0.12);
    }
    
    .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.5rem;
        color: white;
    }
    
    .stat-icon.invoices {
        background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
    }
    
    .stat-icon.revenue {
        background: linear-gradient(135deg, var(--primary-darker), var(--accent-color));
    }
    
    .stat-icon.total {
        background: linear-gradient(135deg, var(--primary-darker), var(--accent-color));
    }
    
    .stat-info h3 {
        font-size: 2rem;
        font-weight: 700;
        color: #1f2937;
        margin: 0 0 5px 0;
    }
    
    .stat-info p {
        color: #6b7280;
        margin: 0;
        font-weight: 500;
    }
    
    /* Invoices Section */
    .invoices-section {
        background: white;
        border-radius: 15px;
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
        padding: 30px;
    }
    
    .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 25px;
        padding-bottom: 20px;
        border-bottom: 2px solid #f3f4f6;
    }
    
    .section-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
        margin: 0;
    }
    
    .search-container {
        position: relative;
    }
    
    /* Enhanced search container styling */
    .search-container form {
        display: flex;
        gap: 10px;
        align-items: center;
        flex-wrap: wrap;
    }

    .search-container select.search-input {
        min-width: 120px;
        width: auto;
    }

    .search-container input.search-input {
        width: 250px;
    }
    
    .search-input {
        padding: 12px 20px;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        font-size: 1rem;
        transition: all 0.3s ease;
    }
    
    .search-input:focus {
        outline: none;
        border-color: var(--primary-color);
        box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
    }

    .badge {
        display: inline-block;
        padding: 5px 10px;
        background: var(--primary-color);
        color: white;
        border-radius: 15px;
        font-size: 0.85rem;
        font-weight: 600;
    }
    
    /* Invoice Cards */
    .invoices-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
        gap: 20px;
    }
    
    .invoice-card {
        background: #f8fafc;
        border: 1px solid #e5e7eb;
        border-radius: 12px;
        overflow: hidden;
        transition: all 0.3s ease;
    }
    
    .invoice-card:hover {
        transform: translateY(-3px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        border-color: var(--primary-color);
    }
    
    .invoice-header {
        background: linear-gradient(135deg, var(--background-light), #e2e8f0);
        padding: 20px;
        border-bottom: 1px solid #e5e7eb;
    }
    
    .invoice-number {
        font-size: 1.2rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 5px;
    }
    
    .invoice-date {
        color: #6b7280;
        font-size: 0.9rem;
    }
    
    .invoice-body {
        padding: 20px;
    }
    
    .client-info {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 15px;
        color: #4b5563;
    }
    
    .invoice-total {
        font-size: 1.4rem;
        font-weight: 700;
        color: var(--primary-dark);
        text-align: right;
        margin-bottom: 15px;
    }
    
    .invoice-meta {
        display: flex;
        justify-content: space-between;
        font-size: 0.9rem;
        color: #6b7280;
        padding-top: 15px;
        border-top: 1px solid #f3f4f6;
    }
    
    .invoice-actions {
        display: flex;
        justify-content: center;
        gap: 10px;
        padding: 15px 20px;
        background: #f9fafb;
        border-top: 1px solid #f3f4f6;
    }
    
    .btn-action {
        width: 40px;
        height: 40px;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1rem;
        transition: all 0.3s ease;
        text-decoration: none;
    }
    
    .btn-edit {
       background: #d1fae5;
	   color: #10b981;
    }
    
    .btn-delete {
        background: #fecaca;
        color: #ef4444;
    }
    
    .btn-action:hover {
        transform: scale(1.1);
    }

    /* Empty State */
    .empty-state {
        text-align: center;
        padding: 60px 20px;
        color: #6b7280;
    }
    
    .empty-state i {
        font-size: 3rem;
        margin-bottom: 20px;
        color: #d1d5db;
    }
    
    .empty-state h3 {
        font-size: 1.3rem;
        margin-bottom: 10px;
        color: #4b5563;
    }
    
    .empty-state p {
        margin-bottom: 20px;
    }

    /* Alert Messages */
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
		color: #10b981;
        border: 1px solid #a7f3d0;
    }
    
    .alert-error {
        background: #fee2e2;
        color: #991b1b;
        border: 1px solid #fca5a5;
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
        background: var(--primary-dark);
        transform: translateY(-1px);
        color: white;
        text-decoration: none;
    }
    
    .btn-secondary {
        background: var(--primary-darker);
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
        background: var(--primary-dark);
        color: white;
        text-decoration: none;
    }

    /* Form styles for invoice view */
    .invoice-form {
        background: white;
        border-radius: 15px;
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
        padding: 30px;
        margin-top: 20px;
    }

    .form-section {
        background: #f8fafc;
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
        display: grid;
        grid-template-columns: 2fr 1fr 1fr 1fr;
        gap: 15px;
        align-items: end;
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

    /* Responsive search */
    @media (max-width: 768px) {
        .search-container form {
            flex-direction: column;
            align-items: stretch;
            gap: 10px;
        }
        
        .search-container select.search-input,
        .search-container input.search-input {
            width: 100%;
        }
        
        .section-header {
            flex-direction: column;
            gap: 15px;
            align-items: stretch;
        }
        
        .search-container {
            width: 100%;
        }
        
        .btn-print {
    background: #10b981; /* vibrant green */
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
    background: #059669; /* darker green on hover */
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
    
    <!-- Check if we're viewing a specific invoice -->
    <c:if test="${param.action == 'view' && not empty invoice}">
        <!-- Invoice Details View -->
        <div class="billing-header">
            <div class="header-content">
                <div class="header-title">
                    <h1><i class="fas fa-eye"></i> View Invoice #${invoice.id}</h1>
                    <p>Invoice details and transaction information</p>
                </div>
                <a href="${pageContext.request.contextPath}/billing" class="btn-new-invoice">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>

        <div class="invoice-form">
            <!-- Customer Information -->
            <div class="form-section">
                <h4><i class="fas fa-user"></i> Customer Information</h4>
                <div class="form-row">
                    <div class="form-group">
                        <label>Customer Name:</label>
                        <p style="padding: 12px 15px; background: #f8fafc; border-radius: 8px; margin: 0;">
                            <c:choose>
                                <c:when test="${invoice.client != null}">
                                    ${invoice.client.fullName}
                                </c:when>
                                <c:otherwise>
                                    Walk-in Customer
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <c:if test="${invoice.client != null}">
                        <div class="form-group">
                            <label>Contact:</label>
                            <p style="padding: 12px 15px; background: #f8fafc; border-radius: 8px; margin: 0;">
                                ${invoice.client.phone} • ${invoice.client.tierLevel}
                            </p>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Invoice Items -->
            <div class="form-section">
                <h4><i class="fas fa-shopping-cart"></i> Invoice Items</h4>
                <div class="items-container">
                    <c:forEach var="item" items="${invoice.items}">
                        <div class="item-row" style="grid-template-columns: 2fr 1fr 1fr 1fr;">
                            <div class="form-group">
                                <label>Book:</label>
                                <p style="padding: 12px 15px; background: #f8fafc; border-radius: 8px; margin: 0;">
                                    ${item.book != null ? item.book.title : 'Book ID: ' += item.bookId}
                                </p>
                            </div>
                            <div class="form-group">
                                <label>Quantity:</label>
                                <p style="padding: 12px 15px; background: #f8fafc; border-radius: 8px; margin: 0;">
                                    ${item.quantity}
                                </p>
                            </div>
                            <div class="form-group">
                                <label>Unit Price:</label>
                                <p style="padding: 12px 15px; background: #f8fafc; border-radius: 8px; margin: 0;">
                                    Rs. <fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00" />
                                </p>
                            </div>
                            <div class="form-group">
                                <label>Total:</label>
                                <p style="padding: 12px 15px; background: #f8fafc; border-radius: 8px; margin: 0;">
                                    Rs. <fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.00" />
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Invoice Totals -->
            <div class="calculation-display">
                <div class="calc-row">
                    <span>Subtotal:</span>
                    <span>Rs. <fmt:formatNumber value="${invoice.subtotal}" pattern="#,##0.00" /></span>
                </div>
                <div class="calc-row">
                    <span>Discount:</span>
                    <span>Rs. <fmt:formatNumber value="${invoice.loyaltyDiscount}" pattern="#,##0.00" /></span>
                </div>
                <div class="calc-row total">
                    <span>Total:</span>
                    <span>Rs. <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0.00" /></span>
                </div>
                <div class="calc-row">
                    <span>Points Earned:</span>
                    <span>${invoice.loyaltyPointsEarned}</span>
                </div>
            </div>

            <!-- Payment Information -->
            <c:if test="${invoice.cashGiven != null && invoice.cashGiven > 0}">
                <div class="form-section">
                    <h4><i class="fas fa-credit-card"></i> Payment Details</h4>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Cash Given:</label>
                            <p style="padding: 12px 15px; background: #f8fafc; border-radius: 8px; margin: 0;">
                                Rs. <fmt:formatNumber value="${invoice.cashGiven}" pattern="#,##0.00" />
                            </p>
                        </div>
                        <div class="form-group">
                            <label>Change:</label>
                            <p style="padding: 12px 15px; background: #f8fafc; border-radius: 8px; margin: 0;">
                                Rs. <fmt:formatNumber value="${invoice.changeAmount}" pattern="#,##0.00" />
                            </p>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="form-footer">
    <a href="${pageContext.request.contextPath}/billing" class="btn-secondary">
        <i class="fas fa-arrow-left"></i> Back to Dashboard
    </a>
    <!-- NEW PRINT BUTTON -->
    <a href="${pageContext.request.contextPath}/print-invoice?id=${invoice.id}" 
       class="btn-primary" target="_blank" 
       onclick="return confirm('This will open a PDF of the invoice. Continue?')">
        <i class="fas fa-print"></i> Print Invoice
    </a>
</div>
        </div>
    </c:if>
    
    <!-- Default Dashboard View -->
    <c:if test="${param.action != 'view'}">
        <!-- Header -->
        <div class="billing-header">
            <div class="header-content">
                <div class="header-title">
                    <h1><i class="fas fa-receipt"></i> Billing System</h1>
                    <p>Manage invoices and process customer transactions</p>
                </div>
                <a href="${pageContext.request.contextPath}/create-invoice" class="btn-new-invoice">
                    <i class="fas fa-plus"></i> New Invoice
                </a>
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

        <!-- Statistics -->
        <div class="stats-container">
            <div class="stat-card">
                <div class="stat-icon invoices">
                    <i class="fas fa-file-invoice"></i>
                </div>
                <div class="stat-info">
                    <h3>${todayInvoices != null ? todayInvoices : 0}</h3>
                    <p>Today's Invoices</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon revenue">
                    <i class="fas fa-money-bill-wave"></i>
                </div>
                <div class="stat-info">
                    <h3>Rs. <fmt:formatNumber value="${todayRevenue != null ? todayRevenue : 0}" pattern="#,##0" /></h3>
                    <p>Today's Revenue</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon total">
                    <i class="fas fa-chart-line"></i>
                </div>
                <div class="stat-info">
                    <h3>${totalInvoices != null ? totalInvoices : 0}</h3>
                    <p>Total Invoices</p>
                </div>
            </div>
        </div>

        <!-- Invoices Section -->
        <div class="invoices-section">
            <!-- Enhanced Search Header -->
            <div class="section-header">
                <h2 class="section-title"><i class="fas fa-list"></i> Recent Invoices</h2>
                <div class="search-container">
                    <form method="GET" action="${pageContext.request.contextPath}/billing">
                        <!-- Search Type Dropdown -->
                        <select name="searchType" class="search-input">
                            <option value="id" ${param.searchType == 'id' || empty param.searchType ? 'selected' : ''}>
                                ID/Invoice #
                            </option>
                            <option value="phone" ${param.searchType == 'phone' ? 'selected' : ''}>
                                Client Phone
                            </option>
                        </select>
                        
                        <!-- Search Input -->
                        <input type="text" 
                               name="search" 
                               class="search-input" 
                               placeholder="Search invoices..." 
                               value="${param.search}">
                        
                        <!-- Search Button -->
                        <button type="submit" class="btn-primary" style="padding: 12px 20px; white-space: nowrap;">
                            <i class="fas fa-search"></i> Search
                        </button>
                        
                        <!-- Clear Search Button (only show if there's an active search) -->
                        <c:if test="${not empty param.search}">
                            <a href="${pageContext.request.contextPath}/billing" 
                               class="btn-secondary" 
                               style="padding: 12px 20px; white-space: nowrap; text-decoration: none;">
                                <i class="fas fa-times"></i> Clear
                            </a>
                        </c:if>
                    </form>
                </div>
            </div>

            <!-- Search Results Indicator -->
            <c:if test="${not empty param.search}">
                <div style="margin-bottom: 20px; padding: 15px; background: #f8fafc; border-radius: 8px; border-left: 4px solid #4f46e5;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <strong>Search Results:</strong> 
                            <c:choose>
                                <c:when test="${param.searchType == 'phone'}">
                                    Searching for invoices by client phone: "<em>${param.search}</em>"
                                </c:when>
                                <c:otherwise>
                                    Searching for invoice ID/Number: "<em>${param.search}</em>"
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <span class="badge">
                            ${invoices != null ? invoices.size() : 0} found
                        </span>
                    </div>
                </div>
            </c:if>

            <div class="invoices-container">
                <c:choose>
                    <c:when test="${not empty invoices}">
                        <div class="invoices-grid">
                            <c:forEach var="invoice" items="${invoices}">
                                <div class="invoice-card" data-invoice-id="${invoice.id}">
                                    <div class="invoice-header">
                                        <div class="invoice-number">
                                            Invoice #${invoice.invoiceNumber != null ? invoice.invoiceNumber : invoice.id}
                                        </div>
                                        <div class="invoice-date">
                                            <fmt:formatDate value="${invoice.invoiceDate != null ? invoice.invoiceDate : invoice.createdAt}" 
                                                           pattern="MMM dd, yyyy" />
                                        </div>
                                    </div>
                                    
                                    <div class="invoice-body">
                                        <div class="client-info">
                                            <i class="fas fa-user"></i>
                                            <span>
                                                <c:choose>
                                                    <c:when test="${invoice.client != null}">
                                                        ${invoice.client.fullName}
                                                        <c:if test="${param.searchType == 'phone' && not empty param.search}">
                                                            <br><small style="color: #6b7280;">${invoice.client.phone}</small>
                                                        </c:if>
                                                    </c:when>
                                                    <c:otherwise>
                                                        Walk-in Customer
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                        
                                        <div class="invoice-total">
                                            Rs. <fmt:formatNumber value="${invoice.totalAmount != null ? invoice.totalAmount : 0}" 
                                                                pattern="#,##0.00" />
                                        </div>
                                        
                                        <div class="invoice-meta">
                                            <div>
                                                <i class="fas fa-shopping-bag"></i>
                                                ${invoice.items != null ? invoice.items.size() : 0} items
                                            </div>
                                            <div>
                                                <i class="fas fa-star"></i>
                                                ${invoice.loyaltyPointsEarned != null ? invoice.loyaltyPointsEarned : 0} pts
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="invoice-actions">
                                        <a href="${pageContext.request.contextPath}/billing?action=view&id=${invoice.id}" 
                                           class="btn-action btn-edit" title="View">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <form method="POST" action="${pageContext.request.contextPath}/billing" 
                                              style="display: inline;" 
                                              onsubmit="return confirm('Are you sure you want to delete this invoice?')">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="invoiceId" value="${invoice.id}">
                                            <button type="submit" class="btn-action btn-delete" title="Delete">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <i class="fas fa-receipt"></i>
                            <c:choose>
                                <c:when test="${not empty param.search}">
                                    <h3>No Invoices Found</h3>
                                    <p>No invoices match your search criteria</p>
                                    <a href="${pageContext.request.contextPath}/billing" class="btn-secondary">
                                        View All Invoices
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <h3>No Invoices Found</h3>
                                    <p>Start creating invoices to see them here</p>
                                    <a href="${pageContext.request.contextPath}/create-invoice" class="btn-primary">
                                        Create Your First Invoice
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>
</main>

<script>
// Auto-hide alerts
document.addEventListener('DOMContentLoaded', function() {
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
    
    // Enhanced search functionality
    const searchTypeSelect = document.querySelector('select[name="searchType"]');
    const searchInput = document.querySelector('input[name="search"]');
    
    if (searchTypeSelect && searchInput) {
        searchTypeSelect.addEventListener('change', function() {
            // Update placeholder based on search type
            if (this.value === 'phone') {
                searchInput.placeholder = 'Enter client phone number...';
            } else {
                searchInput.placeholder = 'Enter invoice ID or number...';
            }
        });
        
        // Set initial placeholder
        if (searchTypeSelect.value === 'phone') {
            searchInput.placeholder = 'Enter client phone number...';
        } else {
            searchInput.placeholder = 'Enter invoice ID or number...';
        }
    }
    
    // Focus search input if there's an error message about search
    if (document.querySelector('.alert-error') && searchInput) {
        searchInput.focus();
    }
});


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