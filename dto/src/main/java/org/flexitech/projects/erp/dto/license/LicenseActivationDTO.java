package org.flexitech.projects.erp.dto.license;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.license.LicenseActivation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicenseActivationDTO extends CommonDTO {

	@NotNull
	private Long licenseId;

	@NotBlank
	private String machineFingerprint;

	private String activatedAt;
	private String lastCheckinAt;

	private Integer status;
	private String statusDesc;

	public LicenseActivationDTO(LicenseActivation activation) {
		super(activation);
		this.machineFingerprint = activation.getMachineFingerprint();
		this.status = activation.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);

		if (CommonValidators.isValidObject(activation.getLicense())) {
			this.licenseId = activation.getLicense().getId();
		}
		if (CommonValidators.isValidObject(activation.getActivatedAt())) {
			this.activatedAt = DateUtils.dateToString(activation.getActivatedAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}
		if (CommonValidators.isValidObject(activation.getLastCheckinAt())) {
			this.lastCheckinAt = DateUtils.dateToString(activation.getLastCheckinAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}
	}
}