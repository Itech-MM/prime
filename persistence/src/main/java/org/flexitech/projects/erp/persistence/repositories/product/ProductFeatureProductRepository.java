package org.flexitech.projects.erp.persistence.repositories.product;

import java.util.List;

import org.flexitech.projects.erp.persistence.entities.product.ProductFeatureProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFeatureProductRepository extends JpaRepository<ProductFeatureProduct, Long> {
	List<ProductFeatureProduct> findByProductId(Long productId);
	void deleteByProductId(Long productId);
}