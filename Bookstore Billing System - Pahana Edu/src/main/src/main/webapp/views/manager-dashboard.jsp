<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manager Dashboard - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
       :root {
    --primary-color: #D86C36;
    --primary-dark: #C4552C;
    --primary-darker: #A63F22;
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

        /* Dashboard-specific header styling that works with sidebar */
        .manager-header {
            background:rgb(216, 108, 54);
            color: white;
            padding: 2rem;
            margin: -30px -30px 30px -30px;
            border-radius: 0 0 20px 20px;
            box-shadow: 0 8px 32px rgba(216, 108, 54, 0.3);
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

        /* Main container - no need to adjust for sidebar as it's handled by main layout */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 30px 20px;
        }

        .nav-tabs {
            display: flex;
            gap: 15px;
            margin-bottom: 30px;
            padding: 0;
            list-style: none;
            flex-wrap: wrap;
        }

        .nav-tab {
            padding: 12px 25px;
            background: white;
            border: 2px solid #e2e8f0;
            border-radius: 50px;
            cursor: pointer;
            text-decoration: none;
            color: #4a5568;
            font-weight: 600;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .nav-tab:hover {
            color: var(--primary-color);
            border-color: var(--primary-color);
            text-decoration: none;
        }

        .nav-tab.active {
            background: var(--primary-color);
            color: white;
            border-color: var(--primary-color);
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

        .btn-danger {
            background: #e53e3e;
            color: white;
        }

        .btn-danger:hover {
            background: #c53030;
            color: white;
            text-decoration: none;
        }

        .btn-warning {
            background: #ed8936;
            color: white;
        }

        .btn-warning:hover {
            background: #dd6b20;
            color: white;
            text-decoration: none;
        }

        .btn-small {
            padding: 8px 15px;
            font-size: 0.9rem;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .data-table th,
        .data-table td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }

        .data-table th {
            background: #f7fafc;
            font-weight: 600;
            color: #2d3748;
        }

        .data-table tr:hover {
            background: #f7fafc;
        }

        .form-container {
            background: #f7fafc;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 20px;
            border: 1px solid #e2e8f0;
        }

        .form-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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
            color: #2d3748;
        }

        .form-input {
            padding: 12px 15px;
            border: 2px solid #e2e8f0;
            border-radius: 8px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .form-input:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(216, 108, 54, 0.1);
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

        .alert-warning {
            background: #faf5e4;
            color: #744210;
            border: 1px solid #f6e05e;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #4a5568;
        }

        .empty-state i {
            font-size: 3rem;
            margin-bottom: 20px;
            color: #cbd5e0;
        }

        .empty-state h3 {
            font-size: 1.3rem;
            margin-bottom: 10px;
            color: #2d3748;
        }

        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            backdrop-filter: blur(3px);
        }

        .modal-content {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            border-radius: 15px;
            padding: 30px;
            max-width: 600px;
            width: 90%;
            max-height: 80vh;
            overflow-y: auto;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #e2e8f0;
        }

        .modal-close {
            background: none;
            border: none;
            font-size: 1.5rem;
            cursor: pointer;
            color: #4a5568;
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

        .badge.staff { background: #4facfe; }
        .badge.manager { background: #764ba2; }
        .badge.admin { background: #e53e3e; }
        .badge.active { background: #48bb78; }
        .badge.inactive { background: #a0aec0; }

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

        .status-indicator {
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
        }

        .status-dot.active { background: #48bb78; }
        .status-dot.inactive { background: #a0aec0; }

        /* Search container styling to match billing system */
        .search-container {
            position: relative;
        }

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
            box-shadow: 0 0 0 3px rgba(216, 108, 54, 0.1);
        }

        @media (max-width: 768px) {
            .header-content {
                flex-direction: column;
                gap: 15px;
                text-align: center;
            }

            .form-row {
                grid-template-columns: 1fr;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            /* Responsive search */
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
       
        
        .nav-tabs {
    list-style: none;
    padding: 0;
    margin: 20px 0;
    display: flex;
    border-bottom: 2px solid #e2e8f0;
    gap: 5px;
}

.nav-tabs li {
    margin: 0;
}

.nav-tab {
    display: inline-block;
    padding: 12px 24px;
    text-decoration: none;
    color: #64748b;
    background-color: #f8fafc;
    border: 2px solid #e2e8f0;
    border-bottom: none;
    border-radius: 8px 8px 0 0;
    font-weight: 500;
    transition: all 0.3s ease;
    position: relative;
    top: 2px;
}

.nav-tab:hover {
    background-color: #f1f5f9;
    color: #475569;
    transform: translateY(-2px);
}

.nav-tab.active {
    background-color: #3b82f6;
    color: white;
    border-color: #3b82f6;
    box-shadow: 0 4px 6px rgba(59, 130, 246, 0.15);
}

.nav-tab.active:hover {
    background-color: #2563eb;
    transform: translateY(-2px);
}

.nav-tab i {
    margin-right: 8px;
} }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
<!-- Manager Dashboard Header -->
<div class="manager-header">
    <div class="header-content">
        <div class="header-title">
            <h1><i class="fas fa-chart-line"></i> Manager Dashboard</h1>
            <p>Staff management and business overview</p>
        </div>
        <div class="user-info">
            <span>Welcome, ${currentUser.fullName != null ? currentUser.fullName : currentUser.username}</span>
            <div class="user-role">${currentUser.role}</div>
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

    <c:if test="${not empty success}">
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i> ${success}
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i> ${error}
        </div>
    </c:if>

    <!-- Navigation -->
    <ul class="nav-tabs">
        <li>
            <a href="ManagerServlet?action=dashboard" 
               class="nav-tab ${param.action == null || param.action == 'dashboard' ? 'active' : ''}">
                <i class="fas fa-home"></i> Dashboard
            </a>
        </li>
        <li>
            <a href="ManagerServlet?action=manageStaff" 
               class="nav-tab ${param.action == 'manageStaff' ? 'active' : ''}">
                <i class="fas fa-users"></i> Staff Management
            </a>
        </li>
        <li>
            <a href="ManagerServlet?action=loyaltySettings" 
               class="nav-tab ${param.action == 'loyaltySettings' ? 'active' : ''}">
                <i class="fas fa-medal"></i> Loyalty Settings
            </a>
        </li>
    </ul>

    <!-- Dashboard Overview -->
    <c:if test="${param.action == null || param.action == 'dashboard'}">
        <!-- Statistics Cards -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-money-bill-wave"></i>
                </div>
                <div class="stat-info">
                    <h3>Rs. <fmt:formatNumber value="${dashboardStats.todayRevenue}" pattern="#,##0.00" /></h3>
                    <p>Today's Revenue</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-chart-line"></i>
                </div>
                <div class="stat-info">
                    <h3>Rs. <fmt:formatNumber value="${dashboardStats.monthlyRevenue}" pattern="#,##0.00" /></h3>
                    <p>Monthly Revenue</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-receipt"></i>
                </div>
                <div class="stat-info">
                    <h3>${dashboardStats.todayInvoices}</h3>
                    <p>Today's Invoices</p>
                </div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-users"></i>
                </div>
                <div class="stat-info">
                    <h3>${dashboardStats.totalStaff}</h3>
                    <p>Total Staff</p>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="content-section">
            <div class="section-header">
                <h2 class="section-title"><i class="fas fa-bolt"></i> Quick Actions</h2>
            </div>
            
            <div style="display: flex; gap: 15px; flex-wrap: wrap;">
                <a href="ManagerServlet?action=manageStaff" class="btn btn-primary">
                    <i class="fas fa-users"></i> Manage Staff
                </a>
                <a href="ManagerServlet?action=loyaltySettings" class="btn btn-secondary">
                    <i class="fas fa-medal"></i> Loyalty Settings
                </a>
                <a href="billing" class="btn btn-secondary">
                    <i class="fas fa-receipt"></i> View Invoices
                </a>
                <a href="create-invoice" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Create Invoice
                </a>
            </div>
        </div>

        <!-- Staff Overview -->
        <div class="content-section">
            <div class="section-header">
                <h2 class="section-title"><i class="fas fa-users"></i> Staff Overview</h2>
                <span class="badge">Total: ${totalStaff}</span>
            </div>
            
            <c:choose>
                <c:when test="${not empty staffList && staffList.size() > 0}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Employee ID</th>
                                <th>Position</th>
                                <th>Email</th>
                                <th>Login Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${staffList}" var="staff" begin="0" end="4">
                                <tr>
                                    <td>${staff.firstName} ${staff.lastName}</td>
                                    <td>${staff.employeeId}</td>
                                    <td>
                                        <span class="badge ${staff.position.toLowerCase()}">${staff.position}</span>
                                    </td>
                                    <td>${staff.email}</td>
                                    <td>
                                        <div class="status-indicator">
                                            <span class="status-dot ${staff.hasUserAccount ? 'active' : 'inactive'}"></span>
                                            ${staff.hasUserAccount ? 'Login Enabled' : 'No Login'}
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div style="text-align: right; margin-top: 15px;">
                        <a href="ManagerServlet?action=manageStaff" class="btn btn-primary">
                            Manage All Staff <i class="fas fa-arrow-right"></i>
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <i class="fas fa-users"></i>
                        <h3>No Staff Members</h3>
                        <p>Add your first staff member to get started</p>
                        <a href="ManagerServlet?action=manageStaff" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Add Staff Member
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <!-- Staff Management Section -->
    <c:if test="${param.action == 'manageStaff'}">
        <div class="content-section">
            <div class="section-header">
                <h2 class="section-title"><i class="fas fa-users"></i> Staff Management</h2>
                <button class="btn btn-primary" onclick="showCreateStaffModal()">
                    <i class="fas fa-plus"></i> Add New Staff
                </button>
            </div>
            
            <c:choose>
                <c:when test="${not empty staffList}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Employee ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Position</th>
                                <th>Login Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="staff" items="${staffList}">
                                <tr>
                                    <td>${staff.employeeId}</td>
                                    <td>${staff.firstName} ${staff.lastName}</td>
                                    <td>${staff.email}</td>
                                    <td>${staff.phone}</td>
                                    <td>
                                        <span class="badge ${staff.position.toLowerCase()}">${staff.position}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${staff.hasUserAccount}">
                                                <span class="badge active">
                                                    <i class="fas fa-check"></i> Login Enabled
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge inactive">
                                                    <i class="fas fa-times"></i> No Login
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <button class="btn btn-primary btn-small" 
                                                onclick="editStaff('${staff.id}', '${staff.employeeId}', '${staff.firstName}', '${staff.lastName}', '${staff.email}', '${staff.phone}', '${staff.position}')">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        
                                        <c:if test="${staff.hasUserAccount}">
                                            <button class="btn btn-warning btn-small"
                                                    onclick="resetPassword('${staff.id}', '${staff.firstName} ${staff.lastName}')"
                                                    title="Reset Password">
                                                <i class="fas fa-key"></i>
                                            </button>
                                        </c:if>
                                        
                                        <form method="POST" style="display: inline;" 
                                              action="ManagerServlet"
                                              onsubmit="return confirm('Are you sure you want to delete this staff member? This will also remove their login access.')">
                                            <input type="hidden" name="action" value="deleteStaff">
                                            <input type="hidden" name="staffId" value="${staff.id}">
                                            <button type="submit" class="btn btn-danger btn-small">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <i class="fas fa-users"></i>
                        <h3>No Staff Members</h3>
                        <p>Add your first staff member to get started</p>
                        <button class="btn btn-primary" onclick="showCreateStaffModal()">
                            <i class="fas fa-plus"></i> Add First Staff Member
                        </button>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <!-- Loyalty Settings Section -->
    <c:if test="${param.action == 'loyaltySettings'}">
        <div class="content-section">
            <div class="section-header">
                <h2 class="section-title"><i class="fas fa-medal"></i> Loyalty Program Settings</h2>
            </div>
            
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i>
                Configure your customer loyalty program settings. Changes will apply to all future transactions.
            </div>
            
            <form method="POST" action="ManagerServlet" class="loyalty-settings-form">
                <input type="hidden" name="action" value="updateLoyaltySettings">
                
                <!-- Points Configuration -->
                <div class="form-container">
                    <h4><i class="fas fa-star"></i> Points Configuration</h4>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Points per Rs. 100 spent:</label>
                            <input type="number" name="pointsPer100Rs" class="form-input" 
                                   value="${loyaltySettings.pointsPer100Rs}" 
                                   min="1" max="100" required>
                            <small style="color: #4a5568;">How many points customers earn per Rs. 100 spent</small>
                        </div>
                    </div>
                </div>
                
                <!-- Silver Tier -->
                <div class="form-container">
                    <h4><i class="fas fa-medal" style="color: #c0c0c0;"></i> Silver Tier</h4>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Silver Discount (%):</label>
                            <input type="number" name="silverDiscount" class="form-input" 
                                   value="${loyaltySettings.silverDiscount}" 
                                   min="0" max="50" step="0.01" required>
                            <small style="color: #4a5568;">Discount percentage for silver tier customers</small>
                        </div>
                    </div>
                </div>
                
                <!-- Gold Tier -->
                <div class="form-container">
                    <h4><i class="fas fa-medal" style="color: #ffd700;"></i> Gold Tier</h4>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Gold Threshold (points):</label>
                            <input type="number" name="goldThreshold" class="form-input" 
                                   value="${loyaltySettings.goldThreshold}" 
                                   min="1" required>
                            <small style="color: #4a5568;">Points required to reach gold tier</small>
                        </div>
                        <div class="form-group">
                            <label>Gold Discount (%):</label>
                            <input type="number" name="goldDiscount" class="form-input" 
                                   value="${loyaltySettings.goldDiscount}" 
                                   min="0" max="50" step="0.01" required>
                            <small style="color: #4a5568;">Discount percentage for gold tier customers</small>
                        </div>
                    </div>
                </div>
                
                <!-- Platinum Tier -->
                <div class="form-container">
                    <h4><i class="fas fa-medal" style="color: #e5e4e2;"></i> Platinum Tier</h4>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Platinum Threshold (points):</label>
                            <input type="number" name="platinumThreshold" class="form-input" 
                                   value="${loyaltySettings.platinumThreshold}" 
                                   min="1" required>
                            <small style="color: #4a5568;">Points required to reach platinum tier</small>
                        </div>
                        <div class="form-group">
                            <label>Platinum Discount (%):</label>
                            <input type="number" name="platinumDiscount" class="form-input" 
                                   value="${loyaltySettings.platinumDiscount}" 
                                   min="0" max="50" step="0.01" required>
                            <small style="color: #4a5568;">Discount percentage for platinum tier customers</small>
                        </div>
                    </div>
                </div>
                
                <div style="text-align: right; margin-top: 30px;">
                    <button type="reset" class="btn btn-secondary">Reset</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Save Loyalty Settings
                    </button>
                </div>
            </form>
        </div>
    </c:if>
</div>

<!-- Staff Modal -->
<div id="staffModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3 id="modalTitle">Add New Staff</h3>
            <button class="modal-close" onclick="closeStaffModal()">&times;</button>
        </div>
        
        <form id="staffForm" method="POST" action="ManagerServlet">
            <input type="hidden" name="action" value="createStaff" id="formAction">
            <input type="hidden" name="staffId" id="staffId">
            
            <!-- Basic Information -->
            <div class="form-container">
                <h4><i class="fas fa-user"></i> Basic Information</h4>
                <div class="form-row">
                    <div class="form-group">
                        <label>Employee ID: *</label>
                        <input type="text" name="employeeId" id="employeeId" class="form-input" required>
                    </div>
                    <div class="form-group">
                        <label>Position:</label>
                        <select name="position" id="position" class="form-input">
                            <option value="staff">Staff</option>
                            <option value="manager">Manager</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>First Name: *</label>
                        <input type="text" name="firstName" id="firstName" class="form-input" required>
                    </div>
                    <div class="form-group">
                        <label>Last Name: *</label>
                        <input type="text" name="lastName" id="lastName" class="form-input" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Email:</label>
                        <input type="email" name="email" id="email" class="form-input">
                    </div>
                    <div class="form-group">
                        <label>Phone:</label>
                        <input type="tel" name="phone" id="phone" class="form-input">
                    </div>
                </div>
            </div>
            
            <!-- Login Credentials -->
            <div class="form-container" id="loginSection">
                <h4><i class="fas fa-key"></i> Login Credentials (Optional)</h4>
                <div class="alert alert-warning">
                    <i class="fas fa-info-circle"></i>
                    If you provide login credentials, this staff member can access the billing system, manage books, and clients.
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Username:</label>
                        <input type="text" name="username" id="username" class="form-input">
                        <small style="color: #4a5568;">Leave empty if no login required</small>
                    </div>
                    <div class="form-group">
                        <label>Password:</label>
                        <input type="password" name="password" id="password" class="form-input">
                        <small style="color: #4a5568;">Only required if username is provided</small>
                    </div>
                </div>
            </div>
            
            <div style="text-align: right; margin-top: 20px;">
                <button type="button" class="btn btn-secondary" onclick="closeStaffModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> Save Staff
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Reset Password Modal -->
<div id="resetPasswordModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Reset Password</h3>
            <button class="modal-close" onclick="closeResetPasswordModal()">&times;</button>
        </div>
        
        <form method="POST" action="ManagerServlet">
            <input type="hidden" name="action" value="resetStaffPassword">
            <input type="hidden" name="staffId" id="resetStaffId">
            
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i>
                Reset password for <strong id="resetStaffName"></strong>?
            </div>
            
            <div class="form-group">
                <label>New Password:</label>
                <input type="password" name="newPassword" class="form-input" required>
                <small style="color: #4a5568;">Enter a new password for this staff member</small>
            </div>
            
            <div style="text-align: right; margin-top: 20px;">
                <button type="button" class="btn btn-secondary" onclick="closeResetPasswordModal()">Cancel</button>
                <button type="submit" class="btn btn-warning">
                    <i class="fas fa-key"></i> Reset Password
                </button>
            </div>
        </form>
    </div>
</div>
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
});

// Staff modal functions
function showCreateStaffModal() {
    document.getElementById('modalTitle').textContent = 'Add New Staff';
    document.getElementById('formAction').value = 'createStaff';
    document.getElementById('staffId').value = '';
    document.getElementById('staffForm').reset();
    document.getElementById('loginSection').style.display = 'block';
    document.getElementById('staffModal').style.display = 'block';
}

function editStaff(id, empId, firstName, lastName, email, phone, position) {
    document.getElementById('modalTitle').textContent = 'Edit Staff';
    document.getElementById('formAction').value = 'updateStaff';
    document.getElementById('staffId').value = id;
    document.getElementById('employeeId').value = empId || '';
    document.getElementById('firstName').value = firstName || '';
    document.getElementById('lastName').value = lastName || '';
    document.getElementById('email').value = email || '';
    document.getElementById('phone').value = phone || '';
    document.getElementById('position').value = position || 'staff';
    
    // Hide login section for existing staff
    document.getElementById('loginSection').style.display = 'none';
    
    document.getElementById('staffModal').style.display = 'block';
}

function closeStaffModal() {
    document.getElementById('staffModal').style.display = 'none';
}

function resetPassword(staffId, staffName) {
    document.getElementById('resetStaffId').value = staffId;
    document.getElementById('resetStaffName').textContent = staffName;
    document.getElementById('resetPasswordModal').style.display = 'block';
}

function closeResetPasswordModal() {
    document.getElementById('resetPasswordModal').style.display = 'none';
}

// Auto-fill username when employee ID changes (for new staff)
document.getElementById('employeeId').addEventListener('input', function() {
    const formAction = document.getElementById('formAction').value;
    if (formAction === 'createStaff' && this.value) {
        const username = document.getElementById('username');
        if (!username.value) {
            username.value = this.value.toLowerCase();
        }
    }
});

// Close modals when clicking outside
window.onclick = function(event) {
    const staffModal = document.getElementById('staffModal');
    const resetPasswordModal = document.getElementById('resetPasswordModal');
    
    if (event.target == staffModal) {
        staffModal.style.display = 'none';
    }
    
    if (event.target == resetPasswordModal) {
        resetPasswordModal.style.display = 'none';
    }
}

// Form validation
document.getElementById('staffForm').addEventListener('submit', function(e) {
    const employeeId = document.getElementById('employeeId').value.trim();
    const firstName = document.getElementById('firstName').value.trim();
    const lastName = document.getElementById('lastName').value.trim();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    
    if (!employeeId || !firstName || !lastName) {
        e.preventDefault();
        alert('Please fill in all required fields (Employee ID, First Name, Last Name)');
        return false;
    }
    
    // If username is provided, password must also be provided
    if (username && !password) {
        e.preventDefault();
        alert('Password is required when username is provided');
        return false;
    }
    
    // If password is provided, username must also be provided
    if (password && !username) {
        e.preventDefault();
        alert('Username is required when password is provided');
        return false;
    }
});

// Loyalty settings form validation
const loyaltyForm = document.querySelector('.loyalty-settings-form');
if (loyaltyForm) {
    loyaltyForm.addEventListener('submit', function(e) {
        const goldThreshold = parseInt(document.querySelector('input[name="goldThreshold"]').value);
        const platinumThreshold = parseInt(document.querySelector('input[name="platinumThreshold"]').value);
        
        if (platinumThreshold <= goldThreshold) {
            e.preventDefault();
            alert('Platinum threshold must be higher than Gold threshold');
            return false;
        }
        
        const silverDiscount = parseFloat(document.querySelector('input[name="silverDiscount"]').value);
        const goldDiscount = parseFloat(document.querySelector('input[name="goldDiscount"]').value);
        const platinumDiscount = parseFloat(document.querySelector('input[name="platinumDiscount"]').value);
        
        if (goldDiscount <= silverDiscount) {
            e.preventDefault();
            alert('Gold discount should be higher than Silver discount');
            return false;
        }
        
        if (platinumDiscount <= goldDiscount) {
            e.preventDefault();
            alert('Platinum discount should be higher than Gold discount');
            return false;
        }
    });
}
</script>

</body>
</html>