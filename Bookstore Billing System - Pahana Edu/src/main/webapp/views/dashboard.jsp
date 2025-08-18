<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Pahana Edu</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            --sidebar-bg: #2c3e50;
            --sidebar-hover: #34495e;
        }
        
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .sidebar {
            background: var(--sidebar-bg);
            min-height: 100vh;
            width: 250px;
            position: fixed;
            top: 0;
            left: 0;
            transition: all 0.3s;
            z-index: 1000;
        }
        
        .sidebar.collapsed {
            width: 70px;
        }
        
        .sidebar .nav-link {
            color: #ecf0f1;
            padding: 15px 20px;
            border-radius: 0;
            transition: all 0.3s;
            border-left: 3px solid transparent;
        }
        
        .sidebar .nav-link:hover {
            background-color: var(--sidebar-hover);
            border-left-color: #3498db;
            color: #ffffff;
        }
        
        .sidebar .nav-link.active {
            background-color: var(--sidebar-hover);
            border-left-color: #e74c3c;
            color: #ffffff;
        }
        
        .sidebar .nav-link i {
            width: 20px;
            text-align: center;
            margin-right: 10px;
        }
        
        .sidebar.collapsed .nav-link span {
            display: none;
        }
        
        .main-content {
            margin-left: 250px;
            transition: all 0.3s;
            min-height: 100vh;
        }
        
        .main-content.expanded {
            margin-left: 70px;
        }
        
        .navbar {
            background: var(--primary-gradient);
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .navbar-brand {
            color: white !important;
            font-weight: bold;
        }
        
        .stats-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            transition: transform 0.3s;
            border: none;
            overflow: hidden;
        }
        
        .stats-card:hover {
            transform: translateY(-5px);
        }
        
        .stats-card .card-body {
            padding: 2rem;
        }
        
        .stats-icon {
            width: 60px;
            height: 60px;
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            color: white;
        }
        
        .stats-icon.books {
            background: linear-gradient(135deg, #3498db, #2980b9);
        }
        
        .stats-icon.invoices {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
        }
        
        .stats-icon.clients {
            background: linear-gradient(135deg, #2ecc71, #27ae60);
        }
        
        .stats-icon.revenue {
            background: linear-gradient(135deg, #f39c12, #e67e22);
        }
        
        .chart-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            border: none;
        }
        
        .recent-activity {
            max-height: 400px;
            overflow-y: auto;
        }
        
        .activity-item {
            display: flex;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #ecf0f1;
        }
        
        .activity-item:last-child {
            border-bottom: none;
        }
        
        .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            font-size: 1rem;
            color: white;
        }
        
        .activity-icon.login {
            background: linear-gradient(135deg, #3498db, #2980b9);
        }
        
        .activity-icon.book {
            background: linear-gradient(135deg, #2ecc71, #27ae60);
        }
        
        .activity-icon.invoice {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
        }
        
        .quick-action-btn {
            border-radius: 12px;
            padding: 15px;
            border: none;
            color: white;
            font-weight: 600;
            transition: all 0.3s;
            text-decoration: none;
            display: block;
            text-align: center;
        }
        
        .quick-action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.2);
            color: white;
        }
        
        .quick-action-btn.books {
            background: linear-gradient(135deg, #3498db, #2980b9);
        }
        
        .quick-action-btn.invoices {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
        }
        
        .quick-action-btn.clients {
            background: linear-gradient(135deg, #2ecc71, #27ae60);
        }
        
        @media (max-width: 768px) {
            .sidebar {
                width: 70px;
            }
            
            .main-content {
                margin-left: 70px;
            }
            
            .sidebar .nav-link span {
                display: none;
            }
        }
    </style>
</head>
<body>
    <%
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("views/login.jsp");
            return;
        }
        
        // Check success message
        String successMessage = request.getParameter("success");
        
        // Format current date
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        String currentDate = now.format(formatter);
    %>
    
    <!-- Sidebar -->
    <div class="sidebar" id="sidebar">
        <div class="p-3">
            <h5 class="text-white mb-0">
                <i class="fas fa-graduation-cap me-2"></i>
                <span>Pahana Edu</span>
            </h5>
        </div>
        <nav class="nav flex-column">
            <a class="nav-link active" href="${pageContext.request.contextPath}/BookServlet?action=dashboard">
                <i class="fas fa-tachometer-alt"></i>
                <span>Dashboard</span>
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/BookServlet?action=list">
                <i class="fas fa-book"></i>
                <span>Books</span>
            </a>
            <a class="nav-link" href="${pageContext.request.contextPath}/BillingServlet?action=invoices">
                <i class="fas fa-file-invoice"></i>
                <span>Billing</span>
            </a>
            <% if ("manager".equals(currentUser.getRole())) { %>
            <a class="nav-link" href="${pageContext.request.contextPath}/ClientServlet?action=list">
                <i class="fas fa-users"></i>
                <span>Clients</span>
            </a>
            <% } %>
            <a class="nav-link" href="${pageContext.request.contextPath}/profile.jsp">
                <i class="fas fa-user-cog"></i>
                <span>Profile</span>
            </a>
        </nav>
        
        <!-- User Info -->
        <div class="mt-auto p-3" style="position: absolute; bottom: 0; width: 100%;">
            <div class="d-flex align-items-center text-white">
                <div class="avatar-circle me-2" style="width: 40px; height: 40px; background: linear-gradient(135deg, #3498db, #2980b9); border-radius: 50%; display: flex; align-items: center; justify-content: center;">
                    <i class="fas fa-user"></i>
                </div>
                <div class="flex-grow-1" style="min-width: 0;">
                    <div class="fw-bold small text-truncate">
                        <span><%= currentUser.getUsername() %></span>
                    </div>
                    <div class="small text-muted text-truncate">
                        <span><%= currentUser.getRole() %></span>
                    </div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/AuthServlet?action=logout" 
               class="btn btn-outline-light btn-sm w-100 mt-2">
                <i class="fas fa-sign-out-alt me-1"></i>
                <span>Logout</span>
            </a>
        </div>
    </div>
    
    <!-- Main Content -->
    <div class="main-content" id="mainContent">
        <!-- Top Navbar -->
        <nav class="navbar navbar-expand-lg">
            <div class="container-fluid">
                <button class="btn btn-link text-white me-3" id="sidebarToggle">
                    <i class="fas fa-bars"></i>
                </button>
                <span class="navbar-brand mb-0 h1">
                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                </span>
                <div class="navbar-nav ms-auto">
                    <span class="nav-link text-white">
                        <i class="fas fa-calendar-alt me-2"></i><%= currentDate %>
                    </span>
                </div>
            </div>
        </nav>
        
        <!-- Page Content -->
        <div class="container-fluid p-4">
            <!-- Success Message -->
            <% if (successMessage != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    <%= successMessage %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>
            
            <!-- Welcome Message -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card stats-card">
                        <div class="card-body">
                            <div class="d-flex align-items-center">
                                <div class="stats-icon books me-3">
                                    <i class="fas fa-hand-wave"></i>
                                </div>
                                <div class="flex-grow-1">
                                    <h4 class="mb-1">Welcome back, <%= currentUser.getUsername() %>!</h4>
                                    <p class="text-muted mb-0">
                                        Here's what's happening with your Pahana Edu system today.
                                        <% if (currentUser.getCompanyName() != null) { %>
                                            Managing <strong><%= currentUser.getCompanyName() %></strong>
                                        <% } %>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Stats Cards -->
            <div class="row mb-4">
                <div class="col-lg-3 col-md-6 mb-3">
                    <div class="card stats-card h-100">
                        <div class="card-body d-flex align-items-center">
                            <div class="stats-icon books me-3">
                                <i class="fas fa-book"></i>
                            </div>
                            <div>
                                <h3 class="mb-0">0</h3>
                                <p class="text-muted mb-0 small">Total Books</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-3">
                    <div class="card stats-card h-100">
                        <div class="card-body d-flex align-items-center">
                            <div class="stats-icon invoices me-3">
                                <i class="fas fa-file-invoice"></i>
                            </div>
                            <div>
                                <h3 class="mb-0">0</h3>
                                <p class="text-muted mb-0 small">Total Invoices</p>
                            </div>
                        </div>
                    </div>
                </div>
                <% if ("manager".equals(currentUser.getRole())) { %>
                <div class="col-lg-3 col-md-6 mb-3">
                    <div class="card stats-card h-100">
                        <div class="card-body d-flex align-items-center">
                            <div class="stats-icon clients me-3">
                                <i class="fas fa-users"></i>
                            </div>
                            <div>
                                <h3 class="mb-0">0</h3>
                                <p class="text-muted mb-0 small">Active Clients</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-3">
                    <div class="card stats-card h-100">
                        <div class="card-body d-flex align-items-center">
                            <div class="stats-icon revenue me-3">
                                <i class="fas fa-dollar-sign"></i>
                            </div>
                            <div>
                                <h3 class="mb-0">$0</h3>
                                <p class="text-muted mb-0 small">Total Revenue</p>
                            </div>
                        </div>
                    </div>
                </div>
                <% } else { %>
                <div class="col-lg-6 col-md-6 mb-3">
                    <div class="card stats-card h-100">
                        <div class="card-body d-flex align-items-center">
                            <div class="stats-icon revenue me-3">
                                <i class="fas fa-clock"></i>
                            </div>
                            <div>
                                <h3 class="mb-0">Recent</h3>
                                <p class="text-muted mb-0 small">Last Login</p>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
            
            <!-- Quick Actions -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card chart-card">
                        <div class="card-header">
                            <h5 class="mb-0">
                                <i class="fas fa-bolt me-2"></i>Quick Actions
                            </h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-lg-4 col-md-6 mb-3">
                                    <a href="${pageContext.request.contextPath}/BookServlet?action=add" class="quick-action-btn books">
                                        <i class="fas fa-plus-circle me-2"></i>Add New Book
                                    </a>
                                </div>
                                <div class="col-lg-4 col-md-6 mb-3">
                                    <a href="${pageContext.request.contextPath}/BillingServlet?action=create" class="quick-action-btn invoices">
                                        <i class="fas fa-file-invoice me-2"></i>Create Invoice
                                    </a>
                                </div>
                                <% if ("manager".equals(currentUser.getRole())) { %>
                                <div class="col-lg-4 col-md-6 mb-3">
                                    <a href="${pageContext.request.contextPath}/ClientServlet?action=add" class="quick-action-btn clients">
                                        <i class="fas fa-user-plus me-2"></i>Add Client
                                    </a>
                                </div>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Recent Activity & Charts -->
            <div class="row">
                <div class="col-lg-6 mb-4">
                    <div class="card chart-card">
                        <div class="card-header">
                            <h5 class="mb-0">
                                <i class="fas fa-chart-line me-2"></i>Monthly Overview
                            </h5>
                        </div>
                        <div class="card-body">
                            <div class="text-center py-5">
                                <i class="fas fa-chart-bar fa-3x text-muted mb-3"></i>
                                <h6 class="text-muted">Charts will appear here</h6>
                                <p class="small text-muted">Once you start adding books and creating invoices</p>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="col-lg-6 mb-4">
                    <div class="card chart-card">
                        <div class="card-header">
                            <h5 class="mb-0">
                                <i class="fas fa-clock me-2"></i>Recent Activity
                            </h5>
                        </div>
                        <div class="card-body">
                            <div class="recent-activity">
                                <div class="activity-item">
                                    <div class="activity-icon login">
                                        <i class="fas fa-sign-in-alt"></i>
                                    </div>
                                    <div class="flex-grow-1">
                                        <div class="fw-bold">System Login</div>
                                        <div class="small text-muted">Welcome to Pahana Edu!</div>
                                        <div class="small text-muted">Just now</div>
                                    </div>
                                </div>
                                
                                <div class="text-center py-4">
                                    <i class="fas fa-history fa-2x text-muted mb-3"></i>
                                    <p class="text-muted mb-0">No recent activity</p>
                                    <p class="small text-muted">Activity will appear here as you use the system</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script>
        // Sidebar toggle functionality
        document.getElementById('sidebarToggle').addEventListener('click', function() {
            const sidebar = document.getElementById('sidebar');
            const mainContent = document.getElementById('mainContent');
            
            sidebar.classList.toggle('collapsed');
            mainContent.classList.toggle('expanded');
        });
        
        // Auto-dismiss alerts
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert-dismissible');
            alerts.forEach(function(alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
        
        // Add loading states for quick action buttons
        document.querySelectorAll('.quick-action-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const originalText = this.innerHTML;
                this.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Loading...';
                this.style.pointerEvents = 'none';
                
                // Re-enable after a short delay (in case of redirect issues)
                setTimeout(() => {
                    this.innerHTML = originalText;
                    this.style.pointerEvents = 'auto';
                }, 3000);
            });
        });
    </script>
</body>
</html>