package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.enums.IssueType;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsIssue;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoodsIssueDTO extends CommonDTO {

    private String docNo;

    @NotNull
    private Long warehouseId;
    private String warehouseName;

    @NotNull
    private Integer issueType;
    private String issueTypeDesc;

    private Long customerId;
    private String customerName;
    private String issuedToName;

    private String issueDate;
    private String remarks;
    private BigDecimal totalAmount;

    private Integer status;
    private String statusDesc;

    private String approvedByName;
    private String approvedTime;
    private String postedByName;
    private String postedTime;

    @Valid
    private List<GoodsIssueLineDTO> lines;

    public GoodsIssueDTO(GoodsIssue issue) {
        super(issue);
        this.docNo = issue.getDocNo();
        if (CommonValidators.isValidObject(issue.getWarehouse())) {
            this.warehouseId = issue.getWarehouse().getId();
            this.warehouseName = issue.getWarehouse().getName();
        }
        this.issueType = issue.getIssueType();
        this.issueTypeDesc = IssueType.getDescByCode(issue.getIssueType());
        if (CommonValidators.isValidObject(issue.getCustomer())) {
            this.customerId = issue.getCustomer().getId();
            this.customerName = issue.getCustomer().getName();
        }
        this.issuedToName = issue.getIssuedToName();
        if (CommonValidators.isValidObject(issue.getIssueDate())) {
            this.issueDate = DateUtils.dateToString(issue.getIssueDate(), CommonConstants.STANDARD_DB_DATE_FORMAT);
        }
        this.remarks = issue.getRemarks();
        this.totalAmount = issue.getTotalAmount();
        this.status = issue.getStatus();
        this.statusDesc = InventoryDocStatus.getDescByCode(issue.getStatus());
        if (CommonValidators.isValidObject(issue.getApprovedBy())) {
            this.approvedByName = issue.getApprovedBy().getName();
        }
        if (CommonValidators.isValidObject(issue.getApprovedTime())) {
            this.approvedTime = DateUtils.dateToString(issue.getApprovedTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(issue.getPostedBy())) {
            this.postedByName = issue.getPostedBy().getName();
        }
        if (CommonValidators.isValidObject(issue.getPostedTime())) {
            this.postedTime = DateUtils.dateToString(issue.getPostedTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
        }
        if (CommonValidators.isValidObject(issue.getLines())) {
            this.lines = issue.getLines().stream().map(GoodsIssueLineDTO::new).collect(Collectors.toList());
        }
    }
}