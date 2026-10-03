package org.flexitech.projects.erp.dto.inventory.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockBalanceSearchDTO {
    private Long itemId;
    private Long categoryId;
    private Long warehouseId;
    private Long locationId;
    private Boolean lowStockOnly;
}