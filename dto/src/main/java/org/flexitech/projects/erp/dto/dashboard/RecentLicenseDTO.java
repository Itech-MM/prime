package org.flexitech.projects.erp.dto.dashboard;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.LicenseStatus;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.persistence.entities.license.License;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecentLicenseDTO {
	private String code;
	private String customerName;
	private String productName;
	private String issuedAt;
	private Integer status;
	private String statusDesc;

	public RecentLicenseDTO(License license) {
		this.code = license.getCode();
		this.status = license.getStatus();
		this.statusDesc = LicenseStatus.getDescByCode(status);

		if (CommonValidators.isValidObject(license.getIssuedAt())) {
			this.issuedAt = DateUtils.dateToString(license.getIssuedAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
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