
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <div class="top-bar">
        <h2 class="section-title">📊 Dashboard</h2>
        <div class="welcome-text">
            Welcome back, <strong>${currentUser.username}</strong>!
            <c:if test="${not empty currentUser.companyName}">
                <br><small>${currentUser.companyName}</small>
            </c:if>
        </div>
    </div>

    <hr />

    <!-- Display Success/Error Messages -->
    <c:if test="${not empty successMessage}">
        <div class="success-message">
            <i class="fas fa-check-circle"></i> ${successMessage}
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="error-message">
            <i class="fas fa-exclamation-circle"></i> ${errorMessage}
        </div>
    </c:if>

    <!-- Dashboard Statistics Cards -->
    <div class="dashboard-stats">
        <div class="stat-card">
            <div class="stat-icon">
                <i class="fas fa-book"></i>
            </div>
            <div class="stat-content">
                <h3>${totalBooks}</h3>
                <p>Total Books</p>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon">
                <i class="fas fa-tags"></i>
            </div>
            <div class="stat-content">
                <h3>${totalCategories}</h3>
                <p>Categories</p>
            </div>
        </div>

        <div class="stat-card warning">
            <div class="stat-icon">
                <i class="fas fa-exclamation-triangle"></i>
            </div>
            <div class="stat-content">
                <h3>${lowStockCount}</h3>
                <p>Low Stock Items</p>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon">
                <i class="fas fa-user-tie"></i>
            </div>
            <div class="stat-content">
                <h3>${currentUser.role}</h3>
                <p>Your Role</p>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions">
        <h3>Quick Actions</h3>
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/BookServlet?action=books" class="action-btn">
                <i class="fas fa-book"></i>
                <span>Manage Books</span>
            </a>
            <a href="${pageContext.request.contextPath}/BillingServlet?action=newInvoice" class="action-btn">
                <i class="fas fa-plus-square"></i>
                <span>New Invoice</span>
            </a>
            <a href="${pageContext.request.contextPath}/BillingServlet?action=invoices" class="action-btn">
                <i class="fas fa-file-invoice"></i>
                <span>View Invoices</span>
            </a>
            <a href="${pageContext.request.contextPath}/BillingServlet?action=report" class="action-btn">
                <i class="fas fa-chart-bar"></i>
                <span>Reports</span>
            </a>
        </div>
    </div>

    <!-- Recent Activity Sections -->
    <div class="dashboard-content">
        <!-- Low Stock Alert -->
        <c:if test="${not empty lowStockBooks}">
            <div class="dashboard-section">
                <h3><i class="fas fa-exclamation-triangle"></i> Low Stock Alert</h3>
                <div class="table-container">
                    <table class="dashboard-table">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Author</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="book" items="${recentBooks}" varStatus="status">
                                <c:if test="${status.index < 10}"> <!-- Show only first 10 recent books -->
                                    <tr>
                                        <td>${book.title}</td>
                                        <td>${book.author}</td>
                                        <td>${book.category}</td>
                                        <td>Rs. ${book.price}</td>
                                        <td>${book.quantity}</td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                        </tbody>
                    </table>
                    <c:if test="${fn:length(recentBooks) > 10}">
                        <p class="table-footer">
                            <a href="${pageContext.request.contextPath}/BookServlet?action=books">
                                View all books →
                            </a>
                        </p>
                    </c:if>
                </div>
            </div>
        </c:if>
    </div>

    <!-- System Information (for managers) -->
    <c:if test="${isManager}">
        <div class="dashboard-section system-info">
            <h3><i class="fas fa-info-circle"></i> System Information</h3>
            <div class="info-grid">
                <div class="info-item">
                    <strong>User:</strong> ${currentUser.username}
                </div>
                <div class="info-item">
                    <strong>Role:</strong> ${currentUser.role}
                </div>
                <c:if test="${not empty currentUser.email}">
                    <div class="info-item">
                        <strong>Email:</strong> ${currentUser.email}
                    </div>
                </c:if>
                <c:if test="${not empty currentUser.companyName}">
                    <div class="info-item">
                        <strong>Company:</strong> ${currentUser.companyName}
                    </div>
                </c:if>
            </div>
        </div>
    </c:if>
</main>

<script>
    // Auto-hide success/error messages after 5 seconds
    document.addEventListener('DOMContentLoaded', function() {
        setTimeout(function() {
            var successMsg = document.querySelector('.success-message');
            var errorMsg = document.querySelector('.error-message');
            
            if (successMsg) {
                successMsg.style.transition = 'opacity 0.3s';
                successMsg.style.opacity = '0';
                setTimeout(function() { 
                    successMsg.style.display = 'none'; 
                }, 300);
            }
            
            if (errorMsg) {
                errorMsg.style.transition = 'opacity 0.3s';
                errorMsg.style.opacity = '0';
                setTimeout(function() { 
                    errorMsg.style.display = 'none'; 
                }, 300);
            }
        }, 5000);
    });

    // Refresh dashboard data periodically (every 5 minutes)
    setInterval(function() {
        // Only refresh if user is still active (optional)
        if (document.visibilityState === 'visible') {
            location.reload();
        }
    }, 300000); // 5 minutes
</script>

