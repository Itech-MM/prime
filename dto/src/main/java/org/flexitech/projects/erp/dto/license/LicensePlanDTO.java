package org.flexitech.projects.erp.dto.license;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.utils.CommonUtils;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.dto.product.ProductFeatureDTO;
import org.flexitech.projects.erp.persistence.entities.license.LicensePlan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicensePlanDTO extends CommonDTO {

	@NotNull
	private Long productId;
	private String productCode;
	private String productName;

	@NotBlank
	private String code;

	@NotBlank
	private String name;

	@NotNull
	private Integer durationDays;

	private Integer maxActivations;
	private Integer maxClients;
	private Integer maxSites;
	private Integer maxGates;

	@NotNull
	private BigDecimal price;
	private String priceDesc;

	@NotNull
	private Integer status;
	private String statusDesc;

	private List<Long> featureIds;
	private List<ProductFeatureDTO> features;

	public LicensePlanDTO(LicensePlan plan) {
		super(plan);
		this.code = plan.getCode();
		this.name = plan.getName();
		this.durationDays = plan.getDurationDays();
		this.maxActivations = plan.getMaxActivations();
		this.maxClients = plan.getMaxClients();
		this.maxSites = plan.getMaxSites();
		this.maxGates = plan.getMaxGates();
		this.price = plan.getPrice();
		this.priceDesc = CommonUtils.formatNumber(plan.getPrice());
		this.status = plan.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);

		if (CommonValidators.isValidObject(plan.getProduct())) {
			this.productId = plan.getProduct().getId();
			this.productCode = plan.getProduct().getCode();
			this.productName = plan.getProduct().getName();
		}

		if (CommonValidators.isValidObject(plan.getFeatures())) {
			this.features = plan.getFeatures().stream().map(ProductFeatureDTO::new).collect(Collectors.toList());
			this.featureIds = plan.getFeatures().stream().map(f -> f.getId()).collect(Collectors.toList());
		}
	}
}