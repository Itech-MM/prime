package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SupplierDTO extends CommonDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String paymentTerms;

    @NotNull
    private Integer status;
    private String statusDesc;

    public SupplierDTO(Supplier supplier) {
        super(supplier);
        this.code = supplier.getCode();
        this.name = supplier.getName();
        this.contactPerson = supplier.getContactPerson();
        this.phone = supplier.getPhone();
        this.email = supplier.getEmail();
        this.address = supplier.getAddress();
        this.paymentTerms = supplier.getPaymentTerms();
        this.status = supplier.getStatus();
        this.statusDesc = ActiveStatus.getDescByCode(status);
    }
}