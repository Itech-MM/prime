var InventoryItem = (function () {

    var currentPage = 0;
    var totalPages = 1;
    var pageSize = 10;
    var isLoading = false;
    var currentRequest = null;

    function init() {
        currentPage = Number($('#itemPagination').data('page')) || 0;
        initPagination(currentPage, totalPages);

        $('#itemSearchBtn').on('click', function () { search(0); });
        $('#itemResetBtn').on('click', function () {
            $('#itemSearchForm')[0].reset();
            search(0);
        });
        $('#itemSearchForm input').on('keypress', function (e) {
            if (e.which === 13) {
                e.preventDefault();
                search(0);
            }
        });
    }

    function initPagination(page, pages) {
        $('#itemPagination').twbsPagination({
            totalPages: Math.max(pages, 1),
            visiblePages: 5,
            startPage: page + 1,
            first: 'First', prev: 'Prev', next: 'Next', last: 'Last',
            onPageClick: function (e, p) {
                if (!isLoading) { search(p - 1); }
            }
        });
    }

    function search(page) {
        if (isLoading) { return; }
        if (currentRequest) { currentRequest.abort(); }

        showLoading(true);
        isLoading = true;

        var data = {
            name: $('#itemSearchForm input[name=name]').val(),
            code: $('#itemSearchForm input[name=code]').val(),
            sku: $('#itemSearchForm input[name=sku]').val(),
            page: page,
            size: pageSize
        };

        currentRequest = $.ajax({
            url: CONTEXT_PATH + 'inventory/items/search',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (res) {
                renderTable(res.results);
                updatePagination(res.pageNo, res.totalPage);
                $('#itemPageInfo').html('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage);
                $('#items-count').removeClass('d-none').text(res.totalRecords);
                showLoading(false);
                isLoading = false;
                currentRequest = null;
            },
            error: function (xhr, status) {
                showLoading(false);
                isLoading = false;
                currentRequest = null;
                if (status !== 'abort') {
                    showToast(xhr.responseText || 'Error searching items', 'error');
                }
            }
        });
    }

    function renderTable(items) {
        var body = $('#itemTableBody');
        body.empty();

        if (!items || items.length === 0) {
            body.append('<tr><td colspan="7" class="text-center text-muted py-4"><i class="fas fa-box-open fa-2x mb-2 d-block"></i>No items found</td></tr>');
            return;
        }

        var canEdit = $('#canEditItem').val() === 'true';
        var canDelete = $('#canDeleteItem').val() === 'true';

        items.forEach(function (item) {
            var statusBadge = item.status === 1
                ? '<span class="badge bg-success">Active</span>'
                : '<span class="badge bg-secondary">Inactive</span>';

            var actions = '';
            if (canEdit) {
                actions += '<a href="' + CONTEXT_PATH + 'inventory/items/setup?id=' + item.id + '" class="btn btn-outline-primary"><i class="fas fa-edit"></i></a>';
            }
            if (canDelete) {
                actions += '<button class="btn btn-outline-danger item-delete-btn" data-id="' + item.id + '" data-name="' + item.name + '"><i class="fas fa-trash"></i></button>';
            }

            body.append(
                '<tr>' +
                '<td>' + (item.code || '-') + '</td>' +
                '<td>' + (item.name || '-') + '</td>' +
                '<td>' + (item.categoryName || '-') + '</td>' +
                '<td>' + (item.brandName || '-') + '</td>' +
                '<td>' + (item.itemTypeDesc || '-') + '</td>' +
                '<td>' + statusBadge + '</td>' +
                '<td><div class="btn-group btn-group-sm">' + actions + '</div></td>' +
                '</tr>'
            );
        });
    }

    function updatePagination(page, pages) {
        if (page !== currentPage || pages !== totalPages) {
            currentPage = page;
            totalPages = pages;
            $('#itemPagination').twbsPagination('destroy');
            initPagination(currentPage, Math.max(totalPages, 1));
        }
    }

    function showLoading(show) {
        if (show) {
            $('#itemLoadingSpinner').removeClass('d-none');
            $('#itemTableContainer').addClass('d-none');
        } else {
            $('#itemLoadingSpinner').addClass('d-none');
            $('#itemTableContainer').removeClass('d-none');
        }
    }

    return { init: init };
})();

$(document).ready(function () {
    InventoryItem.init();
});