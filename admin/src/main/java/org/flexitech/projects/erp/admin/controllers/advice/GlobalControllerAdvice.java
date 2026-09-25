package org.flexitech.projects.erp.admin.controllers.advice;

import java.util.Collections;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;
import org.flexitech.projects.erp.commons.enums.MenuGroupCode;
import org.flexitech.projects.erp.dto.menu.MenuDTO;
import org.flexitech.projects.erp.persistence.entities.user.User;
import org.flexitech.projects.erp.services.auth.AuthenticationService;
import org.flexitech.projects.erp.services.menu.MenuService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
	private final MenuService menuService;
	private final AuthenticationService authenticationService;

	@ModelAttribute("menus")
	public List<MenuDTO> addMenusToModel() {
		User user = authenticationService.getLoggedInUser();
		if (user != null) {
			System.out.println("Logged user:: " + user.getId());
			return menuService.getMenuTreeForUser(user.getId());
		}
		return Collections.emptyList();
	}

	@ModelAttribute("menuGroupCodeList")
	public List<CommonEnumObject> getMenuGroupCodeList() {
		return MenuGroupCode.getAll();
	}

}
