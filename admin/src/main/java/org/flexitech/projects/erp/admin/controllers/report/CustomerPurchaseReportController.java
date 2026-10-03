package org.flexitech.projects.erp.admin.controllers.report;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.dto.report.CustomerPurchaseReportSearchDTO;
import org.flexitech.projects.erp.dto.report.CustomerPurchaseReportSummaryDTO;
import org.flexitech.projects.erp.services.report.CustomerPurchaseReportService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CustomerPurchaseReportController {

	private final CustomerPurchaseReportService reportService;

	public CustomerPurchaseReportController(CustomerPurchaseReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping("/reports/customer-purchases")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_PURCHASE_REPORT+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_PURCHASE_REPORT+"')")
	public String reportPage(Model model) {
		model.addAttribute("searchDTO", new CustomerPurchaseReportSearchDTO());
		return "pages/report/customer-purchase";
	}

	@PostMapping("/reports/customer-purchases/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_PURCHASE_REPORT+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_PURCHASE_REPORT+"')")
	@ResponseBody
	public SearchResultDTO<LicenseDTO> search(@RequestBody CustomerPurchaseReportSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("issuedAt").descending());
			return reportService.searchPurchases(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching customer purchases: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<LicenseDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@PostMapping("/reports/customer-purchases/summary")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_PURCHASE_REPORT+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_PURCHASE_REPORT+"')")
	@ResponseBody
	public CustomerPurchaseReportSummaryDTO summary(@RequestBody CustomerPurchaseReportSearchDTO searchDTO) {
		try {
			return reportService.getSummary(searchDTO);
		} catch (Exception e) {
			log.error("Error building purchase summary: {}", ExceptionUtils.getStackTrace(e));
			return new CustomerPurchaseReportSummaryDTO();
		}
	}
}