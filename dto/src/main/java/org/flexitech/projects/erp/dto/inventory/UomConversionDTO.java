package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.UomConversion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UomConversionDTO extends CommonDTO {

    @NotNull
    private Long fromUomId;
    private String fromUomCode;

    @NotNull
    private Long toUomId;
    private String toUomCode;

    @NotNull
    private BigDecimal factor;

    public UomConversionDTO(UomConversion conversion) {
        super(conversion);
        if (CommonValidators.isValidObject(conversion.getFromUom())) {
            this.fromUomId = conversion.getFromUom().getId();
            this.fromUomCode = conversion.getFromUom().getCode();
        }
        if (CommonValidators.isValidObject(conversion.getToUom())) {
            this.toUomId = conversion.getToUom().getId();
            this.toUomCode = conversion.getToUom().getCode();
        }
        this.factor = conversion.getFactor();
    }
}