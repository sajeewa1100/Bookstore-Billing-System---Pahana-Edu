<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Billing Management - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        /* Enhanced styles for billing */
        .stats-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .stat-card i {
            font-size: 2.5rem;
            margin-bottom: 10px;
            opacity: 0.9;
        }
        
        .stat-number {
            font-size: 2rem;
            font-weight: bold;
            margin: 10px 0;
        }
        
        .stat-label {
            font-size: 0.9rem;
            opacity: 0.9;
        }
        
        /* Table styles matching your books.jsp */
        .billing-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            margin: 20px 0;
        }
        
        .billing-table th {
            background: #f8f9fa;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #2c3e50;
            border-bottom: 2px solid #e9ecef;
        }
        
        .billing-table td {
            padding: 15px;
            border-bottom: 1px solid #e9ecef;
            vertical-align: middle;
        }
        
        .billing-table tr:hover {
            background-color: #f8f9fa;
        }
        
        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
            text-transform: uppercase;
        }
        
        .status-pending {
            background-color: #fff3cd;
            color: #856404;
        }
        
        .status-completed {
            background-color: #d1edff;
            color: #0c5460;
        }
        
        .status-cancelled {
            background-color: #f8d7da;
            color: #721c24;
        }
        
        .total-amount {
            font-weight: bold;
            color: #28a745;
            font-size: 1.1rem;
        }
        
        .action-buttons {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }
        
        .btn-action {
            padding: 6px 12px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 0.8rem;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-view { background-color: #007bff; color: white; }
        .btn-print { background-color: #6c757d; color: white; }
        .btn-complete { background-color: #28a745; color: white; }
        .btn-cancel { background-color: #ffc107; color: #212529; }
        
        .btn-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(0,0,0,0.2);
        }
        
        .no-billings {
            text-align: center;
            padding: 60px;
            color: #6c757d;
        }
        
        .search-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
        }
        
        .form-row {
            display: flex;
            gap: 15px;
            align-items: end;
            flex-wrap: wrap;
        }
        
        .form-group {
            flex: 1;
            min-width: 200px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #2c3e50;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        
        .btn-search {
            background: #007bff;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            height: fit-content;
        }
        
        .btn-search:hover {
            background: #0056b3;
        }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <div class="top-bar">
        <h2 class="section-title">🧾 Billing Management</h2>
        <a href="javascript:void(0);" onclick="alert('Create billing feature coming soon!');" class="btn-add-book">
            <i class="fas fa-plus"></i> Create New Bill
        </a>
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

    <!-- Statistics Cards -->
    <div class="stats-container">
        <div class="stat-card">
            <i class="fas fa-file-invoice"></i>
            <div class="stat-number">
                <c:choose>
                    <c:when test="${totalBillings != null}">${totalBillings}</c:when>
                    <c:otherwise>0</c:otherwise>
                </c:choose>
            </div>
            <div class="stat-label">Total Bills</div>
        </div>
        <div class="stat-card">
            <i class="fas fa-clock"></i>
            <div class="stat-number">
                <c:choose>
                    <c:when test="${pendingBillingsCount != null}">${pendingBillingsCount}</c:when>
                    <c:otherwise>0</c:otherwise>
                </c:choose>
            </div>
            <div class="stat-label">Pending Bills</div>
        </div>
        <div class="stat-card">
            <i class="fas fa-check-circle"></i>
            <div class="stat-number">
                <c:choose>
                    <c:when test="${completedBillingsCount != null}">${completedBillingsCount}</c:when>
                    <c:otherwise>0</c:otherwise>
                </c:choose>
            </div>
            <div class="stat-label">Completed Bills</div>
        </div>
        <div class="stat-card">
            <i class="fas fa-rupee-sign"></i>
            <div class="stat-number">
                <c:choose>
                    <c:when test="${totalRevenue != null}">
                        <fmt:formatNumber value="${totalRevenue}" pattern="#,##0.00" />
                    </c:when>
                    <c:otherwise>0.00</c:otherwise>
                </c:choose>
            </div>
            <div class="stat-label">Total Revenue</div>
        </div>
    </div>

    <!-- Search Section -->
    <div class="search-section">
        <form method="GET" action="${pageContext.request.contextPath}/BillingServlet">
            <input type="hidden" name="action" value="search">
            <div class="form-row">
                <div class="form-group">
                    <label for="searchType">Search By:</label>
                    <select name="searchType" id="searchType">
                        <option value="billNumber" ${searchType == 'billNumber' ? 'selected' : ''}>Bill Number</option>
                        <option value="status" ${searchType == 'status' ? 'selected' : ''}>Status</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="searchQuery">Search Term:</label>
                    <input type="text" name="searchQuery" id="searchQuery" 
                           placeholder="Enter search term..." value="${searchQuery}">
                </div>
                <div class="form-group">
                    <button type="submit" class="btn-search">
                        <i class="fas fa-search"></i> Search
                    </button>
                </div>
            </div>
        </form>
    </div>

    <!-- Billing List -->
    <div class="book-list">
        <c:choose>
            <c:when test="${not empty billings}">
                <table class="billing-table">
                    <thead>
                        <tr>
                            <th>Bill Number</th>
                            <th>Client</th>
                            <th>Total Amount</th>
                            <th>Status</th>
                            <th>Payment Method</th>
                            <th>Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="billing" items="${billings}">
                            <tr>
                                <td>
                                    <strong>
                                        <c:choose>
                                            <c:when test="${billing.billNumber != null}">${billing.billNumber}</c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </strong>
                                </td>
                                <td>
                                    <div>
                                        <strong>
                                            <c:choose>
                                                <c:when test="${billing.clientName != null}">${billing.clientName}</c:when>
                                                <c:when test="${billing.client != null}">${billing.client.fullName}</c:when>
                                                <c:otherwise>Unknown Client</c:otherwise>
                                            </c:choose>
                                        </strong>
                                        <br>
                                        <small style="color: #6c757d;">
                                            <c:choose>
                                                <c:when test="${billing.clientAccountNumber != null}">${billing.clientAccountNumber}</c:when>
                                                <c:when test="${billing.client != null}">${billing.client.accountNumber}</c:when>
                                                <c:otherwise>N/A</c:otherwise>
                                            </c:choose>
                                        </small>
                                    </div>
                                </td>
                                <td class="total-amount">
                                    Rs. 
                                    <c:choose>
                                        <c:when test="${billing.totalAmount != null}">
                                            <fmt:formatNumber value="${billing.totalAmount}" pattern="#,##0.00" />
                                        </c:when>
                                        <c:otherwise>0.00</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="status-badge status-${billing.status != null ? billing.status.toLowerCase() : 'pending'}">
                                        <c:choose>
                                            <c:when test="${billing.status != null}">${billing.status}</c:when>
                                            <c:otherwise>PENDING</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td>
                                    <i class="fas fa-${billing.paymentMethod == 'CARD' ? 'credit-card' : billing.paymentMethod == 'MOBILE' ? 'mobile-alt' : 'money-bill-wave'}"></i>
                                    <c:choose>
                                        <c:when test="${billing.paymentMethod != null}">${billing.paymentMethod}</c:when>
                                        <c:otherwise>CASH</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div>
                                        <c:choose>
                                            <c:when test="${billing.formattedCreatedAtShort != null}">
                                                ${billing.formattedCreatedAtShort}
                                                <br><small style="color: #6c757d;">${billing.formattedCreatedTime}</small>
                                            </c:when>
                                            <c:when test="${billing.createdAt != null}">
                                                <fmt:formatDate value="${billing.createdAt}" pattern="yyyy-MM-dd" />
                                                <br><small style="color: #6c757d;">
                                                    <fmt:formatDate value="${billing.createdAt}" pattern="HH:mm:ss" />
                                                </small>
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/BillingServlet?action=view&id=${billing.id}" 
                                           class="btn-action btn-view" title="View Details">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/BillingServlet?action=print&id=${billing.id}" 
                                           class="btn-action btn-print" title="Print" target="_blank">
                                            <i class="fas fa-print"></i>
                                        </a>
                                        <c:if test="${billing.status == null || billing.status == 'PENDING'}">
                                            <form method="POST" action="${pageContext.request.contextPath}/BillingServlet" 
                                                  style="display: inline;" 
                                                  onsubmit="return confirm('Mark this billing as completed?')">
                                                <input type="hidden" name="action" value="complete">
                                                <input type="hidden" name="billingId" value="${billing.id}">
                                                <button type="submit" class="btn-action btn-complete" title="Complete">
                                                    <i class="fas fa-check"></i>
                                                </button>
                                            </form>
                                            <form method="POST" action="${pageContext.request.contextPath}/BillingServlet" 
                                                  style="display: inline;" 
                                                  onsubmit="return confirm('Cancel this billing?')">
                                                <input type="hidden" name="action" value="cancel">
                                                <input type="hidden" name="billingId" value="${billing.id}">
                                                <button type="submit" class="btn-action btn-cancel" title="Cancel">
                                                    <i class="fas fa-times"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                        <form method="POST" action="${pageContext.request.contextPath}/BillingServlet" 
                                              style="display: inline;" 
                                              onsubmit="return confirm('Are you sure you want to delete this billing?')">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="billingId" value="${billing.id}">
                                            <button type="submit" class="btn-action btn-delete" title="Delete">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-billings">
                    <i class="fas fa-file-invoice" style="font-size: 48px; margin-bottom: 20px; color: #ccc;"></i>
                    <h3>No billing records found</h3>
                    <p>Click "Create New Bill" to add your first billing record.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

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

    // Console log for debugging
    console.log('Billing JSP loaded successfully');
    console.log('Total billings: ${totalBillings}');
    console.log('Billing data available: ${not empty billings}');
</script>

</body>
</html>