package org.flexitech.projects.erp.admin.controllers.license;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.LicenseStatus;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.customer.CustomerDTO;
import org.flexitech.projects.erp.dto.customer.CustomerSearchDTO;
import org.flexitech.projects.erp.dto.license.LicenseAuditLogDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.dto.license.LicenseSearchDTO;
import org.flexitech.projects.erp.services.customer.CustomerService;
import org.flexitech.projects.erp.services.license.LicenseAuditLogService;
import org.flexitech.projects.erp.services.license.LicenseService;
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
public class LicenseController {

	private final LicenseService licenseService;
	private final LicenseAuditLogService licenseAuditLogService;
	private final CustomerService customerService;

	public LicenseController(LicenseService licenseService, LicenseAuditLogService licenseAuditLogService,
			CustomerService customerService) {
		this.licenseService = licenseService;
		this.licenseAuditLogService = licenseAuditLogService;
		this.customerService = customerService;
	}

	@GetMapping("/licenses")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_LIST+"')")
	public String licenseListPage(Model model) {
		model.addAttribute("searchDTO", new LicenseSearchDTO());
		model.addAttribute("statusList", LicenseStatus.getAll());
		return "pages/license/list";
	}

	@GetMapping("/licenses/detail")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_LIST+"')")
	public String licenseDetailPage(Model model, @RequestParam Long id) {
		try {
			LicenseDTO licenseDTO = this.licenseService.getLicenseById(id);
			List<LicenseAuditLogDTO> logs = this.licenseAuditLogService.getLogsByLicenseId(id);
			model.addAttribute("licenseDTO", licenseDTO);
			model.addAttribute("auditLogs", logs);
		} catch (Exception e) {
			log.error("Error loading license detail: {}", ExceptionUtils.getStackTrace(e));
		}
		return "pages/license/detail";
	}

	@PostMapping("/licenses/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_LIST+"')")
	@ResponseBody
	public SearchResultDTO<LicenseDTO> searchLicensesAjax(@RequestBody LicenseSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("issuedAt").descending());
			return licenseService.searchLicenses(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching licenses: {}", e.getMessage());
			SearchResultDTO<LicenseDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@GetMapping("/licenses/customers/select2")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_LIST+"')")
	@ResponseBody
	public Map<String, Object> customersSelect2(@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page) {
		Map<String, Object> response = new HashMap<>();
		try {
			CustomerSearchDTO searchDTO = new CustomerSearchDTO();
			searchDTO.setName(q);

			Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
			SearchResultDTO<CustomerDTO> customerPage = this.customerService.searchCustomers(searchDTO, pageable);

			List<Map<String, Object>> options = customerPage.getResults().stream()
					.map(c -> {
						Map<String, Object> option = new HashMap<>();
						option.put("id", c.getId());
						option.put("text", c.getName() + " (" + c.getCode() + ")");
						return option;
					})
					.collect(Collectors.toList());

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", customerPage.getHasNextPage());

			response.put("results", options);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on license customer select2 search: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
			response.put("pagination", Map.of("more", false));
		}
		return response;
	}
}