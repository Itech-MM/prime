package org.flexitech.projects.erp.dto.license;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.license.LicenseAuditLog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicenseAuditLogDTO extends CommonDTO {

	private Long licenseId;
	private String eventType;
	private String eventTime;
	private String details;

	public LicenseAuditLogDTO(LicenseAuditLog log) {
		super(log);
		this.eventType = log.getEventType();
		this.details = log.getDetails();

		if (CommonValidators.isValidObject(log.getLicense())) {
			this.licenseId = log.getLicense().getId();
		}
		if (CommonValidators.isValidObject(log.getEventTime())) {
			this.eventTime = DateUtils.dateToString(log.getEventTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}
	}
}