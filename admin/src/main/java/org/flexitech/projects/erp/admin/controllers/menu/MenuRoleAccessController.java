package org.flexitech.projects.erp.admin.controllers.menu;

import java.util.List;

import org.flexitech.projects.erp.commons.MenuCodeConstants;
import org.flexitech.projects.erp.commons.enums.MenuGroupCode;
import org.flexitech.projects.erp.dto.request.menu.CopyPermissionsRequest;
import org.flexitech.projects.erp.dto.request.menu.MenuRoleAccessRequest;
import org.flexitech.projects.erp.dto.request.menu.MenuRoleAccessTreeNode;
import org.flexitech.projects.erp.dto.response.ApiResponse;
import org.flexitech.projects.erp.dto.role.RoleDTO;
import org.flexitech.projects.erp.services.menu.MenuRoleAccessService;
import org.flexitech.projects.erp.services.role.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/menu-role-access")
@RequiredArgsConstructor
@Slf4j
public class MenuRoleAccessController {

	private final MenuRoleAccessService menuRoleAccessService;
	private final RoleService roleService;

	@GetMapping
	@PreAuthorize("@menuSecurity.hasMenuAccess('"+MenuCodeConstants.MENU_USER_MANAGEMENT_MENU_ACCESS_MANAGE+"') or @menuSecurity.hasMenuView('"+MenuCodeConstants.MENU_USER_MANAGEMENT_MENU_ACCESS_MANAGE+"')")
	public String manageAccess(Model model) {
		List<RoleDTO> roles = roleService.findAllActiveRoles();
		model.addAttribute("roles", roles);
		return "pages/menu-role-access/manage";
	}

	@GetMapping("/tree/{roleId}")
	@ResponseBody
	public ResponseEntity<List<MenuRoleAccessTreeNode>> getMenuTreeForRole(@PathVariable Long roleId) {
		try {
			List<MenuRoleAccessTreeNode> tree = menuRoleAccessService.getMenuTreeForRole(roleId);
			return ResponseEntity.ok(tree);
		} catch (Exception e) {
			log.error("Error loading menu tree for role: {}", roleId, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/save-permission")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_USER_MANAGEMENT_MENU_ACCESS_MANAGE+"')")
	@ResponseBody
	public ResponseEntity<ApiResponse<?>> savePermission(@RequestBody MenuRoleAccessRequest request) {
		try {
			menuRoleAccessService.saveOrUpdatePermission(request);
			return ResponseEntity.ok(ApiResponse.success("Permission saved successfully"));
		} catch (Exception e) {
			log.error("Error saving permission: {}", e.getMessage());
			return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
		}
	}

	@PostMapping("/save-bulk-permissions")
	@PreAuthorize("@menuSecurity.hasMenuEdit('"+MenuCodeConstants.MENU_USER_MANAGEMENT_MENU_ACCESS_MANAGE+"')")
	@ResponseBody
	public ResponseEntity<ApiResponse<?>> saveBulkPermissions(@RequestBody List<MenuRoleAccessRequest> requests) {
		try {
			menuRoleAccessService.saveBulkPermissions(requests);
			return ResponseEntity.ok(ApiResponse.success("Permissions saved successfully"));
		} catch (Exception e) {
			log.error("Error saving bulk permissions: {}", e.getMessage());
			return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
		}
	}

	@PostMapping("/delete-permission/{id}")
	@PreAuthorize("@menuSecurity.hasMenuDelete('"+MenuCodeConstants.MENU_USER_MANAGEMENT_MENU_ACCESS_MANAGE+"')")
	@ResponseBody
	public ResponseEntity<ApiResponse<?>> deletePermission(@PathVariable Long id) {
		try {
			menuRoleAccessService.deletePermission(id);
			return ResponseEntity.ok(ApiResponse.success("Permission deleted successfully"));
		} catch (Exception e) {
			log.error("Error deleting permission: {}", e.getMessage());
			return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
		}
	}

	@PostMapping("/copy-permissions")
	@ResponseBody
	public ResponseEntity<ApiResponse<?>> copyPermissions(@RequestBody CopyPermissionsRequest request) {
		try {
			menuRoleAccessService.copyPermissions(request.getSourceRoleId(), request.getTargetRoleId());
			return ResponseEntity.ok(ApiResponse.success("Permissions copied successfully"));
		} catch (Exception e) {
			log.error("Error copying permissions: {}", e.getMessage());
			return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
		}
	}

	@GetMapping("/legacy")
	public String legacyManageAccess(Model model, @RequestParam(required = false) Long roleId) {
		List<RoleDTO> roles = roleService.findAllActiveRoles();
		model.addAttribute("roles", roles);

		if (roleId != null) {
			model.addAttribute("selectedRoleId", roleId);
		}

		return "pages/menu-role-access/legacy-manage";
	}
}