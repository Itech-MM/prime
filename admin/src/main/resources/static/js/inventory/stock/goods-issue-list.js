var GoodsIssueList = (function () {

	var currentPage = 0;
	var totalPages = 1;
	var pageSize = 10;
	var isLoading = false;

	function init() {
		initPagination(0, totalPages);

		$('#giSearchBtn').on('click', function () { search(0); });
		$('#giResetBtn').on('click', function () {
			$('#giDocNoInput').val('');
			$('#giTypeFilter').val('');
			$('#giStatusFilter').val('');
			search(0);
		});
		$('#giDocNoInput').on('keypress', function (e) {
			if (e.which === 13) { e.preventDefault(); search(0); }
		});
		$(document).on('click', '.gi-delete-btn', function () {
			var id = $(this).data('id');
			ConfirmDelete.open($(this).data('name'), function () { remove(id); });
		});
	}

	function initPagination(page, pages) {
		$('#giPagination').twbsPagination({
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
		$('#giLoadingSpinner').removeClass('d-none');
		$('#giTableContainer').addClass('d-none');

		$.ajax({
			url: CONTEXT_PATH + 'inventory/stock-documents/goods-issues/search',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				docNo: $('#giDocNoInput').val(),
				issueType: $('#giTypeFilter').val() ? Number($('#giTypeFilter').val()) : null,
				status: $('#giStatusFilter').val() ? Number($('#giStatusFilter').val()) : null,
				page: page,
				size: pageSize
			}),
			success: function (res) {
				renderTable(res.results);
				updatePagination(res.pageNo, res.totalPage);
				$('#giPageInfo').html('Page ' + (res.pageNo + 1) + ' of ' + res.totalPage);
				isLoading = false;
				$('#giLoadingSpinner').addClass('d-none');
				$('#giTableContainer').removeClass('d-none');
			},
			error: function (xhr) {
				isLoading = false;
				$('#giLoadingSpinner').addClass('d-none');
				showToast(xhr.responseText || 'Error searching goods issues', 'error');
			}
		});
	}

	function renderTable(rows) {
		var body = $('#giTableBody');
		body.empty();

		if (!rows || rows.length === 0) {
			body.append('<tr><td colspan="7" class="text-center text-muted py-4">No goods issues found</td></tr>');
			return;
		}

		var canDelete = $('#canDeleteGoodsIssue').val() === 'true';

		rows.forEach(function (gi) {
			var actions = '<a href="' + CONTEXT_PATH + 'inventory/stock-documents/goods-issues/setup?id=' + gi.id + '" class="btn btn-outline-primary"><i class="fas fa-eye"></i></a>';
			if (canDelete && gi.status === 1) {
				actions += '<button class="btn btn-outline-danger gi-delete-btn" data-id="' + gi.id + '" data-name="' + gi.docNo + '"><i class="fas fa-trash"></i></button>';
			}

			var issuedTo = gi.customerName || gi.issuedToName || '-';

			body.append(
				'<tr><td>' + (gi.docNo || '-') + '</td><td>' + (gi.warehouseName || '-') + '</td><td>' + (gi.issueTypeDesc || '-') + '</td>' +
				'<td>' + issuedTo + '</td><td>' + (gi.totalAmount || '-') + '</td>' +
				'<td><span class="badge bg-info text-dark">' + (gi.statusDesc || '-') + '</span></td>' +
				'<td><div class="btn-group btn-group-sm">' + actions + '</div></td></tr>'
			);
		});
	}

	function updatePagination(page, pages) {
		if (page !== currentPage || pages !== totalPages) {
			currentPage = page;
			totalPages = pages;
			$('#giPagination').twbsPagination('destroy');
			initPagination(currentPage, Math.max(totalPages, 1));
		}
	}

	function remove(id) {
		$.post(CONTEXT_PATH + 'inventory/stock-documents/goods-issues/delete?id=' + id)
			.done(function () {
				showToast('Goods issue deleted successfully', 'success');
				search(currentPage);
			})
			.fail(function (xhr) {
				showToast(xhr.responseText || 'Error deleting goods issue', 'error');
			});
	}

	return { init: init };
})();

$(document).ready(function () {
	GoodsIssueList.init();
});