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
public class LowStockItemModel {

	private String code;
	private String name;
	private BigDecimal qtyOnHand;
	private BigDecimal reorderLevel;
}