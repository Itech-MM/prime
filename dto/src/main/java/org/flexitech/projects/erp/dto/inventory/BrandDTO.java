package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BrandDTO extends CommonDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private Integer status;
    private String statusDesc;

    public BrandDTO(Brand brand) {
        super(brand);
        this.code = brand.getCode();
        this.name = brand.getName();
        this.status = brand.getStatus();
        this.statusDesc = ActiveStatus.getDescByCode(status);
    }
}