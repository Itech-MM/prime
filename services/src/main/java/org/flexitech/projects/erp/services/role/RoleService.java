package org.flexitech.projects.erp.services.role;

import java.util.List;

import org.flexitech.projects.erp.dto.role.RoleDTO;

public interface RoleService {
	RoleDTO manageRole(RoleDTO roleDTO) throws Exception;
	RoleDTO getRoleById(Long id) throws Exception;
	List<RoleDTO> getAllRoles() throws Exception;
	boolean deleteRole(Long id) throws Exception;
	List<RoleDTO> findAllActiveRoles();
}
