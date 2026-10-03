package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.List;
import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long>, JpaSpecificationExecutor<ItemCategory> {
    Optional<ItemCategory> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByParentId(Long parentId);
    List<ItemCategory> findByParentId(Long parentId);
}