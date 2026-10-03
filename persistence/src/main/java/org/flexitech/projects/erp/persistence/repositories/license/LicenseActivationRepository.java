package org.flexitech.projects.erp.persistence.repositories.license;

import java.util.List;

import org.flexitech.projects.erp.persistence.entities.license.LicenseActivation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseActivationRepository extends JpaRepository<LicenseActivation, Long> {
	List<LicenseActivation> findByLicenseId(Long licenseId);
	List<LicenseActivation> findByLicenseIdAndMachineFingerprint(Long licenseId, String machineFingerprint);
}