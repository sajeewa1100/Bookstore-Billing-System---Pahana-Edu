<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Client Management - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
</head>

    <style>
        /* Enhanced CSS with proper error handling styles */
        :root {
            --primary-color: #D86C36;
            --primary-dark: #C4552C;
            --primary-darker: #A63F22;
            --accent-color: #f2a23f;
            --background-light: #F2E7DC;
            --tier-silver: #9ca3af;
            --tier-gold: #f59e0b;
            --tier-platinum: #8b5cf6;
            --error-color: #dc2626;
            --success-color: #16a34a;
            --warning-color: #d97706;
        }

        /* Client-specific styles */
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
        
        /* Form validation styles */
        .form-group.has-error input,
        .form-group.has-error select,
        .form-group.has-error textarea {
            border-color: var(--error-color);
            box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
        }

        .error-message {
            color: var(--error-color);
            font-size: 0.875rem;
            margin-top: 5px;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .field-help {
            font-size: 0.8rem;
            color: #6b7280;
            margin-top: 3px;
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
        
        .stat-icon.silver {
            background: linear-gradient(135deg, var(--tier-silver), #6b7280);
        }
        
        .stat-icon.gold {
            background: linear-gradient(135deg, var(--tier-gold), #d97706);
        }
        
        .stat-icon.platinum {
            background: linear-gradient(135deg, var(--tier-platinum), #7c3aed);
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
        
        /* Client Form Styles */
        .client-form {
            background: white;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
            padding: 30px;
            margin-top: 20px;
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

        .form-group label .required {
            color: var(--error-color);
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            padding: 12px 15px;
            border: 2px solid #e5e7eb;
            border-radius: 8px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(216, 108, 54, 0.1);
        }

        .checkbox-container {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-top: 15px;
        }

        .checkbox-container input[type="checkbox"] {
            width: 18px;
            height: 18px;
            accent-color: var(--primary-color);
        }

        .form-footer {
            display: flex;
            justify-content: flex-end;
            gap: 15px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
            margin-top: 20px;
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
            color: #10b981;
            border: 1px solid #a7f3d0;
        }
        
        .alert-error {
            background: #fee2e2;
            color: #991b1b;
            border: 1px solid #fca5a5;
        }

        .alert-info {
            background: #dbeafe;
            color: #2563eb;
            border: 1px solid #93c5fd;
        }

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
            background: var(--primary-darker);
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
            background: var(--primary-dark);
            color: white;
            text-decoration: none;
        }

        /* Profile styles */
        .client-profile {
            background: white;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
            overflow: hidden;
        }

        .profile-header {
            background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
            color: white;
            padding: 30px;
            text-align: center;
        }

        .profile-avatar {
            width: 80px;
            height: 80px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 15px;
            font-size: 2rem;
        }

        .profile-name {
            font-size: 1.8rem;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .profile-account {
            opacity: 0.9;
            font-size: 1rem;
        }

        .tier-badge {
            display: inline-block;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 600;
            text-transform: uppercase;
        }

        .tier-badge.tier-silver {
            background: var(--tier-silver);
            color: white;
        }

        .tier-badge.tier-gold {
            background: var(--tier-gold);
            color: white;
        }

        .tier-badge.tier-platinum {
            background: var(--tier-platinum);
            color: white;
        }

        .profile-content {
            padding: 30px;
        }

        .profile-section {
            margin-bottom: 25px;
            padding-bottom: 20px;
            border-bottom: 1px solid #f3f4f6;
        }

        .profile-section:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }

        .profile-section h5 {
            font-size: 1.1rem;
            font-weight: 600;
            color: #1f2937;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .profile-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }

        .profile-item {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }

        .profile-item label {
            font-size: 0.85rem;
            font-weight: 600;
            color: #6b7280;
            text-transform: uppercase;
        }

        .profile-item span {
            color: #1f2937;
            font-weight: 500;
        }

        .loyalty-points {
            color: var(--primary-color);
            font-weight: 700;
        }

        /* Client Directory Styles */
        .clients-section {
            background: white;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
            padding: 30px;
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

        .search-container {
            position: relative;
        }

        .search-container form {
            display: flex;
            gap: 10px;
            align-items: center;
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

        /* Client Cards Grid */
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
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
        }

        .client-account-number {
            font-size: 0.85rem;
            color: #6b7280;
            margin-bottom: 5px;
        }

        .client-name {
            font-size: 1.2rem;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 10px;
        }

        .client-tier {
            margin-bottom: 5px;
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
            font-size: 0.9rem;
        }

        .contact-item:last-child {
            margin-bottom: 0;
        }

        .client-loyalty {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-top: 15px;
            border-top: 1px solid #f3f4f6;
        }

        .loyalty-points {
            color: var(--primary-color);
            font-weight: 600;
        }

        .badge {
            background: var(--primary-color);
            color: white;
            padding: 3px 8px;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 600;
        }

        .client-meta {
            font-size: 0.8rem;
            color: #9ca3af;
            text-align: center;
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
   			color: #10b981;
        }

        .btn-edit {
            background: #d1fae5;
            color: #10b981;
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
            color: #d1d5db;
        }

        .empty-state h3 {
            font-size: 1.3rem;
            margin-bottom: 10px;
            color: #4b5563;
        }

        .empty-state p {
            margin-bottom: 20px;
        }

        /* Loading state */
        .btn-loading {
            opacity: 0.7;
            cursor: not-allowed;
            pointer-events: none;
        }

        .loading-spinner {
            display: inline-block;
            width: 16px;
            height: 16px;
            border: 2px solid transparent;
            border-top: 2px solid currentColor;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin-right: 8px;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* Responsive design */
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }
            
            .clients-grid {
                grid-template-columns: 1fr;
            }
            
            .search-container form {
                flex-direction: column;
                gap: 10px;
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
                <h1><i class="fas fa-users"></i> Client Management</h1>
                <p>Manage customer accounts, loyalty programs, and client information</p>
            </div>
            <a href="?action=new" class="btn-new-client">
                <i class="fas fa-plus"></i> New Client
            </a>
        </div>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i>
            ${successMessage}
        </div>
    </c:if>
    
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            <i class="fas fa-exclamation-triangle"></i>
            ${errorMessage}
        </div>
    </c:if>

    <div class="stats-container">
        <div class="stat-card">
            <div class="stat-icon total-clients">
                <i class="fas fa-users"></i>
            </div>
            <div class="stat-info">
                <h3>${totalClients > 0 ? totalClients : 0}</h3>
                <p>Total Clients</p>
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-icon silver">
                <i class="fas fa-medal"></i>
            </div>
            <div class="stat-info">
                <h3>${silverClients > 0 ? silverClients : 0}</h3>
                <p>Silver Tier</p>
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-icon gold">
                <i class="fas fa-trophy"></i>
            </div>
            <div class="stat-info">
                <h3>${goldClients > 0 ? goldClients : 0}</h3>
                <p>Gold Tier</p>
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-icon platinum">
                <i class="fas fa-crown"></i>
            </div>
            <div class="stat-info">
                <h3>${platinumClients > 0 ? platinumClients : 0}</h3>
                <p>Platinum Tier</p>
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
                                <i class="fas fa-user-plus"></i> New Client Registration
                            </c:when>
                            <c:otherwise>
                                <i class="fas fa-user-edit"></i> Edit Client Information
                            </c:otherwise>
                        </c:choose>
                    </h2>
                </div>

                <form id="clientForm" method="POST" action="clients">
                    <input type="hidden" name="action" value="${param.action == 'new' ? 'create' : 'update'}">
                    <c:if test="${param.action == 'edit'}">
                        <input type="hidden" name="id" value="${client.id}">
                    </c:if>

                    <div class="form-section">
                        <h4><i class="fas fa-user"></i> Personal Information</h4>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="firstName">First Name <span class="required">*</span></label>
                                <input type="text" id="firstName" name="firstName" 
                                       value="${client != null ? client.firstName : ''}" required maxlength="50"
                                       placeholder="Enter first name">
                                <div class="field-help">Required field</div>
                            </div>
                            <div class="form-group">
                                <label for="lastName">Last Name <span class="required">*</span></label>
                                <input type="text" id="lastName" name="lastName" 
                                       value="${client != null ? client.lastName : ''}" required maxlength="50"
                                       placeholder="Enter last name">
                                <div class="field-help">Required field</div>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" name="email" 
                                       value="${client != null ? client.email : ''}" maxlength="100"
                                       placeholder="client@example.com">
                                <div class="field-help">Optional - used for notifications if provided</div>
                            </div>
                            <div class="form-group">
                                <label for="phone">Phone Number <span class="required">*</span></label>
                                <input type="tel" id="phone" name="phone" 
                                       value="${client != null ? client.phone : ''}" required maxlength="15"
                                       placeholder="1234567890">
                                <div class="field-help">Required - 10-15 digits only</div>
                            </div>
                        </div>
                    </div>

                    <div class="form-section">
                        <h4><i class="fas fa-cog"></i> Account Settings</h4>
                        
                        <c:if test="${param.action == 'edit'}">
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="accountNumber">Account Number</label>
                                    <input type="text" id="accountNumber" name="accountNumber" 
                                           value="${client != null ? client.accountNumber : ''}" readonly 
                                           style="background-color: #f3f4f6;">
                                    <div class="field-help">Auto-generated and cannot be changed</div>
                                </div>
                                <div class="form-group">
                                    <label for="tierLevel">Tier Level</label>
                                    <select id="tierLevel" name="tierLevel">
                                        <option value="SILVER" ${client != null && client.tierLevel == 'SILVER' ? 'selected' : ''}>Silver</option>
                                        <option value="GOLD" ${client != null && client.tierLevel == 'GOLD' ? 'selected' : ''}>Gold</option>
                                        <option value="PLATINUM" ${client != null && client.tierLevel == 'PLATINUM' ? 'selected' : ''}>Platinum</option>
                                    </select>
                                    <div class="field-help">Tier is auto-calculated based on loyalty points</div>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="loyaltyPoints">Loyalty Points</label>
                                    <input type="number" id="loyaltyPoints" name="loyaltyPoints" 
                                           value="${client != null ? client.loyaltyPoints : 0}" min="0" step="1">
                                    <div class="field-help">Points earned from purchases - affects tier level</div>
                                </div>
                                <div class="form-group">
                                </div>
                            </div>
                        </c:if>

                        <div class="checkbox-container">
                            <input type="checkbox" id="sendMailAuto" name="sendMailAuto" 
                                   value="true" ${(client != null && client.sendMailAuto) || param.action == 'new' ? 'checked' : ''}>
                            <label for="sendMailAuto">Send email notifications for transactions and updates</label>
                        </div>
                    </div>

                    <div class="form-footer">
                        <a href="clients" class="btn-secondary">Cancel</a>
                        <button type="submit" class="btn-primary" id="submitBtn">
                            <i class="fas fa-save"></i>
                            <c:choose>
                                <c:when test="${param.action == 'new'}">Create Client</c:when>
                                <c:otherwise>Update Client</c:otherwise>
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
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="profile-name">${client.fullName}</div>
                    <div class="profile-account">Account #${client.accountNumber}</div>
                    <c:set var="tierLower" value="${client.tierLevel != null ? client.tierLevel : 'SILVER'}" />
                    <div class="tier-badge ${tierLower == 'SILVER' ? 'tier-silver' : (tierLower == 'GOLD' ? 'tier-gold' : 'tier-platinum')}" style="margin-top: 10px;">
                        ${tierLower}
                    </div>
                </div>

                <div class="profile-content">
                    <div class="profile-section">
                        <h5><i class="fas fa-address-book"></i> Contact Information</h5>
                        <div class="profile-grid">
                            <div class="profile-item">
                                <label>Email</label>
                                <span>${not empty client.email ? client.email : 'Not provided'}</span>
                            </div>
                            <div class="profile-item">
                                <label>Phone</label>
                                <span>${client.phone}</span>
                            </div>
                        </div>
                    </div>

                    <div class="profile-section">
                        <h5><i class="fas fa-star"></i> Loyalty Information</h5>
                        <div class="profile-grid">
                            <div class="profile-item">
                                <label>Loyalty Points</label>
                                <span class="loyalty-points">${client.loyaltyPoints} points</span>
                            </div>
                            <div class="profile-item">
                                <label>Tier Level</label>
                                <c:set var="tierLowerProfile" value="${client.tierLevel != null ? client.tierLevel : 'SILVER'}" />
                                <span class="tier-badge ${tierLowerProfile == 'SILVER' ? 'tier-silver' : (tierLowerProfile == 'GOLD' ? 'tier-gold' : 'tier-platinum')}">
                                    ${tierLowerProfile}
                                </span>
                            </div>
                            <div class="profile-item">
                                <label>Auto Mail</label>
                                <span>${client.sendMailAuto ? 'Enabled' : 'Disabled'}</span>
                            </div>
                        </div>
                    </div>

                    <div class="form-footer">
                        <a href="clients" class="btn-secondary">Back to Clients</a>
                        <a href="?action=edit&id=${client.id}" class="btn-primary">
                            <i class="fas fa-edit"></i> Edit Client
                        </a>
                    </div>
                </div>
            </div>
        </c:when>

        <c:otherwise>

				 <div class="search-container">
                        <form action="clients" method="get">
                            <select name="searchType" class="search-input">
                                <option value="phone" ${empty param.searchType || param.searchType == 'phone' ? 'selected' : ''}>Phone (Default)</option>
                                <option value="name" ${param.searchType == 'name' ? 'selected' : ''}>Name</option>
                                <option value="id" ${param.searchType == 'id' ? 'selected' : ''}>Client ID</option>
                            </select>
                            <input type="text" name="search" class="search-input" 
                                   placeholder="${param.searchType == 'name' ? 'Search by name...' : (param.searchType == 'id' ? 'Search by ID...' : 'Search by phone...')}" 
                                   value="${param.search}">
                            <button type="submit" class="btn-primary">
                                <i class="fas fa-search"></i>
                            </button>
                        </form>
                    </div>

				<c:choose>
                    <c:when test="${not empty clients}">
                        <div class="clients-grid">
                            <c:forEach var="client" items="${clients}">
                                <div class="client-card">
                                    <div class="client-header-card">
                                        <div class="client-account-number">Account #${client.accountNumber}</div>
                                        <div class="client-name">${client.fullName}</div>
                                        <div class="client-tier">
                                            <c:set var="tierLowerCard" value="${client.tierLevel != null ? client.tierLevel : 'SILVER'}" />
                                            <span class="tier-badge ${tierLowerCard == 'SILVER' ? 'tier-silver' : (tierLowerCard == 'GOLD' ? 'tier-gold' : 'tier-platinum')}">
                                                ${tierLowerCard}
                                            </span>
                                        </div>
                                    </div>

                                    <div class="client-body">
                                        <div class="client-contact">
                                            <c:if test="${not empty client.email}">
                                                <div class="contact-item">
                                                    <i class="fas fa-envelope"></i>
                                                    <span>${client.email}</span>
                                                </div>
                                            </c:if>
                                            <div class="contact-item">
                                                <i class="fas fa-phone"></i>
                                                <span>${client.phone}</span>
                                            </div>
                                        </div>

                                        <div class="client-loyalty">
                                            <span class="loyalty-points">
                                                <i class="fas fa-star"></i> ${client.loyaltyPoints} points
                                            </span>
                                            <c:if test="${client.sendMailAuto}">
                                                <span class="badge">
                                                    <i class="fas fa-envelope-open"></i> Auto Mail
                                                </span>
                                            </c:if>
                                        </div>

                                        <div class="client-meta">
                                            <span>ID: ${client.id}</span>
                                        </div>
                                    </div>

                                    <div class="client-actions">
                                        <a href="?action=view&id=${client.id}" class="btn-action btn-view" 
                                           title="View Client">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <a href="?action=edit&id=${client.id}" class="btn-action btn-edit" 
                                           title="Edit Client">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <form method="POST" action="clients" style="display:inline;" 
                                              onsubmit="return confirm('Are you sure you want to delete ${client.fullName}? This cannot be undone.')">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${client.id}">
                                            <button type="submit" class="btn-action btn-delete" title="Delete Client">
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
                            <i class="fas fa-users"></i>
                            <h3>No Clients Found</h3>
                            <p>
                                <c:choose>
                                    <c:when test="${not empty param.search}">
                                        No clients match your search criteria. Try adjusting your search terms.
                                    </c:when>
                                    <c:otherwise>
                                        Start by creating your first client account to manage customer information and loyalty programs.
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <a href="?action=new" class="btn-primary">
                                <i class="fas fa-plus"></i> Add First Client
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
    
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    }
});
</script>

</body>
</html>