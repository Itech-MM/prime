var InventoryLocation = (function () {

    var loaded = false;
    var currentPage = 0;
    var totalPages = 1;
    var pageSize = 10;
    var isLoading = false;
    var filterWarehouseId = null;

    function loadIfNeeded() {
        if (loaded) { return; }
        loaded = true;
        bindEvents();
        loadWarehouseFilter();
        search(0);
    }

    function bindEvents() {
        $('#locationSearchBtn').on('click', function () { search(0); });
        $('#locationResetBtn').on('click', function () {
            $('#locationSearchInput').val('');
            $('#locationWarehouseFilter').val('').trigger('change');
            filterWarehouseId = null;
            search(0);
        });
        $('#locationSearchInput').on('keypress', function (e) {
            if (e.which === 13) { e.preventDefault(); search(0); }
        });
        $(document).on('click', '.location-edit-btn', function () {
            window.location.href = CONTEXT_PATH + 'inventory/warehouses/setup?tab=locations&locationId=' + $(this).data('id');
        });
        $(document).on('click', '.location-delete-btn', function () {
            var id = $(this).data('id');
            ConfirmDelete.open($(this).data('name'), function () { remove(id); });
        });
    }

    function loadWarehouseFilter() {
        var select = $('#locationWarehouseFilter');
        select.append('<option value="">All Warehouses</option>');
        $.get(CONTEXT_PATH + 'inventory/warehouses/select2', function (res) {
            (res.results || []).forEach(function (o) {
                select.append('<option value="' + o.id + '">' + o.text + '</option>');
            });
        });
        select.on('change', function () {
            filterWarehouseId = $(this).val() || null;
            search(0);
        });
    }

    function search(page) {
        if (isLoading) { return; }
        isLoading = true;
        $('#locationLoadingSpinner').removeClass('d-none');
        $('#locationTableContainer').addClass('d-none');

        $.ajax({
            url: CONTEXT_PATH + 'inventory/locations/search',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ name: $('#locationSearchInput').val(), warehouseId: filterWarehouseId, page: page, size: pageSize }),
            success: function (res) {
                renderTable(res.results);
                updatePaging(res.pageNo, res.totalPage, res.totalRecords);
                isLoading = false;
                $('#locationLoadingSpinner').addClass('d-none');
                $('#locationTableContainer').removeClass('d-none');
                $('#locationPagingWrap').removeClass('d-none');
            },
            error: function (xhr) {
                isLoading = false;
                $('#locationLoadingSpinner').addClass('d-none');
                showToast(xhr.responseText || 'Error loading locations', 'error');
            }
        });
    }

    function renderTable(rows) {
        var body = $('#locationTableBody');
        body.empty();
        var canEdit = $('#canEditLocation').val() === 'true';
        var canDelete = $('#canDeleteLocation').val() === 'true';

        if (!rows || rows.length === 0) {
            body.append('<tr><td colspan="7" class="text-center text-muted py-4">No locations found</td></tr>');
            return;
        }

        rows.forEach(function (l) {
            var statusBadge = l.status === 1 ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-secondary">Inactive</span>';
            var actions = '';
            if (canEdit) { actions += '<button class="btn btn-outline-primary location-edit-btn" data-id="' + l.id + '"><i class="fas fa-edit"></i></button>'; }
            if (canDelete) { actions += '<button class="btn btn-outline-danger location-delete-btn" data-id="' + l.id + '" data-name="' + l.name + '"><i class="fas fa-trash"></i></button>'; }

            body.append(
                '<tr><td>' + (l.warehouseName || '-') + '</td><td>' + (l.code || '-') + '</td><td>' + (l.name || '-') + '</td>' +
                '<td>' + (l.typeDesc || '-') + '</td><td>' + (l.parentName || '-') + '</td>' +
                '<td>' + statusBadge + '</td><td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
            );
        });
    }

    function updatePaging(page, pages, total) {
        currentPage = page;
        totalPages = pages;
        $('#locationPageInfo').text('Page ' + (page + 1) + ' of ' + pages + ' (' + total + ' total)');
        $('#locationPagination').twbsPagination('destroy');
        $('#locationPagination').twbsPagination({
            totalPages: Math.max(pages, 1),
            visiblePages: 5,
            startPage: page + 1,
            first: 'First', prev: 'Prev', next: 'Next', last: 'Last',
            onPageClick: function (e, p) { if (!isLoading) { search(p - 1); } }
        });
    }

    function remove(id) {
        $.post(CONTEXT_PATH + 'inventory/locations/delete?id=' + id)
            .done(function () {
                showToast('Location deleted successfully', 'success');
                search(currentPage);
            })
            .fail(function (xhr) {
                showToast(xhr.responseText || 'Error deleting location', 'error');
            });
    }

    return { loadIfNeeded: loadIfNeeded };
})();