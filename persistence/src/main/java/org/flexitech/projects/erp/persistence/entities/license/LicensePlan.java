package org.flexitech.projects.erp.persistence.entities.license;

import java.math.BigDecimal;
import java.util.List;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;
import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.flexitech.projects.erp.persistence.entities.product.ProductFeature;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.LICENSE_PLAN_TBL)
@Getter
@Setter
public class LicensePlan extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "code", unique = true)
	private String code;

	private String name;

	@Column(name = "duration_days")
	private Integer durationDays;

	@Column(name = "max_activations")
	private Integer maxActivations;

	@Column(name = "max_clients")
	private Integer maxClients;

	@Column(name = "max_sites")
	private Integer maxSites;

	@Column(name = "max_gates")
	private Integer maxGates;

	@Column(name = "price")
	private BigDecimal price;

	private Integer status;

	@ManyToMany
	@JoinTable(
		name = TableNames.PLAN_FEATURE_TBL,
		joinColumns = @JoinColumn(name = "plan_id"),
		inverseJoinColumns = @JoinColumn(name = "feature_id")
	)
	private List<ProductFeature> features;
}