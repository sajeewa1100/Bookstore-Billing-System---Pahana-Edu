<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Help & Documentation - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        :root {
            --primary-color: #D86C36;
            --primary-dark: #C4552C;
            --primary-darker: #A63F22;
            --accent-color: #f2a23f;
            --background-light: #F2E7DC;
            --background-white: #fff;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: var(--background-light);
            color: #1a202c;
        }

        /* Help Header */
        .help-header {
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

        .help-navigation {
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            padding: 15px;
        }

        .help-nav-links {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .help-nav-links a {
            color: white;
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 20px;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            font-size: 0.9rem;
            transition: all 0.3s ease;
        }

        .help-nav-links a:hover {
            background: rgba(255, 255, 255, 0.2);
            text-decoration: none;
            color: white;
        }

        /* Content Sections */
        .help-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }

        .help-section {
            background: white;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
            padding: 30px;
            margin-bottom: 30px;
        }

        .section-header {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 25px;
            padding-bottom: 20px;
            border-bottom: 2px solid #f3f4f6;
        }

        .section-icon {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
            color: white;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.2rem;
        }

        .section-title {
            font-size: 1.8rem;
            font-weight: 700;
            color: #1f2937;
            margin: 0;
        }

        .help-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 25px;
        }

        .help-card {
            background: #f8fafc;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            padding: 25px;
            transition: all 0.3s ease;
        }

        .help-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            border-color: var(--primary-color);
        }

        .card-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 15px;
        }

        .card-icon {
            width: 40px;
            height: 40px;
            background: var(--primary-color);
            color: white;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1rem;
        }

        .card-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #1f2937;
            margin: 0;
        }

        .card-content {
            color: #4b5563;
            line-height: 1.6;
        }

        .card-content ul {
            margin: 10px 0;
            padding-left: 20px;
        }

        .card-content li {
            margin-bottom: 8px;
        }

        .step-list {
            counter-reset: step-counter;
            list-style: none;
            padding: 0;
        }

        .step-list li {
            counter-increment: step-counter;
            background: var(--background-light);
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 15px;
            position: relative;
            padding-left: 60px;
        }

        .step-list li::before {
            content: counter(step-counter);
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            background: var(--primary-color);
            color: white;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 0.9rem;
        }

        .step-title {
            font-weight: 600;
            color: #1f2937;
            margin-bottom: 5px;
        }

        .step-description {
            color: #6b7280;
            font-size: 0.9rem;
        }

        .feature-highlight {
            background: linear-gradient(135deg, var(--background-light), #e2e8f0);
            border: 2px solid var(--accent-color);
            border-radius: 12px;
            padding: 20px;
            margin: 20px 0;
        }

        .feature-highlight h4 {
            color: var(--primary-darker);
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .role-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .role-badge.admin {
            background: #fee2e2;
            color: #991b1b;
        }

        .role-badge.manager {
            background: #dbeafe;
            color: #1e40af;
        }

        .role-badge.staff {
            background: #d1fae5;
            color: #065f46;
        }

        .shortcut-key {
            display: inline-block;
            background: #374151;
            color: white;
            padding: 2px 8px;
            border-radius: 4px;
            font-family: monospace;
            font-size: 0.8rem;
            margin: 0 3px;
        }

        .alert-tip {
            background: #e0f2fe;
            border: 1px solid #4fc3f7;
            border-radius: 8px;
            padding: 15px;
            margin: 15px 0;
            display: flex;
            align-items: flex-start;
            gap: 10px;
        }

        .alert-tip i {
            color: #0277bd;
            font-size: 1.2rem;
            margin-top: 2px;
        }

        .alert-tip .tip-content {
            color: #01579b;
        }

        .faq-item {
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            margin-bottom: 15px;
        }

        .faq-question {
            background: none;
            border: none;
            width: 100%;
            text-align: left;
            padding: 20px;
            font-weight: 600;
            color: #1f2937;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
            transition: background 0.3s ease;
        }

        .faq-question:hover {
            background: #f9fafb;
        }

        .faq-answer {
            padding: 0 20px 20px;
            color: #4b5563;
            line-height: 1.6;
            display: none;
        }

        .faq-answer.active {
            display: block;
        }

        .search-box {
            position: relative;
            margin-bottom: 30px;
        }

        .search-input {
            width: 100%;
            padding: 15px 50px 15px 20px;
            border: 2px solid #e5e7eb;
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .search-input:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(216, 108, 54, 0.1);
        }

        .search-icon {
            position: absolute;
            right: 20px;
            top: 50%;
            transform: translateY(-50%);
            color: #9ca3af;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .header-content {
                flex-direction: column;
                gap: 20px;
                text-align: center;
            }

            .help-nav-links {
                justify-content: center;
            }

            .help-grid {
                grid-template-columns: 1fr;
            }

            .help-section {
                padding: 20px;
                margin-bottom: 20px;
            }
        }
    </style>
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <!-- Help Header -->
    <div class="help-header">
        <div class="header-content">
            <div class="header-title">
                <h1><i class="fas fa-question-circle"></i> Help & Documentation</h1>
                <p>Complete guide to using the Pahana Bookstore Management System</p>
            </div>
            <div class="help-navigation">
                <div class="help-nav-links">
                    <a href="#getting-started"><i class="fas fa-play"></i> Getting Started</a>
                    <a href="#invoicing"><i class="fas fa-receipt"></i> Invoicing</a>
                    <a href="#clients"><i class="fas fa-users"></i> Clients</a>
                    <a href="#books"><i class="fas fa-book"></i> Books</a>
                    <a href="#management"><i class="fas fa-cog"></i> Management</a>
                    <a href="#faq"><i class="fas fa-question"></i> FAQ</a>
                </div>
            </div>
        </div>
    </div>

    <div class="help-container">
        <!-- Search Box -->
        <div class="search-box">
            <input type="text" class="search-input" id="helpSearch" placeholder="Search help topics...">
            <i class="fas fa-search search-icon"></i>
        </div>

        <!-- Getting Started Section -->
        <div class="help-section" id="getting-started">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fas fa-play"></i>
                </div>
                <div>
                    <h2 class="section-title">Getting Started</h2>
                </div>
            </div>

            <div class="help-grid">
                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-sign-in-alt"></i>
                        </div>
                        <h3 class="card-title">First Time Login</h3>
                    </div>
                    <div class="card-content">
                        <p>When you first access the system, you'll be prompted to set up your account:</p>
                        <ol class="step-list">
                            <li>
                                <div class="step-title">Login with Default Credentials</div>
                                <div class="step-description">Use username: <strong>admin</strong>, password: <strong>admin123</strong></div>
                            </li>
                            <li>
                                <div class="step-title">Complete First-Time Setup</div>
                                <div class="step-description">Change your password and provide contact information</div>
                            </li>
                            <li>
                                <div class="step-title">Explore the Dashboard</div>
                                <div class="step-description">Familiarize yourself with the main navigation menu</div>
                            </li>
                        </ol>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-user-tag"></i>
                        </div>
                        <h3 class="card-title">User Roles</h3>
                    </div>
                    <div class="card-content">
                        <p>The system has different user roles with varying permissions:</p>
                        <ul>
                            <li><span class="role-badge admin">Admin</span> - Full system access, user management</li>
                            <li><span class="role-badge manager">Manager</span> - Staff management, loyalty settings, all invoices</li>
                            <li><span class="role-badge staff">Staff</span> - Create invoices, manage books and clients</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-info-circle"></i>
                            <div class="tip-content">
                                Your role determines which menu items and features you can access. Contact your administrator if you need different permissions.
                            </div>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-compass"></i>
                        </div>
                        <h3 class="card-title">Navigation Overview</h3>
                    </div>
                    <div class="card-content">
                        <p>The main navigation menu provides access to all system features:</p>
                        <ul>
                            <li><strong>Dashboard</strong> - Overview and quick actions</li>
                            <li><strong>Create Invoice</strong> - Generate new invoices</li>
                            <li><strong>Invoices</strong> - View and manage invoices</li>
                            <li><strong>Clients</strong> - Customer management</li>
                            <li><strong>Books</strong> - Inventory management</li>
                            <li><strong>Manager Dashboard</strong> - Staff and system management</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- Invoicing Section -->
        <div class="help-section" id="invoicing">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fas fa-receipt"></i>
                </div>
                <div>
                    <h2 class="section-title">Invoice Management</h2>
                </div>
            </div>

            <div class="help-grid">
                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-plus-square"></i>
                        </div>
                        <h3 class="card-title">Creating New Invoices</h3>
                    </div>
                    <div class="card-content">
                        <p>Follow these steps to create a new invoice:</p>
                        <ol class="step-list">
                            <li>
                                <div class="step-title">Choose Customer Type</div>
                                <div class="step-description">Select "Walk-in Customer" or search for a registered client</div>
                            </li>
                            <li>
                                <div class="step-title">Add Books</div>
                                <div class="step-description">Search by ISBN or title, then add books to the invoice</div>
                            </li>
                            <li>
                                <div class="step-title">Set Quantities</div>
                                <div class="step-description">Adjust quantities for each book as needed</div>
                            </li>
                            <li>
                                <div class="step-title">Process Payment</div>
                                <div class="step-description">Enter cash amount and complete the transaction</div>
                            </li>
                        </ol>

                        <div class="feature-highlight">
                            <h4><i class="fas fa-barcode"></i> Barcode Scanning</h4>
                            <p>You can scan book barcodes directly into the ISBN search field for quick product lookup.</p>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-star"></i>
                        </div>
                        <h3 class="card-title">Loyalty Program</h3>
                    </div>
                    <div class="card-content">
                        <p>The system includes an automatic loyalty program:</p>
                        <ul>
                            <li><strong>Silver Tier:</strong> 5% discount (default)</li>
                            <li><strong>Gold Tier:</strong> 10% discount</li>
                            <li><strong>Platinum Tier:</strong> 15% discount</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-lightbulb"></i>
                            <div class="tip-content">
                                Customers earn loyalty points with each purchase. Points automatically upgrade their tier level and discount percentage.
                            </div>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-print"></i>
                        </div>
                        <h3 class="card-title">Printing Invoices</h3>
                    </div>
                    <div class="card-content">
                        <p>Several ways to print invoices:</p>
                        <ul>
                            <li><strong>Create & Print:</strong> Print immediately after creating</li>
                            <li><strong>View & Print:</strong> Print from invoice details page</li>
                            <li><strong>Bulk Operations:</strong> Print multiple invoices (Manager+)</li>
                        </ul>
                        
                        <div class="feature-highlight">
                            <h4><i class="fas fa-file-pdf"></i> PDF Generation</h4>
                            <p>All invoices are generated as PDF files for consistent printing and record keeping.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Client Management Section -->
        <div class="help-section" id="clients">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fas fa-users"></i>
                </div>
                <div>
                    <h2 class="section-title">Client Management</h2>
                </div>
            </div>

            <div class="help-grid">
                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-user-plus"></i>
                        </div>
                        <h3 class="card-title">Adding New Clients</h3>
                    </div>
                    <div class="card-content">
                        <p>Required information for new clients:</p>
                        <ul>
                            <li><strong>First Name & Last Name</strong> (required)</li>
                            <li><strong>Phone Number</strong> (required)</li>
                            <li><strong>Email Address</strong> (optional)</li>
                            <li><strong>Auto-mail notifications</strong> (optional)</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-id-card"></i>
                            <div class="tip-content">
                                Account numbers are automatically generated. All new clients start at Silver tier level.
                            </div>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-search"></i>
                        </div>
                        <h3 class="card-title">Finding Clients</h3>
                    </div>
                    <div class="card-content">
                        <p>Search clients using different methods:</p>
                        <ul>
                            <li><strong>Phone Number</strong> - Most common method</li>
                            <li><strong>Name</strong> - Search by first or last name</li>
                            <li><strong>Client ID</strong> - Direct ID lookup</li>
                        </ul>
                        
                        <div class="feature-highlight">
                            <h4><i class="fas fa-filter"></i> Smart Search</h4>
                            <p>The search is case-insensitive and matches partial entries for easier client lookup.</p>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-medal"></i>
                        </div>
                        <h3 class="card-title">Tier Management</h3>
                    </div>
                    <div class="card-content">
                        <p>Client tiers are automatically calculated based on loyalty points:</p>
                        <ul>
                            <li><strong>Silver:</strong> Default tier (0+ points)</li>
                            <li><strong>Gold:</strong> Requires 500+ points</li>
                            <li><strong>Platinum:</strong> Requires 1000+ points</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-cog"></i>
                            <div class="tip-content">
                                Managers can adjust tier thresholds and discount percentages in the Loyalty Settings.
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Book Management Section -->
        <div class="help-section" id="books">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fas fa-book"></i>
                </div>
                <div>
                    <h2 class="section-title">Book Inventory</h2>
                </div>
            </div>

            <div class="help-grid">
                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-plus-circle"></i>
                        </div>
                        <h3 class="card-title">Adding Books</h3>
                    </div>
                    <div class="card-content">
                        <p>Required information for new books:</p>
                        <ul>
                            <li><strong>Title & Author</strong> (required)</li>
                            <li><strong>ISBN</strong> (required, must be unique)</li>
                            <li><strong>Selling Price</strong> (customer price)</li>
                            <li><strong>Cost Price</strong> (wholesale/purchase price)</li>
                        </ul>
                        
                        <div class="feature-highlight">
                            <h4><i class="fas fa-calculator"></i> Automatic Profit Calculation</h4>
                            <p>The system automatically calculates profit margins based on cost and selling prices.</p>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-barcode"></i>
                        </div>
                        <h3 class="card-title">ISBN Management</h3>
                    </div>
                    <div class="card-content">
                        <p>ISBN handling guidelines:</p>
                        <ul>
                            <li>Supports both ISBN-10 and ISBN-13 formats</li>
                            <li>ISBNs must be unique in the system</li>
                            <li>Remove hyphens when entering ISBNs</li>
                            <li>Used for barcode scanning during invoicing</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-exclamation-triangle"></i>
                            <div class="tip-content">
                                Double-check ISBN accuracy - incorrect ISBNs will cause barcode scanning issues.
                            </div>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-edit"></i>
                        </div>
                        <h3 class="card-title">Price Management</h3>
                    </div>
                    <div class="card-content">
                        <p>Best practices for pricing:</p>
                        <ul>
                            <li>Update cost prices when restocking</li>
                            <li>Monitor profit margins regularly</li>
                            <li>Consider loyalty discounts when setting prices</li>
                            <li>Use the profit margin calculator for guidance</li>
                        </ul>
                        
                        <div class="feature-highlight">
                            <h4><i class="fas fa-chart-line"></i> Profit Tracking</h4>
                            <p>View profit margins in real-time and track inventory value in the statistics dashboard.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Management Features Section -->
        <div class="help-section" id="management">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fas fa-cog"></i>
                </div>
                <div>
                    <h2 class="section-title">Management Features</h2>
                </div>
            </div>

            <div class="help-grid">
                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-users-cog"></i>
                        </div>
                        <h3 class="card-title">Staff Management</h3>
                    </div>
                    <div class="card-content">
                        <p>Manager and Admin features:</p>
                        <ul>
                            <li>Add new staff members</li>
                            <li>Assign roles and permissions</li>
                            <li>Create login accounts for staff</li>
                            <li>Reset staff passwords</li>
                            <li>View staff activity and performance</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-shield-alt"></i>
                            <div class="tip-content">
                                Staff can be added without login credentials for record-keeping purposes.
                            </div>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-sliders-h"></i>
                        </div>
                        <h3 class="card-title">System Settings</h3>
                    </div>
                    <div class="card-content">
                        <p>Configurable system parameters:</p>
                        <ul>
                            <li><strong>Loyalty Points:</strong> Points per Rs. 100 spent</li>
                            <li><strong>Tier Thresholds:</strong> Points required for each tier</li>
                            <li><strong>Discount Rates:</strong> Percentage discounts for each tier</li>
                            <li><strong>System Preferences:</strong> Email notifications, print settings</li>
                        </ul>
                        
                        <div class="feature-highlight">
                            <h4><i class="fas fa-sync-alt"></i> Real-time Updates</h4>
                            <p>Settings changes apply immediately to all new transactions and calculations.</p>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-chart-bar"></i>
                        </div>
                        <h3 class="card-title">Reports & Analytics</h3>
                    </div>
                    <div class="card-content">
                        <p>Available reporting features:</p>
                        <ul>
                            <li>Daily and monthly revenue reports</li>
                            <li>Invoice counts and transaction volume</li>
                            <li>Client tier distribution</li>
                            <li>Book inventory valuation</li>
                            <li>Staff performance metrics</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-calendar-alt"></i>
                            <div class="tip-content">
                                Dashboard statistics update in real-time as new transactions are processed.
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- FAQ Section -->
        <div class="help-section" id="faq">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fas fa-question"></i>
                </div>
                <div>
                    <h2 class="section-title">Frequently Asked Questions</h2>
                </div>
            </div>

            <div class="faq-container">
                <div class="faq-item">
                    <button class="faq-question" onclick="toggleFaq(this)">
                        How do I reset a forgotten password?
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    <div class="faq-answer">
                        <p>Password resets can only be done by Managers or Administrators:</p>
                        <ol>
                            <li>Contact your Manager or Administrator</li>
                            <li>They can access the Manager Dashboard → Staff Management</li>
                            <li>Click the key icon next to your name to reset your password</li>
                            <li>You'll receive a new temporary password to use for login</li>
                        </ol>
                        <p><strong>Note:</strong> Change your password immediately after receiving the reset.</p>
                    </div>
                </div>

                <div class="faq-item">
                    <button class="faq-question" onclick="toggleFaq(this)">
                        Why can't I delete an invoice?
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    <div class="faq-answer">
                        <p>Invoice deletion has specific rules for data integrity:</p>
                        <ul>
                            <li>Only Managers and Administrators can delete invoices</li>
                            <li>Deleting an invoice will also remove loyalty points earned from that transaction</li>
                            <li>Consider carefully before deletion as this action cannot be undone</li>
                            <li>For record-keeping, it's better to keep invoices and add notes if needed</li>
                        </ul>
                    </div>
                </div>

                <div class="faq-item">
                    <button class="faq-question" onclick="toggleFaq(this)">
                        How does the loyalty program calculate points?
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    <div class="faq-answer">
                        <p>Loyalty points are calculated using this formula:</p>
                        <div style="background: #f3f4f6; padding: 15px; border-radius: 8px; margin: 10px 0; font-family: monospace;">
                            Points = (Total Amount ÷ 100) × Points Per Rs. 100
                        </div>
                        <p>Default setting is 1 point per Rs. 100 spent. For example:</p>
                        <ul>
                            <li>Purchase of Rs. 500 = 5 loyalty points</li>
                            <li>Purchase of Rs. 1,250 = 12 loyalty points (rounded down)</li>
                        </ul>
                        <p><strong>Tier Upgrades:</strong> Points are added immediately, and tier status is recalculated automatically.</p>
                    </div>
                </div>

                <div class="faq-item">
                    <button class="faq-question" onclick="toggleFaq(this)">
                        Can I edit book prices after adding them?
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    <div class="faq-answer">
                        <p>Yes, book prices can be updated anytime:</p>
                        <ol>
                            <li>Go to Books section in the navigation menu</li>
                            <li>Find the book you want to edit</li>
                            <li>Click the edit (pencil) icon on the book card</li>
                            <li>Update the selling price and/or cost price</li>
                            <li>The profit margin will be recalculated automatically</li>
                        </ol>
                        <p><strong>Important:</strong> Price changes only affect new invoices, not existing ones.</p>
                    </div>
                </div>

                <div class="faq-item">
                    <button class="faq-question" onclick="toggleFaq(this)">
                        What happens if I enter the wrong ISBN?
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    <div class="faq-answer">
                        <p>ISBN errors can cause scanning issues during invoice creation:</p>
                        <ul>
                            <li><strong>Duplicate ISBN:</strong> System will reject the entry</li>
                            <li><strong>Invalid Format:</strong> Use only numbers, remove hyphens</li>
                            <li><strong>Wrong ISBN:</strong> Edit the book to correct it</li>
                            <li><strong>Scanning Issues:</strong> Verify ISBN matches the physical book</li>
                        </ul>
                        <p>Always double-check ISBNs before saving to avoid future problems.</p>
                    </div>
                </div>

                <div class="faq-item">
                    <button class="faq-question" onclick="toggleFaq(this)">
                        How do I handle walk-in customers vs registered clients?
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    <div class="faq-answer">
                        <p>The system supports both types of customers:</p>
                        
                        <h4>Walk-in Customers:</h4>
                        <ul>
                            <li>No loyalty discounts applied</li>
                            <li>No loyalty points earned</li>
                            <li>Faster checkout process</li>
                            <li>Good for one-time purchases</li>
                        </ul>
                        
                        <h4>Registered Clients:</h4>
                        <ul>
                            <li>Automatic loyalty discounts based on tier</li>
                            <li>Earn loyalty points with each purchase</li>
                            <li>Purchase history tracking</li>
                            <li>Email notifications (if enabled)</li>
                        </ul>
                        
                        <p><strong>Tip:</strong> Encourage regular customers to register for loyalty benefits.</p>
                    </div>
                </div>

                <div class="faq-item">
                    <button class="faq-question" onclick="toggleFaq(this)">
                        What should I do if the system is running slowly?
                        <i class="fas fa-chevron-down"></i>
                    </button>
                    <div class="faq-answer">
                        <p>Try these troubleshooting steps:</p>
                        <ol>
                            <li><strong>Refresh the page:</strong> Press <span class="shortcut-key">F5</span> or <span class="shortcut-key">Ctrl+R</span></li>
                            <li><strong>Clear browser cache:</strong> Use <span class="shortcut-key">Ctrl+Shift+Del</span></li>
                            <li><strong>Check internet connection:</strong> Ensure stable connectivity</li>
                            <li><strong>Close unnecessary tabs:</strong> Free up browser memory</li>
                            <li><strong>Restart browser:</strong> Complete restart if issues persist</li>
                        </ol>
                        <p><strong>If problems continue:</strong> Contact your system administrator.</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Keyboard Shortcuts Section -->
        <div class="help-section">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fas fa-keyboard"></i>
                </div>
                <div>
                    <h2 class="section-title">Keyboard Shortcuts</h2>
                </div>
            </div>

            <div class="help-grid">
                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-bolt"></i>
                        </div>
                        <h3 class="card-title">General Navigation</h3>
                    </div>
                    <div class="card-content">
                        <ul>
                            <li><span class="shortcut-key">Tab</span> - Navigate between form fields</li>
                            <li><span class="shortcut-key">Enter</span> - Submit forms or confirm actions</li>
                            <li><span class="shortcut-key">Esc</span> - Close modals or cancel operations</li>
                            <li><span class="shortcut-key">F5</span> - Refresh page</li>
                        </ul>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-search"></i>
                        </div>
                        <h3 class="card-title">Search & Selection</h3>
                    </div>
                    <div class="card-content">
                        <ul>
                            <li><span class="shortcut-key">Ctrl+F</span> - Find on page</li>
                            <li><span class="shortcut-key">Enter</span> - Select search result</li>
                            <li><span class="shortcut-key">Arrow Keys</span> - Navigate search results</li>
                        </ul>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-print"></i>
                        </div>
                        <h3 class="card-title">Printing</h3>
                    </div>
                    <div class="card-content">
                        <ul>
                            <li><span class="shortcut-key">Ctrl+P</span> - Print current page</li>
                            <li><span class="shortcut-key">Ctrl+S</span> - Save as PDF (in print dialog)</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- Contact & Support Section -->
        <div class="help-section">
            <div class="section-header">
                <div class="section-icon">
                    <i class="fas fa-life-ring"></i>
                </div>
                <div>
                    <h2 class="section-title">Support & Contact</h2>
                </div>
            </div>

            <div class="help-grid">
                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-user-shield"></i>
                        </div>
                        <h3 class="card-title">System Administration</h3>
                    </div>
                    <div class="card-content">
                        <p>For technical support and system issues:</p>
                        <ul>
                            <li>Password resets and account issues</li>
                            <li>Permission and access problems</li>
                            <li>System configuration changes</li>
                            <li>Data backup and recovery</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-phone"></i>
                            <div class="tip-content">
                                Contact your local system administrator or IT support team for technical assistance.
                            </div>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-graduation-cap"></i>
                        </div>
                        <h3 class="card-title">Training Resources</h3>
                    </div>
                    <div class="card-content">
                        <p>Additional learning resources:</p>
                        <ul>
                            <li>Practice with sample data in test mode</li>
                            <li>Review this help documentation regularly</li>
                            <li>Ask experienced colleagues for tips</li>
                            <li>Attend training sessions when available</li>
                        </ul>
                        
                        <div class="feature-highlight">
                            <h4><i class="fas fa-bookmark"></i> Bookmark This Page</h4>
                            <p>Save this help page for quick reference during your daily work.</p>
                        </div>
                    </div>
                </div>

                <div class="help-card">
                    <div class="card-header">
                        <div class="card-icon">
                            <i class="fas fa-exclamation-triangle"></i>
                        </div>
                        <h3 class="card-title">Emergency Procedures</h3>
                    </div>
                    <div class="card-content">
                        <p>In case of system emergencies:</p>
                        <ul>
                            <li><strong>System Down:</strong> Use manual invoice books temporarily</li>
                            <li><strong>Data Loss:</strong> Contact administrator immediately</li>
                            <li><strong>Suspected Security Breach:</strong> Report immediately and change passwords</li>
                            <li><strong>Hardware Issues:</strong> Have backup devices ready</li>
                        </ul>
                        
                        <div class="alert-tip">
                            <i class="fas fa-shield-alt"></i>
                            <div class="tip-content">
                                Regular data backups are essential. Ensure automatic backups are configured and tested.
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<script>
// Search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('helpSearch');
    const helpSections = document.querySelectorAll('.help-section');
    const helpCards = document.querySelectorAll('.help-card');

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase().trim();
        
        if (searchTerm === '') {
            // Show all sections and cards
            helpSections.forEach(section => section.style.display = 'block');
            helpCards.forEach(card => {
                card.style.display = 'block';
                card.style.backgroundColor = '';
            });
        } else {
            // Search through content
            helpSections.forEach(section => {
                let sectionHasMatch = false;
                const cards = section.querySelectorAll('.help-card');
                
                cards.forEach(card => {
                    const cardText = card.textContent.toLowerCase();
                    const hasMatch = cardText.includes(searchTerm);
                    
                    if (hasMatch) {
                        card.style.display = 'block';
                        card.style.backgroundColor = '#fff3cd';
                        sectionHasMatch = true;
                    } else {
                        card.style.display = 'none';
                        card.style.backgroundColor = '';
                    }
                });
                
                section.style.display = sectionHasMatch ? 'block' : 'none';
            });
        }
    });
});

