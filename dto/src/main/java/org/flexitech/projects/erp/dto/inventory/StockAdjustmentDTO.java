package org.flexitech.projects.erp.dto.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.AdjustmentReason;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockAdjustment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentDTO extends CommonDTO {

    private String docNo;

    @NotNull
    private Long warehouseId;
    private String warehouseName;

    @NotNull
    private Integer reason;
    private String reasonDesc;

    private String adjustmentDate;
    private String remarks;

    private Integer status;
    private String statusDesc;

    private String approvedByName;
    private String approvedTime;
    private String postedByName;
    private String postedTime;

    @Valid
    private List<StockAdjustmentLineDTO> lines;

    public StockAdjustmentDTO(StockAdjustment adjustment) {
        super(adjustment);
        this.docNo = adjustment.getDocNo();
        if (CommonValidators.isValidObject(adjustment.getWarehouse())) {
            this.warehouseId = adjustment.getWarehouse().getId();
            this.warehouseName = adjustment.getWarehouse().getName();
        }
        this.reason = adjustment.getReason();
        this.reasonDesc = AdjustmentReason.getDescByCode(adjustment.getReason());
        if (CommonValidators.isValidObject(adjustment.getAdjustmentDate())) {
            this.adjustmentDate = DateUtils.dateToString(adjustment.getAdjustmentDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        this.remarks = adjustment.getRemarks();
        this.status = adjustment.getStatus();
        this.statusDesc = InventoryDocStatus.getDescByCode(adjustment.getStatus());
        if (CommonValidators.isValidObject(adjustment.getApprovedBy())) {
            this.approvedByName = adjustment.getApprovedBy().getName();
        }
        if (CommonValidators.isValidObject(adjustment.getApprovedTime())) {
            this.approvedTime = DateUtils.dateToString(adjustment.getApprovedTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(adjustment.getPostedBy())) {
            this.postedByName = adjustment.getPostedBy().getName();
        }
        if (CommonValidators.isValidObject(adjustment.getPostedTime())) {
            this.postedTime = DateUtils.dateToString(adjustment.getPostedTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(adjustment.getLines())) {
            this.lines = adjustment.getLines().stream().map(StockAdjustmentLineDTO::new).collect(Collectors.toList());
        }
    }
}