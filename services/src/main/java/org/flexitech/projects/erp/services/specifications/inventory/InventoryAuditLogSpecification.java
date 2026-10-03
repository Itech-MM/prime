package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.InventoryAuditLogSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.InventoryAuditLog;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class InventoryAuditLogSpecification {
	public static Specification<InventoryAuditLog> withSearchCriteria(InventoryAuditLogSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validString(searchDTO.getDocType())) {
				predicates.add(criteriaBuilder.equal(root.get("docType"), searchDTO.getDocType()));
			}
			if (CommonValidators.validString(searchDTO.getDocNo())) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("docNo")),
						"%" + searchDTO.getDocNo().toLowerCase() + "%"
				));
			}
			if (CommonValidators.isValidObject(searchDTO.getAction())) {
				predicates.add(criteriaBuilder.equal(root.get("action"), searchDTO.getAction()));
			}
			if (CommonValidators.isValidObject(searchDTO.getFromDate())) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdTime"), searchDTO.getFromDate()));
			}
			if (CommonValidators.isValidObject(searchDTO.getToDate())) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdTime"), searchDTO.getToDate()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}