package org.flexitech.projects.erp.dto.inventory.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemCategorySearchDTO {
    private String code;
    private String name;
    private Long parentId;
}