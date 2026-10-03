var StockAdjustmentSetup = (function () {

	var lineIndex = 0;

	function init() {
		initWarehouseSelect();
		bindActionButtons();

		if (EXISTING_ADJUSTMENT && EXISTING_ADJUSTMENT.id) {
			populateHeader(EXISTING_ADJUSTMENT);
			(EXISTING_ADJUSTMENT.lines || []).forEach(function (line) {
				addLineRow(line);
			});
		}

		$('#addLineBtn').on('click', function () { addLineRow(null); });

		applyStatusRules();
	}

	function initWarehouseSelect() {
		$('#warehouseId').select2({
			width: '100%',
			placeholder: 'Search warehouse',
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
		$('#warehouseId').on('change', function () {
			$('.line-location-select').each(function () {
				refreshLocationSelect($(this));
			});
		});
	}

	function populateHeader(dto) {
		if (dto.warehouseId) {
			$('#warehouseId').append(new Option(dto.warehouseName, dto.warehouseId, true, true)).trigger('change');
		}
		$('#reason').val(dto.reason || '');
		$('#adjustmentDate').val(dto.adjustmentDateIso || '');
		$('#remarks').val(dto.remarks || '');
	}

	function addLineRow(line) {
		$('#noLinesMsg').hide();

		var idx = lineIndex++;
		var diff = line && line.differenceQty != null ? line.differenceQty : null;

		var row = $(
			'<div class="line-row border rounded p-3 mb-3" data-index="' + idx + '">' +
				'<input type="hidden" class="line-id" value="' + (line && line.id ? line.id : '') + '">' +
				'<input type="hidden" class="line-item-id" value="' + (line && line.itemId ? line.itemId : '') + '">' +
				'<input type="hidden" class="line-tracking-type" value="">' +
				'<div class="row g-2 align-items-end">' +
					'<div class="col-md-3"><label class="form-label small fw-bold">Item <span class="text-danger">*</span></label><select class="form-select line-item-select" style="width:100%"></select></div>' +
					'<div class="col-md-3"><label class="form-label small fw-bold">Location <span class="text-danger">*</span></label><select class="form-select line-location-select" style="width:100%"></select></div>' +
					'<div class="col-md-2"><label class="form-label small fw-bold">System Qty</label><div class="form-control-plaintext fw-bold line-system-qty">' + (line ? line.systemQty : '-') + '</div></div>' +
					'<div class="col-md-2"><label class="form-label small fw-bold">Actual Qty <span class="text-danger">*</span></label><input type="number" step="0.0001" class="form-control line-actual-qty"></div>' +
					'<div class="col-md-1"><label class="form-label small fw-bold">Diff</label><div class="form-control-plaintext fw-bold line-diff-qty">' + (diff != null ? diff : '-') + '</div></div>' +
					'<div class="col-md-1 text-end"><button type="button" class="btn btn-outline-danger btn-sm remove-line-btn"><i class="fas fa-trash"></i></button></div>' +
				'</div>' +
				'<div class="row g-2 align-items-end mt-1 batch-fields d-none">' +
					'<div class="col-md-4"><label class="form-label small fw-bold">Batch</label><select class="form-select line-batch-select" style="width:100%"></select></div>' +
					'<div class="col-md-4"><label class="form-label small fw-bold">Unit Cost <span class="text-muted">(increases only)</span></label><input type="number" step="0.0001" class="form-control line-unit-cost"></div>' +
				'</div>' +
				'<div class="row g-2 align-items-end mt-1" id="noBatchCostWrap">' +
					'<div class="col-md-4"><label class="form-label small fw-bold">Unit Cost <span class="text-muted">(increases only)</span></label><input type="number" step="0.0001" class="form-control line-unit-cost-nobatch"></div>' +
				'</div>' +
				'<div class="row g-2 mt-1">' +
					'<div class="col-12"><label class="form-label small fw-bold">Line Remarks</label><input type="text" class="form-control line-remarks" value="' + (line && line.remarks ? line.remarks : '') + '"></div>' +
				'</div>' +
			'</div>'
		);

		$('#linesContainer').append(row);

		initItemSelect(row, line);
		initLocationSelect(row, line);
		initBatchSelect(row, line);

		row.find('.line-actual-qty').val(line ? line.actualQty : '');
		row.find('.line-unit-cost, .line-unit-cost-nobatch').val(line ? line.unitCost : '');

		row.find('.line-actual-qty').on('input', function () {
			recalcDiff(row);
		});

		row.find('.remove-line-btn').on('click', function () {
			row.remove();
			if ($('.line-row').length === 0) { $('#noLinesMsg').show(); }
		});

		applyRowStatusRules(row);
	}

	function recalcDiff(row) {
		var systemQty = parseFloat(row.find('.line-system-qty').text()) || 0;
		var actualQty = parseFloat(row.find('.line-actual-qty').val());
		if (isNaN(actualQty)) {
			row.find('.line-diff-qty').text('-');
			return;
		}
		row.find('.line-diff-qty').text((actualQty - systemQty).toFixed(4));
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
				data: function (params) { return { q: params.term, page: params.page || 0 }; },
				processResults: function (data) { return data; },
				cache: true
			},
			minimumInputLength: 0
		});

		if (line && line.itemId) {
			select.append(new Option(line.itemCode + ' - ' + line.itemName, line.itemId, true, true)).trigger('change');
			row.find('.line-item-id').val(line.itemId);
			row.find('.line-tracking-type').val(line.trackingType || '');
			toggleTrackingFields(row, line.trackingType);
		}

		select.on('select2:select', function (e) {
			var data = e.params.data;
			row.find('.line-item-id').val(data.id);
			row.find('.line-tracking-type').val(data.trackingType || '');
			toggleTrackingFields(row, data.trackingType);
			refreshBatchSelect(row);
		});
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
				data: function (params) { return { q: params.term, page: params.page || 0, warehouseId: warehouseId }; },
				processResults: function (data) { return data; },
				cache: true
			} : undefined,
			minimumInputLength: 0
		});
	}

	function initBatchSelect(row, line) {
		refreshBatchSelect(row);
		if (line && line.batchId) {
			row.find('.line-batch-select').append(new Option(line.batchNo, line.batchId, true, true)).trigger('change');
		}
	}

	function refreshBatchSelect(row) {
		var select = row.find('.line-batch-select');
		var itemId = row.find('.line-item-id').val();
		select.empty();
		select.select2({
			width: '100%',
			placeholder: itemId ? 'Search batch (optional)' : 'Select an item first',
			allowClear: true,
			disabled: !itemId,
			ajax: itemId ? {
				url: CONTEXT_PATH + 'inventory/stock-batches/select2',
				dataType: 'json',
				delay: 250,
				data: function (params) { return { q: params.term, itemId: itemId }; },
				processResults: function (data) { return data; },
				cache: true
			} : undefined,
			minimumInputLength: 0
		});
	}

	function toggleTrackingFields(row, trackingType) {
		var t = Number(trackingType);
		var showBatch = t === 2 || t === 3;
		row.find('.batch-fields').toggleClass('d-none', !showBatch);
		row.find('#noBatchCostWrap').toggleClass('d-none', showBatch);
	}

	function collectPayload() {
		var lines = [];
		$('.line-row').each(function () {
			var row = $(this);
			var unitCost = row.find('.line-unit-cost').is(':visible')
				? row.find('.line-unit-cost').val()
				: row.find('.line-unit-cost-nobatch').val();

			lines.push({
				id: row.find('.line-id').val() || null,
				itemId: row.find('.line-item-id').val(),
				locationId: row.find('.line-location-select').val(),
				batchId: row.find('.line-batch-select').val() || null,
				actualQty: parseFloat(row.find('.line-actual-qty').val()),
				unitCost: unitCost ? parseFloat(unitCost) : null,
				remarks: row.find('.line-remarks').val() || null
			});
		});

		return {
			id: $('#adjustmentId').val() || null,
			warehouseId: $('#warehouseId').val(),
			reason: $('#reason').val() ? Number($('#reason').val()) : null,
			adjustmentDate: $('#adjustmentDate').val() || null,
			remarks: $('#remarks').val(),
			lines: lines
		};
	}

	function saveDraft(onSuccess) {
		var payload = collectPayload();

		if (!payload.warehouseId || !payload.reason) {
			showToast('Warehouse and reason are required', 'error');
			return;
		}
		if (!payload.lines.length) {
			showToast('At least one line item is required', 'error');
			return;
		}

		$.ajax({
			url: CONTEXT_PATH + 'inventory/stock-documents/stock-adjustments/save',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(payload),
			success: function (res) {
				showToast('Stock adjustment saved successfully', 'success');
				if (onSuccess) { onSuccess(res); } else { window.location.href = CONTEXT_PATH + 'inventory/stock-documents/stock-adjustments/setup?id=' + res.id; }
			},
			error: function (xhr) {
				showToast(xhr.responseText || 'Error saving stock adjustment', 'error');
			}
		});
	}

	function applyStatusRules() {
		var status = Number($('#adjustmentStatus').val());
		var canEdit = $('#canEdit').val() === 'true';
		var canApprove = $('#canApprove').val() === 'true';
		var isNew = !$('#adjustmentId').val();

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

		$('.line-row').each(function () { applyRowStatusRules($(this)); });
	}

	function applyRowStatusRules(row) {
		var status = Number($('#adjustmentStatus').val());
		var isNew = !$('#adjustmentId').val();
		var locked = !isNew && status !== 1;
		row.find('select, input, textarea, button').prop('disabled', locked);
	}

	function bindActionButtons() {
		$('#saveDraftBtn').on('click', function () { saveDraft(); });

		$('#submitDocBtn').on('click', function () {
			saveDraft(function (res) {
				confirmAction('Submit Stock Adjustment', 'Submit this document for approval?', function () {
					callAction(res.id, 'submit');
				});
			});
		});

		$('#approveDocBtn').on('click', function () {
			confirmAction('Approve Stock Adjustment', 'Approve this stock adjustment?', function () {
				callAction($('#adjustmentId').val(), 'approve');
			});
		});

		$('#postDocBtn').on('click', function () {
			confirmAction('Post Stock Adjustment', 'Posting will update stock immediately based on the current system balance and cannot be undone. Continue?', function () {
				callAction($('#adjustmentId').val(), 'post');
			});
		});

		$('#cancelDocBtn').on('click', function () {
			confirmAction('Cancel Stock Adjustment', 'Cancel this stock adjustment? This cannot be undone.', function () {
				callAction($('#adjustmentId').val(), 'cancel');
			});
		});

		$('#deleteDocBtn').on('click', function () {
			confirmAction('Delete Stock Adjustment', 'Delete this draft permanently?', function () {
				$.post(CONTEXT_PATH + 'inventory/stock-documents/stock-adjustments/delete?id=' + $('#adjustmentId').val())
					.done(function () {
						showToast('Stock adjustment deleted', 'success');
						window.location.href = CONTEXT_PATH + 'inventory/stock-documents?tab=stock-adjustments';
					})
					.fail(function (xhr) { showToast(xhr.responseText || 'Error deleting', 'error'); });
			});
		});
	}

	function callAction(id, action) {
		$.post(CONTEXT_PATH + 'inventory/stock-documents/stock-adjustments/' + id + '/' + action)
			.done(function () {
				showToast('Action completed successfully', 'success');
				window.location.reload();
			})
			.fail(function (xhr) {
				showToast(xhr.responseText || 'Action failed', 'error');
			});
	}

	function confirmAction(title, body, onConfirm) {
		$('#confirmActionTitle').text(title);
		$('#confirmActionBody').text(body);
		$('#confirmActionBtn').off('click').on('click', function () {
			bootstrap.Modal.getInstance(document.getElementById('confirmActionModal')).hide();
			onConfirm();
		});
		new bootstrap.Modal(document.getElementById('confirmActionModal')).show();
	}

	return { init: init };
})();

$(document).ready(function () {
	StockAdjustmentSetup.init();
});