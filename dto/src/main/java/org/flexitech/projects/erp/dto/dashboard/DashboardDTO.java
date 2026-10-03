package org.flexitech.projects.erp.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardDTO {

	private long totalLicensesIssued;
	private long previousLicensesIssued;

	private long activeLicenses;
	private long previousActiveLicenses;

	private long totalProducts;
	private long expiringSoonCount;

	private List<RecentLicenseDTO> recentLicenses;
	private List<ExpiringLicenseDTO> expiringLicenses;

	private List<String> issuedLabels;
	private List<Long> issuedData;

	private List<String> revenueByProductLabels;
	private List<BigDecimal> revenueByProductData;

	private long totalItems;
	private long warehouseCount;
	private long lowStockCount;
	private long outOfStockCount;
	private BigDecimal stockValue;
	private BigDecimal receivedValue;
	private BigDecimal issuedValue;
	private BigDecimal qtyIn;
	private BigDecimal qtyOut;

	private List<String> movementLabels;
	private List<BigDecimal> movementInData;
	private List<BigDecimal> movementOutData;

	private List<String> stockCategoryLabels;
	private List<BigDecimal> stockCategoryData;

	private List<LowStockItemDTO> lowStockItems;
	private List<StockMovementDTO> recentMovements;
}