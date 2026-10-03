package org.flexitech.projects.erp.services.dashboard;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.enums.LicenseStatus;
import org.flexitech.projects.erp.dto.dashboard.DashboardDTO;
import org.flexitech.projects.erp.dto.dashboard.ExpiringLicenseDTO;
import org.flexitech.projects.erp.dto.dashboard.LowStockItemDTO;
import org.flexitech.projects.erp.dto.dashboard.RecentLicenseDTO;
import org.flexitech.projects.erp.dto.dashboard.StockMovementDTO;
import org.flexitech.projects.erp.persistence.entities.license.License;
import org.flexitech.projects.erp.persistence.repositories.dashboard.InventoryDashboardRepository;
import org.flexitech.projects.erp.persistence.repositories.dashboard.InventoryDashboardRepository.MovementBucket;
import org.flexitech.projects.erp.persistence.repositories.license.LicenseRepository;
import org.flexitech.projects.erp.persistence.repositories.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

	private static final int EXPIRING_SOON_DAYS = 30;
	private static final int DAILY_BUCKET_MAX_DAYS = 31;
	private static final int LIST_LIMIT = 6;

	private final LicenseRepository licenseRepository;
	private final ProductRepository productRepository;
	private final InventoryDashboardRepository inventoryDashboardRepository;

	public DashboardServiceImpl(LicenseRepository licenseRepository, ProductRepository productRepository,
			InventoryDashboardRepository inventoryDashboardRepository) {
		this.licenseRepository = licenseRepository;
		this.productRepository = productRepository;
		this.inventoryDashboardRepository = inventoryDashboardRepository;
	}

	@Override
	public DashboardDTO getDashboardSummary(Date startDate, Date endDate) throws Exception {
		DashboardDTO dto = new DashboardDTO();

		dto.setTotalLicensesIssued(this.licenseRepository.countByIssuedAtBetween(startDate, endDate));
		dto.setActiveLicenses(this.licenseRepository.countByStatusAndIssuedAtBetween(LicenseStatus.ACTIVE.getCode(),
				startDate, endDate));
		dto.setTotalProducts(this.productRepository.count());

		Calendar now = Calendar.getInstance();
		Calendar soon = Calendar.getInstance();
		soon.add(Calendar.DATE, EXPIRING_SOON_DAYS);
		List<License> expiringLicenses = this.licenseRepository.findByStatusAndExpiresAtBetweenOrderByExpiresAtAsc(
				LicenseStatus.ACTIVE.getCode(), now.getTime(), soon.getTime());
		dto.setExpiringSoonCount(expiringLicenses.size());
		dto.setExpiringLicenses(expiringLicenses.stream()
				.map(l -> new ExpiringLicenseDTO(l, daysBetween(now.getTime(), l.getExpiresAt())))
				.collect(Collectors.toList()));

		List<License> recent = this.licenseRepository.findTop5ByOrderByIssuedAtDesc();
		dto.setRecentLicenses(recent.stream().map(RecentLicenseDTO::new).collect(Collectors.toList()));

		List<License> periodLicenses = this.licenseRepository.findByIssuedAtBetweenOrderByIssuedAtAsc(startDate,
				endDate);
		buildIssuedChart(dto, periodLicenses, startDate, endDate);
		buildRevenueByProduct(dto, periodLicenses);

		long days = daysBetween(startDate, endDate);
		Calendar prevEnd = Calendar.getInstance();
		prevEnd.setTime(startDate);
		prevEnd.add(Calendar.SECOND, -1);
		Calendar prevStart = Calendar.getInstance();
		prevStart.setTime(prevEnd.getTime());
		prevStart.add(Calendar.DATE, (int) -days);

		dto.setPreviousLicensesIssued(
				this.licenseRepository.countByIssuedAtBetween(prevStart.getTime(), prevEnd.getTime()));
		dto.setPreviousActiveLicenses(this.licenseRepository.countByStatusAndIssuedAtBetween(
				LicenseStatus.ACTIVE.getCode(), prevStart.getTime(), prevEnd.getTime()));

		buildInventory(dto, startDate, endDate);

		return dto;
	}

	private void buildInventory(DashboardDTO dto, Date startDate, Date endDate) {
		dto.setTotalItems(this.inventoryDashboardRepository.countActiveItems());
		dto.setWarehouseCount(this.inventoryDashboardRepository.countActiveWarehouses());
		dto.setStockValue(this.inventoryDashboardRepository.sumStockValue());
		dto.setLowStockCount(this.inventoryDashboardRepository.countLowStock());
		dto.setOutOfStockCount(this.inventoryDashboardRepository.countOutOfStock());
		dto.setReceivedValue(this.inventoryDashboardRepository.sumPostedReceipts(startDate, endDate));
		dto.setIssuedValue(this.inventoryDashboardRepository.sumPostedIssues(startDate, endDate));
		dto.setLowStockItems(this.inventoryDashboardRepository.findNeedsRestock(LIST_LIMIT).stream()
				.map(LowStockItemDTO::new).collect(Collectors.toList()));
		dto.setRecentMovements(this.inventoryDashboardRepository.findRecentMovements(LIST_LIMIT).stream()
				.map(StockMovementDTO::new).collect(Collectors.toList()));

		Map<String, BigDecimal> categoryValues = this.inventoryDashboardRepository.sumStockValueByCategory(LIST_LIMIT);
		dto.setStockCategoryLabels(new ArrayList<>(categoryValues.keySet()));
		dto.setStockCategoryData(new ArrayList<>(categoryValues.values()));

		buildMovementChart(dto, startDate, endDate);
	}

	private void buildMovementChart(DashboardDTO dto, Date startDate, Date endDate) {
		boolean groupByDay = daysBetween(startDate, endDate) <= DAILY_BUCKET_MAX_DAYS;
		Map<String, String> bucketLabels = buildBucketLabels(startDate, endDate, groupByDay);

		Map<String, BigDecimal> inBuckets = new LinkedHashMap<>();
		Map<String, BigDecimal> outBuckets = new LinkedHashMap<>();
		for (String key : bucketLabels.keySet()) {
			inBuckets.put(key, BigDecimal.ZERO);
			outBuckets.put(key, BigDecimal.ZERO);
		}

		BigDecimal totalIn = BigDecimal.ZERO;
		BigDecimal totalOut = BigDecimal.ZERO;
		for (MovementBucket row : this.inventoryDashboardRepository.findMovementBuckets(startDate, endDate,
				groupByDay)) {
			totalIn = totalIn.add(row.qtyIn);
			totalOut = totalOut.add(row.qtyOut);
			if (inBuckets.containsKey(row.bucket)) {
				inBuckets.put(row.bucket, row.qtyIn);
				outBuckets.put(row.bucket, row.qtyOut);
			}
		}

		dto.setQtyIn(totalIn);
		dto.setQtyOut(totalOut);
		dto.setMovementLabels(new ArrayList<>(bucketLabels.values()));
		dto.setMovementInData(new ArrayList<>(inBuckets.values()));
		dto.setMovementOutData(new ArrayList<>(outBuckets.values()));
	}

	private void buildIssuedChart(DashboardDTO dto, List<License> licenses, Date startDate, Date endDate) {
		boolean groupByDay = daysBetween(startDate, endDate) <= DAILY_BUCKET_MAX_DAYS;
		Map<String, String> bucketLabels = buildBucketLabels(startDate, endDate, groupByDay);
		SimpleDateFormat bucketFormat = createBucketFormat(groupByDay);

		Map<String, Long> buckets = new LinkedHashMap<>();
		for (String key : bucketLabels.keySet()) {
			buckets.put(key, 0L);
		}

		for (License license : licenses) {
			if (license.getIssuedAt() == null) {
				continue;
			}
			String bucketKey = bucketFormat.format(license.getIssuedAt());
			if (buckets.containsKey(bucketKey)) {
				buckets.merge(bucketKey, 1L, Long::sum);
			}
		}

		dto.setIssuedLabels(new ArrayList<>(bucketLabels.values()));
		dto.setIssuedData(new ArrayList<>(buckets.values()));
	}

	private void buildRevenueByProduct(DashboardDTO dto, List<License> licenses) {
		Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
		for (License license : licenses) {
			if (license.getPlan() == null || license.getPlan().getProduct() == null) {
				continue;
			}
			String productName = license.getPlan().getProduct().getName();
			BigDecimal amount = license.getPricePaid() != null ? license.getPricePaid() : BigDecimal.ZERO;
			revenueMap.merge(productName, amount, BigDecimal::add);
		}

		List<Map.Entry<String, BigDecimal>> sorted = revenueMap.entrySet().stream()
				.sorted((a, b) -> b.getValue().compareTo(a.getValue())).limit(LIST_LIMIT).collect(Collectors.toList());

		List<String> labels = new ArrayList<>();
		List<BigDecimal> data = new ArrayList<>();
		for (Map.Entry<String, BigDecimal> entry : sorted) {
			labels.add(entry.getKey());
			data.add(entry.getValue());
		}

		dto.setRevenueByProductLabels(labels);
		dto.setRevenueByProductData(data);
	}

	private Map<String, String> buildBucketLabels(Date startDate, Date endDate, boolean groupByDay) {
		SimpleDateFormat keyFormat = new SimpleDateFormat(groupByDay ? "dd/MM" : "MMM yyyy");
		SimpleDateFormat bucketFormat = createBucketFormat(groupByDay);

		Calendar cursor = Calendar.getInstance();
		cursor.setTime(startDate);
		if (!groupByDay) {
			cursor.set(Calendar.DAY_OF_MONTH, 1);
		}
		Calendar last = Calendar.getInstance();
		last.setTime(new Date(endDate.getTime() - 1));

		Map<String, String> labels = new LinkedHashMap<>();
		while (!cursor.after(last)) {
			labels.put(bucketFormat.format(cursor.getTime()), keyFormat.format(cursor.getTime()));
			cursor.add(groupByDay ? Calendar.DATE : Calendar.MONTH, 1);
		}
		return labels;
	}

	private SimpleDateFormat createBucketFormat(boolean groupByDay) {
		return new SimpleDateFormat(groupByDay ? "yyyyMMdd" : "yyyyMM");
	}

	private long daysBetween(Date start, Date end) {
		long diff = end.getTime() - start.getTime();
		return Math.max(1, diff / (1000 * 60 * 60 * 24));
	}
}