// FAQ toggle functionality
function toggleFaq(button) {
    const faqItem = button.parentElement;
    const answer = faqItem.querySelector('.faq-answer');
    const icon = button.querySelector('i');
    
    // Toggle answer visibility
    answer.classList.toggle('active');
    
    // Rotate icon
    if (answer.classList.contains('active')) {
        icon.classList.remove('fa-chevron-down');
        icon.classList.add('fa-chevron-up');
    } else {
        icon.classList.remove('fa-chevron-up');
        icon.classList.add('fa-chevron-down');
    }
}

// Smooth scrolling for navigation links
document.querySelectorAll('.help-nav-links a').forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        const targetId = this.getAttribute('href').substring(1);
        const targetSection = document.getElementById(targetId);
        
        if (targetSection) {
            targetSection.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Highlight current section in navigation
window.addEventListener('scroll', function() {
    const navLinks = document.querySelectorAll('.help-nav-links a');
    const sections = document.querySelectorAll('.help-section');
    
    let currentSection = '';
    sections.forEach(section => {
        const rect = section.getBoundingClientRect();
        if (rect.top <= 100 && rect.bottom >= 100) {
            currentSection = section.id;
        }
    });
    
    navLinks.forEach(link => {
        link.style.backgroundColor = '';
        link.style.color = '';
        if (link.getAttribute('href') === '#' + currentSection) {
            link.style.backgroundColor = 'rgba(255, 255, 255, 0.3)';
            link.style.color = 'white';
        }
    });
});
</script>

</body>
</html>