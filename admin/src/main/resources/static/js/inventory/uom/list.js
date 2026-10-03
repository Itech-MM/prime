var InventoryUom = (function () {

    var loaded = false;
    var currentPage = 0;
    var totalPages = 1;
    var pageSize = 10;
    var isLoading = false;

    function loadIfNeeded() {
        if (loaded) { return; }
        loaded = true;
        bindEvents();
        loadStatusOptions();
        search(0);
    }

    function bindEvents() {
        $('#uomSearchBtn').on('click', function () { search(0); });
        $('#uomResetBtn').on('click', function () {
            $('#uomSearchInput').val('');
            search(0);
        });
        $('#uomSearchInput').on('keypress', function (e) {
            if (e.which === 13) { e.preventDefault(); search(0); }
        });
		$(document).on('click', '.uom-edit-btn', function () {
		    window.location.href = CONTEXT_PATH + 'inventory/items/setup?tab=units&uomId=' + $(this).data('id');
		});
		$(document).on('click', '.uom-delete-btn', function () {
            var id = $(this).data('id');
            ConfirmDelete.open($(this).data('name'), function () { remove(id); });
        });
    }

    function loadStatusOptions() {
        $.get(CONTEXT_PATH + 'enums/active-status', function (list) {
            var select = $('#uomStatus').empty().append('<option value="">Select Status</option>');
            list.forEach(function (o) {
                select.append('<option value="' + o.code + '">' + o.desc + '</option>');
            });
        });
    }

    function search(page) {
        if (isLoading) { return; }
        isLoading = true;
        $('#uomLoadingSpinner').removeClass('d-none');
        $('#uomTableContainer').addClass('d-none');

        var term = $('#uomSearchInput').val();

        $.ajax({
            url: CONTEXT_PATH + 'inventory/units/search',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ name: term, code: term, page: page, size: pageSize }),
            success: function (res) {
                renderTable(res.results);
                updatePaging(res.pageNo, res.totalPage, res.totalRecords);
                $('#units-count').removeClass('d-none').text(res.totalRecords);
                isLoading = false;
                $('#uomLoadingSpinner').addClass('d-none');
                $('#uomTableContainer').removeClass('d-none');
                $('#uomPagingWrap').removeClass('d-none');
            },
            error: function (xhr) {
                isLoading = false;
                $('#uomLoadingSpinner').addClass('d-none');
                showToast(xhr.responseText || 'Error loading units', 'error');
            }
        });
    }

    function renderTable(rows) {
        var body = $('#uomTableBody');
        body.empty();
        var canEdit = $('#canEditUom').val() === 'true';
        var canDelete = $('#canDeleteUom').val() === 'true';

        if (!rows || rows.length === 0) {
            body.append('<tr><td colspan="4" class="text-center text-muted py-4">No units found</td></tr>');
            return;
        }

        rows.forEach(function (u) {
            var statusBadge = u.status === 1 ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-secondary">Inactive</span>';
            var actions = '';
            if (canEdit) { actions += '<button class="btn btn-outline-primary uom-edit-btn" data-id="' + u.id + '"><i class="fas fa-edit"></i></button>'; }
            if (canDelete) { actions += '<button class="btn btn-outline-danger uom-delete-btn" data-id="' + u.id + '" data-name="' + u.name + '"><i class="fas fa-trash"></i></button>'; }

            body.append(
                '<tr><td>' + (u.code || '-') + '</td><td>' + (u.name || '-') + '</td>' +
                '<td>' + statusBadge + '</td><td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
            );
        });
    }

    function updatePaging(page, pages, total) {
        currentPage = page;
        totalPages = pages;
        $('#uomPageInfo').text('Page ' + (page + 1) + ' of ' + pages + ' (' + total + ' total)');
        $('#uomPagination').twbsPagination('destroy');
        $('#uomPagination').twbsPagination({
            totalPages: Math.max(pages, 1),
            visiblePages: 5,
            startPage: page + 1,
            first: 'First', prev: 'Prev', next: 'Next', last: 'Last',
            onPageClick: function (e, p) { if (!isLoading) { search(p - 1); } }
        });
    }

    function openModal(id) {
        $('#uomForm')[0].reset();
        $('#uomId').val('');

        if (id) {
            $('#uomModalTitle').text('Update Unit of Measure');
            $.get(CONTEXT_PATH + 'inventory/units/' + id, function (dto) {
                $('#uomId').val(dto.id);
                $('#uomCode').val(dto.code);
                $('#uomName').val(dto.name);
                $('#uomStatus').val(dto.status);
                $('#uomModal').modal('show');
                setTimeout(function () { $('#uomName').trigger('focus'); }, 300);
            }).fail(function (xhr) {
                showToast(xhr.responseText || 'Unit not found', 'error');
            });
        } else {
            $('#uomModalTitle').text('Add Unit of Measure');
            $('#uomModal').modal('show');
            setTimeout(function () { $('#uomCode').trigger('focus'); }, 300);
        }
    }

    function save() {
        var dto = {
            id: $('#uomId').val() || null,
            code: $('#uomCode').val(),
            name: $('#uomName').val(),
            status: $('#uomStatus').val() ? Number($('#uomStatus').val()) : null
        };

        $('#uomSaveBtn').prop('disabled', true);

        $.ajax({
            url: CONTEXT_PATH + 'inventory/units/save',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(dto),
            success: function () {
                $('#uomModal').modal('hide');
                showToast('Unit saved successfully', 'success');
                search(currentPage);
                $('#uomSaveBtn').prop('disabled', false);
            },
            error: function (xhr) {
                $('#uomSaveBtn').prop('disabled', false);
                showToast(xhr.responseText || 'Error saving unit', 'error');
            }
        });
    }

    function remove(id) {
        $.post(CONTEXT_PATH + 'inventory/units/delete?id=' + id)
            .done(function () {
                showToast('Unit deleted successfully', 'success');
                search(currentPage);
            })
            .fail(function (xhr) {
                showToast(xhr.responseText || 'Error deleting unit', 'error');
            });
    }

    return { loadIfNeeded: loadIfNeeded };
})();