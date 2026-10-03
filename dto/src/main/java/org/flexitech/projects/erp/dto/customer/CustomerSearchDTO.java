package org.flexitech.projects.erp.dto.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerSearchDTO {
	private String name;
	private String code;
	private String contactEmail;
}