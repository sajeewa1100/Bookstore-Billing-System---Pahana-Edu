<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Books - Pahana Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
</head>
<body>

<%-- Include Sidebar --%>
<jsp:include page="sidebar.jsp" flush="true" />

<main class="main-content">
    <div class="top-bar">
        <h2 class="section-title">📚 Book Section</h2>
        <a href="javascript:void(0);" onclick="openAddModal();" onclick="openAddModal();" class="btn-add-book">
            <i class="fas fa-plus"></i> Add New Book
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

    <!-- Categories Filter - COMPLETE FIXED VERSION -->
<div class="filter-section">
    <form action="${pageContext.request.contextPath}/BookServlet" method="get">
        <input type="hidden" name="action" value="books" />
        <label for="category">Filter by Category:</label>
        <select name="category" id="category">
            <option value="">-- All Categories --</option>
            <c:forEach var="category" items="${categories}">
                <option value="${category}" 
                    <c:if test="${selectedCategory eq category}">selected</c:if>>${category}
                </option>
            </c:forEach>
        </select>
        <button type="submit" class="btn-filter">
            <i class="fas fa-filter"></i> Filter
        </button>
    </form>
</div>

    <!-- Book List -->
    <div class="book-list">
        <c:choose>
            <c:when test="${not empty books}">
                <table class="book-table">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Author</th>
                            <th>Category</th>
                            <th>ISBN</th>
                            <th>Publisher</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="book" items="${books}">
                            <tr>
                                <td>${book.title}</td>
                                <td>${book.author}</td>
                                <td>${book.category}</td>
                                <td>${book.isbn}</td>
                                <td>${book.publisher}</td>
                                <td>Rs. ${book.price}</td>
                                <td>${book.quantity}</td>
                                <td>
                                    <button type="button" class="btn-edit" onclick="editBook('${book.id}', '${book.title}', '${book.author}', '${book.category}', '${book.isbn}', '${book.publisher}', '${book.price}', '${book.quantity}')">
                                        <i class="fas fa-edit"></i> Edit
                                    </button>
                                    <form action="${pageContext.request.contextPath}/BookServlet" method="post" style="display: inline;" onsubmit="return confirmDelete();">
                                        <input type="hidden" name="action" value="delete" />
                                        <input type="hidden" name="id" value="${book.id}" />
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
                    <i class="fas fa-book" style="font-size: 48px; margin-bottom: 20px; color: #ccc;"></i>
                    <h3>No books available</h3>
                    <p>Click "Add New Book" to add your first book to the collection.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Add Book Modal -->
    <div id="bookModal" class="modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="modalTitle">Add New Book</h3>
                <span class="close" onclick="closeModal()">&times;</span>
            </div>
            <hr />
            
            <form action="${pageContext.request.contextPath}/BookServlet" method="post" id="bookForm">
                <input type="hidden" name="action" value="add" id="formAction" />
                <input type="hidden" name="id" id="bookId" />

                <div class="form-group">
                    <label for="title">Title:</label>
                    <input type="text" id="title" name="title" required placeholder="Enter book title" />
                </div>

                <div class="form-group">
                    <label for="author">Author:</label>
                    <input type="text" id="author" name="author" required placeholder="Enter author name" />
                </div>

                <div class="form-group">
                    <label for="modalCategory">Category:</label>
                    <select id="modalCategory" name="category" required>
                        <option value="">-- Select Category --</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category}">${category}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="isbn">ISBN:</label>
                    <input type="text" id="isbn" name="isbn" placeholder="Enter ISBN (optional)" />
                </div>

                <div class="form-group">
                    <label for="publisher">Publisher:</label>
                    <input type="text" id="publisher" name="publisher" placeholder="Enter publisher name (optional)" />
                </div>

                <div class="form-group">
                    <label for="price">Price (Rs.):</label>
                    <input type="number" id="price" step="0.01" name="price" required placeholder="0.00" min="0" />
                </div>

                <div class="form-group">
                    <label for="quantity">Quantity:</label>
                    <input type="number" id="quantity" name="quantity" required placeholder="0" min="0" />
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save" id="saveBtn">
                        <i class="fas fa-save"></i> Save Book
                    </button>
                </div>
            </form>
        </div>
    </div>
</main>

<script>
    // Modal functions
    function openAddModal() {
        document.getElementById('modalTitle').textContent = 'Add New Book';
        document.getElementById('formAction').value = 'add';
        document.getElementById('bookId').value = '';
        document.getElementById('saveBtn').innerHTML = '<i class="fas fa-save"></i> Save Book';
        
        // Clear form
        document.getElementById('bookForm').reset();
        
        document.getElementById('bookModal').style.display = 'block';
    }

    function editBook(id, title, author, category, isbn, publisher, price, quantity) {
        document.getElementById('modalTitle').textContent = 'Edit Book';
        document.getElementById('formAction').value = 'update';
        document.getElementById('bookId').value = id;
        document.getElementById('saveBtn').innerHTML = '<i class="fas fa-save"></i> Update Book';
        
        // Fill form with book data
        document.getElementById('title').value = title;
        document.getElementById('author').value = author;
        document.getElementById('modalCategory').value = category;
        document.getElementById('isbn').value = isbn || '';
        document.getElementById('publisher').value = publisher || '';
        document.getElementById('price').value = price;
        document.getElementById('quantity').value = quantity;
        
        document.getElementById('bookModal').style.display = 'block';
    }

    function closeModal() {
        document.getElementById('bookModal').style.display = 'none';
    }

    function confirmDelete() {
        return confirm('Are you sure you want to delete this book? This action cannot be undone.');
    }

    // Close modal when clicking outside
    window.onclick = function(event) {
        var modal = document.getElementById('bookModal');
        if (event.target == modal) {
            closeModal();
        }
    }

    // Form validation
    document.getElementById('bookForm').addEventListener('submit', function(e) {
        var price = parseFloat(document.getElementById('price').value);
        var quantity = parseInt(document.getElementById('quantity').value);
        
        if (price < 0) {
            alert('Price cannot be negative');
            e.preventDefault();
            return false;
        }
        
        if (quantity < 0) {
            alert('Quantity cannot be negative');
            e.preventDefault();
            return false;
        }
        
        if (isNaN(price)) {
            alert('Please enter a valid price');
            e.preventDefault();
            return false;
        }
        
        if (isNaN(quantity)) {
            alert('Please enter a valid quantity');
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
            closeModal();
        }
    });
</script>

</body>
</html>
