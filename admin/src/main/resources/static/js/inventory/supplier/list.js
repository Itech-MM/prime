var InventorySupplier = (function () {

    var currentPage = 0;
    var totalPages = 1;
    var pageSize = 10;
    var isLoading = false;
    var currentRequest = null;

    function init() {
        initPagination(0, totalPages);

        $('#searchBtn').on('click', function () { search(0); });
        $('#resetBtn').on('click', function () {
            $('#searchForm')[0].reset();
            search(0);
        });
        $('#searchForm input').on('keypress', function (e) {
            if (e.which === 13) { e.preventDefault(); search(0); }
        });
        $(document).on('click', '.edit-btn', function () {
            window.location.href = CONTEXT_PATH + 'inventory/suppliers/setup?id=' + $(this).data('id');
        });
        $(document).on('click', '.delete-btn', function () {
            $('#deleteModalForm').attr('action', CONTEXT_PATH + 'inventory/suppliers/delete?id=' + $(this).data('id'));
            $('#deleteSupplierName').text($(this).data('name'));
            $('#deleteSupplierModal').modal('show');
        });
    }

    function initPagination(page, pages) {
        $('#pagination').twbsPagination({
            totalPages: Math.max(pages, 1),
            visiblePages: 5,
            startPage: page + 1,
            first: 'First', prev: 'Prev', next: 'Next', last: 'Last',
            onPageClick: function (e, p) { if (!isLoading) { search(p - 1); } }
        });
    }

    function search(page) {
        if (isLoading) { return; }
        if (currentRequest) { currentRequest.abort(); }

        showLoading(true);
        isLoading = true;

        var data = {
            name: $('#searchForm input[name=name]').val(),
            code: $('#searchForm input[name=code]').val(),
            phone: $('#searchForm input[name=phone]').val(),
            page: page,
            size: pageSize
        };

        currentRequest = $.ajax({
            url: CONTEXT_PATH + 'inventory/suppliers/search',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                renderTable(res.results);
                updatePagination(res.pageNo, res.totalPage);
                $('#resultCount').html('Showing ' + res.pageCount + ' of ' + res.totalRecords + ' suppliers');
                $('#pageInfo').html('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage);
                showLoading(false);
                isLoading = false;
                currentRequest = null;
            },
            error: function (xhr, status) {
                showLoading(false);
                isLoading = false;
                currentRequest = null;
                if (status !== 'abort') {
                    showToast(xhr.responseText || 'Error searching suppliers', 'error');
                }
            }
        });
    }

    function renderTable(rows) {
        var body = $('#suppliersTableBody');
        body.empty();

        if (!rows || rows.length === 0) {
            body.append('<tr><td colspan="7" class="text-center text-muted py-4">No suppliers found</td></tr>');
            return;
        }

        var canEdit = $('#canEdit').val() === 'true';
        var canDelete = $('#canDelete').val() === 'true';

        rows.forEach(function (s) {
            var statusBadge = s.status === 1 ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-secondary">Inactive</span>';
            var actions = '';
            if (canEdit) { actions += '<button class="btn btn-outline-primary edit-btn" data-id="' + s.id + '"><i class="fas fa-edit"></i></button>'; }
            if (canDelete) { actions += '<button class="btn btn-outline-danger delete-btn" data-id="' + s.id + '" data-name="' + s.name + '"><i class="fas fa-trash"></i></button>'; }

            body.append(
                '<tr><td>' + (s.name || '-') + '</td><td>' + (s.code || '-') + '</td><td>' + (s.contactPerson || '-') + '</td>' +
                '<td>' + (s.phone || '-') + '</td><td>' + (s.email || '-') + '</td>' +
                '<td>' + statusBadge + '</td><td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
            );
        });
    }

    function updatePagination(page, pages) {
        if (page !== currentPage || pages !== totalPages) {
            currentPage = page;
            totalPages = pages;
            $('#pagination').twbsPagination('destroy');
            initPagination(currentPage, Math.max(totalPages, 1));
        }
    }

    function showLoading(show) {
        if (show) {
            $('#loadingSpinner').removeClass('d-none');
            $('#tableContainer').addClass('d-none');
        } else {
            $('#loadingSpinner').addClass('d-none');
            $('#tableContainer').removeClass('d-none');
        }
    }

    return { init: init };
})();

$(document).ready(function () {
    InventorySupplier.init();
});