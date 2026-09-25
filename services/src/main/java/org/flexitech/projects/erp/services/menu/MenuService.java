package org.flexitech.projects.erp.services.menu;

import java.util.List;

import org.flexitech.projects.erp.dto.menu.MenuDTO;

public interface MenuService {
	List<MenuDTO> getAllMenus(Integer status);
	
	List<MenuDTO> getMenuTreeForUser(Long userId);
}
