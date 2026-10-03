package org.flexitech.projects.erp.dto.license;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicenseSearchDTO {
	private String code;
	private Long customerId;
	private Long productId;
	private Long planId;
	private Integer status;
}