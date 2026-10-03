package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.ItemCategorySearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.ItemCategory;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class ItemCategorySpecification {
    public static Specification<ItemCategory> withSearchCriteria(ItemCategorySearchDTO searchDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (CommonValidators.validString(searchDTO.getCode())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")),
                        "%" + searchDTO.getCode().toLowerCase() + "%"
                ));
            }

            if (CommonValidators.validString(searchDTO.getName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + searchDTO.getName().toLowerCase() + "%"
                ));
            }

            if (CommonValidators.validLong(searchDTO.getParentId())) {
                predicates.add(criteriaBuilder.equal(root.get("parent").get("id"), searchDTO.getParentId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}