package org.flexitech.projects.erp.admin.controllers.inventory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.admin.configs.MenuSecurity;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.InventoryAuditAction;
import org.flexitech.projects.erp.commons.enums.StockMovementType;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.InventoryAuditLogDTO;
import org.flexitech.projects.erp.dto.inventory.StockBalanceDTO;
import org.flexitech.projects.erp.dto.inventory.StockLedgerDTO;
import org.flexitech.projects.erp.dto.inventory.search.InventoryAuditLogSearchDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockBalanceSearchDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockLedgerSearchDTO;
import org.flexitech.projects.erp.services.inventory.InventoryAuditLogService;
import org.flexitech.projects.erp.services.inventory.StockReportService;
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
public class StockReportController {

	private static final String INV_REPORT_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_ON_HAND + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_ON_HAND + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_CARD + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_CARD + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_REPORT + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_REPORT + "')";

	private final StockReportService stockReportService;
	private final InventoryAuditLogService auditLogService;
	private final MenuSecurity menuSecurity;

	public StockReportController(StockReportService stockReportService, InventoryAuditLogService auditLogService, MenuSecurity menuSecurity) {
		this.stockReportService = stockReportService;
		this.auditLogService = auditLogService;
		this.menuSecurity = menuSecurity;
	}

	@GetMapping("/inventory/reports")
	@PreAuthorize(INV_REPORT_ACCESS)
	public String shellPage(Model model, @RequestParam(required = false, defaultValue = "stock-on-hand") String tab) {

		model.addAttribute("activeTab", tab);
		model.addAttribute("viewStockOnHand", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_STOCK_ON_HAND) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_STOCK_ON_HAND));
		model.addAttribute("viewStockCard", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_STOCK_CARD) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_STOCK_CARD));
		model.addAttribute("viewAuditLog", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_STOCK_REPORT) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_STOCK_REPORT));
		model.addAttribute("movementTypeList", StockMovementType.getAll());
		model.addAttribute("auditActionList", InventoryAuditAction.getAll());

		return "pages/inventory/report/shell";
	}

	@PostMapping("/inventory/reports/stock-on-hand/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_ON_HAND + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_ON_HAND + "')")
	@ResponseBody
	public SearchResultDTO<StockBalanceDTO> searchStockOnHand(@RequestBody StockBalanceSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("item.name").ascending());
			return stockReportService.searchStockOnHand(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching stock on hand: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<StockBalanceDTO> empty = new SearchResultDTO<>();
			empty.setResults(new ArrayList<>());
			empty.setTotalRecords(0);
			empty.setTotalPage(0);
			empty.setPageNo(0);
			return empty;
		}
	}

	@PostMapping("/inventory/reports/stock-card/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_CARD + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_CARD + "')")
	@ResponseBody
	public SearchResultDTO<StockLedgerDTO> getStockCard(@RequestBody StockCardSearchRequest request,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		try {
			StockLedgerSearchDTO searchDTO = new StockLedgerSearchDTO();
			searchDTO.setItemId(request.getItemId());
			searchDTO.setLocationId(request.getLocationId());
			searchDTO.setMovementType(request.getMovementType());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (CommonValidators.validString(request.getFromDate())) {
				Date from = sdf.parse(request.getFromDate());
				searchDTO.setFromDate(from);
			}
			if (CommonValidators.validString(request.getToDate())) {
				Date to = sdf.parse(request.getToDate());
				to = new Date(to.getTime() + (24L * 60 * 60 * 1000) - 1);
				searchDTO.setToDate(to);
			}

			Pageable pageable = PageRequest.of(page, size, Sort.by("postedAt").descending());
			return stockReportService.getStockCard(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error loading stock card: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<StockLedgerDTO> empty = new SearchResultDTO<>();
			empty.setResults(new ArrayList<>());
			empty.setTotalRecords(0);
			empty.setTotalPage(0);
			empty.setPageNo(0);
			return empty;
		}
	}

	@PostMapping("/inventory/reports/audit-log/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_REPORT + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_REPORT + "')")
	@ResponseBody
	public SearchResultDTO<InventoryAuditLogDTO> searchAuditLog(@RequestBody AuditLogSearchRequest request,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		try {
			InventoryAuditLogSearchDTO searchDTO = new InventoryAuditLogSearchDTO();
			searchDTO.setDocType(request.getDocType());
			searchDTO.setDocNo(request.getDocNo());
			searchDTO.setAction(request.getAction());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (CommonValidators.validString(request.getFromDate())) {
				Date from = sdf.parse(request.getFromDate());
				searchDTO.setFromDate(from);
			}
			if (CommonValidators.validString(request.getToDate())) {
				Date to = sdf.parse(request.getToDate());
				to = new Date(to.getTime() + (24L * 60 * 60 * 1000) - 1);
				searchDTO.setToDate(to);
			}

			Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
			return auditLogService.searchAuditLog(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching audit log: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<InventoryAuditLogDTO> empty = new SearchResultDTO<>();
			empty.setResults(new ArrayList<>());
			empty.setTotalRecords(0);
			empty.setTotalPage(0);
			empty.setPageNo(0);
			return empty;
		}
	}

	public static class StockCardSearchRequest {
		private Long itemId;
		private Long locationId;
		private Integer movementType;
		private String fromDate;
		private String toDate;

		public Long getItemId() { return itemId; }
		public void setItemId(Long itemId) { this.itemId = itemId; }
		public Long getLocationId() { return locationId; }
		public void setLocationId(Long locationId) { this.locationId = locationId; }
		public Integer getMovementType() { return movementType; }
		public void setMovementType(Integer movementType) { this.movementType = movementType; }
		public String getFromDate() { return fromDate; }
		public void setFromDate(String fromDate) { this.fromDate = fromDate; }
		public String getToDate() { return toDate; }
		public void setToDate(String toDate) { this.toDate = toDate; }
	}

	public static class AuditLogSearchRequest {
		private String docType;
		private String docNo;
		private Integer action;
		private String fromDate;
		private String toDate;

		public String getDocType() { return docType; }
		public void setDocType(String docType) { this.docType = docType; }
		public String getDocNo() { return docNo; }
		public void setDocNo(String docNo) { this.docNo = docNo; }
		public Integer getAction() { return action; }
		public void setAction(Integer action) { this.action = action; }
		public String getFromDate() { return fromDate; }
		public void setFromDate(String fromDate) { this.fromDate = fromDate; }
		public String getToDate() { return toDate; }
		public void setToDate(String toDate) { this.toDate = toDate; }
	}
}