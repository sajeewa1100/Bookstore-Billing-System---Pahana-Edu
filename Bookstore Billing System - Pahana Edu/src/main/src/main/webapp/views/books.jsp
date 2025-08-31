<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Book Management - Pahana Bookstore</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
	
<style>


:root {
    --primary-color: #D86C36;
    --primary-dark: #C4552C;
    --primary-darker: #A63F22;
    --accent-color: #f2a23f;
    --background-light: #F2E7DC;
    --book-blue: #3b82f6;
    --book-green: #10b981;
    --book-purple: #8b5cf6;
}

/* Book Management Header */
.client-header {
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
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

.btn-new-client {
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

.btn-new-client:hover {
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

.stat-icon.total-clients {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
}

.stat-icon.platinum {
    background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
}

.stat-icon.silver {
    background: linear-gradient(135deg, #f59e0b, #d97706);
}

.stat-icon.gold {
    background: linear-gradient(135deg, #ef4444, #dc2626);
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

/* Book Form Styles */
.client-form {
    background: white;
    border-radius: 15px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
    padding: 30px;
    margin-top: 20px;
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

.form-group input {
    padding: 12px 15px;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    font-size: 1rem;
    transition: all 0.3s ease;
}

.form-group input:focus {
    outline: none;
    border-color: var(--primary-color);
    box-shadow: 0 0 0 3px rgba(216, 108, 54, 0.1);
}

.form-group input[type="number"] {
    text-align: right;
}

.form-group input[name="price"], 
.form-group input[name="costPrice"] {
    font-weight: 500;
    color: var(--book-green);
}

.form-group input[name="isbn"] {
    font-family: 'Courier New', monospace;
    letter-spacing: 0.5px;
}

.field-help {
    font-size: 0.85rem;
    color: #6b7280;
    margin-top: 4px;
}

.required {
    color: #ef4444;
}

/* Profit Margin Display */
#profitMargin {
    padding: 12px 15px;
    background-color: #f0fdf4;
    border: 2px solid #bbf7d0;
    border-radius: 8px;
    font-weight: 600;
    font-size: 16px;
    color: var(--book-green);
    transition: all 0.2s ease;
}

#profitMargin.negative {
    background-color: #fef2f2;
    border-color: #fecaca;
    color: #dc2626;
}

.form-footer {
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    padding-top: 20px;
    border-top: 1px solid #e5e7eb;
    margin-top: 20px;
}

/* Book Profile View */
.client-profile {
    background: white;
    border-radius: 15px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
    overflow: hidden;
}

.profile-header {
    background: linear-gradient(135deg, var(--background-light), #e2e8f0);
    padding: 40px;
    text-align: center;
    border-bottom: 1px solid #e5e7eb;
}

.profile-avatar {
    width: 80px;
    height: 80px;
    background: var(--book-blue);
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2rem;
    margin: 0 auto 20px;
}

.profile-name {
    font-size: 1.8rem;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 10px;
}

.profile-account {
    color: #6b7280;
    font-size: 1.1rem;
}

.profile-content {
    padding: 30px;
}

.profile-section {
    margin-bottom: 30px;
}

.profile-section h5 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #1f2937;
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.profile-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
}

.profile-item {
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.profile-item label {
    font-size: 0.9rem;
    color: #6b7280;
    font-weight: 500;
}

.profile-item span {
    font-size: 1rem;
    color: #1f2937;
    font-weight: 600;
}

.loyalty-points {
    color: var(--book-green) !important;
    font-weight: 700 !important;
}

/* Book List Section */
.clients-section {
    background: white;
    border-radius: 15px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
    padding: 30px;
}

/* Search Container */
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
    min-width: 140px;
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

.search-input[placeholder*="title"] {
    border-left: 4px solid var(--book-blue);
}

.search-input[placeholder*="author"] {
    border-left: 4px solid var(--book-purple);
}

.search-input[placeholder*="ISBN"] {
    border-left: 4px solid #f59e0b;
}

/* Book Cards */
.clients-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
}

.client-card {
    background: #f8fafc;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.3s ease;
}

.client-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    border-color: var(--primary-color);
}

.client-header-card {
    background: linear-gradient(135deg, var(--background-light), #e2e8f0);
    padding: 20px;
    border-bottom: 1px solid #e5e7eb;
}

.client-account-number {
    font-size: 0.9rem;
    color: #6b7280;
    font-family: 'Courier New', monospace;
    background: rgba(255, 255, 255, 0.7);
    padding: 4px 8px;
    border-radius: 4px;
    display: inline-block;
    margin-bottom: 8px;
}

.client-name {
    font-size: 1.2rem;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 5px;
    line-height: 1.3;
}

.client-body {
    padding: 20px;
}

.client-contact {
    margin-bottom: 15px;
}

.contact-item {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    color: #4b5563;
}

.contact-item i {
    width: 16px;
    color: var(--primary-color);
}

.client-loyalty {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    align-items: center;
}

.badge {
    background: var(--primary-color);
    color: white;
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 0.85rem;
    font-weight: 600;
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

.client-meta {
    display: flex;
    justify-content: space-between;
    font-size: 0.9rem;
    color: #6b7280;
    padding-top: 15px;
    border-top: 1px solid #f3f4f6;
    margin-top: 15px;
}

.client-actions {
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

.btn-view {
       background: #d1fae5;
   	   color: var(--book-green);
}

.btn-edit {
    background: #d1fae5;
    color: var(--book-green);
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
    color: var(--book-blue);
}

.empty-state h3 {
    font-size: 1.3rem;
    margin-bottom: 10px;
    color: #4b5563;
}

.empty-state p {
    margin-bottom: 20px;
    max-width: 400px;
    margin-left: auto;
    margin-right: auto;
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
    color: var(--book-green);
    border: 1px solid #a7f3d0;
}

.alert-error {
    background: #fee2e2;
    color: #991b1b;
    border: 1px solid #fca5a5;
}

/* Buttons */
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
    background: #6b7280;
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
    background: #4b5563;
    color: white;
    text-decoration: none;
}

/* Tier Badges */
.tier-badge {
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.tier-silver {
    background: #f3f4f6;
    color: #6b7280;
}

.tier-gold {
    background: #fef3c7;
    color: #d97706;
}

.tier-platinum {
    background: #dbeafe;
    color: var(--book-blue);
}

/* Responsive Design */
@media (max-width: 768px) {
    .header-content {
        flex-direction: column;
        gap: 20px;
        text-align: center;
    }
    
    .stats-container {
        grid-template-columns: 1fr;
    }
    
    .form-row {
        grid-template-columns: 1fr;
        gap: 15px;
    }
    
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
    
    .clients-grid {
        grid-template-columns: 1fr;
    }
    
    .profile-grid {
        grid-template-columns: 1fr;
    }
    
    .form-footer {
        flex-direction: column;
    }
    
    .client-loyalty {
        flex-direction: column;
        align-items: flex-start;
    }
}

@media (max-width: 480px) {
    .header-title h1 {
        font-size: 2rem;
    }
    
    .stat-card {
        padding: 20px;
    }
    
    .stat-info h3 {
        font-size: 1.5rem;
    }
    
    .client-form,
    .clients-section,
    .client-profile {
        margin: 0 -15px;
        border-radius: 0;
    }
}
</style>
</head>

<body>

	<%-- Include Sidebar --%>
	<jsp:include page="sidebar.jsp" flush="true" />

	<main class="main-content">
		<div class="client-header">
			<div class="header-content">
				<div class="header-title">
					<h1>
						<i class="fas fa-book"></i> Book Management
					</h1>
					<p>Manage book inventory, pricing, and information</p>
				</div>
				<a href="?action=new" class="btn-new-client"> <i
					class="fas fa-plus"></i> New Book
				</a>
			</div>
		</div>

		<c:if test="${not empty successMessage}">
			<div class="alert alert-success">
				<i class="fas fa-check-circle"></i> ${successMessage}
			</div>
		</c:if>

		<c:if test="${not empty errorMessage}">
			<div class="alert alert-error">
				<i class="fas fa-exclamation-triangle"></i> ${errorMessage}
			</div>
		</c:if>

		<div class="stats-container">
			<div class="stat-card">
				<div class="stat-icon total-clients">
					<i class="fas fa-book"></i>
				</div>
				<div class="stat-info">
					<h3>${totalBooks > 0 ? totalBooks : 0}</h3>
					<p>Total Books</p>
				</div>
			</div>

			<div class="stat-card">
				<div class="stat-icon platinum">
					<i class="fas fa-dollar-sign"></i>
				</div>
				<div class="stat-info">
					<h3>
						<fmt:formatNumber value="${totalInventoryValue}" type="currency"
							currencySymbol="Rs. " maxFractionDigits="0" />
					</h3>
					<p>Total Cost Value</p>
				</div>
			</div>
		</div>

		<c:choose>
			<c:when test="${param.action == 'new' || param.action == 'edit'}">
				<div class="client-form">
					<div class="section-header">
						<h2 class="section-title">
							<c:choose>
								<c:when test="${param.action == 'new'}">
									<i class="fas fa-plus-circle"></i> Add New Book
                            </c:when>
								<c:otherwise>
									<i class="fas fa-edit"></i> Edit Book Information
                            </c:otherwise>
							</c:choose>
						</h2>
					</div>

					<form id="bookForm" method="POST" action="books">
						<input type="hidden" name="action"
							value="${param.action == 'new' ? 'create' : 'update'}">
						<c:if test="${param.action == 'edit'}">
							<input type="hidden" name="id" value="${book.id}">
						</c:if>

						<div class="form-section">
							<h4>
								<i class="fas fa-info-circle"></i> Book Information
							</h4>

							<div class="form-row">
								<div class="form-group">
									<label for="title">Book Title <span class="required">*</span></label>
									<input type="text" id="title" name="title"
										value="${book != null ? book.title : ''}" required
										maxlength="255" placeholder="Enter book title">
									<div class="field-help">Required field</div>
								</div>
								<div class="form-group">
									<label for="author">Author <span class="required">*</span></label>
									<input type="text" id="author" name="author"
										value="${book != null ? book.author : ''}" required
										maxlength="255" placeholder="Enter author name">
									<div class="field-help">Required field</div>
								</div>
							</div>

							<div class="form-row">
								<div class="form-group">
									<label for="isbn">ISBN <span class="required">*</span></label>
									<input type="text" id="isbn" name="isbn"
										value="${book != null ? book.isbn : ''}" required
										maxlength="20" placeholder="978-0123456789">
									<div class="field-help">ISBN-10 or ISBN-13 format</div>
								</div>
								<div class="form-group"></div>
							</div>
						</div>

						<div class="form-section">
							<h4>
								<i class="fas fa-dollar-sign"></i> Pricing Information
							</h4>

							<div class="form-row">
								<div class="form-group">
									<label for="price">Selling Price <span class="required">*</span></label>
									<input type="number" id="price" name="price" step="0.01"
										min="0" value="${book != null ? book.price : ''}" required
										placeholder="0.00">
									<div class="field-help">Customer selling price</div>
								</div>
								<div class="form-group">
									<label for="costPrice">Cost Price <span
										class="required">*</span></label> <input type="number" id="costPrice"
										name="costPrice" step="0.01" min="0"
										value="${book != null ? book.costPrice : ''}" required
										placeholder="0.00">
									<div class="field-help">Purchase/wholesale cost</div>
								</div>
							</div>

							<c:if test="${param.action == 'edit'}">
								<div class="form-row">
									<div class="form-group">
										<label>Profit Margin</label>
										<div
											style="padding: 10px 0; font-weight: bold; color: #059669;"
											id="profitMargin">
											<c:if
												test="${book != null && book.price != null && book.costPrice != null}">
												<c:set var="margin"
													value="${((book.price - book.costPrice) / book.costPrice) * 100}" />
												<fmt:formatNumber value="${margin}" maxFractionDigits="1" />%
                                        </c:if>
										</div>
										<div class="field-help">Calculated automatically</div>
									</div>
									<div class="form-group"></div>
								</div>
							</c:if>
						</div>

						<div class="form-footer">
							<a href="books" class="btn-secondary">Cancel</a>
							<button type="submit" class="btn-primary" id="submitBtn">
								<i class="fas fa-save"></i>
								<c:choose>
									<c:when test="${param.action == 'new'}">Add Book</c:when>
									<c:otherwise>Update Book</c:otherwise>
								</c:choose>
							</button>
						</div>
					</form>
				</div>
			</c:when>

			<c:when test="${param.action == 'view'}">
				<div class="client-profile">
					<div class="profile-header">
						<div class="profile-avatar">
							<i class="fas fa-book"></i>
						</div>
						<div class="profile-name">${book.title}</div>
						<div class="profile-account">by ${book.author}</div>
					</div>

					<div class="profile-content">
						<div class="profile-section">
							<h5>
								<i class="fas fa-info-circle"></i> Book Details
							</h5>
							<div class="profile-grid">
								<div class="profile-item">
									<label>ISBN</label> <span>${book.isbn}</span>
								</div>
								<div class="profile-item">
									<label>Book ID</label> <span>#${book.id}</span>
								</div>
							</div>
						</div>

						<div class="profile-section">
							<h5>
								<i class="fas fa-dollar-sign"></i> Pricing Information
							</h5>
							<div class="profile-grid">
								<div class="profile-item">
									<label>Selling Price</label> <span class="loyalty-points"><fmt:formatNumber
											value="${book.price}" type="currency" currencySymbol="$" /></span>
								</div>
								<div class="profile-item">
									<label>Cost Price</label> <span><fmt:formatNumber
											value="${book.costPrice}" type="currency" currencySymbol="$" /></span>
								</div>
								<div class="profile-item">
									<label>Profit Margin</label>
									<c:set var="margin"
										value="${((book.price - book.costPrice) / book.costPrice) * 100}" />
									<span class="loyalty-points"><fmt:formatNumber
											value="${margin}" maxFractionDigits="1" />%</span>
								</div>
							</div>
						</div>

						<div class="form-footer">
							<a href="books" class="btn-secondary">Back to Books</a> <a
								href="?action=edit&id=${book.id}" class="btn-primary"> <i
								class="fas fa-edit"></i> Edit Book
							</a>
						</div>
					</div>
				</div>
			</c:when>

			<c:otherwise>
				<div class="clients-section">
					<div class="section-header">
						<h2 class="section-title">Book Library</h2>
						<div class="search-container">
							<form action="books" method="get">
								<select name="searchType" class="search-input">
									<option value="isbn"
										${empty param.searchType || param.searchType == 'isbn' ? 'selected' : ''}>ISBN
										(Default)</option>
									<option value="title"
										${param.searchType == 'title' ? 'selected' : ''}>Title</option>
									<option value="author"
										${param.searchType == 'author' ? 'selected' : ''}>Author</option>
								</select> <input type="text" name="search" class="search-input"
									placeholder="${param.searchType == 'title' ? 'Search by title...' : (param.searchType == 'author' ? 'Search by author...' : 'Search by ISBN...')}"
									value="${param.search}">
								<button type="submit" class="btn-primary">
									<i class="fas fa-search"></i>
								</button>
							</form>
						</div>
					</div>

					<c:choose>
						<c:when test="${not empty books}">
							<div class="clients-grid">
								<c:forEach var="book" items="${books}">
									<div class="client-card">
										<div class="client-header-card">
											<div class="client-account-number">ISBN: ${book.isbn}</div>
											<div class="client-name">${book.title}</div>
										</div>

										<div class="client-body">
											<div class="client-contact">
												<div class="contact-item">
													<i class="fas fa-user"></i> <span>${book.author}</span>
												</div>
												<div class="contact-item">
													<i class="fas fa-dollar-sign"></i> <span><fmt:formatNumber
															value="${book.price}" type="currency" currencySymbol="Rs." /></span>
												</div>
											</div>

											<div class="client-loyalty">
												<c:set var="margin"
													value="${((book.price - book.costPrice) / book.costPrice) * 100}" />
												<span class="badge"> <i class="fas fa-chart-line"></i>
													<fmt:formatNumber value="${margin}" maxFractionDigits="0" />%
													margin
												</span>
											</div>

											<div class="client-meta">
												<span>ID: ${book.id}</span>
											</div>
										</div>

										<div class="client-actions">
											<a href="?action=view&id=${book.id}"
												class="btn-action btn-view" title="View Book"> <i
												class="fas fa-eye"></i>
											</a> <a href="?action=edit&id=${book.id}"
												class="btn-action btn-edit" title="Edit Book"> <i
												class="fas fa-edit"></i>
											</a>
											<form method="POST" action="books" style="display: inline;"
												onsubmit="return confirm('Are you sure you want to delete ${book.title}? This cannot be undone.')">
												<input type="hidden" name="action" value="delete"> <input
													type="hidden" name="id" value="${book.id}">
												<button type="submit" class="btn-action btn-delete"
													title="Delete Book">
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
								<i class="fas fa-book"></i>
								<h3>No Books Found</h3>
								<p>
									<c:choose>
										<c:when test="${not empty param.search}">
                                        No books match your search criteria. Try adjusting your search terms.
                                    </c:when>
										<c:otherwise>
                                        Start by adding your first book to manage book information and pricing.
                                    </c:otherwise>
									</c:choose>
								</p>
								<a href="?action=new" class="btn-primary"> <i
									class="fas fa-plus"></i> Add First Book
								</a>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</c:otherwise>
		</c:choose>
	</main>

	<script>
document.addEventListener('DOMContentLoaded', function() {
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.3s';
            setTimeout(function() {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 300);
        });
    }, 5000);
    
    // Calculate profit margin dynamically in edit form
    const priceInput = document.getElementById('price');
    const costPriceInput = document.getElementById('costPrice');
    const profitMarginDiv = document.getElementById('profitMargin');
    
    function calculateMargin() {
        if (priceInput && costPriceInput && profitMarginDiv) {
            const price = parseFloat(priceInput.value) || 0;
            const costPrice = parseFloat(costPriceInput.value) || 0;
            
            if (costPrice > 0) {
                const margin = ((price - costPrice) / costPrice) * 100;
                profitMarginDiv.textContent = margin.toFixed(1) + '%';
                profitMarginDiv.style.color = margin >= 0 ? '#059669' : '#dc2626';
            } else {
                profitMarginDiv.textContent = '0.0%';
            }
        }
    }
    
    if (priceInput) priceInput.addEventListener('input', calculateMargin);
    if (costPriceInput) costPriceInput.addEventListener('input', calculateMargin);
});
</script>

</body>
</html>