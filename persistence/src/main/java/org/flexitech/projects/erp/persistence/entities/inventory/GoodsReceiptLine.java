package org.flexitech.projects.erp.persistence.entities.inventory;

import java.math.BigDecimal;
import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_GOODS_RECEIPT_LINE_TBL)
@Getter
@Setter
public class GoodsReceiptLine extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "goods_receipt_id")
    private GoodsReceipt goodsReceipt;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "uom_id")
    private UnitOfMeasure uom;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @Column(precision = 18, scale = 4)
    private BigDecimal qty;

    @Column(name = "base_qty", precision = 18, scale = 4)
    private BigDecimal baseQty;

    @Column(name = "unit_cost", precision = 18, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "line_total", precision = 18, scale = 4)
    private BigDecimal lineTotal;

    @Column(name = "batch_no")
    private String batchNo;

    @Temporal(TemporalType.DATE)
    @Column(name = "mfg_date")
    private Date mfgDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expiry_date")
    private Date expiryDate;

    @Lob
    @Column(name = "serial_nos")
    private String serialNos;
}