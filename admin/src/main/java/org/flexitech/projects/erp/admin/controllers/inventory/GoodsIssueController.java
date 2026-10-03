package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.admin.configs.MenuSecurity;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.enums.IssueType;
import org.flexitech.projects.erp.commons.validations.ValidationResponseUtil;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.GoodsIssueDTO;
import org.flexitech.projects.erp.dto.inventory.search.GoodsIssueSearchDTO;
import org.flexitech.projects.erp.services.inventory.GoodsIssueService;
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
public class GoodsIssueController {

	private static final String INV_GI_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "')";
	private static final String INV_GI_EDIT = "@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "')";

	private final GoodsIssueService goodsIssueService;
	private final MenuSecurity menuSecurity;

	public GoodsIssueController(GoodsIssueService goodsIssueService, MenuSecurity menuSecurity) {
		this.goodsIssueService = goodsIssueService;
		this.menuSecurity = menuSecurity;
	}

	@GetMapping("/inventory/stock-documents/goods-issues/setup")
	@PreAuthorize(INV_GI_ACCESS)
	public String setupPage(Model model, @RequestParam(required = false) Long id) {

		GoodsIssueDTO issueDTO = new GoodsIssueDTO();
		if (CommonValidators.validLong(id)) {
			try {
				issueDTO = this.goodsIssueService.getIssueById(id);
			} catch (Exception e) {
				log.error("Failed to get goods issue:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		model.addAttribute("issueDTO", issueDTO);
		model.addAttribute("canEdit", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_GOODS_ISSUE));
		model.addAttribute("issueTypeList", IssueType.getAll());
		model.addAttribute("docStatusList", InventoryDocStatus.getAll());
		model.addAttribute("canApprove", menuSecurity.checkMenuApprove(MenuCodeConstants.MENU_INV_GOODS_ISSUE));
		return "pages/inventory/stock/goods-issue-setup";
	}

	@PostMapping("/inventory/stock-documents/goods-issues/save")
	@PreAuthorize(INV_GI_EDIT)
	@ResponseBody
	public ResponseEntity<?> save(@Valid @RequestBody GoodsIssueDTO issueDTO, BindingResult result) {
		if (result.hasErrors()) {
			return ValidationResponseUtil.badRequest(result);
		}
		try {
			return ResponseEntity.ok(goodsIssueService.saveDraft(issueDTO));
		} catch (Exception e) {
			log.error("Error saving goods issue: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/inventory/stock-documents/goods-issues/{id}")
	@PreAuthorize(INV_GI_ACCESS)
	@ResponseBody
	public ResponseEntity<?> getOne(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsIssueService.getIssueById(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-issues/search")
	@PreAuthorize(INV_GI_ACCESS)
	@ResponseBody
	public SearchResultDTO<GoodsIssueDTO> search(@RequestBody GoodsIssueSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
			return goodsIssueService.searchIssues(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching goods issues: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<GoodsIssueDTO> empty = new SearchResultDTO<>();
			empty.setResults(new ArrayList<>());
			empty.setTotalRecords(0);
			empty.setTotalPage(0);
			empty.setPageNo(0);
			return empty;
		}
	}

	@PostMapping("/inventory/stock-documents/goods-issues/{id}/submit")
	@PreAuthorize(INV_GI_EDIT)
	@ResponseBody
	public ResponseEntity<?> submit(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsIssueService.submit(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-issues/{id}/approve")
	@PreAuthorize("@menuSecurity.hasMenuApprove('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "')")
	@ResponseBody
	public ResponseEntity<?> approve(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsIssueService.approve(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-issues/{id}/post")
	@PreAuthorize(INV_GI_EDIT)
	@ResponseBody
	public ResponseEntity<?> postDoc(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsIssueService.post(id));
		} catch (Exception e) {
			log.error("Error posting goods issue: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-issues/{id}/cancel")
	@PreAuthorize(INV_GI_EDIT)
	@ResponseBody
	public ResponseEntity<?> cancel(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(goodsIssueService.cancel(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/stock-documents/goods-issues/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "')")
	@ResponseBody
	public ResponseEntity<?> delete(@RequestParam Long id) {
		try {
			goodsIssueService.deleteIssue(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}