var AuditLogReport = (function () {

	var currentPage = 0;
	var totalPages = 1;
	var pageSize = 20;
	var isLoading = false;

	function init() {
		initPagination(0, totalPages);

		$('#alSearchBtn').on('click', function () { search(0); });
		$('#alResetBtn').on('click', function () {
			$('#alDocTypeFilter').val('');
			$('#alDocNoInput').val('');
			$('#alActionFilter').val('');
			$('#alFromDate, #alToDate').val('');
			search(0);
		});
		$('#alDocNoInput').on('keypress', function (e) {
			if (e.which === 13) { e.preventDefault(); search(0); }
		});

		search(0);
	}

	function initPagination(page, pages) {
		$('#alPagination').twbsPagination({
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
		$('#alLoadingSpinner').removeClass('d-none');
		$('#alTableContainer').addClass('d-none');

		$.ajax({
			url: CONTEXT_PATH + 'inventory/reports/audit-log/search',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				docType: $('#alDocTypeFilter').val() || null,
				docNo: $('#alDocNoInput').val() || null,
				action: $('#alActionFilter').val() ? Number($('#alActionFilter').val()) : null,
				fromDate: $('#alFromDate').val() || null,
				toDate: $('#alToDate').val() || null
			}),
			success: function (res) {
				renderTable(res.results);
				updatePagination(res.pageNo, res.totalPage);
				$('#alPageInfo').text('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage + ' (' + res.totalRecords + ' total)');
				isLoading = false;
				$('#alLoadingSpinner').addClass('d-none');
				$('#alTableContainer').removeClass('d-none');
			},
			error: function (xhr) {
				isLoading = false;
				$('#alLoadingSpinner').addClass('d-none');
				showToast(xhr.responseText || 'Error loading audit log', 'error');
			}
		});
	}

	function renderTable(rows) {
		var body = $('#alTableBody');
		body.empty();

		if (!rows || rows.length === 0) {
			body.append('<tr><td colspan="6" class="text-center text-muted py-4">No audit entries found</td></tr>');
			return;
		}

		rows.forEach(function (entry) {
			var actionBadge = actionBadgeClass(entry.action);
			body.append(
				'<tr><td>' + (entry.createdTime || '-') + '</td>' +
				'<td>' + docTypeLabel(entry.docType) + '</td>' +
				'<td>' + (entry.docNo || '-') + '</td>' +
				'<td><span class="badge ' + actionBadge + '">' + (entry.actionDesc || '-') + '</span></td>' +
				'<td>' + (entry.createdByName || '-') + '</td>' +
				'<td>' + (entry.remarks || '-') + '</td></tr>'
			);
		});
	}

	function docTypeLabel(docType) {
		if (docType === 'GOODS_RECEIPT') { return 'Goods Receipt'; }
		if (docType === 'GOODS_ISSUE') { return 'Goods Issue'; }
		if (docType === 'STOCK_ADJUSTMENT') { return 'Stock Adjustment'; }
		return docType || '-';
	}

	function actionBadgeClass(action) {
		switch (Number(action)) {
			case 1: return 'bg-secondary';
			case 2: return 'bg-secondary';
			case 3: return 'bg-info text-dark';
			case 4: return 'bg-primary';
			case 5: return 'bg-success';
			case 6: return 'bg-danger';
			case 7: return 'bg-dark';
			default: return 'bg-secondary';
		}
	}

	function updatePagination(page, pages) {
		if (page !== currentPage || pages !== totalPages) {
			currentPage = page;
			totalPages = pages;
			$('#alPagination').twbsPagination('destroy');
			initPagination(currentPage, Math.max(totalPages, 1));
		}
	}

	return { init: init };
})();

$(document).ready(function () {
	AuditLogReport.init();
});