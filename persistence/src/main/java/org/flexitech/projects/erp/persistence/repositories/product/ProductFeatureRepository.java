package org.flexitech.projects.erp.persistence.repositories.product;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.product.ProductFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductFeatureRepository extends JpaRepository<ProductFeature, Long>, JpaSpecificationExecutor<ProductFeature> {
	Optional<ProductFeature> findByCode(String code);
}