package org.flexitech.projects.erp.admin.controllers.role;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.dto.role.RoleDTO;
import org.flexitech.projects.erp.services.role.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping("roles")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_ROLE_LIST+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_ROLE_LIST+"')")
	public String roleSetup(Model model, @RequestParam(required = false) Long id) {
		RoleDTO role = new RoleDTO();
		try {
			if(CommonValidators.validLong(id)) {
				role = this.roleService.getRoleById(id);
			}
			commonModel(model, role);
		}catch(Exception e) {
			log.error("Error on role setup:: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}
		return "pages/role/setup";
	}

	@PostMapping("roles")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_ROLE_LIST+"')")
	public String roleManage(@Valid@ModelAttribute RoleDTO roleDTO, BindingResult result, Model model, RedirectAttributes attr) {

		try {
			if(result.hasErrors()) {
				commonModel(model, roleDTO);
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Please check all fields!");
				return "pages/role/setup";
			}
			boolean isUpdate = CommonValidators.validLong(roleDTO.getId());

			RoleDTO saved = this.roleService.manageRole(roleDTO);

			if(saved == null) {
				commonModel(model, roleDTO);
				model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, isUpdate ? "Failed to update role!":"Failed to create role");
				return "pages/role/setup";
			}

			attr.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, isUpdate ? "Create role success!":"Update role success!");
		}catch(Exception e) {
			log.error("Error on role setup:: {}", ExceptionUtils.getStackTrace(e));
			commonModel(model, roleDTO);
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}

		return "redirect:/roles";
	}

	private void commonModel(Model model, RoleDTO role) {
		try {
			model.addAttribute("roleDTO", role);
			model.addAttribute("roleList", this.roleService.getAllRoles());
		}catch(Exception e) {
			log.error("Error on role setup:: {}", ExceptionUtils.getStackTrace(e));
			model.addAttribute(CommonConstants.FORM_ERROR_MESSAGE, e.getMessage());
		}
	}

}
