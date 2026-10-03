package org.flexitech.projects.erp.dto.dashboard;

import java.math.BigDecimal;

import org.flexitech.projects.erp.persistence.models.inventory.LowStockItemModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LowStockItemDTO {

	private String code;
	private String name;
	private BigDecimal qtyOnHand;
	private BigDecimal reorderLevel;
	
	public LowStockItemDTO(LowStockItemModel m) {
		this.code = m.getCode();
		this.name = m.getName();
		this.qtyOnHand = m.getQtyOnHand();
		this.reorderLevel = m.getReorderLevel();
	}
	
}