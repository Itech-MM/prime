package org.flexitech.projects.erp.persistence.entities.inventory;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_BRAND_TBL)
@Getter
@Setter
public class Brand extends BasedEntity {

    private String code;
    private String name;
    private Integer status;
}