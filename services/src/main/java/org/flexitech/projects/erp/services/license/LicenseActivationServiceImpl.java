package org.flexitech.projects.erp.services.license;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.license.LicenseActivationDTO;
import org.flexitech.projects.erp.persistence.entities.license.License;
import org.flexitech.projects.erp.persistence.entities.license.LicenseActivation;
import org.flexitech.projects.erp.persistence.repositories.license.LicenseActivationRepository;
import org.flexitech.projects.erp.persistence.repositories.license.LicenseRepository;
import org.springframework.stereotype.Service;

@Service
public class LicenseActivationServiceImpl implements LicenseActivationService {

	private final LicenseActivationRepository licenseActivationRepository;
	private final LicenseRepository licenseRepository;

	public LicenseActivationServiceImpl(LicenseActivationRepository licenseActivationRepository,
			LicenseRepository licenseRepository) {
		this.licenseActivationRepository = licenseActivationRepository;
		this.licenseRepository = licenseRepository;
	}

	@Override
	public LicenseActivationDTO recordActivation(Long licenseId, String machineFingerprint) throws Exception {
		License license = this.licenseRepository.findById(licenseId)
				.orElseThrow(() -> new Exception("License not found!"));

		LicenseActivation activation = new LicenseActivation();
		activation.setLicense(license);
		activation.setMachineFingerprint(machineFingerprint);
		activation.setActivatedAt(Calendar.getInstance().getTime());
		activation.setLastCheckinAt(Calendar.getInstance().getTime());
		activation.setStatus(ActiveStatus.ACTIVE.getCode());

		LicenseActivation saved = this.licenseActivationRepository.save(activation);
		return new LicenseActivationDTO(saved);
	}

	@Override
	public List<LicenseActivationDTO> getActivationsByLicenseId(Long licenseId) throws Exception {
		return this.licenseActivationRepository.findByLicenseId(licenseId).stream()
				.map(LicenseActivationDTO::new)
				.collect(Collectors.toList());
	}
}