package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.StockBalanceSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBalance;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class StockBalanceSpecification {
	public static Specification<StockBalance> withSearchCriteria(StockBalanceSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validLong(searchDTO.getItemId())) {
				predicates.add(criteriaBuilder.equal(root.get("item").get("id"), searchDTO.getItemId()));
			}

			if (CommonValidators.validLong(searchDTO.getCategoryId())) {
				predicates.add(criteriaBuilder.equal(root.get("item").get("category").get("id"), searchDTO.getCategoryId()));
			}

			if (CommonValidators.validLong(searchDTO.getWarehouseId())) {
				predicates.add(criteriaBuilder.equal(root.get("location").get("warehouse").get("id"), searchDTO.getWarehouseId()));
			}

			if (CommonValidators.validLong(searchDTO.getLocationId())) {
				predicates.add(criteriaBuilder.equal(root.get("location").get("id"), searchDTO.getLocationId()));
			}

			if (Boolean.TRUE.equals(searchDTO.getLowStockOnly())) {
				predicates.add(criteriaBuilder.isNotNull(root.get("item").get("reorderLevel")));
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("qtyOnHand"), root.get("item").get("reorderLevel")));
			}

			predicates.add(criteriaBuilder.greaterThan(root.get("qtyOnHand"), java.math.BigDecimal.ZERO));

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}