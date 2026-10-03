package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.StockMovementType;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockLedger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockLedgerDTO extends CommonDTO {

    private Long itemId;
    private String itemCode;
    private String itemName;

    private Long locationId;
    private String locationName;

    private Long batchId;
    private String batchNo;

    private Long serialId;
    private String serialNo;

    private Integer movementType;
    private String movementTypeDesc;

    private BigDecimal qtyIn;
    private BigDecimal qtyOut;
    private BigDecimal balanceAfter;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    private String refDocType;
    private Long refDocId;
    private Long refLineId;

    private String postedAt;
    private String postedByName;

    public StockLedgerDTO(StockLedger ledger) {
        super(ledger);
        if (CommonValidators.isValidObject(ledger.getItem())) {
            this.itemId = ledger.getItem().getId();
            this.itemCode = ledger.getItem().getCode();
            this.itemName = ledger.getItem().getName();
        }
        if (CommonValidators.isValidObject(ledger.getLocation())) {
            this.locationId = ledger.getLocation().getId();
            this.locationName = ledger.getLocation().getName();
        }
        if (CommonValidators.isValidObject(ledger.getBatch())) {
            this.batchId = ledger.getBatch().getId();
            this.batchNo = ledger.getBatch().getBatchNo();
        }
        if (CommonValidators.isValidObject(ledger.getSerial())) {
            this.serialId = ledger.getSerial().getId();
            this.serialNo = ledger.getSerial().getSerialNo();
        }
        this.movementType = ledger.getMovementType();
        this.movementTypeDesc = StockMovementType.getDescByCode(ledger.getMovementType());
        this.qtyIn = ledger.getQtyIn();
        this.qtyOut = ledger.getQtyOut();
        this.balanceAfter = ledger.getBalanceAfter();
        this.unitCost = ledger.getUnitCost();
        this.totalCost = ledger.getTotalCost();
        this.refDocType = ledger.getRefDocType();
        this.refDocId = ledger.getRefDocId();
        this.refLineId = ledger.getRefLineId();
        if (CommonValidators.isValidObject(ledger.getPostedAt())) {
            this.postedAt = DateUtils.dateToString(ledger.getPostedAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(ledger.getPostedBy())) {
            this.postedByName = ledger.getPostedBy().getName();
        }
    }
}