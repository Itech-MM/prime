package org.flexitech.projects.erp.admin.controllers.license;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.license.LicensePlanDTO;
import org.flexitech.projects.erp.dto.license.LicensePlanSearchDTO;
import org.flexitech.projects.erp.dto.product.ProductDTO;
import org.flexitech.projects.erp.dto.product.ProductSearchDTO;
import org.flexitech.projects.erp.persistence.entities.product.ProductFeatureProduct;
import org.flexitech.projects.erp.persistence.repositories.product.ProductFeatureProductRepository;
import org.flexitech.projects.erp.services.license.LicensePlanService;
import org.flexitech.projects.erp.services.product.ProductService;
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
public class LicensePlanController {

	private final LicensePlanService licensePlanService;
	private final ProductService productService;
	private final ProductFeatureProductRepository productFeatureProductRepository;

	public LicensePlanController(LicensePlanService licensePlanService, ProductService productService,
			ProductFeatureProductRepository productFeatureProductRepository) {
		this.licensePlanService = licensePlanService;
		this.productService = productService;
		this.productFeatureProductRepository = productFeatureProductRepository;
	}

	@GetMapping("/license-plans/setup")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"')")
	public String planSetupPage(Model model, @RequestParam(required = false) Long id) {

		LicensePlanDTO planDTO = new LicensePlanDTO();
		if (CommonValidators.validLong(id)) {
			try {
				planDTO = this.licensePlanService.getPlanById(id);
			} catch (Exception e) {
				log.error("Failed to get license plan with id:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		model.addAttribute("planDTO", planDTO);
		model.addAttribute("statusList", ActiveStatus.getAll());
		return "pages/license-plan/setup";
	}

	@GetMapping("/license-plans")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"')")
	public String planListPage(Model model) {
		model.addAttribute("searchDTO", new LicensePlanSearchDTO());
		return "pages/license-plan/list";
	}

	@PostMapping("/license-plans/setup")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"')")
	public String managePlan(@Valid @ModelAttribute LicensePlanDTO planDTO,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (result.hasErrors()) {
				model.addAttribute("planDTO", planDTO);
				model.addAttribute("statusList", ActiveStatus.getAll());
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Failed to save license plan!");
				return "pages/license-plan/setup";
			}
			boolean isUpdate = CommonValidators.validLong(planDTO.getId());

			licensePlanService.managePlan(planDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate ? "License plan update successfully!" : "License plan created successfully!");
			return "redirect:/license-plans";
		} catch (Exception e) {
			model.addAttribute("planDTO", planDTO);
			model.addAttribute("statusList", ActiveStatus.getAll());
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
			return "pages/license-plan/setup";
		}
	}

	@PostMapping("/license-plans/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"')")
	@ResponseBody
	public SearchResultDTO<LicensePlanDTO> searchPlansAjax(@RequestBody LicensePlanSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return licensePlanService.searchPlans(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching license plans: {}", e.getMessage());
			SearchResultDTO<LicensePlanDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@GetMapping("/license-plans/products/select2")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"')")
	@ResponseBody
	public Map<String, Object> productsSelect2(@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page) {
		Map<String, Object> response = new HashMap<>();
		try {
			ProductSearchDTO searchDTO = new ProductSearchDTO();
			searchDTO.setName(q);

			Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
			SearchResultDTO<ProductDTO> productPage = this.productService.searchProducts(searchDTO, pageable);

			List<Map<String, Object>> options = productPage.getResults().stream()
					.filter(p -> p.getStatus() != null && p.getStatus() == 1)
					.map(p -> {
						Map<String, Object> option = new HashMap<>();
						option.put("id", p.getId());
						option.put("text", p.getName() + " (" + p.getCode() + ")");
						return option;
					})
					.collect(Collectors.toList());

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", productPage.getHasNextPage());

			response.put("results", options);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on plan product select2 search: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
			response.put("pagination", Map.of("more", false));
		}
		return response;
	}

	@GetMapping("/license-plans/features/select2")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_PLAN_LIST+"')")
	@ResponseBody
	public Map<String, Object> featuresSelect2(@RequestParam(required = false) Long productId,
			@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (!CommonValidators.validLong(productId)) {
				response.put("results", new ArrayList<>());
				response.put("pagination", Map.of("more", false));
				return response;
			}

			List<ProductFeatureProduct> links = this.productFeatureProductRepository.findByProductId(productId);

			List<ProductFeatureProduct> filtered = links.stream()
					.filter(link -> !CommonValidators.validString(q)
							|| link.getFeature().getName().toLowerCase().contains(q.toLowerCase()))
					.collect(Collectors.toList());

			int pageSize = 20;
			int fromIndex = page * pageSize;
			int toIndex = Math.min(fromIndex + pageSize, filtered.size());

			List<Map<String, Object>> options = new ArrayList<>();
			if (fromIndex < filtered.size()) {
				options = filtered.subList(fromIndex, toIndex).stream()
						.map(link -> {
							Map<String, Object> option = new HashMap<>();
							option.put("id", link.getFeature().getId());
							option.put("text", link.getFeature().getName());
							return option;
						})
						.collect(Collectors.toList());
			}

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", toIndex < filtered.size());

			response.put("results", options);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on plan feature select2 search: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
			response.put("pagination", Map.of("more", false));
		}
		return response;
	}
}