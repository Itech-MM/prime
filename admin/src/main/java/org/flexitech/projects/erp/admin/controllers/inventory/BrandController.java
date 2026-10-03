package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.validations.ValidationResponseUtil;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.BrandDTO;
import org.flexitech.projects.erp.dto.inventory.search.BrandSearchDTO;
import org.flexitech.projects.erp.services.inventory.BrandService;
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
public class BrandController {

	private static final String INV_BRAND_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_BRAND + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_BRAND + "')";

	private final BrandService brandService;

	public BrandController(BrandService brandService) {
		this.brandService = brandService;
	}

	@PostMapping("/inventory/brands/search")
	@PreAuthorize(INV_BRAND_ACCESS)
	@ResponseBody
	public SearchResultDTO<BrandDTO> searchBrandsAjax(@RequestBody BrandSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return brandService.searchBrands(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching brands: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<BrandDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@GetMapping("/inventory/brands/{id}")
	@PreAuthorize(INV_BRAND_ACCESS)
	@ResponseBody
	public ResponseEntity<?> getBrand(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(brandService.getBrandById(id));
		} catch (Exception e) {
			log.error("Error getting brand: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/brands/save")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_BRAND + "')")
	@ResponseBody
	public ResponseEntity<?> saveBrand(@Valid @RequestBody BrandDTO brandDTO, BindingResult result) {
		if (result.hasErrors()) {
			return ValidationResponseUtil.badRequest(result);
		}
		try {
			return ResponseEntity.ok(brandService.manageBrand(brandDTO));
		} catch (Exception e) {
			log.error("Error saving brand: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/brands/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_BRAND + "')")
	@ResponseBody
	public ResponseEntity<?> deleteBrand(@RequestParam Long id) {
		try {
			brandService.deleteBrand(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("Error deleting brand: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/inventory/brands/select2")
	@PreAuthorize(INV_BRAND_ACCESS)
	@ResponseBody
	public Map<String, Object> select2(@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page) {

		BrandSearchDTO searchDTO = new BrandSearchDTO();
		searchDTO.setName(q);

		Map<String, Object> response = new HashMap<>();
		try {
			Pageable pageable = PageRequest.of(page, 10, Sort.by("name").ascending());
			SearchResultDTO<BrandDTO> result = brandService.searchBrands(searchDTO, pageable);

			List<Map<String, Object>> results = new ArrayList<>();
			for (BrandDTO brand : result.getResults()) {
				Map<String, Object> option = new HashMap<>();
				option.put("id", brand.getId());
				option.put("text", brand.getName());
				results.add(option);
			}

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on brand select2: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
		}
		return response;
	}
}