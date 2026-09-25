package org.flexitech.projects.erp.admin.controllers.dashboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardRestController {

	/*
	 * private final DashboardService dashboardService;
	 * 
	 * @GetMapping("/summary")
	 * 
	 * @PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.
	 * MENU_DASHBOARD+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.
	 * MENU_DASHBOARD+"')") public ResponseEntity<DashboardDTO> getDashboardSummary(
	 * 
	 * @RequestParam(required = false) LocalDate startDate,
	 * 
	 * @RequestParam(required = false) LocalDate endDate) {
	 * 
	 * if (startDate == null || endDate == null) { endDate = LocalDate.now();
	 * startDate = endDate.withDayOfMonth(1); }
	 * 
	 * Date startDateData = toStartOfDay(startDate); Date endDateData =
	 * toExclusiveEnd(endDate);
	 * 
	 * System.out.println("StartDate :: " + startDateData);
	 * System.out.println("EndDate :: " + endDateData);
	 * 
	 * DashboardDTO dto = new DashboardDTO();
	 * dto.setTotalRevenue(dashboardService.getTotalRevenue(startDateData,
	 * endDateData));
	 * dto.setTotalOrders(dashboardService.getTotalOrdersCount(startDateData,
	 * endDateData));
	 * dto.setTotalProducts(dashboardService.getTotalProductsCount());
	 * dto.setLowStockCount(dashboardService.getLowStockCount());
	 * dto.setRecentOrders(dashboardService.getRecentOrders());
	 * dto.setLowStockItems(dashboardService.getLowStockItems());
	 * dto.setSalesLabels(dashboardService.getSalesAnalyticsLabels(startDateData,
	 * endDateData));
	 * dto.setSalesData(dashboardService.getSalesAnalyticsData(startDateData,
	 * endDateData));
	 * dto.setPieLabels(dashboardService.getProductTypePieLabels(startDateData,
	 * endDateData));
	 * dto.setPieData(dashboardService.getProductTypePieData(startDateData,
	 * endDateData));
	 * 
	 * 
	 * long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; LocalDate
	 * prevEnd = startDate.minusDays(1); LocalDate prevStart =
	 * prevEnd.minusDays(days - 1);
	 * dto.setPreviousRevenue(dashboardService.getTotalRevenue(prevStart, prevEnd));
	 * dto.setPreviousOrders(dashboardService.getTotalOrdersCount(prevStart,
	 * prevEnd));
	 * 
	 * return ResponseEntity.ok(dto); }
	 * 
	 * private Date toStartOfDay(LocalDate date) { return
	 * Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()); }
	 * 
	 * private Date toExclusiveEnd(LocalDate date) { return
	 * Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	 * }
	 */
}