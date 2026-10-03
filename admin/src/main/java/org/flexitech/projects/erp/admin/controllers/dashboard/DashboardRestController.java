package org.flexitech.projects.erp.admin.controllers.dashboard;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.dto.dashboard.DashboardDTO;
import org.flexitech.projects.erp.services.dashboard.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardRestController {

	private final DashboardService dashboardService;

	@GetMapping("/summary")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_DASHBOARD+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_DASHBOARD+"')")
	public ResponseEntity<DashboardDTO> getDashboardSummary(
			@RequestParam(required = false) LocalDate startDate,
			@RequestParam(required = false) LocalDate endDate) {

		if (startDate == null || endDate == null) {
			endDate = LocalDate.now();
			startDate = endDate.withDayOfMonth(1);
		}

		Date startDateData = toStartOfDay(startDate);
		Date endDateData = toExclusiveEnd(endDate);

		try {
			DashboardDTO dto = dashboardService.getDashboardSummary(startDateData, endDateData);
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			log.error("Error building dashboard summary: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	private Date toStartOfDay(LocalDate date) {
		return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private Date toExclusiveEnd(LocalDate date) {
		return Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}