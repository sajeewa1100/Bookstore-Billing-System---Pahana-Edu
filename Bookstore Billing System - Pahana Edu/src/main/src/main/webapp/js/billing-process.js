// Billing Process JavaScript

class BillingProcess {
    constructor() {
        this.modal = document.getElementById('invoiceModal');
        this.form = document.getElementById('invoiceForm');
        this.itemsContainer = document.getElementById('itemsContainer');
        this.itemCounter = 0;
        this.selectedClient = null;
        this.debounceTimer = null;
        
        this.init();
    }
    
    init() {
        this.bindEvents();
        this.loadInitialData();
    }
    
    bindEvents() {
        // Modal controls
        document.getElementById('newInvoiceBtn').addEventListener('click', () => this.openModal());
        document.querySelector('.close').addEventListener('click', () => this.closeModal());
        document.querySelector('.btn-cancel').addEventListener('click', () => this.closeModal());
        
        // Form submission
        document.getElementById('saveInvoiceBtn').addEventListener('click', () => this.saveInvoice());
        
        // Client search
        document.getElementById('clientSearch').addEventListener('input', (e) => this.searchClients(e.target.value));
        
        // Add item button
        document.getElementById('addItemBtn').addEventListener('click', () => this.addItemRow());
        
        // Cash calculation
        document.getElementById('cashGiven').addEventListener('input', () => this.calculateChange());
        
        // Refresh button
        document.getElementById('refreshBtn').addEventListener('click', () => this.refreshPage());
        
        // Close modal on outside click
        window.addEventListener('click', (e) => {
            if (e.target === this.modal) {
                this.closeModal();
            }
        });
    }
    
    loadInitialData() {
        // Add initial item row
        this.addItemRow();
    }
    
    openModal(invoiceData = null) {
        if (invoiceData) {
            // Edit mode
            document.getElementById('modalTitle').innerHTML = '<i class="fas fa-edit"></i> Edit Invoice';
            this.populateFormForEdit(invoiceData);
        } else {
            // New invoice mode
            document.getElementById('modalTitle').innerHTML = '<i class="fas fa-receipt"></i> New Invoice';
            this.resetForm();
            this.addItemRow();
        }
        
        this.modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
        
        // Focus first input
        setTimeout(() => {
            document.getElementById('clientSearch').focus();
        }, 100);
    }
    
    closeModal() {
        this.modal.style.display = 'none';
        document.body.style.overflow = 'auto';
        this.resetForm();
    }
    
    resetForm() {
        this.form.reset();
        this.selectedClient = null;
        this.itemsContainer.innerHTML = '';
        this.itemCounter = 0;
        this.updateSelectedClientDisplay();
        this.updateCalculations();
        document.getElementById('clientResults').style.display = 'none';
    }
    
    searchClients(searchTerm) {
        clearTimeout(this.debounceTimer);
        
        if (searchTerm.length < 2) {
            document.getElementById('clientResults').style.display = 'none';
            return;
        }
        
        this.debounceTimer = setTimeout(() => {
            this.performClientSearch(searchTerm);
        }, 300);
    }
    
    async performClientSearch(searchTerm) {
        try {
            const response = await fetch(`/billing-process?action=searchClients&searchTerm=${encodeURIComponent(searchTerm)}`);
            const data = await response.json();
            
            if (data.success) {
                this.displayClientResults(data.clients);
            } else {
                console.error('Client search failed:', data.message);
            }
        } catch (error) {
            console.error('Error searching clients:', error);
        }
    }
    
    displayClientResults(clients) {
        const resultsContainer = document.getElementById('clientResults');
        resultsContainer.innerHTML = '';
        
        if (clients.length === 0) {
            resultsContainer.innerHTML = '<div class="search-result-item">No customers found</div>';
        } else {
            clients.forEach(client => {
                const item = document.createElement('div');
                item.className = 'search-result-item';
                item.innerHTML = `
                    <div><strong>${client.fullName}</strong></div>
                    <div style="font-size: 0.9em; color: #718096;">
                        ${client.phone} • ${client.tierLevel} (${client.loyaltyPoints} pts)
                    </div>
                `;
                item.addEventListener('click', () => this.selectClient(client));
                resultsContainer.appendChild(item);
            });
        }
        
        resultsContainer.style.display = 'block';
    }
    
    selectClient(client) {
        this.selectedClient = client;
        document.getElementById('selectedClientId').value = client.id;
        document.getElementById('clientSearch').value = client.fullName;
        document.getElementById('clientResults').style.display = 'none';
        this.updateSelectedClientDisplay();
        this.updateCalculations();
    }
    
