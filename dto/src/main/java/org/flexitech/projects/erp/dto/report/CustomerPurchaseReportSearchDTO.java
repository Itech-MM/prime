package org.flexitech.projects.erp.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerPurchaseReportSearchDTO {
	private Long customerId;
	private Long productId;
	private Long planId;
	private String startDate;
	private String endDate;
}