package org.flexitech.projects.erp.persistence.repositories.role;

import java.util.List;

import org.flexitech.projects.erp.persistence.entities.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {
	@Query("SELECT m FROM Role m WHERE (:status IS NULL OR m.status = :status)")
	List<Role> findByStatus(@Param("status") Integer status);
}
