/**
 * Enhanced Session Management JavaScript
 * Handles session timeout, auto-logout, session extension, and keep-alive functionality
 */

class SessionManager {
    constructor(options = {}) {
        this.options = {
            sessionTimeout: options.sessionTimeout || 30, // minutes
            warningTime: options.warningTime || 5, // minutes before timeout
            keepAliveInterval: options.keepAliveInterval || 5, // minutes
            autoLogoutUrl: options.autoLogoutUrl || '/AuthServlet?action=logout',
            extendSessionUrl: options.extendSessionUrl || '/AuthServlet?action=extendSession',
            loginUrl: options.loginUrl || '/views/login.jsp',
            ...options
        };

        this.warningTimer = null;
        this.logoutTimer = null;
        this.keepAliveTimer = null;
        this.lastActivity = Date.now();
        this.isWarningShown = false;
        this.sessionActive = true;

        this.init();
    }

    init() {
        this.setupActivityTracking();
        this.setupKeepAlive();
        this.startSessionTimer();
        this.createWarningModal();

        console.log('✅ SessionManager initialized');
        console.log(`Session timeout: ${this.options.sessionTimeout} minutes`);
        console.log(`Warning time: ${this.options.warningTime} minutes before timeout`);
    }

    setupActivityTracking() {
        // Track user activity
        const activityEvents = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
        
        activityEvents.forEach(event => {
            document.addEventListener(event, () => {
                this.updateActivity();
            }, { passive: true });
        });

        // Track AJAX requests
        this.interceptAjaxRequests();
    }

    interceptAjaxRequests() {
        // Intercept fetch requests
        const originalFetch = window.fetch;
        window.fetch = (...args) => {
            this.updateActivity();
            return originalFetch.apply(this, args)
                .then(response => {
                    this.handleApiResponse(response);
                    return response;
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    throw error;
                });
        };

        // Intercept XMLHttpRequest
        const originalOpen = XMLHttpRequest.prototype.open;
        XMLHttpRequest.prototype.open = function(...args) {
            this.addEventListener('load', () => {
                sessionManager.handleApiResponse(this);
            });
            this.addEventListener('error', () => {
                console.error('XHR error:', this.statusText);
            });
            return originalOpen.apply(this, args);
        };
    }

    handleApiResponse(response) {
        // Handle session expiry from API responses
        if (response.status === 401 || response.status === 403) {
            this.handleSessionExpiry();
        }
    }

    updateActivity() {
        this.lastActivity = Date.now();
        
        // If warning is shown and user is active, hide it
        if (this.isWarningShown) {
            this.hideSessionWarning();
            this.resetSessionTimer();
        }
    }

    setupKeepAlive() {
        // Send keep-alive requests periodically
        this.keepAliveTimer = setInterval(() => {
            if (this.sessionActive) {
                this.sendKeepAlive();
            }
        }, this.options.keepAliveInterval * 60 * 1000);
    }

