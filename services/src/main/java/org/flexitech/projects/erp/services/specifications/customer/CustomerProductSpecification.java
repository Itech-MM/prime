package org.flexitech.projects.erp.services.specifications.customer;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.customer.CustomerProductSearchDTO;
import org.flexitech.projects.erp.persistence.entities.customer.CustomerProduct;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class CustomerProductSpecification {
	public static Specification<CustomerProduct> withSearchCriteria(CustomerProductSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validLong(searchDTO.getCustomerId())) {
				predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), searchDTO.getCustomerId()));
			}

			if (CommonValidators.validLong(searchDTO.getProductId())) {
				predicates.add(criteriaBuilder.equal(root.get("product").get("id"), searchDTO.getProductId()));
			}

			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(criteriaBuilder.equal(root.get("status"), searchDTO.getStatus()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}