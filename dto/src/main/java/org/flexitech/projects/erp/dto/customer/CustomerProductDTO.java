package org.flexitech.projects.erp.dto.customer;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.persistence.entities.customer.CustomerProduct;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerProductDTO extends CommonDTO {

	@NotNull
	private Long customerId;
	private String customerCode;
	private String customerName;

	@NotNull
	private Long productId;
	private String productCode;
	private String productName;

	private String purchasedAt;

	@NotNull
	private Integer status;
	private String statusDesc;

	private LicenseDTO license;

	public CustomerProductDTO(CustomerProduct customerProduct) {
		super(customerProduct);
		this.status = customerProduct.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);

		if (CommonValidators.isValidObject(customerProduct.getPurchasedAt())) {
			this.purchasedAt = DateUtils.dateToString(customerProduct.getPurchasedAt(),
					CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}

		if (CommonValidators.isValidObject(customerProduct.getCustomer())) {
			this.customerId = customerProduct.getCustomer().getId();
			this.customerCode = customerProduct.getCustomer().getCode();
			this.customerName = customerProduct.getCustomer().getName();
		}

		if (CommonValidators.isValidObject(customerProduct.getProduct())) {
			this.productId = customerProduct.getProduct().getId();
			this.productCode = customerProduct.getProduct().getCode();
			this.productName = customerProduct.getProduct().getName();
		}
	}

	public CustomerProductDTO(CustomerProduct customerProduct, LicenseDTO license) {
		this(customerProduct);
		this.license = license;
	}
}