<style>
    /* Dashboard specific styles */
    .dashboard-stats {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 20px;
        margin: 20px 0;
    }

    .stat-card {
        background: #fff;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        display: flex;
        align-items: center;
        border-left: 4px solid #007bff;
    }

    .stat-card.warning {
        border-left-color: #ffc107;
    }

    .stat-icon {
        font-size: 2.5em;
        margin-right: 15px;
        color: #007bff;
    }

    .stat-card.warning .stat-icon {
        color: #ffc107;
    }

    .stat-content h3 {
        margin: 0;
        font-size: 2em;
        font-weight: bold;
        color: #333;
    }

    .stat-content p {
        margin: 5px 0 0 0;
        color: #666;
        font-size: 0.9em;
    }

    .quick-actions {
        margin: 30px 0;
    }

    .quick-actions h3 {
        margin-bottom: 15px;
        color: #333;
    }

    .action-buttons {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 15px;
    }

    .action-btn {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20px;
        background: #f8f9fa;
        border: 1px solid #dee2e6;
        border-radius: 8px;
        text-decoration: none;
        color: #333;
        transition: all 0.3s ease;
    }

    .action-btn:hover {
        background: #007bff;
        color: #fff;
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0,123,255,0.3);
    }

    .action-btn i {
        font-size: 2em;
        margin-bottom: 10px;
    }

    .dashboard-content {
        display: grid;
        gap: 30px;
        margin-top: 30px;
    }

    .dashboard-section {
        background: #fff;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .dashboard-section h3 {
        margin-top: 0;
        margin-bottom: 15px;
        color: #333;
        border-bottom: 2px solid #f0f0f0;
        padding-bottom: 10px;
    }

    .dashboard-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 10px;
    }

    .dashboard-table th,
    .dashboard-table td {
        padding: 10px;
        text-align: left;
        border-bottom: 1px solid #eee;
    }

    .dashboard-table th {
        background: #f8f9fa;
        font-weight: bold;
        color: #333;
    }

    .stock-warning {
        color: #dc3545;
        font-weight: bold;
    }

    .btn-small {
        padding: 5px 10px;
        background: #007bff;
        color: #fff;
        text-decoration: none;
        border-radius: 4px;
        font-size: 0.85em;
    }

    .btn-small:hover {
        background: #0056b3;
    }

    .table-footer,
    .section-footer {
        text-align: center;
        margin-top: 15px;
        padding-top: 15px;
        border-top: 1px solid #eee;
    }

    .table-footer a,
    .section-footer a {
        color: #007bff;
        text-decoration: none;
    }

    .table-footer a:hover,
    .section-footer a:hover {
        text-decoration: underline;
    }

    .category-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        gap: 10px;
        margin-top: 15px;
    }

    .category-item {
        background: #f8f9fa;
        border-radius: 6px;
        padding: 15px;
        text-align: center;
        transition: all 0.3s ease;
    }

    .category-item:hover {
        background: #e9ecef;
        transform: translateY(-2px);
    }

    .category-item a {
        text-decoration: none;
        color: #333;
        display: block;
    }

    .category-item i {
        display: block;
        font-size: 1.5em;
        margin-bottom: 8px;
        color: #007bff;
    }

    .system-info .info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 15px;
        margin-top: 15px;
    }

    .info-item {
        padding: 10px;
        background: #f8f9fa;
        border-radius: 4px;
    }

    .welcome-text {
        font-size: 1.1em;
        color: #333;
    }

    .welcome-text strong {
        color: #007bff;
    }

    .welcome-text small {
        color: #666;
        font-size: 0.9em;
    }
</style>

</body>
</html>
                                <th>Title</th>
                                <th>Author</th>
                                <th>Category</th>
                                <th>Stock</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="book" items="${lowStockBooks}" varStatus="status">
                                <c:if test="${status.index < 5}"> <!-- Show only first 5 -->
                                    <tr>
                                        <td>${book.title}</td>
                                        <td>${book.author}</td>
                                        <td>${book.category}</td>
                                        <td class="stock-warning">${book.quantity}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/BookServlet?action=books" class="btn-small">
                                                Update Stock
                                            </a>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                        </tbody>
                    </table>
                    <c:if test="${fn:length(lowStockBooks) > 5}">
                        <p class="table-footer">
                            <a href="${pageContext.request.contextPath}/BookServlet?action=books">
                                View all ${fn:length(lowStockBooks)} low stock items →
                            </a>
                        </p>
                    </c:if>
                </div>
            </div>


        <!-- Categories Overview -->
        <c:if test="${not empty categories}">
            <div class="dashboard-section">
                <h3><i class="fas fa-tags"></i> Categories Overview</h3>
                <div class="category-grid">
                    <c:forEach var="category" items="${categories}" varStatus="status">
                        <c:if test="${status.index < 8}"> <!-- Show only first 8 categories -->
                            <div class="category-item">
                                <a href="${pageContext.request.contextPath}/BookServlet?action=books&category=${category}">
                                    <i class="fas fa-folder"></i>
                                    <span>${category}</span>
                                </a>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
                <c:if test="${fn:length(categories) > 8}">
                    <p class="section-footer">
                        <a href="${pageContext.request.contextPath}/BookServlet?action=books">
                            View all categories →
                        </a>
                    </p>
                </c:if>
            </div>
        </c:if>
     </body>
</html>
        