package org.flexitech.projects.erp.services.specifications.inventory;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.inventory.search.ItemSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Item;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class ItemSpecification {
    public static Specification<Item> withSearchCriteria(ItemSearchDTO searchDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (CommonValidators.validString(searchDTO.getCode())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")),
                        "%" + searchDTO.getCode().toLowerCase() + "%"
                ));
            }

            if (CommonValidators.validString(searchDTO.getSku())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("sku")),
                        "%" + searchDTO.getSku().toLowerCase() + "%"
                ));
            }

            if (CommonValidators.validString(searchDTO.getBarcode())) {
                predicates.add(criteriaBuilder.equal(root.get("barcode"), searchDTO.getBarcode()));
            }

            if (CommonValidators.validString(searchDTO.getName())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + searchDTO.getName().toLowerCase() + "%"
                ));
            }

            if (CommonValidators.validLong(searchDTO.getCategoryId())) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), searchDTO.getCategoryId()));
            }

            if (CommonValidators.validLong(searchDTO.getBrandId())) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), searchDTO.getBrandId()));
            }

            if (CommonValidators.isValidObject(searchDTO.getItemType())) {
                predicates.add(criteriaBuilder.equal(root.get("itemType"), searchDTO.getItemType()));
            }

            if (CommonValidators.isValidObject(searchDTO.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), searchDTO.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}