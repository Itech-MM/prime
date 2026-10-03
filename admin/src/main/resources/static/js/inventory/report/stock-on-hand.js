var StockOnHandReport = (function () {

	var currentPage = 0;
	var totalPages = 1;
	var pageSize = 20;
	var isLoading = false;

	function init() {
		initItemSelect();
		initWarehouseSelect();
		initPagination(0, totalPages);

		$('#sohSearchBtn').on('click', function () { search(0); });
		$('#sohResetBtn').on('click', function () {
			$('#sohItemFilter').val(null).trigger('change');
			$('#sohWarehouseFilter').val(null).trigger('change');
			$('#sohLowStockOnly').prop('checked', false);
			search(0);
		});

		search(0);
	}

	function initItemSelect() {
		$('#sohItemFilter').select2({
			width: '100%',
			placeholder: 'All items',
			allowClear: true,
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
	}

	function initWarehouseSelect() {
		$('#sohWarehouseFilter').select2({
			width: '100%',
			placeholder: 'All warehouses',
			allowClear: true,
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
	}

	function initPagination(page, pages) {
		$('#sohPagination').twbsPagination({
			totalPages: Math.max(pages, 1),
			visiblePages: 5,
			startPage: page + 1,
			first: 'First', prev: 'Prev', next: 'Next', last: 'Last',
			onPageClick: function (e, p) { if (!isLoading) { search(p - 1); } }
		});
	}

	function search(page) {
		if (isLoading) { return; }
		isLoading = true;
		$('#sohLoadingSpinner').removeClass('d-none');
		$('#sohTableContainer').addClass('d-none');

		$.ajax({
			url: CONTEXT_PATH + 'inventory/reports/stock-on-hand/search',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				itemId: $('#sohItemFilter').val() || null,
				warehouseId: $('#sohWarehouseFilter').val() || null,
				lowStockOnly: $('#sohLowStockOnly').is(':checked')
			}),
			success: function (res) {
				renderTable(res.results);
				updatePagination(res.pageNo, res.totalPage);
				$('#sohPageInfo').text('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage + ' (' + res.totalRecords + ' total)');
				isLoading = false;
				$('#sohLoadingSpinner').addClass('d-none');
				$('#sohTableContainer').removeClass('d-none');
			},
			error: function (xhr) {
				isLoading = false;
				$('#sohLoadingSpinner').addClass('d-none');
				showToast(xhr.responseText || 'Error loading stock on hand', 'error');
			}
		});
	}

	function renderTable(rows) {
		var body = $('#sohTableBody');
		body.empty();

		if (!rows || rows.length === 0) {
			body.append('<tr><td colspan="9" class="text-center text-muted py-4">No stock found</td></tr>');
			return;
		}

		rows.forEach(function (b) {
			body.append(
				'<tr><td>' + (b.itemCode || '') + ' - ' + (b.itemName || '-') + '</td>' +
				'<td>' + (b.warehouseName || '-') + '</td><td>' + (b.locationName || '-') + '</td>' +
				'<td>' + (b.batchNo || '-') + '</td><td>' + (b.qtyOnHand != null ? b.qtyOnHand : '-') + '</td>' +
				'<td>' + (b.qtyReserved != null ? b.qtyReserved : '-') + '</td><td>' + (b.qtyAvailable != null ? b.qtyAvailable : '-') + '</td>' +
				'<td>' + (b.avgCost != null ? b.avgCost : '-') + '</td><td>' + (b.lastMovementAt || '-') + '</td></tr>'
			);
		});
	}

	function updatePagination(page, pages) {
		if (page !== currentPage || pages !== totalPages) {
			currentPage = page;
			totalPages = pages;
			$('#sohPagination').twbsPagination('destroy');
			initPagination(currentPage, Math.max(totalPages, 1));
		}
	}

	return { init: init };
})();

$(document).ready(function () {
	StockOnHandReport.init();
});