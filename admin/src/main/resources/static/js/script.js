
/**
 * Global Toast Message System
 * Usage: 
 *   showToast('Success message', 'success');
 *   showToast('Error message', 'error');
 *   showToast('Info message', 'info');
 *   showToast('Warning message', 'warning');
 */

// Toast configuration
const ToastConfig = {
	success: {
		icon: 'fas fa-check-circle',
		bgClass: 'bg-success',
		delay: 5000
	},
	error: {
		icon: 'fas fa-exclamation-circle',
		bgClass: 'bg-danger',
		delay: 7000
	}, 
	danger: {
		icon: 'fas fa-exclamation-circle',
		bgClass: 'bg-danger',
		delay: 7000
	},
	info: {
		icon: 'fas fa-info-circle',
		bgClass: 'bg-info',
		delay: 4000
	},
	warning: {
		icon: 'fas fa-exclamation-triangle',
		bgClass: 'bg-warning',
		delay: 6000
	}
};

// Global toast function
function showToast(message, type = 'info') {
	const config = ToastConfig[type] || ToastConfig.info;

	// Create toast element
	const toastId = 'toast-' + Date.now();
	const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white ${config.bgClass} border-0" 
             role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="${config.icon} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                        data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

	// Get or create toast container
	let toastContainer = document.getElementById('dynamicToastContainer');
	if (!toastContainer) {
		toastContainer = document.createElement('div');
		toastContainer.id = 'dynamicToastContainer';
		toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
		toastContainer.style.zIndex = '1100';
		document.body.appendChild(toastContainer);
	}

	// Add toast to container
	toastContainer.insertAdjacentHTML('beforeend', toastHtml);

	// Initialize and show toast
	const toastElement = document.getElementById(toastId);
	const toast = new bootstrap.Toast(toastElement, {
		delay: config.delay,
		autohide: true
	});

	// Remove element from DOM after hide
	toastElement.addEventListener('hidden.bs.toast', function() {
		this.remove();
	});

	toast.show();
}

// Alternative shorter functions for common types
function showSuccess(message) {
	showToast(message, 'success');
}

function showError(message) {
	showToast(message, 'error');
}

function showInfo(message) {
	showToast(message, 'info');
}

function showWarning(message) {
	showToast(message, 'warning');
}

// Initialize on document ready


// Make functions globally available
window.showToast = showToast;
window.showSuccess = showSuccess;
window.showError = showError;
window.showInfo = showInfo;
window.showWarning = showWarning;