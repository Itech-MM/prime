package org.flexitech.projects.erp.persistence.repositories.license;

import java.util.List;
import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.license.LicensePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LicensePlanRepository extends JpaRepository<LicensePlan, Long>, JpaSpecificationExecutor<LicensePlan> {
	Optional<LicensePlan> findByCode(String code);
	List<LicensePlan> findByProductId(Long productId);
}