package org.flexitech.projects.erp.admin.controllers.user;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.user.UserDTO;
import org.flexitech.projects.erp.dto.user.UserSearchDTO;
import org.flexitech.projects.erp.services.role.RoleService;
import org.flexitech.projects.erp.services.user.UserService;
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
public class UserController {

	private final UserService userService;
	private final RoleService roleService;

	public UserController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}

	@GetMapping("/user-setup")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_USER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_USER_LIST+"')")
	public String userSetupPage(Model model, @RequestParam(required = false) Long id) {

		UserDTO userDTO = new UserDTO();
		if(CommonValidators.validLong(id)) {
			try {
				userDTO = this.userService.getUserById(id);
			} catch (Exception e) {
				log.error("Failed to get user with id:: {}", ExceptionUtils.getStackTrace(e));
			}
		}

		commonSetupModel(model, userDTO);
		return "pages/user/setup";
	}

	@GetMapping("/users")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_USER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_USER_LIST+"')")
	public String userListPage(Model model) {

		model.addAttribute("searchDTO", new UserSearchDTO());
		Pageable page = Pageable.ofSize(CommonConstants.ROW_PER_PAGE);

		try {
			model.addAttribute("userList", this.userService.searchUsers(new UserSearchDTO(), page));
		} catch (Exception e) {
			log.error("Error on user setup page: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "pages/user/list";
	}


	@PostMapping("/user-setup")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_USER_LIST+"')")
    public String manageUser(@Valid @ModelAttribute UserDTO userDTO,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                commonSetupModel(model, userDTO);
                model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Failed to save user!");
                return "pages/user/setup";
            }
            boolean isUpdate = CommonValidators.validLong(userDTO.getId());

            userService.manageUser(userDTO);
            redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate? "User update successfully!":"User created successfully!");
            return "redirect:/users";
        } catch (Exception e) {
        	commonSetupModel(model, userDTO);
            model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
            return "pages/user/setup";
        }
    }

	@PostMapping("/users/search")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_USER_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_USER_LIST+"')")
	@ResponseBody
	public SearchResultDTO<UserDTO> searchUsersAjax(@RequestBody UserSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
			return userService.searchUsers(searchDTO, pageable);
		} catch (Exception e) {
			log.error("Error searching users: {}", e.getMessage());
			// Return empty result on error
			SearchResultDTO<UserDTO> emptyResult = new SearchResultDTO<>();
			emptyResult.setResults(new ArrayList<>());
			emptyResult.setTotalRecords(0);
			emptyResult.setTotalPage(0);
			emptyResult.setPageNo(0);
			return emptyResult;
		}
	}

	@PostMapping("/users/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('"+MenuCodeConstants.MENU_USER_LIST+"')")
	public String deleteProduct(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			userService.deleteUser(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "User deleted successfully!");
		} catch (Exception e) {
			log.error("Error deleting product: {}", ExceptionUtils.getStackTrace(e));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "redirect:/users";
	}


	private void commonSetupModel(Model model, UserDTO userDTO) {
        try {
        	model.addAttribute("userDTO", userDTO);
    		model.addAttribute("statusList", ActiveStatus.getAll());
			model.addAttribute("roleList", this.roleService.getAllRoles());
		} catch (Exception e) {
			log.error("Error :: {}", ExceptionUtils.getStackTrace(e));
			 model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

	}
}
