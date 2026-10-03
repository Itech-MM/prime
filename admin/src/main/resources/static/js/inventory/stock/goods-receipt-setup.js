var GoodsReceiptSetup = (function() {

    var lineIndex = 0;

    function init() {
        initSupplierSelect();
        initWarehouseSelect();
        bindActionButtons();

        if (EXISTING_RECEIPT && EXISTING_RECEIPT.id) {
            populateHeader(EXISTING_RECEIPT);
            (EXISTING_RECEIPT.lines || []).forEach(function(line) {
                addLineRow(line);
            });
        }

        $('#addLineBtn').on('click', function() { addLineRow(null); });

        applyStatusRules();
        recalculateGrandTotal();
    }

    function initSupplierSelect() {
        $('#supplierId').select2({
            width: '100%',
            placeholder: 'Search supplier',
            ajax: {
                url: CONTEXT_PATH + 'inventory/suppliers/select2',
                dataType: 'json',
                delay: 250,
                data: function(params) { return { q: params.term, page: params.page || 0 }; },
                processResults: function(data) { return data; },
                cache: true
            },
            minimumInputLength: 0
        });
    }

    function initWarehouseSelect() {
        $('#warehouseId').select2({
            width: '100%',
            placeholder: 'Search warehouse',
            ajax: {
                url: CONTEXT_PATH + 'inventory/warehouses/select2',
                dataType: 'json',
                delay: 250,
                data: function(params) { return { q: params.term, page: params.page || 0 }; },
                processResults: function(data) { return data; },
                cache: true
            },
            minimumInputLength: 0
        });
        $('#warehouseId').on('change', function() {
            $('.line-location-select').each(function() {
                refreshLocationSelect($(this));
            });
        });
    }

    function populateHeader(dto) {
        if (dto.supplierId) {
            $('#supplierId').append(new Option(dto.supplierName, dto.supplierId, true, true)).trigger('change');
        }
        if (dto.warehouseId) {
            $('#warehouseId').append(new Option(dto.warehouseName, dto.warehouseId, true, true)).trigger('change');
        }
        $('#receivedDate').val(dto.receivedDate || '');
        $('#deliveryNoteNo').val(dto.deliveryNoteNo || '');
        $('#remarks').val(dto.remarks || '');
    }

    function addLineRow(line) {
        $('#noLinesMsg').hide();

        var idx = lineIndex++;
        var row = $(
            '<div class="line-row border rounded p-3 mb-3" data-index="' + idx + '">' +
            '<input type="hidden" class="line-id" value="' + (line && line.id ? line.id : '') + '">' +
            '<input type="hidden" class="line-tracking-type" value="">' +
            '<div class="row g-2 align-items-end">' +
            '<div class="col-md-4"><label class="form-label small fw-bold">Item <span class="text-danger">*</span></label><select class="form-select line-item-select" style="width:100%" required></select></div>' +
            '<div class="col-md-2"><label class="form-label small fw-bold">Qty <span class="text-danger">*</span></label><input type="number" step="0.0001" class="form-control line-qty" required></div>' +
            '<div class="col-md-2"><label class="form-label small fw-bold">UOM</label><select class="form-select line-uom-select" style="width:100%"></select></div>' +
            '<div class="col-md-2"><label class="form-label small fw-bold">Unit Cost <span class="text-danger">*</span></label><input type="number" step="0.0001" class="form-control line-unit-cost" required></div>' +
            '<div class="col-md-1"><label class="form-label small fw-bold">Total</label><div class="form-control-plaintext line-total fw-bold">0.00</div></div>' +
            '<div class="col-md-1 text-end"><button type="button" class="btn btn-outline-danger btn-sm remove-line-btn"><i class="fas fa-trash"></i></button></div>' +
            '</div>' +
            '<div class="row g-2 align-items-end mt-1">' +
            '<div class="col-md-4"><label class="form-label small fw-bold">Location <span class="text-danger">*</span></label><select class="form-select line-location-select" style="width:100%" required></select></div>' +
            '<div class="col-md-8 batch-fields d-none">' +
            '<div class="row g-2">' +
            '<div class="col-md-4"><label class="form-label small fw-bold">Batch No <span class="batch-required text-danger">*</span></label><input type="text" class="form-control line-batch-no"></div>' +
            '<div class="col-md-4"><label class="form-label small fw-bold">Mfg Date</label><input type="date" class="form-control line-mfg-date"></div>' +
            '<div class="col-md-4"><label class="form-label small fw-bold">Expiry Date</label><input type="date" class="form-control line-expiry-date"></div>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="row g-2 mt-1 serial-fields d-none">' +
            '<div class="col-12">' +
            '<label class="form-label small fw-bold">Serial Numbers <span class="text-danger">*</span></label>' +
            '<textarea class="form-control line-serial-nos" rows="2" placeholder="Comma separated, count must match quantity"></textarea>' +
            '</div>' +
            '</div>' +
            '</div>'
        );

        $('#linesContainer').append(row);

        initItemSelect(row, line);
        initUomSelect(row, line);
        initLocationSelect(row, line);

        row.find('.line-qty').val(line ? line.qty : '');
        row.find('.line-unit-cost').val(line ? line.unitCost : '');
        row.find('.line-batch-no').val(line ? line.batchNo : '');
        row.find('.line-mfg-date').val(line ? line.mfgDateIso : '');
        row.find('.line-expiry-date').val(line ? line.expiryDateIso : '');
        row.find('.line-serial-nos').val(line ? line.serialNos : '');
        row.find('.line-total').text(computeLineTotal(row).toFixed(2));

        row.find('.line-qty, .line-unit-cost').on('input', function() {
            row.find('.line-total').text(computeLineTotal(row).toFixed(2));
            recalculateGrandTotal();
        });

        row.find('.remove-line-btn').on('click', function() {
            row.remove();
            recalculateGrandTotal();
            if ($('.line-row').length === 0) { $('#noLinesMsg').show(); }
        });

        applyRowStatusRules(row);
    }

    function initItemSelect(row, line) {
        var select = row.find('.line-item-select');
        select.select2({
            width: '100%',
            placeholder: 'Search item',
            ajax: {
                url: CONTEXT_PATH + 'inventory/items/select2',
                dataType: 'json',
                delay: 250,
                data: function(params) { return { q: params.term, page: params.page || 0 }; },
                processResults: function(data) { return data; },
                cache: true
            },
            minimumInputLength: 0
        });

        if (line && line.itemId) {
            var opt = new Option(line.itemCode + ' - ' + line.itemName, line.itemId, true, true);
            select.append(opt).trigger('change');
            row.find('.line-tracking-type').val(line.trackingType || '');
            toggleTrackingFields(row, line.trackingType);
        }

        select.on('select2:select', function(e) {
            var data = e.params.data;
            row.find('.line-tracking-type').val(data.trackingType || '');
            toggleTrackingFields(row, data.trackingType);

            if (data.baseUomId) {
                var uomSelect = row.find('.line-uom-select');
                uomSelect.empty();
                uomSelect.append(new Option(data.baseUomCode, data.baseUomId, true, true)).trigger('change');
            }

            if (!row.find('.line-unit-cost').val() && data.standardCost) {
                row.find('.line-unit-cost').val(data.standardCost);
                row.find('.line-total').text(computeLineTotal(row).toFixed(2));
                recalculateGrandTotal();
            }
        });
    }

    function initUomSelect(row, line) {
        var select = row.find('.line-uom-select');
        select.select2({
            width: '100%',
            placeholder: 'Unit',
            ajax: {
                url: CONTEXT_PATH + 'inventory/units/select2',
                dataType: 'json',
                delay: 250,
                data: function(params) { return { q: params.term, page: params.page || 0 }; },
                processResults: function(data) { return data; },
                cache: true
            },
            minimumInputLength: 0
        });

        if (line && line.uomId) {
            select.append(new Option(line.uomCode, line.uomId, true, true)).trigger('change');
        }
    }

    function initLocationSelect(row, line) {
        var select = row.find('.line-location-select');
        refreshLocationSelect(select);

        if (line && line.locationId) {
            select.append(new Option(line.locationName, line.locationId, true, true)).trigger('change');
        }
    }

    function refreshLocationSelect(select) {
        var warehouseId = $('#warehouseId').val();
        select.empty();
        select.select2({
            width: '100%',
            placeholder: warehouseId ? 'Search location' : 'Select a warehouse first',
            disabled: !warehouseId,
            ajax: warehouseId ? {
                url: CONTEXT_PATH + 'inventory/locations/select2',
                dataType: 'json',
                delay: 250,
                data: function(params) { return { q: params.term, page: params.page || 0, warehouseId: warehouseId }; },
                processResults: function(data) { return data; },
                cache: true
            } : undefined,
            minimumInputLength: 0
        });
    }

    function toggleTrackingFields(row, trackingType) {
        var t = Number(trackingType);
        row.find('.batch-fields').toggleClass('d-none', t !== 2 && t !== 3);
        row.find('.batch-required').toggleClass('d-none', t !== 2);
        row.find('.serial-fields').toggleClass('d-none', t !== 3);
    }

    function computeLineTotal(row) {
        var qty = parseFloat(row.find('.line-qty').val()) || 0;
        var cost = parseFloat(row.find('.line-unit-cost').val()) || 0;
        return qty * cost;
    }

    function recalculateGrandTotal() {
        var total = 0;
        $('.line-row').each(function() {
            total += computeLineTotal($(this));
        });
        $('#grandTotal').text(total.toFixed(2));
    }

    function collectPayload() {
        var lines = [];
        $('.line-row').each(function() {
            var row = $(this);
            lines.push({
                id: row.find('.line-id').val() || null,
                itemId: row.find('.line-item-select').val(),
                uomId: row.find('.line-uom-select').val(),
                locationId: row.find('.line-location-select').val(),
                qty: parseFloat(row.find('.line-qty').val()) || null,
                unitCost: parseFloat(row.find('.line-unit-cost').val()) || null,
                batchNo: row.find('.line-batch-no').val() || null,
                mfgDate: row.find('.line-mfg-date').val() || null,
                expiryDate: row.find('.line-expiry-date').val() || null,
                serialNos: row.find('.line-serial-nos').val() || null
            });
        });

        return {
            id: $('#receiptId').val() || null,
            supplierId: $('#supplierId').val(),
            warehouseId: $('#warehouseId').val(),
            receivedDate: $('#receivedDate').val() || null,
            deliveryNoteNo: $('#deliveryNoteNo').val(),
            remarks: $('#remarks').val(),
            lines: lines
        };
    }

    function saveDraft(onSuccess) {

        if (!window.validateForm('goodsReceiptForm')) {
            return;
        }
		
		var payload = collectPayload();

        if (!payload.lines.length) {
            showToast('At least one line item is required', 'error');
            return;
        }

        $.ajax({
            url: CONTEXT_PATH + 'inventory/stock-documents/goods-receipts/save',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: function(res) {
                showToast('Goods receipt saved successfully', 'success');
                if (onSuccess) { onSuccess(res); } else { window.location.href = CONTEXT_PATH + 'inventory/stock-documents/goods-receipts/setup?id=' + res.id; }
            },
            error: function(xhr) {
                showToast(xhr.responseText || 'Error saving goods receipt', 'error');
            }
        });
    }

    function applyStatusRules() {
        var status = Number($('#receiptStatus').val());
        var canEdit = $('#canEdit').val() === 'true';
        var canApprove = $('#canApprove').val() === 'true';
        var isNew = !$('#receiptId').val();

        $('#saveDraftBtn, #submitDocBtn, #approveDocBtn, #postDocBtn, #cancelDocBtn, #deleteDocBtn, #addLineBtn').addClass('d-none');

        if (isNew) {
            if (canEdit) { $('#saveDraftBtn, #addLineBtn').removeClass('d-none'); }
            return;
        }

        if (status === 1) {
            if (canEdit) { $('#saveDraftBtn, #submitDocBtn, #deleteDocBtn, #addLineBtn').removeClass('d-none'); }
        } else if (status === 2) {
            if (canApprove) { $('#approveDocBtn').removeClass('d-none'); }
            if (canEdit) { $('#cancelDocBtn').removeClass('d-none'); }
        } else if (status === 3) {
            if (canEdit) { $('#postDocBtn, #cancelDocBtn').removeClass('d-none'); }
        }

        $('.line-row').each(function() { applyRowStatusRules($(this)); });
    }

    function applyRowStatusRules(row) {
        var status = Number($('#receiptStatus').val());
        var isNew = !$('#receiptId').val();
        var locked = !isNew && status !== 1;

        row.find('select, input, textarea, button').prop('disabled', locked);
    }

    function bindActionButtons() {
        $('#saveDraftBtn').on('click', function() { saveDraft(); });

        $('#submitDocBtn').on('click', function() {
            saveDraft(function(res) {
                confirmAction('Submit Goods Receipt', 'Submit this document for approval?', function() {
                    callAction(res.id, 'submit');
                });
            });
        });

        $('#approveDocBtn').on('click', function() {
            confirmAction('Approve Goods Receipt', 'Approve this goods receipt?', function() {
                callAction($('#receiptId').val(), 'approve');
            });
        });

        $('#postDocBtn').on('click', function() {
            confirmAction('Post Goods Receipt', 'Posting will update stock balances immediately and cannot be undone. Continue?', function() {
                callAction($('#receiptId').val(), 'post');
            });
        });

        $('#cancelDocBtn').on('click', function() {
            confirmAction('Cancel Goods Receipt', 'Cancel this goods receipt? This cannot be undone.', function() {
                callAction($('#receiptId').val(), 'cancel');
            });
        });

        $('#deleteDocBtn').on('click', function() {
            confirmAction('Delete Goods Receipt', 'Delete this draft permanently?', function() {
                $.post(CONTEXT_PATH + 'inventory/stock-documents/goods-receipts/delete?id=' + $('#receiptId').val())
                    .done(function() {
                        showToast('Goods receipt deleted', 'success');
                        window.location.href = CONTEXT_PATH + 'inventory/stock-documents';
                    })
                    .fail(function(xhr) { showToast(xhr.responseText || 'Error deleting', 'error'); });
            });
        });
    }

    function callAction(id, action) {
        $.post(CONTEXT_PATH + 'inventory/stock-documents/goods-receipts/' + id + '/' + action)
            .done(function() {
                showToast('Action completed successfully', 'success');
                window.location.reload();
            })
            .fail(function(xhr) {
                showToast(xhr.responseText || 'Action failed', 'error');
            });
    }

    function confirmAction(title, body, onConfirm) {
        $('#confirmActionTitle').text(title);
        $('#confirmActionBody').text(body);
        $('#confirmActionBtn').off('click').on('click', function() {
            bootstrap.Modal.getInstance(document.getElementById('confirmActionModal')).hide();
            onConfirm();
        });
        new bootstrap.Modal(document.getElementById('confirmActionModal')).show();
    }

    return { init: init };
})();

$(document).ready(function() {
    GoodsReceiptSetup.init();
});