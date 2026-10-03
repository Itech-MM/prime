package org.flexitech.projects.erp.persistence.entities.inventory;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_ITEM_CATEGORY_TBL)
@Getter
@Setter
public class ItemCategory extends BasedEntity {

    private String code;
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ItemCategory parent;

    private Integer status;
}