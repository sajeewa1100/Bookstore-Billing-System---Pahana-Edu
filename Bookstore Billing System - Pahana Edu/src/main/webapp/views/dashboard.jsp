<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    
    <style>
        /* Additional styles for tier management */
        .tier-card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            margin: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            border-left: 4px solid #007bff;
        }
        
        .tier-bronze { border-left-color: #cd7f32; }
        .tier-silver { border-left-color: #c0c0c0; }
        .tier-gold { border-left-color: #ffd700; }
        .tier-platinum { border-left-color: #e5e4e2; }
        
        .tier-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        .tier-table th,
        .tier-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        .tier-table th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        
        .tier-badge {
            padding: 4px 8px;
            border-radius: 4px;
            color: white;
            font-size: 0.8em;
            font-weight: bold;
        }
        
        .tier-badge.bronze { background-color: #cd7f32; }
        .tier-badge.silver { background-color: #c0c0c0; color: #333; }
        .tier-badge.gold { background-color: #ffd700; color: #333; }
        .tier-badge.platinum { background-color: #e5e4e2; color: #333; }
        
        .btn-tier {
            padding: 6px 12px;
            margin: 2px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 0.9em;
        }
        
        .btn-tier.edit {
            background-color: #ffc107;
            color: #212529;
        }
        
        .btn-tier.delete {
            background-color: #dc3545;
            color: white;
        }
        
        .btn-tier:hover {
            opacity: 0.8;
        }
        
        .discount-rate {
            font-weight: bold;
            color: #28a745;
        }
        
        .points-range {
            font-weight: 600;
            color: #495057;
        }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <div class="top-bar">
        <h2 class="section-title">📊 Dashboard - Tier Management</h2>
        <a href="javascript:void(0);" onclick="openAddTierModal();" class="btn-add-book">
            <i class="fas fa-plus"></i> Add New Tier
        </a>
    </div>

    <hr />

    <!-- Display Success/Error Messages -->
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="success-message">
            <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
        </div>
        <c:remove var="successMessage" scope="session" />
    </c:if>

    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="error-message">
            <i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMessage}
        </div>
        <c:remove var="errorMessage" scope="session" />
    </c:if>

    <!-- Debug Info -->
    <div style="background: #f0f0f0; padding: 10px; margin: 10px 0; border-radius: 4px;">
        <strong>Debug Info:</strong><br>
        Current User: ${currentUser != null ? currentUser.username : 'Not logged in'}<br>
        Is Manager: ${isManager}<br>
        Tiers Count: ${tiers != null ? fn:length(tiers) : 'No tiers data'}<br>
        Action: ${param.action}
    </div>

    <!-- Tier Overview Cards -->
    <c:if test="${not empty tiers}">
        <div style="display: flex; flex-wrap: wrap; margin: 20px 0;">
            <c:forEach var="tier" items="${tiers}">
                <div class="tier-card tier-${fn:toLowerCase(tier.tierName)}">
                    <h4>
                        <span class="tier-badge ${fn:toLowerCase(tier.tierName)}">${tier.tierName}</span>
                    </h4>
                    <div class="points-range">
                        ${tier.minPoints} - ${tier.maxPoints != null ? tier.maxPoints : '∞'} points
                    </div>
                    <div class="discount-rate">
                        <fmt:formatNumber value="${tier.discountRate * 100}" type="number" maxFractionDigits="1"/>% discount
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <!-- Tier List Table -->
    <div class="book-list">
        <h3>All Tiers</h3>
        <c:choose>
            <c:when test="${not empty tiers}">
                <table class="tier-table">
                    <thead>
                        <tr>
                            <th>Tier Name</th>
                            <th>Points Range</th>
                            <th>Discount Rate</th>
                            <th>Created Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="tier" items="${tiers}">
                            <tr>
                                <td>
                                    <span class="tier-badge ${fn:toLowerCase(tier.tierName)}">${tier.tierName}</span>
                                </td>
                                <td class="points-range">
                                    ${tier.minPoints} - ${tier.maxPoints != null ? tier.maxPoints : '∞'}
                                </td>
                                <td class="discount-rate">
                                    <fmt:formatNumber value="${tier.discountRate * 100}" type="number" maxFractionDigits="1"/>%
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${tier.createdAt != null}">
                                            <fmt:formatDate value="${tier.createdAtAsDate}" pattern="MMM dd, yyyy" />
                                        </c:when>
                                        <c:otherwise>Recently created</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <button type="button" class="btn-tier edit" 
                                            onclick="editTier('${tier.id}', '${fn:escapeXml(tier.tierName)}', ${tier.minPoints}, ${tier.maxPoints != null ? tier.maxPoints : 'null'}, ${tier.discountRate * 100})">
                                        <i class="fas fa-edit"></i> Edit
                                    </button>
                                    <form action="${pageContext.request.contextPath}/DashboardServlet" method="post" style="display: inline;" onsubmit="return confirmDeleteTier('${tier.tierName}');">
                                        <input type="hidden" name="action" value="deletetier" />
                                        <input type="hidden" name="id" value="${tier.id}" />
                                        <button type="submit" class="btn-tier delete">
                                            <i class="fas fa-trash"></i> Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-books">
                    <i class="fas fa-layer-group" style="font-size: 48px; margin-bottom: 20px; color: #ccc;"></i>
                    <h3>No tiers available</h3>
                    <p>Click "Add New Tier" to create your first tier for the loyalty program.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Add/Edit Tier Modal -->
    <div id="tierModal" class="modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="tierModalTitle">Add New Tier</h3>
                <span class="close" onclick="closeTierModal()">&times;</span>
            </div>
            <hr />
            
            <form action="${pageContext.request.contextPath}/DashboardServlet" method="post" id="tierForm">
                <input type="hidden" name="action" value="createtier" id="tierFormAction" />
                <input type="hidden" name="id" id="tierId" />

                <div class="form-group">
                    <label for="tierName">Tier Name:</label>
                    <input type="text" id="tierName" name="tierName" required placeholder="e.g., Bronze, Silver, Gold" maxlength="50" />
                </div>

                <div class="form-group">
                    <label for="minPoints">Minimum Points:</label>
                    <input type="number" id="minPoints" name="minPoints" required placeholder="0" min="0" max="999999" />
                </div>

                <div class="form-group">
                    <label for="maxPoints">Maximum Points (optional):</label>
                    <input type="number" id="maxPoints" name="maxPoints" placeholder="Leave empty for unlimited" min="1" max="999999" />
                </div>

                <div class="form-group">
                    <label for="discountRate">Discount Rate (%):</label>
                    <input type="number" id="discountRate" name="discountRate" required placeholder="5.0" min="0" max="100" step="0.1" />
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save" id="tierSaveBtn">
                        <i class="fas fa-save"></i> Save Tier
                    </button>
                </div>
            </form>
        </div>
    </div>
</main>

<script>
    // Tier Modal functions
    function openAddTierModal() {
        document.getElementById('tierModalTitle').textContent = 'Add New Tier';
        document.getElementById('tierFormAction').value = 'createtier';
        document.getElementById('tierId').value = '';
        document.getElementById('tierSaveBtn').innerHTML = '<i class="fas fa-save"></i> Save Tier';
        
        // Clear form
        document.getElementById('tierForm').reset();
        
        document.getElementById('tierModal').style.display = 'block';
    }

    function editTier(id, tierName, minPoints, maxPoints, discountRate) {
        console.log('Edit tier:', {id, tierName, minPoints, maxPoints, discountRate});
        
        document.getElementById('tierModalTitle').textContent = 'Edit Tier';
        document.getElementById('tierFormAction').value = 'updatetier';
        document.getElementById('tierId').value = id;
        document.getElementById('tierSaveBtn').innerHTML = '<i class="fas fa-save"></i> Update Tier';
        
        // Fill form with tier data
        document.getElementById('tierName').value = tierName;
        document.getElementById('minPoints').value = minPoints;
        document.getElementById('maxPoints').value = (maxPoints !== null && maxPoints !== 'null') ? maxPoints : '';
        document.getElementById('discountRate').value = discountRate;
        
        document.getElementById('tierModal').style.display = 'block';
    }

    function closeTierModal() {
        document.getElementById('tierModal').style.display = 'none';
    }

    function confirmDeleteTier(tierName) {
        return confirm('Are you sure you want to delete the "' + tierName + '" tier? This action cannot be undone.');
    }

    // Close modal when clicking outside
    window.onclick = function(event) {
        var modal = document.getElementById('tierModal');
        if (event.target == modal) {
            closeTierModal();
        }
    }

    // Form validation
    document.getElementById('tierForm').addEventListener('submit', function(e) {
        var minPoints = parseInt(document.getElementById('minPoints').value);
        var maxPoints = document.getElementById('maxPoints').value;
        var discountRate = parseFloat(document.getElementById('discountRate').value);
        
        if (minPoints < 0) {
            alert('Minimum points cannot be negative');
            e.preventDefault();
            return false;
        }
        
        if (maxPoints && parseInt(maxPoints) <= minPoints) {
            alert('Maximum points must be greater than minimum points');
            e.preventDefault();
            return false;
        }
        
        if (discountRate < 0 || discountRate > 100) {
            alert('Discount rate must be between 0 and 100');
            e.preventDefault();
            return false;
        }
        
        return true;
    });

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

    // Escape key to close modal
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeTierModal();
        }
    });
</script>

</body>
</html>