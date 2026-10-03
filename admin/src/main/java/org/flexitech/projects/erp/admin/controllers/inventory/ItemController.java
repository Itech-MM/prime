package org.flexitech.projects.erp.admin.controllers.inventory;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.admin.configs.MenuSecurity;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.enums.CostingMethod;
import org.flexitech.projects.erp.commons.enums.ItemType;
import org.flexitech.projects.erp.commons.enums.TrackingType;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.ItemDTO;
import org.flexitech.projects.erp.dto.inventory.search.ItemSearchDTO;
import org.flexitech.projects.erp.services.inventory.ItemService;
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
public class ItemController {

	private static final String INV_ITEM_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_ITEM + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_ITEM + "')";

	private static final String INV_MANAGEMENT_ACCESS =
			"@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_ITEM + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_ITEM + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_CATEGORY + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_CATEGORY + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_BRAND + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_BRAND + "')"
			+ " or @menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_INV_UOM + "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_INV_UOM + "')";

	private final ItemService itemService;
	private final MenuSecurity menuSecurity;

	public ItemController(ItemService itemService, MenuSecurity menuSecurity) {
		this.itemService = itemService;
		this.menuSecurity = menuSecurity;
	}

	@GetMapping("/inventory/items")
	@PreAuthorize(INV_MANAGEMENT_ACCESS)
	public String itemManagementPage(Model model, @RequestParam(required = false, defaultValue = "items") String tab) {

		model.addAttribute("activeTab", tab);
		model.addAttribute("searchDTO", new ItemSearchDTO());
		model.addAttribute("statusList", ActiveStatus.getAll());

		Pageable page = Pageable.ofSize(CommonConstants.ROW_PER_PAGE);
		try {
			model.addAttribute("itemList", this.itemService.searchItems(new ItemSearchDTO(), page));
		} catch (Exception e) {
			log.error("Error on item management page: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "pages/inventory/item/shell";
	}

	@GetMapping("/inventory/items/setup")
	@PreAuthorize(INV_MANAGEMENT_ACCESS)
	public String itemSetupPage(Model model,
			@RequestParam(required = false) Long id,
			@RequestParam(required = false, defaultValue = "items") String tab,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Long brandId,
			@RequestParam(required = false) Long uomId) {

		ItemDTO itemDTO = new ItemDTO();
		if (CommonValidators.validLong(id)) {
			try {
				itemDTO = this.itemService.getItemById(id);
			} catch (Exception e) {
				log.error("Failed to get item with id:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		model.addAttribute("activeTab", tab);
		model.addAttribute("presetCategoryEditId", categoryId);
		model.addAttribute("presetBrandEditId", brandId);
		model.addAttribute("presetUomEditId", uomId);

		model.addAttribute("viewItem", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_ITEM) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_ITEM));
		model.addAttribute("viewCategory", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_CATEGORY) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_CATEGORY));
		model.addAttribute("viewBrand", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_BRAND) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_BRAND));
		model.addAttribute("viewUom", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_UOM) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_UOM));

		model.addAttribute("canEditItem", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_ITEM));
		model.addAttribute("canEditCategory", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_CATEGORY));
		model.addAttribute("canEditBrand", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_BRAND));
		model.addAttribute("canEditUom", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_UOM));

		commonSetupModel(model, itemDTO);
		return "pages/inventory/item/setup";
	}

	@PostMapping("/inventory/items/setup")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_INV_ITEM + "')")
	public String manageItem(@Valid @ModelAttribute ItemDTO itemDTO,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (result.hasErrors()) {
				model.addAttribute("activeTab", "items");
				model.addAttribute("viewItem", true);
				model.addAttribute("viewCategory", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_CATEGORY) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_CATEGORY));
				model.addAttribute("viewBrand", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_BRAND) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_BRAND));
				model.addAttribute("viewUom", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_UOM) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_UOM));
				model.addAttribute("canEditItem", true);
				model.addAttribute("canEditCategory", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_CATEGORY));
				model.addAttribute("canEditBrand", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_BRAND));
				model.addAttribute("canEditUom", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_UOM));
				commonSetupModel(model, itemDTO);
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Failed to save item!");
				return "pages/inventory/item/setup";
			}
			boolean isUpdate = CommonValidators.validLong(itemDTO.getId());

			itemService.manageItem(itemDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate ? "Item updated successfully!" : "Item created successfully!");
			return "redirect:/inventory/items";
		} catch (Exception e) {
			model.addAttribute("activeTab", "items");
			model.addAttribute("viewItem", true);
			model.addAttribute("viewCategory", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_CATEGORY) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_CATEGORY));
			model.addAttribute("viewBrand", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_BRAND) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_BRAND));
			model.addAttribute("viewUom", menuSecurity.checkMenuAccess(MenuCodeConstants.MENU_INV_UOM) || menuSecurity.checkMenuView(MenuCodeConstants.MENU_INV_UOM));
			model.addAttribute("canEditItem", true);
			model.addAttribute("canEditCategory", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_CATEGORY));
			model.addAttribute("canEditBrand", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_BRAND));
			model.addAttribute("canEditUom", menuSecurity.checkMenuEdit(MenuCodeConstants.MENU_INV_UOM));
			commonSetupModel(model, itemDTO);
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
			return "pages/inventory/item/setup";
		}
	}

	@PostMapping("/inventory/items/search")
	@PreAuthorize(INV_ITEM_ACCESS)
	@ResponseBody
	public SearchResultDTO<ItemDTO> searchItemsAjax(@RequestBody ItemSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return itemService.searchItems(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching items: {}", e.getMessage());
			SearchResultDTO<ItemDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@PostMapping("/inventory/items/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_INV_ITEM + "')")
	public String deleteItem(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			itemService.deleteItem(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Item deleted successfully!");
		} catch (Exception e) {
			log.error("Error deleting item: {}", ExceptionUtils.getStackTrace(e));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "redirect:/inventory/items";
	}
	
	@GetMapping("/inventory/items/select2")
	@PreAuthorize(INV_ITEM_ACCESS)
	@ResponseBody
	public java.util.Map<String, Object> select2(@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page) {

		ItemSearchDTO searchDTO = new ItemSearchDTO();
		searchDTO.setName(q);

		java.util.Map<String, Object> response = new java.util.HashMap<>();
		try {
			Pageable pageable = PageRequest.of(page, 10, Sort.by("name").ascending());
			SearchResultDTO<ItemDTO> result = itemService.searchItems(searchDTO, pageable);

			java.util.List<java.util.Map<String, Object>> results = new java.util.ArrayList<>();
			for (ItemDTO item : result.getResults()) {
				java.util.Map<String, Object> option = new java.util.HashMap<>();
				option.put("id", item.getId());
				option.put("text", item.getCode() + " - " + item.getName());
				option.put("trackingType", item.getTrackingType());
				option.put("baseUomId", item.getBaseUomId());
				option.put("baseUomCode", item.getBaseUomCode());
				option.put("standardCost", item.getStandardCost());
				results.add(option);
			}

			java.util.Map<String, Object> pagination = new java.util.HashMap<>();
			pagination.put("more", result.getHasNextPage());

			response.put("results", results);
			response.put("pagination", pagination);
		} catch (Exception e) {
			response.put("results", new java.util.ArrayList<>());
		}
		return response;
	}

	private void commonSetupModel(Model model, ItemDTO itemDTO) {
		try {
			model.addAttribute("itemDTO", itemDTO);
			model.addAttribute("statusList", ActiveStatus.getAll());
			model.addAttribute("itemTypeList", ItemType.getAll());
			model.addAttribute("trackingTypeList", TrackingType.getAll());
			model.addAttribute("costingMethodList", CostingMethod.getAll());
		} catch (Exception e) {
			log.error("Error :: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}
	}
}