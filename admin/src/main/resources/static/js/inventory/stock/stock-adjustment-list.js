var StockAdjustmentList = (function () {

	var currentPage = 0;
	var totalPages = 1;
	var pageSize = 10;
	var isLoading = false;

	function init() {
		initPagination(0, totalPages);

		$('#saSearchBtn').on('click', function () { search(0); });
		$('#saResetBtn').on('click', function () {
			$('#saDocNoInput').val('');
			$('#saReasonFilter').val('');
			$('#saStatusFilter').val('');
			search(0);
		});
		$('#saDocNoInput').on('keypress', function (e) {
			if (e.which === 13) { e.preventDefault(); search(0); }
		});
		$(document).on('click', '.sa-delete-btn', function () {
			var id = $(this).data('id');
			ConfirmDelete.open($(this).data('name'), function () { remove(id); });
		});
	}

	function initPagination(page, pages) {
		$('#saPagination').twbsPagination({
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
		$('#saLoadingSpinner').removeClass('d-none');
		$('#saTableContainer').addClass('d-none');

		$.ajax({
			url: CONTEXT_PATH + 'inventory/stock-documents/stock-adjustments/search',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				docNo: $('#saDocNoInput').val(),
				reason: $('#saReasonFilter').val() ? Number($('#saReasonFilter').val()) : null,
				status: $('#saStatusFilter').val() ? Number($('#saStatusFilter').val()) : null,
				page: page,
				size: pageSize
			}),
			success: function (res) {
				renderTable(res.results);
				updatePagination(res.pageNo, res.totalPage);
				$('#saPageInfo').html('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage);
				isLoading = false;
				$('#saLoadingSpinner').addClass('d-none');
				$('#saTableContainer').removeClass('d-none');
			},
			error: function (xhr) {
				isLoading = false;
				$('#saLoadingSpinner').addClass('d-none');
				showToast(xhr.responseText || 'Error searching stock adjustments', 'error');
			}
		});
	}

	function renderTable(rows) {
		var body = $('#saTableBody');
		body.empty();

		if (!rows || rows.length === 0) {
			body.append('<tr><td colspan="5" class="text-center text-muted py-4">No stock adjustments found</td></tr>');
			return;
		}

		var canDelete = $('#canDeleteStockAdjustment').val() === 'true';

		rows.forEach(function (sa) {
			var actions = '<a href="' + CONTEXT_PATH + 'inventory/stock-documents/stock-adjustments/setup?id=' + sa.id + '" class="btn btn-outline-primary"><i class="fas fa-eye"></i></a>';
			if (canDelete && sa.status === 1) {
				actions += '<button class="btn btn-outline-danger sa-delete-btn" data-id="' + sa.id + '" data-name="' + sa.docNo + '"><i class="fas fa-trash"></i></button>';
			}

			body.append(
				'<tr><td>' + (sa.docNo || '-') + '</td><td>' + (sa.warehouseName || '-') + '</td><td>' + (sa.reasonDesc || '-') + '</td>' +
				'<td><span class="badge bg-info text-dark">' + (sa.statusDesc || '-') + '</span></td>' +
				'<td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
			);
		});
	}

	function updatePagination(page, pages) {
		if (page !== currentPage || pages !== totalPages) {
			currentPage = page;
			totalPages = pages;
			$('#saPagination').twbsPagination('destroy');
			initPagination(currentPage, Math.max(totalPages, 1));
		}
	}

	function remove(id) {
		$.post(CONTEXT_PATH + 'inventory/stock-documents/stock-adjustments/delete?id=' + id)
			.done(function () {
				showToast('Stock adjustment deleted successfully', 'success');
				search(currentPage);
			})
			.fail(function (xhr) {
				showToast(xhr.responseText || 'Error deleting stock adjustment', 'error');
			});
	}

	return { init: init };
})();

$(document).ready(function () {
	StockAdjustmentList.init();
});