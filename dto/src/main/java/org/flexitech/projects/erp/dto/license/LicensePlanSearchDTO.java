package org.flexitech.projects.erp.dto.license;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicensePlanSearchDTO {
	private String name;
	private String code;
	private Long productId;
}