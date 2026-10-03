package org.flexitech.projects.erp.services.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

    @Override
    @Transactional(readOnly = true)
    public List<MenuRoleAccessTreeNode> getMenuTreeForRole(Long roleId) {
        List<Menu> activeMenus = menuRepository.findByStatusOrderBySequenceAsc(1);

        List<Menu> rootMenus = new ArrayList<>();
        Map<Long, List<Menu>> childrenByParent = new HashMap<>();
        for (Menu menu : activeMenus) {
            if (menu.getParentMenu() == null) {
                rootMenus.add(menu);
            } else {
                childrenByParent.computeIfAbsent(menu.getParentMenu().getId(), k -> new ArrayList<>()).add(menu);
            }
        }

        Map<Long, MenuRoleAccess> permissionsMap = menuRoleAccessRepository.findByRoleId(roleId).stream()
                .collect(Collectors.toMap(access -> access.getMenu().getId(), Function.identity()));

        return buildTreeNodes(rootMenus, childrenByParent, permissionsMap, 0);
    }

    private List<MenuRoleAccessTreeNode> buildTreeNodes(List<Menu> menus, Map<Long, List<Menu>> childrenByParent,
                                                       Map<Long, MenuRoleAccess> permissionsMap, int level) {
        List<MenuRoleAccessTreeNode> nodes = new ArrayList<>();

        for (Menu menu : menus) {
            MenuRoleAccessTreeNode node = new MenuRoleAccessTreeNode();
            node.setId(menu.getId());
            node.setName(menu.getName());
            node.setIcon(menu.getIcon());
            node.setUrl(menu.getUrl());
            node.setLevel(level);

            List<Menu> children = childrenByParent.getOrDefault(menu.getId(), List.of());
            node.setHasChildren(!children.isEmpty());

            MenuRoleAccess access = permissionsMap.get(menu.getId());
            if (access != null) {
                node.setMenuRoleAccessId(access.getId());
                node.setCanView(access.getCanView());
                node.setCanAccess(access.getCanAccess());
                node.setCanEdit(access.getCanEdit());
                node.setCanApprove(access.getCanApprove());
                node.setCanDelete(access.getCanDelete());
                node.setIsDefault(access.getIsDefault());
                node.setPermissionPriority(access.getPermissionPriority());
            } else {
                node.setCanView(false);
                node.setCanAccess(false);
                node.setCanEdit(false);
                node.setCanApprove(false);
                node.setCanDelete(false);
                node.setIsDefault(false);
                node.setPermissionPriority(0);
            }

            if (!children.isEmpty()) {
                node.setChildren(buildTreeNodes(children, childrenByParent, permissionsMap, level + 1));
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
            access = menuRoleAccessRepository.findById(request.getId())
                    .orElseGet(() -> new MenuRoleAccess(role, menu));
        } else {
            access = menuRoleAccessRepository.findByRoleIdAndMenuId(role.getId(), menu.getId())
                    .orElseGet(() -> new MenuRoleAccess(role, menu));
        }

        if (request.getCanView() != null) access.setCanView(request.getCanView());
        if (request.getCanAccess() != null) access.setCanAccess(request.getCanAccess());
        if (request.getCanEdit() != null) access.setCanEdit(request.getCanEdit());
        if (request.getCanApprove() != null) access.setCanApprove(request.getCanApprove());
        if (request.getCanDelete() != null) access.setCanDelete(request.getCanDelete());
        if (request.getIsDefault() != null) access.setIsDefault(request.getIsDefault());
        if (request.getPermissionPriority() != null) access.setPermissionPriority(request.getPermissionPriority());

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
        if (sourceRoleId == null || targetRoleId == null) {
            throw new RuntimeException("Source and target role are required");
        }
        if (sourceRoleId.equals(targetRoleId)) {
            throw new RuntimeException("Source and target role must be different");
        }

        roleRepository.findById(sourceRoleId)
                .orElseThrow(() -> new RuntimeException("Source role not found with ID: " + sourceRoleId));

        Role targetRole = roleRepository.findById(targetRoleId)
                .orElseThrow(() -> new RuntimeException("Target role not found with ID: " + targetRoleId));

        List<MenuRoleAccess> sourcePermissions = menuRoleAccessRepository.findByRoleId(sourceRoleId);
        List<MenuRoleAccess> existingTargetPermissions = menuRoleAccessRepository.findByRoleId(targetRoleId);

        Map<Long, MenuRoleAccess> existingTargetMap = existingTargetPermissions.stream()
                .collect(Collectors.toMap(access -> access.getMenu().getId(), Function.identity()));

        Set<Long> sourceMenuIds = new HashSet<>();

        for (MenuRoleAccess sourceAccess : sourcePermissions) {
            Long menuId = sourceAccess.getMenu().getId();
            sourceMenuIds.add(menuId);

            MenuRoleAccess targetAccess = existingTargetMap.get(menuId);
            if (targetAccess == null) {
                targetAccess = new MenuRoleAccess();
                targetAccess.setRole(targetRole);
                targetAccess.setMenu(sourceAccess.getMenu());
            }

            targetAccess.setCanView(sourceAccess.getCanView());
            targetAccess.setCanAccess(sourceAccess.getCanAccess());
            targetAccess.setCanEdit(sourceAccess.getCanEdit());
            targetAccess.setCanApprove(sourceAccess.getCanApprove());
            targetAccess.setCanDelete(sourceAccess.getCanDelete());
            targetAccess.setIsDefault(sourceAccess.getIsDefault());
            targetAccess.setPermissionPriority(sourceAccess.getPermissionPriority());

            menuRoleAccessRepository.save(targetAccess);
        }

        List<MenuRoleAccess> obsolete = existingTargetPermissions.stream()
                .filter(access -> !sourceMenuIds.contains(access.getMenu().getId()))
                .toList();
        if (!obsolete.isEmpty()) {
            menuRoleAccessRepository.deleteAll(obsolete);
        }
    }

    @Override
    public void deletePermission(Long id) {
        menuRoleAccessRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuRoleAccessDTO> getMenuAccessByRoleId(Long roleId) {
        List<Menu> parentMenus = this.menuRepository.findAllActiveParentMenu();
        List<MenuRoleAccessDTO> menuAccessList = new ArrayList<>();

        for (Menu parentMenu : parentMenus) {
            Optional<MenuRoleAccess> parentAccessOpt = this.menuRoleAccessRepository.findByRoleIdAndMenuId(roleId,
                    parentMenu.getId());
            List<Menu> childMenus = this.menuRepository.findByParentMenu_Id(parentMenu.getId());

            MenuRoleAccessDTO parentDto = parentAccessOpt.isPresent()
                    ? new MenuRoleAccessDTO(parentAccessOpt.get())
                    : MenuRoleAccessDTO.empty(parentMenu);

            for (Menu childMenu : childMenus) {
                Optional<MenuRoleAccess> childAccessOpt = this.menuRoleAccessRepository.findByRoleIdAndMenuId(roleId,
                        childMenu.getId());
                MenuRoleAccessDTO childDto = childAccessOpt.isPresent()
                        ? new MenuRoleAccessDTO(childAccessOpt.get())
                        : MenuRoleAccessDTO.empty(childMenu);
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

        menuRoleAccess.setCanView(dto.getCanView() != null ? dto.getCanView() : false);
        menuRoleAccess.setCanAccess(dto.getCanAccess() != null ? dto.getCanAccess() : false);
        menuRoleAccess.setCanEdit(dto.getCanEdit() != null ? dto.getCanEdit() : false);
        menuRoleAccess.setCanApprove(dto.getCanApprove() != null ? dto.getCanApprove() : false);
        menuRoleAccess.setCanDelete(dto.getCanDelete() != null ? dto.getCanDelete() : false);
        menuRoleAccess.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
        menuRoleAccess.setPermissionPriority(dto.getPermissionPriority() != null ? dto.getPermissionPriority() : 0);

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
                    .orElseGet(() -> new MenuRoleAccess(role, menu));

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
            return menuRepository.findByStatusOrderBySequenceAsc(1).stream().map(MenuDTO::new).toList();
        }

        return menuRepository.findByIdNotInAndStatus(accessedMenuIds, 1).stream().map(MenuDTO::new).toList();
    }
}