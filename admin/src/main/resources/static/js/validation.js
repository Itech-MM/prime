/**
 * Validation Utility
 * Usage: Add class "needs-validation" to your form and include this script
 */

class FormValidator {
    constructor() {
        this.initialized = false;
        this.init();
    }

    init() {
        if (this.initialized) return;

        this.initializeForms();
        this.initialized = true;

        console.log('FormValidator initialized');
    }

    /**
     * Initialize all forms with needs-validation class
     */
    initializeForms() {
        const forms = document.querySelectorAll('.needs-validation');

        forms.forEach(form => {
            this.setupFormValidation(form);
        });
    }

    /**
     * Setup validation for a specific form
     */
    setupFormValidation(form) {
        // Prevent submission if invalid
        form.addEventListener('submit', (event) => {
            if (!this.validateForm(form)) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add('was-validated');
        });

        // Real-time validation on blur
        const requiredFields = form.querySelectorAll('[required]');
        requiredFields.forEach(field => {
            field.addEventListener('blur', () => {
                this.validateField(field);
            });

            field.addEventListener('input', () => {
                if (field.value.trim() !== '') {
                    field.classList.remove('is-invalid');
                    field.classList.add('is-valid');
                }
            });
        });

        // Handle Summernote editors if present
        this.setupSummernoteValidation(form);

        // Handle file inputs
        this.setupFileInputValidation(form);
    }

    /**
     * Validate entire form
     */
    validateForm(form) {
        let isValid = true;
        const fields = form.querySelectorAll('[required]');

        fields.forEach(field => {
            if (!this.validateField(field)) {
                isValid = false;
            }
        });

        // Validate Summernote fields
        if (!this.validateSummernoteFields(form)) {
            isValid = false;
        }

        if (!isValid) {
            showToast("Please check all fields!", "error")
        }

        return isValid;
    }

    /**
     * Validate individual field
     */
    validateField(field) {
        const value = field.value.trim();
        let isValid = true;

        // Skip hidden fields
        if (field.type === 'hidden') return true;

        // Validate based on field type
        switch (field.type) {
            case 'email':
                isValid = this.validateEmail(value);
                break;
            case 'number':
                isValid = this.validateNumber(field, value);
                break;
            case 'file':
                isValid = this.validateFile(field);
                break;
            case 'select-one':
                isValid = this.validateSelect(field, value);
                break;
            default:
                isValid = value !== '';
        }

        // Update field classes
        if (isValid) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        } else {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
        }

