var GoodsReceiptList = (function () {

	var currentPage = 0;
	var totalPages = 1;
	var pageSize = 10;
	var isLoading = false;

	function init() {
		initPagination(0, totalPages);

		$('#grSearchBtn').on('click', function () { search(0); });
		$('#grResetBtn').on('click', function () {
			$('#grDocNoInput').val('');
			$('#grStatusFilter').val('');
			search(0);
		});
		$('#grDocNoInput').on('keypress', function (e) {
			if (e.which === 13) { e.preventDefault(); search(0); }
		});
		$(document).on('click', '.gr-delete-btn', function () {
			var id = $(this).data('id');
			ConfirmDelete.open($(this).data('name'), function () { remove(id); });
		});
	}

	function initPagination(page, pages) {
		$('#grPagination').twbsPagination({
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
		$('#grLoadingSpinner').removeClass('d-none');
		$('#grTableContainer').addClass('d-none');

		$.ajax({
			url: CONTEXT_PATH + 'inventory/stock-documents/goods-receipts/search',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				docNo: $('#grDocNoInput').val(),
				status: $('#grStatusFilter').val() ? Number($('#grStatusFilter').val()) : null,
				page: page,
				size: pageSize
			}),
			success: function (res) {
				renderTable(res.results);
				updatePagination(res.pageNo, res.totalPage);
				$('#grPageInfo').html('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage);
				isLoading = false;
				$('#grLoadingSpinner').addClass('d-none');
				$('#grTableContainer').removeClass('d-none');
			},
			error: function (xhr) {
				isLoading = false;
				$('#grLoadingSpinner').addClass('d-none');
				showToast(xhr.responseText || 'Error searching goods receipts', 'error');
			}
		});
	}

	function renderTable(rows) {
		var body = $('#grTableBody');
		body.empty();

		if (!rows || rows.length === 0) {
			body.append('<tr><td colspan="7" class="text-center text-muted py-4">No goods receipts found</td></tr>');
			return;
		}

		var canDelete = $('#canDeleteGoodsReceipt').val() === 'true';

		rows.forEach(function (gr) {
			var actions = '<a href="' + CONTEXT_PATH + 'inventory/stock-documents/goods-receipts/setup?id=' + gr.id + '" class="btn btn-outline-primary"><i class="fas fa-eye"></i></a>';
			if (canDelete && gr.status === 1) {
				actions += '<button class="btn btn-outline-danger gr-delete-btn" data-id="' + gr.id + '" data-name="' + gr.docNo + '"><i class="fas fa-trash"></i></button>';
			}

			body.append(
				'<tr><td>' + (gr.docNo || '-') + '</td><td>' + (gr.supplierName || '-') + '</td><td>' + (gr.warehouseName || '-') + '</td>' +
				'<td>' + (gr.receivedDate || '-') + '</td><td>' + (gr.totalAmount || '-') + '</td>' +
				'<td><span class="badge bg-info text-dark">' + (gr.statusDesc || '-') + '</span></td>' +
				'<td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
			);
		});
	}

	function updatePagination(page, pages) {
		if (page !== currentPage || pages !== totalPages) {
			currentPage = page;
			totalPages = pages;
			$('#grPagination').twbsPagination('destroy');
			initPagination(currentPage, Math.max(totalPages, 1));
		}
	}

	function remove(id) {
		$.post(CONTEXT_PATH + 'inventory/stock-documents/goods-receipts/delete?id=' + id)
			.done(function () {
				showToast('Goods receipt deleted successfully', 'success');
				search(currentPage);
			})
			.fail(function (xhr) {
				showToast(xhr.responseText || 'Error deleting goods receipt', 'error');
			});
	}

	return { init: init };
})();

$(document).ready(function () {
	GoodsReceiptList.init();
});