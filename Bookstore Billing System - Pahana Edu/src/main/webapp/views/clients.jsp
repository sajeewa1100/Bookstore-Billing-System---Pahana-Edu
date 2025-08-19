<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clients - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        /* Additional styles for tier information */
        .tier-info {
            display: block;
            font-size: 0.8em;
            color: #666;
            margin-top: 2px;
        }
        
        .tier-bronze { background-color: #cd7f32; color: white; }
        .tier-silver { background-color: #c0c0c0; color: white; }
        .tier-gold { background-color: #ffd700; color: black; }
        .tier-platinum { background-color: #e5e4e2; color: black; }
        
        .search-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        
        .search-form {
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }
        
        .search-input-group {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        
        .search-input-group select,
        .search-input-group input {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .search-input-group input {
            min-width: 200px;
        }
        
        .search-help {
            font-size: 0.9em;
            color: #666;
            margin-top: 5px;
        }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <div class="top-bar">
        <h2 class="section-title">👤 Client Management</h2>
        <a href="javascript:void(0);" onclick="openAddModal();" class="btn-add-book">
            <i class="fas fa-plus"></i> Add New Client
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

    <!-- Search Section -->
    <div class="search-section">
        <form action="${pageContext.request.contextPath}/ClientServlet" method="get" class="search-form">
            <input type="hidden" name="action" value="search" />
            
            <div class="search-input-group">
                <label for="searchType"><strong>Search by:</strong></label>
                <select name="searchType" id="searchType">
                    <option value="phone" <c:if test="${searchType eq 'phone'}">selected</c:if>>Phone Number</option>
                    <option value="name" <c:if test="${searchType eq 'name'}">selected</c:if>>Name</option>
                    <option value="email" <c:if test="${searchType eq 'email'}">selected</c:if>>Email</option>
                    <option value="id" <c:if test="${searchType eq 'id'}">selected</c:if>>Account ID</option>
                </select>
            </div>
            
            <div class="search-input-group">
                <input type="text" name="searchQuery" id="searchQuery" 
                       value="${searchQuery}" placeholder="Enter search term..." />
                <button type="submit" class="btn-filter">
                    <i class="fas fa-search"></i> Search
                </button>
                <a href="${pageContext.request.contextPath}/ClientServlet?action=clients" class="btn-clear">
                    <i class="fas fa-times"></i> Clear
                </a>
            </div>
        </form>
        
        <div class="search-help" id="searchHelp">
            <!-- Dynamic help text will be shown here -->
        </div>
    </div>

    <!-- Client List -->
    <div class="book-list">
        <c:choose>
            <c:when test="${not empty clients}">
                <table class="book-table">
                    <thead>
                        <tr>
                            <th>Account ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>City</th>
                            <th>Loyalty Points</th>
                            <th>Tier Level</th>
                            <th>Mail Auto</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="client" items="${clients}">
                            <tr>
                                <td><strong>${client.accountNumber}</strong></td>
                                <td>${client.firstName} ${client.lastName}</td>
                                <td>${client.email}</td>
                                <td>${client.phone}</td>
                                <td>${client.city}</td>
                                <td><span class="loyalty-points">${client.loyaltyPoints}</span></td>
                                <td>
                                    <span class="tier-badge tier-${fn:toLowerCase(client.tierLevel)}">
                                        ${client.tierLevel}
                                    </span>
                                </td>
                                <td>
                                    <span class="auto-mail ${client.sendMailAuto ? 'enabled' : 'disabled'}">
                                        <i class="fas ${client.sendMailAuto ? 'fa-check' : 'fa-times'}"></i>
                                    </span>
                                </td>
                                <td>
                                    <button type="button" class="btn-view" onclick="viewClient('${client.id}')">
                                        <i class="fas fa-eye"></i> View
                                    </button>
                                    <button type="button" class="btn-edit" onclick="editClient('${client.id}', '${client.firstName}', '${client.lastName}', '${client.email}', '${client.phone}', '${client.state}', '${client.street}', '${client.city}', '${client.zip}', ${client.sendMailAuto})">
                                        <i class="fas fa-edit"></i> Edit
                                    </button>
                                    <form action="${pageContext.request.contextPath}/ClientServlet" method="post" style="display: inline;" onsubmit="return confirmDelete('${client.firstName} ${client.lastName}');">
                                        <input type="hidden" name="action" value="delete" />
                                        <input type="hidden" name="id" value="${client.id}" />
                                        <button type="submit" class="btn-delete">
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
                    <i class="fas fa-users" style="font-size: 48px; margin-bottom: 20px; color: #ccc;"></i>
                    <h3>No clients found</h3>
                    <p>
                        <c:choose>
                            <c:when test="${not empty searchQuery}">
                                No clients match your search criteria. Try a different search term.
                            </c:when>
                            <c:otherwise>
                                Click "Add New Client" to register your first client.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Add/Edit Client Modal -->
    <div id="clientModal" class="modal" style="display: none;">
        <div class="modal-content modal-large">
            <div class="modal-header">
                <h3 id="modalTitle">Add New Client</h3>
                <span class="close" onclick="closeModal()">&times;</span>
            </div>
            <hr />
            
            <form action="${pageContext.request.contextPath}/ClientServlet" method="post" id="clientForm">
                <input type="hidden" name="action" value="add" id="formAction" />
                <input type="hidden" name="id" id="clientId" />

                <div class="form-row">
                    <div class="form-group half-width">
                        <label for="firstName">First Name: <span class="required">*</span></label>
                        <input type="text" id="firstName" name="firstName" required placeholder="Enter first name" />
                    </div>

                    <div class="form-group half-width">
                        <label for="lastName">Last Name: <span class="required">*</span></label>
                        <input type="text" id="lastName" name="lastName" required placeholder="Enter last name" />
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group half-width">
                        <label for="email">Email: <span class="required">*</span></label>
                        <input type="email" id="email" name="email" required placeholder="Enter email address" />
                    </div>

                    <div class="form-group half-width">
                        <label for="phone">Phone Number: <span class="required">*</span></label>
                        <input type="tel" id="phone" name="phone" required placeholder="e.g., 0771234567 or +94771234567" />
                        <small class="form-help">Sri Lankan phone format: 0771234567, +94771234567, or 771234567</small>
                    </div>
                </div>

                <div class="form-group">
                    <label for="street">Street Address: <span class="required">*</span></label>
                    <input type="text" id="street" name="street" required placeholder="Enter street address" />
                </div>

                <div class="form-row">
                    <div class="form-group third-width">
                        <label for="city">City: <span class="required">*</span></label>
                        <input type="text" id="city" name="city" required placeholder="Enter city" />
                    </div>

                    <div class="form-group third-width">
                        <label for="state">State/Province: <span class="required">*</span></label>
                        <input type="text" id="state" name="state" required placeholder="Enter state/province" />
                    </div>

                    <div class="form-group third-width">
                        <label for="zip">Postal Code: <span class="required">*</span></label>
                        <input type="text" id="zip" name="zip" required placeholder="Enter postal code" />
                    </div>
                </div>

                <div class="form-group">
                    <label class="checkbox-label">
                        <input type="checkbox" id="sendMailAuto" name="sendMailAuto" value="true" />
                        <span class="checkmark"></span>
                        Send Mail Automatically
                    </label>
                    <small class="form-help">Enable automatic email notifications for this client</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save" id="saveBtn">
                        <i class="fas fa-save"></i> Save Client
                    </button>
                    <button type="button" class="btn-cancel" onclick="closeModal()">
                        <i class="fas fa-times"></i> Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Client Profile View Modal -->
    <div id="clientViewModal" class="modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Client Profile</h3>
                <span class="close" onclick="closeViewModal()">&times;</span>
            </div>
            <hr />
            
            <div id="clientProfile">
                <!-- Profile content will be loaded here via AJAX -->
            </div>
        </div>
    </div>
</main>

<script>
    // Update search help text based on selected search type
    function updateSearchHelp() {
        const searchType = document.getElementById('searchType').value;
        const helpElement = document.getElementById('searchHelp');
        
        switch(searchType) {
            case 'phone':
                helpElement.innerHTML = '<i class="fas fa-info-circle"></i> <strong>Phone Search:</strong> Enter any part of the phone number. Supports Sri Lankan formats: 0771234567, +94771234567, 771234567';
                break;
            case 'name':
                helpElement.innerHTML = '<i class="fas fa-info-circle"></i> <strong>Name Search:</strong> Search by first name, last name, or full name';
                break;
            case 'email':
                helpElement.innerHTML = '<i class="fas fa-info-circle"></i> <strong>Email Search:</strong> Enter any part of the email address';
                break;
            case 'id':
                helpElement.innerHTML = '<i class="fas fa-info-circle"></i> <strong>Account ID Search:</strong> Enter any part of the account number';
                break;
            default:
                helpElement.innerHTML = '';
        }
    }

    // Initialize search help on page load
    document.addEventListener('DOMContentLoaded', function() {
        updateSearchHelp();
        
        // Update help when search type changes
        document.getElementById('searchType').addEventListener('change', updateSearchHelp);
    });

    // Modal functions
    function openAddModal() {
        document.getElementById('modalTitle').textContent = 'Add New Client';
        document.getElementById('formAction').value = 'add';
        document.getElementById('clientId').value = '';
        document.getElementById('saveBtn').innerHTML = '<i class="fas fa-save"></i> Save Client';
        
        // Clear form
        document.getElementById('clientForm').reset();
        
        document.getElementById('clientModal').style.display = 'block';
    }

    function editClient(id, firstName, lastName, email, phone, state, street, city, zip, sendMailAuto) {
        document.getElementById('modalTitle').textContent = 'Edit Client';
        document.getElementById('formAction').value = 'update';
        document.getElementById('clientId').value = id;
        document.getElementById('saveBtn').innerHTML = '<i class="fas fa-save"></i> Update Client';
        
        // Fill form with client data
        document.getElementById('firstName').value = firstName || '';
        document.getElementById('lastName').value = lastName || '';
        document.getElementById('email').value = email || '';
        document.getElementById('phone').value = phone || '';
        document.getElementById('state').value = state || '';
        document.getElementById('street').value = street || '';
        document.getElementById('city').value = city || '';
        document.getElementById('zip').value = zip || '';
        document.getElementById('sendMailAuto').checked = sendMailAuto;
        
        document.getElementById('clientModal').style.display = 'block';
    }

    function viewClient(clientId) {
        // Load client profile via AJAX
        fetch('${pageContext.request.contextPath}/ClientServlet?action=profile&id=' + clientId)
            .then(response => response.text())
            .then(html => {
                document.getElementById('clientProfile').innerHTML = html;
                document.getElementById('clientViewModal').style.display = 'block';
            })
            .catch(error => {
                console.error('Error loading client profile:', error);
                alert('Error loading client profile');
            });
    }

    function closeModal() {
        document.getElementById('clientModal').style.display = 'none';
    }

    function closeViewModal() {
        document.getElementById('clientViewModal').style.display = 'none';
    }

    function confirmDelete(clientName) {
        return confirm('Are you sure you want to delete client "' + clientName + '"? This action cannot be undone.');
    }

    // Close modal when clicking outside
    window.onclick = function(event) {
        var modal = document.getElementById('clientModal');
        var viewModal = document.getElementById('clientViewModal');
        if (event.target == modal) {
            closeModal();
        }
        if (event.target == viewModal) {
            closeViewModal();
        }
    }

    // Form validation with Sri Lankan phone validation
    document.getElementById('clientForm').addEventListener('submit', function(e) {
        var email = document.getElementById('email').value;
        var phone = document.getElementById('phone').value;
        var zip = document.getElementById('zip').value;
        
        // Email validation
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            alert('Please enter a valid email address');
            e.preventDefault();
            return false;
        }
        
        // Sri Lankan phone validation
        if (!isValidSriLankanPhone(phone)) {
            alert('Please enter a valid Sri Lankan phone number.\nAccepted formats:\n- 0771234567\n- +94771234567\n- 771234567');
            e.preventDefault();
            return false;
        }
        
        // Zip code validation (basic)
        if (zip.length < 4) {
            alert('Please enter a valid postal code');
            e.preventDefault();
            return false;
        }
        
        return true;
    });

    // Sri Lankan phone validation function
    function isValidSriLankanPhone(phone) {
        if (!phone) return false;
        
        // Remove all non-digit characters except +
        var cleanPhone = phone.replace(/[^\d+]/g, '');
        
        // Check different Sri Lankan phone patterns
        if (cleanPhone.startsWith('+94')) {
            // International format: +94771234567
            var withoutCountry = cleanPhone.substring(3);
            return withoutCountry.length === 9 && withoutCountry.startsWith('7');
        } else if (cleanPhone.startsWith('0')) {
            // Local format: 0771234567
            return cleanPhone.length >= 9 && cleanPhone.length <= 10;
        } else if (cleanPhone.length === 9 && cleanPhone.startsWith('7')) {
            // Mobile without leading 0: 771234567
            return true;
        }
        
        return false;
    }

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
            closeModal();
            closeViewModal();
        }
    });

    // Phone number formatting for Sri Lankan numbers
    document.getElementById('phone').addEventListener('input', function(e) {
        var value = e.target.value.replace(/[^\d+]/g, '');
        
        // Auto-format Sri Lankan mobile numbers
        if (value.startsWith('0') && value.length === 10) {
            // Format as 077 123 4567
            var formatted = value.replace(/(\d{3})(\d{3})(\d{4})/, '$1 $2 $3');
            e.target.value = formatted;
        } else if (value.startsWith('+94') && value.length === 12) {
            // Format as +94 77 123 4567
            var formatted = value.replace(/(\+94)(\d{2})(\d{3})(\d{4})/, '$1 $2 $3 $4');
            e.target.value = formatted;
        }
    });

    // Search functionality
    document.getElementById('searchQuery').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            this.closest('form').submit();
        }
    });

    // Update placeholder text based on search type
    document.getElementById('searchType').addEventListener('change', function() {
        var searchQuery = document.getElementById('searchQuery');
        var searchType = this.value;
        
        switch(searchType) {
            case 'phone':
                searchQuery.placeholder = 'e.g., 0771234567, +94771234567, or 1234567';
                break;
            case 'name':
                searchQuery.placeholder = 'Enter first name, last name, or full name';
                break;
            case 'email':
                searchQuery.placeholder = 'Enter email address or part of it';
                break;
            case 'id':
                searchQuery.placeholder = 'Enter account ID or part of it';
                break;
            default:
                searchQuery.placeholder = 'Enter search term...';
        }
    });

    // Initialize placeholder on page load
    document.addEventListener('DOMContentLoaded', function() {
        var searchTypeSelect = document.getElementById('searchType');
        if (searchTypeSelect) {
            searchTypeSelect.dispatchEvent(new Event('change'));
        }
    });
</script>

</body>
</html>