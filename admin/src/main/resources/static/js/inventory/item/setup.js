$(document).ready(function () {

    $('#categoryId').select2({
        width: '100%',
        placeholder: 'Search and select category',
        ajax: {
            url: CONTEXT_PATH + 'inventory/item-categories/select2',
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return { q: params.term, page: params.page || 0 };
            },
            processResults: function (data) { return data; },
            cache: true
        },
        minimumInputLength: 0
    });

    $('#brandId').select2({
        width: '100%',
        placeholder: 'Search and select brand (optional)',
        allowClear: true,
        ajax: {
            url: CONTEXT_PATH + 'inventory/brands/select2',
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return { q: params.term, page: params.page || 0 };
            },
            processResults: function (data) { return data; },
            cache: true
        },
        minimumInputLength: 0
    });

    $('#baseUomId').select2({
        width: '100%',
        placeholder: 'Search and select unit',
        ajax: {
            url: CONTEXT_PATH + 'inventory/units/select2',
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return { q: params.term, page: params.page || 0 };
            },
            processResults: function (data) { return data; },
            cache: true
        },
        minimumInputLength: 0
    });

    if (PRESET_CATEGORY) {
        var catOption = new Option(PRESET_CATEGORY_NAME, PRESET_CATEGORY, true, true);
        $('#categoryId').append(catOption).trigger('change');
    }
    if (PRESET_BRAND) {
        var brandOption = new Option(PRESET_BRAND_NAME, PRESET_BRAND, true, true);
        $('#brandId').append(brandOption).trigger('change');
    }
    if (PRESET_UOM) {
        var uomOption = new Option(PRESET_UOM_NAME, PRESET_UOM, true, true);
        $('#baseUomId').append(uomOption).trigger('change');
    }

    function toggleShelfLife() {
        if ($('#hasExpiry').is(':checked')) {
            $('#shelfLifeWrap').show();
        } else {
            $('#shelfLifeWrap').hide();
        }
    }
    toggleShelfLife();
    $('#hasExpiry').on('change', toggleShelfLife);

    $('#itemForm').on('submit', function (e) {
        var valid = true;
        ['#categoryId', '#baseUomId'].forEach(function (sel) {
            if (!$(sel).val()) {
                $(sel).next('.select2-container').find('.select2-selection').addClass('is-invalid');
                valid = false;
            } else {
                $(sel).next('.select2-container').find('.select2-selection').removeClass('is-invalid');
            }
        });
        if (!valid) {
            e.preventDefault();
            showToast('Please fill all required fields', 'error');
        }
    });

    var cameFromItemTab = false;

    function showTab(tabId) {
        var el = document.getElementById(tabId + '-tab');
        if (el) {
            new bootstrap.Tab(el).show();
        }
    }

    function initialActivate() {
        var tab = $('#activeTab').val() || 'items';
        showTab(tab);
    }
    initialActivate();

    $('#quickAddCategoryBtn').on('click', function () {
        cameFromItemTab = true;
        showTab('categories');
        setTimeout(function () { $('#categoryTabCode').trigger('focus'); }, 300);
    });
    $('#quickAddBrandBtn').on('click', function () {
        cameFromItemTab = true;
        showTab('brands');
        setTimeout(function () { $('#brandTabCode').trigger('focus'); }, 300);
    });
    $('#quickAddUomBtn').on('click', function () {
        cameFromItemTab = true;
        showTab('units');
        setTimeout(function () { $('#uomTabCode').trigger('focus'); }, 300);
    });

    function loadStatusOptionsInto(selectId) {
        $.get(CONTEXT_PATH + 'enums/active-status', function (list) {
            var select = $(selectId).empty().append('<option value="">Select Status</option>');
            list.forEach(function (o) {
                select.append('<option value="' + o.code + '">' + o.desc + '</option>');
            });
        });
    }

    function returnAfterSave(tabName) {
        if (cameFromItemTab) {
            cameFromItemTab = false;
            showTab('items');
        } else {
            window.location.href = CONTEXT_PATH + 'inventory/items?tab=' + tabName;
        }
    }

    loadStatusOptionsInto('#categoryTabStatus');

    $('#categoryTabParentId').select2({
        width: '100%',
        placeholder: 'None (top level)',
        allowClear: true,
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

    function clearCategoryTab() {
        $('#categoryTabId').val('');
        $('#categoryTabCode').val('');
        $('#categoryTabName').val('');
        $('#categoryTabStatus').val('');
        $('#categoryTabParentId').val(null).trigger('change');
        $('#categoryTabForm').find('.is-invalid').removeClass('is-invalid');
        $('#categoryTabForm').find('[data-field-error]').text('');
    }

    function loadCategoryForEdit(id) {
        $.get(CONTEXT_PATH + 'inventory/item-categories/' + id, function (dto) {
            $('#categoryTabId').val(dto.id);
            $('#categoryTabCode').val(dto.code);
            $('#categoryTabName').val(dto.name);
            $('#categoryTabStatus').val(dto.status);
            if (dto.parentId) {
                var opt = new Option(dto.parentName, dto.parentId, true, true);
                $('#categoryTabParentId').append(opt).trigger('change');
            }
        }).fail(function (xhr) {
            showToast(xhr.responseText || 'Category not found', 'error');
        });
    }

    $('#categoryTabClearBtn').on('click', clearCategoryTab);

    var presetCategoryEditId = $('#presetCategoryEditId').val();
    if (presetCategoryEditId) {
        loadCategoryForEdit(presetCategoryEditId);
    }

    loadStatusOptionsInto('#brandTabStatus');

    function clearBrandTab() {
        $('#brandTabId').val('');
        $('#brandTabCode').val('');
        $('#brandTabName').val('');
        $('#brandTabStatus').val('');
        $('#brandTabForm').find('.is-invalid').removeClass('is-invalid');
        $('#brandTabForm').find('[data-field-error]').text('');
    }

    function loadBrandForEdit(id) {
        $.get(CONTEXT_PATH + 'inventory/brands/' + id, function (dto) {
            $('#brandTabId').val(dto.id);
            $('#brandTabCode').val(dto.code);
            $('#brandTabName').val(dto.name);
            $('#brandTabStatus').val(dto.status);
        }).fail(function (xhr) {
            showToast(xhr.responseText || 'Brand not found', 'error');
        });
    }

    $('#brandTabClearBtn').on('click', clearBrandTab);

    var presetBrandEditId = $('#presetBrandEditId').val();
    if (presetBrandEditId) {
        loadBrandForEdit(presetBrandEditId);
    }

    loadStatusOptionsInto('#uomTabStatus');

    function clearUomTab() {
        $('#uomTabId').val('');
        $('#uomTabCode').val('');
        $('#uomTabName').val('');
        $('#uomTabStatus').val('');
        $('#uomTabForm').find('.is-invalid').removeClass('is-invalid');
        $('#uomTabForm').find('[data-field-error]').text('');
    }

    function loadUomForEdit(id) {
        $.get(CONTEXT_PATH + 'inventory/units/' + id, function (dto) {
            $('#uomTabId').val(dto.id);
            $('#uomTabCode').val(dto.code);
            $('#uomTabName').val(dto.name);
            $('#uomTabStatus').val(dto.status);
        }).fail(function (xhr) {
            showToast(xhr.responseText || 'Unit not found', 'error');
        });
    }

    $('#uomTabClearBtn').on('click', clearUomTab);

    var presetUomEditId = $('#presetUomEditId').val();
    if (presetUomEditId) {
        loadUomForEdit(presetUomEditId);
    }

    AjaxFormSubmit.init('#categoryTabForm, #brandTabForm, #uomTabForm');

    $('#categoryTabForm').on('ajaxForm:success', function () {
        showToast('Category saved successfully', 'success');
        clearCategoryTab();
        returnAfterSave('categories');
    });

    $('#brandTabForm').on('ajaxForm:success', function () {
        showToast('Brand saved successfully', 'success');
        clearBrandTab();
        returnAfterSave('brands');
    });

    $('#uomTabForm').on('ajaxForm:success', function () {
        showToast('Unit saved successfully', 'success');
        clearUomTab();
        returnAfterSave('units');
    });
});