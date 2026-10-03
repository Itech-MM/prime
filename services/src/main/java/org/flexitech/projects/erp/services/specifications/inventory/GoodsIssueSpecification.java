package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.GoodsIssueSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsIssue;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class GoodsIssueSpecification {
	public static Specification<GoodsIssue> withSearchCriteria(GoodsIssueSearchDTO searchDTO) {
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

			if (CommonValidators.isValidObject(searchDTO.getIssueType())) {
				predicates.add(criteriaBuilder.equal(root.get("issueType"), searchDTO.getIssueType()));
			}

			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(criteriaBuilder.equal(root.get("status"), searchDTO.getStatus()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}