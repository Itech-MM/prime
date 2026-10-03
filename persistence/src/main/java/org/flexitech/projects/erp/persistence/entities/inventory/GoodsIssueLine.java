package org.flexitech.projects.erp.persistence.entities.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_GOODS_ISSUE_LINE_TBL)
@Getter
@Setter
public class GoodsIssueLine extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "goods_issue_id")
    private GoodsIssue goodsIssue;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "uom_id")
    private UnitOfMeasure uom;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @Column(precision = 18, scale = 4)
    private BigDecimal qty;

    @Column(name = "base_qty", precision = 18, scale = 4)
    private BigDecimal baseQty;

    @Column(name = "unit_cost", precision = 18, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "line_total", precision = 18, scale = 4)
    private BigDecimal lineTotal;

    @Lob
    @Column(name = "serial_nos")
    private String serialNos;
}