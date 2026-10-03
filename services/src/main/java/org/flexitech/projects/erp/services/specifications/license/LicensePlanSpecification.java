package org.flexitech.projects.erp.services.specifications.license;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.license.LicensePlanSearchDTO;
import org.flexitech.projects.erp.persistence.entities.license.LicensePlan;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class LicensePlanSpecification {
	public static Specification<LicensePlan> withSearchCriteria(LicensePlanSearchDTO searchDTO) {
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

			if (CommonValidators.validLong(searchDTO.getProductId())) {
				predicates.add(criteriaBuilder.equal(root.get("product").get("id"), searchDTO.getProductId()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}