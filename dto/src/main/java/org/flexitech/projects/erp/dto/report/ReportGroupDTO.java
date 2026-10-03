package org.flexitech.projects.erp.dto.report;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.utils.CommonUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportGroupDTO {
	private String label;
	private BigDecimal totalAmount;
	private String totalAmountDesc;
	private int count;

	public ReportGroupDTO(String label, BigDecimal totalAmount, int count) {
		this.label = label;
		this.totalAmount = totalAmount;
		this.totalAmountDesc = CommonUtils.formatNumber(totalAmount);
		this.count = count;
	}
}