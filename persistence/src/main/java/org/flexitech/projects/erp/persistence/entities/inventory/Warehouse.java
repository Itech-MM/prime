package org.flexitech.projects.erp.persistence.entities.inventory;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_WAREHOUSE_TBL)
@Getter
@Setter
public class Warehouse extends BasedEntity {

    private String code;
    private String name;
    private String address;

    @Column(name = "manager_name")
    private String managerName;

    @Column(name = "allow_negative_stock")
    private Boolean allowNegativeStock;

    private Integer status;
}