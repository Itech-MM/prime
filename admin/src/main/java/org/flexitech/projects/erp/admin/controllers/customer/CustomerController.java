package org.flexitech.projects.erp.admin.controllers.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.customer.CustomerDTO;
import org.flexitech.projects.erp.dto.customer.CustomerSearchDTO;
import org.flexitech.projects.erp.services.customer.CustomerService;
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
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping("/customers/setup")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	public String customerSetupPage(Model model, @RequestParam(required = false) Long id) {

		CustomerDTO customerDTO = new CustomerDTO();
		if (CommonValidators.validLong(id)) {
			try {
				customerDTO = this.customerService.getCustomerById(id);
			} catch (Exception e) {
				log.error("Failed to get customer with id:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		commonSetupModel(model, customerDTO);
		return "pages/customer/setup";
	}

	@GetMapping("/customers")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	public String customerListPage(Model model) {

		model.addAttribute("searchDTO", new CustomerSearchDTO());
		Pageable page = Pageable.ofSize(CommonConstants.ROW_PER_PAGE);

		try {
			model.addAttribute("customerList", this.customerService.searchCustomers(new CustomerSearchDTO(), page));
		} catch (Exception e) {
			log.error("Error on customer list page: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "pages/customer/list";
	}

	@PostMapping("/customers/setup")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	public String manageCustomer(@Valid @ModelAttribute CustomerDTO customerDTO,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (result.hasErrors()) {
				commonSetupModel(model, customerDTO);
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Failed to save customer!");
				return "pages/customer/setup";
			}
			boolean isUpdate = CommonValidators.validLong(customerDTO.getId());

			customerService.manageCustomer(customerDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate ? "Customer update successfully!" : "Customer created successfully!");
			return "redirect:/customers";
		} catch (Exception e) {
			commonSetupModel(model, customerDTO);
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
			return "pages/customer/setup";
		}
	}

	@PostMapping("/customers/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	@ResponseBody
	public SearchResultDTO<CustomerDTO> searchCustomersAjax(@RequestBody CustomerSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return customerService.searchCustomers(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching customers: {}", e.getMessage());
			SearchResultDTO<CustomerDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}
	
	@GetMapping("/customers/select2")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	@ResponseBody
	public Map<String, Object> searchCustomersSelect2(@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			CustomerSearchDTO searchDTO = new CustomerSearchDTO();
			searchDTO.setName(q);
			
			Pageable pageable = PageRequest.of(page, CommonConstants.ROW_PER_PAGE, Sort.by("name").ascending());
			SearchResultDTO<CustomerDTO> result = customerService.searchCustomers(searchDTO, pageable);
			List<Map<String, Object>> results = new ArrayList<>();
			for (CustomerDTO brand : result.getResults()) {
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

	@PostMapping("/customers/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('"+MenuCodeConstants.MENU_CUSTOMER_LIST+"')")
	public String deleteCustomer(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			customerService.deleteCustomer(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Customer deleted successfully!");
		} catch (Exception e) {
			log.error("Error deleting customer: {}", ExceptionUtils.getStackTrace(e));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "redirect:/customers";
	}

	private void commonSetupModel(Model model, CustomerDTO customerDTO) {
		try {
			model.addAttribute("customerDTO", customerDTO);
			model.addAttribute("statusList", ActiveStatus.getAll());
		} catch (Exception e) {
			log.error("Error :: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}
	}
}