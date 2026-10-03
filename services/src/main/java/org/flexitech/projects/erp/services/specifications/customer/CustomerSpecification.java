package org.flexitech.projects.erp.services.specifications.customer;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.customer.CustomerSearchDTO;
import org.flexitech.projects.erp.persistence.entities.customer.Customer;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class CustomerSpecification {
	public static Specification<Customer> withSearchCriteria(CustomerSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validString(searchDTO.getName())) {
				predicates.add(criteriaBuilder.like(
					criteriaBuilder.lower(root.get("name")),
					"%" + searchDTO.getName().toLowerCase() + "%"
				));
			}

			if (CommonValidators.validString(searchDTO.getCode())) {
				predicates.add(criteriaBuilder.like(
					criteriaBuilder.lower(root.get("code")),
					"%" + searchDTO.getCode().toLowerCase() + "%"
				));
			}

			if (CommonValidators.validString(searchDTO.getContactEmail())) {
				predicates.add(criteriaBuilder.like(
					criteriaBuilder.lower(root.get("contactEmail")),
					"%" + searchDTO.getContactEmail().toLowerCase() + "%"
				));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}