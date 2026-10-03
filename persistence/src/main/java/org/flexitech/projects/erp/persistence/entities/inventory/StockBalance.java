package org.flexitech.projects.erp.persistence.entities.inventory;

import java.math.BigDecimal;
import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_STOCK_BALANCE_TBL)
@Getter
@Setter
public class StockBalance extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private WarehouseLocation location;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @Column(name = "qty_on_hand", precision = 18, scale = 4)
    private BigDecimal qtyOnHand = BigDecimal.ZERO;

    @Column(name = "qty_reserved", precision = 18, scale = 4)
    private BigDecimal qtyReserved = BigDecimal.ZERO;

    @Column(name = "avg_cost", precision = 18, scale = 4)
    private BigDecimal avgCost = BigDecimal.ZERO;

    @Column(name = "last_movement_at")
    private Date lastMovementAt;

    @Version
    private Long version;
}