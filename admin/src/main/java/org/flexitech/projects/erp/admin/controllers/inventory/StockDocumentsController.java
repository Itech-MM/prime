package org.flexitech.projects.erp.admin.controllers.inventory;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.admin.configs.MenuSecurity;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.AdjustmentReason;
import org.flexitech.projects.erp.commons.enums.IssueType;
import org.flexitech.projects.erp.dto.inventory.search.GoodsIssueSearchDTO;
import org.flexitech.projects.erp.dto.inventory.search.GoodsReceiptSearchDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockAdjustmentSearchDTO;
import org.flexitech.projects.erp.services.inventory.GoodsIssueService;
import org.flexitech.projects.erp.services.inventory.GoodsReceiptService;
import org.flexitech.projects.erp.services.inventory.StockAdjustmentService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class StockDocumentsController {

	private static final String INV_STOCK_DOCS_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_GOODS_RECEIPT + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_GOODS_RECEIPT + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "')";

	private final GoodsReceiptService goodsReceiptService;
	private final GoodsIssueService goodsIssueService;
	private final StockAdjustmentService stockAdjustmentService;
	private final MenuSecurity menuSecurity;

	public StockDocumentsController(GoodsReceiptService goodsReceiptService, GoodsIssueService goodsIssueService,
			StockAdjustmentService stockAdjustmentService, MenuSecurity menuSecurity) {
		this.goodsReceiptService = goodsReceiptService;
		this.goodsIssueService = goodsIssueService;
		this.stockAdjustmentService = stockAdjustmentService;
		this.menuSecurity = menuSecurity;
	}

	@GetMapping("/inventory/stock-documents")
	@PreAuthorize(INV_STOCK_DOCS_ACCESS)
	public String shellPage(Model model, @RequestParam(required = false, defaultValue = "goods-receipts") String tab) {

		model.addAttribute("activeTab", tab);

		model.addAttribute("viewGoodsReceipt", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_GOODS_RECEIPT) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_GOODS_RECEIPT));
		model.addAttribute("viewGoodsIssue", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_GOODS_ISSUE) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_GOODS_ISSUE));
		model.addAttribute("viewStockAdjustment", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT));

		model.addAttribute("canEditGoodsReceipt", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_GOODS_RECEIPT));
		model.addAttribute("canDeleteGoodsReceipt", menuSecurity.checkMenuDelete(MenuCodeConstants.MENU_INV_GOODS_RECEIPT));
		model.addAttribute("canEditGoodsIssue", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_GOODS_ISSUE));
		model.addAttribute("canDeleteGoodsIssue", menuSecurity.checkMenuDelete(MenuCodeConstants.MENU_INV_GOODS_ISSUE));
		model.addAttribute("canEditStockAdjustment", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT));
		model.addAttribute("canDeleteStockAdjustment", menuSecurity.checkMenuDelete(MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT));

		model.addAttribute("issueTypeList", IssueType.getAll());
		model.addAttribute("reasonList", AdjustmentReason.getAll());

		try {
			model.addAttribute("goodsReceiptList", this.goodsReceiptService.searchReceipts(new GoodsReceiptSearchDTO(), Pageable.ofSize(10)));
		} catch (Exception e) {
			log.error("Error loading goods receipts: {}", ExceptionUtils.getStackTrace(e));
		}
		try {
			model.addAttribute("goodsIssueList", this.goodsIssueService.searchIssues(new GoodsIssueSearchDTO(), Pageable.ofSize(10)));
		} catch (Exception e) {
			log.error("Error loading goods issues: {}", ExceptionUtils.getStackTrace(e));
		}
		try {
			model.addAttribute("stockAdjustmentList", this.stockAdjustmentService.searchAdjustments(new StockAdjustmentSearchDTO(), Pageable.ofSize(10)));
		} catch (Exception e) {
			log.error("Error loading stock adjustments: {}", ExceptionUtils.getStackTrace(e));
		}

		return "pages/inventory/stock/shell";
	}
}