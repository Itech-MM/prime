package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.admin.configs.MenuSecurity;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.validations.ValidationResponseUtil;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.GoodsReceiptDTO;
import org.flexitech.projects.erp.dto.inventory.search.GoodsReceiptSearchDTO;
import org.flexitech.projects.erp.services.inventory.GoodsReceiptService;
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
public class GoodsReceiptController {

	private static final String INV_GR_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_GOODS_RECEIPT + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_GOODS_RECEIPT + "')";
	private static final String INV_GR_EDIT = "@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_GOODS_RECEIPT + "')";

	private final GoodsReceiptService goodsReceiptService;
	private final MenuSecurity menuSecurity;

	public GoodsReceiptController(GoodsReceiptService goodsReceiptService, MenuSecurity menuSecurity) {
		this.goodsReceiptService = goodsReceiptService;
		this.menuSecurity = menuSecurity;
	}

	@GetMapping("/inventory/stock-documents/goods-receipts/setup")
	@PreAuthorize(INV_GR_ACCESS)
	public String setupPage(Model model, @RequestParam(required = false) Long id) {

		GoodsReceiptDTO receiptDTO = new GoodsReceiptDTO();
		if (CommonValidators.validLong(id)) {
			try {
				receiptDTO = this.goodsReceiptService.getReceiptById(id);
			} catch (Exception e) {
				log.error("Failed to get goods receipt:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		model.addAttribute("receiptDTO", receiptDTO);
		model.addAttribute("canEdit", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_GOODS_RECEIPT));
		model.addAttribute("docStatusList", InventoryDocStatus.getAll());
		model.addAttribute("canApprove", menuSecurity.checkMenuApprove(MenuCodeConstants.MENU_INV_GOODS_RECEIPT));
		return "pages/inventory/stock/goods-receipt-setup";
	}

	@PostMapping("/inventory/stock-documents/goods-receipts/save")
	@PreAuthorize(INV_GR_EDIT)
	@ResponseBody
	public ResponseEntity<?> save(@Valid @RequestBody GoodsReceiptDTO receiptDTO, BindingResult result) {
		if (result.hasErrors()) {
			return ValidationResponseUtil.badRequest(result);
		}
		try {
			return ResponseEntity.ok(goodsReceiptService.saveDraft(receiptDTO));
		} catch (Exception e) {
			log.error("Error saving goods receipt: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/inventory/stock-documents/goods-receipts/{id}")
	@PreAuthorize(INV_GR_ACCESS)
	@ResponseBody
	public ResponseEntity<?> getOne(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsReceiptService.getReceiptById(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-receipts/search")
	@PreAuthorize(INV_GR_ACCESS)
	@ResponseBody
	public SearchResultDTO<GoodsReceiptDTO> search(@RequestBody GoodsReceiptSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
			return goodsReceiptService.searchReceipts(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching goods receipts: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<GoodsReceiptDTO> empty = new SearchResultDTO<>();
			empty.setResults(new ArrayList<>());
			empty.setTotalRecords(0);
			empty.setTotalPage(0);
			empty.setPageNo(0);
			return empty;
		}
	}

	@PostMapping("/inventory/stock-documents/goods-receipts/{id}/submit")
	@PreAuthorize(INV_GR_EDIT)
	@ResponseBody
	public ResponseEntity<?> submit(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsReceiptService.submit(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-receipts/{id}/approve")
	@PreAuthorize("@menuSecurity.hasMenuApprove('" + MenuCodeConstants.MENU_INV_GOODS_RECEIPT + "')")
	@ResponseBody
	public ResponseEntity<?> approve(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsReceiptService.approve(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-receipts/{id}/post")
	@PreAuthorize(INV_GR_EDIT)
	@ResponseBody
	public ResponseEntity<?> postDoc(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsReceiptService.post(id));
		} catch (Exception e) {
			log.error("Error posting goods receipt: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-receipts/{id}/cancel")
	@PreAuthorize(INV_GR_EDIT)
	@ResponseBody
	public ResponseEntity<?> cancel(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsReceiptService.cancel(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-receipts/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_GOODS_RECEIPT + "')")
	@ResponseBody
	public ResponseEntity<?> delete(@RequestParam Long id) {
		try {
			goodsReceiptService.deleteReceipt(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/inventory/stock-documents/goods-receipts/select2")
	@PreAuthorize(INV_GR_ACCESS)
	@ResponseBody
	public Map<String, Object> select2(@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page) {
		GoodsReceiptSearchDTO searchDTO = new GoodsReceiptSearchDTO();
		searchDTO.setDocNo(q);

		Map<String, Object> response = new HashMap<>();
		try {
			Pageable pageable = PageRequest.of(page, 10, Sort.by("createdTime").descending());
			SearchResultDTO<GoodsReceiptDTO> result = goodsReceiptService.searchReceipts(searchDTO, pageable);

			List<Map<String, Object>> results = new ArrayList<>();
			for (GoodsReceiptDTO dto : result.getResults()) {
				Map<String, Object> option = new HashMap<>();
				option.put("id", dto.getId());
				option.put("text", dto.getDocNo());
				results.add(option);
			}

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			response.put("results", new ArrayList<>());
		}
		return response;
	}
}