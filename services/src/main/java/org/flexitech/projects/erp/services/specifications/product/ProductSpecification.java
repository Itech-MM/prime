package org.flexitech.projects.erp.services.specifications.product;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.product.ProductSearchDTO;
import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
	public static Specification<Product> withSearchCriteria(ProductSearchDTO searchDTO) {
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

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}