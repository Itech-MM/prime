package org.flexitech.projects.erp.persistence.entities.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_STOCK_ADJUSTMENT_LINE_TBL)
@Getter
@Setter
public class StockAdjustmentLine extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "stock_adjustment_id")
    private StockAdjustment stockAdjustment;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @Column(name = "system_qty", precision = 18, scale = 4)
    private BigDecimal systemQty;

    @Column(name = "actual_qty", precision = 18, scale = 4)
    private BigDecimal actualQty;

    @Column(name = "difference_qty", precision = 18, scale = 4)
    private BigDecimal differenceQty;

    @Column(name = "unit_cost", precision = 18, scale = 4)
    private BigDecimal unitCost;

    private String remarks;
}