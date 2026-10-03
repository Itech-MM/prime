package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsIssueLine;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoodsIssueLineDTO {

    private Long id;

    @NotNull
    private Long itemId;
    private String itemCode;
    private String itemName;

    @NotNull
    private Long uomId;
    private String uomCode;

    @NotNull
    private Long locationId;
    private String locationName;

    private Long batchId;
    private String batchNo;

    @NotNull
    private BigDecimal qty;
    private BigDecimal baseQty;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;
    private String serialNos;

    public GoodsIssueLineDTO(GoodsIssueLine line) {
        this.id = line.getId();
        if (CommonValidators.isValidObject(line.getItem())) {
            this.itemId = line.getItem().getId();
            this.itemCode = line.getItem().getCode();
            this.itemName = line.getItem().getName();
        }
        if (CommonValidators.isValidObject(line.getUom())) {
            this.uomId = line.getUom().getId();
            this.uomCode = line.getUom().getCode();
        }
        if (CommonValidators.isValidObject(line.getLocation())) {
            this.locationId = line.getLocation().getId();
            this.locationName = line.getLocation().getName();
        }
        if (CommonValidators.isValidObject(line.getBatch())) {
            this.batchId = line.getBatch().getId();
            this.batchNo = line.getBatch().getBatchNo();
        }
        this.qty = line.getQty();
        this.baseQty = line.getBaseQty();
        this.unitCost = line.getUnitCost();
        this.lineTotal = line.getLineTotal();
        this.serialNos = line.getSerialNos();
    }
}