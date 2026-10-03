package org.flexitech.projects.erp.persistence.models.inventory;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementModel {

	private String postedAt;
	private String itemCode;
	private String itemName;
	private String direction;
	private BigDecimal quantity;
	private String refDocType;
}