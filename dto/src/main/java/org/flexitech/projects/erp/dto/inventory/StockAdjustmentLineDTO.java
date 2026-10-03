package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.persistence.entities.inventory.StockAdjustmentLine;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentLineDTO {

	private Long id;

	@NotNull
	private Long itemId;
	private String itemCode;
	private String itemName;

	@NotNull
	private Long locationId;
	private String locationName;

	private Long batchId;
	private String batchNo;

	private BigDecimal systemQty;

	@NotNull
	private BigDecimal actualQty;

	@NotNull
	private BigDecimal unitCost;

	private BigDecimal differenceQty;
	private String remarks;

	public StockAdjustmentLineDTO(StockAdjustmentLine line) {
		this.id = line.getId();
		if (CommonValidators.isValidObject(line.getItem())) {
			this.itemId = line.getItem().getId();
			this.itemCode = line.getItem().getCode();
			this.itemName = line.getItem().getName();
		}
		if (CommonValidators.isValidObject(line.getLocation())) {
			this.locationId = line.getLocation().getId();
			this.locationName = line.getLocation().getName();
		}
		if (CommonValidators.isValidObject(line.getBatch())) {
			this.batchId = line.getBatch().getId();
			this.batchNo = line.getBatch().getBatchNo();
		}
		this.systemQty = line.getSystemQty();
		this.actualQty = line.getActualQty();
		this.differenceQty = line.getDifferenceQty();
		this.unitCost = line.getUnitCost();
		this.remarks = line.getRemarks();
	}
}