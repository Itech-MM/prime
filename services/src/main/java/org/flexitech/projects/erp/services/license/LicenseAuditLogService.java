package org.flexitech.projects.erp.services.license;

import java.util.List;

import org.flexitech.projects.erp.dto.license.LicenseAuditLogDTO;

public interface LicenseAuditLogService {

	void log(Long licenseId, String eventType, String details) throws Exception;

	List<LicenseAuditLogDTO> getLogsByLicenseId(Long licenseId) throws Exception;
}