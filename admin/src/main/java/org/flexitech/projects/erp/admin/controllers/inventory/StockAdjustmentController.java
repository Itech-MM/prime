package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.admin.configs.MenuSecurity;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.AdjustmentReason;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.validations.ValidationResponseUtil;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.StockAdjustmentDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockAdjustmentSearchDTO;
import org.flexitech.projects.erp.services.inventory.StockAdjustmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class StockAdjustmentController {

	private static final String INV_SA_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "')";
	private static final String INV_SA_EDIT = "@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "')";

	private final StockAdjustmentService stockAdjustmentService;
	private final MenuSecurity menuSecurity;

	public StockAdjustmentController(StockAdjustmentService stockAdjustmentService, MenuSecurity menuSecurity) {
		this.stockAdjustmentService = stockAdjustmentService;
		this.menuSecurity = menuSecurity;
	}

	@GetMapping("/inventory/stock-documents/stock-adjustments/setup")
	@PreAuthorize(INV_SA_ACCESS)
	public String setupPage(Model model, @RequestParam(required = false) Long id) {

		StockAdjustmentDTO adjustmentDTO = new StockAdjustmentDTO();
		if (CommonValidators.validLong(id)) {
			try {
				adjustmentDTO = this.stockAdjustmentService.getAdjustmentById(id);
			} catch (Exception e) {
				log.error("Failed to get stock adjustment:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		model.addAttribute("adjustmentDTO", adjustmentDTO);
		model.addAttribute("canEdit", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT));
		model.addAttribute("reasonList", AdjustmentReason.getAll());
		model.addAttribute("docStatusList", InventoryDocStatus.getAll());
		model.addAttribute("canApprove", menuSecurity.checkMenuApprove(MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT));
		return "pages/inventory/stock/stock-adjustment-setup";
	}

	@PostMapping("/inventory/stock-documents/stock-adjustments/save")
	@PreAuthorize(INV_SA_EDIT)
	@ResponseBody
	public ResponseEntity<?> save(@Valid @RequestBody StockAdjustmentDTO adjustmentDTO, BindingResult result) {
		if (result.hasErrors()) {
			return ValidationResponseUtil.badRequest(result);
		}
		try {
			return ResponseEntity.ok(stockAdjustmentService.saveDraft(adjustmentDTO));
		} catch (Exception e) {
			log.error("Error saving stock adjustment: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/inventory/stock-documents/stock-adjustments/{id}")
	@PreAuthorize(INV_SA_ACCESS)
	@ResponseBody
	public ResponseEntity<?> getOne(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(stockAdjustmentService.getAdjustmentById(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/stock-adjustments/search")
	@PreAuthorize(INV_SA_ACCESS)
	@ResponseBody
	public SearchResultDTO<StockAdjustmentDTO> search(@RequestBody StockAdjustmentSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
			return stockAdjustmentService.searchAdjustments(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching stock adjustments: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<StockAdjustmentDTO> empty = new SearchResultDTO<>();
			empty.setResults(new ArrayList<>());
			empty.setTotalRecords(0);
			empty.setTotalPage(0);
			empty.setPageNo(0);
			return empty;
		}
	}

	@PostMapping("/inventory/stock-documents/stock-adjustments/{id}/submit")
	@PreAuthorize(INV_SA_EDIT)
	@ResponseBody
	public ResponseEntity<?> submit(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(stockAdjustmentService.submit(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/stock-adjustments/{id}/approve")
	@PreAuthorize("@menuSecurity.hasMenuApprove('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "')")
	@ResponseBody
	public ResponseEntity<?> approve(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(stockAdjustmentService.approve(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/stock-adjustments/{id}/post")
	@PreAuthorize(INV_SA_EDIT)
	@ResponseBody
	public ResponseEntity<?> postDoc(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(stockAdjustmentService.post(id));
		} catch (Exception e) {
			log.error("Error posting stock adjustment: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/stock-adjustments/{id}/cancel")
	@PreAuthorize(INV_SA_EDIT)
	@ResponseBody
	public ResponseEntity<?> cancel(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(stockAdjustmentService.cancel(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/stock-adjustments/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "')")
	@ResponseBody
	public ResponseEntity<?> delete(@RequestParam Long id) {
		try {
			stockAdjustmentService.deleteAdjustment(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}