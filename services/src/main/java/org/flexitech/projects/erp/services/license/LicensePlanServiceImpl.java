package org.flexitech.projects.erp.services.license;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.license.LicensePlanDTO;
import org.flexitech.projects.erp.dto.license.LicensePlanSearchDTO;
import org.flexitech.projects.erp.persistence.entities.license.LicensePlan;
import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.flexitech.projects.erp.persistence.entities.product.ProductFeature;
import org.flexitech.projects.erp.persistence.repositories.license.LicensePlanRepository;
import org.flexitech.projects.erp.persistence.repositories.product.ProductFeatureRepository;
import org.flexitech.projects.erp.persistence.repositories.product.ProductRepository;
import org.flexitech.projects.erp.services.specifications.license.LicensePlanSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LicensePlanServiceImpl implements LicensePlanService {

	private final LicensePlanRepository licensePlanRepository;
	private final ProductRepository productRepository;
	private final ProductFeatureRepository productFeatureRepository;

	public LicensePlanServiceImpl(LicensePlanRepository licensePlanRepository, ProductRepository productRepository,
			ProductFeatureRepository productFeatureRepository) {
		this.licensePlanRepository = licensePlanRepository;
		this.productRepository = productRepository;
		this.productFeatureRepository = productFeatureRepository;
	}

	@Override
	@Transactional
	public LicensePlanDTO managePlan(LicensePlanDTO planDTO) throws Exception {
		LicensePlan plan = null;
		if (CommonValidators.validLong(planDTO.getId())) {
			plan = this.licensePlanRepository.findById(planDTO.getId())
					.orElseThrow(() -> new Exception("License plan not found!"));
		} else {
			plan = new LicensePlan();
		}

		plan.setCode(planDTO.getCode());
		plan.setName(planDTO.getName());
		plan.setDurationDays(planDTO.getDurationDays());
		plan.setMaxActivations(planDTO.getMaxActivations());
		plan.setMaxClients(planDTO.getMaxClients());
		plan.setMaxSites(planDTO.getMaxSites());
		plan.setMaxGates(planDTO.getMaxGates());
		plan.setStatus(planDTO.getStatus());
		plan.setPrice(planDTO.getPrice());

		Product product = this.productRepository.findById(planDTO.getProductId())
				.orElseThrow(() -> new Exception("Product not found!"));
		plan.setProduct(product);

		if (CommonValidators.isValidObject(planDTO.getFeatureIds()) && !planDTO.getFeatureIds().isEmpty()) {
			List<ProductFeature> features = this.productFeatureRepository.findAllById(planDTO.getFeatureIds());
			plan.setFeatures(features);
		}

		LicensePlan saved = this.licensePlanRepository.save(plan);
		return new LicensePlanDTO(saved);
	}

	@Override
	public LicensePlanDTO getPlanById(Long id) throws Exception {
		LicensePlan plan = this.licensePlanRepository.findById(id)
				.orElseThrow(() -> new Exception("License plan not found!"));
		return new LicensePlanDTO(plan);
	}

	@Override
	public SearchResultDTO<LicensePlanDTO> searchPlans(LicensePlanSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<LicensePlan> spec = LicensePlanSpecification.withSearchCriteria(searchDTO);
			Page<LicensePlan> planPage = licensePlanRepository.findAll(spec, pageable);
			return convertToCommonSearchDTO(planPage);
		} catch (Exception e) {
			throw new Exception("Error searching license plans: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean deletePlan(Long id) throws Exception {
		LicensePlan plan = this.licensePlanRepository.findById(id)
				.orElseThrow(() -> new Exception("License plan not found!"));
		this.licensePlanRepository.delete(plan);
		return true;
	}

	private SearchResultDTO<LicensePlanDTO> convertToCommonSearchDTO(Page<LicensePlan> planPage) {
		SearchResultDTO<LicensePlanDTO> result = new SearchResultDTO<>();

		result.setPageNo(planPage.getNumber());
		result.setLimit(planPage.getSize());
		result.setTotalPage(planPage.getTotalPages());
		result.setTotalRecords((int) planPage.getTotalElements());
		result.setPageCount(planPage.getNumberOfElements());
		result.setHasNextPage(planPage.hasNext());

		List<LicensePlanDTO> planDTOs = planPage.getContent().stream().map(LicensePlanDTO::new).collect(Collectors.toList());

		result.setResults(planDTOs);
		return result;
	}
}