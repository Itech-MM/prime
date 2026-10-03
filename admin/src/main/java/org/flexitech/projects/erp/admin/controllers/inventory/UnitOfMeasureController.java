package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.validations.ValidationResponseUtil;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.UnitOfMeasureDTO;
import org.flexitech.projects.erp.dto.inventory.search.UnitOfMeasureSearchDTO;
import org.flexitech.projects.erp.services.inventory.UnitOfMeasureService;
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
public class UnitOfMeasureController {

	private static final String INV_UOM_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_UOM + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_UOM + "')";

	private final UnitOfMeasureService uomService;

	public UnitOfMeasureController(UnitOfMeasureService uomService) {
		this.uomService = uomService;
	}

	@PostMapping("/inventory/units/search")
	@PreAuthorize(INV_UOM_ACCESS)
	@ResponseBody
	public SearchResultDTO<UnitOfMeasureDTO> searchUomsAjax(@RequestBody UnitOfMeasureSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return uomService.searchUoms(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching units: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<UnitOfMeasureDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@GetMapping("/inventory/units/{id}")
	@PreAuthorize(INV_UOM_ACCESS)
	@ResponseBody
	public ResponseEntity<?> getUom(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(uomService.getUomById(id));
		} catch (Exception e) {
			log.error("Error getting unit: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/units/save")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_UOM + "')")
	@ResponseBody
	public ResponseEntity<?> saveUom(@Valid @RequestBody UnitOfMeasureDTO uomDTO, BindingResult result) {
		if (result.hasErrors()) {
			return ValidationResponseUtil.badRequest(result);
		}
		try {
			return ResponseEntity.ok(uomService.manageUom(uomDTO));
		} catch (Exception e) {
			log.error("Error saving unit: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/units/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_UOM + "')")
	@ResponseBody
	public ResponseEntity<?> deleteUom(@RequestParam Long id) {
		try {
			uomService.deleteUom(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("Error deleting unit: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/inventory/units/select2")
	@PreAuthorize(INV_UOM_ACCESS)
	@ResponseBody
	public Map<String, Object> select2(@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page) {

		UnitOfMeasureSearchDTO searchDTO = new UnitOfMeasureSearchDTO();
		searchDTO.setName(q);

		Map<String, Object> response = new HashMap<>();
		try {
			Pageable pageable = PageRequest.of(page, 10, Sort.by("name").ascending());
			SearchResultDTO<UnitOfMeasureDTO> result = uomService.searchUoms(searchDTO, pageable);

			List<Map<String, Object>> results = new ArrayList<>();
			for (UnitOfMeasureDTO uom : result.getResults()) {
				Map<String, Object> option = new HashMap<>();
				option.put("id", uom.getId());
				option.put("text", uom.getCode() + " - " + uom.getName());
				results.add(option);
			}

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on unit select2: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
		}
		return response;
	}
}