package org.flexitech.projects.erp.dto.inventory.search;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryAuditLogSearchDTO {
	private String docType;
	private String docNo;
	private Integer action;
	private Date fromDate;
	private Date toDate;
}