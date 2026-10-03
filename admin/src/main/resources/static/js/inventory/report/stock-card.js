var StockCardReport = (function () {

	var currentPage = 0;
	var totalPages = 1;
	var pageSize = 20;
	var isLoading = false;

	function init() {
		initItemSelect();
		initLocationSelect();
		initPagination(0, totalPages);

		$('#scSearchBtn').on('click', function () { search(0); });
		$('#scResetBtn').on('click', function () {
			$('#scItemFilter').val(null).trigger('change');
			$('#scLocationFilter').val(null).trigger('change');
			$('#scMovementFilter').val('');
			$('#scFromDate, #scToDate').val('');
			showEmptyPrompt();
		});

		$('#scItemFilter').on('change', function () {
			if ($(this).val()) {
				search(0);
			} else {
				showEmptyPrompt();
			}
		});
	}

	function initItemSelect() {
		$('#scItemFilter').select2({
			width: '100%',
			placeholder: 'Select item',
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

	function initLocationSelect() {
		$('#scLocationFilter').select2({
			width: '100%',
			placeholder: 'All locations',
			allowClear: true,
			ajax: {
				url: CONTEXT_PATH + 'inventory/locations/select2',
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
		$('#scPagination').twbsPagination({
			totalPages: Math.max(pages, 1),
			visiblePages: 5,
			startPage: page + 1,
			first: 'First', prev: 'Prev', next: 'Next', last: 'Last',
			onPageClick: function (e, p) { if (!isLoading) { search(p - 1); } }
		});
	}

	function search(page) {
		if (!$('#scItemFilter').val()) {
			showEmptyPrompt();
			return;
		}
		if (isLoading) { return; }
		isLoading = true;

		$('#scEmptyPrompt').addClass('d-none');
		$('#scLoadingSpinner').removeClass('d-none');
		$('#scTableContainer').addClass('d-none');
		$('#scPagingWrap').addClass('d-none');

		$.ajax({
			url: CONTEXT_PATH + 'inventory/reports/stock-card/search',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				itemId: $('#scItemFilter').val(),
				locationId: $('#scLocationFilter').val() || null,
				movementType: $('#scMovementFilter').val() ? Number($('#scMovementFilter').val()) : null,
				fromDate: $('#scFromDate').val() || null,
				toDate: $('#scToDate').val() || null
			}),
			success: function (res) {
				renderTable(res.results);
				updatePagination(res.pageNo, res.totalPage);
				$('#scPageInfo').text('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage + ' (' + res.totalRecords + ' total)');
				isLoading = false;
				$('#scLoadingSpinner').addClass('d-none');
				$('#scTableContainer').removeClass('d-none');
				$('#scPagingWrap').removeClass('d-none');
			},
			error: function (xhr) {
				isLoading = false;
				$('#scLoadingSpinner').addClass('d-none');
				showToast(xhr.responseText || 'Error loading stock card', 'error');
			}
		});
	}

	function renderTable(rows) {
		var body = $('#scTableBody');
		body.empty();

		if (!rows || rows.length === 0) {
			body.append('<tr><td colspan="9" class="text-center text-muted py-4">No movements found</td></tr>');
			return;
		}

		rows.forEach(function (l) {
			var refDoc = l.refDocType ? (l.refDocType + ' #' + l.refDocId) : '-';
			body.append(
				'<tr><td>' + (l.postedAt || '-') + '</td><td>' + (l.locationName || '-') + '</td>' +
				'<td>' + (l.movementTypeDesc || '-') + '</td>' +
				'<td>' + (l.qtyIn && l.qtyIn != 0 ? l.qtyIn : '-') + '</td>' +
				'<td>' + (l.qtyOut && l.qtyOut != 0 ? l.qtyOut : '-') + '</td>' +
				'<td>' + (l.balanceAfter != null ? l.balanceAfter : '-') + '</td>' +
				'<td>' + (l.unitCost != null ? l.unitCost : '-') + '</td>' +
				'<td>' + refDoc + '</td><td>' + (l.postedByName || '-') + '</td></tr>'
			);
		});
	}

	function updatePagination(page, pages) {
		if (page !== currentPage || pages !== totalPages) {
			currentPage = page;
			totalPages = pages;
			$('#scPagination').twbsPagination('destroy');
			initPagination(currentPage, Math.max(totalPages, 1));
		}
	}

	function showEmptyPrompt() {
		$('#scEmptyPrompt').removeClass('d-none');
		$('#scTableContainer').addClass('d-none');
		$('#scPagingWrap').addClass('d-none');
	}

	return { init: init };
})();

$(document).ready(function () {
	StockCardReport.init();
});