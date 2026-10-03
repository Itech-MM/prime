package org.flexitech.projects.erp.dto.dashboard;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.persistence.entities.license.License;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExpiringLicenseDTO {
	private String code;
	private String customerName;
	private String productName;
	private String expiresAt;
	private long daysRemaining;

	public ExpiringLicenseDTO(License license, long daysRemaining) {
		this.code = license.getCode();
		this.daysRemaining = daysRemaining;

		if (CommonValidators.isValidObject(license.getExpiresAt())) {
			this.expiresAt = DateUtils.dateToString(license.getExpiresAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}

		if (CommonValidators.isValidObject(license.getCustomerProduct())
				&& CommonValidators.isValidObject(license.getCustomerProduct().getCustomer())) {
			this.customerName = license.getCustomerProduct().getCustomer().getName();
		}

		if (CommonValidators.isValidObject(license.getPlan())
				&& CommonValidators.isValidObject(license.getPlan().getProduct())) {
			this.productName = license.getPlan().getProduct().getName();
		}
	}
}