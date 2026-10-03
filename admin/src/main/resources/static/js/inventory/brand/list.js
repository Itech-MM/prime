var InventoryBrand = (function () {

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
        $('#brandSearchBtn').on('click', function () { search(0); });
        $('#brandResetBtn').on('click', function () {
            $('#brandSearchInput').val('');
            search(0);
        });
        $('#brandSearchInput').on('keypress', function (e) {
            if (e.which === 13) { e.preventDefault(); search(0); }
        });
		$(document).on('click', '.brand-edit-btn', function () {
		    window.location.href = CONTEXT_PATH + 'inventory/items/setup?tab=brands&brandId=' + $(this).data('id');
		});
		$(document).on('click', '.brand-delete-btn', function () {
            var id = $(this).data('id');
            ConfirmDelete.open($(this).data('name'), function () { remove(id); });
        });
    }

    function loadStatusOptions() {
        $.get(CONTEXT_PATH + 'enums/active-status', function (list) {
            var select = $('#brandStatus').empty().append('<option value="">Select Status</option>');
            list.forEach(function (o) {
                select.append('<option value="' + o.code + '">' + o.desc + '</option>');
            });
        });
    }

    function search(page) {
        if (isLoading) { return; }
        isLoading = true;
        $('#brandLoadingSpinner').removeClass('d-none');
        $('#brandTableContainer').addClass('d-none');

        $.ajax({
            url: CONTEXT_PATH + 'inventory/brands/search',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ name: $('#brandSearchInput').val(), page: page, size: pageSize }),
            success: function (res) {
                renderTable(res.results);
                updatePaging(res.pageNo, res.totalPage, res.totalRecords);
                $('#brands-count').removeClass('d-none').text(res.totalRecords);
                isLoading = false;
                $('#brandLoadingSpinner').addClass('d-none');
                $('#brandTableContainer').removeClass('d-none');
                $('#brandPagingWrap').removeClass('d-none');
            },
            error: function (xhr) {
                isLoading = false;
                $('#brandLoadingSpinner').addClass('d-none');
                showToast(xhr.responseText || 'Error loading brands', 'error');
            }
        });
    }

    function renderTable(rows) {
        var body = $('#brandTableBody');
        body.empty();
        var canEdit = $('#canEditBrand').val() === 'true';
        var canDelete = $('#canDeleteBrand').val() === 'true';

        if (!rows || rows.length === 0) {
            body.append('<tr><td colspan="4" class="text-center text-muted py-4">No brands found</td></tr>');
            return;
        }

        rows.forEach(function (b) {
            var statusBadge = b.status === 1 ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-secondary">Inactive</span>';
            var actions = '';
            if (canEdit) { actions += '<button class="btn btn-outline-primary brand-edit-btn" data-id="' + b.id + '"><i class="fas fa-edit"></i></button>'; }
            if (canDelete) { actions += '<button class="btn btn-outline-danger brand-delete-btn" data-id="' + b.id + '" data-name="' + b.name + '"><i class="fas fa-trash"></i></button>'; }

            body.append(
                '<tr><td>' + (b.code || '-') + '</td><td>' + (b.name || '-') + '</td>' +
                '<td>' + statusBadge + '</td><td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
            );
        });
    }

    function updatePaging(page, pages, total) {
        currentPage = page;
        totalPages = pages;
        $('#brandPageInfo').text('Page ' + (page + 1) + ' of ' + pages + ' (' + total + ' total)');
        $('#brandPagination').twbsPagination('destroy');
        $('#brandPagination').twbsPagination({
            totalPages: Math.max(pages, 1),
            visiblePages: 5,
            startPage: page + 1,
            first: 'First', prev: 'Prev', next: 'Next', last: 'Last',
            onPageClick: function (e, p) { if (!isLoading) { search(p - 1); } }
        });
    }

    function openModal(id) {
        $('#brandForm')[0].reset();
        $('#brandId').val('');
        $('.invalid-feedback').text('');

        if (id) {
            $('#brandModalTitle').text('Update Brand');
            $.get(CONTEXT_PATH + 'inventory/brands/' + id, function (dto) {
                $('#brandId').val(dto.id);
                $('#brandCode').val(dto.code);
                $('#brandName').val(dto.name);
                $('#brandStatus').val(dto.status);
                $('#brandModal').modal('show');
                setTimeout(function () { $('#brandName').trigger('focus'); }, 300);
            }).fail(function (xhr) {
                showToast(xhr.responseText || 'Brand not found', 'error');
            });
        } else {
            $('#brandModalTitle').text('Add Brand');
            $('#brandModal').modal('show');
            setTimeout(function () { $('#brandCode').trigger('focus'); }, 300);
        }
    }

    function save() {
        var dto = {
            id: $('#brandId').val() || null,
            code: $('#brandCode').val(),
            name: $('#brandName').val(),
            status: $('#brandStatus').val() ? Number($('#brandStatus').val()) : null
        };

        $('#brandSaveBtn').prop('disabled', true);

        $.ajax({
            url: CONTEXT_PATH + 'inventory/brands/save',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(dto),
            success: function () {
                $('#brandModal').modal('hide');
                showToast('Brand saved successfully', 'success');
                search(currentPage);
                $('#brandSaveBtn').prop('disabled', false);
            },
            error: function (xhr) {
                $('#brandSaveBtn').prop('disabled', false);
                showToast(xhr.responseText || 'Error saving brand', 'error');
            }
        });
    }

    function remove(id) {
        $.post(CONTEXT_PATH + 'inventory/brands/delete?id=' + id)
            .done(function () {
                showToast('Brand deleted successfully', 'success');
                search(currentPage);
            })
            .fail(function (xhr) {
                showToast(xhr.responseText || 'Error deleting brand', 'error');
            });
    }

    return { loadIfNeeded: loadIfNeeded };
})();