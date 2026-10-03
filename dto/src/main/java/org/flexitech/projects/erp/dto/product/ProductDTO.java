package org.flexitech.projects.erp.dto.product;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.enums.ProductKeyStatus;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.flexitech.projects.erp.persistence.entities.product.ProductFeatureProduct;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO extends CommonDTO {

	@NotBlank
	private String code;

	@NotBlank
	private String name;

	@NotNull
	private Integer status;
	private String statusDesc;

	private String keyAlgorithm;
	private String privateKeyLocation;
	private String publicKeyLocation;
	private String keyGeneratedAt;
	private Integer keyStatus;
	private String keyStatusDesc;

	private List<Long> featureIds;
	private List<ProductFeatureDTO> features;

	public ProductDTO(Product product) {
		super(product);
		this.code = product.getCode();
		this.name = product.getName();
		this.status = product.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);

		this.keyAlgorithm = product.getKeyAlgorithm();
		this.privateKeyLocation = product.getPrivateKeyLocation();
		this.publicKeyLocation = product.getPublicKeyLocation();
		this.keyStatus = product.getKeyStatus();
		this.keyStatusDesc = ProductKeyStatus.getDescByCode(keyStatus);
		if (CommonValidators.isValidObject(product.getKeyGeneratedAt())) {
			this.keyGeneratedAt = DateUtils.dateToString(product.getKeyGeneratedAt(),
					CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}
	}

	public ProductDTO(Product product, List<ProductFeatureProduct> links) {
		this(product);
		if (CommonValidators.isValidObject(links)) {
			this.features = links.stream()
					.map(link -> new ProductFeatureDTO(link.getFeature()))
					.collect(Collectors.toList());
			this.featureIds = links.stream()
					.map(link -> link.getFeature().getId())
					.collect(Collectors.toList());
		}
	}
}