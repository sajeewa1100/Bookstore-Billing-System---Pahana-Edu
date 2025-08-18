<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<aside class="sidebar">
    <div class="sidebar-top">
        <div class="sidebar-box">
            <img src="${pageContext.request.contextPath}/assets/Logo.png" alt="Logo" class="sidebar-logo" /> 
            <span class="sidebar-title">Pahana Edu</span>
        </div>
    </div>

    <div class="menu-list">
        <hr />
    </div>

    <!-- Navigation Menu -->
    <nav class="sidebar-menu">
        <div class="menu-container">
            <ul class="menu-list">
                <!-- Dashboard Link -->
                <li class="${fn:contains(pageContext.request.requestURI, 'dashboard') || param.action == 'dashboard' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/DashboardServlet?action=dashboard">
                        <i class="fas fa-chart-line icon"></i> Dashboard
                    </a>
                </li>

                <!-- Clients Link -->
                <li class="${fn:contains(pageContext.request.requestURI, 'ClientServlet') || param.action == 'clients' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/ClientServlet?action=clients">
                        <i class="fas fa-users icon"></i> Clients
                    </a>
                </li>

                <!-- Books Link -->
                <li class="${fn:contains(pageContext.request.requestURI, 'BookServlet') || param.action == 'books' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/BookServlet?action=books">
                        <i class="fas fa-book icon"></i> Books
                    </a>
                </li>

                <!-- Invoices Link -->
                <li class="${fn:contains(pageContext.request.requestURI, 'BillingServlet') && param.action == 'invoices' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/BillingServlet?action=invoices">
                        <i class="fas fa-file-invoice icon"></i> Invoices
                    </a>
                </li>

                <!-- New Invoice Link -->
                <li class="${fn:contains(pageContext.request.requestURI, 'BillingServlet') && param.action == 'newInvoice' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/BillingServlet?action=newInvoice">
                        <i class="fas fa-plus-square icon"></i> New Invoice
                    </a>
                </li>

                <!-- Reports Link -->
                <li class="${fn:contains(pageContext.request.requestURI, 'BillingServlet') && param.action == 'report' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/BillingServlet?action=report">
                        <i class="fas fa-chart-bar icon"></i> Reports
                    </a>
                </li>
            </ul>

            <!-- User Info -->
            <div class="sidebar-header">
                <c:choose>
                    <c:when test="${not empty sessionScope.user.companyName}">
                        <h4>${sessionScope.user.companyName}</h4>
                    </c:when>
                    <c:otherwise>
                        <h4>${sessionScope.user.username}</h4>
                    </c:otherwise>
                </c:choose>
                <small>
                    <i class="fas fa-user-circle"></i> ${sessionScope.user.role}
                </small>
            </div>

            <!-- Logout Section -->
            <ul class="logout-section">
                <li>
                    <a href="${pageContext.request.contextPath}/AuthServlet?action=logout" class="logout-link"
                       onclick="return confirm('Are you sure you want to logout?')">
                        <i class="fas fa-sign-out-alt icon"></i> Logout
                    </a>
                </li>
            </ul>
        </div>
    </nav>
</aside>



<script>
    // Mobile menu toggle
    document.addEventListener('DOMContentLoaded', function() {
        // Add mobile menu toggle button
        const toggleBtn = document.createElement('button');
        toggleBtn.classList.add('mobile-menu-toggle');
        toggleBtn.innerHTML = '<i class="fas fa-bars"></i>';
        document.body.appendChild(toggleBtn);

        toggleBtn.addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('open');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', function(e) {
            if (window.innerWidth <= 768) {
                const sidebar = document.querySelector('.sidebar');
                const toggle = document.querySelector('.mobile-menu-toggle');
                
                if (!sidebar.contains(e.target) && !toggle.contains(e.target)) {
                    sidebar.classList.remove('open');
                }
            }
        });
    });
</script>