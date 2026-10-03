package org.flexitech.projects.erp.dto.report;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerPurchaseReportSummaryDTO {
	private BigDecimal totalAmount;
	private String totalAmountDesc;
	private int totalCount;
	private List<ReportGroupDTO> byProduct;
	private List<ReportGroupDTO> byCustomer;
}