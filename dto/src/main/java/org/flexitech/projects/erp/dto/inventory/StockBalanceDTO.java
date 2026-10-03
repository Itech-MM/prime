package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBalance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockBalanceDTO extends CommonDTO {

    private Long itemId;
    private String itemCode;
    private String itemName;

    private Long locationId;
    private String locationName;
    private String warehouseName;

    private Long batchId;
    private String batchNo;

    private BigDecimal qtyOnHand;
    private BigDecimal qtyReserved;
    private BigDecimal qtyAvailable;
    private BigDecimal avgCost;

    private String lastMovementAt;

    public StockBalanceDTO(StockBalance balance) {
        super(balance);
        if (CommonValidators.isValidObject(balance.getItem())) {
            this.itemId = balance.getItem().getId();
            this.itemCode = balance.getItem().getCode();
            this.itemName = balance.getItem().getName();
        }
        if (CommonValidators.isValidObject(balance.getLocation())) {
            this.locationId = balance.getLocation().getId();
            this.locationName = balance.getLocation().getName();
            if (CommonValidators.isValidObject(balance.getLocation().getWarehouse())) {
                this.warehouseName = balance.getLocation().getWarehouse().getName();
            }
        }
        if (CommonValidators.isValidObject(balance.getBatch())) {
            this.batchId = balance.getBatch().getId();
            this.batchNo = balance.getBatch().getBatchNo();
        }
        this.qtyOnHand = balance.getQtyOnHand();
        this.qtyReserved = balance.getQtyReserved();
        if (CommonValidators.isValidObject(balance.getQtyOnHand()) && CommonValidators.isValidObject(balance.getQtyReserved())) {
            this.qtyAvailable = balance.getQtyOnHand().subtract(balance.getQtyReserved());
        }
        this.avgCost = balance.getAvgCost();
        if (CommonValidators.isValidObject(balance.getLastMovementAt())) {
            this.lastMovementAt = DateUtils.dateToString(balance.getLastMovementAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
    }
}