    async sendKeepAlive() {
        try {
            const response = await fetch('/AuthServlet?action=extendSession', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'keepAlive=true'
            });

            if (!response.ok) {
                console.warn('Keep-alive request failed');
                if (response.status === 401) {
                    this.handleSessionExpiry();
                }
            } else {
                const result = await response.json();
                if (!result.success) {
                    this.handleSessionExpiry();
                }
            }
        } catch (error) {
            console.error('Keep-alive error:', error);
        }
    }

    startSessionTimer() {
        this.clearTimers();

        const warningTimeMs = (this.options.sessionTimeout - this.options.warningTime) * 60 * 1000;
        const logoutTimeMs = this.options.sessionTimeout * 60 * 1000;

        // Set warning timer
        this.warningTimer = setTimeout(() => {
            this.showSessionWarning();
        }, warningTimeMs);

        // Set logout timer
        this.logoutTimer = setTimeout(() => {
            this.autoLogout();
        }, logoutTimeMs);

        console.log(`⏰ Session timers set: Warning in ${this.options.sessionTimeout - this.options.warningTime}min, Logout in ${this.options.sessionTimeout}min`);
    }

    resetSessionTimer() {
        if (this.sessionActive) {
            this.startSessionTimer();
            console.log('🔄 Session timer reset');
        }
    }

    showSessionWarning() {
        if (!this.sessionActive) return;

        this.isWarningShown = true;
        const modal = document.getElementById('sessionWarningModal');
        if (modal) {
            modal.style.display = 'block';
            this.startWarningCountdown();
        }

        console.log('⚠️ Session warning shown');
    }

    hideSessionWarning() {
        this.isWarningShown = false;
        const modal = document.getElementById('sessionWarningModal');
        if (modal) {
            modal.style.display = 'none';
        }
    }

    startWarningCountdown() {
        const countdownElement = document.getElementById('sessionCountdown');
        if (!countdownElement) return;

        let remainingTime = this.options.warningTime * 60; // seconds

        const countdown = setInterval(() => {
            if (!this.isWarningShown || !this.sessionActive) {
                clearInterval(countdown);
                return;
            }

            const minutes = Math.floor(remainingTime / 60);
            const seconds = remainingTime % 60;
            countdownElement.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;

            remainingTime--;

            if (remainingTime < 0) {
                clearInterval(countdown);
            }
        }, 1000);
    }

    async extendSession() {
        try {
            const response = await fetch(this.options.extendSessionUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'extendSession=true'
            });

            if (response.ok) {
                const result = await response.json();
                if (result.success) {
                    this.hideSessionWarning();
                    this.resetSessionTimer();
                    this.showNotification('Session extended successfully', 'success');
                    console.log('✅ Session extended');
                } else {
                    this.handleSessionExpiry();
                }
            } else {
                this.handleSessionExpiry();
            }
        } catch (error) {
            console.error('Session extension error:', error);
            this.handleSessionExpiry();
        }
    }

    autoLogout() {
        this.sessionActive = false;
        this.clearTimers();
        
        console.log('🚪 Auto-logout triggered');
        
        // Show logout message
        this.showNotification('Session expired. You will be redirected to login.', 'warning');
        
        // Redirect after a brief delay
        setTimeout(() => {
            window.location.href = this.options.loginUrl + '?reason=timeout';
        }, 2000);
    }

    handleSessionExpiry() {
        this.sessionActive = false;
        this.clearTimers();
        
        console.log('❌ Session expired');
        
        // Hide any open modals
        this.hideSessionWarning();
        
        // Show expiry message
        this.showNotification('Your session has expired. Please log in again.', 'error');
        
        // Redirect to login
        setTimeout(() => {
            window.location.href = this.options.loginUrl + '?reason=expired';
        }, 2000);
    }

    clearTimers() {
        if (this.warningTimer) {
            clearTimeout(this.warningTimer);
            this.warningTimer = null;
        }
        
        if (this.logoutTimer) {
            clearTimeout(this.logoutTimer);
            this.logoutTimer = null;
        }
    }

    cleanup() {
        this.sessionActive = false;
        this.clearTimers();
        
        if (this.keepAliveTimer) {
            clearInterval(this.keepAliveTimer);
            this.keepAliveTimer = null;
        }
        
        console.log('🧹 SessionManager cleanup completed');
    }

    createWarningModal() {
        // Check if modal already exists
        if (document.getElementById('sessionWarningModal')) {
            return;
        }

        const modalHtml = `
            <div id="sessionWarningModal" class="session-modal" style="display: none;">
                <div class="session-modal-content">
                    <div class="session-modal-header">
                        <h3><i class="fas fa-clock"></i> Session Expiring</h3>
                    </div>
                    <div class="session-modal-body">
                        <p>Your session will expire in:</p>
                        <div class="session-countdown" id="sessionCountdown">5:00</div>
                        <p>Would you like to extend your session?</p>
                    </div>
                    <div class="session-modal-footer">
                        <button class="btn btn-secondary" onclick="sessionManager.autoLogout()">
                            <i class="fas fa-sign-out-alt"></i> Logout Now
                        </button>
                        <button class="btn btn-primary" onclick="sessionManager.extendSession()">
                            <i class="fas fa-clock"></i> Extend Session
                        </button>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        this.addModalStyles();
    }

    addModalStyles() {
        // Check if styles already added
        if (document.getElementById('sessionManagerStyles')) {
            return;
        }

        const styles = `
            <style id="sessionManagerStyles">
                .session-modal {
                    display: none;
                    position: fixed;
                    z-index: 10000;
                    left: 0;
                    top: 0;
                    width: 100%;
                    height: 100%;
                    background-color: rgba(0,0,0,0.7);
                    backdrop-filter: blur(5px);
                    animation: fadeIn 0.3s ease;
                }

                .session-modal-content {
                    background-color: white;
                    margin: 15% auto;
                    border-radius: 15px;
                    width: 90%;
                    max-width: 450px;
                    box-shadow: 0 20px 40px rgba(0,0,0,0.3);
                    animation: slideIn 0.3s ease;
                    overflow: hidden;
                }

                .session-modal-header {
                    background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
                    color: white;
                    padding: 25px;
                    text-align: center;
                }

                .session-modal-header h3 {
                    margin: 0;
                    font-size: 1.5rem;
                    font-weight: 600;
                }

                .session-modal-body {
                    padding: 30px;
                    text-align: center;
                }

                .session-modal-body p {
                    color: #666;
                    margin: 10px 0;
                    font-size: 16px;
                }

                .session-countdown {
                    font-size: 3rem;
                    font-weight: bold;
                    color: #dc3545;
                    font-family: 'Courier New', monospace;
                    background: #f8f9fa;
                    border-radius: 10px;
                    padding: 20px;
                    margin: 20px 0;
                    border: 2px solid #dee2e6;
                }

                .session-modal-footer {
                    background: #f8f9fa;
                    padding: 20px 30px;
                    display: flex;
                    gap: 15px;
                    justify-content: center;
                }

                .session-modal-footer .btn {
                    padding: 12px 24px;
                    border: none;
                    border-radius: 8px;
                    font-weight: 600;
                    cursor: pointer;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    transition: all 0.3s ease;
                    text-decoration: none;
                    font-size: 14px;
                }

                .session-modal-footer .btn-primary {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                }

                .session-modal-footer .btn-secondary {
                    background: #6c757d;
                    color: white;
                }

                .session-modal-footer .btn:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 8px 20px rgba(0,0,0,0.2);
                }

                .session-notification {
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    z-index: 10001;
                    padding: 15px 20px;
                    border-radius: 8px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    min-width: 300px;
                    animation: slideInRight 0.3s ease;
                }

                .session-notification.success {
                    background: #d4edda;
                    color: #155724;
                    border: 1px solid #c3e6cb;
                }

                .session-notification.error {
                    background: #f8d7da;
                    color: #721c24;
                    border: 1px solid #f5c6cb;
                }

                .session-notification.warning {
                    background: #fff3cd;
                    color: #856404;
                    border: 1px solid #ffeaa7;
                }

                .session-notification.info {
                    background: #d1ecf1;
                    color: #0c5460;
                    border: 1px solid #bee5eb;
                }

                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }

                @keyframes slideIn {
                    from { transform: translateY(-50px); opacity: 0; }
                    to { transform: translateY(0); opacity: 1; }
                }

                @keyframes slideInRight {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }

                @media (max-width: 480px) {
                    .session-modal-content {
                        margin: 10% auto;
                        width: 95%;
                    }

                    .session-modal-footer {
                        flex-direction: column;
                    }

                    .session-notification {
                        top: 10px;
                        right: 10px;
                        left: 10px;
                        min-width: auto;
                    }
                }
            </style>
        `;

        document.head.insertAdjacentHTML('beforeend', styles);
    }

    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `session-notification ${type}`;
        
        const icon = {
            success: 'fas fa-check-circle',
            error: 'fas fa-exclamation-circle',
            warning: 'fas fa-exclamation-triangle',
            info: 'fas fa-info-circle'
        }[type] || 'fas fa-info-circle';
        
        notification.innerHTML = `
            <i class="${icon}"></i>
            <span>${message}</span>
        `;
        
        document.body.appendChild(notification);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            notification.style.animation = 'slideInRight 0.3s ease reverse';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 5000);
    }

    // Public methods for manual session management
    static getInstance() {
        if (!window.sessionManagerInstance) {
            window.sessionManagerInstance = new SessionManager();
        }
        return window.sessionManagerInstance;
    }

    static extendSession() {
        const instance = SessionManager.getInstance();
        instance.extendSession();
    }

    static logout() {
        const instance = SessionManager.getInstance();
        instance.autoLogout();
    }

    static updateActivity() {
        const instance = SessionManager.getInstance();
        instance.updateActivity();
    }
}

// Auto-initialize when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        window.sessionManager = SessionManager.getInstance();
    });
} else {
    window.sessionManager = SessionManager.getInstance();
}

// Handle page visibility changes
document.addEventListener('visibilitychange', () => {
    if (!document.hidden && window.sessionManager) {
        window.sessionManager.updateActivity();
        console.log('👀 Page became visible, activity updated');
    }
});

// Handle before page unload
window.addEventListener('beforeunload', () => {
    if (window.sessionManager) {
        window.sessionManager.cleanup();
    }
});

// Global utilities
window.SessionUtils = {
    extendSession: () => SessionManager.extendSession(),
    logout: () => SessionManager.logout(),
    updateActivity: () => SessionManager.updateActivity(),
    
    // Check if user is currently active
    isUserActive: () => {
        if (!window.sessionManager) return false;
        const timeSinceActivity = Date.now() - window.sessionManager.lastActivity;
        return timeSinceActivity < 60000; // Active within last minute
    },
    
    // Get session status
    getSessionStatus: () => {
        if (!window.sessionManager) return 'unknown';
        return window.sessionManager.sessionActive ? 'active' : 'inactive';
    }
};

console.log('📱 Session Management JavaScript loaded');

// Export for module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SessionManager;
}