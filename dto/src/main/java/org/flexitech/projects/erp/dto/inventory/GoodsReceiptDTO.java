package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsReceipt;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoodsReceiptDTO extends CommonDTO {

    private String docNo;

    @NotNull
    private Long supplierId;
    private String supplierName;

    @NotNull
    private Long warehouseId;
    private String warehouseName;

    private String receivedDate;
    private String deliveryNoteNo;
    private String remarks;
    private BigDecimal totalAmount;

    private Integer status;
    private String statusDesc;

    private String approvedByName;
    private String approvedTime;
    private String postedByName;
    private String postedTime;

    @Valid
    private List<GoodsReceiptLineDTO> lines;

    public GoodsReceiptDTO(GoodsReceipt receipt) {
        super(receipt);
        this.docNo = receipt.getDocNo();
        if (CommonValidators.isValidObject(receipt.getSupplier())) {
            this.supplierId = receipt.getSupplier().getId();
            this.supplierName = receipt.getSupplier().getName();
        }
        if (CommonValidators.isValidObject(receipt.getWarehouse())) {
            this.warehouseId = receipt.getWarehouse().getId();
            this.warehouseName = receipt.getWarehouse().getName();
        }
        if (CommonValidators.isValidObject(receipt.getReceivedDate())) {
            this.receivedDate = DateUtils.dateToString(receipt.getReceivedDate(), CommonConstants.STANDARD_DB_DATE_FORMAT);
        }
        this.deliveryNoteNo = receipt.getDeliveryNoteNo();
        this.remarks = receipt.getRemarks();
        this.totalAmount = receipt.getTotalAmount();
        this.status = receipt.getStatus();
        this.statusDesc = InventoryDocStatus.getDescByCode(receipt.getStatus());
        if (CommonValidators.isValidObject(receipt.getApprovedBy())) {
            this.approvedByName = receipt.getApprovedBy().getName();
        }
        if (CommonValidators.isValidObject(receipt.getApprovedTime())) {
            this.approvedTime = DateUtils.dateToString(receipt.getApprovedTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(receipt.getPostedBy())) {
            this.postedByName = receipt.getPostedBy().getName();
        }
        if (CommonValidators.isValidObject(receipt.getPostedTime())) {
            this.postedTime = DateUtils.dateToString(receipt.getPostedTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(receipt.getLines())) {
            this.lines = receipt.getLines().stream().map(GoodsReceiptLineDTO::new).collect(Collectors.toList());
        }
    }
}