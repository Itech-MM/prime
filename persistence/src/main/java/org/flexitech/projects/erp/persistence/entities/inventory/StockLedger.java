package org.flexitech.projects.erp.persistence.entities.inventory;

import java.math.BigDecimal;
import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;
import org.flexitech.projects.erp.persistence.entities.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_STOCK_LEDGER_TBL)
@Getter
@Setter
public class StockLedger extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @ManyToOne
    @JoinColumn(name = "serial_id")
    private StockSerial serial;

    @Column(name = "movement_type")
    private Integer movementType;

    @Column(name = "qty_in", precision = 18, scale = 4)
    private BigDecimal qtyIn = BigDecimal.ZERO;

    @Column(name = "qty_out", precision = 18, scale = 4)
    private BigDecimal qtyOut = BigDecimal.ZERO;

    @Column(name = "balance_after", precision = 18, scale = 4)
    private BigDecimal balanceAfter;

    @Column(name = "unit_cost", precision = 18, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 18, scale = 4)
    private BigDecimal totalCost;

    @Column(name = "ref_doc_type")
    private String refDocType;

    @Column(name = "ref_doc_id")
    private Long refDocId;

    @Column(name = "ref_line_id")
    private Long refLineId;

    @Column(name = "posted_at")
    private Date postedAt;

    @ManyToOne
    @JoinColumn(name = "posted_by")
    private User postedBy;
}