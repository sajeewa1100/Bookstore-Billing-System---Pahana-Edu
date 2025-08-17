<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Pahana Edu Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>

<body>
    <%-- Include Sidebar --%>
    <jsp:include page="sidebar.jsp" flush="true" />

    <main class="main-content">
        <!-- Loading Overlay -->
        <div id="pageLoader" class="page-loader">
            <div class="loader-content">
                <div class="spinner-large"></div>
                <h3>Loading Dashboard...</h3>
                <p>Please wait while we prepare your data</p>
            </div>
        </div>

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

        <!-- Dashboard Header -->
        <div class="dashboard-header">
            <div class="dashboard-header-content">
                <div class="dashboard-title-section">
                    <h1><i class="fas fa-tachometer-alt"></i>Dashboard</h1>
                    <p>Welcome back! Here's what's happening with your bookstore today.</p>
                    <div class="dashboard-date">
                        <i class="fas fa-calendar-alt"></i>
                        <span id="currentDate"></span>
                    </div>
                </div>
                <div class="dashboard-action-section">
                    <div class="quick-actions">
                        <button class="btn-quick-action" onclick="location.href='BillingServlet'" title="Create New Bill">
                            <i class="fas fa-plus-circle"></i>
                            New Bill
                        </button>
                        <button class="btn-quick-action btn-secondary" onclick="location.href='BookServlet'" title="Manage Books">
                            <i class="fas fa-book"></i>
                            Books
                        </button>
                        <button class="btn-quick-action btn-tertiary" onclick="refreshDashboard()" title="Refresh Data">
                            <i class="fas fa-sync-alt" id="refreshIcon"></i>
                            Refresh
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Statistics Overview -->
        <div class="stats-section">
            <h2 class="section-title">
                <i class="fas fa-chart-bar"></i>
                Business Overview
            </h2>
            <div class="stats-container">
                <div class="stats-card stats-books">
                    <div class="stats-icon">
                        <i class="fas fa-book"></i>
                    </div>
                    <div class="stats-info">
                        <h3 id="totalBooks">${totalBooks != null ? totalBooks : 0}</h3>
                        <p>Total Books</p>
                        <small class="stats-change positive">
                            <i class="fas fa-arrow-up"></i>
                            +${booksAddedThisMonth != null ? booksAddedThisMonth : 0} this month
                        </small>
                    </div>
                </div>

                <div class="stats-card stats-clients">
                    <div class="stats-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stats-info">
                        <h3 id="totalClients">${totalClients != null ? totalClients : 0}</h3>
                        <p>Registered Clients</p>
                        <small class="stats-change positive">
                            <i class="fas fa-arrow-up"></i>
                            +${clientsAddedThisMonth != null ? clientsAddedThisMonth : 0} this month
                        </small>
                    </div>
                </div>

                <div class="stats-card stats-bills">
                    <div class="stats-icon">
                        <i class="fas fa-file-invoice"></i>
                    </div>
                    <div class="stats-info">
                        <h3 id="totalBills">${totalBillings != null ? totalBillings : 0}</h3>
                        <p>Total Bills</p>
                        <small class="stats-change">
                            <i class="fas fa-clock"></i>
                            ${pendingBills != null ? pendingBills : 0} pending
                        </small>
                    </div>
                </div>

                <div class="stats-card stats-revenue">
                    <div class="stats-icon">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div class="stats-info">
                        <h3 id="totalRevenue">Rs. <fmt:formatNumber value="${totalRevenue != null ? totalRevenue : 0}" type="number" minFractionDigits="2"/></h3>
                        <p>Total Revenue</p>
                        <small class="stats-change positive">
                            <i class="fas fa-chart-line"></i>
                            This month: Rs. <fmt:formatNumber value="${monthlyRevenue != null ? monthlyRevenue : 0}" type="number" minFractionDigits="2"/>
                        </small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Quick Actions Grid -->
        <div class="quick-actions-section">
            <h2 class="section-title">
                <i class="fas fa-bolt"></i>
                Quick Actions
            </h2>
            <div class="quick-actions-grid">
                <div class="action-card" onclick="location.href='BillingServlet'">
                    <div class="action-icon">
                        <i class="fas fa-receipt"></i>
                    </div>
                    <h4>Billing Management</h4>
                    <p>Create, view, and manage customer bills</p>
                    <div class="action-badge">
                        ${pendingBills != null ? pendingBills : 0} pending
                    </div>
                </div>

                <div class="action-card" onclick="location.href='BookServlet'">
                    <div class="action-icon">
                        <i class="fas fa-book"></i>
                    </div>
                    <h4>Book Inventory</h4>
                    <p>Add, edit, and track book inventory</p>
                    <div class="action-badge action-badge-warning">
                        ${lowStockBooks != null ? lowStockBooks : 0} low stock
                    </div>
                </div>

                <div class="action-card" onclick="location.href='ClientServlet'">
                    <div class="action-icon">
                        <i class="fas fa-user-friends"></i>
                    </div>
                    <h4>Client Management</h4>
                    <p>Manage customer information and profiles</p>
                    <div class="action-badge action-badge-info">
                        ${totalClients != null ? totalClients : 0} clients
                    </div>
                </div>

                <div class="action-card" onclick="showReportsModal()">
                    <div class="action-icon">
                        <i class="fas fa-chart-line"></i>
                    </div>
                    <h4>Reports & Analytics</h4>
                    <p>View detailed business reports</p>
                    <div class="action-badge action-badge-success">
                        Generate
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Activities -->
        <div class="recent-activities-section">
            <div class="activities-container">
                <!-- Recent Bills -->
                <div class="activity-panel">
                    <div class="panel-header">
                        <h3><i class="fas fa-clock"></i>Recent Bills</h3>
                        <a href="BillingServlet" class="view-all-link">View All</a>
                    </div>
                    <div class="panel-content">
                        <c:choose>
                            <c:when test="${not empty recentBills}">
                                <c:forEach var="bill" items="${recentBills}" varStatus="status">
                                    <c:if test="${status.index < 5}">
                                        <div class="activity-item">
                                            <div class="activity-icon">
                                                <i class="fas fa-file-invoice"></i>
                                            </div>
                                            <div class="activity-info">
                                                <h6>Bill #${bill.billNumber}</h6>
                                                <p>${bill.clientName}</p>
                                                <small class="activity-time">${bill.formattedBillDateShort}</small>
                                            </div>
                                            <div class="activity-meta">
                                                <span class="status-badge status-${fn:toLowerCase(bill.status)}">${bill.status}</span>
                                                <strong>Rs. <fmt:formatNumber value="${bill.totalAmount}" type="number" minFractionDigits="2"/></strong>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-file-invoice"></i>
                                    <p>No recent bills found</p>
                                    <button class="btn-create" onclick="location.href='BillingServlet'">
                                        Create First Bill
                                    </button>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Low Stock Alert -->
                <div class="activity-panel">
                    <div class="panel-header">
                        <h3><i class="fas fa-exclamation-triangle"></i>Low Stock Alert</h3>
                        <a href="BookServlet?filter=lowStock" class="view-all-link">Manage Inventory</a>
                    </div>
                    <div class="panel-content">
                        <c:choose>
                            <c:when test="${not empty lowStockBooksList}">
                                <c:forEach var="book" items="${lowStockBooksList}" varStatus="status">
                                    <c:if test="${status.index < 5}">
                                        <div class="activity-item low-stock-item">
                                            <div class="activity-icon warning">
                                                <i class="fas fa-book"></i>
                                            </div>
                                            <div class="activity-info">
                                                <h6>${book.title}</h6>
                                                <p>by ${book.author}</p>
                                                <small class="activity-time">ISBN: ${book.isbn}</small>
                                            </div>
                                            <div class="activity-meta">
                                                <span class="stock-badge ${book.quantity <= 5 ? 'critical' : 'warning'}">
                                                    ${book.quantity} left
                                                </span>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-check-circle"></i>
                                    <p>All books are well stocked</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Reports Modal -->
    <div id="reportsModal" class="modal">
        <div class="modal-content modal-lg">
            <div class="modal-header">
                <h3><i class="fas fa-chart-bar"></i>Business Reports</h3>
                <span class="close" data-bs-dismiss="modal">&times;</span>
            </div>
            <div class="modal-body">
                <div class="reports-grid">
                    <div class="report-item" onclick="generateReport('sales')">
                        <div class="report-icon">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <h5>Sales Report</h5>
                        <p>View detailed sales analytics and trends</p>
                    </div>
                    <div class="report-item" onclick="generateReport('inventory')">
                        <div class="report-icon">
                            <i class="fas fa-boxes"></i>
                        </div>
                        <h5>Inventory Report</h5>
                        <p>Check current stock levels and movements</p>
                    </div>
                    <div class="report-item" onclick="generateReport('clients')">
                        <div class="report-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <h5>Client Report</h5>
                        <p>Analyze client activity and statistics</p>
                    </div>
                    <div class="report-item" onclick="generateReport('financial')">
                        <div class="report-icon">
                            <i class="fas fa-money-bill-wave"></i>
                        </div>
                        <h5>Financial Summary</h5>
                        <p>Overview of revenue and financial metrics</p>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>

    <script>
        // Page Loading Animation
        document.addEventListener('DOMContentLoaded', function() {
            // Simulate loading time for smooth experience
            setTimeout(function() {
                const loader = document.getElementById('pageLoader');
                loader.classList.add('fade-out');
                setTimeout(() => {
                    loader.style.display = 'none';
                    // Animate cards in sequence
                    animateCardsIn();
                }, 300);
            }, 800);

            // Set current date
            updateCurrentDate();
            
            // Auto-refresh data every 5 minutes
            setInterval(refreshStats, 300000);
            
            // Auto-dismiss alerts
            setTimeout(dismissAlerts, 5000);
        });

        // Update current date display
        function updateCurrentDate() {
            const now = new Date();
            const options = { 
                weekday: 'long', 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric' 
            };
            document.getElementById('currentDate').textContent = now.toLocaleDateString('en-US', options);
        }

        // Animate cards entrance
        function animateCardsIn() {
            const cards = document.querySelectorAll('.stats-card, .action-card, .activity-panel');
            cards.forEach((card, index) => {
                setTimeout(() => {
                    card.classList.add('animate-in');
                }, index * 100);
            });
        }

        // Refresh dashboard data
        function refreshDashboard() {
            const refreshIcon = document.getElementById('refreshIcon');
            refreshIcon.classList.add('fa-spin');
            
            // Show mini loader for stats cards
            const statsCards = document.querySelectorAll('.stats-card h3');
            statsCards.forEach(stat => {
                stat.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
            });

            // Simulate API call
            setTimeout(() => {
                location.reload();
            }, 1500);
        }

        // Refresh stats only (AJAX call)
        function refreshStats() {
            fetch('DashboardServlet?action=refreshStats')
                .then(response => response.json())
                .then(data => {
                    // Update stats without full page reload
                    if (data.totalBooks) document.getElementById('totalBooks').textContent = data.totalBooks;
                    if (data.totalClients) document.getElementById('totalClients').textContent = data.totalClients;
                    if (data.totalBills) document.getElementById('totalBills').textContent = data.totalBills;
                    if (data.totalRevenue) {
                        document.getElementById('totalRevenue').textContent = 
                            'Rs. ' + parseFloat(data.totalRevenue).toLocaleString('en-US', {minimumFractionDigits: 2});
                    }
                })
                .catch(error => console.log('Stats refresh failed:', error));
        }

        // Show reports modal
        function showReportsModal() {
            document.getElementById('reportsModal').style.display = 'block';
        }

        // Generate report
        function generateReport(type) {
            // Show loading state
            const reportItems = document.querySelectorAll('.report-item');
            reportItems.forEach(item => {
                item.style.opacity = '0.6';
                item.style.pointerEvents = 'none';
            });

            // Add loading spinner to clicked item
            event.currentTarget.innerHTML += '<div class="mini-loader"><i class="fas fa-spinner fa-spin"></i></div>';

            // Simulate report generation
            setTimeout(() => {
                // Close modal and redirect to report page
                document.getElementById('reportsModal').style.display = 'none';
                window.open(`ReportServlet?type=${type}`, '_blank');
                
                // Reset items
                reportItems.forEach(item => {
                    item.style.opacity = '1';
                    item.style.pointerEvents = 'auto';
                    const loader = item.querySelector('.mini-loader');
                    if (loader) loader.remove();
                });
            }, 2000);
        }

        // Auto-dismiss alerts
        function dismissAlerts() {
            const alerts = document.querySelectorAll('.alert-dismissible');
            alerts.forEach(alert => {
                alert.style.transition = 'opacity 0.3s, transform 0.3s';
                alert.style.opacity = '0';
                alert.style.transform = 'translateY(-10px)';
                setTimeout(() => alert.style.display = 'none', 300);
            });
        }

        // Manual alert dismiss
        document.querySelectorAll('.btn-close').forEach(btn => {
            btn.onclick = function() {
                const alert = this.closest('.alert-dismissible');
                alert.style.transition = 'opacity 0.3s, transform 0.3s';
                alert.style.opacity = '0';
                alert.style.transform = 'translateY(-10px)';
                setTimeout(() => alert.style.display = 'none', 300);
            }
        });

        // Modal functionality
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

        // Keyboard shortcuts
        document.addEventListener('keydown', function(e) {
            // Ctrl/Cmd + R for refresh
            if ((e.ctrlKey || e.metaKey) && e.key === 'r') {
                e.preventDefault();
                refreshDashboard();
            }
            
            // Ctrl/Cmd + B for new bill
            if ((e.ctrlKey || e.metaKey) && e.key === 'b') {
                e.preventDefault();
                location.href = 'BillingServlet';
            }
        });

        // Add hover effects for interactive elements
        document.querySelectorAll('.stats-card, .action-card').forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-2px)';
            });
            
            card.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
            });
        });
    </script>
</body>
</html>