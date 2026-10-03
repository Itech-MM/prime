package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.ItemCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemCategoryDTO extends CommonDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private Long parentId;
    private String parentName;

    @NotNull
    private Integer status;
    private String statusDesc;

    public ItemCategoryDTO(ItemCategory category) {
        super(category);
        this.code = category.getCode();
        this.name = category.getName();
        if (CommonValidators.isValidObject(category.getParent())) {
            this.parentId = category.getParent().getId();
            this.parentName = category.getParent().getName();
        }
        this.status = category.getStatus();
        this.statusDesc = ActiveStatus.getDescByCode(status);
    }
}