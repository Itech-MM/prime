var GoodsIssueSetup = (function () {

	var lineIndex = 0;

	function init() {
		initWarehouseSelect();
		initCustomerSelect();
		bindIssueTypeToggle();
		bindActionButtons();

		if (EXISTING_ISSUE && EXISTING_ISSUE.id) {
			populateHeader(EXISTING_ISSUE);
			(EXISTING_ISSUE.lines || []).forEach(function (line) {
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

	function initCustomerSelect() {
		$('#customerId').select2({
			width: '100%',
			placeholder: 'Search customer (optional)',
			allowClear: true,
			ajax: {
				url: CONTEXT_PATH + 'customers/select2',
				dataType: 'json',
				delay: 250,
				data: function (params) { return { q: params.term, page: params.page || 0 }; },
				processResults: function (data) { return data; },
				cache: true
			},
			minimumInputLength: 0
		});
	}

	function bindIssueTypeToggle() {
		$('#issueType').on('change', function () {
			var isSale = $(this).val() === '1';
			$('#customerWrap').toggle(isSale);
			$('#issuedToWrap').toggle(!isSale);
		});
	}

	function populateHeader(dto) {
		if (dto.warehouseId) {
			$('#warehouseId').append(new Option(dto.warehouseName, dto.warehouseId, true, true)).trigger('change');
		}
		$('#issueType').val(dto.issueType).trigger('change');
		if (dto.customerId) {
			$('#customerId').append(new Option(dto.customerName, dto.customerId, true, true)).trigger('change');
		}
		$('#issuedToName').val(dto.issuedToName || '');
		$('#issueDate').val(dto.issueDate || '');
		$('#remarks').val(dto.remarks || '');
	}

	function addLineRow(line) {
		$('#noLinesMsg').hide();

		var idx = lineIndex++;
		var row = $(
			'<div class="line-row border rounded p-3 mb-3" data-index="' + idx + '">' +
				'<input type="hidden" class="line-id" value="' + (line && line.id ? line.id : '') + '">' +
				'<input type="hidden" class="line-item-id" value="' + (line && line.itemId ? line.itemId : '') + '">' +
				'<input type="hidden" class="line-tracking-type" value="">' +
				'<div class="row g-2 align-items-end">' +
					'<div class="col-md-4"><label class="form-label small fw-bold">Item <span class="text-danger">*</span></label><select class="form-select line-item-select" style="width:100%"></select></div>' +
					'<div class="col-md-2"><label class="form-label small fw-bold">Qty <span class="text-danger">*</span></label><input type="number" step="0.0001" class="form-control line-qty"></div>' +
					'<div class="col-md-2"><label class="form-label small fw-bold">UOM</label><select class="form-select line-uom-select" style="width:100%"></select></div>' +
					'<div class="col-md-3"><label class="form-label small fw-bold">Location <span class="text-danger">*</span></label><select class="form-select line-location-select" style="width:100%"></select></div>' +
					'<div class="col-md-1 text-end"><button type="button" class="btn btn-outline-danger btn-sm remove-line-btn"><i class="fas fa-trash"></i></button></div>' +
				'</div>' +
				'<div class="row g-2 align-items-end mt-1 batch-fields d-none">' +
					'<div class="col-md-6"><label class="form-label small fw-bold">Batch <span class="text-danger">*</span></label><select class="form-select line-batch-select" style="width:100%"></select></div>' +
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
		initBatchSelect(row, line);

		row.find('.line-qty').val(line ? line.qty : '');
		row.find('.line-serial-nos').val(line ? line.serialNos : '');

		row.find('.remove-line-btn').on('click', function () {
			row.remove();
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

			if (data.baseUomId) {
				var uomSelect = row.find('.line-uom-select');
				uomSelect.empty();
				uomSelect.append(new Option(data.baseUomCode, data.baseUomId, true, true)).trigger('change');
			}
		});
	}

	function initUomSelect(row, line) {
		var select = row.find('.line-uom-select');
		select.select2({ width: '100%', placeholder: 'Unit', minimumInputLength: 0 });
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
			placeholder: itemId ? 'Search batch' : 'Select an item first',
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
		row.find('.batch-fields').toggleClass('d-none', t !== 2);
		row.find('.serial-fields').toggleClass('d-none', t !== 3);
	}

	function collectPayload() {
		var lines = [];
		$('.line-row').each(function () {
			var row = $(this);
			lines.push({
				id: row.find('.line-id').val() || null,
				itemId: row.find('.line-item-id').val(),
				uomId: row.find('.line-uom-select').val(),
				locationId: row.find('.line-location-select').val(),
				batchId: row.find('.line-batch-select').val() || null,
				qty: parseFloat(row.find('.line-qty').val()) || null,
				serialNos: row.find('.line-serial-nos').val() || null
			});
		});

		return {
			id: $('#issueId').val() || null,
			warehouseId: $('#warehouseId').val(),
			issueType: $('#issueType').val() ? Number($('#issueType').val()) : null,
			customerId: $('#customerId').val() || null,
			issuedToName: $('#issuedToName').val(),
			issueDate: $('#issueDate').val() || null,
			remarks: $('#remarks').val(),
			lines: lines
		};
	}

	function saveDraft(onSuccess) {
		var payload = collectPayload();

		if (!payload.warehouseId || !payload.issueType) {
			showToast('Warehouse and issue type are required', 'error');
			return;
		}
		if (!payload.lines.length) {
			showToast('At least one line item is required', 'error');
			return;
		}

		$.ajax({
			url: CONTEXT_PATH + 'inventory/stock-documents/goods-issues/save',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify(payload),
			success: function (res) {
				showToast('Goods issue saved successfully', 'success');
				if (onSuccess) { onSuccess(res); } else { window.location.href = CONTEXT_PATH + 'inventory/stock-documents/goods-issues/setup?id=' + res.id; }
			},
			error: function (xhr) {
				showToast(xhr.responseText || 'Error saving goods issue', 'error');
			}
		});
	}

	function applyStatusRules() {
		var status = Number($('#issueStatus').val());
		var canEdit = $('#canEdit').val() === 'true';
		var canApprove = $('#canApprove').val() === 'true';
		var isNew = !$('#issueId').val();

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
		var status = Number($('#issueStatus').val());
		var isNew = !$('#issueId').val();
		var locked = !isNew && status !== 1;
		row.find('select, input, textarea, button').prop('disabled', locked);
	}

	function bindActionButtons() {
		$('#saveDraftBtn').on('click', function () { saveDraft(); });

		$('#submitDocBtn').on('click', function () {
			saveDraft(function (res) {
				confirmAction('Submit Goods Issue', 'Submit this document for approval?', function () {
					callAction(res.id, 'submit');
				});
			});
		});

		$('#approveDocBtn').on('click', function () {
			confirmAction('Approve Goods Issue', 'Approve this goods issue?', function () {
				callAction($('#issueId').val(), 'approve');
			});
		});

		$('#postDocBtn').on('click', function () {
			confirmAction('Post Goods Issue', 'Posting will deduct stock immediately and cannot be undone. Continue?', function () {
				callAction($('#issueId').val(), 'post');
			});
		});

		$('#cancelDocBtn').on('click', function () {
			confirmAction('Cancel Goods Issue', 'Cancel this goods issue? This cannot be undone.', function () {
				callAction($('#issueId').val(), 'cancel');
			});
		});

		$('#deleteDocBtn').on('click', function () {
			confirmAction('Delete Goods Issue', 'Delete this draft permanently?', function () {
				$.post(CONTEXT_PATH + 'inventory/stock-documents/goods-issues/delete?id=' + $('#issueId').val())
					.done(function () {
						showToast('Goods issue deleted', 'success');
						window.location.href = CONTEXT_PATH + 'inventory/stock-documents?tab=goods-issues';
					})
					.fail(function (xhr) { showToast(xhr.responseText || 'Error deleting', 'error'); });
			});
		});
	}

	function callAction(id, action) {
		$.post(CONTEXT_PATH + 'inventory/stock-documents/goods-issues/' + id + '/' + action)
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
	GoodsIssueSetup.init();
});