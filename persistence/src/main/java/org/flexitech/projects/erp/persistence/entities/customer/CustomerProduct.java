package org.flexitech.projects.erp.persistence.entities.customer;

import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;
import org.flexitech.projects.erp.persistence.entities.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.CUSTOMER_PRODUCT_TBL)
@Getter
@Setter
public class CustomerProduct extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "purchased_at")
	private Date purchasedAt;

	private Integer status;
}