package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBatch;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBatchRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class StockBatchController {

	private final StockBatchRepository stockBatchRepository;

	public StockBatchController(StockBatchRepository stockBatchRepository) {
		this.stockBatchRepository = stockBatchRepository;
	}

	@GetMapping("/inventory/stock-batches/select2")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_GOODS_ISSUE + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_STOCK_ADJUSTMENT + "')")
	@ResponseBody
	public Map<String, Object> select2(@RequestParam Long itemId, @RequestParam(required = false) String q) {

		Map<String, Object> response = new HashMap<>();
		try {
			List<StockBatch> batches = this.stockBatchRepository.findByItemId(itemId);

			List<Map<String, Object>> results = new ArrayList<>();
			for (StockBatch batch : batches) {
				if (CommonValidators.validString(q) && !batch.getBatchNo().toLowerCase().contains(q.toLowerCase())) {
					continue;
				}
				Map<String, Object> option = new HashMap<>();
				option.put("id", batch.getId());
				option.put("text", batch.getBatchNo() + (batch.getExpiryDate() != null ? " (exp: " + batch.getExpiryDate() + ")" : ""));
				results.add(option);
			}

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", false);

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on batch select2: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
		}
		return response;
	}
}