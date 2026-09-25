$(document).ready(function() {
    let salesChart = null;
    let productTypeChart = null;
    let currentPreset = 'month';
    let currentStart = null;
    let currentEnd = null;

    const presetLabels = {
        today: 'Today',
        week: 'This Week',
        year: 'This Year',
        month: 'This Month',
        last12months: 'Last 12 Months',
        custom: 'Custom Range'
    };

    function formatDate(d) {
        return d.getFullYear() + '-' +
            String(d.getMonth() + 1).padStart(2, '0') + '-' +
            String(d.getDate()).padStart(2, '0');
    }

    function formatDisplayDate(d) {
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }

    function getPresetRange(preset) {
        const today = new Date();
        let start = new Date(today);
        let end = new Date(today);

        switch (preset) {
            case 'today':
                break;
            case 'week':
                start.setDate(today.getDate() - today.getDay());
                break;
            case 'month':
                start = new Date(today.getFullYear(), today.getMonth(), 1);
                break;
            case 'year':
                start = new Date(today.getFullYear(), 0, 1);
                break;
            case 'last12months':
                start = new Date(today.getFullYear(), today.getMonth() - 11, 1);
                break;
        }
        return { start, end };
    }

    function setActiveRangeLabel(start, end) {
        $('#active-range-label').text(formatDisplayDate(start) + ' – ' + formatDisplayDate(end));
    }

    function showSkeletons() {
        $('.skeleton-container').show();
        $('.dash-content').hide();
        $('#recent-orders-table').empty();
        $('#low-stock-list').empty();
        if (salesChart) salesChart.destroy();
        if (productTypeChart) productTypeChart.destroy();
    }

    function hideSkeletons() {
        $('.skeleton-container').hide();
        $('.dash-content').show();
    }

    function formatDateTime(dateTimeString) {
        if (!dateTimeString) return '';
        const date = new Date(dateTimeString);
        return date.getFullYear() + '-' +
            String(date.getMonth() + 1).padStart(2, '0') + '-' +
            String(date.getDate()).padStart(2, '0') + ' ' +
            String(date.getHours()).padStart(2, '0') + ':' +
            String(date.getMinutes()).padStart(2, '0');
    }

	function getStatusBadge(status) {
	    switch (status) {
	        case 1: return '<span class="badge bg-success">Finished</span>';
	        case 2: return '<span class="badge bg-warning text-dark">Pending</span>';
	        case 3: return '<span class="badge bg-info text-white">Confirmed</span>';
	        case 4: return '<span class="badge bg-primary text-white">Delivery</span>';
	        case 5: return '<span class="badge bg-primary text-white">Shipped</span>';
	        case 6: return '<span class="badge bg-danger">Cancel</span>';
	        default: return '<span class="badge bg-secondary">Unknown</span>';
	    }
	}

    function renderTrend($el, currentVal, previousVal) {
        if (previousVal === undefined || previousVal === null) {
            $el.empty();
            return;
        }
        if (previousVal === 0) {
            if (currentVal === 0) {
                $el.removeClass('up down').addClass('flat').html('<i class="fas fa-minus"></i> No change');
            } else {
                $el.removeClass('down flat').addClass('up').html('<i class="fas fa-arrow-up"></i> New activity');
            }
            return;
        }
        const diff = ((currentVal - previousVal) / previousVal) * 100;
        const rounded = Math.abs(diff).toFixed(1);
        if (diff > 0.05) {
            $el.removeClass('down flat').addClass('up').html(`<i class="fas fa-arrow-up"></i> ${rounded}% vs last period`);
        } else if (diff < -0.05) {
            $el.removeClass('up flat').addClass('down').html(`<i class="fas fa-arrow-down"></i> ${rounded}% vs last period`);
        } else {
            $el.removeClass('up down').addClass('flat').html('<i class="fas fa-minus"></i> No change');
        }
    }

    function loadDashboardData(start, end) {
        showSkeletons();

        const params = {};
        if (start && end) {
            params.startDate = formatDate(start);
            params.endDate = formatDate(end);
            setActiveRangeLabel(start, end);
        }

        $.ajax({
            url: CONTEXT_PATH + 'api/dashboard/summary',
            type: 'GET',
            data: params,
            dataType: 'json',
            success: function(data) {
                $('#revenue-card .h5').text(data.totalRevenue != null ? data.totalRevenue.toFixed(2) : '0.00');
                $('#orders-card .h5').text(data.totalOrders);
                $('#products-card .h5').text(data.totalProducts);
                $('#stock-card .h5').text(data.lowStockCount);

                renderTrend($('#revenue-card .stat-trend'), data.totalRevenue || 0, data.previousRevenue);
                renderTrend($('#orders-card .stat-trend'), data.totalOrders || 0, data.previousOrders);

                if (data.recentOrders && data.recentOrders.length > 0) {
                    $.each(data.recentOrders, function(index, order) {
                        const row = `<tr>
                                        <td>${order.orderCodeNumber || ''}</td>
                                        <td>${order.orderCodeNumber || ''}</td>
                                        <td>${formatDateTime(order.orderDate)}</td>
                                        <td>${order.totalQuantity || 0}</td>
                                        <td>${order.netAmount != null ? order.netAmount.toFixed(2) : '0.00'}</td>
                                        <td>${getStatusBadge(order.orderStatus)}</td>
                                    </tr>`;
                        $('#recent-orders-table').append(row);
                    });
                } else {
                    $('#recent-orders-table').append('<tr><td colspan="6" class="text-center py-4 text-muted">No orders found</td></tr>');
                }

                if (data.lowStockItems && data.lowStockItems.length > 0) {
                    $.each(data.lowStockItems, function(index, item) {
                        const listItem = `<li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                        <div>
                                            <h6 class="my-0 font-weight-bold">${item.name || ''}</h6>
                                            <small class="text-muted">${item.code || ''}</small>
                                        </div>
                                        <span class="badge bg-danger rounded-pill">${item.quantity || 0}</span>
                                    </li>`;
                        $('#low-stock-list').append(listItem);
                    });
                } else {
                    $('#low-stock-list').append('<li class="list-group-item text-center py-4 text-muted">All items are well stocked</li>');
                }

                hideSkeletons();

                const ctxLine = document.getElementById('salesLineChart').getContext('2d');
                salesChart = new Chart(ctxLine, {
                    type: 'line',
                    data: {
                        labels: data.salesLabels && data.salesLabels.length ? data.salesLabels : ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                        datasets: [{
                            label: 'Revenue',
                            data: data.salesData && data.salesData.length ? data.salesData : [0, 0, 0, 0, 0, 0],
                            borderColor: '#0d6efd',
                            backgroundColor: 'rgba(13, 110, 253, 0.05)',
                            fill: true,
                            tension: 0.3
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: { legend: { display: false } },
                        scales: { y: { beginAtZero: true } }
                    }
                });

                const ctxPie = document.getElementById('productTypePieChart').getContext('2d');
                productTypeChart = new Chart(ctxPie, {
                    type: 'doughnut',
                    data: {
                        labels: data.pieLabels && data.pieLabels.length ? data.pieLabels : ['No Data'],
                        datasets: [{
                            data: data.pieData && data.pieData.length ? data.pieData : [100],
                            backgroundColor: ['#0d6efd', '#198754', '#dc3545', '#ffc107', '#0dcaf0', '#6c757d']
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: { legend: { position: 'bottom' } }
                    }
                });
            },
            error: function(xhr, status, error) {
                console.error('Failed to update dashboard data metrics', error);
                hideSkeletons();
            }
        });
    }

    function applyPreset(preset) {
        currentPreset = preset;
        $('.date-filter-option').removeClass('active');
        $(`.date-filter-option[data-preset="${preset}"]`).addClass('active');
        $('#date-filter-current').text(presetLabels[preset]);
        $('#custom-range-panel').removeClass('show');
        $('#date-filter').removeClass('open');

        const { start, end } = getPresetRange(preset);
        currentStart = start;
        currentEnd = end;
        loadDashboardData(start, end);
    }

    // Toggle dropdown
    $('#date-filter-toggle').on('click', function(e) {
        e.stopPropagation();
        $('#date-filter').toggleClass('open');
    });

    $(document).on('click', function(e) {
        if (!$(e.target).closest('#date-filter').length) {
            $('#date-filter').removeClass('open');
            $('#custom-range-panel').removeClass('show');
        }
    });

    // Preset selection
    $(document).on('click', '.date-filter-option', function(e) {
        const preset = $(this).data('preset');

        if (preset === 'custom') {
            e.stopPropagation();
            $('.date-filter-option').removeClass('active');
            $(this).addClass('active');
            $('#custom-range-panel').addClass('show');

            if (currentStart && currentEnd) {
                $('#custom-start-date').val(formatDate(currentStart));
                $('#custom-end-date').val(formatDate(currentEnd));
            }
            return;
        }

        applyPreset(preset);
    });

    $('#apply-custom-range').on('click', function(e) {
        e.stopPropagation();
        const startVal = $('#custom-start-date').val();
        const endVal = $('#custom-end-date').val();

        if (!startVal || !endVal) return;

        const start = new Date(startVal);
        const end = new Date(endVal);

        if (start > end) {
            alert('Start date must be before end date.');
            return;
        }

        currentPreset = 'custom';
        currentStart = start;
        currentEnd = end;
        $('#date-filter-current').text('Custom Range');
        $('#date-filter').removeClass('open');
        $('#custom-range-panel').removeClass('show');

        loadDashboardData(start, end);
    });

    $('#refresh-btn').on('click', function() {
        loadDashboardData(currentStart, currentEnd);
    });

    /*applyPreset('month');*/
});