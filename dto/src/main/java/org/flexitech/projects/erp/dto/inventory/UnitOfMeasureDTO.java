package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.UnitOfMeasure;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnitOfMeasureDTO extends CommonDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private Integer status;
    private String statusDesc;

    public UnitOfMeasureDTO(UnitOfMeasure uom) {
        super(uom);
        this.code = uom.getCode();
        this.name = uom.getName();
        this.status = uom.getStatus();
        this.statusDesc = ActiveStatus.getDescByCode(status);
    }
}