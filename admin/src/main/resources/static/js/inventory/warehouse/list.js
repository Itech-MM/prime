var InventoryWarehouseList = (function () {

    var currentPage = 0;
    var totalPages = 1;
    var pageSize = 10;
    var isLoading = false;
    var currentRequest = null;

    function init() {
        initPagination(0, totalPages);

        $('#warehouseSearchBtn').on('click', function () { search(0); });
        $('#warehouseResetBtn').on('click', function () {
            $('#warehouseSearchForm')[0].reset();
            search(0);
        });
        $('#warehouseSearchForm input').on('keypress', function (e) {
            if (e.which === 13) { e.preventDefault(); search(0); }
        });
    }

    function initPagination(page, pages) {
        $('#warehousePagination').twbsPagination({
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
            name: $('#warehouseSearchForm input[name=name]').val(),
            code: $('#warehouseSearchForm input[name=code]').val(),
            page: page,
            size: pageSize
        };

        currentRequest = $.ajax({
            url: CONTEXT_PATH + 'inventory/warehouses/search',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                renderTable(res.results);
                updatePagination(res.pageNo, res.totalPage);
                $('#warehousePageInfo').html('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage);
                showLoading(false);
                isLoading = false;
                currentRequest = null;
            },
            error: function (xhr, status) {
                showLoading(false);
                isLoading = false;
                currentRequest = null;
                if (status !== 'abort') {
                    showToast(xhr.responseText || 'Error searching warehouses', 'error');
                }
            }
        });
    }

    function renderTable(rows) {
        var body = $('#warehouseTableBody');
        body.empty();

        if (!rows || rows.length === 0) {
            body.append('<tr><td colspan="6" class="text-center text-muted py-4">No warehouses found</td></tr>');
            return;
        }

        var canEdit = $('#canEditWarehouse').val() === 'true';
        var canDelete = $('#canDeleteWarehouse').val() === 'true';

        rows.forEach(function (wh) {
            var statusBadge = wh.status === 1 ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-secondary">Inactive</span>';
            var negBadge = wh.allowNegativeStock ? '<span class="badge bg-warning text-dark">Allowed</span>' : '<span class="badge bg-secondary">Blocked</span>';
            var actions = '';
            if (canEdit) { actions += '<a href="' + CONTEXT_PATH + 'inventory/warehouses/setup?id=' + wh.id + '" class="btn btn-outline-primary"><i class="fas fa-edit"></i></a>'; }
            if (canDelete) { actions += '<button class="btn btn-outline-danger warehouse-delete-btn" data-id="' + wh.id + '" data-name="' + wh.name + '"><i class="fas fa-trash"></i></button>'; }

            body.append(
                '<tr><td>' + (wh.code || '-') + '</td><td>' + (wh.name || '-') + '</td><td>' + (wh.managerName || '-') + '</td>' +
                '<td>' + negBadge + '</td><td>' + statusBadge + '</td><td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
            );
        });
    }

    function updatePagination(page, pages) {
        if (page !== currentPage || pages !== totalPages) {
            currentPage = page;
            totalPages = pages;
            $('#warehousePagination').twbsPagination('destroy');
            initPagination(currentPage, Math.max(totalPages, 1));
        }
    }

    function showLoading(show) {
        if (show) {
            $('#warehouseLoadingSpinner').removeClass('d-none');
            $('#warehouseTableContainer').addClass('d-none');
        } else {
            $('#warehouseLoadingSpinner').addClass('d-none');
            $('#warehouseTableContainer').removeClass('d-none');
        }
    }

    return { init: init };
})();

$(document).ready(function () {
    InventoryWarehouseList.init();
});