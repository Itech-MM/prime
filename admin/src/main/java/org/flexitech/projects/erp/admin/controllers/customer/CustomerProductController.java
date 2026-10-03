package org.flexitech.projects.erp.admin.controllers.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.utils.FileUtils;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.customer.CustomerProductDTO;
import org.flexitech.projects.erp.dto.customer.CustomerProductSearchDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.dto.license.LicensePlanDTO;
import org.flexitech.projects.erp.dto.license.LicensePlanSearchDTO;
import org.flexitech.projects.erp.dto.product.ProductDTO;
import org.flexitech.projects.erp.dto.product.ProductSearchDTO;
import org.flexitech.projects.erp.services.customer.CustomerProductService;
import org.flexitech.projects.erp.services.license.LicensePlanService;
import org.flexitech.projects.erp.services.license.LicenseService;
import org.flexitech.projects.erp.services.product.ProductService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CustomerProductController {

	private final CustomerProductService customerProductService;
	private final ProductService productService;
	private final LicensePlanService licensePlanService;
	private final LicenseService licenseService;
	private final FileUtils fileUtils;

	public CustomerProductController(CustomerProductService customerProductService, ProductService productService,
			LicensePlanService licensePlanService, LicenseService licenseService, FileUtils fileUtils) {
		this.customerProductService = customerProductService;
		this.productService = productService;
		this.licensePlanService = licensePlanService;
		this.licenseService = licenseService;
		this.fileUtils = fileUtils;
	}

	@GetMapping("/customer-products/available-select2")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	@ResponseBody
	public Map<String, Object> availableProductsSelect2(@RequestParam Long customerId,
			@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page) {
		Map<String, Object> response = new HashMap<>();
		try {
			ProductSearchDTO searchDTO = new ProductSearchDTO();
			searchDTO.setName(q);

			Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
			SearchResultDTO<ProductDTO> productPage = this.productService.searchProducts(searchDTO, pageable);

			CustomerProductSearchDTO ownedSearch = new CustomerProductSearchDTO();
			ownedSearch.setCustomerId(customerId);
			List<Long> purchasedProductIds = this.customerProductService
					.searchCustomerProducts(ownedSearch, Pageable.unpaged()).getResults().stream()
					.map(CustomerProductDTO::getProductId)
					.collect(Collectors.toList());

			List<Map<String, Object>> options = productPage.getResults().stream()
					.filter(p -> p.getStatus() != null && p.getStatus() == 1)
					.filter(p -> !purchasedProductIds.contains(p.getId()))
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
			log.error("Error on available products select2 search: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
			response.put("pagination", Map.of("more", false));
		}
		return response;
	}

	@GetMapping("/customer-products/plans-select2")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	@ResponseBody
	public Map<String, Object> plansSelect2(@RequestParam Long productId,
			@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page) {
		Map<String, Object> response = new HashMap<>();
		try {
			LicensePlanSearchDTO searchDTO = new LicensePlanSearchDTO();
			searchDTO.setProductId(productId);
			searchDTO.setName(q);

			Pageable pageable = PageRequest.of(page, 20, Sort.by("name").ascending());
			SearchResultDTO<LicensePlanDTO> planPage = this.licensePlanService.searchPlans(searchDTO, pageable);

			List<Map<String, Object>> options = planPage.getResults().stream()
					.filter(p -> p.getStatus() != null && p.getStatus() == 1)
					.map(p -> {
						Map<String, Object> option = new HashMap<>();
						option.put("id", p.getId());
						option.put("text", p.getName() + " (" + p.getCode() + ")");
						return option;
					})
					.collect(Collectors.toList());

			Map<String, Object> pagination = new HashMap<>();
			pagination.put("more", planPage.getHasNextPage());

			response.put("results", options);
			response.put("pagination", pagination);
		} catch (Exception e) {
			log.error("Error on plan select2 search: {}", ExceptionUtils.getStackTrace(e));
			response.put("results", new ArrayList<>());
			response.put("pagination", Map.of("more", false));
		}
		return response;
	}

	@GetMapping("/customer-products/by-customer")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	@ResponseBody
	public List<CustomerProductDTO> byCustomer(@RequestParam Long customerId) {
		try {
			CustomerProductSearchDTO searchDTO = new CustomerProductSearchDTO();
			searchDTO.setCustomerId(customerId);
			return this.customerProductService.searchCustomerProducts(searchDTO, Pageable.unpaged()).getResults();
		} catch (Exception e) {
			log.error("Error loading customer products: {}", ExceptionUtils.getStackTrace(e));
			return List.of();
		}
	}

	@PostMapping("/customer-products/purchase")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	@ResponseBody
	public Map<String, Object> purchase(@RequestParam Long customerId, @RequestParam Long productId,
			@RequestParam Long planId) {
		Map<String, Object> response = new HashMap<>();
		try {
			this.customerProductService.purchaseProduct(customerId, productId, planId);
			response.put("success", true);
			response.put("message", "Product purchased and license issued successfully!");
		} catch (Exception e) {
			log.error("Error purchasing product: {}", ExceptionUtils.getStackTrace(e));
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}

	@PostMapping("/customer-products/reissue-token")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	@ResponseBody
	public Map<String, Object> reissueToken(@RequestParam Long licenseId,
			@RequestParam(required = false) String fingerprint) {
		Map<String, Object> response = new HashMap<>();
		try {
			this.licenseService.reissueToken(licenseId, fingerprint);
			response.put("success", true);
			response.put("message", "Fingerprint updated and token reissued successfully!");
		} catch (Exception e) {
			log.error("Error reissuing token: {}", ExceptionUtils.getStackTrace(e));
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}

	@GetMapping("/licenses/{id}/token/download")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_LICENSE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_LICENSE_LIST+"')")
	@ResponseBody
	public ResponseEntity<ByteArrayResource> downloadToken(@PathVariable Long id) {
		try {
			LicenseDTO license = this.licenseService.getLicenseById(id);
			if (!CommonValidators.validString(license.getTokenLocation())) {
				return ResponseEntity.notFound().build();
			}

			byte[] content = this.fileUtils.readFileBytes(license.getTokenLocation());
			ByteArrayResource resource = new ByteArrayResource(content);

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + license.getCode() + ".fxs\"")
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.contentLength(content.length)
					.body(resource);
		} catch (Exception e) {
			log.error("Error downloading token: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.internalServerError().build();
		}
	}
}