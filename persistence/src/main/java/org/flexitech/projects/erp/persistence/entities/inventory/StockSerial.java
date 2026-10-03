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
@Table(name = TableNames.INV_SERIAL_TBL,
        uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "serial_no"}))
@Getter
@Setter
public class StockSerial extends BasedEntity {

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "serial_no")
    private String serialNo;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @ManyToOne
    @JoinColumn(name = "current_location_id")
    private WarehouseLocation currentLocation;

    private Integer status;

    @Temporal(TemporalType.DATE)
    @Column(name = "warranty_start")
    private Date warrantyStart;

    @Temporal(TemporalType.DATE)
    @Column(name = "warranty_end")
    private Date warrantyEnd;
}