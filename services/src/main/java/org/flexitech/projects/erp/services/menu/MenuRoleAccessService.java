package org.flexitech.projects.erp.services.menu;

import java.util.List;

import org.flexitech.projects.erp.dto.menu.MenuDTO;
import org.flexitech.projects.erp.dto.menu.MenuRoleAccessDTO;
import org.flexitech.projects.erp.dto.request.menu.MenuRoleAccessRequest;
import org.flexitech.projects.erp.dto.request.menu.MenuRoleAccessTreeNode;

public interface MenuRoleAccessService {	
    List<MenuRoleAccessTreeNode> getMenuTreeForRole(Long roleId);
    void saveOrUpdatePermission(MenuRoleAccessRequest request);
    void saveBulkPermissions(List<MenuRoleAccessRequest> requests);
    void copyPermissions(Long sourceRoleId, Long targetRoleId);
    void deletePermission(Long id);
    
    List<MenuRoleAccessDTO> getMenuAccessByRoleId(Long roleId);
    MenuRoleAccessDTO getMenuAccessById(Long id);
    MenuRoleAccessDTO saveMenuAccess(MenuRoleAccessDTO dto);
    void saveBulkMenuAccess(Long roleId, List<Long> menuIds,
                          List<Boolean> canView, List<Boolean> canAccess,
                          List<Boolean> canEdit, List<Boolean> canDelete);
    void deleteMenuAccess(Long id);
    List<MenuDTO> getMenusWithoutAccessForRole(Long roleId);
}