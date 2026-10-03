package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.StockAdjustmentSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockAdjustment;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class StockAdjustmentSpecification {
	public static Specification<StockAdjustment> withSearchCriteria(StockAdjustmentSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validString(searchDTO.getDocNo())) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("docNo")),
						"%" + searchDTO.getDocNo().toLowerCase() + "%"
				));
			}

			if (CommonValidators.validLong(searchDTO.getWarehouseId())) {
				predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), searchDTO.getWarehouseId()));
			}

			if (CommonValidators.isValidObject(searchDTO.getReason())) {
				predicates.add(criteriaBuilder.equal(root.get("reason"), searchDTO.getReason()));
			}

			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(criteriaBuilder.equal(root.get("status"), searchDTO.getStatus()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}