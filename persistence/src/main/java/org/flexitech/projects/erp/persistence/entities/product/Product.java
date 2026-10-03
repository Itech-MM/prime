package org.flexitech.projects.erp.persistence.entities.product;

import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.PRODUCT_TBL)
@Getter
@Setter
public class Product extends BasedEntity {

	@Column(name = "code", unique = true)
	private String code;

	private String name;

	private Integer status;

	@Column(name = "key_algorithm")
	private String keyAlgorithm;

	@Column(name = "private_key_location")
	private String privateKeyLocation;

	@Column(name = "public_key_location")
	private String publicKeyLocation;

	@Column(name = "key_generated_at")
	private Date keyGeneratedAt;

	@Column(name = "key_status")
	private Integer keyStatus;

}