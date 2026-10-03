package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBatch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockBatchDTO extends CommonDTO {

    @NotNull
    private Long itemId;
    private String itemName;

    @NotBlank
    private String batchNo;

    private String mfgDate;
    private String expiryDate;

    private Long supplierId;
    private String supplierName;

    private String receivedAt;

    public StockBatchDTO(StockBatch batch) {
        super(batch);
        if (CommonValidators.isValidObject(batch.getItem())) {
            this.itemId = batch.getItem().getId();
            this.itemName = batch.getItem().getName();
        }
        this.batchNo = batch.getBatchNo();
        if (CommonValidators.isValidObject(batch.getMfgDate())) {
            this.mfgDate = DateUtils.dateToString(batch.getMfgDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(batch.getExpiryDate())) {
            this.expiryDate = DateUtils.dateToString(batch.getExpiryDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(batch.getSupplier())) {
            this.supplierId = batch.getSupplier().getId();
            this.supplierName = batch.getSupplier().getName();
        }
        if (CommonValidators.isValidObject(batch.getReceivedAt())) {
            this.receivedAt = DateUtils.dateToString(batch.getReceivedAt(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
    }
}