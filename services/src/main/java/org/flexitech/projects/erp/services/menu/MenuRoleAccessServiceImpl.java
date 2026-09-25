package org.flexitech.projects.erp.services.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.dto.menu.MenuDTO;
import org.flexitech.projects.erp.dto.menu.MenuRoleAccessDTO;
import org.flexitech.projects.erp.dto.request.menu.MenuRoleAccessRequest;
import org.flexitech.projects.erp.dto.request.menu.MenuRoleAccessTreeNode;
import org.flexitech.projects.erp.persistence.entities.menu.Menu;
import org.flexitech.projects.erp.persistence.entities.menu.MenuRoleAccess;
import org.flexitech.projects.erp.persistence.entities.role.Role;
import org.flexitech.projects.erp.persistence.repositories.menu.MenuRepository;
import org.flexitech.projects.erp.persistence.repositories.menu.MenuRoleAccessRepository;
import org.flexitech.projects.erp.persistence.repositories.role.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuRoleAccessServiceImpl implements MenuRoleAccessService {

    private final MenuRoleAccessRepository menuRoleAccessRepository;
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;

    public MenuRoleAccessServiceImpl(RoleRepository roleRepository, 
                                   MenuRoleAccessRepository menuRoleAccessRepository,
                                   MenuRepository menuRepository) {
        this.menuRoleAccessRepository = menuRoleAccessRepository;
        this.menuRepository = menuRepository;
        this.roleRepository = roleRepository;
    }

    // ==================== TREE-BASED METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public List<MenuRoleAccessTreeNode> getMenuTreeForRole(Long roleId) {
        // Get all active root menus (parent is null)
        List<Menu> rootMenus = menuRepository.findByParentMenuIsNullAndStatusOrderBySequenceAsc(1);
        
        // Get existing permissions for this role
        Map<Long, MenuRoleAccess> permissionsMap = menuRoleAccessRepository.findByRoleId(roleId).stream()
                .collect(Collectors.toMap(access -> access.getMenu().getId(), Function.identity()));
        
        // Build tree recursively
        return buildTreeNodes(rootMenus, roleId, permissionsMap, 0);
    }

    private List<MenuRoleAccessTreeNode> buildTreeNodes(List<Menu> menus, Long roleId, 
                                                       Map<Long, MenuRoleAccess> permissionsMap, int level) {
        List<MenuRoleAccessTreeNode> nodes = new ArrayList<>();
        
        for (Menu menu : menus) {
            MenuRoleAccessTreeNode node = new MenuRoleAccessTreeNode();
            node.setId(menu.getId());
            node.setName(menu.getName());
            node.setIcon(menu.getIcon());
            node.setUrl(menu.getUrl());
            node.setLevel(level);
            
            // Check if menu has children
            List<Menu> children = menuRepository.findByParentMenu_IdAndStatusOrderBySequenceAsc(menu.getId(), 1);
            node.setHasChildren(!children.isEmpty());
            
            // Set permissions if they exist
            if (permissionsMap.containsKey(menu.getId())) {
                MenuRoleAccess access = permissionsMap.get(menu.getId());
                node.setMenuRoleAccessId(access.getId());
                node.setCanView(access.getCanView());
                node.setCanAccess(access.getCanAccess());
                node.setCanEdit(access.getCanEdit());
                node.setCanDelete(access.getCanDelete());
                node.setIsDefault(access.getIsDefault());
                node.setPermissionPriority(access.getPermissionPriority());
            } else {
                // Default values if no permission exists
                node.setCanView(false);
                node.setCanAccess(false);
                node.setCanEdit(false);
                node.setCanDelete(false);
                node.setIsDefault(false);
                node.setPermissionPriority(0);
            }
            
            // Recursively build children if this menu has children
            if (!children.isEmpty()) {
                List<MenuRoleAccessTreeNode> childNodes = buildTreeNodes(children, roleId, permissionsMap, level + 1);
                node.setChildren(childNodes);
            }
            
            nodes.add(node);
        }
        
        return nodes;
    }

    @Override
    public void saveOrUpdatePermission(MenuRoleAccessRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + request.getRoleId()));
        
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu not found with ID: " + request.getMenuId()));
        
        MenuRoleAccess access;
        
        if (request.getId() != null) {
            // Update existing
            access = menuRoleAccessRepository.findById(request.getId())
                    .orElse(new MenuRoleAccess(role, menu));
        } else {
            // Check if already exists for this role and menu
            access = menuRoleAccessRepository.findByRoleIdAndMenuId(role.getId(), menu.getId())
                    .orElse(new MenuRoleAccess(role, menu));
        }
        
        // Update permissions (only update if provided)
        if (request.getCanView() != null) access.setCanView(request.getCanView());
        if (request.getCanAccess() != null) access.setCanAccess(request.getCanAccess());
        if (request.getCanEdit() != null) access.setCanEdit(request.getCanEdit());
        if (request.getCanDelete() != null) access.setCanDelete(request.getCanDelete());
        if (request.getIsDefault() != null) access.setIsDefault(request.getIsDefault());
        if (request.getPermissionPriority() != null) access.setPermissionPriority(request.getPermissionPriority());
        
        // Validate at least one permission is granted
		/*
		 * if (!access.hasAnyPermission()) { throw new
		 * RuntimeException("At least one permission must be granted"); }
		 */
        
        menuRoleAccessRepository.save(access);
    }

    @Override
    public void saveBulkPermissions(List<MenuRoleAccessRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return;
        }
        
        Map<Long, List<MenuRoleAccessRequest>> requestsByRole = requests.stream()
                .collect(Collectors.groupingBy(MenuRoleAccessRequest::getRoleId));
        
        for (Map.Entry<Long, List<MenuRoleAccessRequest>> entry : requestsByRole.entrySet()) {
            Long roleId = entry.getKey();
            roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
            
            for (MenuRoleAccessRequest request : entry.getValue()) {
                saveOrUpdatePermission(request);
            }
        }
    }

    @Override
    public void copyPermissions(Long sourceRoleId, Long targetRoleId) {
        roleRepository.findById(sourceRoleId)
                .orElseThrow(() -> new RuntimeException("Source role not found with ID: " + sourceRoleId));
        
        Role targetRole = roleRepository.findById(targetRoleId)
                .orElseThrow(() -> new RuntimeException("Target role not found with ID: " + targetRoleId));
        
        // Get all permissions from source role
        List<MenuRoleAccess> sourcePermissions = menuRoleAccessRepository.findByRoleId(sourceRoleId);
        
        // Get existing target permissions
        List<MenuRoleAccess> existingTargetPermissions = menuRoleAccessRepository.findByRoleId(targetRoleId);
        
        // Create a map for quick lookup of existing permissions
        Map<Long, MenuRoleAccess> existingTargetMap = existingTargetPermissions.stream()
                .collect(Collectors.toMap(access -> access.getMenu().getId(), Function.identity()));
        
        // Copy permissions
        for (MenuRoleAccess sourceAccess : sourcePermissions) {
            MenuRoleAccess targetAccess;
            
            if (existingTargetMap.containsKey(sourceAccess.getMenu().getId())) {
                // Update existing
                targetAccess = existingTargetMap.get(sourceAccess.getMenu().getId());
            } else {
                // Create new
                targetAccess = new MenuRoleAccess();
                targetAccess.setRole(targetRole);
                targetAccess.setMenu(sourceAccess.getMenu());
            }
            
            // Copy permissions
            targetAccess.setCanView(sourceAccess.getCanView());
            targetAccess.setCanAccess(sourceAccess.getCanAccess());
            targetAccess.setCanEdit(sourceAccess.getCanEdit());
            targetAccess.setCanDelete(sourceAccess.getCanDelete());
            targetAccess.setIsDefault(sourceAccess.getIsDefault());
            targetAccess.setPermissionPriority(sourceAccess.getPermissionPriority());
            
            menuRoleAccessRepository.save(targetAccess);
        }
    }

    @Override
    public void deletePermission(Long id) {
        menuRoleAccessRepository.deleteById(id);
    }

    // ==================== LEGACY METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public List<MenuRoleAccessDTO> getMenuAccessByRoleId(Long roleId) {
        List<Menu> parentMenus = this.menuRepository.findAllActiveParentMenu();
        List<MenuRoleAccessDTO> menuAccessList = new ArrayList<>();

        for (Menu parentMenu : parentMenus) {
            Optional<MenuRoleAccess> parentAccessOpt = this.menuRoleAccessRepository.findByRoleIdAndMenuId(roleId,
                    parentMenu.getId());
            List<Menu> childMenus = this.menuRepository.findByParentMenu_Id(parentMenu.getId());

            MenuRoleAccessDTO parentDto;

            if (parentAccessOpt.isPresent()) {
                parentDto = new MenuRoleAccessDTO(parentAccessOpt.get());
            } else {
                parentDto = MenuRoleAccessDTO.empty(parentMenu);
            }

            for (Menu childMenu : childMenus) {
                Optional<MenuRoleAccess> childAccessOpt = this.menuRoleAccessRepository.findByRoleIdAndMenuId(roleId,
                        childMenu.getId());
                MenuRoleAccessDTO childDto;

                if (childAccessOpt.isPresent()) {
                    childDto = new MenuRoleAccessDTO(childAccessOpt.get());
                } else {
                    childDto = MenuRoleAccessDTO.empty(childMenu);
                }
                parentDto.getChildMenuAccess().add(childDto);
            }

            menuAccessList.add(parentDto);
        }

        return menuAccessList;
    }

    @Override
    @Transactional(readOnly = true)
    public MenuRoleAccessDTO getMenuAccessById(Long id) {
        return menuRoleAccessRepository.findById(id).map(MenuRoleAccessDTO::new)
                .orElseThrow(() -> new RuntimeException("Menu access not found"));
    }

    @Override
    public MenuRoleAccessDTO saveMenuAccess(MenuRoleAccessDTO dto) {
        Role role = roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));

        Menu menu = menuRepository.findById(dto.getMenuId()).orElseThrow(() -> new RuntimeException("Menu not found"));

        MenuRoleAccess menuRoleAccess;

        if (dto.getId() != null) {
            menuRoleAccess = menuRoleAccessRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Menu access not found"));
        } else {
            menuRoleAccess = new MenuRoleAccess();
            menuRoleAccess.setRole(role);
            menuRoleAccess.setMenu(menu);
        }

        // Set permissions with null safety
        menuRoleAccess.setCanView(dto.getCanView() != null ? dto.getCanView() : false);
        menuRoleAccess.setCanAccess(dto.getCanAccess() != null ? dto.getCanAccess() : false);
        menuRoleAccess.setCanEdit(dto.getCanEdit() != null ? dto.getCanEdit() : false);
        menuRoleAccess.setCanDelete(dto.getCanDelete() != null ? dto.getCanDelete() : false);
        menuRoleAccess.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
        menuRoleAccess.setPermissionPriority(dto.getPermissionPriority() != null ? dto.getPermissionPriority() : 0);

        // Validate at least one permission
		/*
		 * if (!menuRoleAccess.hasAnyPermission()) { throw new
		 * RuntimeException("At least one permission must be granted"); }
		 */

        MenuRoleAccess saved = menuRoleAccessRepository.save(menuRoleAccess);
        return new MenuRoleAccessDTO(saved);
    }

    @Override
    public void saveBulkMenuAccess(Long roleId, List<Long> menuIds, List<Boolean> canView, List<Boolean> canAccess,
                                  List<Boolean> canEdit, List<Boolean> canDelete) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

        for (int i = 0; i < menuIds.size(); i++) {
            Long menuId = menuIds.get(i);
            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new RuntimeException("Menu not found with ID: " + menuId));

            MenuRoleAccess access = menuRoleAccessRepository.findByRoleIdAndMenuId(roleId, menuId)
                    .orElse(new MenuRoleAccess(role, menu));

            // Set permissions if provided
            if (canView != null && i < canView.size()) {
                access.setCanView(canView.get(i));
            }
            if (canAccess != null && i < canAccess.size()) {
                access.setCanAccess(canAccess.get(i));
            }
            if (canEdit != null && i < canEdit.size()) {
                access.setCanEdit(canEdit.get(i));
            }
            if (canDelete != null && i < canDelete.size()) {
                access.setCanDelete(canDelete.get(i));
            }

			/*
			 * if (!access.hasAnyPermission()) { throw new
			 * RuntimeException("At least one permission must be granted for menu: " +
			 * menu.getName()); }
			 */

            menuRoleAccessRepository.save(access);
        }
    }

    @Override
    public void deleteMenuAccess(Long id) {
        menuRoleAccessRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDTO> getMenusWithoutAccessForRole(Long roleId) {
        List<Long> accessedMenuIds = menuRoleAccessRepository.findMenuIdsByRoleId(roleId);

        if (accessedMenuIds.isEmpty()) {
            return menuRepository.findByStatusOrderBySequenceAsc(1).stream().map(MenuDTO::new).toList(); // Active menus
        }

        return menuRepository.findByIdNotInAndStatus(accessedMenuIds, 1).stream().map(MenuDTO::new).toList();
    }
}