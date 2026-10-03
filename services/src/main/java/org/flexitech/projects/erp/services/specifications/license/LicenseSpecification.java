package org.flexitech.projects.erp.services.specifications.license;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.license.LicenseSearchDTO;
import org.flexitech.projects.erp.persistence.entities.license.License;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class LicenseSpecification {
	public static Specification<License> withSearchCriteria(LicenseSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validString(searchDTO.getCode())) {
				predicates.add(criteriaBuilder.like(
					criteriaBuilder.lower(root.get("code")),
					"%" + searchDTO.getCode().toLowerCase() + "%"
				));
			}

			if (CommonValidators.validLong(searchDTO.getCustomerId())) {
				predicates.add(criteriaBuilder.equal(root.get("customerProduct").get("customer").get("id"), searchDTO.getCustomerId()));
			}

			if (CommonValidators.validLong(searchDTO.getPlanId())) {
				predicates.add(criteriaBuilder.equal(root.get("plan").get("id"), searchDTO.getPlanId()));
			}

			if (CommonValidators.validLong(searchDTO.getProductId())) {
				predicates.add(criteriaBuilder.equal(root.get("plan").get("product").get("id"), searchDTO.getProductId()));
			}

			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(criteriaBuilder.equal(root.get("status"), searchDTO.getStatus()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}