package org.flexitech.projects.erp.services.user;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.user.UserDTO;
import org.flexitech.projects.erp.dto.user.UserSearchDTO;
import org.springframework.data.domain.Pageable;

public interface UserService {
	
	UserDTO manageUser(UserDTO userDTO) throws Exception;
	
	UserDTO getUserById(Long id)throws Exception;
	
	SearchResultDTO<UserDTO> searchUsers(UserSearchDTO searchDTO, Pageable pageable) throws Exception;
	
	boolean deleteUser(Long id) throws Exception;
}
