package org.flexitech.projects.erp.persistence.entities.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;
import org.flexitech.projects.erp.persistence.entities.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_STOCK_ADJUSTMENT_TBL)
@Getter
@Setter
public class StockAdjustment extends BasedEntity {

    @Column(name = "doc_no", unique = true)
    private String docNo;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    private Integer reason;

    @Temporal(TemporalType.DATE)
    @Column(name = "adjustment_date")
    private Date adjustmentDate;

    private String remarks;

    private Integer status;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_time")
    private Date approvedTime;

    @ManyToOne
    @JoinColumn(name = "posted_by")
    private User postedBy;

    @Column(name = "posted_time")
    private Date postedTime;

    @OneToMany(mappedBy = "stockAdjustment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockAdjustmentLine> lines = new ArrayList<>();
}