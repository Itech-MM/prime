var InventoryCategory = (function () {

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
        loadParentOptions();
        search(0);
    }

    function bindEvents() {
        $('#categorySearchBtn').on('click', function () { search(0); });
        $('#categoryResetBtn').on('click', function () {
            $('#categorySearchInput').val('');
            search(0);
        });
        $('#categorySearchInput').on('keypress', function (e) {
            if (e.which === 13) { e.preventDefault(); search(0); }
        });
		$(document).on('click', '.category-edit-btn', function () {
		    window.location.href = CONTEXT_PATH + 'inventory/items/setup?tab=categories&categoryId=' + $(this).data('id');
		});
        $(document).on('click', '.category-delete-btn', function () {
            ConfirmDelete.open($(this).data('name'), function () { remove($(this).data('id')); }.bind(this));
        });
    }

    function loadStatusOptions() {
        $.get(CONTEXT_PATH + 'enums/active-status', function (list) {
            var select = $('#categoryStatus').empty().append('<option value="">Select Status</option>');
            list.forEach(function (o) {
                select.append('<option value="' + o.code + '">' + o.desc + '</option>');
            });
        });
    }

    function loadParentOptions() {
        $('#categoryParentId').select2({
            width: '100%',
            placeholder: 'None (top level)',
            allowClear: true,
            dropdownParent: $('#categoryModal'),
            ajax: {
                url: CONTEXT_PATH + 'inventory/item-categories/select2',
                dataType: 'json',
                delay: 250,
                data: function (params) { return { q: params.term, page: params.page || 0 }; },
                processResults: function (data) { return data; },
                cache: true
            },
            minimumInputLength: 0
        });
    }

    function search(page) {
        if (isLoading) { return; }
        isLoading = true;
        $('#categoryLoadingSpinner').removeClass('d-none');
        $('#categoryTableContainer').addClass('d-none');

        $.ajax({
            url: CONTEXT_PATH + 'inventory/item-categories/search',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ name: $('#categorySearchInput').val(), page: page, size: pageSize }),
            success: function (res) {
                renderTable(res.results);
                updatePaging(res.pageNo, res.totalPage, res.totalRecords);
                $('#categories-count').removeClass('d-none').text(res.totalRecords);
                isLoading = false;
                $('#categoryLoadingSpinner').addClass('d-none');
                $('#categoryTableContainer').removeClass('d-none');
                $('#categoryPagingWrap').removeClass('d-none');
            },
            error: function (xhr) {
                isLoading = false;
                $('#categoryLoadingSpinner').addClass('d-none');
                showToast(xhr.responseText || 'Error loading categories', 'error');
            }
        });
    }

    function renderTable(rows) {
        var body = $('#categoryTableBody');
        body.empty();
        var canEdit = $('#canEditCategory').val() === 'true';
        var canDelete = $('#canDeleteCategory').val() === 'true';

        if (!rows || rows.length === 0) {
            body.append('<tr><td colspan="5" class="text-center text-muted py-4">No categories found</td></tr>');
            return;
        }

        rows.forEach(function (c) {
            var statusBadge = c.status === 1 ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-secondary">Inactive</span>';
            var actions = '';
            if (canEdit) { actions += '<button class="btn btn-outline-primary category-edit-btn" data-id="' + c.id + '"><i class="fas fa-edit"></i></button>'; }
            if (canDelete) { actions += '<button class="btn btn-outline-danger category-delete-btn" data-id="' + c.id + '" data-name="' + c.name + '"><i class="fas fa-trash"></i></button>'; }

            body.append(
                '<tr><td>' + (c.code || '-') + '</td><td>' + (c.name || '-') + '</td><td>' + (c.parentName || '-') + '</td>' +
                '<td>' + statusBadge + '</td><td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
            );
        });
    }

    function updatePaging(page, pages, total) {
        currentPage = page;
        totalPages = pages;
        $('#categoryPageInfo').text('Page ' + (page + 1) + ' of ' + pages + ' (' + total + ' total)');
        $('#categoryPagination').twbsPagination('destroy');
        $('#categoryPagination').twbsPagination({
            totalPages: Math.max(pages, 1),
            visiblePages: 5,
            startPage: page + 1,
            first: 'First', prev: 'Prev', next: 'Next', last: 'Last',
            onPageClick: function (e, p) { if (!isLoading) { search(p - 1); } }
        });
    }

    function openModal(id) {
        $('#categoryForm')[0].reset();
        $('#categoryId').val('');
        $('.invalid-feedback').text('');
        $('#categoryParentId').val(null).trigger('change');

        if (id) {
            $('#categoryModalTitle').text('Update Category');
            $.get(CONTEXT_PATH + 'inventory/item-categories/' + id, function (dto) {
                $('#categoryId').val(dto.id);
                $('#categoryCode').val(dto.code);
                $('#categoryName').val(dto.name);
                $('#categoryStatus').val(dto.status);
                if (dto.parentId) {
                    var opt = new Option(dto.parentName, dto.parentId, true, true);
                    $('#categoryParentId').append(opt).trigger('change');
                }
                $('#categoryModal').modal('show');
                setTimeout(function () { $('#categoryName').trigger('focus'); }, 300);
            }).fail(function (xhr) {
                showToast(xhr.responseText || 'Category not found', 'error');
            });
        } else {
            $('#categoryModalTitle').text('Add Category');
            $('#categoryModal').modal('show');
            setTimeout(function () { $('#categoryCode').trigger('focus'); }, 300);
        }
    }

    function save() {
        var dto = {
            id: $('#categoryId').val() || null,
            code: $('#categoryCode').val(),
            name: $('#categoryName').val(),
            parentId: $('#categoryParentId').val() || null,
            status: $('#categoryStatus').val() ? Number($('#categoryStatus').val()) : null
        };

        $('.invalid-feedback').text('');
        $('#categorySaveBtn').prop('disabled', true);

        $.ajax({
            url: CONTEXT_PATH + 'inventory/item-categories/save',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(dto),
            success: function () {
                $('#categoryModal').modal('hide');
                showToast('Category saved successfully', 'success');
                search(currentPage);
                $('#categorySaveBtn').prop('disabled', false);
            },
            error: function (xhr) {
                $('#categorySaveBtn').prop('disabled', false);
                showToast(xhr.responseText || 'Error saving category', 'error');
            }
        });
    }

    function remove(id) {
        $.post(CONTEXT_PATH + 'inventory/item-categories/delete?id=' + id)
            .done(function () {
                showToast('Category deleted successfully', 'success');
                search(currentPage);
            })
            .fail(function (xhr) {
                showToast(xhr.responseText || 'Error deleting category', 'error');
            });
    }

    return { loadIfNeeded: loadIfNeeded };
})();