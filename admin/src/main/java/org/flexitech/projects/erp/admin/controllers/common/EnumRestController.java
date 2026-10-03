package org.flexitech.projects.erp.admin.controllers.common;

import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enums")
public class EnumRestController {

	@GetMapping("/active-status")
	public List<CommonEnumObject> getActiveStatusList(){
		return ActiveStatus.getAll();
	}
	
}
