package org.flexitech.projects.erp.admin.controllers;

import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WelcomeController {

	@GetMapping("/")
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_DASHBOARD+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_DASHBOARD+"')")
	public String dashboard() {
		log.debug("continue to dashboard");
		return "pages/dashboard/index";
	}

}
