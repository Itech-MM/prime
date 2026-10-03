package org.flexitech.projects.erp.dto.license;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.LicenseStatus;
import org.flexitech.projects.erp.commons.utils.CommonUtils;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.license.License;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicenseDTO extends CommonDTO {

	private Long customerProductId;
	private Long customerId;
	private String customerCode;
	private String customerName;

	private Long productId;
	private String productCode;
	private String productName;

	private Long planId;
	private String planCode;
	private String planName;

	private String code;
	private String fingerprint;

	private Integer status;
	private String statusDesc;

	private String issuedAt;
	private String expiresAt;
	private Integer graceDays;

	private String tokenLocation;
	private boolean tokenAvailable;

	private BigDecimal pricePaid;
	private String priceDesc;

	public LicenseDTO(License license) {
		super(license);
		this.code = license.getCode();
		this.fingerprint = license.getFingerprint();
		this.status = license.getStatus();
		this.statusDesc = LicenseStatus.getDescByCode(status);
		this.graceDays = license.getGraceDays();
		this.tokenLocation = license.getTokenLocation();
		this.tokenAvailable = CommonValidators.validString(license.getTokenLocation());
		this.pricePaid = license.getPricePaid();
		this.priceDesc = CommonUtils.formatNumber(license.getPricePaid());
		
		if (CommonValidators.isValidObject(license.getIssuedAt())) {
			this.issuedAt = DateUtils.dateToString(license.getIssuedAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}
		if (CommonValidators.isValidObject(license.getExpiresAt())) {
			this.expiresAt = DateUtils.dateToString(license.getExpiresAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}

		if (CommonValidators.isValidObject(license.getPlan())) {
			this.planId = license.getPlan().getId();
			this.planCode = license.getPlan().getCode();
			this.planName = license.getPlan().getName();
			if (CommonValidators.isValidObject(license.getPlan().getProduct())) {
				this.productId = license.getPlan().getProduct().getId();
				this.productCode = license.getPlan().getProduct().getCode();
				this.productName = license.getPlan().getProduct().getName();
			}
		}

		if (CommonValidators.isValidObject(license.getCustomerProduct())) {
			this.customerProductId = license.getCustomerProduct().getId();
			if (CommonValidators.isValidObject(license.getCustomerProduct().getCustomer())) {
				this.customerId = license.getCustomerProduct().getCustomer().getId();
				this.customerCode = license.getCustomerProduct().getCustomer().getCode();
				this.customerName = license.getCustomerProduct().getCustomer().getName();
			}
		}
	}
}