$(document).ready(function () {

    $('#locationTabWarehouseId').select2({
        width: '100%',
        placeholder: 'Search and select warehouse',
        ajax: {
            url: CONTEXT_PATH + 'inventory/warehouses/select2',
            dataType: 'json',
            delay: 250,
            data: function (params) { return { q: params.term, page: params.page || 0 }; },
            processResults: function (data) { return data; },
            cache: true
        },
        minimumInputLength: 0
    });

    function refreshParentOptions(warehouseId, excludeId) {
        $('#locationTabParentId').empty();
        $('#locationTabParentId').select2({
            width: '100%',
            placeholder: 'None (top level)',
            allowClear: true,
            ajax: {
                url: CONTEXT_PATH + 'inventory/locations/select2',
                dataType: 'json',
                delay: 250,
                data: function (params) {
                    return { q: params.term, page: params.page || 0, warehouseId: warehouseId, excludeId: excludeId };
                },
                processResults: function (data) { return data; },
                cache: true
            },
            minimumInputLength: 0
        });
    }

    $('#locationTabWarehouseId').on('change', function () {
        refreshParentOptions($(this).val(), $('#locationTabId').val() || null);
    });

    function loadStatusOptions() {
        $.get(CONTEXT_PATH + 'enums/active-status', function (list) {
            var select = $('#locationTabStatus').empty().append('<option value="">Select Status</option>');
            list.forEach(function (o) {
                select.append('<option value="' + o.code + '">' + o.desc + '</option>');
            });
        });
    }
    loadStatusOptions();

    function clearLocationTab() {
        $('#locationTabId').val('');
        $('#locationTabCode').val('');
        $('#locationTabName').val('');
        $('#locationTabType').val('');
        $('#locationTabStatus').val('');
        $('#locationTabWarehouseId').val(null).trigger('change');
        $('#locationTabParentId').val(null).trigger('change');
        $('#locationTabForm').find('.is-invalid').removeClass('is-invalid');
        $('#locationTabForm').find('[data-field-error]').text('');
    }

    function loadLocationForEdit(id) {
        $.get(CONTEXT_PATH + 'inventory/locations/' + id, function (dto) {
            $('#locationTabId').val(dto.id);
            $('#locationTabCode').val(dto.code);
            $('#locationTabName').val(dto.name);
            $('#locationTabType').val(dto.type);
            $('#locationTabStatus').val(dto.status);

            var whOption = new Option(dto.warehouseName, dto.warehouseId, true, true);
            $('#locationTabWarehouseId').append(whOption).trigger('change');

            refreshParentOptions(dto.warehouseId, dto.id);
            if (dto.parentId) {
                var opt = new Option(dto.parentName, dto.parentId, true, true);
                $('#locationTabParentId').append(opt).trigger('change');
            }
        }).fail(function (xhr) {
            showToast(xhr.responseText || 'Location not found', 'error');
        });
    }

    $('#locationTabClearBtn').on('click', clearLocationTab);

    var presetLocationEditId = $('#presetLocationEditId').val();
    if (presetLocationEditId) {
        loadLocationForEdit(presetLocationEditId);
    } else {
        refreshParentOptions(null, null);
    }

    AjaxFormSubmit.init('#locationTabForm');

    $('#locationTabForm').on('ajaxForm:success', function () {
        showToast('Location saved successfully', 'success');
        clearLocationTab();
        window.location.href = CONTEXT_PATH + 'inventory/warehouses?tab=locations';
    });

    $('#warehouseForm').on('submit', function (e) {
        if (!$('#warehouseForm')[0].checkValidity()) {
            e.preventDefault();
            showToast('Please fill all required fields', 'error');
        }
    });
});