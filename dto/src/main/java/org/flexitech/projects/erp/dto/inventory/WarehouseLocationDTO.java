package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.enums.LocationType;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.WarehouseLocation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseLocationDTO extends CommonDTO {

    @NotNull
    private Long warehouseId;
    private String warehouseName;

    private Long parentId;
    private String parentName;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private Integer type;
    private String typeDesc;

    @NotNull
    private Integer status;
    private String statusDesc;

    public WarehouseLocationDTO(WarehouseLocation location) {
        super(location);
        if (CommonValidators.isValidObject(location.getWarehouse())) {
            this.warehouseId = location.getWarehouse().getId();
            this.warehouseName = location.getWarehouse().getName();
        }
        if (CommonValidators.isValidObject(location.getParent())) {
            this.parentId = location.getParent().getId();
            this.parentName = location.getParent().getName();
        }
        this.code = location.getCode();
        this.name = location.getName();
        this.type = location.getType();
        this.typeDesc = LocationType.getDescByCode(location.getType());
        this.status = location.getStatus();
        this.statusDesc = ActiveStatus.getDescByCode(status);
    }
}