    updateSelectedClientDisplay() {
        const display = document.getElementById('selectedClientInfo');
        if (this.selectedClient) {
            display.innerHTML = `
                <div><strong>${this.selectedClient.fullName}</strong></div>
                <div style="font-size: 0.9em; color: #718096;">
                    ${this.selectedClient.phone} • ${this.selectedClient.tierLevel} • ${this.selectedClient.loyaltyPoints} pts
                </div>
            `;
        } else {
            display.innerHTML = '<span class="no-selection">No customer selected (Walk-in)</span>';
        }
    }
    
    addItemRow() {
        const row = document.createElement('div');
        row.className = 'item-row';
        row.dataset.itemId = this.itemCounter++;
        
        row.innerHTML = `
            <div class="item-search-container">
                <label>Book:</label>
                <input type="text" class="book-search" placeholder="Search books..." onInput="billingProcess.searchBooks(this)">
                <div class="search-results book-results"></div>
                <input type="hidden" name="bookId[]" class="book-id">
                <input type="hidden" name="unitPrice[]" class="unit-price">
            </div>
            <div>
                <label>Quantity:</label>
                <input type="number" name="quantity[]" class="quantity-input" value="1" min="1" onInput="billingProcess.updateCalculations()">
            </div>
            <div>
                <label>Price:</label>
                <input type="text" class="item-total" readonly placeholder="0.00">
            </div>
            <div>
                <label>Total:</label>
                <input type="text" class="line-total" readonly placeholder="0.00">
            </div>
            <div>
                <button type="button" class="item-remove-btn" onClick="billingProcess.removeItemRow(this)">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        `;
        
        this.itemsContainer.appendChild(row);
    }
    
    removeItemRow(button) {
        const row = button.closest('.item-row');
        row.remove();
        this.updateCalculations();
    }
    
    async searchBooks(input) {
        const searchTerm = input.value;
        const resultsContainer = input.parentNode.querySelector('.book-results');
        
        if (searchTerm.length < 2) {
            resultsContainer.style.display = 'none';
            return;
        }
        
        try {
            const response = await fetch(`/billing-process?action=searchBooks&searchTerm=${encodeURIComponent(searchTerm)}`);
            const data = await response.json();
            
            if (data.success) {
                this.displayBookResults(data.books, resultsContainer, input);
            }
        } catch (error) {
            console.error('Error searching books:', error);
        }
    }
    
    displayBookResults(books, container, input) {
        container.innerHTML = '';
        
        if (books.length === 0) {
            container.innerHTML = '<div class="search-result-item">No books found</div>';
        } else {
            books.forEach(book => {
                const item = document.createElement('div');
                item.className = 'search-result-item';
                item.innerHTML = `
                    <div><strong>${book.title}</strong></div>
                    <div style="font-size: 0.9em; color: #718096;">
                        by ${book.author} • Rs. ${book.price}
                    </div>
                `;
                item.addEventListener('click', () => this.selectBook(book, input));
                container.appendChild(item);
            });
        }
        
        container.style.display = 'block';
    }
    
    selectBook(book, input) {
        const row = input.closest('.item-row');
        
        // Update input fields
        input.value = book.title;
        row.querySelector('.book-id').value = book.id;
        row.querySelector('.unit-price').value = book.price;
        row.querySelector('.item-total').value = `Rs. ${parseFloat(book.price).toFixed(2)}`;
        
        // Hide results
        row.querySelector('.book-results').style.display = 'none';
        
        // Update calculations
        this.updateCalculations();
    }
    
    async updateCalculations() {
        const items = this.getInvoiceItems();
        
        if (items.length === 0) {
            this.displayCalculations({
                subtotal: 0,
                loyaltyDiscount: 0,
                totalAmount: 0,
                loyaltyPointsEarned: 0
            });
            return;
        }
        
        try {
            const formData = new FormData();
            formData.append('action', 'calculateInvoice');
            
            if (this.selectedClient) {
                formData.append('clientId', this.selectedClient.id);
            }
            
            items.forEach(item => {
                formData.append('bookId[]', item.bookId);
                formData.append('quantity[]', item.quantity);
                formData.append('unitPrice[]', item.unitPrice);
            });
            
            const response = await fetch('/billing-process', {
                method: 'POST',
                body: formData
            });
            
            const data = await response.json();
            
            if (data.success) {
                this.displayCalculations(data);
                this.updateItemTotals();
            }
        } catch (error) {
            console.error('Error calculating invoice:', error);
        }
    }
    
    getInvoiceItems() {
        const items = [];
        const rows = document.querySelectorAll('.item-row');
        
        rows.forEach(row => {
            const bookId = row.querySelector('.book-id').value;
            const quantity = row.querySelector('.quantity-input').value;
            const unitPrice = row.querySelector('.unit-price').value;
            
            if (bookId && quantity && unitPrice) {
                items.push({
                    bookId: bookId,
                    quantity: parseInt(quantity),
                    unitPrice: parseFloat(unitPrice)
                });
            }
        });
        
        return items;
    }
    
