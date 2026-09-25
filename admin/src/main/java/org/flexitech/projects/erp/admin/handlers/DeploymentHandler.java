package org.flexitech.projects.erp.admin.handlers;

import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.persistence.entities.role.Role;
import org.flexitech.projects.erp.persistence.entities.user.User;
import org.flexitech.projects.erp.persistence.repositories.role.RoleRepository;
import org.flexitech.projects.erp.persistence.repositories.user.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * For Development Only
 */
@Component
@Profile("dev")
public class DeploymentHandler {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	public DeploymentHandler(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@PostConstruct
	protected void preHandle() throws Exception {
		
		List<Role> roles = this.roleRepository.findAll();
		Role admin = null;
		if(!CommonValidators.validList(roles)) {
			Role role = new Role();
			role.setName("Admin");
			role.setCode("ROLE_ADMIN");
			admin = this.roleRepository.save(role);
		}
		
		if(admin == null) {
			admin = this.roleRepository.findById(1L).orElseThrow(()-> new Exception("Role not found!"));
		}
		
		List<User> users = this.userRepository.findAll();
		if(!CommonValidators.validList(users)) {
			User user = new User();
			user.setName("Admin");
			user.setPhoneNumber("09883360492");
			user.setPassword(passwordEncoder.encode("123456"));
			user.setStatus(ActiveStatus.ACTIVE.getCode());
			user.setRole(admin);
			user.setStatus(1);
			
			this.userRepository.save(user);
		}
	}
	
}
