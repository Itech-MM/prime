package org.flexitech.projects.erp.dto.inventory.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemSearchDTO {
    private String code;
    private String sku;
    private String barcode;
    private String name;
    private Long categoryId;
    private Long brandId;
    private Integer itemType;
    private Integer status;
}