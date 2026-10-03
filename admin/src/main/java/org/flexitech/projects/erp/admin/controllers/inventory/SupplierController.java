package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.SupplierDTO;
import org.flexitech.projects.erp.dto.inventory.search.SupplierSearchDTO;
import org.flexitech.projects.erp.services.inventory.SupplierService;
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
public class SupplierController {

	private static final String INV_SUPPLIER_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_SUPPLIER + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_SUPPLIER + "')";

	private final SupplierService supplierService;

	public SupplierController(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	@GetMapping("/inventory/suppliers/setup")
	@PreAuthorize(INV_SUPPLIER_ACCESS)
	public String supplierSetupPage(Model model, @RequestParam(required = false) Long id) {

		SupplierDTO supplierDTO = new SupplierDTO();
		if (CommonValidators.validLong(id)) {
			try {
				supplierDTO = this.supplierService.getSupplierById(id);
			} catch (Exception e) {
				log.error("Failed to get supplier with id:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		commonSetupModel(model, supplierDTO);
		return "pages/inventory/supplier/setup";
	}

	@GetMapping("/inventory/suppliers")
	@PreAuthorize(INV_SUPPLIER_ACCESS)
	public String supplierListPage(Model model) {

		model.addAttribute("searchDTO", new SupplierSearchDTO());
		Pageable page = Pageable.ofSize(CommonConstants.ROW_PER_PAGE);

		try {
			model.addAttribute("supplierList", this.supplierService.searchSuppliers(new SupplierSearchDTO(), page));
		} catch (Exception e) {
			log.error("Error on supplier list page: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "pages/inventory/supplier/list";
	}

	@PostMapping("/inventory/suppliers/setup")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_SUPPLIER + "')")
	public String manageSupplier(@Valid @ModelAttribute SupplierDTO supplierDTO,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (result.hasErrors()) {
				commonSetupModel(model, supplierDTO);
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Failed to save supplier!");
				return "pages/inventory/supplier/setup";
			}
			boolean isUpdate = CommonValidators.validLong(supplierDTO.getId());

			supplierService.manageSupplier(supplierDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate ? "Supplier updated successfully!" : "Supplier created successfully!");
			return "redirect:/inventory/suppliers";
		} catch (Exception e) {
			commonSetupModel(model, supplierDTO);
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
			return "pages/inventory/supplier/setup";
		}
	}

	@PostMapping("/inventory/suppliers/search")
	@PreAuthorize(INV_SUPPLIER_ACCESS)
	@ResponseBody
	public SearchResultDTO<SupplierDTO> searchSuppliersAjax(@RequestBody SupplierSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return supplierService.searchSuppliers(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching suppliers: {}", e.getMessage());
			SearchResultDTO<SupplierDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@PostMapping("/inventory/suppliers/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_SUPPLIER + "')")
	public String deleteSupplier(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			supplierService.deleteSupplier(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Supplier deleted successfully!");
		} catch (Exception e) {
			log.error("Error deleting supplier: {}", ExceptionUtils.getStackTrace(e));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "redirect:/inventory/suppliers";
	}

	@GetMapping("/inventory/suppliers/select2")
	@PreAuthorize(INV_SUPPLIER_ACCESS)
	@ResponseBody
	public java.util.Map<String, Object> select2(@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page) {

		SupplierSearchDTO searchDTO = new SupplierSearchDTO();
		searchDTO.setName(q);

		java.util.Map<String, Object> response = new java.util.HashMap<>();
		try {
			Pageable pageable = PageRequest.of(page, 10, Sort.by("name").ascending());
			SearchResultDTO<SupplierDTO> result = supplierService.searchSuppliers(searchDTO, pageable);

			java.util.List<java.util.Map<String, Object>> results = new ArrayList<>();
			for (SupplierDTO supplier : result.getResults()) {
				java.util.Map<String, Object> option = new java.util.HashMap<>();
				option.put("id", supplier.getId());
				option.put("text", supplier.getCode() + " - " + supplier.getName());
				results.add(option);
			}

			java.util.Map<String, Object> pagination = new java.util.HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on supplier select2: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
		}
		return response;
	}

	private void commonSetupModel(Model model, SupplierDTO supplierDTO) {
		try {
			model.addAttribute("supplierDTO", supplierDTO);
			model.addAttribute("statusList", ActiveStatus.getAll());
		} catch (Exception e) {
			log.error("Error :: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}
	}
}