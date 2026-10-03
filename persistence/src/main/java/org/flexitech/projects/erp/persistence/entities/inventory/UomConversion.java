package org.flexitech.projects.erp.persistence.entities.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_UOM_CONVERSION_TBL,
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_uom_id", "to_uom_id"}))
@Getter
@Setter
public class UomConversion extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "from_uom_id")
    private UnitOfMeasure fromUom;

    @ManyToOne
    @JoinColumn(name = "to_uom_id")
    private UnitOfMeasure toUom;

    @Column(precision = 18, scale = 6)
    private BigDecimal factor;
}