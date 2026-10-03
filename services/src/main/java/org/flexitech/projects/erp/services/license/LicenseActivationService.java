package org.flexitech.projects.erp.services.license;

import java.util.List;

import org.flexitech.projects.erp.dto.license.LicenseActivationDTO;

public interface LicenseActivationService {

	LicenseActivationDTO recordActivation(Long licenseId, String machineFingerprint) throws Exception;

	List<LicenseActivationDTO> getActivationsByLicenseId(Long licenseId) throws Exception;
}