        return isValid;
    }

    /**
     * Email validation
     */
    validateEmail(email) {
        if (!email) return false;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; //regex pattern
        return emailRegex.test(email);
    }

    /**
     * Number validation
     */
    validateNumber(field, value) {
        if (!value) return false;

        const numValue = parseFloat(value);
        const min = parseFloat(field.getAttribute('min'));
        const max = parseFloat(field.getAttribute('max'));

        if (!isNaN(min) && numValue < min) return false;
        if (!isNaN(max) && numValue > max) return false;

        return true;
    }

    /**
     * File validation
     */
    validateFile(field) {
        const file = field.files[0];
        if (!file) return true; // File is optional unless required

        // Check file type
        const accept = field.getAttribute('accept');
        if (accept && !this.validateFileType(file, accept)) {
            this.showCustomError(field, 'Invalid file type. Please select a valid file.');
            return false;
        }

        // Check file size (default 5MB)
        const maxSize = 5 * 1024 * 1024; // 5MB in bytes
        if (file.size > maxSize) {
            this.showCustomError(field, 'File size must be less than 5MB.');
            return false;
        }

        return true;
    }

    /**
     * Select validation
     */
    validateSelect(field, value) {
        return value !== '' && value !== null;
    }

    /**
     * File type validation
     */
    validateFileType(file, accept) {
        const acceptedTypes = accept.split(',').map(type => type.trim());
        return acceptedTypes.some(type => {
            if (type === '*') return true;
            if (type.startsWith('.')) {
                return file.name.toLowerCase().endsWith(type.toLowerCase());
            }
            return file.type.match(type.replace('*', '.*'));
        });
    }

    /**
     * Summernote validation setup
     */
    setupSummernoteValidation(form) {
        const summernoteEditors = form.querySelectorAll('.summernote-editor');

        summernoteEditors.forEach(editor => {
            // Sync Summernote content with original textarea
            $(editor).on('summernote.change', () => {
                const content = $(editor).summernote('code');
                editor.value = content;

                // Validate if required
                if (editor.hasAttribute('required')) {
                    this.validateSummernoteField(editor);
                }
            });
        });
    }

    /**
     * Validate Summernote fields
     */
    validateSummernoteFields(form) {
        let isValid = true;
        const summernoteEditors = form.querySelectorAll('.summernote-editor[required]');

        summernoteEditors.forEach(editor => {
            if (!this.validateSummernoteField(editor)) {
                isValid = false;
            }
        });

        return isValid;
    }

    /**
     * Validate individual Summernote field
     */
    validateSummernoteField(editor) {
        const content = $(editor).summernote('code');
        const isValid = content.trim() !== '' && content !== '<p><br></p>';

        // Find the Summernote container
        const summernoteContainer = $(editor).siblings('.note-editor');

        if (summernoteContainer.length) {
            if (isValid) {
                summernoteContainer.removeClass('is-invalid').addClass('is-valid');
            } else {
                summernoteContainer.removeClass('is-valid').addClass('is-invalid');
            }
        }

        return isValid;
    }

    /**
     * File input validation setup
     */
    setupFileInputValidation(form) {
        const fileInputs = form.querySelectorAll('input[type="file"]');

        fileInputs.forEach(input => {
            input.addEventListener('change', (e) => {
                this.validateFile(input);
                this.previewImage(input);
            });
        });
    }

    /**
         * Image preview for file inputs
         */
    previewImage(input) {
        const file = input.files[0];
        if (!file || !file.type.startsWith('image/')) return;

        // SAFE FALLBACK CHAIN: Existing logic runs first, new lookup runs last
        const previewContainer = input.closest('.img-container')?.querySelector('.image-preview') ||
            input.parentElement.nextElementSibling?.querySelector('.image-preview') ||
            input.parentElement.querySelector('.image-preview'); // New fallback

        if (!previewContainer) {
            console.warn('Image preview container not found for input:', input.id);
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            previewContainer.innerHTML = `
	                <img src="${e.target.result}" class="img-thumbnail mb-2" 
	                     style="max-height: 150px; object-fit: contain;" alt="Preview">
	                <p class="small text-muted mb-0">Image Preview</p>
	            `;
        };
        reader.readAsDataURL(file);
    }

    /**
     * Show custom error message
     */
    showCustomError(field, message) {
        // Remove existing custom error
        const existingError = field.parentNode.querySelector('.custom-invalid-feedback');
        if (existingError) {
            existingError.remove();
        }

        // Add new error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'custom-invalid-feedback text-danger small mt-1';
        errorDiv.textContent = message;
        field.parentNode.appendChild(errorDiv);

        field.classList.add('is-invalid');
    }

    /**
     * Reset form validation
     */
    resetFormValidation(form) {
        form.classList.remove('was-validated');

        // Reset all fields
        const fields = form.querySelectorAll('.is-valid, .is-invalid');
        fields.forEach(field => {
            field.classList.remove('is-valid', 'is-invalid');
        });

        // Reset Summernote validation
        const summernoteContainers = form.querySelectorAll('.note-editor');
        summernoteContainers.forEach(container => {
            container.classList.remove('is-valid', 'is-invalid');
        });

        // Remove custom error messages
        const customErrors = form.querySelectorAll('.custom-invalid-feedback');
        customErrors.forEach(error => error.remove());
    }

    /**
     * Validate form and show errors
     */
    validateAndShow(form) {
        const isValid = this.validateForm(form);
        form.classList.add('was-validated');
        return isValid;
    }

    /**
     * Add validation to dynamically created forms
     */
    addForm(formElement) {
        this.setupFormValidation(formElement);
    }
}

// Create global instance
window.FormValidator = new FormValidator();

// Auto-initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.FormValidator.init();
});

/**
 * Global form reset function
 */
window.resetForm = function(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.reset();
        window.FormValidator.resetFormValidation(form);

        // Reset Summernote editors
        $(form).find('.summernote-editor').each(function() {
            if ($(this).summernote('reset')) {
                $(this).summernote('reset');
            }
        });
    }
};

/**
 * Quick form validation check
 */
window.validateForm = function(formId) {
    const form = document.getElementById(formId);
    return form ? window.FormValidator.validateAndShow(form) : false;
};