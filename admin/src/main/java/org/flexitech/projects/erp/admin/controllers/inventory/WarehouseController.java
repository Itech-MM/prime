package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.admin.configs.MenuSecurity;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.enums.LocationType;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.WarehouseDTO;
import org.flexitech.projects.erp.dto.inventory.search.WarehouseSearchDTO;
import org.flexitech.projects.erp.services.inventory.WarehouseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WarehouseController {

	private static final String INV_WAREHOUSE_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_WAREHOUSE + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_WAREHOUSE + "')";

	private static final String INV_WAREHOUSE_MGMT_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_WAREHOUSE + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_WAREHOUSE + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_LOCATION + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_LOCATION + "')";

	private final WarehouseService warehouseService;
	private final MenuSecurity menuSecurity;

	public WarehouseController(WarehouseService warehouseService, MenuSecurity menuSecurity) {
		this.warehouseService = warehouseService;
		this.menuSecurity = menuSecurity;
	}

	@GetMapping("/inventory/warehouses")
	@PreAuthorize(INV_WAREHOUSE_MGMT_ACCESS)
	public String warehouseManagementPage(Model model, @RequestParam(required = false, defaultValue = "warehouses") String tab) {

		model.addAttribute("activeTab", tab);
		model.addAttribute("searchDTO", new WarehouseSearchDTO());
		model.addAttribute("statusList", ActiveStatus.getAll());
		model.addAttribute("locationTypeList", LocationType.getAll());

		model.addAttribute("canEditWarehouse", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_WAREHOUSE));
		model.addAttribute("canDeleteWarehouse", menuSecurity.checkMenuDelete(MenuCodeConstants.MENU_INV_WAREHOUSE));
		model.addAttribute("canEditLocation", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_LOCATION));
		model.addAttribute("canDeleteLocation", menuSecurity.checkMenuDelete(MenuCodeConstants.MENU_INV_LOCATION));
		model.addAttribute("viewWarehouse", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_WAREHOUSE) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_WAREHOUSE));
		model.addAttribute("viewLocation", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_LOCATION) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_LOCATION));

		Pageable page = Pageable.ofSize(CommonConstants.ROW_PER_PAGE);
		try {
			model.addAttribute("warehouseList", this.warehouseService.searchWarehouses(new WarehouseSearchDTO(), page));
		} catch (Exception e) {
			log.error("Error on warehouse management page: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "pages/inventory/warehouse/shell";
	}

	@GetMapping("/inventory/warehouses/setup")
	@PreAuthorize(INV_WAREHOUSE_MGMT_ACCESS)
	public String warehouseSetupPage(Model model,
			@RequestParam(required = false) Long id,
			@RequestParam(required = false, defaultValue = "warehouses") String tab,
			@RequestParam(required = false) Long locationId) {

		WarehouseDTO warehouseDTO = new WarehouseDTO();
		if (CommonValidators.validLong(id)) {
			try {
				warehouseDTO = this.warehouseService.getWarehouseById(id);
			} catch (Exception e) {
				log.error("Failed to get warehouse with id:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		model.addAttribute("activeTab", tab);
		model.addAttribute("presetLocationEditId", locationId);

		model.addAttribute("viewWarehouse", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_WAREHOUSE) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_WAREHOUSE));
		model.addAttribute("viewLocation", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_LOCATION) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_LOCATION));
		model.addAttribute("canEditWarehouse", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_WAREHOUSE));
		model.addAttribute("canEditLocation", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_LOCATION));

		commonSetupModel(model, warehouseDTO);
		return "pages/inventory/warehouse/setup";
	}

	@PostMapping("/inventory/warehouses/setup")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_WAREHOUSE + "')")
	public String manageWarehouse(@Valid @ModelAttribute WarehouseDTO warehouseDTO,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (result.hasErrors()) {
				populateSetupPermissions(model);
				model.addAttribute("activeTab", "warehouses");
				commonSetupModel(model, warehouseDTO);
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Failed to save warehouse!");
				return "pages/inventory/warehouse/setup";
			}
			boolean isUpdate = CommonValidators.validLong(warehouseDTO.getId());

			warehouseService.manageWarehouse(warehouseDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate ? "Warehouse updated successfully!" : "Warehouse created successfully!");
			return "redirect:/inventory/warehouses";
		} catch (Exception e) {
			populateSetupPermissions(model);
			model.addAttribute("activeTab", "warehouses");
			commonSetupModel(model, warehouseDTO);
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
			return "pages/inventory/warehouse/setup";
		}
	}

	@PostMapping("/inventory/warehouses/search")
	@PreAuthorize(INV_WAREHOUSE_ACCESS)
	@ResponseBody
	public SearchResultDTO<WarehouseDTO> searchWarehousesAjax(@RequestBody WarehouseSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return warehouseService.searchWarehouses(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching warehouses: {}", ExceptionUtils.getStackTrace(e));
			SearchResultDTO<WarehouseDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@PostMapping("/inventory/warehouses/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_WAREHOUSE + "')")
	public String deleteWarehouse(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			warehouseService.deleteWarehouse(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Warehouse deleted successfully!");
		} catch (Exception e) {
			log.error("Error deleting warehouse: {}", ExceptionUtils.getStackTrace(e));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "redirect:/inventory/warehouses";
	}

	@GetMapping("/inventory/warehouses/select2")
	@PreAuthorize(INV_WAREHOUSE_ACCESS)
	@ResponseBody
	public java.util.Map<String, Object> select2(@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page) {

		WarehouseSearchDTO searchDTO = new WarehouseSearchDTO();
		searchDTO.setName(q);

		java.util.Map<String, Object> response = new java.util.HashMap<>();
		try {
			Pageable pageable = PageRequest.of(page, 10, Sort.by("name").ascending());
			SearchResultDTO<WarehouseDTO> result = warehouseService.searchWarehouses(searchDTO, pageable);

			java.util.List<java.util.Map<String, Object>> results = new ArrayList<>();
			for (WarehouseDTO warehouse : result.getResults()) {
				java.util.Map<String, Object> option = new java.util.HashMap<>();
				option.put("id", warehouse.getId());
				option.put("text", warehouse.getCode() + " - " + warehouse.getName());
				results.add(option);
			}

			java.util.Map<String, Object> pagination = new java.util.HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on warehouse select2: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
		}
		return response;
	}

	private void populateSetupPermissions(Model model) {
		model.addAttribute("viewWarehouse", true);
		model.addAttribute("viewLocation", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_LOCATION) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_LOCATION));
		model.addAttribute("canEditWarehouse", true);
		model.addAttribute("canEditLocation", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_LOCATION));
	}

	private void commonSetupModel(Model model, WarehouseDTO warehouseDTO) {
		try {
			model.addAttribute("warehouseDTO", warehouseDTO);
			model.addAttribute("statusList", ActiveStatus.getAll());
			model.addAttribute("locationTypeList", LocationType.getAll());
		} catch (Exception e) {
			log.error("Error :: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}
	}
}