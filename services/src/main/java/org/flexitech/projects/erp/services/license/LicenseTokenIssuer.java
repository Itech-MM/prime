package org.flexitech.projects.erp.services.license;

import org.flexitech.projects.erp.dto.license.LicenseDTO;

public interface LicenseTokenIssuer {
	LicenseDTO issueToken(Long licenseId) throws Exception;
}