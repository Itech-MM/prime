package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.StockLedgerSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockLedger;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class StockLedgerSpecification {
	public static Specification<StockLedger> withSearchCriteria(StockLedgerSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validLong(searchDTO.getItemId())) {
				predicates.add(criteriaBuilder.equal(root.get("item").get("id"), searchDTO.getItemId()));
			}

			if (CommonValidators.validLong(searchDTO.getLocationId())) {
				predicates.add(criteriaBuilder.equal(root.get("location").get("id"), searchDTO.getLocationId()));
			}

			if (CommonValidators.isValidObject(searchDTO.getMovementType())) {
				predicates.add(criteriaBuilder.equal(root.get("movementType"), searchDTO.getMovementType()));
			}

			if (CommonValidators.isValidObject(searchDTO.getFromDate())) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("postedAt"), searchDTO.getFromDate()));
			}

			if (CommonValidators.isValidObject(searchDTO.getToDate())) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("postedAt"), searchDTO.getToDate()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}