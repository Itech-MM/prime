$(document).ready(function() {
    let issuedChart = null;
    let revenueChart = null;
    let movementChart = null;
    let categoryChart = null;
    let currentPreset = 'month';
    let currentStart = null;
    let currentEnd = null;

    const PALETTE = ['#6366f1', '#14b8a6', '#f59e0b', '#ec4899', '#0ea5e9', '#22c55e', '#f97316', '#8b5cf6'];
    const GREEN = '#10b981';
    const ROSE = '#f43f5e';
    const INDIGO = '#6366f1';

    const presetKeys = {
        today: 'common.today',
        week: 'common.thisWeek',
        year: 'common.thisYear',
        month: 'common.thisMonth',
        last12months: 'common.last12Months',
        custom: 'common.customRange'
    };

    const compactFormatter = new Intl.NumberFormat('en-US', { notation: 'compact', maximumFractionDigits: 1 });

    function presetLabel(preset) {
        return I18n.t(presetKeys[preset]);
    }

    function esc(value) {
        return String(value === null || value === undefined ? '' : value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function fmtNumber(value) {
        return Number(value || 0).toLocaleString('en-US', { maximumFractionDigits: 2 });
    }

    function fmtCompact(value) {
        return compactFormatter.format(Number(value || 0));
    }

    function hexToRgba(hex, alpha) {
        const n = parseInt(hex.slice(1), 16);
        return 'rgba(' + ((n >> 16) & 255) + ',' + ((n >> 8) & 255) + ',' + (n & 255) + ',' + alpha + ')';
    }

    function gradientFill(ctx, hex, height) {
        const gradient = ctx.createLinearGradient(0, 0, 0, height || 260);
        gradient.addColorStop(0, hexToRgba(hex, 0.38));
        gradient.addColorStop(1, hexToRgba(hex, 0.02));
        return gradient;
    }

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

    function destroyCharts() {
        [issuedChart, revenueChart, movementChart, categoryChart].forEach(function(chart) {
            if (chart) chart.destroy();
        });
        issuedChart = null;
        revenueChart = null;
        movementChart = null;
        categoryChart = null;
    }

    function showSkeletons() {
        $('#dashboard-root').addClass('is-loading');
        $('#revenue-no-data, #category-no-data').removeClass('show');
        $('#revenue-chart-wrapper, #category-chart-wrapper').removeClass('d-none');
        $('#recent-licenses-table, #expiring-list, #low-stock-table, #recent-movements-table').empty();
        destroyCharts();
    }

    function hideSkeletons() {
        $('#dashboard-root').removeClass('is-loading');
    }

    function getStatusBadge(statusDesc) {
        if (!statusDesc) return `<span class="badge bg-secondary">${esc(I18n.t('common.unknown'))}</span>`;
        const safe = esc(statusDesc);
        const lower = statusDesc.toLowerCase();
        if (lower.includes('active')) return `<span class="badge bg-success">${safe}</span>`;
        if (lower.includes('grace')) return `<span class="badge bg-warning text-dark">${safe}</span>`;
        if (lower.includes('expired')) return `<span class="badge bg-danger">${safe}</span>`;
        return `<span class="badge bg-secondary">${safe}</span>`;
    }

    function renderTrend($el, currentVal, previousVal) {
        if (previousVal === undefined || previousVal === null) {
            $el.empty();
            return;
        }
        if (previousVal === 0) {
            if (currentVal === 0) {
                $el.removeClass('up down').addClass('flat').html('<i class="fas fa-minus"></i> ' + esc(I18n.t('dashboard.trend.noChange')));
            } else {
                $el.removeClass('down flat').addClass('up').html('<i class="fas fa-arrow-up"></i> ' + esc(I18n.t('dashboard.trend.new')));
            }
            return;
        }
        const diff = ((currentVal - previousVal) / previousVal) * 100;
        const rounded = Math.abs(diff).toFixed(1);
        if (diff > 0.05) {
            $el.removeClass('down flat').addClass('up').html('<i class="fas fa-arrow-up"></i> ' + esc(I18n.t('dashboard.trend.vsLast', rounded)));
        } else if (diff < -0.05) {
            $el.removeClass('up flat').addClass('down').html('<i class="fas fa-arrow-down"></i> ' + esc(I18n.t('dashboard.trend.vsLast', rounded)));
        } else {
            $el.removeClass('up down').addClass('flat').html('<i class="fas fa-minus"></i> ' + esc(I18n.t('dashboard.trend.noChange')));
        }
    }

    function renderStats(data) {
        $('#issued-card .stat-value').text(fmtNumber(data.totalLicensesIssued));
        $('#active-card .stat-value').text(fmtNumber(data.activeLicenses));
        $('#products-card .stat-value').text(fmtNumber(data.totalProducts));
        $('#expiring-card .stat-value').text(fmtNumber(data.expiringSoonCount));

        renderTrend($('#issued-card .stat-trend'), data.totalLicensesIssued || 0, data.previousLicensesIssued);
        renderTrend($('#active-card .stat-trend'), data.activeLicenses || 0, data.previousActiveLicenses);

        $('#items-card .stat-value').text(fmtNumber(data.totalItems));
        $('#stockvalue-card .stat-value').text(fmtNumber(data.stockValue));
        const warehouses = data.warehouseCount || 0;
        $('#stockvalue-card .stat-sub').text(I18n.t(warehouses === 1 ? 'dashboard.stockValue.warehouse.one' : 'dashboard.stockValue.warehouse.many', warehouses));
        $('#lowstock-card .stat-value').text(fmtNumber(data.lowStockCount));
        $('#outofstock-card .stat-value').text(fmtNumber(data.outOfStockCount));

        $('#mv-qty-in').text(fmtNumber(data.qtyIn));
        $('#mv-qty-out').text(fmtNumber(data.qtyOut));
        $('#mv-received').text(fmtNumber(data.receivedValue));
        $('#mv-issued').text(fmtNumber(data.issuedValue));
    }

    function renderLicenseTables(data) {
        if (data.recentLicenses && data.recentLicenses.length > 0) {
            $.each(data.recentLicenses, function(index, lic) {
                const row = `<tr>
                                <td>${esc(lic.code || '-')}</td>
                                <td class="text-ellipsis" style="max-width: 160px;" title="${esc(lic.customerName)}">${esc(lic.customerName || '-')}</td>
                                <td class="text-ellipsis" style="max-width: 160px;" title="${esc(lic.productName)}">${esc(lic.productName || '-')}</td>
                                <td>${esc(lic.issuedAt || '-')}</td>
                                <td>${getStatusBadge(lic.statusDesc)}</td>
                            </tr>`;
                $('#recent-licenses-table').append(row);
            });
        } else {
            $('#recent-licenses-table').append('<tr><td colspan="5" class="text-center py-4 text-muted">' + esc(I18n.t('dashboard.empty.licenses')) + '</td></tr>');
        }

        if (data.expiringLicenses && data.expiringLicenses.length > 0) {
            $.each(data.expiringLicenses, function(index, item) {
                const badgeClass = item.daysRemaining <= 7 ? 'bg-danger' : 'bg-warning text-dark';
                const fullLabel = (item.customerName || '') + ' - ' + (item.productName || '');
                const listItem = `<li class="list-group-item expiring-item d-flex justify-content-between align-items-center py-3">
                                <div style="min-width: 0;">
                                    <h6 class="my-0 font-weight-bold text-ellipsis" title="${esc(fullLabel)}">${esc(fullLabel)}</h6>
                                    <small class="text-muted">${esc(item.code || '')}</small>
                                </div>
                                <span class="badge ${badgeClass} days-badge rounded-pill">${esc(I18n.t('dashboard.daysShort', item.daysRemaining))}</span>
                            </li>`;
                $('#expiring-list').append(listItem);
            });
        } else {
            $('#expiring-list').append('<li class="list-group-item text-center py-4 text-muted">' + esc(I18n.t('dashboard.empty.expiring')) + '</li>');
        }
    }

    function renderInventoryTables(data) {
        if (data.lowStockItems && data.lowStockItems.length > 0) {
            $.each(data.lowStockItems, function(index, item) {
                const onHand = Number(item.qtyOnHand || 0);
                const reorder = item.reorderLevel === null || item.reorderLevel === undefined ? null : Number(item.reorderLevel);
                const pct = reorder && reorder > 0 ? Math.max(0, Math.min(100, (onHand / reorder) * 100)) : 0;
                const barColor = onHand <= 0 ? 'linear-gradient(90deg,#e11d48,#fb7185)' : 'linear-gradient(90deg,#ea580c,#fbbf24)';
                const row = `<tr>
                                <td style="max-width: 170px;">
                                    <div class="fw-semibold text-ellipsis" title="${esc(item.name)}">${esc(item.name || '-')}</div>
                                    <small class="text-muted">${esc(item.code || '')}</small>
                                </td>
                                <td class="text-end fw-semibold">${fmtNumber(onHand)}</td>
                                <td class="text-end">${reorder === null ? '-' : fmtNumber(reorder)}</td>
                                <td><div class="stock-progress"><div style="width: ${Math.max(pct, onHand <= 0 ? 100 : 4)}%; background: ${barColor};"></div></div></td>
                            </tr>`;
                $('#low-stock-table').append(row);
            });
        } else {
            $('#low-stock-table').append('<tr><td colspan="4" class="text-center py-4 text-muted">' + esc(I18n.t('dashboard.empty.lowStock')) + '</td></tr>');
        }

        if (data.recentMovements && data.recentMovements.length > 0) {
            $.each(data.recentMovements, function(index, mv) {
                const incoming = mv.direction === 'IN';
                const row = `<tr>
                                <td style="max-width: 200px;">
                                    <div class="d-flex align-items-center gap-2">
                                        <span class="avatar-dot ${incoming ? 'avatar-in' : 'avatar-out'}"><i class="fas ${incoming ? 'fa-arrow-down' : 'fa-arrow-up'}"></i></span>
                                        <div style="min-width: 0;">
                                            <div class="fw-semibold text-ellipsis" title="${esc(mv.itemName)}">${esc(mv.itemName || '-')}</div>
                                            <small class="text-muted">${esc(mv.itemCode || '')}</small>
                                        </div>
                                    </div>
                                </td>
                                <td><span class="badge ${incoming ? 'bg-success' : 'bg-danger'}">${esc(I18n.t(incoming ? 'common.in' : 'common.out'))}</span></td>
                                <td class="text-end fw-semibold">${fmtNumber(mv.quantity)}</td>
                                <td class="text-capitalize">${esc(mv.refDocType || '-')}</td>
                                <td>${esc(mv.postedAt || '-')}</td>
                            </tr>`;
                $('#recent-movements-table').append(row);
            });
        } else {
            $('#recent-movements-table').append('<tr><td colspan="5" class="text-center py-4 text-muted">' + esc(I18n.t('dashboard.empty.movements')) + '</td></tr>');
        }
    }

    function renderIssuedChart(data) {
        const ctx = document.getElementById('issuedLineChart').getContext('2d');
        issuedChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.issuedLabels || [],
                datasets: [{
                    label: I18n.t('dashboard.licensesIssued'),
                    data: data.issuedData || [],
                    borderColor: INDIGO,
                    backgroundColor: gradientFill(ctx, INDIGO, 260),
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: INDIGO,
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: 'rgba(148, 163, 184, 0.18)' } },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    function renderRevenueChart(data) {
        const labels = data.revenueByProductLabels || [];
        const values = (data.revenueByProductData || []).map(function(v) { return Number(v) || 0; });
        const total = values.reduce(function(a, b) { return a + b; }, 0);

        if (total <= 0) {
            $('#revenue-chart-wrapper').addClass('d-none');
            $('#revenue-no-data').addClass('show');
            return;
        }

        const ctx = document.getElementById('revenueDonutChart').getContext('2d');
        revenueChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    label: I18n.t('dashboard.revenue'),
                    data: values,
                    backgroundColor: labels.map(function(l, i) { return PALETTE[i % PALETTE.length]; }),
                    borderColor: '#ffffff',
                    borderWidth: 3,
                    hoverOffset: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '62%',
                plugins: {
                    legend: {
                        position: 'right',
                        labels: { boxWidth: 12, usePointStyle: true, font: { size: 11 }, color: '#0f1f33' }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const value = context.parsed || 0;
                                const sum = context.dataset.data.reduce(function(a, b) { return a + b; }, 0);
                                const pct = sum > 0 ? ((value / sum) * 100).toFixed(1) : 0;
                                return ' ' + context.label + ': ' + fmtNumber(value) + ' (' + pct + '%)';
                            }
                        }
                    }
                }
            }
        });
    }

    function renderMovementChart(data) {
        const ctx = document.getElementById('movementChart').getContext('2d');
        movementChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.movementLabels || [],
                datasets: [
                    {
                        label: I18n.t('dashboard.mv.qtyIn'),
                        data: (data.movementInData || []).map(Number),
                        backgroundColor: GREEN,
                        borderRadius: 6,
                        maxBarThickness: 22
                    },
                    {
                        label: I18n.t('dashboard.mv.qtyOut'),
                        data: (data.movementOutData || []).map(Number),
                        backgroundColor: ROSE,
                        borderRadius: 6,
                        maxBarThickness: 22
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: { mode: 'index', intersect: false },
                plugins: {
                    legend: {
                        position: 'top',
                        align: 'end',
                        labels: { boxWidth: 10, usePointStyle: true, font: { size: 11 } }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { callback: function(v) { return fmtCompact(v); } },
                        grid: { color: 'rgba(148, 163, 184, 0.18)' }
                    },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    function renderCategoryChart(data) {
        const labels = data.stockCategoryLabels || [];
        const values = (data.stockCategoryData || []).map(function(v) { return Number(v) || 0; });
        const total = values.reduce(function(a, b) { return a + b; }, 0);

        if (total <= 0) {
            $('#category-chart-wrapper').addClass('d-none');
            $('#category-no-data').addClass('show');
            return;
        }

        const ctx = document.getElementById('categoryChart').getContext('2d');
        categoryChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: values,
                    backgroundColor: labels.map(function(l, i) { return PALETTE[(i + 3) % PALETTE.length]; }),
                    borderColor: '#ffffff',
                    borderWidth: 3,
                    hoverOffset: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '60%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { boxWidth: 12, usePointStyle: true, font: { size: 11 }, color: '#0f1f33' }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const value = context.parsed || 0;
                                const sum = context.dataset.data.reduce(function(a, b) { return a + b; }, 0);
                                const pct = sum > 0 ? ((value / sum) * 100).toFixed(1) : 0;
                                return ' ' + context.label + ': ' + fmtNumber(value) + ' (' + pct + '%)';
                            }
                        }
                    }
                }
            }
        });
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
                renderStats(data);
                renderLicenseTables(data);
                renderInventoryTables(data);

                hideSkeletons();

                renderIssuedChart(data);
                renderRevenueChart(data);
                renderMovementChart(data);
                renderCategoryChart(data);
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
        $('#date-filter-current').text(presetLabel(preset));
        $('#custom-range-panel').removeClass('show');
        $('#date-filter').removeClass('open');

        const { start, end } = getPresetRange(preset);
        currentStart = start;
        currentEnd = end;
        loadDashboardData(start, end);
    }

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
            alert(I18n.t('dashboard.error.dateOrder'));
            return;
        }

        currentPreset = 'custom';
        currentStart = start;
        currentEnd = end;
        $('#date-filter-current').text(presetLabel('custom'));
        $('#date-filter').removeClass('open');
        $('#custom-range-panel').removeClass('show');

        loadDashboardData(start, end);
    });

    $('#refresh-btn').on('click', function() {
        loadDashboardData(currentStart, currentEnd);
    });

    I18n.load(['common.', 'dashboard.']).always(function() {
        applyPreset('month');
    });
});