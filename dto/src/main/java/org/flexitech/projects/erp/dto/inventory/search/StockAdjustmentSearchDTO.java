package org.flexitech.projects.erp.dto.inventory.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentSearchDTO {
    private String docNo;
    private Long warehouseId;
    private Integer reason;
    private Integer status;
}