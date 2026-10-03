package org.flexitech.projects.erp.services.product;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.product.ProductFeatureDTO;
import org.flexitech.projects.erp.dto.product.ProductFeatureSearchDTO;
import org.springframework.data.domain.Pageable;

public interface ProductFeatureService {

	ProductFeatureDTO manageFeature(ProductFeatureDTO featureDTO) throws Exception;

	ProductFeatureDTO getFeatureById(Long id) throws Exception;

	SearchResultDTO<ProductFeatureDTO> searchFeatures(ProductFeatureSearchDTO searchDTO, Pageable pageable) throws Exception;

	boolean deleteFeature(Long id) throws Exception;
}