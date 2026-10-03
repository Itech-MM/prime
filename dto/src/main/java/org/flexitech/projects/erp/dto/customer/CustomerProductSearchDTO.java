package org.flexitech.projects.erp.dto.customer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerProductSearchDTO {
	private Long customerId;
	private Long productId;
	private Integer status;
}