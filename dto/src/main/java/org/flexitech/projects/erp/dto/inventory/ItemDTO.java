package org.flexitech.projects.erp.dto.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.enums.CostingMethod;
import org.flexitech.projects.erp.commons.enums.ItemType;
import org.flexitech.projects.erp.commons.enums.TrackingType;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemDTO extends CommonDTO {

    @NotBlank
    private String code;

    private String sku;
    private String barcode;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long categoryId;
    private String categoryName;

    private Long brandId;
    private String brandName;

    @NotNull
    private Long baseUomId;
    private String baseUomCode;

    @NotNull
    private Integer itemType;
    private String itemTypeDesc;

    @NotNull
    private Integer trackingType;
    private String trackingTypeDesc;

    private Integer costingMethod;
    private String costingMethodDesc;

    private BigDecimal standardCost;
    private BigDecimal salePrice;
    private BigDecimal taxRate;
    private BigDecimal minQty;
    private BigDecimal maxQty;
    private BigDecimal reorderLevel;
    private BigDecimal reorderQty;

    private Boolean hasExpiry;
    private Integer shelfLifeDays;

    @NotNull
    private Integer status;
    private String statusDesc;

    public ItemDTO(Item item) {
        super(item);
        this.code = item.getCode();
        this.sku = item.getSku();
        this.barcode = item.getBarcode();
        this.name = item.getName();
        this.description = item.getDescription();

        if (CommonValidators.isValidObject(item.getCategory())) {
            this.categoryId = item.getCategory().getId();
            this.categoryName = item.getCategory().getName();
        }
        if (CommonValidators.isValidObject(item.getBrand())) {
            this.brandId = item.getBrand().getId();
            this.brandName = item.getBrand().getName();
        }
        if (CommonValidators.isValidObject(item.getBaseUom())) {
            this.baseUomId = item.getBaseUom().getId();
            this.baseUomCode = item.getBaseUom().getCode();
        }

        this.itemType = item.getItemType();
        this.itemTypeDesc = ItemType.getDescByCode(item.getItemType());
        this.trackingType = item.getTrackingType();
        this.trackingTypeDesc = TrackingType.getDescByCode(item.getTrackingType());
        this.costingMethod = item.getCostingMethod();
        this.costingMethodDesc = CostingMethod.getDescByCode(item.getCostingMethod());

        this.standardCost = item.getStandardCost();
        this.salePrice = item.getSalePrice();
        this.taxRate = item.getTaxRate();
        this.minQty = item.getMinQty();
        this.maxQty = item.getMaxQty();
        this.reorderLevel = item.getReorderLevel();
        this.reorderQty = item.getReorderQty();

        this.hasExpiry = item.getHasExpiry();
        this.shelfLifeDays = item.getShelfLifeDays();

        this.status = item.getStatus();
        this.statusDesc = ActiveStatus.getDescByCode(status);
    }
}