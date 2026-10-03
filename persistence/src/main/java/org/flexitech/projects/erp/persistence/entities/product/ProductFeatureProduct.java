package org.flexitech.projects.erp.persistence.entities.product;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.PRODUCT_FEATURE_PRODUCT_TBL)
@Getter
@Setter
public class ProductFeatureProduct extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "feature_id")
	private ProductFeature feature;
}