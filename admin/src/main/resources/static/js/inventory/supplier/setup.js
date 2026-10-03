$(document).ready(function () {
    $('#supplierForm').on('submit', function (e) {
        if (!$('#supplierForm')[0].checkValidity()) {
            e.preventDefault();
            showToast('Please fill all required fields', 'error');
        }
    });
});