package org.flexitech.projects.erp.services.product;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.product.ProductFeatureDTO;
import org.flexitech.projects.erp.dto.product.ProductFeatureSearchDTO;
import org.flexitech.projects.erp.persistence.entities.product.ProductFeature;
import org.flexitech.projects.erp.persistence.repositories.product.ProductFeatureRepository;
import org.flexitech.projects.erp.services.specifications.product.ProductFeatureSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductFeatureServiceImpl implements ProductFeatureService {

	private final ProductFeatureRepository productFeatureRepository;

	public ProductFeatureServiceImpl(ProductFeatureRepository productFeatureRepository) {
		this.productFeatureRepository = productFeatureRepository;
	}

	@Override
	public ProductFeatureDTO manageFeature(ProductFeatureDTO featureDTO) throws Exception {
		ProductFeature feature = null;
		if (CommonValidators.validLong(featureDTO.getId())) {
			feature = this.productFeatureRepository.findById(featureDTO.getId())
					.orElseThrow(() -> new Exception("Feature not found!"));
		} else {
			feature = new ProductFeature();
		}

		feature.setCode(featureDTO.getCode());
		feature.setName(featureDTO.getName());

		ProductFeature saved = this.productFeatureRepository.save(feature);
		return new ProductFeatureDTO(saved);
	}

	@Override
	public ProductFeatureDTO getFeatureById(Long id) throws Exception {
		ProductFeature feature = this.productFeatureRepository.findById(id)
				.orElseThrow(() -> new Exception("Feature not found!"));
		return new ProductFeatureDTO(feature);
	}

	@Override
	public SearchResultDTO<ProductFeatureDTO> searchFeatures(ProductFeatureSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<ProductFeature> spec = ProductFeatureSpecification.withSearchCriteria(searchDTO);
			Page<ProductFeature> featurePage = productFeatureRepository.findAll(spec, pageable);
			return convertToCommonSearchDTO(featurePage);
		} catch (Exception e) {
			throw new Exception("Error searching features: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean deleteFeature(Long id) throws Exception {
		ProductFeature feature = this.productFeatureRepository.findById(id)
				.orElseThrow(() -> new Exception("Feature not found!"));
		this.productFeatureRepository.delete(feature);
		return true;
	}

	private SearchResultDTO<ProductFeatureDTO> convertToCommonSearchDTO(Page<ProductFeature> featurePage) {
		SearchResultDTO<ProductFeatureDTO> result = new SearchResultDTO<>();

		result.setPageNo(featurePage.getNumber());
		result.setLimit(featurePage.getSize());
		result.setTotalPage(featurePage.getTotalPages());
		result.setTotalRecords((int) featurePage.getTotalElements());
		result.setPageCount(featurePage.getNumberOfElements());
		result.setHasNextPage(featurePage.hasNext());

		List<ProductFeatureDTO> featureDTOs = featurePage.getContent().stream().map(ProductFeatureDTO::new).collect(Collectors.toList());

		result.setResults(featureDTOs);
		return result;
	}
}