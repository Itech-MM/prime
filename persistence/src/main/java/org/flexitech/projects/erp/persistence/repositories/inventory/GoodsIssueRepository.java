package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.GoodsIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GoodsIssueRepository extends JpaRepository<GoodsIssue, Long>, JpaSpecificationExecutor<GoodsIssue> {
    Optional<GoodsIssue> findByDocNo(String docNo);
    boolean existsByDocNo(String docNo);
    boolean existsByWarehouseId(Long warehouseId);
}