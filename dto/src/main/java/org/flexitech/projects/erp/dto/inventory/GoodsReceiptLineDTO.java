package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsReceiptLine;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoodsReceiptLineDTO {

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

    @NotNull
    private BigDecimal qty;
    private BigDecimal baseQty;

    @NotNull
    private BigDecimal unitCost;
    private BigDecimal lineTotal;

    private String batchNo;
    private String mfgDate;
    private String expiryDate;
    private String serialNos;

    public GoodsReceiptLineDTO(GoodsReceiptLine line) {
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
        this.qty = line.getQty();
        this.baseQty = line.getBaseQty();
        this.unitCost = line.getUnitCost();
        this.lineTotal = line.getLineTotal();
        this.batchNo = line.getBatchNo();
        if (CommonValidators.isValidObject(line.getMfgDate())) {
            this.mfgDate = DateUtils.dateToString(line.getMfgDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(line.getExpiryDate())) {
            this.expiryDate = DateUtils.dateToString(line.getExpiryDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        this.serialNos = line.getSerialNos();
    }
}