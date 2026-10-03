package org.flexitech.projects.erp.persistence.entities.inventory;

import java.math.BigDecimal;
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
@Table(name = TableNames.INV_GOODS_RECEIPT_TBL)
@Getter
@Setter
public class GoodsReceipt extends BasedEntity {

    @Column(name = "doc_no", unique = true)
    private String docNo;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Temporal(TemporalType.DATE)
    @Column(name = "received_date")
    private Date receivedDate;

    @Column(name = "delivery_note_no")
    private String deliveryNoteNo;

    private String remarks;

    @Column(name = "total_amount", precision = 18, scale = 4)
    private BigDecimal totalAmount;

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

    @OneToMany(mappedBy = "goodsReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsReceiptLine> lines = new ArrayList<>();
}