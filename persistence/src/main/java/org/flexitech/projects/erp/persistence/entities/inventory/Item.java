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
@Table(name = TableNames.INV_ITEM_TBL)
@Getter
@Setter
public class Item extends BasedEntity {

    private String code;
    private String sku;
    private String barcode;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ItemCategory category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "base_uom_id")
    private UnitOfMeasure baseUom;

    @Column(name = "item_type")
    private Integer itemType;

    @Column(name = "tracking_type")
    private Integer trackingType;

    @Column(name = "costing_method")
    private Integer costingMethod;

    @Column(name = "standard_cost", precision = 18, scale = 4)
    private BigDecimal standardCost;

    @Column(name = "sale_price", precision = 18, scale = 4)
    private BigDecimal salePrice;

    @Column(name = "tax_rate", precision = 9, scale = 4)
    private BigDecimal taxRate;

    @Column(name = "min_qty", precision = 18, scale = 4)
    private BigDecimal minQty;

    @Column(name = "max_qty", precision = 18, scale = 4)
    private BigDecimal maxQty;

    @Column(name = "reorder_level", precision = 18, scale = 4)
    private BigDecimal reorderLevel;

    @Column(name = "reorder_qty", precision = 18, scale = 4)
    private BigDecimal reorderQty;

    @Column(name = "has_expiry")
    private Boolean hasExpiry;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    private Integer status;
}