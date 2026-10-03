package org.flexitech.projects.erp.persistence.entities.product;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.PRODUCT_FEATURE_TBL)
@Getter
@Setter
public class ProductFeature extends BasedEntity {

	@Column(name = "code", unique = true)
	private String code;

	private String name;
}