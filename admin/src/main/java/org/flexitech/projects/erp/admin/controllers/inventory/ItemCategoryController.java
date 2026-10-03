package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.validations.ValidationResponseUtil;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.ItemCategoryDTO;
import org.flexitech.projects.erp.dto.inventory.search.ItemCategorySearchDTO;
import org.flexitech.projects.erp.services.inventory.ItemCategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
public class ItemCategoryController {

	private static final String INV_CATEGORY_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_CATEGORY + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_CATEGORY + "')";

	private final ItemCategoryService categoryService;

	public ItemCategoryController(ItemCategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@PostMapping("/inventory/item-categories/search")
	@PreAuthorize(INV_CATEGORY_ACCESS)
	@ResponseBody
	public SearchResultDTO<ItemCategoryDTO> searchCategoriesAjax(@RequestBody ItemCategorySearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return categoryService.searchCategories(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching categories: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<ItemCategoryDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@GetMapping("/inventory/item-categories/{id}")
	@PreAuthorize(INV_CATEGORY_ACCESS)
	@ResponseBody
	public ResponseEntity<?> getCategory(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(categoryService.getCategoryById(id));
		} catch (Exception e) {
			log.error("Error getting category: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/item-categories/save")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_CATEGORY + "')")
	@ResponseBody
	public ResponseEntity<?> saveCategory(@Valid @RequestBody ItemCategoryDTO categoryDTO, BindingResult result) {
		if (result.hasErrors()) {
			return ValidationResponseUtil.badRequest(result);
		}
		try {
			return ResponseEntity.ok(categoryService.manageCategory(categoryDTO));
		} catch (Exception e) {
			log.error("Error saving category: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/item-categories/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_CATEGORY + "')")
	@ResponseBody
	public ResponseEntity<?> deleteCategory(@RequestParam Long id) {
		try {
			categoryService.deleteCategory(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("Error deleting category: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/inventory/item-categories/select2")
	@PreAuthorize(INV_CATEGORY_ACCESS)
	@ResponseBody
	public Map<String, Object> select2(@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page) {

		ItemCategorySearchDTO searchDTO = new ItemCategorySearchDTO();
		searchDTO.setName(q);

		Map<String, Object> response = new HashMap<>();
		try {
			Pageable pageable = PageRequest.of(page, 10, Sort.by("name").ascending());
			SearchResultDTO<ItemCategoryDTO> result = categoryService.searchCategories(searchDTO, pageable);

			List<Map<String, Object>> results = new ArrayList<>();
			for (ItemCategoryDTO category : result.getResults()) {
				Map<String, Object> option = new HashMap<>();
				option.put("id", category.getId());
				option.put("text", category.getName());
				results.add(option);
			}

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on category select2: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
		}
		return response;
	}
}