    displayCalculations(calc) {
        document.getElementById('calculatedSubtotal').textContent = `Rs. ${parseFloat(calc.subtotal || 0).toFixed(2)}`;
        document.getElementById('calculatedDiscount').textContent = `Rs. ${parseFloat(calc.loyaltyDiscount || 0).toFixed(2)}`;
        document.getElementById('calculatedTotal').textContent = `Rs. ${parseFloat(calc.totalAmount || 0).toFixed(2)}`;
        document.getElementById('calculatedPoints').textContent = calc.loyaltyPointsEarned || 0;
        
        // Update hidden fields
        document.getElementById('subtotal').value = calc.subtotal || 0;
        document.getElementById('loyaltyDiscount').value = calc.loyaltyDiscount || 0;
        document.getElementById('totalAmount').value = calc.totalAmount || 0;
        document.getElementById('loyaltyPointsEarned').value = calc.loyaltyPointsEarned || 0;
        
        this.calculateChange();
    }
    
    updateItemTotals() {
        const rows = document.querySelectorAll('.item-row');
        
        rows.forEach(row => {
            const quantity = parseInt(row.querySelector('.quantity-input').value) || 0;
            const unitPrice = parseFloat(row.querySelector('.unit-price').value) || 0;
            const total = quantity * unitPrice;
            
            row.querySelector('.line-total').value = `Rs. ${total.toFixed(2)}`;
        });
    }
    
    calculateChange() {
        const totalAmount = parseFloat(document.getElementById('totalAmount').value) || 0;
        const cashGiven = parseFloat(document.getElementById('cashGiven').value) || 0;
        const change = Math.max(0, cashGiven - totalAmount);
        
        document.getElementById('changeAmount').value = `Rs. ${change.toFixed(2)}`;
    }
    
    async saveInvoice() {
        if (!this.validateForm()) {
            return;
        }
        
        this.showLoading(true);
        
        try {
            const formData = new FormData(this.form);
            formData.append('action', 'createInvoice');
            
            const response = await fetch('/billing-process', {
                method: 'POST',
                body: formData
            });
            
            const data = await response.json();
            
            if (data.success) {
                this.showSuccess('Invoice created successfully!');
                setTimeout(() => {
                    this.closeModal();
                    this.refreshPage();
                }, 1500);
            } else {
                this.showError(data.message || 'Failed to create invoice');
            }
        } catch (error) {
            console.error('Error saving invoice:', error);
            this.showError('Error saving invoice. Please try again.');
        } finally {
            this.showLoading(false);
        }
    }
    
    validateForm() {
        const items = this.getInvoiceItems();
        
        if (items.length === 0) {
            this.showError('Please add at least one item to the invoice.');
            return false;
        }
        
        const totalAmount = parseFloat(document.getElementById('totalAmount').value) || 0;
        const cashGiven = parseFloat(document.getElementById('cashGiven').value) || 0;
        
        if (cashGiven > 0 && cashGiven < totalAmount) {
            this.showError('Cash given cannot be less than the total amount.');
            return false;
        }
        
        return true;
    }
    
    showLoading(show) {
        document.getElementById('loadingOverlay').style.display = show ? 'flex' : 'none';
    }
    
    showSuccess(message) {
        this.showAlert(message, 'success');
    }
    
    showError(message) {
        this.showAlert(message, 'error');
    }
    
    showAlert(message, type) {
        // Remove existing alerts
        document.querySelectorAll('.alert').forEach(alert => alert.remove());
        
        const alert = document.createElement('div');
        alert.className = `alert alert-${type}`;
        alert.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
            ${message}
        `;
        
        const main = document.querySelector('.main-content');
        main.insertBefore(alert, main.firstChild);
        
        // Auto-hide after 5 seconds
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    }
    
    refreshPage() {
        window.location.reload();
    }
    
    populateFormForEdit(invoiceData) {
        // Implementation for edit mode
        // This would be called when editing an existing invoice
        console.log('Edit mode not fully implemented yet', invoiceData);
    }
}

// Global functions for inline event handlers
function editInvoice(invoiceId) {
    console.log('Edit invoice:', invoiceId);
    // Implement edit functionality
}

function printInvoice(invoiceId) {
    const printUrl = `/billing-process?action=print&id=${invoiceId}`;
    window.open(printUrl, '_blank');
}

// Initialize when DOM is ready
let billingProcess;
document.addEventListener('DOMContentLoaded', function() {
    billingProcess = new BillingProcess();
});