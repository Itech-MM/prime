package org.flexitech.projects.erp.dto.customer;

import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.customer.Customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO extends CommonDTO {

	@NotBlank
	private String code;

	@NotBlank
	private String name;

	private String contactEmail;

	@NotNull
	private Integer status;
	private String statusDesc;

	public CustomerDTO(Customer customer) {
		super(customer);
		this.code = customer.getCode();
		this.name = customer.getName();
		this.contactEmail = customer.getContactEmail();
		this.status = customer.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
	}
}