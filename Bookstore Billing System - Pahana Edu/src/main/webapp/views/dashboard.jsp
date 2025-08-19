<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tier Management - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f7fa;
        }
        .container {
            margin-top: 20px;
        }
        .card {
            margin-bottom: 20px;
            border-radius: 10px;
            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
        }
        .card-header {
            background-color: #467fd0;
            color: white;
        }
        .btn-primary, .btn-danger, .btn-success {
            border-radius: 8px;
            font-size: 16px;
        }
        .table-hover tbody tr:hover {
            background-color: #f1f1f1;
        }
        .modal-header {
            background-color: #467fd0;
            color: white;
        }
        .modal-footer .btn {
            border-radius: 5px;
        }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<!-- Main Container -->
<div class="container">

    <!-- Page Title -->
    <h1 class="my-4">
        <i class="fas fa-layer-group"></i> Tier Management
    </h1>

    <!-- Success/Error Messages -->
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle"></i> ${sessionScope.errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <!-- Tier List -->
    <div class="card">
        <div class="card-header">
            <h5 class="mb-0">Manage Tiers</h5>
        </div>
        <div class="card-body">

            <!-- Tier Statistics -->
            <div class="row mb-3">
                <div class="col-md-4">
                    <div class="card text-white bg-info">
                        <div class="card-body">
                            <h5 class="card-title">Total Tiers</h5>
                            <h2>${fn:length(tiers)}</h2>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Tier Table -->
            <div class="table-responsive">
                <table class="table table-bordered table-hover">
                    <thead>
                    <tr>
                        <th>Tier Name</th>
                        <th>Points Range</th>
                        <th>Discount Rate</th>
                        <th>Created On</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="tier" items="${tiers}">
                        <tr>
                            <td>${tier.tierName}</td>
                            <td>${tier.minPoints} - ${tier.maxPoints}</td>
                            <td>${tier.discountRate}%</td>
                            <td><fmt:formatDate value="${tier.createdAt}" pattern="MMM dd, yyyy" /></td>
                            <td>
                                <button class="btn btn-warning btn-sm" data-bs-toggle="modal"
                                        data-bs-target="#editTierModal" onclick="editTier('${tier.id}', '${tier.tierName}', ${tier.minPoints}, ${tier.maxPoints}, ${tier.discountRate})">
                                    <i class="fas fa-edit"></i> Edit
                                </button>
                                <button class="btn btn-danger btn-sm" data-bs-toggle="modal"
                                        data-bs-target="#deleteTierModal" onclick="deleteTier('${tier.id}', '${tier.tierName}')">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Add New Tier Button -->
            <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addTierModal">
                <i class="fas fa-plus"></i> Add New Tier
            </button>
        </div>
    </div>

    <!-- Add Tier Modal -->
    <div class="modal fade" id="addTierModal" tabindex="-1" aria-labelledby="addTierModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addTierModalLabel">Add New Tier</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/DashboardServlet" method="post">
                    <input type="hidden" name="action" value="createtier">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="tierName" class="form-label">Tier Name</label>
                            <input type="text" class="form-control" id="tierName" name="tierName" required>
                        </div>
                        <div class="mb-3">
                            <label for="minPoints" class="form-label">Minimum Points</label>
                            <input type="number" class="form-control" id="minPoints" name="minPoints" min="0" required>
                        </div>
                        <div class="mb-3">
                            <label for="maxPoints" class="form-label">Maximum Points</label>
                            <input type="number" class="form-control" id="maxPoints" name="maxPoints" min="1" required>
                        </div>
                        <div class="mb-3">
                            <label for="discountRate" class="form-label">Discount Rate (%)</label>
                            <input type="number" class="form-control" id="discountRate" name="discountRate" min="0" max="100" step="0.1" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Create Tier</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Edit Tier Modal -->
    <div class="modal fade" id="editTierModal" tabindex="-1" aria-labelledby="editTierModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editTierModalLabel">Edit Tier</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/DashboardServlet" method="post">
                    <input type="hidden" name="action" value="updatetier">
                    <input type="hidden" id="editTierId" name="id">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="editTierName" class="form-label">Tier Name</label>
                            <input type="text" class="form-control" id="editTierName" name="tierName" required>
                        </div>
                        <div class="mb-3">
                            <label for="editMinPoints" class="form-label">Minimum Points</label>
                            <input type="number" class="form-control" id="editMinPoints" name="minPoints" min="0" required>
                        </div>
                        <div class="mb-3">
                            <label for="editMaxPoints" class="form-label">Maximum Points</label>
                            <input type="number" class="form-control" id="editMaxPoints" name="maxPoints" min="1" required>
                        </div>
                        <div class="mb-3">
                            <label for="editDiscountRate" class="form-label">Discount Rate (%)</label>
                            <input type="number" class="form-control" id="editDiscountRate" name="discountRate" min="0" max="100" step="0.1" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Update Tier</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Delete Tier Modal -->
    <div class="modal fade" id="deleteTierModal" tabindex="-1" aria-labelledby="deleteTierModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title" id="deleteTierModalLabel">Confirm Deletion</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete the tier "<span id="deleteTierName"></span>"?</p>
                    <p class="text-danger"><small>This action cannot be undone.</small></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="${pageContext.request.contextPath}/DashboardServlet" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="deletetier">
                        <input type="hidden" id="deleteTierId" name="id">
                        <button type="submit" class="btn btn-danger">Delete Tier</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

</div>

<!-- Scripts -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
<script>
    function editTier(id, name, minPoints, maxPoints, discountRate) {
        document.getElementById('editTierId').value = id;
        document.getElementById('editTierName').value = name;
        document.getElementById('editMinPoints').value = minPoints;
        document.getElementById('editMaxPoints').value = maxPoints;
        document.getElementById('editDiscountRate').value = discountRate;
        new bootstrap.Modal(document.getElementById('editTierModal')).show();
    }

    function deleteTier(id, name) {
        document.getElementById('deleteTierId').value = id;
        document.getElementById('deleteTierName').textContent = name;
        new bootstrap.Modal(document.getElementById('deleteTierModal')).show();
    }
</script>

</body>
</html>
