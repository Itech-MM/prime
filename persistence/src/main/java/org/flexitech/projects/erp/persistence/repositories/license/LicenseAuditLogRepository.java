package org.flexitech.projects.erp.persistence.repositories.license;

import java.util.List;

import org.flexitech.projects.erp.persistence.entities.license.LicenseAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseAuditLogRepository extends JpaRepository<LicenseAuditLog, Long> {
	List<LicenseAuditLog> findByLicenseIdOrderByEventTimeDesc(Long licenseId);
}