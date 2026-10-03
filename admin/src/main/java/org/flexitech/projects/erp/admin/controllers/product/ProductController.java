package org.flexitech.projects.erp.admin.controllers.product;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.utils.FileUtils;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.product.ProductDTO;
import org.flexitech.projects.erp.dto.product.ProductSearchDTO;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ProductController {

	private final ProductService productService;
	private final FileUtils fileUtils;

	public ProductController(ProductService productService, FileUtils fileUtils) {
		this.productService = productService;
		this.fileUtils = fileUtils;
	}

	@GetMapping("/products/setup")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_PRODUCT_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_PRODUCT_LIST+"')")
	public String productSetupPage(Model model, @RequestParam(required = false) Long id) {

		ProductDTO productDTO = new ProductDTO();
		if (CommonValidators.validLong(id)) {
			try {
				productDTO = this.productService.getProductById(id);
			} catch (Exception e) {
				log.error("Failed to get product with id:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		commonSetupModel(model, productDTO);
		return "pages/product/setup";
	}

	@GetMapping("/products")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_PRODUCT_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_PRODUCT_LIST+"')")
	public String productListPage(Model model) {

		model.addAttribute("searchDTO", new ProductSearchDTO());
		Pageable page = Pageable.ofSize(CommonConstants.ROW_PER_PAGE);

		try {
			model.addAttribute("productList", this.productService.searchProducts(new ProductSearchDTO(), page));
		} catch (Exception e) {
			log.error("Error on product list page: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "pages/product/list";
	}

	@PostMapping("/products/setup")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_PRODUCT_LIST+"')")
	public String manageProduct(@Valid @ModelAttribute ProductDTO productDTO,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (result.hasErrors()) {
				commonSetupModel(model, productDTO);
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Failed to save product!");
				return "pages/product/setup";
			}
			boolean isUpdate = CommonValidators.validLong(productDTO.getId());

			productService.manageProduct(productDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate ? "Product update successfully!" : "Product created successfully!");
			return "redirect:/products";
		} catch (Exception e) {
			commonSetupModel(model, productDTO);
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
			return "pages/product/setup";
		}
	}

	@PostMapping("/products/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_PRODUCT_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_PRODUCT_LIST+"')")
	@ResponseBody
	public SearchResultDTO<ProductDTO> searchProductsAjax(@RequestBody ProductSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return productService.searchProducts(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching products: {}", e.getMessage());
			SearchResultDTO<ProductDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@PostMapping("/products/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('"+MenuCodeConstants.MENU_PRODUCT_LIST+"')")
	public String deleteProduct(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			productService.deleteProduct(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Product deleted successfully!");
		} catch (Exception e) {
			log.error("Error deleting product: {}", ExceptionUtils.getStackTrace(e));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "redirect:/products";
	}

	@GetMapping("/products/{id}/public-key/download")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_PRODUCT_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_PRODUCT_LIST+"')")
	@ResponseBody
	public ResponseEntity<ByteArrayResource> downloadPublicKey(@PathVariable Long id) {
		try {
			ProductDTO productDTO = this.productService.getProductById(id);
			if (!CommonValidators.validString(productDTO.getPublicKeyLocation())) {
				return ResponseEntity.notFound().build();
			}

			byte[] content = this.fileUtils.readFileBytes(productDTO.getPublicKeyLocation());
			ByteArrayResource resource = new ByteArrayResource(content);

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + productDTO.getCode() + "-public.pem\"")
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.contentLength(content.length)
					.body(resource);
		} catch (Exception e) {
			log.error("Error downloading public key: {}", ExceptionUtils.getStackTrace(e));
			return ResponseEntity.internalServerError().build();
		}
	}

	private void commonSetupModel(Model model, ProductDTO productDTO) {
		try {
			model.addAttribute("productDTO", productDTO);
			model.addAttribute("statusList", ActiveStatus.getAll());
		} catch (Exception e) {
			log.error("Error :: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}
	}
}