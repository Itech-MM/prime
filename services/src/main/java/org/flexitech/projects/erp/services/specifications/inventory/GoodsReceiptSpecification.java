package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.GoodsReceiptSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsReceipt;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class GoodsReceiptSpecification {
	public static Specification<GoodsReceipt> withSearchCriteria(GoodsReceiptSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validString(searchDTO.getDocNo())) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("docNo")),
						"%" + searchDTO.getDocNo().toLowerCase() + "%"
				));
			}

			if (CommonValidators.validLong(searchDTO.getSupplierId())) {
				predicates.add(criteriaBuilder.equal(root.get("supplier").get("id"), searchDTO.getSupplierId()));
			}

			if (CommonValidators.validLong(searchDTO.getWarehouseId())) {
				predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), searchDTO.getWarehouseId()));
			}

			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(criteriaBuilder.equal(root.get("status"), searchDTO.getStatus()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}