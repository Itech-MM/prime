package org.flexitech.projects.erp.dto.inventory.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoodsReceiptSearchDTO {
    private String docNo;
    private Long supplierId;
    private Long warehouseId;
    private Integer status;
    private String fromDate;
    private String toDate;
}