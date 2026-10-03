package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.StockSerialStatus;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockSerial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockSerialDTO extends CommonDTO {

    @NotNull
    private Long itemId;
    private String itemName;

    @NotBlank
    private String serialNo;

    private Long batchId;
    private String batchNo;

    private Long currentLocationId;
    private String currentLocationName;

    private Integer status;
    private String statusDesc;

    private String warrantyStart;
    private String warrantyEnd;

    public StockSerialDTO(StockSerial serial) {
        super(serial);
        if (CommonValidators.isValidObject(serial.getItem())) {
            this.itemId = serial.getItem().getId();
            this.itemName = serial.getItem().getName();
        }
        this.serialNo = serial.getSerialNo();
        if (CommonValidators.isValidObject(serial.getBatch())) {
            this.batchId = serial.getBatch().getId();
            this.batchNo = serial.getBatch().getBatchNo();
        }
        if (CommonValidators.isValidObject(serial.getCurrentLocation())) {
            this.currentLocationId = serial.getCurrentLocation().getId();
            this.currentLocationName = serial.getCurrentLocation().getName();
        }
        this.status = serial.getStatus();
        this.statusDesc = StockSerialStatus.getDescByCode(serial.getStatus());
        if (CommonValidators.isValidObject(serial.getWarrantyStart())) {
            this.warrantyStart = DateUtils.dateToString(serial.getWarrantyStart(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(serial.getWarrantyEnd())) {
            this.warrantyEnd = DateUtils.dateToString(serial.getWarrantyEnd(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
    }
}