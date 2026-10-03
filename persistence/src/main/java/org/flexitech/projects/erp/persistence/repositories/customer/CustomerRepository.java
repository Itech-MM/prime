package org.flexitech.projects.erp.persistence.repositories.customer;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
	Optional<Customer> findByCode(String code);
}