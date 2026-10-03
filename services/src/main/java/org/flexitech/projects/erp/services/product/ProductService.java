package org.flexitech.projects.erp.services.product;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.product.ProductDTO;
import org.flexitech.projects.erp.dto.product.ProductSearchDTO;
import org.springframework.data.domain.Pageable;

public interface ProductService {

	ProductDTO manageProduct(ProductDTO productDTO) throws Exception;

	ProductDTO getProductById(Long id) throws Exception;

	SearchResultDTO<ProductDTO> searchProducts(ProductSearchDTO searchDTO, Pageable pageable) throws Exception;

	boolean deleteProduct(Long id) throws Exception;
}