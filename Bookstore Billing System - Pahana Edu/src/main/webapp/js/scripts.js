// Edit Book Logic
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

// Modal close function
function closeModal() {
    document.getElementById('bookModal').style.display = 'none';
}

// Confirm delete function
function confirmDelete() {
    return confirm('Are you sure you want to delete this book? This action cannot be undone.');
}
