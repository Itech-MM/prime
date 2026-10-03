package org.flexitech.projects.erp.dto.product;

import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.product.ProductFeature;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductFeatureDTO extends CommonDTO {

	@NotBlank
	private String code;

	@NotBlank
	private String name;

	public ProductFeatureDTO(ProductFeature feature) {
		super(feature);
		this.code = feature.getCode();
		this.name = feature.getName();
	}
}