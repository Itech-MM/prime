package org.flexitech.projects.erp.dto.inventory.search;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockLedgerSearchDTO {
	private Long itemId;
	private Long locationId;
	private Integer movementType;
	private Date fromDate;
	private Date toDate;
}