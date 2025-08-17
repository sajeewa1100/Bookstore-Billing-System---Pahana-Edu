<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<aside class="sidebar">
	<div class="sidebar-top">
		<div class="sidebar-box">
			<img src="assets/Logo.png" alt="Logo" class="sidebar-logo" /> <span
				class="sidebar-title">Pahana Edu</span>
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
				<li
					class="${fn:endsWith(request.getRequestURI(), '/BookServlet') || 'dashboard' == currentPage ? 'active' : ''}">
					<a href="<c:url value='/BookServlet?action=dashboard' />"><i
						class="fas fa-chart-line icon"></i> Dashboard</a>
				</li>

				<!-- Clients Link -->
				<li
					class="${fn:endsWith(request.getRequestURI(), '/BookServlet') || 'clients' == currentPage ? 'active' : ''}">
					<a href="<c:url value='/ClientServlet?action=clients' />"><i
						class="fas fa-users icon"></i> Clients</a>
				</li>

				<!-- Books Link -->
				<li
					class="${fn:endsWith(request.getRequestURI(), '/BookServlet') || 'books' == currentPage ? 'active' : ''}">
					<a href="<c:url value='/BookServlet?action=books' />"><i
						class="fas fa-book icon"></i> Books</a>
				</li>

				<!-- Invoices Link -->
				<li
					class="${fn:endsWith(request.getRequestURI(), '/BookServlet') || 'invoices' == currentPage ? 'active' : ''}">
					<a href="<c:url value='/BillingServlet?action=invoices' />"><i
						class="fas fa-file-invoice icon"></i> Invoices</a>
				</li>

				<!-- New Invoice Link -->
				<li
					class="${fn:endsWith(request.getRequestURI(), '/BookServlet') || 'invoice_form' == currentPage ? 'active' : ''}">
					<a href="<c:url value='/BillingServlet?action=newInvoice' />"><i
						class="fas fa-plus-square icon"></i> New Invoice</a>
				</li>

				<!-- Reports Link -->
				<li
					class="${fn:endsWith(request.getRequestURI(), '/BookServlet') || 'report' == currentPage ? 'active' : ''}">
					<a href="<c:url value='/BillingServlet?action=report' />"><i
						class="fas fa-chart-bar icon"></i> Reports</a>
				</li>
			</ul>

			<!-- User Info -->
			<div class="sidebar-header">
				<h4>${sessionScope.user.companyName}</h4>
				<small>${sessionScope.user.role}</small>
			</div>

			<!-- Logout Section -->
			<ul class="logout-section">
				<li><a href="<c:url value='/BookServlet?action=logout' />"
					class="logout-link"> <i class="fas fa-sign-out-alt icon"></i>
						Logout
				</a></li>
			</ul>
		</div>
	</nav>
</aside>
