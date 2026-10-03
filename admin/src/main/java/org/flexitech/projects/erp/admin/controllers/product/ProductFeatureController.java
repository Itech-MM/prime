package org.flexitech.projects.erp.admin.controllers.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.product.ProductFeatureDTO;
import org.flexitech.projects.erp.dto.product.ProductFeatureSearchDTO;
import org.flexitech.projects.erp.services.product.ProductFeatureService;
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
public class ProductFeatureController {

	private final ProductFeatureService productFeatureService;

	public ProductFeatureController(ProductFeatureService productFeatureService) {
		this.productFeatureService = productFeatureService;
	}

	@GetMapping("/product-features/setup")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_PRODUCT_FEATURE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_PRODUCT_FEATURE_LIST+"')")
	public String productFeatureSetupPage(Model model, @RequestParam(required = false) Long id) {

		ProductFeatureDTO featureDTO = new ProductFeatureDTO();
		if (CommonValidators.validLong(id)) {
			try {
				featureDTO = this.productFeatureService.getFeatureById(id);
			} catch (Exception e) {
				log.error("Failed to get product feature with id:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		model.addAttribute("featureDTO", featureDTO);
		return "pages/product-feature/setup";
	}

	@GetMapping("/product-features")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_PRODUCT_FEATURE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_PRODUCT_FEATURE_LIST+"')")
	public String productFeatureListPage(Model model) {

		model.addAttribute("searchDTO", new ProductFeatureSearchDTO());
		Pageable page = Pageable.ofSize(CommonConstants.ROW_PER_PAGE);

		try {
			model.addAttribute("featureList", this.productFeatureService.searchFeatures(new ProductFeatureSearchDTO(), page));
		} catch (Exception e) {
			log.error("Error on product feature list page: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "pages/product-feature/list";
	}

	@PostMapping("/product-features/setup")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_PRODUCT_FEATURE_LIST+"')")
	public String manageFeature(@Valid @ModelAttribute ProductFeatureDTO featureDTO,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (result.hasErrors()) {
				model.addAttribute("featureDTO", featureDTO);
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Failed to save product feature!");
				return "pages/product-feature/setup";
			}
			boolean isUpdate = CommonValidators.validLong(featureDTO.getId());

			productFeatureService.manageFeature(featureDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate ? "Product feature update successfully!" : "Product feature created successfully!");
			return "redirect:/product-features";
		} catch (Exception e) {
			model.addAttribute("featureDTO", featureDTO);
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
			return "pages/product-feature/setup";
		}
	}

	@PostMapping("/product-features/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_PRODUCT_FEATURE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_PRODUCT_FEATURE_LIST+"')")
	@ResponseBody
	public SearchResultDTO<ProductFeatureDTO> searchFeaturesAjax(@RequestBody ProductFeatureSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return productFeatureService.searchFeatures(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching product features: {}", e.getMessage());
			SearchResultDTO<ProductFeatureDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@PostMapping("/product-features/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('"+MenuCodeConstants.MENU_PRODUCT_FEATURE_LIST+"')")
	public String deleteFeature(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			productFeatureService.deleteFeature(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Product feature deleted successfully!");
		} catch (Exception e) {
			log.error("Error deleting product feature: {}", ExceptionUtils.getStackTrace(e));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "redirect:/product-features";
	}
	
	@GetMapping("/product-features/select2")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_PRODUCT_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_PRODUCT_LIST+"')")
	@ResponseBody
	public Map<String, Object> select2Search(@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "0") int page) {
		Map<String, Object> response = new HashMap<>();
		try {
			ProductFeatureSearchDTO searchDTO = new ProductFeatureSearchDTO();
			searchDTO.setName(q);

			Pageable pageable = PageRequest.of(page, CommonConstants.ROW_PER_PAGE, Sort.by("name").ascending());
			SearchResultDTO<ProductFeatureDTO> result = this.productFeatureService.searchFeatures(searchDTO, pageable);

			List<Map<String, Object>> options = result.getResults().stream()
					.map(f -> {
						Map<String, Object> option = new HashMap<>();
						option.put("id", f.getId());
						option.put("text", f.getName());
						return option;
					})
					.collect(Collectors.toList());

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", options);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on feature select2 search: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
			response.put("pagination", Map.of("more", false));
		}
		return response;
	}
}