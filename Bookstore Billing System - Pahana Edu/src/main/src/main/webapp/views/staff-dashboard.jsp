<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Dashboard - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        :root {
            --primary-color: #D86C36;
            --primary-dark: #C4552C;
            --accent-color: #f2a23f;
            --background-light: #F2E7DC;
            --background-white: #fff;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: var(--background-light);
            color: #1a202c;
        }

        .staff-header {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
            color: white;
            padding: 2rem;
            box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
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

        .user-info {
            text-align: right;
        }

        .user-info span {
            display: block;
            font-size: 1.1rem;
            margin-bottom: 5px;
        }

        .user-role {
            background: rgba(255, 255, 255, 0.2);
            padding: 5px 15px;
            border-radius: 15px;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 30px 20px;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 25px;
            margin-bottom: 40px;
        }

        .stat-card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
            border: 1px solid #e2e8f0;
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
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
        }

        .stat-info h3 {
            font-size: 2rem;
            font-weight: 700;
            color: #1a202c;
            margin: 0 0 5px 0;
        }

        .stat-info p {
            color: #4a5568;
            margin: 0;
            font-weight: 500;
        }

        .content-section {
            background: white;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
            padding: 30px;
            margin-bottom: 30px;
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            padding-bottom: 20px;
            border-bottom: 2px solid #f7fafc;
        }

        .section-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: #1a202c;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .btn {
            padding: 12px 25px;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            text-align: center;
        }

        .btn-primary {
            background: var(--primary-color);
            color: white;
        }

        .btn-primary:hover {
            background: var(--primary-dark);
            transform: translateY(-1px);
            color: white;
            text-decoration: none;
        }

        .btn-secondary {
            background: #4a5568;
            color: white;
        }

        .btn-secondary:hover {
            background: #2d3748;
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
            background: #c6f6d5;
            color: #22543d;
            border: 1px solid #9ae6b4;
        }

        .alert-error {
            background: #fed7d7;
            color: #742a2a;
            border: 1px solid #fc8181;
        }

        .alert-info {
            background: #bee3f8;
            color: #2a69ac;
            border: 1px solid #63b3ed;
        }

        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }

        .action-card {
            background: white;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            padding: 25px;
            text-align: center;
            transition: all 0.3s ease;
            text-decoration: none;
            color: #1a202c;
        }

        .action-card:hover {
            border-color: var(--primary-color);
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            text-decoration: none;
            color: var(--primary-color);
        }

        .action-card i {
            font-size: 2.5rem;
            margin-bottom: 15px;
            color: var(--primary-color);
        }

        .action-card h3 {
            margin: 0 0 10px 0;
            font-size: 1.2rem;
        }

        .action-card p {
            color: #4a5568;
            margin: 0;
            font-size: 0.9rem;
        }

        .logout-btn {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            border: 1px solid rgba(255, 255, 255, 0.3);
            padding: 8px 20px;
            border-radius: 20px;
            text-decoration: none;
            transition: all 0.3s ease;
        }

        .logout-btn:hover {
            background: rgba(255, 255, 255, 0.3);
            color: white;
            text-decoration: none;
        }

        @media (max-width: 768px) {
            .header-content {
                flex-direction: column;
                gap: 15px;
                text-align: center;
            }

            .stats-grid,
            .quick-actions {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<!-- Staff Dashboard Header -->
<div class="staff-header">
    <div class="header-content">
        <div class="header-title">
            <h1><i class="fas fa-home"></i> Staff Dashboard</h1>
            <p>Welcome to your workspace</p>
        </div>
        <div class="user-info">
            <span>Welcome, ${currentUser.fullName != null ? currentUser.fullName : currentUser.username}</span>
            <div class="user-role">Staff Member</div>
            <a href="AuthServlet?action=logout" class="logout-btn">
                <i class="fas fa-sign-out-alt"></i> Logout
            </a>
        </div>
    </div>
</div>

<div class="container">
    <!-- Alert Messages -->
    <c:if test="${not empty param.success}">
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> ${param.success}
        </div>
    </c:if>

    <c:if test="${not empty param.error}">
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i> ${param.error}
        </div>
    </c:if>

    <!-- Welcome Message -->
    <div class="content-section">
        <div class="alert alert-info">
            <i class="fas fa-info-circle"></i>
            Welcome to your staff dashboard! You can create invoices, manage books, and handle client information. 
            Use the navigation menu to access different features.
        </div>
    </div>

    <!-- Statistics (if available) -->
    <c:if test="${not empty dashboardStats}">
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-file-invoice"></i>
                </div>
                <div class="stat-info">
                    <h3>${dashboardStats.myInvoicesCount != null ? dashboardStats.myInvoicesCount : 0}</h3>
                    <p>My Invoices</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-users"></i>
                </div>
                <div class="stat-info">
                    <h3>${dashboardStats.totalClients != null ? dashboardStats.totalClients : 0}</h3>
                    <p>Total Clients</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-book"></i>
                </div>
                <div class="stat-info">
                    <h3>${dashboardStats.totalBooks != null ? dashboardStats.totalBooks : 0}</h3>
                    <p>Available Books</p>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Quick Actions -->
    <div class="content-section">
        <div class="section-header">
            <h2 class="section-title"><i class="fas fa-bolt"></i> Quick Actions</h2>
        </div>
        
        <div class="quick-actions">
            <a href="create-invoice" class="action-card">
                <i class="fas fa-plus-square"></i>
                <h3>Create Invoice</h3>
                <p>Generate a new invoice for a client</p>
            </a>
            
            <a href="billing?action=myInvoices" class="action-card">
                <i class="fas fa-file-alt"></i>
                <h3>My Invoices</h3>
                <p>View and manage invoices you've created</p>
            </a>
            
            <a href="clients" class="action-card">
                <i class="fas fa-address-book"></i>
                <h3>Manage Clients</h3>
                <p>Add, edit, and manage client information</p>
            </a>
            
            <a href="books" class="action-card">
                <i class="fas fa-book"></i>
                <h3>Manage Books</h3>
                <p>Add, edit, and manage book inventory</p>
            </a>
            
            <a href="AuthServlet?action=profile" class="action-card">
                <i class="fas fa-user-edit"></i>
                <h3>My Profile</h3>
                <p>Update your profile and change password</p>
            </a>
        </div>
    </div>

    <!-- Recent Activity (if available) -->
    <c:if test="${not empty recentInvoices}">
        <div class="content-section">
            <div class="section-header">
                <h2 class="section-title"><i class="fas fa-clock"></i> Recent Activity</h2>
                <a href="BillingServlet?action=myInvoices" class="btn btn-primary">View All</a>
            </div>
            
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="background: #f7fafc;">
                            <th style="padding: 12px; text-align: left; border-bottom: 1px solid #e2e8f0;">Invoice #</th>
                            <th style="padding: 12px; text-align: left; border-bottom: 1px solid #e2e8f0;">Client</th>
                            <th style="padding: 12px; text-align: left; border-bottom: 1px solid #e2e8f0;">Amount</th>
                            <th style="padding: 12px; text-align: left; border-bottom: 1px solid #e2e8f0;">Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${recentInvoices}" var="invoice" begin="0" end="4">
                            <tr style="border-bottom: 1px solid #e2e8f0;">
                                <td style="padding: 12px;">#${invoice.invoiceNumber}</td>
                                <td style="padding: 12px;">${invoice.clientName}</td>
                                <td style="padding: 12px;">Rs. <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0.00" /></td>
                                <td style="padding: 12px;"><fmt:formatDate value="${invoice.invoiceDate}" pattern="MMM dd, yyyy" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <!-- Help Section -->
    <div class="content-section">
        <div class="section-header">
            <h2 class="section-title"><i class="fas fa-question-circle"></i> Need Help?</h2>
        </div>
        
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px;">
            <div style="padding: 20px; background: #f7fafc; border-radius: 10px;">
                <h4 style="color: var(--primary-color); margin: 0 0 10px 0;">
                    <i class="fas fa-file-invoice"></i> Creating Invoices
                </h4>
                <p style="margin: 0; color: #4a5568; font-size: 0.9rem;">
                    Click "Create Invoice" to generate new invoices. Make sure client and book information is up to date.
                </p>
            </div>
            
            <div style="padding: 20px; background: #f7fafc; border-radius: 10px;">
                <h4 style="color: var(--primary-color); margin: 0 0 10px 0;">
                    <i class="fas fa-users"></i> Managing Clients
                </h4>
                <p style="margin: 0; color: #4a5568; font-size: 0.9rem;">
                    Add new clients or update existing client information before creating invoices for them.
                </p>
            </div>
            
            <div style="padding: 20px; background: #f7fafc; border-radius: 10px;">
                <h4 style="color: var(--primary-color); margin: 0 0 10px 0;">
                    <i class="fas fa-book"></i> Book Inventory
                </h4>
                <p style="margin: 0; color: #4a5568; font-size: 0.9rem;">
                    Keep book inventory updated with current prices and stock levels for accurate invoicing.
                </p>
            </div>
        </div>
    </div>
</div>

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
});
</script>

</body>
</html>