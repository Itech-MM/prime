package org.flexitech.projects.erp.dto.dashboard;

import java.math.BigDecimal;

import org.flexitech.projects.erp.persistence.models.inventory.StockMovementModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {

	private String postedAt;
	private String itemCode;
	private String itemName;
	private String direction;
	private BigDecimal quantity;
	private String refDocType;
	
	public StockMovementDTO(StockMovementModel m) {
		this.postedAt = m.getPostedAt();
		this.itemCode = m.getItemCode();
		this.itemName = m.getItemName();
		this.direction = m.getDirection();
		this.quantity = m.getQuantity();
		this.refDocType = m.getRefDocType();
	}
}