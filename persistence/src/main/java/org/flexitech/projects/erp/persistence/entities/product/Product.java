package org.flexitech.projects.erp.persistence.entities.product;

import java.util.Date;
import java.util.List;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
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

	@Lob
	@Column(name = "private_key_enc")
	private String privateKeyEnc;

	@Lob
	@Column(name = "public_key")
	private String publicKey;

	@Column(name = "key_generated_at")
	private Date keyGeneratedAt;

	@Column(name = "key_status")
	private Integer keyStatus;

	@OneToMany(mappedBy = "product")
	private List<ProductFeature> features;
}