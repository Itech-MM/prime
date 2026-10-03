package org.flexitech.projects.erp.persistence.entities.inventory;

import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_BATCH_TBL,
        uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "batch_no"}))
@Getter
@Setter
public class StockBatch extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "batch_no")
    private String batchNo;

    @Temporal(TemporalType.DATE)
    @Column(name = "mfg_date")
    private Date mfgDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expiry_date")
    private Date expiryDate;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "received_at")
    private Date receivedAt;
}