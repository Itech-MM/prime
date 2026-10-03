package org.flexitech.projects.erp.persistence.entities.inventory;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_LOCATION_TBL,
        uniqueConstraints = @UniqueConstraint(columnNames = {"warehouse_id", "code"}))
@Getter
@Setter
public class WarehouseLocation extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private WarehouseLocation parent;

    private String code;
    private String name;
    private Integer type;
    private Integer status;
}