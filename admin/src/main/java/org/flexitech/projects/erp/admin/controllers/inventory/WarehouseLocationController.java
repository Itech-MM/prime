package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.validations.ValidationResponseUtil;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.WarehouseLocationDTO;
import org.flexitech.projects.erp.dto.inventory.search.WarehouseLocationSearchDTO;
import org.flexitech.projects.erp.services.inventory.WarehouseLocationService;
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
public class WarehouseLocationController {

	private static final String INV_LOCATION_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_LOCATION + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_LOCATION + "')";

	private final WarehouseLocationService locationService;

	public WarehouseLocationController(WarehouseLocationService locationService) {
		this.locationService = locationService;
	}

	@PostMapping("/inventory/locations/search")
	@PreAuthorize(INV_LOCATION_ACCESS)
	@ResponseBody
	public SearchResultDTO<WarehouseLocationDTO> searchLocationsAjax(@RequestBody WarehouseLocationSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return locationService.searchLocations(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching locations: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<WarehouseLocationDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@GetMapping("/inventory/locations/{id}")
	@PreAuthorize(INV_LOCATION_ACCESS)
	@ResponseBody
	public ResponseEntity<?> getLocation(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(locationService.getLocationById(id));
		} catch (Exception e) {
			log.error("Error getting location: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/locations/save")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_LOCATION + "')")
	@ResponseBody
	public ResponseEntity<?> saveLocation(@Valid @RequestBody WarehouseLocationDTO locationDTO, BindingResult result) {
		if (result.hasErrors()) {
			return ValidationResponseUtil.badRequest(result);
		}
		try {
			return ResponseEntity.ok(locationService.manageLocation(locationDTO));
		} catch (Exception e) {
			log.error("Error saving location: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/inventory/locations/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_LOCATION + "')")
	@ResponseBody
	public ResponseEntity<?> deleteLocation(@RequestParam Long id) {
		try {
			locationService.deleteLocation(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("Error deleting location: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/inventory/locations/select2")
	@PreAuthorize(INV_LOCATION_ACCESS)
	@ResponseBody
	public Map<String, Object> select2(@RequestParam(required = false) String q,
			@RequestParam(required = false) Long warehouseId,
			@RequestParam(required = false) Long excludeId,
			@RequestParam(defaultValue = "0") int page) {

		WarehouseLocationSearchDTO searchDTO = new WarehouseLocationSearchDTO();
		searchDTO.setName(q);
		searchDTO.setWarehouseId(warehouseId);

		Map<String, Object> response = new HashMap<>();
		try {
			Pageable pageable = PageRequest.of(page, 10, Sort.by("name").ascending());
			SearchResultDTO<WarehouseLocationDTO> result = locationService.searchLocations(searchDTO, pageable);

			List<Map<String, Object>> results = new ArrayList<>();
			for (WarehouseLocationDTO location : result.getResults()) {
				if (excludeId != null && excludeId.equals(location.getId())) {
					continue;
				}
				Map<String, Object> option = new HashMap<>();
				option.put("id", location.getId());
				option.put("text", location.getCode() + " - " + location.getName());
				results.add(option);
			}

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on location select2: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
		}
		return response;
	}
}