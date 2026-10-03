package org.flexitech.projects.erp.persistence.repositories.product;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	Optional<Product> findByCode(String code);
}