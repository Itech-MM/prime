package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseDTO extends CommonDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String address;
    private String managerName;
    private Boolean allowNegativeStock;

    @NotNull
    private Integer status;
    private String statusDesc;

    public WarehouseDTO(Warehouse warehouse) {
        super(warehouse);
        this.code = warehouse.getCode();
        this.name = warehouse.getName();
        this.address = warehouse.getAddress();
        this.managerName = warehouse.getManagerName();
        this.allowNegativeStock = warehouse.getAllowNegativeStock();
        this.status = warehouse.getStatus();
        this.statusDesc = ActiveStatus.getDescByCode(status);
    }
}