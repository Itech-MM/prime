package org.flexitech.projects.erp.services.license;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.dto.license.LicenseAuditLogDTO;
import org.flexitech.projects.erp.persistence.entities.license.License;
import org.flexitech.projects.erp.persistence.entities.license.LicenseAuditLog;
import org.flexitech.projects.erp.persistence.repositories.license.LicenseAuditLogRepository;
import org.flexitech.projects.erp.persistence.repositories.license.LicenseRepository;
import org.springframework.stereotype.Service;

@Service
public class LicenseAuditLogServiceImpl implements LicenseAuditLogService {

	private final LicenseAuditLogRepository licenseAuditLogRepository;
	private final LicenseRepository licenseRepository;

	public LicenseAuditLogServiceImpl(LicenseAuditLogRepository licenseAuditLogRepository,
			LicenseRepository licenseRepository) {
		this.licenseAuditLogRepository = licenseAuditLogRepository;
		this.licenseRepository = licenseRepository;
	}

	@Override
	public void log(Long licenseId, String eventType, String details) throws Exception {
		License license = this.licenseRepository.findById(licenseId)
				.orElseThrow(() -> new Exception("License not found!"));

		LicenseAuditLog auditLog = new LicenseAuditLog();
		auditLog.setLicense(license);
		auditLog.setEventType(eventType);
		auditLog.setEventTime(Calendar.getInstance().getTime());
		auditLog.setDetails(details);

		this.licenseAuditLogRepository.save(auditLog);
	}

	@Override
	public List<LicenseAuditLogDTO> getLogsByLicenseId(Long licenseId) throws Exception {
		return this.licenseAuditLogRepository.findByLicenseIdOrderByEventTimeDesc(licenseId).stream()
				.map(LicenseAuditLogDTO::new)
				.collect(Collectors.toList());
	}
}