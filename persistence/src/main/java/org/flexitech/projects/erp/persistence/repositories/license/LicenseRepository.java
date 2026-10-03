package org.flexitech.projects.erp.persistence.repositories.license;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.license.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LicenseRepository extends JpaRepository<License, Long>, JpaSpecificationExecutor<License> {
	Optional<License> findByCode(String code);
	long countByPlanIdAndCodeStartingWith(Long planId, String prefix);
	Optional<License> findByCustomerProductId(Long customerProductId);

	long countByIssuedAtBetween(Date start, Date end);
	long countByStatusAndIssuedAtBetween(Integer status, Date start, Date end);
	List<License> findByIssuedAtBetweenOrderByIssuedAtAsc(Date start, Date end);
	List<License> findTop5ByOrderByIssuedAtDesc();
	List<License> findByStatusAndExpiresAtBetweenOrderByExpiresAtAsc(Integer status, Date start, Date end);
}