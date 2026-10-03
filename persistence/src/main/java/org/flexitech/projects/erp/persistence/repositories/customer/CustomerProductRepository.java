package org.flexitech.projects.erp.persistence.repositories.customer;

import java.util.List;

import org.flexitech.projects.erp.persistence.entities.customer.CustomerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerProductRepository extends JpaRepository<CustomerProduct, Long>, JpaSpecificationExecutor<CustomerProduct> {
	List<CustomerProduct> findByCustomerId(Long customerId);
}