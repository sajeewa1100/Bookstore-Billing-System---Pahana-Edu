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
                
             
                
                <!-- Manager+ Only (Admin and Manager) -->
                <c:if test="${sessionScope.user.role == 'admin' || sessionScope.user.role == 'manager'}">
                    <li class="${fn:contains(pageContext.request.requestURI, 'ManagerServlet') ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/ManagerServlet?action=dashboard">
                            <i class="fas fa-chart-line icon"></i> Manager Dashboard
                        </a>
                    </li>
                    
                 <!-- Fixed: Use /create-invoice instead of BillingServlet?action=createInvoice -->
                    <li class="${fn:contains(pageContext.request.requestURI, 'create-invoice') ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/create-invoice">
                            <i class="fas fa-plus-square icon"></i> Create Invoice
                        </a>
                    </li>
                    
                    <!-- Fixed: Use /billing instead of /BillingServlet -->
                    <li class="${fn:contains(pageContext.request.requestURI, 'billing') && (param.action == 'invoices' || param.action == 'manageInvoices') ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/billing?action=invoices">
                            <i class="fas fa-file-invoice icon"></i> All Invoices
                        </a>
                    </li>
                </c:if>
                
                <!-- Staff+ Only (Staff, Manager, Admin) -->
                <c:if test="${sessionScope.user.role == 'staff' || sessionScope.user.role == 'manager' || sessionScope.user.role == 'admin'}">
                    
                    <!-- Staff Dashboard (Staff see this as main dashboard) -->
                    <c:if test="${sessionScope.user.role == 'staff'}">
                        <li class="${fn:contains(pageContext.request.requestURI, 'billing') && (param.action == 'dashboard' || param.action == null) ? 'active' : ''}">
                            <a href="${pageContext.request.contextPath}/billing?action=dashboard">
                                <i class="fas fa-home icon"></i> Dashboard
                            </a>
                        </li>
                    </c:if>
                    
                    
                    
                    <!-- Staff can only see their own invoices, managers can see all -->
                    <c:if test="${sessionScope.user.role == 'staff'}">
                        <li class="${fn:contains(pageContext.request.requestURI, 'billing') && param.action == 'myInvoices' ? 'active' : ''}">
                            <a href="${pageContext.request.contextPath}/billing?action=myInvoices">
                                <i class="fas fa-file-alt icon"></i> My Invoices
                            </a>
                        </li>
                    </c:if>
                    
                    <!-- Fixed: Use correct servlet mappings -->
                    <li class="${fn:contains(pageContext.request.requestURI, 'clients') ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/clients">
                            <i class="fas fa-address-book icon"></i> Clients
                        </a>
                    </li>
                    
                    <li class="${fn:contains(pageContext.request.requestURI, 'books') ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/books">
                            <i class="fas fa-book icon"></i> Books
                        </a>
                    </li>
                </c:if>
                
                <br>
                <hr>
                <!-- Help & Support - Available to All Users -->
				<li
					class="${fn:contains(pageContext.request.requestURI, 'help') ? 'active' : ''}">
					<a href="${pageContext.request.contextPath}/help"> <i
						class=""></i> Help & Support
				</a>
				</li>

			</ul>                      
            
            <!-- User Info -->
            <div class="sidebar-header">
                <c:choose>
                    <c:when test="${not empty sessionScope.user.fullName}">
                        <h4>${sessionScope.user.fullName}</h4>
                    </c:when>
                    <c:when test="${not empty sessionScope.user.companyName}">
                        <h4>${sessionScope.user.companyName}</h4>
                    </c:when>
                    <c:otherwise>
                        <h4>${sessionScope.user.username}</h4>
                    </c:otherwise>
                </c:choose>
                <small>
                    <i class="fas fa-user-circle"></i> 
                    <c:choose>
                        <c:when test="${sessionScope.user.role == 'admin'}">Administrator</c:when>
                        <c:when test="${sessionScope.user.role == 'manager'}">Manager</c:when>
                        <c:when test="${sessionScope.user.role == 'staff'}">Staff Member</c:when>
                        <c:otherwise>${sessionScope.user.role}</c:otherwise>
                    </c:choose>
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