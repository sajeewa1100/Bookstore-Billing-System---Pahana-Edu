<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Pahana Edu</title>
    
    <!-- CSS Files - Use proper context path -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="main-container">
        <!-- Include Sidebar -->
        <%@ include file="sidebar.jsp" %>
        
        <div class="content-area">
            <!-- Success/Error Messages -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> ${successMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
                </div>
            </c:if>
            
            <!-- Dashboard Header -->
            <div class="dashboard-header">
                <h1><i class="fas fa-chart-line"></i> Dashboard</h1>
                <p>Welcome back, ${currentUser.username}! Here's what's happening with your book inventory.</p>
            </div>
            
            <!-- Statistics Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-value">${totalBooks}</div>
                    <div class="stat-label">Total Books</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-value">${totalCategories}</div>
                    <div class="stat-label">Categories</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-value">${lowStockCount}</div>
                    <div class="stat-label">Low Stock Items</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-value">
                        <c:choose>
                            <c:when test="${not empty recentBooks}">
                                ${fn:length(recentBooks)}
                            </c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="stat-label">Total Inventory Items</div>
                </div>
            </div>
            
            <!-- Low Stock Books Section -->
            <c:if test="${not empty lowStockBooks}">
                <div class="dashboard-section">
                    <h3 class="section-title">
                        <i class="fas fa-exclamation-triangle low-stock-icon"></i>
                        Low Stock Alert (≤5 books)
                    </h3>
                    <ul class="book-list">
                        <c:forEach var="book" items="${lowStockBooks}" varStatus="status">
                            <li class="book-item">
                                <span class="book-name">${book.title}</span>
                                <span class="book-stock low-stock">
                                    <i class="fas fa-box"></i> ${book.stock} left
                                </span>
                            </li>
                            <c:if test="${status.index >= 9}">
                                <li class="book-item">
                                    <span class="more-items">
                                        And ${fn:length(lowStockBooks) - 10} more items...
                                    </span>
                                    <a href="${pageContext.request.contextPath}/BookServlet?action=books&filter=lowstock">
                                        View All
                                    </a>
                                </li>
                                <c:set var="breakLoop" value="true"/>
                            </c:if>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            
            <!-- Quick Actions -->
            <div class="dashboard-section">
                <h3 class="section-title">
                    <i class="fas fa-bolt"></i>
                    Quick Actions
                </h3>
                <div class="quick-actions">
                    <a href="${pageContext.request.contextPath}/BookServlet?action=add" 
                       class="btn btn-primary">
                        <i class="fas fa-plus"></i> Add New Book
                    </a>
                    <a href="${pageContext.request.contextPath}/BillingServlet?action=newInvoice" 
                       class="btn btn-success">
                        <i class="fas fa-file-invoice"></i> Create Invoice
                    </a>
                    <a href="${pageContext.request.contextPath}/ClientServlet?action=add" 
                       class="btn btn-info">
                        <i class="fas fa-user-plus"></i> Add Client
                    </a>
                    <a href="${pageContext.request.contextPath}/BillingServlet?action=report" 
                       class="btn btn-warning">
                        <i class="fas fa-chart-bar"></i> View Reports
                    </a>
                </div>
            </div>
            
            <!-- Recent Books Preview -->
            <c:if test="${not empty recentBooks}">
                <div class="dashboard-section">
                    <h3 class="section-title">
                        <i class="fas fa-clock"></i>
                        Book Inventory Overview
                    </h3>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Title</th>
                                    <th>Author</th>
                                    <th>Category</th>
                                    <th>Stock</th>
                                    <th>Price</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="book" items="${recentBooks}" begin="0" end="9">
                                    <tr>
                                        <td>${book.title}</td>
                                        <td>${book.author}</td>
                                        <td>${book.category}</td>
                                        <td>
                                            <span class="${book.stock <= 5 ? 'low-stock' : ''}">
                                                ${book.stock}
                                            </span>
                                        </td>
                                        <td>Rs. ${book.price}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/BookServlet?action=books" 
                           class="btn btn-outline-primary">
                            View All Books
                        </a>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
    
    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>