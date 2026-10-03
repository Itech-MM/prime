package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.SupplierSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Supplier;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class SupplierSpecification {
    public static Specification<Supplier> withSearchCriteria(SupplierSearchDTO searchDTO) {
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

            if (CommonValidators.validString(searchDTO.getPhone())) {
                predicates.add(criteriaBuilder.equal(root.get("phone"), searchDTO.getPhone()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}